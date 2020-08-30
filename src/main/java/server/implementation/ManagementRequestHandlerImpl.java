package server.implementation;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.CategorySpecificAvailabilityQuery;
import mutual.model.enums.ManagementRequestType;
import org.json.simple.JSONObject;
import server.api.*;

import java.time.LocalDate;
import java.util.*;

public class ManagementRequestHandlerImpl implements ManagementRequestHandler {

    private final ManagementDAO dao;
    private final MutualDAO mutualDAO;
    private List<Long> authorizedHotels;
    private ManagementRequestParser requestParser;
    private ManagementResponseBuilder responseBuilder;

    public ManagementRequestHandlerImpl(ConnectionPool connectionPool) {
        this.authorizedHotels = new ArrayList<>();
        this.mutualDAO = new MutualDAOImpl(connectionPool);
        this.dao = new ManagementDAOImpl(connectionPool, mutualDAO);
        this.requestParser = new ManagementRequestParserImpl();
        this.responseBuilder = new ManagementResponseBuilderImpl();
    }

    @Override
    public String handle(JSONObject request) {

        if (request == null) {
            return null;
        }
        switch (getRequestType(request)) {
            case ATTEMPT_SIGN_IN: {
                ManagementAccount account = requestParser.parseSignInAttempt(request);
                account = dao.getAccount(account);
                return responseBuilder.buildSignInAttemptResponse(account);
            }
            case CREATE_NEW_ACCOUNT: {
                ManagementAccount newAccount = requestParser.parseNewAccountRequest(request);
                long accountId = dao.newAccount(newAccount);
                return responseBuilder.buildNewAccountResponse(accountId);
            }
            case DELETE_HOTEL: {
                long hotelId = requestParser.parseHotelDeletionRequest(request);
                boolean wasSuccess = dao.deleteHotel(hotelId);
                return responseBuilder.buildDeleteHotelResponse(wasSuccess);
            }
            case GET_UPCOMING_CHECK_INS: {
                long hotelId = requestParser.parseUpcomingCheckInsRequest(request);
                List<CustomerReservation> reservations = dao.getReservations(hotelId);
                return responseBuilder.buildUpcomingCheckInsResponse(reservations);
            }
            case GENERIC_AVAILABILITY_QUERY: {
                ManagementRoomQuery query = requestParser.parseGenericAvailabilityRequest(request);
                List<RoomListing> availability = dao.getAvailability(query);
                return responseBuilder.buildGenericAvailabilityResponse(availability);
            }
            case NEW_RESERVATION: {
                CustomerReservation newReservation = requestParser.parseNewReservationRequest(request);
                long reservationId = dao.newReservation(newReservation);
                return responseBuilder.buildNewReservationResponse(reservationId);
            }
            case UPDATE_HOTEL_DETAILS: {
                HotelDetailChange detailChange = requestParser.parseHotelDetailUpdateRequest(request);
                boolean wasSuccess = dao.updateHotel(detailChange);
                return responseBuilder.buildHotelDetailUpdateResponse(wasSuccess);
            }
            case GET_ROOM_CATEGORIES: {
                long hotelId = requestParser.parseRoomCategoryRequest(request);
                Set<RoomCategory> roomCategories = dao.getRoomCategories(hotelId);
                return responseBuilder.buildRoomCategoryResponse(roomCategories);
            }
            case RESERVATION_SEARCH: {
                ManagementReservationQuery managementReservationQuery = requestParser.parseReservationSearchRequest(request);
                List<CustomerReservation> matchingReservations = dao.getReservations(managementReservationQuery);
                return responseBuilder.buildReservationSearchResponse(matchingReservations);
            }
            case NEW_HOTEL: {
                ManagementHotelDetails hotel = requestParser.parseNewHotelRequest(request);
                long newHotelId = dao.newHotel(hotel);
                return responseBuilder.buildNewHotelResponse(newHotelId);
            }
            case CATEGORY_SPECIFIC_AVAILABILITY_QUERY: {
                CategorySpecificAvailabilityQuery query = requestParser.parseCategorySpecificAvailabilityRequest(request);
                Map<LocalDate, AvailabilityListing> availability = dao.getAvailability(query);
                return responseBuilder.buildCategorySpecificAvailabilityResponse(availability);
            }
            case MODIFY_AVAILABILITY: {
                AvailabilityUpdateRequest updateRequest = requestParser.parseAvailabilityUpdateRequest(request);
                boolean wasSuccess = dao.updateAvailability(updateRequest);
                return responseBuilder.buildAvailabilityUpdateResponse(wasSuccess);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "failure");
        return jsonObject.toJSONString();
    }

    private ManagementRequestType getRequestType(JSONObject request) {
        return ManagementRequestType.valueOf((String) request.get("request"));
    }
}
