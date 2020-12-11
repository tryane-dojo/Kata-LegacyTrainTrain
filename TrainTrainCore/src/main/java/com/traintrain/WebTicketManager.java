package com.traintrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cache.ITrainCaching;
import com.cache.SeatEntity;
import com.cache.TrainCaching;

public class WebTicketManager {
    
    static final String urITrainDataService = "http://localhost:8081";
    private ITrainCaching trainCaching;
    private IBookingReferenceService bookingReferenceService;
    private IDataTrainService dataTrainService = new DataTrainService();
    

    public WebTicketManager(IBookingReferenceService bookingReferenceService, IDataTrainService dataTrainService) throws InterruptedException {
        this.bookingReferenceService = bookingReferenceService;
        this.dataTrainService = dataTrainService;
        trainCaching = new TrainCaching();
        trainCaching.Clear();
    }
    
    public WebTicketManager() throws InterruptedException {
        trainCaching = new TrainCaching();
        trainCaching.Clear();
        bookingReferenceService = new BookingReferenceService();
    }

    public Reservation reserve(String trainId, int nbSeatRequested) throws IOException, InterruptedException {

        // get the train
        TrainTopology train = dataTrainService.getTrainTopology(trainId);
        if (train.doNotExceedTrainCapacity(nbSeatRequested)) {
            int numberOfReserv = 0;
            List<Seat> availableSeats = new ArrayList<Seat>();
            // find seats to reserve
            for (Seat seat : train.getSeats()) {
                if (seat.isFree()) {
                    if (numberOfReserv < nbSeatRequested) {
                        numberOfReserv++;
                        availableSeats.add(seat);
                    }
                }
            }
            
            if (numberOfReserv == nbSeatRequested) {
                String bookingRef = bookingReferenceService.getBookingReference();
                for (Seat availableSeat : availableSeats) {
                    availableSeat.setBookingRef(bookingRef);
                }
                this.trainCaching.Save(toSeatsEntities(trainId, availableSeats, bookingRef));
                
                if (numberOfReserv == 0) {
                    String output = String.format("Reserved seat(s): ", numberOfReserv);
                    System.out.println(output);
                }
                dataTrainService.applyReservation(trainId, availableSeats, bookingRef);
                return new Reservation(trainId, bookingRef, availableSeats);
            } else {
                return new Reservation(trainId);
            }
        }
        return new Reservation(trainId);
    }

    private List<SeatEntity> toSeatsEntities(String train, List<Seat> availableSeats, String bookingRef) throws InterruptedException {
        List<SeatEntity> seatEntities = new ArrayList<SeatEntity>();
        for (Seat seat : availableSeats) {
            seatEntities.add(new SeatEntity(train, bookingRef, seat.getCoachName(), seat.getSeatNumber()));
        }
        return seatEntities;
    }
}
