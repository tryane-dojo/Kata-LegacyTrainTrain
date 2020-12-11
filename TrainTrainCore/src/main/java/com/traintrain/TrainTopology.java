package com.traintrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrainTopology {
    private List<Seat> seats;

    public TrainTopology(List<Seat> seats) {
        this.seats = seats;
    }

    public static TrainTopology fromJson(String trainTopologyJson) throws IOException {

        List<Seat> seats = new ArrayList<>();
        //var sample:
        //"{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        final ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Map<String, SeatJson>> stuff_in_stuff = objectMapper.readValue(trainTopologyJson, new TypeReference<Map<String, Map<String, SeatJson>>>() {
        });

        for (Map<String, SeatJson> value : stuff_in_stuff.values()) {
            for (SeatJson seatJson : value.values()) {
                int seat_number = Integer.parseInt(seatJson.seat_number);
                seats.add(new Seat(seatJson.coach, seat_number, seatJson.booking_reference));

            }
        }

        return new TrainTopology(seats);
    }

    public int getReservedSeats() {
        return (int) seats.stream().filter(seat -> seat.getBookingRef().isEmpty() == false).count();
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public int getMaxSeat() {
        return seats.size();
    }

    boolean doNotExceedTrainCapacity(int nbSeatRequested) {
        return getReservedSeats() + nbSeatRequested <= Math.floor(ThresholdManager.getMaxRes() * getMaxSeat());
    }

    private List<Seat> findAvailableSeats(int nbSeatRequested) {
        return seats.stream().filter(Seat::isFree).limit(nbSeatRequested).collect(Collectors.toList());
    }

    public BookingAttempt builBookingAttempt(int nbSeatRequested) {
        List<Seat> availableSeats = findAvailableSeats(nbSeatRequested);
        return new BookingAttempt(nbSeatRequested, availableSeats);
    }
}