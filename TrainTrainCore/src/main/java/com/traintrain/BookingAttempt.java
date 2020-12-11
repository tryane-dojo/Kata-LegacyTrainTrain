package com.traintrain;

import java.util.List;

public class BookingAttempt {

    private List<Seat> availableSeats;

    public BookingAttempt(List<Seat> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<Seat> getSeats() {
        return availableSeats;
    }

}
