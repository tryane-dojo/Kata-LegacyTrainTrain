package com.traintrain;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class BookingReferenceService implements IBookingReferenceService {

	private static final String uriBookingReferenceService = "http://localhost:8082";
	@Override
	public String getBookingReference() {
		String bookingRef;
		Client client = ClientBuilder.newClient();
		try {			
			WebTarget target = client.target(uriBookingReferenceService + "/booking_reference/");
			bookingRef = target.request(MediaType.APPLICATION_JSON).get(String.class);
		}
		finally {
		    client.close();
		}
		return bookingRef;
	}

}
