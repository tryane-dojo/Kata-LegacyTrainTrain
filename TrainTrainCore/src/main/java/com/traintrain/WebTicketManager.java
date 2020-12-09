package com.traintrain;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.cache.ITrainCaching;
import com.cache.SeatEntity;
import com.cache.TrainCaching;

public class WebTicketManager {
    private static final String uriBookingReferenceService = "http://localhost:8082";
    private static final String urITrainDataService = "http://localhost:8081";
    private ITrainCaching trainCaching;

    public WebTicketManager() throws InterruptedException {
        trainCaching = new TrainCaching();
        trainCaching.Clear();
    }

    public String reserve(String train, int seats) throws IOException, InterruptedException {
        List<Seat> availableSeats = new ArrayList<Seat>();
        int count = 0;
        String result = "";
        String bookingRef;

        // get the train
        String JsonTrain = getTrain(train);

        result = JsonTrain;

        Train trainInst = new Train(JsonTrain);
        if ((trainInst.ReservedSeats + seats) <= Math.floor(ThresholdManager.getMaxRes() * trainInst.getMaxSeat())) {
            int numberOfReserv = 0;
            // find seats to reserve
            for (int index = 0, i = 0; index < trainInst.Seats.size(); index++) {
                Seat each = trainInst.Seats.get(index);
                if (each.getBookingRef() == "") {
                    i++;
                    if (i <= seats) {
                        availableSeats.add(each);
                    }
                }
            }

            for (Seat seat : availableSeats) {
                count++;
            }

            int reservedSets = 0;


            if (count != seats) {
                return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}",
                        train);
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
                    numberOfReserv++;
                    reservedSets++;
                }
            }

            if (numberOfReserv == seats) {

                this.trainCaching.Save(toSeatsEntities(train, availableSeats, bookingRef));

                if (reservedSets == 0) {
                    String output = String.format("Reserved seat(s): ", reservedSets);
                    System.out.println(output);
                }

                String todod = "[TODOD]";

                String postContent = buildPostContent(train, bookingRef, availableSeats);

                Client client = ClientBuilder.newClient();
                try {
                    WebTarget webTarget = client.target(urITrainDataService + "/reserve/");
                    Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
                    request.post(Entity.text(postContent));
                }
                finally {
                    client.close();
                }
                return String.format(
                        "{{\"train_id\": \"%s\", \"booking_reference\": \"%s\", \"seats\": %s}}",
                        train,
                        bookingRef,
                        dumpSeats(availableSeats));
            }

        }
        return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}", train);
    }

    private static String buildPostContent(String trainId, String booking_ref, List<Seat> availableSeats) {
        StringBuilder seats = new StringBuilder("[");

        boolean firstTime = true;

        for (Seat seat : availableSeats) {
            if (!firstTime) {
                seats.append(", ");
            } else {
                firstTime = false;
            }

            seats.append(String.format("\"%d%s\"", seat.getSeatNumber(), seat.getCoachName()));
        }

        seats.append("]");

        String result = String.format(
                "{{\r\n\t\"train_id\": \"%s\",\r\n\t\"seats\": %s,\r\n\t\"booking_reference\": \"%S\"\r\n}}",
                trainId, seats.toString(), booking_ref);

        return result;
    }

    private String dumpSeats(List<Seat> seats) {
        StringBuilder sb = new StringBuilder("[");

        boolean firstTime = true;

        for (Seat seat : seats) {
            if (!firstTime) {
                sb.append(", ");
            } else {
                firstTime = false;
            }

            sb.append(String.format("\"%d%s\"", seat.getSeatNumber(), seat.getCoachName()));
        }

        sb.append("]");

        return sb.toString();
    }

    protected String getTrain(String train) {
        String JsonTrainTopology;
        Client client = ClientBuilder.newClient();
        try {

            WebTarget target = client.target(urITrainDataService + "/api/data_for_train/");
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
