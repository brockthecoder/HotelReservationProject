package client.customer.api;

import client.customer.model.HotelDetails;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.util.List;
import java.util.Map;

public interface ServerCustomerResponseJSONConverter {

    HotelDetails parseHotelDetails(String json);

    Map<HotelDetails, List<RoomListing>> parseAvailability(String json);

    long parseNewReservationId(String json);

    List<CustomerReservation> parseReservationDetails(String json);

    double parsePriceForExtraNights(String json);

    boolean parseReservationModificationResponse(String jsonResponse);
}
