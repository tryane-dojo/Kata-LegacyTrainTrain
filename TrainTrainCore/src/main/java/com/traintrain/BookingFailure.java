package com.traintrain;

import java.util.List;

public class BookingFailure extends BookingConfirmation {

    public BookingFailure(String trainId) {
        super(trainId, "", List.of());
    }

}
