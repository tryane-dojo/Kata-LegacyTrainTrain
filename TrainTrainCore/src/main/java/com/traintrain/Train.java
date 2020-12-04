package com.traintrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Train {
    public int ReservedSeats;
    public List<Seat> seats;

    public Train(String trainTopol) throws IOException {

        seats = new ArrayList<>();

        //var sample:
        //"{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        final ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Map<String, SeatJson>> stuff_in_stuff = objectMapper.readValue(trainTopol, new TypeReference<Map<String, Map<String, SeatJson>>>() {
        });

        for (Map<String, SeatJson> value : stuff_in_stuff.values()) {
            for (SeatJson seatJson : value.values()) {
                int seat_number = Integer.parseInt(seatJson.seat_number);
                seats.add(new Seat(seatJson.coach, seat_number, seatJson.booking_reference));
                if (!(new Seat(seatJson.coach, seat_number, seatJson.booking_reference).getBookingRef() == "")) {
                    this.ReservedSeats++;
                }
            }
        }
    }

    public Train() {
        seats = new ArrayList<>();
    }

    public int getMaxSeat() {
        return this.seats.size();
    }

    public boolean hasLessThanThreshold(int i) {
        return ReservedSeats < i;
    }
}