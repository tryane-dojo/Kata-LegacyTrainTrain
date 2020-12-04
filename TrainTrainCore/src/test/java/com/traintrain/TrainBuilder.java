package com.traintrain;

import java.util.ArrayList;
import java.util.List;

public class TrainBuilder {

	private List<Seat> seats = new ArrayList<>();

	public TrainBuilder addSeat(int seatNumber, String coachName, String bookingReference) {
		seats.add(new Seat(coachName, seatNumber, bookingReference));

		return this;
	}

	public Train build() {

		Train train = new Train(seats);
		return train;
	}

}
