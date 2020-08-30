package client.mutual.model;

import client.customer.model.HotelDetails;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class CustomerReservation implements Comparable<CustomerReservation> {

    private long id;

    private DecimalFormat df;

    private HotelDetails hotel;

    private Customer customer;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private RoomListing roomListing;

    private double total;

    private ReservationPaymentInfo paymentInfo;

    public CustomerReservation() {
        df = new DecimalFormat("0.00");
        paymentInfo = new ReservationPaymentInfo();
    }

    public CustomerReservation(int id, HotelDetails hotel, Customer customer, LocalDate checkInDate, LocalDate checkOutDate, RoomListing roomListing, double total, ReservationPaymentInfo paymentInfo) {
        this.df = new DecimalFormat("0.00");
        this.id = id;
        this.hotel = hotel;
        this.customer = customer;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomListing = roomListing;
        this.total = total;
        this.paymentInfo = paymentInfo;
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

    public RoomListing getRoomListing() {
        return roomListing;
    }

    public void setRoom(RoomListing roomListing) {
        this.roomListing = roomListing;
    }

    public ReservationPaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(ReservationPaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HotelDetails getHotel() {
        return hotel;
    }

    public void setHotel(HotelDetails hotel) {
        this.hotel = hotel;
    }

    public void setRoomListing(RoomListing roomListing) {
        this.roomListing = roomListing;
    }

    public double getTotal() {
        return Double.parseDouble(df.format(total));
    }

    public void setTotal(double total) {
        this.total = Double.parseDouble(df.format(total));
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CustomerReservation && ((CustomerReservation) o).getId() == this.id);
    }

    @Override
    public int compareTo(CustomerReservation o) {
        return (int)this.id - (int)o.id;
    }

    public String datesToString() {
        return String.format("Check-In Date: %s%s Check-out Date: %s%s", checkInDate.toString(), System.lineSeparator(), checkOutDate.toString(), System.lineSeparator());
    }

    public String toString() {
        return String.format("Hotel: %s%sDescription:%s%s%sRoom:%s%s%sCheck-In Date: %s%sCheck-Out Date: %s%sTotal: %s%s",
                            hotel.getName(), System.lineSeparator(), System.lineSeparator(), hotel.descriptionToString(), System.lineSeparator(), System.lineSeparator(),
                            roomListing.toString(), System.lineSeparator(), checkInDate.toString(), System.lineSeparator(),
                            checkOutDate.toString(), System.lineSeparator(), total, System.lineSeparator());
    }
}
