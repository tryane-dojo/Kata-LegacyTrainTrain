package com.traintrain;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TrainDataService implements ITrainDataService {

    static final String urITrainDataService = "http://localhost:8081";

    @Override
    public void bookSeats(BookingAttempt bookingAttempt) throws JsonProcessingException {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget webTarget = client.target(TrainDataService.urITrainDataService + "/reserve/");
            Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON_TYPE);

            Form form = new Form();
            form.param("train_id", bookingAttempt.getTrainId());

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (Seat seat : bookingAttempt.getSeats()) {
                builder.append("\"").append(seat.toString()).append("\"");
            }
            builder.append("]");

            form.param("seats", builder.toString());
            form.param("booking_reference", bookingAttempt.getReference());

            request.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        } finally {
            client.close();
        }

    }

    @Override
    public TrainTopology getTrainTopology(String trainId) throws IOException {
        String JsonTrainTopology;
        Client client = ClientBuilder.newClient();
        try {

            WebTarget target = client.target(TrainDataService.urITrainDataService + "/data_for_train/");
            WebTarget path = target.path(String.valueOf(trainId));
            Invocation.Builder request = path.request(MediaType.APPLICATION_JSON);
            JsonTrainTopology = request.get(String.class);
        } finally {
            client.close();
        }
        return TrainTopology.fromJson(trainId, JsonTrainTopology);
    }

}
