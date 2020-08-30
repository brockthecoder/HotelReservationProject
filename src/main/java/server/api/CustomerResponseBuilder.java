package server.api;

import client.customer.model.HotelDetails;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.util.List;
import java.util.Map;

public interface CustomerResponseBuilder {

    String buildHotelResponse(HotelDetails hotel);

    String buildNewReservationResponse(long reservationId);

    String buildReservationSearchResponse(List<CustomerReservation> matchingReservations);

    String buildReservationModificationResponse(boolean wasSuccessful);

    String buildAvailabilityResponse(Map<HotelDetails, List<RoomListing>> availability);

    String buildExtraNightsResponse(double price);
}
