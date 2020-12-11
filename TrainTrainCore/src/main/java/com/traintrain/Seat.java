package com.traintrain;

import java.util.Objects;

public class Seat {
    private String coachName;
    private int    seatNumber;
    private String bookingReference;

    public Seat(String coach, int seatNumber) {
        this(coach, seatNumber, "");
    }

    public Seat(String coachName, int seatNumber, String bookingReference) {
        this.coachName = coachName;
        this.seatNumber = seatNumber;
        this.bookingReference = bookingReference;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    boolean isFree() {
        return bookingReference.isEmpty();
    }

    @Override
    public String toString() {
        return coachName + seatNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (bookingReference == null ? 0 : bookingReference.hashCode());
        result = prime * result + (coachName == null ? 0 : coachName.hashCode());
        result = prime * result + seatNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Seat other = (Seat) obj;
        if (!Objects.equals(bookingReference, other.bookingReference)) {
            return false;
        }
        if (!Objects.equals(coachName, other.coachName)) {
            return false;
        }
        if (seatNumber != other.seatNumber)
            return false;
        return true;
    }

}
