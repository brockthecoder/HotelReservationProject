package client.management.implementation;

import client.management.api.ManagementRequestJSONConverter;
import client.management.model.*;
import mutual.model.enums.ManagementRequestType;
import mutual.model.enums.UpdateAction;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import mutual.model.enums.Requester;
import mutual.utilities.JSONBuilderUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@SuppressWarnings(value = "unchecked")
public class ManagementRequestJSONConverterImpl implements ManagementRequestJSONConverter {

    @Override
    public String getNewAccountJSONRequest(ManagementAccount account) {

        if (account == null) {
            throw new NullPointerException("The account was null when attempting to create a new account request");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.CREATE_NEW_ACCOUNT.toString());
        jsonObject.put("email", account.getEmail());
        jsonObject.put("password", account.getPassword());
        return jsonObject.toJSONString();
    }

    @Override
    public String getSignInJSONRequest(ManagementAccount account) {

        if (account == null) {
            throw new NullPointerException("The account was null when attempting to create a sign in request");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.ATTEMPT_SIGN_IN.toString());
        jsonObject.put("email", account.getEmail());
        jsonObject.put("password", account.getPassword());
        return jsonObject.toJSONString();
    }

    @Override
    public String getHotelDeletionJSONRequest(ManagementHotelDetails hotel) {

        if (hotel == null) {
            throw new NullPointerException("The hotel was null when attempting to create a deletion request");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.DELETE_HOTEL.toString());
        jsonObject.put("hotel_id", hotel.getId());
        return jsonObject.toJSONString();
    }

    @Override
    public String getUpcomingCheckInsJSONRequest(ManagementHotelDetails hotel) {

        if (hotel == null) {
            throw new NullPointerException("The hotel was null when attempting to create a check-in request");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.GET_UPCOMING_CHECK_INS.toString());
        jsonObject.put("hotel_id", hotel.getId());
        return jsonObject.toJSONString();
    }

    @Override
    public String getGenericAvailabilityJSONRequest(ManagementRoomQuery query) {

        if (query == null) {
            throw new NullPointerException("The query was null when attempting to create an availability request");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.GENERIC_AVAILABILITY_QUERY.toString());
        jsonObject.put("hotel_id", query.getHotel().getId());
        jsonObject.put("check_in_date", query.getCheckInDate().toString());
        jsonObject.put("check_out_date", query.getCheckOutDate().toString());
        jsonObject.put("num_of_people", query.getNumOfPeople());
        return jsonObject.toJSONString();
    }

