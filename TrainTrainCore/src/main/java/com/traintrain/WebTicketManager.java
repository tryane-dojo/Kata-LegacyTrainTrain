package com.traintrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cache.ITrainCaching;
import com.cache.SeatEntity;
import com.cache.TrainCaching;

public class WebTicketManager {
    
    static final String urITrainDataService = "http://localhost:8081";
    private IBookingReferenceService bookingReferenceService;
    private IDataTrainService dataTrainService = new DataTrainService();
    

    public WebTicketManager(IBookingReferenceService bookingReferenceService, IDataTrainService dataTrainService) throws InterruptedException {
        this.bookingReferenceService = bookingReferenceService;
        this.dataTrainService = dataTrainService;
    }
    
    public WebTicketManager() throws InterruptedException {
        bookingReferenceService = new BookingReferenceService();
    }

    public Reservation reserve(String trainId, int nbSeatRequested) throws IOException, InterruptedException {

        // get the train
        TrainTopology train = dataTrainService.getTrainTopology(trainId);
        if (train.doNotExceedTrainCapacity(nbSeatRequested)) {
            BookingAttempt bookingAttempt = train.builBookingAttempt(nbSeatRequested);
            if (bookingAttempt.isFullfiled()) {
                String bookingRef = bookingReferenceService.getBookingReference();
                bookingAttempt.assignReference(bookingRef);
                
                dataTrainService.applyReservation(trainId, bookingAttempt.getSeats(), bookingRef);
                return new Reservation(trainId, bookingRef, bookingAttempt.getSeats());
            } else {
                return new Reservation(trainId);
            }
        }
        return new Reservation(trainId);
    }
}
