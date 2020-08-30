package mutual.utilities;

import client.customer.model.HotelDetails;
import client.management.model.AmenityType;
import client.management.model.ManagementHotelDetails;
import client.management.model.OperatingHours;
import client.management.model.RoomCategory;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import client.mutual.model.RoomListing;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Set;

@SuppressWarnings(value = "unchecked")
public class JSONBuilderUtils {

    public static JSONObject buildJSONHotel(HotelDetails hotel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", hotel.getId());
        jsonObject.put("name", hotel.getName());
        jsonObject.put("description", hotel.getDescription());
        jsonObject.put("phone_number", hotel.getPhoneNumber());
        jsonObject.put("street_address", hotel.getStreetAddress());
        jsonObject.put("state", hotel.getState());
        jsonObject.put("city", hotel.getCity());
        jsonObject.put("check_in_time", hotel.getCheckInTime().toString());
        jsonObject.put("check_out_time", hotel.getCheckOutTime().toString());
        jsonObject.put("num_of_floors", hotel.getNumOfFloors());
        jsonObject.put("check_in_age", hotel.getCheckInAge());
        jsonObject.put("amenities", buildJSONAmenities(hotel.getAmenities()));
        jsonObject.put("operating_hours", buildJSONOperatingHours(hotel.getOperatingHours()));
        return jsonObject;
    }
    public static JSONObject buildJSONManagementHotel(ManagementHotelDetails hotel) {
        JSONObject jsonObject = buildJSONHotel(hotel);
        jsonObject.put("room_categories", buildJSONRoomCategoriesArray(hotel.getRoomCategories()));
        return jsonObject;
    }

    public static JSONArray buildJSONAmenities(Set<AmenityType> amenities) {
        if (amenities == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (AmenityType amenityType : amenities) {
            jsonArray.add(amenityType.name());
        }
        return jsonArray;
    }

    public static JSONArray buildJSONOperatingHours(Set<OperatingHours> operatingHours) {
        if (operatingHours == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (OperatingHours hours : operatingHours) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opening_time", hours.getOpeningTime().toString());
            jsonObject.put("closing_time", hours.getClosingTime().toString());
            jsonObject.put("day_of_week", hours.getDayOfWeek().name());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public static JSONArray buildJSONRoomCategoriesArray(Set<RoomCategory> roomCategories) {
        if (roomCategories == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (RoomCategory roomCategory : roomCategories) {
            jsonArray.add(buildJSONRoomCategory(roomCategory));
        }
        return jsonArray;
    }

    public static JSONObject buildJSONRoomCategory(RoomCategory roomCategory) {
        if (roomCategory == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", roomCategory.getId());
        jsonObject.put("name", roomCategory.getName());
        jsonObject.put("description", roomCategory.getDescription());
        jsonObject.put("max_occupants", roomCategory.getMaxOccupants());
        return jsonObject;
    }

    public static JSONArray buildCustomerReservationJSONArray(List<CustomerReservation> reservations) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (CustomerReservation reservation : reservations) {
                jsonArray.add(buildJSONCustomerReservation(reservation));
            }
            return jsonArray;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public static JSONObject buildJSONCustomerReservation(CustomerReservation reservation) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (reservation.getHotel() != null) {
                jsonObject.put("hotel", buildJSONHotel(reservation.getHotel()));
            }
            jsonObject.put("id", reservation.getId());
            jsonObject.put("customer", buildJSONCustomer(reservation.getCustomer()));
            jsonObject.put("check_in_date", reservation.getCheckInDate().toString());
            jsonObject.put("check_out_date", reservation.getCheckOutDate().toString());
            jsonObject.put("payment_info", buildPaymentInfoJSON(reservation.getPaymentInfo()));
            jsonObject.put("total", reservation.getTotal());
            jsonObject.put("room_listing", buildRoomListingJSON(reservation.getRoomListing()));
            return jsonObject;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public static JSONObject buildJSONCustomer(Customer customer) {
       try {
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("first_name", customer.getFirstName());
           jsonObject.put("last_name", customer.getLastName());
           jsonObject.put("phone_number", customer.getPhoneNumber());
           jsonObject.put("email", customer.getEmail());
           jsonObject.put("street_address", customer.getStreetAddress());
           jsonObject.put("state", customer.getState());
           jsonObject.put("city", customer.getCity());
           return jsonObject;
       }
       catch (NullPointerException e) {
           return null;
       }
    }

    public static JSONObject buildPaymentInfoJSON(ReservationPaymentInfo paymentInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("card_number", paymentInfo.getCardNumber());
            jsonObject.put("cardholder_name", paymentInfo.getCardHolderName());
            jsonObject.put("cvv", paymentInfo.getCvv());
            jsonObject.put("zip_code", paymentInfo.getZipCode());
            jsonObject.put("expiration_date", paymentInfo.getExpirationDate());
            jsonObject.put("card_type", paymentInfo.getCreditCardType().name());
            return jsonObject;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public static JSONObject buildRoomListingJSON(RoomListing roomListing) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nightly_rate", roomListing.getNightlyRate());
            jsonObject.put("room_category_id", roomListing.getRoomCategoryId());
            jsonObject.put("room_name", roomListing.getRoomName());
            jsonObject.put("room_description", roomListing.getDescription());
            jsonObject.put("max_occupants", roomListing.getMaxOccupants());
            return jsonObject;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

}
