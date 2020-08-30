package server.implementation;

import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.CustomerReservation;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.ReservationModificationType;
import mutual.utilities.JSONParsingUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.api.CustomerRequestParser;
import server.model.ExtraNightsRequest;
import server.model.ReservationModificationRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CustomerRequestParserImpl implements CustomerRequestParser {

    @Override
    public long parseHotelRequest(JSONObject request) {
        try {
            return JSONParsingUtils.getLongFromJSON("hotel_id", request);
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return -1;
        }
    }

    @Override
    public CustomerReservation parseNewReservationRequest(JSONObject request) {
        return JSONParsingUtils.getCustomerReservationFromJSON("reservation", request);

    }

    @Override
    public CustomerReservationQuery parseReservationSearchByIdRequest(JSONObject request) {
        try {
            CustomerReservationQuery query = new CustomerReservationQuery();
            query.setReservationId(JSONParsingUtils.getLongFromJSON("reservation_id", request));
            query.setLastName((String) request.get("customer_last_name"));
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public CustomerReservationQuery parseReservationSearchByEmailRequest(JSONObject request) {
        try {
            CustomerReservationQuery query = new CustomerReservationQuery();
            query.setEmail((String) request.get("reservation_email"));
            query.setLastName((String) request.get("customer_last_name"));
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public ReservationModificationRequest parseReservationModificationRequest(JSONObject request) {
        try {
            ReservationModificationRequest modificationRequest = new ReservationModificationRequest();
            modificationRequest.setReservation(JSONParsingUtils.getCustomerReservationFromJSON("reservation", request));
            modificationRequest.setModificationType(ReservationModificationType.valueOf((String) request.get("modification_type")));
            return modificationRequest;

        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }

    }

    @Override
    public RoomAvailabilityQuery parseRoomAvailabilityQueryRequest(JSONObject request) {
        try {
            RoomAvailabilityQuery query = new RoomAvailabilityQuery();
            query.setCheckInDate(JSONParsingUtils.getDateFromJSON("check_in_date", request));
            query.setCheckOutDate(JSONParsingUtils.getDateFromJSON("check_out_date", request));
            query.setNumOfPeople(JSONParsingUtils.getLongFromJSON("num_of_people", request));
            query.setCity((String) request.get("city"));
            return query;
        }
        catch (ClassCastException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }

    @Override
    public ExtraNightsRequest parseExtraNightsRequest(JSONObject request) {
        try {
            ExtraNightsRequest extraNightsRequest = new ExtraNightsRequest();
            extraNightsRequest.setRoomCategoryId(JSONParsingUtils.getLongFromJSON("room_category_id", request));
            JSONArray jsonArray = (JSONArray) request.get("dates");
            for (Object night : jsonArray) {
                extraNightsRequest.getExtraNights().add(LocalDate.parse((String) night));
            }
            return extraNightsRequest;
        }
        catch (ClassCastException | DateTimeParseException | NullPointerException e) {
            System.out.println("There was a null or invalid property in the request");
            System.out.println("Request: " + request);
            return null;
        }
    }
}
