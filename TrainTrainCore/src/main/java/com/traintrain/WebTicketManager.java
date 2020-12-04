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
        String bookingRef;

        // get the train
        Train train = this.getTrain(trainId);

        if (this.canWeBookSeats(nbSeatsToBook, train)) {

            // find seat without booking ref
            List<Seat> availableSeatsToBook = train.findAvailableSeatsToBook(nbSeatsToBook);

            if (availableSeatsToBook.size() == nbSeatsToBook) {
                bookingRef = getBookReference();
                
                for (Seat availableSeat : availableSeatsToBook) {
                    availableSeat.setBookingRef(bookingRef);
                }

                this.trainCaching.Save(this.toSeatsEntities(trainId, availableSeatsToBook, bookingRef));

                if (availableSeatsToBook.size() == 0) {
                    String output = String.format("Reserved seat(s): ", availableSeatsToBook.size());
                    System.out.println(output);
                }

                sendReserveToTrainService(trainId, availableSeatsToBook, bookingRef);
                return String.format(
                        "{{\"train_id\": \"%s\", \"booking_reference\": \"%s\", \"seats\": %s}}",
                        trainId,
                        bookingRef,
                        this.dumpSeats(availableSeatsToBook));
            } else {
                return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}",
                        trainId);
            }

        }
        return String.format("{{\"train_id\": \"%s\", \"booking_reference\": \"\", \"seats\": []}}", trainId);
    }

    protected String getBookReference() {
		String bookingRef;
		Client client = ClientBuilder.newClient();
		try {
			
			WebTarget target = client.target(uriBookingReferenceService + "/booking_reference/");
			bookingRef = target.request(MediaType.APPLICATION_JSON).get(String.class);
		} finally {
		    client.close();
		}
		return bookingRef;
	}

	protected void sendReserveToTrainService(String trainId, List<Seat> availableSeats, String bookingRef) {
        String postContent = buildPostContent(trainId, bookingRef, availableSeats);

        Client client = ClientBuilder.newClient();
        try {
            WebTarget webTarget = client.target(urITrainDataService + "/reserve/");
            Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
            request.post(Entity.text(postContent));
        } finally {
            client.close();
        }
    }

    private boolean canWeBookSeats(int nbSeatsToBook, Train train) {
        return (train.getReservedSeatCount() + nbSeatsToBook) <= Math.floor(ThresholdManager.getMaxRes() * train.getMaxSeat());
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

    protected Train getTrain(String train) throws IOException {
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
        return new Train(JsonTrainTopology);
    }  
    
    private List<SeatEntity> toSeatsEntities(String train, List<Seat> availableSeats, String bookingRef) throws InterruptedException {
        List<SeatEntity> seatEntities = new ArrayList<SeatEntity>();
        for (Seat seat : availableSeats) {
            seatEntities.add(new SeatEntity(train, bookingRef, seat.getCoachName(), seat.getSeatNumber()));
        }
        return seatEntities;
    }
}
