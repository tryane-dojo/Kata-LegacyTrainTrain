package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traintrain.BookingAttempt;
import com.traintrain.Seat;
import com.traintrain.SeatJson;
import com.traintrain.TrainTopology;

public class TrainTopologyShould {

    @Test
    public void should_deserialize_train_topology() {
        String trainTopology = "{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        List<Seat> Seats = new ArrayList<Seat>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Map<String, SeatJson>> stuff_in_stuff = objectMapper.readValue(trainTopology, new TypeReference<Map<String, Map<String, SeatJson>>>() {
            });

            for (Map<String, SeatJson> value : stuff_in_stuff.values()) {
                for (SeatJson seatJson : value.values()) {
                    int seat_number = Integer.parseInt(seatJson.seat_number);
                    Seats.add(new Seat(seatJson.coach, seat_number, seatJson.booking_reference));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AssertSeats(Seats);
    }

    private void AssertSeats(List<Seat> seats) {
        String seatA1 = seats.get(0).toString();
        String seatA2 = seats.get(1).toString();
        Assertions.assertEquals(2, seats.size());
        Assertions.assertEquals("A1", seatA1);
        Assertions.assertEquals("A2", seatA2);
    }

    @Nested
    public class BuildBookingAttemptShould {

        @Test
        void obtains_booking_with_first_seats_when_train_is_empty() {
            // given
            TrainTopology train = TrainTopologyBuilder.aTrain()
                    .withFreeSeat(1, "A")
                    .withFreeSeat(2, "A")
                    .withFreeSeat(3, "A")
                    .withFreeSeat(1, "B")
                    .withFreeSeat(2, "B")
                    .withFreeSeat(3, "B")
                    .build();

            // when
            BookingAttempt attempt = train.builBookingAttempt(2);

            //then        
            assertThat(attempt.getSeats()).hasSize(2).containsExactly(new Seat("A", 1), new Seat("A", 2));
        }

        @Test
        void obtains_booking_with_free_seats_when_seats_are_booked() {
            // given
            TrainTopology train = TrainTopologyBuilder.aTrain()
                    .withBookedSeat(1, "A", "::irrelevant_booking_reference::")
                    .withFreeSeat(2, "A")
                    .withFreeSeat(3, "A")
                    .withFreeSeat(1, "B")
                    .withFreeSeat(2, "B")
                    .withFreeSeat(3, "B")
                    .build();

            // when
            BookingAttempt attempt = train.builBookingAttempt(2);

            //then        
            assertThat(attempt.getSeats()).hasSize(2).containsExactly(new Seat("A", 2), new Seat("A", 3));
        }

        @Test
        @Disabled
        void obtains_booking_with_all_seats_in_the_same_coach() {
            // given
            TrainTopology train = TrainTopologyBuilder.aTrain()
                    .withBookedSeat(1, "A", "::irrelevant_booking_reference::")
                    .withBookedSeat(2, "A", "::irrelevant_booking_reference::")
                    .withFreeSeat(3, "A")
                    .withFreeSeat(1, "B")
                    .withFreeSeat(2, "B")
                    .withFreeSeat(3, "B")
                    .build();

            // when
            BookingAttempt attempt = train.builBookingAttempt(2);

            //then        
            assertThat(attempt.getSeats()).hasSize(2).containsExactly(new Seat("B", 1), new Seat("B", 2));
        }

    }

}
