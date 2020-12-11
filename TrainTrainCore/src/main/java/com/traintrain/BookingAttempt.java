package com.traintrain;

import java.util.List;

public class BookingAttempt {

    private String     trainId;
    private List<Seat> seats;
    private int        nbSeatRequested;

    public BookingAttempt(String trainId, int nbSeatRequested, List<Seat> seats) {
        this.trainId = trainId;
        this.nbSeatRequested = nbSeatRequested;
        this.seats = seats;
    }

    public String getTrainId() {
        return trainId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    boolean isFullfiled() {
        return getSeats().size() == nbSeatRequested;
    }

    void assignReference(String bookingRef) {
        for (Seat seat : seats) {
            seat.setBookingRef(bookingRef);
        }
    }

}
