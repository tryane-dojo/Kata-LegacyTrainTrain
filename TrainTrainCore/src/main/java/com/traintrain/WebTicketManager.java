package com.traintrain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import com.cache.ITrainCaching;
import com.cache.SeatEntity;
import com.cache.TrainCaching;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebTicketManager {
    private static final String uriBookingReferenceService = "http://localhost:8082";
    private static final String urITrainDataService = "http://localhost:8081";
    private ITrainCaching trainCaching;

    public WebTicketManager() throws InterruptedException {
        trainCaching = new TrainCaching();
        trainCaching.Clear();
    }

    public Reservation reserve(String trainId, int nbSeatRequested) throws IOException, InterruptedException {
        List<Seat> availableSeats = new ArrayList<Seat>();
        String bookingRef;

        // get the train
        TrainTopology train = getTrainTopology(trainId);
        if ((train.reservedSeats + nbSeatRequested) <= Math.floor(ThresholdManager.getMaxRes() * train.getMaxSeat())) {
            int numberOfReserv = 0;
            // find seats to reserve
            for (Seat seat : train.seats) {
                if (seat.getBookingRef() == "") {
                    if (numberOfReserv < nbSeatRequested) {
                        numberOfReserv++;
                        availableSeats.add(seat);
                    }
                }
            }
            
            if (numberOfReserv != nbSeatRequested) {
                return new Reservation(trainId);
            } else {
                Client client = ClientBuilder.newClient();
                try {
                    bookingRef = getBookRef(client);
                }
                finally {
                    client.close();
                }
                for (Seat availableSeat : availableSeats) {
                    availableSeat.setBookingRef(bookingRef);
                }
            }

            if (numberOfReserv == nbSeatRequested) {

                this.trainCaching.Save(toSeatsEntities(trainId, availableSeats, bookingRef));

                if (numberOfReserv == 0) {
                    String output = String.format("Reserved seat(s): ", numberOfReserv);
                    System.out.println(output);
                }

                Reservation postReservation = new Reservation(trainId, bookingRef, availableSeats);

                Client client = ClientBuilder.newClient();
                try {
                    WebTarget webTarget = client.target(urITrainDataService + "/reserve/");
                    Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
                    
                    Form form = new Form();
                    form.param("train_id", postReservation.getTrain_id());
                    
                    StringBuilder builder = new StringBuilder();
                    builder.append("[");
                    for (String seat : postReservation.getSeats()) {
                        builder.append("\"").append(seat).append("\"");
                    }
                    builder.append("]");
                    
                    form.param("seats", builder.toString());
                    form.param("booking_reference", postReservation.getBooking_reference());
                                        
                    String string = new ObjectMapper().writeValueAsString(postReservation);
                    System.out.println(string);
                    request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                }
                finally {
                    client.close();
                }
                return new Reservation(trainId, bookingRef, availableSeats);
            }

        }
        return new Reservation(trainId);
    }

    protected TrainTopology getTrainTopology(String trainId) throws IOException {
        
        String JsonTrainTopology;
        Client client = ClientBuilder.newClient();
        try {
        
            WebTarget target = client.target(urITrainDataService + "/data_for_train/");
            WebTarget path = target.path(String.valueOf(trainId));
            Invocation.Builder request = path.request(MediaType.APPLICATION_JSON);
            JsonTrainTopology = request.get(String.class);
        }
        finally {
            client.close();
        }
        String trainTopology = JsonTrainTopology;
        return new TrainTopology(trainTopology);
    }
    
    protected String getBookRef(Client client) {
        String booking_ref;

        WebTarget target = client.target(uriBookingReferenceService + "/booking_reference/");
        booking_ref = target.request(MediaType.APPLICATION_JSON).get(String.class);

        return booking_ref;
    }

    private List<SeatEntity> toSeatsEntities(String train, List<Seat> availableSeats, String bookingRef) throws InterruptedException {
        List<SeatEntity> seatEntities = new ArrayList<SeatEntity>();
        for (Seat seat : availableSeats) {
            seatEntities.add(new SeatEntity(train, bookingRef, seat.getCoachName(), seat.getSeatNumber()));
        }
        return seatEntities;
    }
}
