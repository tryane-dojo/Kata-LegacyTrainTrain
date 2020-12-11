package com.traintrain;

public class Seat {
    private String coachName;
    private int seatNumber;
    private String bookingRef;

    public Seat(String coach, int seatNumber) {
        this(coach, seatNumber, "");
    }

    public Seat(String coachName, int seatNumber, String bookingRef) {
        this.coachName = coachName;
        this.seatNumber = seatNumber;
        this.bookingRef = bookingRef;
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

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    @Override
    public String toString() {
        return coachName + seatNumber;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookingRef == null) ? 0 : bookingRef.hashCode());
		result = prime * result + ((coachName == null) ? 0 : coachName.hashCode());
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
		if (bookingRef == null) {
			if (other.bookingRef != null)
				return false;
		} else if (!bookingRef.equals(other.bookingRef))
			return false;
		if (coachName == null) {
			if (other.coachName != null)
				return false;
		} else if (!coachName.equals(other.coachName))
			return false;
		if (seatNumber != other.seatNumber)
			return false;
		return true;
	}

    boolean isFree() {
        return getBookingRef() == "";
    }
    
    
}
