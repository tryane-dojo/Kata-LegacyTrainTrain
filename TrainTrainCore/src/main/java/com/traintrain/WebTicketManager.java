package com.traintrain;

import java.io.IOException;

public class WebTicketManager {

    private IBookingReferenceService bookingReferenceService;
    private ITrainDataService        trainDataService;

    public WebTicketManager(IBookingReferenceService bookingReferenceService, ITrainDataService trainDataService) throws InterruptedException {
        this.bookingReferenceService = bookingReferenceService;
        this.trainDataService = trainDataService;
    }

    public WebTicketManager() {
        bookingReferenceService = new BookingReferenceService();
        trainDataService = new TrainDataService();
    }

    public Reservation reserve(String trainId, int nbSeatRequested) throws IOException {

        // get the train
        TrainTopology train = trainDataService.getTrainTopology(trainId);
        if (train.canBookWithoutExceedCapacity(nbSeatRequested)) {
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
