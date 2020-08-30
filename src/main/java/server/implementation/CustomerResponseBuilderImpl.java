package server.implementation;

import client.customer.model.HotelDetails;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.utilities.JSONBuilderUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.api.CustomerResponseBuilder;

import java.util.List;
import java.util.Map;

@SuppressWarnings(value = "unchecked")
public class CustomerResponseBuilderImpl implements CustomerResponseBuilder {

    @Override
    public String buildHotelResponse(HotelDetails hotel) {
        JSONObject jsonObject = new JSONObject();
        if (hotel == null) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", "success");
        jsonObject.put("hotel", JSONBuilderUtils.buildJSONHotel(hotel));
        return jsonObject.toJSONString();
    }

    @Override
    public String buildNewReservationResponse(long reservationId) {
        JSONObject jsonObject = new JSONObject();
        if (reservationId  == -1) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", "success");
        jsonObject.put("reservation_id", reservationId);
        return jsonObject.toJSONString();

    }

    @Override
    public String buildReservationSearchResponse(List<CustomerReservation> matchingReservations) {
        JSONObject jsonObject = new JSONObject();
        if (matchingReservations == null) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", "success");
        jsonObject.put("reservations", JSONBuilderUtils.buildCustomerReservationJSONArray(matchingReservations));
        return jsonObject.toJSONString();
    }

    @Override
    public String buildReservationModificationResponse(boolean wasSuccessful) {
        JSONObject jsonObject = new JSONObject();
        if (wasSuccessful) {
            jsonObject.put("status", "success");
        }
        else {
            jsonObject.put("status", "failure");
        }
        return jsonObject.toJSONString();
    }

    @Override
    public String buildAvailabilityResponse(Map<HotelDetails, List<RoomListing>> availability) {
        JSONObject jsonObject = new JSONObject();
        if (availability == null) {
            jsonObject.put("status", "failure");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", "success");
        JSONArray hotelsArray = new JSONArray();
        for (HotelDetails hotelDetails : availability.keySet()) {
            JSONObject hotelAvailabilityObject = new JSONObject();
            hotelAvailabilityObject.put("hotel", JSONBuilderUtils.buildJSONHotel(hotelDetails));
            JSONArray roomListingArray = new JSONArray();
            for (RoomListing roomListing : availability.get(hotelDetails)) {
                roomListingArray.add(JSONBuilderUtils.buildRoomListingJSON(roomListing));
            }
            hotelAvailabilityObject.put("room_listings", roomListingArray);
            hotelsArray.add(hotelAvailabilityObject);
        }
        jsonObject.put("available_hotels", hotelsArray);
        return jsonObject.toJSONString();
    }

    @Override
    public String buildExtraNightsResponse(double price) {
        JSONObject jsonObject = new JSONObject();
        if (price == -1) {
            jsonObject.put("status", "failure");
        } else {
            jsonObject.put("status", "success");
            jsonObject.put("price_for_extra_nights", price);
        }
        return jsonObject.toJSONString();
    }
}
