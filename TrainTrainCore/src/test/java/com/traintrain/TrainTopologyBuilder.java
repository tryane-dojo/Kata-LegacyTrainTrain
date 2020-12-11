package com.traintrain;

import java.util.ArrayList;
import java.util.List;

public class TrainTopologyBuilder {

    private String     trainId;
    private List<Seat> seats = new ArrayList<>();

    private TrainTopologyBuilder(String trainId) {
        this.trainId = trainId;
    }

    public static TrainTopologyBuilder aTrain(String trainId) {

        return new TrainTopologyBuilder(trainId);
    }

    public TrainTopologyBuilder withFreeSeat(int seatNumber, String coachName) {
        Seat seat = new Seat(coachName, seatNumber);
        seats.add(seat);
        return this;
    }

    public TrainTopologyBuilder withBookedSeat(int seatNumber, String coachName, String bookingReference) {
        Seat seat = new Seat(coachName, seatNumber, bookingReference);
        seats.add(seat);
        return this;
    }

    public TrainTopology build() {
        return new TrainTopology(trainId, List.copyOf(seats));
    }

}