    @Override
    public String getNewReservationJSONRequest(CustomerReservation reservation) {

        if (reservation == null) {
            throw new NullPointerException("The reservation was null when attempting to create a new reservation request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.NEW_RESERVATION.toString());
        jsonObject.put("reservation", JSONBuilderUtils.buildJSONCustomerReservation(reservation));
        return jsonObject.toJSONString();
    }

    private JSONObject getPaymentInfoJSON(ReservationPaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("card_number", paymentInfo.getCardNumber());
        jsonObject.put("cvv", paymentInfo.getCvv());
        jsonObject.put("expiration_date", paymentInfo.getExpirationDate());
        jsonObject.put("zip_code", paymentInfo.getZipCode());
        jsonObject.put("cardholder_name", paymentInfo.getCardHolderName());
        jsonObject.put("card_type", paymentInfo.getCreditCardType().name());
        return jsonObject;
    }

    private JSONObject getCustomerJSON(Customer customer) {
        if (customer == null) {
            return null;
        }
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

    @Override
    public String getHotelDetailUpdateJSONRequest(ManagementHotelDetails hotel, HotelDetailChange detailChange) {

        if (hotel == null || detailChange == null) {
            throw new NullPointerException("The hotel or detail was null when attempting to create a update request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.UPDATE_HOTEL_DETAILS.toString());
        jsonObject.put("hotel_id", detailChange.getHotelId());
        jsonObject.put("modification_type", detailChange.getPropertyModificationType().toString());
        switch (detailChange.getPropertyModificationType()) {
            case UPDATE_NAME:
                jsonObject.put("new_name", detailChange.getNewValue());
                break;
            case UPDATE_DESCRIPTION:
                jsonObject.put("new_description", detailChange.getNewValue());
                break;
            case UPDATE_PHONE_NUMBER:
                jsonObject.put("new_phone_number", detailChange.getNewValue());
                break;
            case UPDATE_CHECK_IN_AGE:
                jsonObject.put("new_check_in_age", detailChange.getNewValue());
                break;
            case UPDATE_CHECK_OUT_TIME:
                jsonObject.put("new_check_out_time", ((LocalTime) detailChange.getNewValue()).toString());
                break;
            case UPDATE_CHECK_IN_TIME:
                jsonObject.put("new_check_in_time", ((LocalTime) detailChange.getNewValue()).toString());
                break;
            case UPDATE_AMENITIES:
                JSONArray amenityArray = getJSONAmenityArray((Set<AmenityType>) detailChange.getNewValue());
                jsonObject.put("updated_amenities", amenityArray);
                break;
            case UPDATE_OPERATING_HOURS:
                JSONArray operatingHoursArray= getJSONOperatingHoursArray((Set<OperatingHours>) detailChange.getNewValue());
                jsonObject.put("updated_operating_hours", operatingHoursArray);
                break;
            case REMOVE_ROOM_CATEGORY:
                jsonObject.put("room_category_id", ((RoomCategory) detailChange.getNewValue()).getId());
                break;
            case ADD_ROOM_CATEGORY:
                JSONObject newRoomCategory = getRoomCategoryJSON((RoomCategory) detailChange.getNewValue());
                jsonObject.put("new_room_category", newRoomCategory);
                break;
            case UPDATE_ROOM_CATEGORY:
                JSONObject updatedRoomCategory = getRoomCategoryJSON((RoomCategory) detailChange.getNewValue());
                jsonObject.put("updated_room_category", updatedRoomCategory);
                break;
        }
        return jsonObject.toJSONString();
    }

    private JSONObject getRoomCategoryJSON(RoomCategory roomCategory) {
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

    private JSONArray getJSONOperatingHoursArray(Set<OperatingHours> operatingHoursSet) {
        if (operatingHoursSet == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (OperatingHours hours : operatingHoursSet) {
            JSONObject operatingHours = new JSONObject();
            operatingHours.put("opening_time", hours.getOpeningTime().toString());
            operatingHours.put("closing_time", hours.getClosingTime().toString());
            operatingHours.put("day_of_week", hours.getDayOfWeek().name());
            jsonArray.add(operatingHours);
        }
        return jsonArray;
    }

    private JSONArray getJSONAmenityArray(Set<AmenityType> amenitySet) {
        if (amenitySet == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (AmenityType amenity : amenitySet) {
            jsonArray.add(amenity.name());
        }
        return jsonArray;
    }

    @Override
    public String getRoomCategoryJSONRequest(ManagementHotelDetails hotel) {

        if (hotel == null) {
            throw new NullPointerException("The hotel was null when attempting to create a room categories request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.GET_ROOM_CATEGORIES.toString());
        jsonObject.put("hotel_id", hotel.getId());
        return jsonObject.toJSONString();
    }

    @Override
    public String getReservationSearchJSONRequest(ManagementReservationQuery managementReservationQuery) {

        if (managementReservationQuery == null) {
            throw new NullPointerException("The reservation query was null when attempting to create a search request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.RESERVATION_SEARCH.toString());
        jsonObject.put("hotel_id", managementReservationQuery.getHotelId());
        jsonObject.put("identifier_type", managementReservationQuery.getIdentifierType().name());
        switch (managementReservationQuery.getIdentifierType()) {
            case RESERVATION_NUMBER:
                jsonObject.put("reservation_id", Long.valueOf(managementReservationQuery.getIdentifier()));
                break;
            case EMAIL:
                jsonObject.put("customer_email", managementReservationQuery.getIdentifier());
                break;
            case FULL_NAME:
                jsonObject.put("customer_full_name", managementReservationQuery.getIdentifier());
                break;
        }
        return jsonObject.toJSONString();
    }

    @Override
    public String getNewHotelJSONRequest(ManagementHotelDetails hotel) {

        if (hotel == null) {
            throw new NullPointerException("The hotel was null when attempting to create a new hotel request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.NEW_HOTEL.toString());
        jsonObject.put("owner_account_id", hotel.getHotelOwner().getId());
        JSONObject hotelObject = getHotelJSON(hotel);
        jsonObject.put("hotel", hotelObject);
        return jsonObject.toJSONString();
    }

    private JSONObject getHotelJSON(ManagementHotelDetails hotel) {
        if (hotel == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", hotel.getName());
        jsonObject.put("description", hotel.getDescription());
        jsonObject.put("phone_number", hotel.getPhoneNumber());
        jsonObject.put("street_address", hotel.getStreetAddress());
        jsonObject.put("state", hotel.getState());
        jsonObject.put("city", hotel.getCity());
        jsonObject.put("check_in_age", hotel.getCheckInAge());
        jsonObject.put("num_of_floors", hotel.getNumOfFloors());
        JSONArray amenityArray = getJSONAmenityArray(hotel.getAmenities());
        jsonObject.put("amenities", amenityArray);
        JSONArray operatingHoursArray = getJSONOperatingHoursArray(hotel.getOperatingHours());
        jsonObject.put("operating_hours", operatingHoursArray);
        jsonObject.put("check_in_time", hotel.getCheckInTime().toString());
        jsonObject.put("check_out_time", hotel.getCheckOutTime().toString());
        JSONArray roomCategoryArray = getJSONRoomCategoryArray(hotel.getRoomCategories());
        jsonObject.put("room_categories", roomCategoryArray);
        return jsonObject;
    }

    private JSONArray getJSONRoomCategoryArray(Set<RoomCategory> roomCategories) {
        if (roomCategories == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (RoomCategory roomCategory : roomCategories) {
            jsonArray.add(getRoomCategoryJSON(roomCategory));
        }
        return jsonArray;
    }

    @Override
    public String getCategorySpecificAvailabilityJSONRequest(ManagementHotelDetails hotel, LocalDate startDate, LocalDate endDate, RoomCategory category) {

        if (hotel == null || startDate == null || endDate == null || category == null) {
            throw new NullPointerException("a null argument was passed when attempting to create an availability request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.CATEGORY_SPECIFIC_AVAILABILITY_QUERY.toString());
        jsonObject.put("hotel_id", hotel.getId());
        jsonObject.put("room_category_id", category.getId());
        jsonObject.put("start_date", startDate.toString());
        jsonObject.put("end_date", endDate.toString());
        return jsonObject.toJSONString();
    }

    @Override
    public String getAvailabilityUpdateJSONRequest(ManagementHotelDetails hotel, RoomCategory roomCategory, List<AvailabilityUpdateTuple> datesAndRoomCount) {

        if (roomCategory == null || datesAndRoomCount == null) {
            throw new NullPointerException("The room category or dates were null when attempting to create an availability update request");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.toString());
        jsonObject.put("request", ManagementRequestType.MODIFY_AVAILABILITY.toString());
        jsonObject.put("hotel_id", hotel.getId());
        jsonObject.put("room_category_id", roomCategory.getId());
        JSONArray modificationArray = getJSONAvailabilityModificationArray(datesAndRoomCount);
        jsonObject.put("modifications", modificationArray);
        return jsonObject.toJSONString();
    }

    private JSONArray getJSONAvailabilityModificationArray(List<AvailabilityUpdateTuple> datesAndRoomCount) {
        if (datesAndRoomCount == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (AvailabilityUpdateTuple tuple : datesAndRoomCount) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", tuple.getDate().toString());
            jsonObject.put("num_of_rooms", tuple.getNumOfRooms());
            jsonObject.put("action", tuple.getAction().name());
            if (tuple.getAction() == UpdateAction.ADD) {
                jsonObject.put("nightly_rate", tuple.getNightlyRate());
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public String getOperatingHoursRequest(long hotelID) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.name());
        jsonObject.put("request", ManagementRequestType.GET_OPERATING_HOURS.name());
        jsonObject.put("hotel_id", hotelID);
        return jsonObject.toJSONString();
    }

    @Override
    public String getAmenityRequest(long hotelID) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.MANAGEMENT.name());
        jsonObject.put("request", ManagementRequestType.GET_AMENITIES.name());
        jsonObject.put("hotel_id", hotelID);
        return jsonObject.toJSONString();
    }

    @Override
    public String getCloseConnectionRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", "management");
        jsonObject.put("request", "close_connection");
        return jsonObject.toJSONString();
    }
}
