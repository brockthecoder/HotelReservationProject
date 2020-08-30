package client.management.implementation;

import client.management.model.*;
import mutual.model.enums.CreditCardType;
import client.management.api.ServerManagementResponseJSONConverter;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import client.mutual.model.RoomListing;
import mutual.utilities.JSONParsingUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


public class ServerManagementResponseJSONConverterImpl implements ServerManagementResponseJSONConverter {

    JSONParser parser;

    public ServerManagementResponseJSONConverterImpl() {
        parser = new JSONParser();
    }

    @Override
    public long parseId(String json) {

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
            return (long) jsonObject.get("id");
        }
        catch (ParseException | ClassCastException e) {
            return -1;
        }
    }

    @Override
    public ManagementAccount parseManagementAccountSignIn(String json) {
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
            ManagementAccount account = new ManagementAccount();
            account.setHotels(new ArrayList<>());
            JSONArray hotelArray = (JSONArray) jsonObject.get("hotels");
            if (hotelArray != null) {
                for (Object hotelObject : hotelArray) {
                    account.getHotels().add(JSONParsingUtils.parseManagementHotelJSON(account, (JSONObject) hotelObject));
                }
            }
            account.setId((long) jsonObject.get("id"));
            account.setEmail((String) jsonObject.get("email"));
            account.setPassword((String) jsonObject.get("password"));
            return account;
        }
        catch (ParseException | ClassCastException e) {
            return null;
        }
    }

    @Override
    public boolean parseBoolean(String json) {

        if (json == null) {
            return false;
        }

        try {
            parser.reset();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String status = (String) jsonObject.get("status");
            return status.equals("success");
        }
        catch (ParseException | ClassCastException e) {
            return false;
        }
    }

    @Override
    public List<CustomerReservation> parseReservationList(String json, ManagementHotelDetails hotel) {

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
            if (reservationArray == null) {
                return null;
            }
            return parseJSONReservationArray(reservationArray, hotel);

        }
        catch (ParseException | DateTimeParseException |ClassCastException e) {
            return null;
        }
    }

    private List<CustomerReservation> parseJSONReservationArray(JSONArray reservationArray, ManagementHotelDetails hotel) {

        if (reservationArray == null) {
            return null;
        }
        List<CustomerReservation> reservations = new ArrayList<>();
        for (Object reservation : reservationArray) {
            JSONObject reservationObject = (JSONObject) reservation;
            CustomerReservation customerReservation = new CustomerReservation();
            customerReservation.setId((long) reservationObject.get("id"));
            customerReservation.setHotel(hotel);
            customerReservation.setCustomer(parseCustomerJSON((JSONObject) reservationObject.get("customer")));
            customerReservation.setCheckInDate(LocalDate.parse((String) reservationObject.get("check_in_date")));
            customerReservation.setCheckOutDate(LocalDate.parse((String) reservationObject.get("check_out_date")));
            customerReservation.setRoomListing(parseRoomListingJSON((JSONObject) reservationObject.get("room_listing")));
            customerReservation.setTotal((double) reservationObject.get("total"));
            customerReservation.setPaymentInfo(parsePaymentInfoJSON((JSONObject) reservationObject.get("payment_info")));
            reservations.add(customerReservation);
        }
        return reservations;
    }

    private ReservationPaymentInfo parsePaymentInfoJSON(JSONObject paymentInfoObject) {

        if (paymentInfoObject == null) {
            return null;
        }
        ReservationPaymentInfo paymentInfo = new ReservationPaymentInfo();
        paymentInfo.setCardNumber((String) paymentInfoObject.get("card_number"));
        paymentInfo.setCvv((String) paymentInfoObject.get("cvv"));
        paymentInfo.setZipCode((String) paymentInfoObject.get("zip_code"));
        paymentInfo.setCardHolderName((String) paymentInfoObject.get("cardholder_name"));
        paymentInfo.setExpirationDate((String) paymentInfoObject.get("expiration_date"));
        paymentInfo.setCreditCardType(CreditCardType.valueOf((String) paymentInfoObject.get("card_type")));
        return paymentInfo;
    }

    private RoomListing parseRoomListingJSON(JSONObject roomListingObject) {

        if (roomListingObject == null) {
            return null;
        }
        RoomListing roomListing = new RoomListing();
        roomListing.setRoomCategoryId((long) roomListingObject.get("room_category_id"));
        roomListing.setRoomName((String) roomListingObject.get("room_name"));
        roomListing.setDescription((String) roomListingObject.get("room_description"));
        roomListing.setMaxOccupants((long) roomListingObject.get("max_occupants"));
        roomListing.setNightlyRate((double) roomListingObject.get("nightly_rate"));
        return roomListing;
    }

    private Customer parseCustomerJSON(JSONObject customerObject) {
        if (customerObject == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setFirstName((String) customerObject.get("first_name"));
        customer.setLastName((String) customerObject.get("last_name"));
        customer.setPhoneNumber((String) customerObject.get("phone_number"));
        customer.setEmail((String) customerObject.get("email"));
        customer.setStreetAddress((String) customerObject.get("street_address"));
        customer.setState((String) customerObject.get("state"));
        customer.setCity((String) customerObject.get("city"));
        return customer;
    }

    @Override
    public List<RoomListing> parseGenericAvailabilityList(String json) {

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
            JSONArray roomListingsArray = (JSONArray) jsonObject.get("available_rooms");
            if (roomListingsArray == null) {
                return null;
            }

            List<RoomListing> availability = new ArrayList<>();
            for (Object roomListing : roomListingsArray) {
                availability.add(parseRoomListingJSON((JSONObject) roomListing));
            }
            return availability;
        }
        catch (ParseException | ClassCastException e) {
            return null;
        }
    }

    @Override
    public Set<RoomCategory> parseRoomCategories(String json) {

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
            JSONArray roomCategoryArray = (JSONArray) jsonObject.get("room_categories");
            if (roomCategoryArray == null) {
                return null;
            }

            Set<RoomCategory> roomCategories = new HashSet<>();
            for (Object roomCategory : roomCategoryArray) {
                roomCategories.add(parseRoomCategoryJSON((JSONObject) roomCategory));
            }
            return roomCategories;
        }
        catch (ParseException | ClassCastException e) {
            return null;
        }
    }

    private RoomCategory parseRoomCategoryJSON(JSONObject roomCategoryObject) {

        if (roomCategoryObject == null) {
            return null;
        }
        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setId((long) roomCategoryObject.get("id"));
        roomCategory.setName((String) roomCategoryObject.get("name"));
        roomCategory.setDescription((String) roomCategoryObject.get("description"));
        roomCategory.setMaxOccupants((long) roomCategoryObject.get("max_occupants"));
        return roomCategory;
    }

    @Override
    public Map<LocalDate, AvailabilityListing> parseCategoryAvailability(String json) {

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
            JSONArray availabilityArray = (JSONArray) jsonObject.get("availability");
            return parseJSONAvailabilityArray(availabilityArray);
        }
        catch (ParseException | DateTimeParseException |ClassCastException e) {
            return null;
        }
    }

    private Map<LocalDate, AvailabilityListing> parseJSONAvailabilityArray(JSONArray availabilityArray) {

        if (availabilityArray == null) {
            return null;
        }
        Map<LocalDate, AvailabilityListing> availabilityMap = new TreeMap<>();

        for (Object availabilityListing : availabilityArray) {

            JSONObject availabilityObject = (JSONObject) availabilityListing;
            LocalDate date = LocalDate.parse((String) availabilityObject.get("date"));
            AvailabilityListing listing = new AvailabilityListing();
            listing.setNumOfRooms((long) availabilityObject.get("num_of_rooms"));
            listing.setNightlyRate((double) availabilityObject.get("nightly_rate"));
            availabilityMap.put(date, listing);
        }

        return availabilityMap;

    }
}
