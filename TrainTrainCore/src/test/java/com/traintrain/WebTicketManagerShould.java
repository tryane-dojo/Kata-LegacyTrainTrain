package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class WebTicketManagerShould {

    private static final String TRAIN_ID = "express_2000";
    
    WebTicketManager            webTicketManager;
    
    @Mock
    IBookingReferenceService bookingReferenceService;
    
    @Mock
    IDataTrainService dataTrainService;

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        webTicketManager = new WebTicketManager(bookingReferenceService, dataTrainService) {

            @Override
            protected TrainTopology getTrainTopology(String trainId) throws IOException {
                TrainTopology trainTopology = new TrainTopology("{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}, \"3A\": {\"booking_reference\": \"\", \"seat_number\": \"3\", \"coach\": \"A\"}, \"4A\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"A\"}}}");
                return trainTopology;
            }
            
        };
        
    }

    @Test
    void reserve_seats_requested_when_train_is_empty() throws IOException, InterruptedException {

        //given
        int nbSeatRequested = 1;
        String bookingRef ="2dadaz4";
        List<Seat> availableSeats = new ArrayList<>();
        Seat seat = new Seat("A", 1);
		availableSeats.add(seat);
		Reservation reservationParameter = new Reservation(TRAIN_ID, bookingRef, availableSeats);
		when(bookingReferenceService.getBookingReference()).thenReturn(bookingRef);
		when(dataTrainService.applyReservation(eq(TRAIN_ID), any(), any())).thenReturn(reservationParameter);

        // when
        Reservation reservation = webTicketManager.reserve(TRAIN_ID, nbSeatRequested);

        //then
        assertThat(reservation.getTrain_id()).isEqualTo(TRAIN_ID);
        assertThat(reservation.getBooking_reference()).isEqualTo(bookingRef);
        assertThat(reservation.getSeats()).containsExactly("1A");

    }

}
