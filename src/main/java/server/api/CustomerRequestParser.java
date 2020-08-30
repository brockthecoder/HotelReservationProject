package server.api;

import client.customer.model.RoomAvailabilityQuery;
import client.management.model.ManagementReservationQuery;
import client.mutual.model.CustomerReservation;
import mutual.model.CustomerReservationQuery;
import org.json.simple.JSONObject;
import server.model.ExtraNightsRequest;
import server.model.ReservationModificationRequest;

public interface CustomerRequestParser {

    long parseHotelRequest(JSONObject request);

    CustomerReservation parseNewReservationRequest(JSONObject request);

    CustomerReservationQuery parseReservationSearchByIdRequest(JSONObject request);

    CustomerReservationQuery parseReservationSearchByEmailRequest(JSONObject request);

    ReservationModificationRequest parseReservationModificationRequest(JSONObject request);

    RoomAvailabilityQuery parseRoomAvailabilityQueryRequest(JSONObject request);

    ExtraNightsRequest parseExtraNightsRequest(JSONObject request);
}
