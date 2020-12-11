package com.traintrain;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataTrainService implements IDataTrainService {

	@Override
	public Reservation applyReservation(String trainId, List<Seat> availableSeats, String bookingRef)
			throws JsonProcessingException {
	
		Reservation postReservation = new Reservation(trainId, bookingRef, availableSeats);
		
		Client client = ClientBuilder.newClient();
		try {
		    WebTarget webTarget = client.target(WebTicketManager.urITrainDataService + "/reserve/");
		    Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
		    
		    Form form = new Form();
		    form.param("train_id", postReservation.getTrain_id());
		    
		    StringBuilder builder = new StringBuilder();
		    builder.append("[");
		    for (String seat : postReservation.getSeats()) {
		        builder.append("\"").append(seat).append("\"");
		    }
		    builder.append("]");
		    
		    form.param("seats", builder.toString());
		    form.param("booking_reference", postReservation.getBooking_reference());
		                        
		    String string = new ObjectMapper().writeValueAsString(postReservation);
		    System.out.println(string);
		    request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		}
		finally {
		    client.close();
		}
		return postReservation;
	}

    @Override
    public TrainTopology getTrainTopology(String trainId) throws IOException {
        String JsonTrainTopology;
        Client client = ClientBuilder.newClient();
        try {
        
            WebTarget target = client.target(WebTicketManager.urITrainDataService + "/data_for_train/");
            WebTarget path = target.path(String.valueOf(trainId));
            Invocation.Builder request = path.request(MediaType.APPLICATION_JSON);
            JsonTrainTopology = request.get(String.class);
        }
        finally {
            client.close();
        }
        String trainTopology = JsonTrainTopology;
        return new TrainTopology(trainTopology);
    }

	
	
}
