package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TrainTopologyShould {

    private static final String TRAIN_ID = "express_2000";

    @Test
    public void should_deserialize_train_topology() throws IOException {
        String trainTopologyJson = "{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        TrainTopology trainTopology = TrainTopology.fromJson(TRAIN_ID, trainTopologyJson);

        AssertSeats(trainTopology.getSeats());
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
            TrainTopology train = TrainTopologyBuilder.aTrain(TRAIN_ID)
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
            TrainTopology train = TrainTopologyBuilder.aTrain(TRAIN_ID)
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
        void obtains_booking_with_all_seats_in_the_same_coach() {
            // given
            TrainTopology train = TrainTopologyBuilder.aTrain(TRAIN_ID)
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
