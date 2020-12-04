package com.traintrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Train {
    public List<Seat> seats;

    public Train(String trainTopol) throws IOException {

        seats = new ArrayList<>();

        //var sample:
        //"{\"seats\": {\"1A\": {\"booking_reference\": \"\", \"seat_number\": \"1\", \"coach\": \"A\"}, \"2A\": {\"booking_reference\": \"\", \"seat_number\": \"2\", \"coach\": \"A\"}}}";
        final ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Map<String, SeatJson>> stuff_in_stuff = objectMapper.readValue(trainTopol, new TypeReference<Map<String, Map<String, SeatJson>>>() {
        });

        for (Map<String, SeatJson> value : stuff_in_stuff.values()) {
            for (SeatJson seatJson : value.values()) {
                int seat_number = Integer.parseInt(seatJson.seat_number);
                seats.add(new Seat(seatJson.coach, seat_number, seatJson.booking_reference));

            }
        }
    }

    public Train() {
        seats = new ArrayList<>();
    }

    public Train(List<Seat> seats) {
    	this();
    	this.seats = seats;
	}

    public int getReservedSeatCount() {
    	return (int) seats.stream().filter(Seat::isBooked).count();
    }
    
	public int getMaxSeat() {
        return this.seats.size();
    }

	List<Seat> findAvailableSeatsToBook(int nbSeatsToBook) {
		List<Seat> availableSeatsToBook = new ArrayList<>();
	
		for (int index = 0, i = 0; index < seats.size(); index++) {
		    Seat seat = seats.get(index);
		    if (seat.isNotBooked()) {
		        i++;
		        if (i <= nbSeatsToBook) {
		            availableSeatsToBook.add(seat);
		        }
		    }
		}
		return availableSeatsToBook;
	}

    
}