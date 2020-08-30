package client.mutual.model.builders;

import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.customer.model.HotelDetails;
import client.mutual.model.RoomListing;
import mutual.model.enums.CreditCardType;

import java.time.LocalDate;

public class ReservationBuilder {

    private CustomerReservation reservation;

    public ReservationBuilder() {
        reservation = new CustomerReservation();
    }

    public ReservationBuilder at(HotelDetails hotel) {
        reservation.setHotel(hotel);
        return this;
    }

    public ReservationBuilder withTotal(double total) {

        reservation.setTotal(total);
        return this;
    }

    public ReservationBuilder byCustomer(Customer customer) {

        reservation.setCustomer(customer);
        return this;
    }

    public ReservationBuilder withCheckInDate(LocalDate checkInDate) {

        reservation.setCheckInDate(checkInDate);
        return this;
    }

    public ReservationBuilder withCheckOutDate(LocalDate checkOutDate) {

        reservation.setCheckOutDate(checkOutDate);
        return this;
    }

    public ReservationBuilder inRoomCategory(RoomListing roomListing) {

        reservation.setRoom(roomListing);
        return this;
    }

    public ReservationBuilder withCreditCard(String cardNumber) {

        reservation.getPaymentInfo().setCardNumber(cardNumber);
        return this;
    }

    public ReservationBuilder withCreditCardType(CreditCardType cardType) {

        reservation.getPaymentInfo().setCreditCardType(cardType);
        return this;
    }

    public ReservationBuilder withCVV(String cvv) {

        reservation.getPaymentInfo().setCvv(cvv);
        return this;
    }

    public ReservationBuilder withCreditCardZipCode(String zipCode) {

        reservation.getPaymentInfo().setZipCode(zipCode);
        return this;
    }

    public ReservationBuilder withCreditCardHolderNameOf(String cardHolderName) {

        reservation.getPaymentInfo().setCardHolderName(cardHolderName);
        return this;
    }

    public ReservationBuilder withCreditCardExpirationDateOf(String expirationDate) {

        reservation.getPaymentInfo().setExpirationDate(expirationDate);
        return this;
    }

    public CustomerReservation build() {
        return reservation;
    }

}

