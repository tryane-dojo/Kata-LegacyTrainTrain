package com.traintrain;

import com.cache.ITrainCaching;
import com.cache.SeatEntity;
import com.cache.TrainCaching;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebTicketManager {
    private static final String uriBookingReferenceService = "http://localhost:51691";
    private static final String urITrainDataService = "http://localhost:50680";
    private ITrainCaching trainCaching;

    public WebTicketManager() throws InterruptedException {
        this.trainCaching = new TrainCaching();
        this.trainCaching.Clear();
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

    public String reserve(String trainId, int nbSeatsToBook) throws IOException, InterruptedException {
        List<Seat> availableSeats = new ArrayList<>();
        int nbAvailableSeats = 0;
        String bookingRef;

        // get the train
        String JsonTrain = this.getTrain(trainId);
        Train train = new Train(JsonTrain);

        if (this.canWeBookSeats(nbSeatsToBook, train)) {
            int numberOfReserv = 0;

            // find seat without booking ref
            for (int index = 0, i = 0; index < train.Seats.size(); index++) {
                Seat seat = train.Seats.get(index);
                if (seat.getBookingRef() == "") {
                    i++;
                    if (i <= nbSeatsToBook) {
                        availableSeats.add(seat);
                    }
                }
            }

            for (Seat seat : availableSeats) {
                nbAvailableSeats++;
            }

            int reservedSeats = 0;


            if (nbAvailableSeats != nbSeatsToBook) {
                return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}",
                        trainId);
            } else {
                Client client = ClientBuilder.newClient();
                try {
                    bookingRef = this.getBookRef(client);
                } finally {
                    client.close();
                }
                for (Seat availableSeat : availableSeats) {
                    availableSeat.setBookingRef(bookingRef);
                    numberOfReserv++;
                    reservedSeats++;
                }
            }

            if (numberOfReserv == nbSeatsToBook) {

                this.trainCaching.Save(this.toSeatsEntities(trainId, availableSeats, bookingRef));

                if (reservedSeats == 0) {
                    String output = String.format("Reserved seat(s): ", reservedSeats);
                    System.out.println(output);
                }

                String todod = "[TODOD]";

                String postContent = buildPostContent(trainId, bookingRef, availableSeats);

                Client client = ClientBuilder.newClient();
                try {
                    WebTarget webTarget = client.target(urITrainDataService + "/reserve/");
                    Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
                    request.post(Entity.text(postContent));
                } finally {
                    client.close();
                }
                return String.format(
                        "{{\"train_id\": \"%s\", \"booking_reference\": \"%s\", \"seats\": %s}}",
                        trainId,
                        bookingRef,
                        this.dumpSeats(availableSeats));
            }

        }
        return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}", trainId);
    }

    private boolean canWeBookSeats(int nbSeatsToBook, Train train) {
        return (train.ReservedSeats + nbSeatsToBook) <= Math.floor(ThresholdManager.getMaxRes() * train.getMaxSeat());
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
        } finally {
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
