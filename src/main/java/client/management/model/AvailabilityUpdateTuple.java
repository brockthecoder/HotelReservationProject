package client.management.model;

import mutual.model.enums.UpdateAction;

import java.time.LocalDate;

public class AvailabilityUpdateTuple {

    private UpdateAction action;

    private LocalDate date;

    private long numOfRooms;

    private double nightlyRate;

    public LocalDate getDate() {
        return date;
    }

    public AvailabilityUpdateTuple() {
    }

    public AvailabilityUpdateTuple(UpdateAction action, LocalDate date, long numOfRooms) {
        this.action = action;
        this.date = date;
        this.numOfRooms = numOfRooms;
    }

    public AvailabilityUpdateTuple(UpdateAction action, LocalDate date, long numOfRooms, double nightlyRate) {
        this.action = action;
        this.date = date;
        this.numOfRooms = numOfRooms;
        this.nightlyRate = nightlyRate;
    }

    public AvailabilityUpdateTuple(LocalDate date, long numOfRooms) {
        this.date = date;
        this.numOfRooms = numOfRooms;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(long numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    public UpdateAction getAction() {
        return action;
    }

    public void setAction(UpdateAction action) {
        this.action = action;
    }

    public double getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(double nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AvailabilityUpdateTuple && ((AvailabilityUpdateTuple) o).getDate() == this.getDate());
    }

    @Override
    public String toString() {
        return "AvailabilityUpdateTuple{" +
                "action=" + action +
                ", date=" + date +
                ", numOfRooms=" + numOfRooms +
                ", nightlyRate=" + nightlyRate +
                '}';
    }
}
