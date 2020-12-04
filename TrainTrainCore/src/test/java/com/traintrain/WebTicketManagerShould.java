package com.traintrain;

import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WebTicketManagerShould {

    @Test
    public void
    return_empty_seat_list_if_rervation_capacity_is_over_threshold() throws InterruptedException, IOException {

        WebTicketManager webTicketManager = new WebTicketManager() {

            @Override
            protected Train getTrain(String train) throws IOException {
                return new Train("{\"seats\": {\"1A\": {\"booking_reference\": \"reserved\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}");
            }

        };

        String trainId = "train_id";
        int nbSeatsToBook = 1;

        String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);

        assertThat(reservation).isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"\", \"seats\": []}}");
    }

    @Test
    public void
    reservation_successfull_if_train_is_empty() throws InterruptedException, IOException {

        WebTicketManager webTicketManager = new WebTicketManager() {

            @Override
            protected Train getTrain(String train) throws IOException {
                return new Train("{\"seats\": "
                        + "{"
                        + "\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, "
                        + "\"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"},"
                        + "\"3A\": {\"booking_reference\": \"\", \"seat_number\": \"3\", \"coach\": \"A\"},"
                        + "\"4A\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"A\"},"
                        + "\"5A\": {\"booking_reference\": \"\", \"seat_number\": \"5\", \"coach\": \"A\"}"
                        + ""
                        + "}}");
            }

            @Override
            protected String getBookRef(Client client) {
                return "75bcd15";
            }

            @Override
            protected void sendReserveToTrainService(String trainId, List<Seat> availableSeats, String bookingRef) {


            }

        };

        String trainId = "train_id";
        int nbSeatsToBook = 1;

        String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);

        assertThat(reservation).isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"75bcd15\", \"seats\": [\"1A\"]}}");
    }


}
