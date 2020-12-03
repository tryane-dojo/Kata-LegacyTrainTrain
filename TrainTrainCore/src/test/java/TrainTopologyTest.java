import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traintrain.Seat;
import com.traintrain.SeatJson;


public class TrainTopologyTest {

    @Test
    public void should_deserialize_train_topology() {
        String trainTopology =
                "{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        List<Seat> Seats = new ArrayList<Seat>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Map<String, SeatJson>> stuff_in_stuff = objectMapper.readValue(trainTopology, new TypeReference<Map<String, Map<String, SeatJson>>>() {
            });

            for (Map<String, SeatJson> value : stuff_in_stuff.values()) {
                for (SeatJson seatJson : value.values()) {
                    int seat_number = Integer.parseInt(seatJson.seat_number);
                    Seats.add(new Seat(seatJson.coach, seat_number, seatJson.booking_reference));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AssertSeats(Seats);
    }

    private void AssertSeats(List<Seat> seats) {
        String seatA1 = seats.get(0).toString();
        String seatA2 = seats.get(1).toString();
        Assertions.assertEquals(2, seats.size());
        Assertions.assertEquals("A1", seatA1);
        Assertions.assertEquals("A2", seatA2);
    }
}
