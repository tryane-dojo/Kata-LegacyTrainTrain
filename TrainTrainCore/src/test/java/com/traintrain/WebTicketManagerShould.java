package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebTicketManagerShould {

    private static final String TRAIN_ID = "express_2000";
    WebTicketManager            webTicketManager;

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        webTicketManager = new WebTicketManager();
    }

    @Test
    void reserve_seats_requested_when_train_is_empty() throws IOException, InterruptedException {

        //given
        int nbSeatRequested = 1;

        // when
        Reservation reservation = webTicketManager.reserve(TRAIN_ID, nbSeatRequested);

        //then
        assertThat(reservation.getTrain_id()).isEqualTo(TRAIN_ID);
        assertThat(reservation.getBooking_reference()).isNotEmpty();
        assertThat(reservation.getSeats()).containsExactly("1A");

    }

}
