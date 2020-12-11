package com.traintrain;

import java.util.List;

public class BookingAttemptFailure extends BookingAttempt {

    public BookingAttemptFailure(String trainId, int nbSeatRequested) {
        super(trainId, nbSeatRequested, List.of());
    }

}
