package com.traintrain;

import java.util.List;
import java.util.stream.Collectors;

public class Coach {

    private List<Seat> seats;

    public Coach(List<Seat> seats) {
        this.seats = seats;
    }

    public BookingAttempt buildBookingAttempt(int nbSeatRequested) {
        List<Seat> availableSeats = findAvailableSeats(nbSeatRequested);
        return new BookingAttempt(nbSeatRequested, availableSeats);
    }

    private List<Seat> findAvailableSeats(int nbSeatRequested) {
        return seats.stream().filter(Seat::isFree).limit(nbSeatRequested).collect(Collectors.toList());
    }

}
