package com.traintrain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingConfirmation {

    private String       train_id;

    private String       booking_reference;

    private List<String> seats = new ArrayList<>();

    public BookingConfirmation(String train, String bookingRef, List<Seat> availableSeats) {
        train_id = train;
        booking_reference = bookingRef;
        seats = availableSeats.stream().map(seat -> seat.getSeatNumber() + seat.getCoachName()).collect(Collectors.toList());
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public String getBooking_reference() {
        return booking_reference;
    }

    public void setBooking_reference(String booking_reference) {
        this.booking_reference = booking_reference;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

}
