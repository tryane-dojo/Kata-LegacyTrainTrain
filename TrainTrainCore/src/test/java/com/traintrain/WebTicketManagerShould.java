package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
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
    public void beforeEach() throws InterruptedException, IOException {
        webTicketManager = new WebTicketManager(bookingReferenceService, dataTrainService);
    }

    @SuppressWarnings("unchecked")
	@Test
    void reserve_seats_requested_when_train_is_empty() throws IOException, InterruptedException {
        //given
        TrainTopology trainTopology = new TrainTopology("{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}, \"3A\": {\"booking_reference\": \"\", \"seat_number\": \"3\", \"coach\": \"A\"}, \"4A\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"A\"}}}");
        when(dataTrainService.getTrainTopology(TRAIN_ID)).thenReturn(trainTopology);
        int nbSeatRequested = 1;
        String bookingRef ="2dadaz4";
        
		when(bookingReferenceService.getBookingReference()).thenReturn(bookingRef);

        // when
        Reservation reservation = webTicketManager.reserve(TRAIN_ID, nbSeatRequested);

        //then
        assertThat(reservation.getTrain_id()).isEqualTo(TRAIN_ID);
        assertThat(reservation.getBooking_reference()).isEqualTo(bookingRef);
        assertThat(reservation.getSeats()).containsExactly("1A");
        ArgumentCaptor<List<Seat>> seatsCaptor = ArgumentCaptor.forClass(List.class);
        verify(dataTrainService).applyReservation(eq(TRAIN_ID), seatsCaptor.capture(), eq(bookingRef));
        assertThat(seatsCaptor.getValue()).containsExactly(new Seat("A", 1, bookingRef));

    }
    
    @Test
    public void not_reserve_if_train_is_over_threshold() throws IOException, InterruptedException {
        // given
        TrainTopology trainTopology = new TrainTopology("{\"seats\": {\"1A\": {\"booking_reference\": \"aaa\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"bbbbb\", \"seat_number\": \"2\", \"coach\": \"A\"}, \"3A\": {\"booking_reference\": \"cccccc\", \"seat_number\": \"3\", \"coach\": \"A\"}, \"4A\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"A\"}}}");
        when(dataTrainService.getTrainTopology(TRAIN_ID)).thenReturn(trainTopology);
        
        // when
        Reservation reservation = webTicketManager.reserve(TRAIN_ID, 1);

        // then
        Assertions.assertThat(reservation.getBooking_reference()).isNull();
        Assertions.assertThat(reservation.getSeats()).isEmpty();
    }
    
    @Test
    public void reserve_all_seats_in_same_coach() throws IOException, InterruptedException {
    	// given
        TrainTopology trainTopology = new TrainTopology("{\"seats\": {"
        		+ "\"1A\": {\"booking_reference\": \"a\", \"seat_number\": \"1\", \"coach\": \"A\"}, "
        		+ "\"2A\": {\"booking_reference\": \"a\", \"seat_number\": \"2\", \"coach\": \"A\"}, "
        		+ "\"3A\": {\"booking_reference\": \"a\", \"seat_number\": \"3\", \"coach\": \"A\"}, "
        		+ "\"4A\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"A\"}, "
        		+ "\"1B\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"B\"}, "
        		+ "\"2B\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"B\"}, "
        		+ "\"3B\": {\"booking_reference\": \"\", \"seat_number\": \"3\", \"coach\": \"B\"}, "
        		+ "\"4B\": {\"booking_reference\": \"\", \"seat_number\": \"4\", \"coach\": \"B\"}, "
        		+ "\"5B\": {\"booking_reference\": \"\", \"seat_number\": \"5\", \"coach\": \"B\"} "
        		+ "}}");
        String bookingRef ="2dadaz4";
		when(bookingReferenceService.getBookingReference()).thenReturn(bookingRef);
        when(dataTrainService.getTrainTopology(TRAIN_ID)).thenReturn(trainTopology);
        
        // when
        Reservation reservation = webTicketManager.reserve(TRAIN_ID, 2);

        // then
        Assertions.assertThat(reservation.getBooking_reference()).isNotEmpty();
        Assertions.assertThat(reservation.getSeats()).containsExactly("1B", "2B");
    }

}
