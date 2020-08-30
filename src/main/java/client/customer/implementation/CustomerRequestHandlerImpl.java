package client.customer.implementation;

import client.customer.api.CustomerRequestHandler;
import client.mutual.api.ClientCommunicationSocket;
import client.customer.api.CustomerRequestJSONConverter;
import client.customer.api.ServerCustomerResponseJSONConverter;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.ReservationModificationType;
import client.customer.model.HotelDetails;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.implementation.ClientCommunicationSocketImpl;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CustomerRequestHandlerImpl implements CustomerRequestHandler {

    CustomerRequestJSONConverter requestConverter;
    ServerCustomerResponseJSONConverter responseConverter;
    ClientCommunicationSocket communicationSocket;

    public CustomerRequestHandlerImpl() {
        responseConverter = new ServerCustomerResponseJSONConverterImpl();
        communicationSocket = new ClientCommunicationSocketImpl();
        requestConverter = new CustomerRequestJSONConverterImpl();
    }

    @Override
    public HotelDetails getHotelDetailsById(long id) {

        String jsonRequest = requestConverter.getHotelJSONRequest(id);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseHotelDetails(jsonResponse);
    }

    @Override
    public long newReservation(CustomerReservation reservation) {

        String jsonRequest = requestConverter.getNewReservationJSONRequest(reservation);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseNewReservationId(jsonResponse);
    }

    @Override
    public List<CustomerReservation> getReservationById(CustomerReservationQuery query) {

        String jsonRequest = requestConverter.getReservationByIdJSONRequest(query);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseReservationDetails(jsonResponse);
    }

    @Override
    public List<CustomerReservation> getReservationByEmail(CustomerReservationQuery query) {

        String jsonRequest = requestConverter.getReservationByEmailJSONRequest(query);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseReservationDetails(jsonResponse);
    }

    @Override
    public boolean modifyReservation(CustomerReservation reservation, ReservationModificationType action) {

        String jsonRequest = requestConverter.getReservationModificationJSONRequest(reservation, action);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseReservationModificationResponse(jsonResponse);
    }

    @Override
    public Map<HotelDetails, List<RoomListing>> getHotelAvailability(RoomAvailabilityQuery query) {

        String jsonRequest = requestConverter.getHotelAvailabilityJSONRequest(query);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parseAvailability(jsonResponse);
    }

    @Override
    public double getPriceForExtraNights(List<LocalDate> dates, CustomerReservation reservation) {

        String jsonRequest = requestConverter.getPriceForExtraNightsJSONRequest(dates, reservation);
        String jsonResponse = communicationSocket.sendRequest(jsonRequest);
        return responseConverter.parsePriceForExtraNights(jsonResponse);
    }

    @Override
    public void closeConnection() {
        String jsonRequest = requestConverter.getCloseConnectionRequest();
        communicationSocket.close(jsonRequest);
    }
}
