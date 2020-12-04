package com.traintrain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class WebTicketManagerShould {

	@Test public void 
	return_empty_seat_list_if_rervation_capacity_is_over_threshold() throws InterruptedException, IOException {
		
		WebTicketManager webTicketManager = new WebTicketManager() {

			@Override
			protected String getTrain(String train) {
				return "{\"seats\": {\"1A\": {\"booking_reference\": \"reserved\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
			}
			
		};
		
		String trainId = "train_id";
		int nbSeatsToBook = 1;
		
		String reservation = webTicketManager.reserve(trainId, nbSeatsToBook);
		
		assertThat(reservation).isEqualTo("{{\"train_id\": \"train_id\", \"booking_reference\": \"\", \"seats\": []}}");
	}

	
	
}
