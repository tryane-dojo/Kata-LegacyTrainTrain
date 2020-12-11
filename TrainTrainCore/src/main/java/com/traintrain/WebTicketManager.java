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

    public Reservation attemptToBook(String trainId, int nbSeatRequested) throws IOException {

        TrainTopology train = trainDataService.getTrainTopology(trainId);
        if (train.canBookWithoutExceedCapacity(nbSeatRequested)) {
            BookingAttempt bookingAttempt = train.buildBookingAttempt(nbSeatRequested);
            if (bookingAttempt.isFullfiled()) {
                String bookingRef = bookingReferenceService.getBookingReference();
                bookingAttempt.assignReference(bookingRef);

                trainDataService.bookSeats(bookingAttempt);
                return new Reservation(trainId, bookingRef, bookingAttempt.getSeats());
            } else {
                return Reservation.emtpyBooking(trainId);
            }
        }
        return Reservation.emtpyBooking(trainId);
    }
}
