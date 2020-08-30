package client.customer.model;

import java.time.LocalDate;

public class RoomAvailabilityQuery {

    private String city;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private long numOfPeople;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public long getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(long numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    @Override
    public String toString() {
        return "RoomAvailabilityQuery{" +
                "city='" + city + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numOfPeople=" + numOfPeople +
                '}';
    }
}
