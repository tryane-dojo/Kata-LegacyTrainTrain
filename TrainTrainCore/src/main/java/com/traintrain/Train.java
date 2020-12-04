package com.traintrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Train {
    public List<Seat> seats;
    private List<Coach> coachs = new ArrayList<>();

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

            }
        }
    }

    public Train() {
        seats = new ArrayList<>();
    }

    public Train(List<Seat> seats) {
        this();
        this.seats = seats;
        seats.stream().collect(Collectors.groupingBy(Seat::getCoachName)).forEach((coachName, seatList) -> coachs.add(new Coach(seatList)));
    }

    public int getReservedSeatCount() {
        return (int) seats.stream().filter(Seat::isBooked).count();
    }

    public int getMaxSeat() {
        return this.seats.size();
    }

    List<Seat> findAvailableSeatsToBook(int nbSeatsToBook) {
        Optional<Coach> coach = coachs.stream().filter(c -> c.findAvailableSeatsToBook(nbSeatsToBook).size() == nbSeatsToBook).findFirst();
        if (coach.isPresent()) {
            return coach.get().findAvailableSeatsToBook(nbSeatsToBook);
        } else {
            return Collections.emptyList();
        }
    }

    boolean canWeBookSeats(int nbSeatsToBook, double trainThreadshold) {
        return (getReservedSeatCount() + nbSeatsToBook) <= Math.floor(trainThreadshold * getMaxSeat());
    }


}