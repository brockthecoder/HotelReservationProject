package mutual.utilities;

import client.customer.model.HotelDetails;
import client.management.model.*;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import client.mutual.model.RoomListing;
import mutual.model.enums.CreditCardType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

public class JSONParsingUtils {

    public static RoomCategory getRoomCategoryFromJSON(JSONObject roomCategoryJSON) {
        try {
            RoomCategory roomCategory = new RoomCategory();
            roomCategory.setId(getLongFromJSON("id", roomCategoryJSON));
            roomCategory.setMaxOccupants(getLongFromJSON("max_occupants", roomCategoryJSON));
            roomCategory.setName((String) roomCategoryJSON.get("name"));
            roomCategory.setDescription((String) roomCategoryJSON.get("description"));
            return roomCategory;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + roomCategoryJSON);
            return null;
        }
    }

    public static Set<AmenityType> getAmenitiesFromJSON(JSONArray amenityArray) {
        if (amenityArray == null) {
            return new HashSet<>(0);
        }
        try {
            Set<AmenityType> amenities = new HashSet<>();
            for (Object amenity : amenityArray) {
                amenities.add(AmenityType.valueOf((String) amenity));
            }
            return amenities;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the JSON Array");
            System.out.println("JSON Array: " + amenityArray);
            return null;
        }
    }

    public static ManagementHotelDetails parseManagementHotelJSON(ManagementAccount owner, JSONObject hotelJSON) {
        try {
            ManagementHotelDetails hotel = new ManagementHotelDetails();
            hotel.setId(getLongFromJSON("id", hotelJSON));
            hotel.setName((String) hotelJSON.get("name"));
            hotel.setDescription((String) hotelJSON.get("description"));
            hotel.setPhoneNumber((String) hotelJSON.get("phone_number"));
            hotel.setStreetAddress((String) hotelJSON.get("street_address"));
            hotel.setState((String) hotelJSON.get("state"));
            hotel.setCity((String) hotelJSON.get("city"));
            hotel.setCheckOutTime(LocalTime.parse((String) hotelJSON.get("check_out_time")));
            hotel.setCheckInTime(LocalTime.parse((String) hotelJSON.get("check_in_time")));
            hotel.setCheckInAge((long) hotelJSON.get("check_in_age"));
            hotel.setNumOfFloors((long) hotelJSON.get("num_of_floors"));
            hotel.setHotelOwner(owner);
            if (hotelJSON.get("amenities") != null) {
                hotel.setAmenities(getAmenitiesFromJSON((JSONArray) hotelJSON.get("amenities")));
            }
            if (hotelJSON.get("operating_hours") != null) {
                hotel.setOperatingHours(getOperatingHoursFromJSON("operating_hours", hotelJSON));
            }
            if (hotelJSON.get("room_categories") != null) {
                Set<RoomCategory> roomCategories = new HashSet<>();
                JSONArray roomCategoryArray = (JSONArray) hotelJSON.get("room_categories");
                for (Object roomCategoryObject : roomCategoryArray) {
                    roomCategories.add(getRoomCategoryFromJSON((JSONObject) roomCategoryObject));
                }
                hotel.setRoomCategories(roomCategories);
            }
            return hotel;
        }
        catch (ClassCastException | NullPointerException | DateTimeParseException e) {
            e.printStackTrace();
            System.out.println("An error occurred while parsing management hotel details");
            return null;
        }
    }

    public static LocalTime getTimeFromJSON(String key, JSONObject jsonObject) {
        try {
            return LocalTime.parse((String) jsonObject.get(key));
        }
        catch (ClassCastException | DateTimeParseException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static Customer getCustomerFromJSON(String key, JSONObject jsonObject) {
        try {
            Customer customer = new Customer();
            JSONObject customerJSON = (JSONObject) jsonObject.get(key);
            customer.setFirstName((String) customerJSON.get("first_name"));
            customer.setLastName((String) customerJSON.get("last_name"));
            customer.setPhoneNumber((String) customerJSON.get("phone_number"));
            customer.setEmail((String) customerJSON.get("email"));
            customer.setStreetAddress((String) customerJSON.get("street_address"));
            customer.setState((String) customerJSON.get("state"));
            customer.setCity((String) customerJSON.get("city"));
            return customer;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static double getDoubleFromJSON(String key, JSONObject jsonObject) {
        try {
            return (double) jsonObject.get(key);
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return -1.0;
        }
    }

    public static ReservationPaymentInfo getPaymentInfoFromJSON(String key, JSONObject jsonObject) {
        try {
            ReservationPaymentInfo paymentInfo = new ReservationPaymentInfo();
            JSONObject paymentInfoJSON = (JSONObject) jsonObject.get(key);
            paymentInfo.setCardNumber((String) paymentInfoJSON.get("card_number"));
            paymentInfo.setCardHolderName((String) paymentInfoJSON.get("cardholder_name"));
            paymentInfo.setCreditCardType(CreditCardType.valueOf((String) paymentInfoJSON.get("card_type")));
            paymentInfo.setCvv((String) paymentInfoJSON.get("cvv"));
            paymentInfo.setExpirationDate((String) paymentInfoJSON.get("expiration_date"));
            paymentInfo.setZipCode((String) paymentInfoJSON.get("zip_code"));
            return paymentInfo;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static LocalDate getDateFromJSON(String key, JSONObject jsonObject) {
        try {
            return LocalDate.parse((String) jsonObject.get(key));
        }
        catch (ClassCastException | DateTimeParseException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static long getLongFromJSON(String key, JSONObject jsonObject) {
        try {
            return (long) jsonObject.get(key);
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return -1;
        }
    }

    public static Set<OperatingHours> getOperatingHoursFromJSON(String key, JSONObject jsonObject) {
        try {
            Set<OperatingHours> operatingHoursSet = new HashSet<>();
            JSONArray operatingHoursArray = (JSONArray) jsonObject.get(key);
            for (Object operatingHoursObject : operatingHoursArray) {
                JSONObject operatingHoursJSON = (JSONObject) operatingHoursObject;
                OperatingHours operatingHours = new OperatingHours();
                operatingHours.setOpeningTime(getTimeFromJSON("opening_time", operatingHoursJSON));
                operatingHours.setClosingTime(getTimeFromJSON("closing_time", operatingHoursJSON));
                operatingHours.setDayOfWeek(DayOfWeek.valueOf((String) operatingHoursJSON.get("day_of_week")));
                operatingHoursSet.add(operatingHours);
            }
            return operatingHoursSet;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the jsonObject");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static CustomerReservation getCustomerReservationFromJSON(JSONObject jsonObject) {
        try {
            CustomerReservation reservation = new CustomerReservation();
            reservation.setId(getLongFromJSON("id", jsonObject));
            reservation.setHotel(parseHotelJSON("hotel", jsonObject));
            reservation.setCustomer(JSONParsingUtils.getCustomerFromJSON("customer", jsonObject));
            reservation.setCheckInDate(JSONParsingUtils.getDateFromJSON("check_in_date", jsonObject));
            reservation.setCheckOutDate(JSONParsingUtils.getDateFromJSON("check_out_date", jsonObject));
            reservation.setTotal(JSONParsingUtils.getDoubleFromJSON("total", jsonObject));
            reservation.setRoomListing(JSONParsingUtils.getRoomListingFromJSON("room_listing", jsonObject));
            reservation.setPaymentInfo(JSONParsingUtils.getPaymentInfoFromJSON("payment_info", jsonObject));
            return reservation;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    private static HotelDetails parseHotelJSON(String key, JSONObject jsonObject) {
        try {
            JSONObject hotelObject = (JSONObject) jsonObject.get(key);
            HotelDetails hotelDetails = new HotelDetails();
            hotelDetails.setId(getLongFromJSON("id", hotelObject));
            hotelDetails.setName((String) hotelObject.get("name"));
            hotelDetails.setDescription((String) hotelObject.get("description"));
            hotelDetails.setPhoneNumber((String) hotelObject.get("phone_number"));
            hotelDetails.setStreetAddress((String) hotelObject.get("street_address"));
            hotelDetails.setState((String) hotelObject.get("state"));
            hotelDetails.setCity((String) hotelObject.get("city"));
            hotelDetails.setCheckInAge(getLongFromJSON("check_in_age", hotelObject));
            hotelDetails.setNumOfFloors(getLongFromJSON("num_of_floors", hotelObject));
            hotelDetails.setCheckInTime(getTimeFromJSON("check_in_time", hotelObject));
            hotelDetails.setCheckOutTime(getTimeFromJSON("check_out_time", hotelObject));
            hotelDetails.setAmenities(getAmenitiesFromJSON((JSONArray) hotelObject.get("amenities")));
            hotelDetails.setOperatingHours(getOperatingHoursFromJSON("operating_hours", hotelObject));
            return hotelDetails;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    private static RoomListing getRoomListingFromJSON(String key, JSONObject jsonObject) {
        try {
            JSONObject roomListingObject = (JSONObject) jsonObject.get(key);
            RoomListing roomListing = new RoomListing();
            roomListing.setRoomCategoryId(getLongFromJSON("room_category_id", roomListingObject));
            roomListing.setNightlyRate(getDoubleFromJSON("nightly_rate", roomListingObject));
            roomListing.setMaxOccupants(getLongFromJSON("max_occupants", roomListingObject));
            roomListing.setRoomName((String) roomListingObject.get("room_name"));
            roomListing.setDescription((String) roomListingObject.get("room_description"));
            return roomListing;
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + jsonObject);
            return null;
        }
    }

    public static CustomerReservation getCustomerReservationFromJSON(String key, JSONObject request) {
        try {
            JSONObject jsonObject = (JSONObject) request.get(key);
            return getCustomerReservationFromJSON(jsonObject);
        }
        catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }
}
