package com.traintrain;

import java.util.List;

public class BookingAttempt {

    private String     trainId;
    private List<Seat> seats;
    private int        nbSeatRequested;
    private String     reference;

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
        return seats.size() == nbSeatRequested;
    }

    void assignReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    BookingConfirmation confirm() {
        return new BookingConfirmation(trainId, reference, seats);
    }

}
