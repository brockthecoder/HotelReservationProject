package server.api;

import client.customer.model.HotelDetails;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.CustomerReservationQuery;
import server.model.ExtraNightsRequest;
import server.model.ReservationModificationRequest;

import java.util.List;
import java.util.Map;

public interface CustomerDAO {

    HotelDetails getHotel(long hotelId);

    long newReservation(CustomerReservation reservation);

    List<CustomerReservation> getReservationsById(CustomerReservationQuery query);

    List<CustomerReservation> getReservationsByEmail(CustomerReservationQuery query);

    boolean modifyReservation(ReservationModificationRequest modificationRequest);

    Map<HotelDetails, List<RoomListing>> getRoomAvailability(RoomAvailabilityQuery query);

    double getPriceForExtraNights(ExtraNightsRequest extraNightsRequest);
}
