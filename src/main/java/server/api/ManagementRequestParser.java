package server.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.CategorySpecificAvailabilityQuery;
import org.json.simple.JSONObject;

public interface ManagementRequestParser {

    ManagementAccount parseSignInAttempt(JSONObject request);

    ManagementAccount parseNewAccountRequest(JSONObject request);

    long parseHotelDeletionRequest(JSONObject request);

    long parseUpcomingCheckInsRequest(JSONObject request);

    ManagementRoomQuery parseGenericAvailabilityRequest(JSONObject request);

    CustomerReservation parseNewReservationRequest(JSONObject request);

    HotelDetailChange parseHotelDetailUpdateRequest(JSONObject request);

    long parseRoomCategoryRequest(JSONObject request);

    ManagementReservationQuery parseReservationSearchRequest(JSONObject request);

    ManagementHotelDetails parseNewHotelRequest(JSONObject request);

    CategorySpecificAvailabilityQuery parseCategorySpecificAvailabilityRequest(JSONObject request);

    AvailabilityUpdateRequest parseAvailabilityUpdateRequest(JSONObject request);

    long parseOperatingHoursRequest(JSONObject request);

    long parseAmenityRequest(JSONObject request);

}
