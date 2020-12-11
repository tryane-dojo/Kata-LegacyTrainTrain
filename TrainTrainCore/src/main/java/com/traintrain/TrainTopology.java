package com.traintrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrainTopology {

    private String      trainId;
    private List<Coach> coaches;

    public TrainTopology(String trainId, List<Seat> allSeats) {
        this.trainId = trainId;
        coaches = new ArrayList<>();
        allSeats.stream().collect(Collectors.groupingBy(Seat::getCoachName)).values().forEach(seats -> {
            coaches.add(new Coach(trainId, seats));
        });
    }

    public static TrainTopology fromJson(String trainId, String trainTopologyJson) throws IOException {

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

        return new TrainTopology(trainId, seats);
    }

    public int getReservedSeats() {
        return (int) getSeats().stream().filter(Predicate.not(Seat::isFree)).count();
    }

    public List<Seat> getSeats() {
        return coaches.stream().map(Coach::getSeats).collect(Collectors.flatMapping(List::stream, Collectors.toList()));
    }

    boolean canBookWithoutExceedCapacity(int nbSeatRequested) {
        return getReservedSeats() + nbSeatRequested <= Math.floor(ThresholdManager.getMaxRes() * getSeats().size());
    }

    public BookingAttempt buildBookingAttempt(int nbSeatRequested) {

        for (Coach coach : coaches) {
            BookingAttempt bookingAttempt = coach.buildBookingAttempt(nbSeatRequested);
            if (bookingAttempt.isFullfiled()) {
                return bookingAttempt;
            }
        }

        return new BookingAttempt(trainId, nbSeatRequested, List.of());
    }
}