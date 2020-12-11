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
    private ITrainDataService trainDataService = new TrainDataService();
    

    public WebTicketManager(IBookingReferenceService bookingReferenceService, ITrainDataService dataTrainService) throws InterruptedException {
        this.bookingReferenceService = bookingReferenceService;
        this.trainDataService = dataTrainService;
    }
    
    public WebTicketManager() throws InterruptedException {
        bookingReferenceService = new BookingReferenceService();
    }

    public Reservation reserve(String trainId, int nbSeatRequested) throws IOException, InterruptedException {

        // get the train
        TrainTopology train = trainDataService.getTrainTopology(trainId);
        if (train.doNotExceedTrainCapacity(nbSeatRequested)) {
            BookingAttempt bookingAttempt = train.builBookingAttempt(nbSeatRequested);
            if (bookingAttempt.isFullfiled()) {
                String bookingRef = bookingReferenceService.getBookingReference();
                bookingAttempt.assignReference(bookingRef);
                
                trainDataService.applyReservation(trainId, bookingAttempt.getSeats(), bookingRef);
                return new Reservation(trainId, bookingRef, bookingAttempt.getSeats());
            } else {
                return new Reservation(trainId);
            }
        }
        return new Reservation(trainId);
    }
}
