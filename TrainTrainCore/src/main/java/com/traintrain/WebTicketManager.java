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

    public Reservation reserve(String train, int seats) throws IOException, InterruptedException {
        List<Seat> availableSeats = new ArrayList<Seat>();
        String bookingRef;

        // get the train
        String JsonTrain = getTrain(train);
        Train trainInst = new Train(JsonTrain);
        if ((trainInst.reservedSeats + seats) <= Math.floor(ThresholdManager.getMaxRes() * trainInst.getMaxSeat())) {
            int numberOfReserv = 0;
            // find seats to reserve
            for (Seat seat : trainInst.seats) {
                if (seat.getBookingRef() == "") {
                    if (numberOfReserv < seats) {
                        numberOfReserv++;
                        availableSeats.add(seat);
                    }
                }
            }
            
            if (numberOfReserv != seats) {
                return new Reservation(train);
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

            if (numberOfReserv == seats) {

                this.trainCaching.Save(toSeatsEntities(train, availableSeats, bookingRef));

                if (numberOfReserv == 0) {
                    String output = String.format("Reserved seat(s): ", numberOfReserv);
                    System.out.println(output);
                }

                String todod = "[TODOD]";

                Reservation postReservation = new Reservation(train, bookingRef, availableSeats);

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
                    request.post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                }
                finally {
                    client.close();
                }
                return new Reservation(train, bookingRef, availableSeats);
            }

        }
        return new Reservation(train);
    }

    protected String getTrain(String train) {
        String JsonTrainTopology;
        Client client = ClientBuilder.newClient();
        try {

            WebTarget target = client.target(urITrainDataService + "/data_for_train/");
            WebTarget path = target.path(String.valueOf(train));
            Invocation.Builder request = path.request(MediaType.APPLICATION_JSON);
            JsonTrainTopology = request.get(String.class);
        }
        finally {
            client.close();
        }
        return JsonTrainTopology;
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
