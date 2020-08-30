package server.implementation;

import client.customer.model.HotelDetails;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.CustomerRequestType;
import org.json.simple.JSONObject;
import server.api.*;
import server.model.ExtraNightsRequest;
import server.model.ReservationModificationRequest;

import java.util.List;
import java.util.Map;

public class CustomerRequestHandlerImpl implements CustomerRequestHandler {

    private ConnectionPool connectionPool;
    private CustomerRequestParser requestParser;
    private CustomerResponseBuilder responseBuilder;
    private CustomerDAO dao;

    public CustomerRequestHandlerImpl(ConnectionPool connectionPool) {
        this.requestParser = new CustomerRequestParserImpl();
        this.responseBuilder = new CustomerResponseBuilderImpl();
        this.connectionPool = connectionPool;
        this.dao = new CustomerDAOImpl(connectionPool, new MutualDAOImpl(connectionPool));
    }

    @Override
    public String handle(JSONObject request) {
        if (request == null) {
            return null;
        }
        switch (getRequestType(request)) {
            case GET_AVAILABLE_ROOMS: {
                RoomAvailabilityQuery query = requestParser.parseRoomAvailabilityQueryRequest(request);
                Map<HotelDetails, List<RoomListing>> roomAvailability = dao.getRoomAvailability(query);
                return responseBuilder.buildAvailabilityResponse(roomAvailability);
            }
            case GET_HOTEL_DETAILS: {
                long hotelId = requestParser.parseHotelRequest(request);
                HotelDetails hotel = dao.getHotel(hotelId);
                return responseBuilder.buildHotelResponse(hotel);
            }
            case NEW_RESERVATION: {
                CustomerReservation newReservation = requestParser.parseNewReservationRequest(request);
                long reservationId = dao.newReservation(newReservation);
                return responseBuilder.buildNewReservationResponse(reservationId);
            }
            case LOAD_RESERVATION_BY_ID: {
                CustomerReservationQuery query = requestParser.parseReservationSearchByIdRequest(request);
                List<CustomerReservation> matchingReservations = dao.getReservationsById(query);
                return responseBuilder.buildReservationSearchResponse(matchingReservations);
            }
            case LOAD_RESERVATION_BY_EMAIL:{
                CustomerReservationQuery query = requestParser.parseReservationSearchByEmailRequest(request);
                List<CustomerReservation> matchingReservations = dao.getReservationsByEmail(query);
                return responseBuilder.buildReservationSearchResponse(matchingReservations);
            }
            case MODIFY_RESERVATION:{
                ReservationModificationRequest modificationRequest = requestParser.parseReservationModificationRequest(request);
                boolean successfullyModified = dao.modifyReservation(modificationRequest);
                return responseBuilder.buildReservationModificationResponse(successfullyModified);
            }
            case GET_PRICE_FOR_EXTRA_NIGHTS: {
                ExtraNightsRequest extraNightsRequest = requestParser.parseExtraNightsRequest(request);
                double priceForExtraNights = dao.getPriceForExtraNights(extraNightsRequest);
                return responseBuilder.buildExtraNightsResponse(priceForExtraNights);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "failure");
        return jsonObject.toJSONString();
    }

    private CustomerRequestType getRequestType(JSONObject request) {
        return CustomerRequestType.valueOf((String) request.get("request"));
    }
}
