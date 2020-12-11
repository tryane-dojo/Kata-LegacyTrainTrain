package com.traintrain;

import java.util.List;
import java.util.stream.Collectors;

public class Coach {

    private List<Seat> seats;
    private String     trainId;

    public Coach(String trainId, List<Seat> seats) {
        this.trainId = trainId;
        this.seats = seats;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public BookingAttempt buildBookingAttempt(int nbSeatRequested) {
        List<Seat> availableSeats = findAvailableSeats(nbSeatRequested);
        return new BookingAttempt(trainId, nbSeatRequested, availableSeats);
    }

    private List<Seat> findAvailableSeats(int nbSeatRequested) {
        return getSeats().stream().filter(Seat::isFree).limit(nbSeatRequested).collect(Collectors.toList());
    }

}
