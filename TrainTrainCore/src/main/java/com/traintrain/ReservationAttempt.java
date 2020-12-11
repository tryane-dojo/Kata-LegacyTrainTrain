package com.traintrain;

import java.util.ArrayList;
import java.util.List;

public class ReservationAttempt {
	
	public List<Seat> findAvailableSeats(int nbSeatRequested, List<Seat> trainSeats) {
		List<Seat> availableSeats = new ArrayList<Seat>();
		// find seats to reserve
		for (Seat seat : trainSeats) {
		    if (seat.getBookingRef() == "") {
		        if (availableSeats.size() < nbSeatRequested) {
		            availableSeats.add(seat);
		        }
		    }
		}
		return availableSeats;
	}

}
