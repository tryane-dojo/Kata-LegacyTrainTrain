package com.traintrain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ITrainDataService {

    TrainTopology getTrainTopology(String trainId) throws IOException;

    void bookSeats(BookingAttempt bookingAttempt) throws JsonProcessingException;

}
