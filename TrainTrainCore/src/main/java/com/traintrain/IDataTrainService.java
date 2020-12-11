package com.traintrain;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IDataTrainService {

	Reservation applyReservation(String trainId, List<Seat> availableSeats, String bookingRef)
			throws JsonProcessingException ;

}
