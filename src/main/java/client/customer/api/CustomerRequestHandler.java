package client.customer.api;

import client.customer.model.HotelDetails;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.CustomerReservation;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.ReservationModificationType;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomerRequestHandler {

    HotelDetails getHotelDetailsById(long id);

    long newReservation(CustomerReservation reservation);

    double getPriceForExtraNights(List<LocalDate> dates, CustomerReservation reservation);

    List<CustomerReservation> getReservationById(CustomerReservationQuery id);

    List<CustomerReservation> getReservationByEmail(CustomerReservationQuery customerReservationQuery);

    boolean modifyReservation(CustomerReservation reservation, ReservationModificationType action);

    Map<HotelDetails, List<RoomListing>> getHotelAvailability(RoomAvailabilityQuery query);

    void closeConnection();

}
