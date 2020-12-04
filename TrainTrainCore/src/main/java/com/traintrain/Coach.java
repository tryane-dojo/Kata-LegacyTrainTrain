package com.traintrain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Coach {

    private final List<Seat> seats;

    public Coach(List<Seat> seats) {
        this.seats = seats;
    }

    List<Seat> findAvailableSeatsToBook(int nbSeatsToBook) {
        List<Seat> seatList = seats.stream().filter(Seat::isNotBooked).limit(nbSeatsToBook).collect(Collectors.toList());
        return seatList.size() == nbSeatsToBook ? seatList : Collections.emptyList();
    }
}
