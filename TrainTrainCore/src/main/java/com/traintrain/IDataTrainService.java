package com.traintrain;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IDataTrainService {

	void applyReservation(String trainId, List<Seat> availableSeats, String bookingRef)
			throws JsonProcessingException ;

    TrainTopology getTrainTopology(String trainId) throws IOException;

}
