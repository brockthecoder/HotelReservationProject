package server.implementation;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.CategorySpecificAvailabilityQuery;
import mutual.model.enums.HotelPropertyModificationType;
import mutual.model.enums.ReservationIdentifierType;
import mutual.model.enums.UpdateAction;
import mutual.utilities.JSONParsingUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.api.ManagementRequestParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagementRequestParserImpl implements ManagementRequestParser {

    @Override
    public ManagementAccount parseSignInAttempt(JSONObject request) {
        return getEmailAndPasswordFromRequest(request);
    }

    @Override
    public ManagementAccount parseNewAccountRequest(JSONObject request) {
        return getEmailAndPasswordFromRequest(request);
    }

    ManagementAccount getEmailAndPasswordFromRequest(JSONObject request) {
        try {
            ManagementAccount account = new ManagementAccount();
            account.setEmail((String) request.get("email"));
            account.setPassword((String) request.get("password"));
            return account;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public long parseHotelDeletionRequest(JSONObject request) {
        return JSONParsingUtils.getLongFromJSON("hotel_id",request);
    }

    @Override
    public long parseUpcomingCheckInsRequest(JSONObject request) {
        return JSONParsingUtils.getLongFromJSON("hotel_id",request);
    }

    @Override
    public ManagementRoomQuery parseGenericAvailabilityRequest(JSONObject request) {
        try {
            ManagementRoomQuery query = new ManagementRoomQuery();
            query.setHotel(new ManagementHotelDetails());
            query.getHotel().setId(JSONParsingUtils.getLongFromJSON("hotel_id", request));
            query.setCheckInDate(JSONParsingUtils.getDateFromJSON("check_in_date", request));
            query.setCheckOutDate(JSONParsingUtils.getDateFromJSON("check_out_date", request));
            query.setNumOfPeople(JSONParsingUtils.getLongFromJSON("num_of_people", request));
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public CustomerReservation parseNewReservationRequest(JSONObject request) {
        try {
            return JSONParsingUtils.getCustomerReservationFromJSON("reservation", request);
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public HotelDetailChange parseHotelDetailUpdateRequest(JSONObject request) {
        try {
            HotelDetailChange detailChange = new HotelDetailChange();
            detailChange.setHotelId(JSONParsingUtils.getLongFromJSON("hotel_id", request));
            detailChange.setPropertyModificationType(HotelPropertyModificationType.valueOf((String) request.get("modification_type")));
            switch (detailChange.getPropertyModificationType()) {
                case UPDATE_NAME:
                    detailChange.setNewValue(request.get("new_name"));
                    break;
                case UPDATE_DESCRIPTION:
                    detailChange.setNewValue(request.get("new_description"));
                    break;
                case UPDATE_PHONE_NUMBER:
                    detailChange.setNewValue(request.get("new_phone_number"));
                    break;
                case UPDATE_CHECK_IN_AGE:
                    detailChange.setNewValue(request.get("new_check_in_age"));
                    break;
                case UPDATE_CHECK_OUT_TIME:
                    detailChange.setNewValue(JSONParsingUtils.getTimeFromJSON("new_check_out_time", request));
                    break;
                case UPDATE_CHECK_IN_TIME:
                    detailChange.setNewValue(JSONParsingUtils.getTimeFromJSON("new_check_in_time", request));
                    break;
                case UPDATE_AMENITIES:
                    detailChange.setNewValue(JSONParsingUtils.getAmenitiesFromJSON((JSONArray) request.get("updated_amenities")));
                    break;
                case UPDATE_OPERATING_HOURS:
                    detailChange.setNewValue(JSONParsingUtils.getOperatingHoursFromJSON("updated_operating_hours", request));
                    break;
                case REMOVE_ROOM_CATEGORY:
                    detailChange.setNewValue(request.get("room_category_id"));
                    break;
                case ADD_ROOM_CATEGORY:
                    detailChange.setNewValue(JSONParsingUtils.getRoomCategoryFromJSON((JSONObject) request.get("new_room_category")));
                    break;
                case UPDATE_ROOM_CATEGORY:
                    detailChange.setNewValue(JSONParsingUtils.getRoomCategoryFromJSON((JSONObject) request.get("updated_room_category")));
                    break;
            }
            return detailChange;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }

    }

    @Override
    public long parseRoomCategoryRequest(JSONObject request) {
        return JSONParsingUtils.getLongFromJSON("hotel_id", request);
    }

    @Override
    public ManagementReservationQuery parseReservationSearchRequest(JSONObject request) {
        try {
            ManagementReservationQuery query = new ManagementReservationQuery();
            query.setHotelId(JSONParsingUtils.getLongFromJSON("hotel_id", request));
            query.setIdentifierType(ReservationIdentifierType.valueOf((String) request.get("identifier_type")));
            switch (query.getIdentifierType()) {
                case RESERVATION_NUMBER:
                    query.setIdentifier(String.valueOf(JSONParsingUtils.getLongFromJSON("reservation_id", request)));
                    break;
                case EMAIL:
                    query.setIdentifier((String) request.get("customer_email"));
                    break;
                case FULL_NAME:
                    query.setIdentifier((String) request.get("customer_full_name"));
                    break;
            }
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public ManagementHotelDetails parseNewHotelRequest(JSONObject request) {
        try {
            ManagementHotelDetails hotel = new ManagementHotelDetails();
            hotel.setHotelOwner(new ManagementAccount());
            hotel.getHotelOwner().setId(JSONParsingUtils.getLongFromJSON("owner_account_id", request));
            JSONObject hotelJSON = (JSONObject) request.get("hotel");
            hotel.setName((String) hotelJSON.get("name"));
            hotel.setDescription((String) hotelJSON.get("description"));
            hotel.setPhoneNumber((String) hotelJSON.get("phone_number"));
            hotel.setStreetAddress((String) hotelJSON.get("street_address"));
            hotel.setState((String) hotelJSON.get("state"));
            hotel.setCity((String) hotelJSON.get("city"));
            hotel.setCheckInAge(JSONParsingUtils.getLongFromJSON("check_in_age", hotelJSON));
            hotel.setNumOfFloors(JSONParsingUtils.getLongFromJSON("num_of_floors", hotelJSON));
            hotel.setAmenities(JSONParsingUtils.getAmenitiesFromJSON((JSONArray) hotelJSON.get("amenities")));
            hotel.setOperatingHours(JSONParsingUtils.getOperatingHoursFromJSON("operating_hours", hotelJSON));
            hotel.setCheckInTime(JSONParsingUtils.getTimeFromJSON("check_in_time", hotelJSON));
            hotel.setCheckOutTime(JSONParsingUtils.getTimeFromJSON("check_out_time", hotelJSON));
            JSONArray roomCategoryArray = (JSONArray) hotelJSON.get("room_categories");
            Set<RoomCategory> roomCategories = new HashSet<>();
            for (Object roomCategory : roomCategoryArray) {
                roomCategories.add(JSONParsingUtils.getRoomCategoryFromJSON((JSONObject) roomCategory));
            }
            hotel.setRoomCategories(roomCategories);
            return hotel;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public CategorySpecificAvailabilityQuery parseCategorySpecificAvailabilityRequest(JSONObject request) {
        try {
            CategorySpecificAvailabilityQuery query = new CategorySpecificAvailabilityQuery();
            query.setHotelId(JSONParsingUtils.getLongFromJSON("hotel_id", request));
            query.setRoomCategoryId(JSONParsingUtils.getLongFromJSON("room_category_id", request));
            query.setStartDate(JSONParsingUtils.getDateFromJSON("start_date", request));
            query.setEndDate(JSONParsingUtils.getDateFromJSON("end_date", request));
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public AvailabilityUpdateRequest parseAvailabilityUpdateRequest(JSONObject request) {
        try {
            AvailabilityUpdateRequest updateRequest = new AvailabilityUpdateRequest();
            updateRequest.setHotelID(JSONParsingUtils.getLongFromJSON("hotel_id", request));
            updateRequest.setRoomCategoryId(JSONParsingUtils.getLongFromJSON("room_category_id", request));
            updateRequest.setUpdates(getAvailabilityUpdateListFromJSON((JSONArray) request.get("modifications")));
            return updateRequest;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    private List<AvailabilityUpdateTuple> getAvailabilityUpdateListFromJSON(JSONArray modifications) {
        try {
            List<AvailabilityUpdateTuple> updates = new ArrayList<>();
           for (Object updateObject : modifications) {
               JSONObject updateJSON = (JSONObject) updateObject;
               AvailabilityUpdateTuple updateTuple = new AvailabilityUpdateTuple();
               updateTuple.setAction(UpdateAction.valueOf((String) updateJSON.get("action")));
               if (updateTuple.getAction() == UpdateAction.ADD) {
                   updateTuple.setNightlyRate(JSONParsingUtils.getDoubleFromJSON("nightly_rate", updateJSON));
               }
               updateTuple.setNumOfRooms(JSONParsingUtils.getLongFromJSON("num_of_rooms", updateJSON));
               updateTuple.setDate(JSONParsingUtils.getDateFromJSON("date", updateJSON));
               updates.add(updateTuple);
           }
           return updates;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the JSON Array");
            System.out.println("JSON Array: " + modifications);
            return null;
        }
    }
}
