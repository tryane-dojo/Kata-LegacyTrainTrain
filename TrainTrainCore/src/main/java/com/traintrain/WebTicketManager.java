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

    public BookingConfirmation attemptToBook(String trainId, int nbSeatRequested) throws IOException {

        TrainTopology train = trainDataService.getTrainTopology(trainId);
        if (train.canBookWithoutExceedCapacity(nbSeatRequested)) {
            BookingAttempt bookingAttempt = train.buildBookingAttempt(nbSeatRequested);
            if (bookingAttempt.isFullfiled()) {
                String bookingReference = bookingReferenceService.getBookingReference();
                bookingAttempt.assignReference(bookingReference);

                trainDataService.bookSeats(bookingAttempt);
                return bookingAttempt.confirm();
            } else {
                return new BookingFailure(trainId);
            }
        }
        return new BookingFailure(trainId);
    }
}
