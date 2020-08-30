package client.management.implementation;

import client.management.api.ManagementRequestJSONConverter;
import client.management.api.ServerManagementResponseJSONConverter;
import client.management.model.*;
import client.mutual.api.ClientCommunicationSocket;
import client.mutual.implementation.ClientCommunicationSocketImpl;
import client.mutual.model.CustomerReservation;
import client.management.api.ManagementRequestHandler;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagementRequestHandlerImpl implements ManagementRequestHandler {

    private ClientCommunicationSocket communicationSocket;
    private ManagementRequestJSONConverter requestConverter;
    private ServerManagementResponseJSONConverter responseConverter;

    public ManagementRequestHandlerImpl() {
        this.communicationSocket = new ClientCommunicationSocketImpl();
        this.requestConverter = new ManagementRequestJSONConverterImpl();
        this.responseConverter = new ServerManagementResponseJSONConverterImpl();
    }

    public long createNewAccount(ManagementAccount account) {
        String jsonRequest = requestConverter.getNewAccountJSONRequest(account);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseId(jsonResponse);
    }


    @Override
    public ManagementAccount signIn(ManagementAccount account) {
        String jsonRequest = requestConverter.getSignInJSONRequest(account);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseManagementAccountSignIn(jsonResponse);
    }

    @Override
    public boolean deleteHotel(ManagementHotelDetails hotel) {
        String jsonRequest = requestConverter.getHotelDeletionJSONRequest(hotel);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseBoolean(jsonResponse);
    }

    @Override
    public List<CustomerReservation> getUpcomingCheckIns(ManagementHotelDetails hotelDetails) {
        String jsonRequest = requestConverter.getUpcomingCheckInsJSONRequest(hotelDetails);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseReservationList(jsonResponse, hotelDetails);
    }

    @Override
    public List<RoomListing> getAvailability(ManagementRoomQuery query) {
        String jsonRequest = requestConverter.getGenericAvailabilityJSONRequest(query);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseGenericAvailabilityList(jsonResponse);
    }

    @Override
    public long newReservation(CustomerReservation reservation) {
        String jsonRequest = requestConverter.getNewReservationJSONRequest(reservation);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseId(jsonResponse);
    }

    @Override
    public boolean updateHotelDetail(ManagementHotelDetails hotel, HotelDetailChange detailChange) {
        String jsonRequest = requestConverter.getHotelDetailUpdateJSONRequest(hotel, detailChange);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseBoolean(jsonResponse);
    }

    @Override
    public Set<RoomCategory> getRoomCategories(ManagementHotelDetails hotel) {
        String jsonRequest = requestConverter.getRoomCategoryJSONRequest(hotel);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseRoomCategories(jsonResponse);
    }

    @Override
    public List<CustomerReservation> reservationSearch(ManagementReservationQuery query, ManagementHotelDetails hotel) {
        String jsonRequest = requestConverter.getReservationSearchJSONRequest(query);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseReservationList(jsonResponse, hotel);
    }

    @Override
    public long createNewHotel(ManagementHotelDetails hotel) {
        String jsonRequest = requestConverter.getNewHotelJSONRequest(hotel);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseId(jsonResponse);
    }

    @Override
    public Map<LocalDate, AvailabilityListing> getAvailabilityForCategory(ManagementHotelDetails hotel, LocalDate startDate, LocalDate endDate, RoomCategory category) {
        String jsonRequest = requestConverter.getCategorySpecificAvailabilityJSONRequest(hotel, startDate, endDate, category);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseCategoryAvailability(jsonResponse);
    }

    @Override
    public boolean updateAvailability(ManagementHotelDetails hotel, RoomCategory roomCategory, List<AvailabilityUpdateTuple> datesAndRoomCount) {
        String jsonRequest = requestConverter.getAvailabilityUpdateJSONRequest(hotel, roomCategory, datesAndRoomCount);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseBoolean(jsonResponse);
    }

    @Override
    public void closeConnection() {
        String jsonRequest = requestConverter.getCloseConnectionRequest();
        communicationSocket.close(jsonRequest);
    }
}
