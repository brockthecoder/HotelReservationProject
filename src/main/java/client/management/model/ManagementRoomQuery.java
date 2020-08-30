package client.management.model;

import java.time.LocalDate;

public class ManagementRoomQuery {

    private ManagementHotelDetails hotel;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private long numOfPeople;

    public ManagementHotelDetails getHotel() {
        return hotel;
    }

    public void setHotel(ManagementHotelDetails hotel) {
        this.hotel = hotel;
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
        return "ManagementRoomQuery{" +
                "hotel=" + hotel +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numOfPeople=" + numOfPeople +
                '}';
    }
}
