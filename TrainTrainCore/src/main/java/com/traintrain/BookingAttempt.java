package com.traintrain;

import java.util.List;

public class BookingAttempt {

    private List<Seat> seats;
    private int nbSeatRequested;

    public BookingAttempt(int nbSeatRequested, List<Seat> seats) {
        this.nbSeatRequested = nbSeatRequested;
        this.seats = seats;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    boolean isFullfiled() {
        return getSeats().size() == nbSeatRequested;
    }

}
