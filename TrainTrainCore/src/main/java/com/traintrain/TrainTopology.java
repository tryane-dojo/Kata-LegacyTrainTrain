package com.traintrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainTopology {
    private int reservedSeats;
    private List<Seat> seats;

    public TrainTopology(List<Seat> seats) {
        this.seats = seats;
    }
    
    public TrainTopology(String trainTopol) throws IOException {

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
                    this.reservedSeats++;
                }
            }
        }
    }

    public int getReservedSeats() {
        return (int) seats.stream().filter(seat -> seat.getBookingRef().isEmpty() == false).count();
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public int getMaxSeat() {
        return this.seats.size();
    }

    public boolean hasLessThanThreshold(int i) {
        return reservedSeats < i;
    }
}