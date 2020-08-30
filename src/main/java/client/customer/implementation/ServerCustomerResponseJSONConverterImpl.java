package client.customer.implementation;

import client.customer.api.ServerCustomerResponseJSONConverter;
import client.customer.model.HotelDetails;
import client.management.model.AmenityType;
import client.management.model.OperatingHours;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.utilities.JSONParsingUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class ServerCustomerResponseJSONConverterImpl implements ServerCustomerResponseJSONConverter {

    JSONParser parser;

    public ServerCustomerResponseJSONConverterImpl() {
        parser = new JSONParser();
    }

    @Override
    public HotelDetails parseHotelDetails(String json) {

        if (json == null) {
            return null;
        }
        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            if (status.equals("failure")) {
                return null;
            }
            return getHotelDetailsFromJson((JSONObject) jsonObject.get("hotel"));
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private HotelDetails getHotelDetailsFromJson(JSONObject jsonObject) {

        if (jsonObject == null) {
            return null;
        }

        HotelDetails hotelDetails = new HotelDetails();
        hotelDetails.setName((String) jsonObject.get("name"));
        hotelDetails.setPhoneNumber((String) jsonObject.get("phone_number"));
        hotelDetails.setDescription((String) jsonObject.get("description"));
        hotelDetails.setStreetAddress((String) jsonObject.get("street_address"));
        hotelDetails.setState((String) jsonObject.get("state"));
        hotelDetails.setCity((String) jsonObject.get("city"));
        hotelDetails.setCheckInAge((long) jsonObject.get("check_in_age"));
        hotelDetails.setCheckInTime(LocalTime.parse((String) jsonObject.get("check_in_time")));
        hotelDetails.setCheckOutTime(LocalTime.parse((String) jsonObject.get("check_out_time")));
        hotelDetails.setId((long) jsonObject.get("id"));
        JSONArray amenitiesArray = (JSONArray) jsonObject.get("amenities");
        hotelDetails.setAmenities(getAmenitiesFromJSON(amenitiesArray));
        JSONArray operatingHoursArray = (JSONArray) jsonObject.get("operating_hours");
        hotelDetails.setOperatingHours(getOperatingHoursFromJSON(operatingHoursArray));
        hotelDetails.setNumOfFloors((long) jsonObject.get("num_of_floors"));
        return hotelDetails;
    }

    private Set<OperatingHours> getOperatingHoursFromJSON(JSONArray operatingHoursArray) {

        if (operatingHoursArray == null) {
            return null;
        }

        Set<OperatingHours> operatingHours = new TreeSet<>();
        for (Object hours : operatingHoursArray) {
            JSONObject hoursMap = (JSONObject) hours;
            OperatingHours hoursObject = new OperatingHours();
            hoursObject.setOpeningTime(LocalTime.parse((String) hoursMap.get("opening_time")));
            hoursObject.setClosingTime(LocalTime.parse((String) hoursMap.get("closing_time")));
            hoursObject.setDayOfWeek(DayOfWeek.valueOf((String) hoursMap.get("day_of_week")));
            operatingHours.add(hoursObject);
        }
        return operatingHours;
    }

    private Set<AmenityType> getAmenitiesFromJSON(JSONArray amenitiesArray) {

        Set<AmenityType> amenities = new HashSet<>();

        if (amenitiesArray == null) {
            return null;
        }

        for (Object amenity : amenitiesArray) {
            amenities.add(AmenityType.valueOf((String) amenity));
        }
        return amenities;
    }

    @Override
    public Map<HotelDetails, List<RoomListing>> parseAvailability(String json) {

        if (json == null) {
            return null;
        }
        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            if (status.equals("failure")) {
                return null;
            }
            JSONArray hotelAvailabilityArray = (JSONArray) jsonObject.get("available_hotels");
            Map<HotelDetails, List<RoomListing>> availabilityMap = new HashMap<>();
            for (Object hotel : hotelAvailabilityArray) {
                HotelDetails hotelDetails = getHotelDetailsFromJson((JSONObject) ((JSONObject) hotel).get("hotel"));
                List<RoomListing> roomListings = getRoomListingsFromJson((JSONArray) ((JSONObject) hotel).get("room_listings"));
                availabilityMap.put(hotelDetails, roomListings);
            }
            return availabilityMap;
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<RoomListing> getRoomListingsFromJson(JSONArray roomListingsArray) {
        if (roomListingsArray == null) {
            return null;
        }
        List<RoomListing> roomListings = new ArrayList<>();

        for (Object roomListing : roomListingsArray) {
            roomListings.add(parseRoomListing((JSONObject) roomListing));
        }
        return roomListings;
    }

    private RoomListing parseRoomListing(JSONObject roomListingJSON) {

        if (roomListingJSON == null) {
            return null;
        }
        RoomListing roomListing = new RoomListing();
        roomListing.setRoomCategoryId((long) roomListingJSON.get("room_category_id"));
        roomListing.setRoomName((String) roomListingJSON.get("room_name"));
        roomListing.setDescription((String) roomListingJSON.get("room_description"));
        roomListing.setMaxOccupants((long) roomListingJSON.get("max_occupants"));
        roomListing.setNightlyRate((double) roomListingJSON.get("nightly_rate"));
        return roomListing;
    }

    @Override
    public long parseNewReservationId(String json) {
        if (json == null) {
            return -1;
        }

        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            if (status.equals("failure")) {
                return -1;
            }
            return (long) jsonObject.get("reservation_id");
        }
        catch (ClassCastException | ParseException e) {
            return -1;
        }
    }

    @Override
    public List<CustomerReservation> parseReservationDetails(String json) {

        if (json == null) {
            return null;
        }

        try {

            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            if (status.equals("failure")) {
                return null;
            }
            JSONArray reservationArray = (JSONArray) jsonObject.get("reservations");
            List<CustomerReservation> reservations = new ArrayList<>();
            for (Object reservation : reservationArray) {
                reservations.add(JSONParsingUtils.getCustomerReservationFromJSON((JSONObject) reservation));
            }
            return reservations;
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public double parsePriceForExtraNights(String json) {

        if (json == null) {
            return -1;
        }

        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            if (status.equals("failure")) {
                return -1;
            }
            return (double) jsonObject.get("price_for_extra_nights");
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean parseReservationModificationResponse(String jsonResponse) {
        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            String status = (String) jsonObject.get("status");
            return status.equals("success");
        }
        catch (ClassCastException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
