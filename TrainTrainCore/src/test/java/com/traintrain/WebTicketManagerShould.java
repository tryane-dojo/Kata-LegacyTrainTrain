package com.traintrain;

import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WebTicketManagerShould {

    private static final String EMPTY_BOOKING_REFERENCE = "";

    @Test
    public void return_empty_seat_list_if_rervation_capacity_is_over_threshold()
            throws InterruptedException, IOException {

        WebTicketManager webTicketManager = new WebTicketManager() {

            @Override
            protected Train getTrain(String train) throws IOException {

                String bookingReference = "A2AUEJ5EE";
                return new TrainBuilder().addSeat(1, "A", bookingReference).addSeat(2, "A", EMPTY_BOOKING_REFERENCE).build();

            }

        };

        String trainId = "train_id";
        int nbSeatsToBook = 1;

        String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);

        assertThat(reservation).isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"\", \"seats\": []}}");
    }

    @Test
    public void reservation_successfull_if_train_is_empty() throws InterruptedException, IOException {

        WebTicketManager webTicketManager = new WebTicketManager() {

            @Override
            protected Train getTrain(String train) throws IOException {

                return new TrainBuilder()
                        .addSeat(1, "A", EMPTY_BOOKING_REFERENCE)
                        .addSeat(2, "A", EMPTY_BOOKING_REFERENCE)
                        .addSeat(3, "A", EMPTY_BOOKING_REFERENCE)
                        .addSeat(4, "A", EMPTY_BOOKING_REFERENCE)
                        .addSeat(5, "A", EMPTY_BOOKING_REFERENCE)
                        .build();

            }

            @Override
            protected String getBookReference() {
                return "75bcd15";
            }

            @Override
            protected void sendReserveToTrainService(String trainId, List<Seat> availableSeats, String bookingRef) {

            }

        };

        String trainId = "train_id";
        int nbSeatsToBook = 1;

        String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);

        assertThat(reservation)
                .isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"75bcd15\", \"seats\": [\"1A\"]}}");
    }

    @Test
    public void reserved_two_seats_in_the_same_coach() throws InterruptedException, IOException {
        WebTicketManager webTicketManager = new WebTicketManager() {

            @Override
            protected Train getTrain(String train) throws IOException {

                return new TrainBuilder()
                        .addSeat(1, "A", "AAA")
                        .addSeat(2, "A", "AAA")
                        .addSeat(3, "A", "AAA")
                        .addSeat(4, "A", "AAA")
                        .addSeat(5, "A", EMPTY_BOOKING_REFERENCE)
                        .addSeat(1, "B", EMPTY_BOOKING_REFERENCE)
                        .addSeat(2, "B", EMPTY_BOOKING_REFERENCE)
                        .addSeat(3, "B", EMPTY_BOOKING_REFERENCE)
                        .addSeat(4, "B", EMPTY_BOOKING_REFERENCE)
                        .addSeat(5, "B", EMPTY_BOOKING_REFERENCE)
                        .build();

            }

            @Override
            protected String getBookReference() {
                return "75bcd15";
            }

            @Override
            protected void sendReserveToTrainService(String trainId, List<Seat> availableSeats, String bookingRef) {

            }

        };

        String trainId = "train_id";
        int nbSeatsToBook = 2;

        String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);

        assertThat(reservation)
                .isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"75bcd15\", \"seats\": [\"1B\", \"2B\"]}}");
    }

}
