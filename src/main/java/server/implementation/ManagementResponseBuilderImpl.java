package server.implementation;

import client.management.model.AvailabilityListing;
import client.management.model.ManagementAccount;
import client.management.model.ManagementHotelDetails;
import client.management.model.RoomCategory;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.utilities.JSONBuilderUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.api.ManagementResponseBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings(value = "unchecked")
public class ManagementResponseBuilderImpl implements ManagementResponseBuilder {

    @Override
    public String buildSignInAttemptResponse(ManagementAccount account) {
        JSONObject jsonObject = new JSONObject();
        if (account == null || account.getId() == -1) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("id", account.getId());
        JSONArray hotelArray = new JSONArray();
        for (ManagementHotelDetails hotel : account.getHotels()) {
            hotelArray.add(JSONBuilderUtils.buildJSONManagementHotel(hotel));
        }
        jsonObject.put("hotels", hotelArray);
        jsonObject.put("status", "success");
        return jsonObject.toJSONString();
    }

    @Override
    public String buildNewAccountResponse(long accountId) {
        return getIdJSON(accountId);
    }

    private String getIdJSON(long id) {
        JSONObject jsonObject = new JSONObject();
        if (id == -1L) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("id", id);
        jsonObject.put("status", "success");
        return jsonObject.toJSONString();
    }

    @Override
    public String buildDeleteHotelResponse(boolean wasSuccess) {
        return getBooleanJSONResponse(wasSuccess);

    }

    @Override
    public String buildUpcomingCheckInsResponse(List<CustomerReservation> reservations) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reservations", JSONBuilderUtils.buildCustomerReservationJSONArray(reservations));
            jsonObject.put("status", "success");
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
    }

    @Override
    public String buildGenericAvailabilityResponse(List<RoomListing> availability) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (RoomListing roomListing : availability) {
                jsonArray.add(JSONBuilderUtils.buildRoomListingJSON(roomListing));
            }
            jsonObject.put("available_rooms", jsonArray);
            jsonObject.put("status", "success");
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
    }

    @Override
    public String buildNewReservationResponse(long reservationId) {
        return getIdJSON(reservationId);

    }

    @Override
    public String buildHotelDetailUpdateResponse(boolean wasSuccess) {
        return getBooleanJSONResponse(wasSuccess);
    }

    private String getBooleanJSONResponse(boolean wasSuccess) {
        JSONObject jsonObject = new JSONObject();
        if (wasSuccess) {
            jsonObject.put("status", "success");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", "failure");
        return jsonObject.toJSONString();
    }

    @Override
    public String buildRoomCategoryResponse(Set<RoomCategory> roomCategories) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "success");
            jsonObject.put("room_categories", JSONBuilderUtils.buildJSONRoomCategoriesArray(roomCategories));
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
    }

    @Override
    public String buildReservationSearchResponse(List<CustomerReservation> matchingReservations) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "success");
            jsonObject.put("reservations", JSONBuilderUtils.buildCustomerReservationJSONArray(matchingReservations));
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
    }

    @Override
    public String buildNewHotelResponse(long newHotelId) {
        return getIdJSON(newHotelId);
    }

    @Override
    public String buildCategorySpecificAvailabilityResponse(Map<LocalDate, AvailabilityListing> availability) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "success");
            JSONArray jsonArray = new JSONArray();
            for (LocalDate date : availability.keySet()) {
                JSONObject availabilityObject = new JSONObject();
                availabilityObject.put("date", date.toString());
                availabilityObject.put("num_of_rooms", availability.get(date).getNumOfRooms());
                availabilityObject.put("nightly_rate", availability.get(date).getNightlyRate());
                jsonArray.add(availabilityObject);
            }
            jsonObject.put("availability", jsonArray);
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
    }

    @Override
    public String buildAvailabilityUpdateResponse(boolean wasSuccess) {
        return getBooleanJSONResponse(wasSuccess);
    }
}
