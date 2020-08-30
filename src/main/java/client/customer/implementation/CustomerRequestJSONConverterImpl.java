package client.customer.implementation;

import client.customer.api.CustomerRequestJSONConverter;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.CustomerRequestType;
import mutual.model.enums.ReservationModificationType;
import client.customer.model.RoomAvailabilityQuery;
import mutual.model.enums.Requester;
import mutual.utilities.JSONBuilderUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import client.mutual.model.CustomerReservation;
import java.time.LocalDate;
import java.util.List;

@SuppressWarnings(value = "unchecked")
public class CustomerRequestJSONConverterImpl implements CustomerRequestJSONConverter {

    @Override
    public String getHotelJSONRequest(long id) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.CUSTOMER.name());
        jsonObject.put("request", CustomerRequestType.GET_HOTEL_DETAILS.name());
        jsonObject.put("hotel_id", id);
        return jsonObject.toJSONString();

    }

    @Override
    public String getNewReservationJSONRequest(CustomerReservation reservation) {

       try {
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("requester", Requester.CUSTOMER.name());
           jsonObject.put("request", CustomerRequestType.NEW_RESERVATION.name());
           jsonObject.put("reservation", JSONBuilderUtils.buildJSONCustomerReservation(reservation));
           return jsonObject.toJSONString();
       }
       catch (NullPointerException e) {
           throw new NullPointerException("Part of the reservation object was null");
       }
    }

    @Override
    public String getReservationByIdJSONRequest(CustomerReservationQuery query) {

        if (query == null) {
            throw new NullPointerException("The query is null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.CUSTOMER.name());
        jsonObject.put("request", CustomerRequestType.LOAD_RESERVATION_BY_ID.name());
        jsonObject.put("reservation_id", query.getReservationId());
        jsonObject.put("customer_last_name", query.getLastName());
        return jsonObject.toJSONString();
    }

    @Override
    public String getReservationByEmailJSONRequest(CustomerReservationQuery query) {

        if (query == null) {
            throw new NullPointerException("The query is null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.CUSTOMER.name());
        jsonObject.put("request", CustomerRequestType.LOAD_RESERVATION_BY_EMAIL.name());
        jsonObject.put("reservation_email", query.getEmail());
        jsonObject.put("customer_last_name", query.getLastName());
        return jsonObject.toJSONString();
    }

    @Override
    public String getReservationModificationJSONRequest(CustomerReservation reservation, ReservationModificationType action) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("requester", Requester.CUSTOMER.name());
            jsonObject.put("request", CustomerRequestType.MODIFY_RESERVATION.name());
            jsonObject.put("modification_type", action.name());
            jsonObject.put("reservation", JSONBuilderUtils.buildJSONCustomerReservation(reservation));
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            throw new NullPointerException("Part of the reservation object was null");
        }
    }

    @Override
    public String getHotelAvailabilityJSONRequest(RoomAvailabilityQuery query) {

        if (query == null || query.getCheckInDate() == null || query.getCheckOutDate() == null || query.getNumOfPeople() == 0 || query.getCity() == null) {
            throw new NullPointerException("One of the arguments was null");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.CUSTOMER.name());
        jsonObject.put("request", CustomerRequestType.GET_AVAILABLE_ROOMS.name());
        jsonObject.put("city", query.getCity());
        jsonObject.put("check_in_date", query.getCheckInDate().toString());
        jsonObject.put("check_out_date", query.getCheckOutDate().toString());
        jsonObject.put("num_of_people", query.getNumOfPeople());
        return jsonObject.toJSONString();
    }

    @Override
    public String getPriceForExtraNightsJSONRequest(List<LocalDate> dates, CustomerReservation reservation) {

        if (dates == null || reservation == null) {
            throw new NullPointerException("One of the arguments was null");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", Requester.CUSTOMER.name());
        jsonObject.put("request", CustomerRequestType.GET_PRICE_FOR_EXTRA_NIGHTS.name());
        jsonObject.put("room_category_id", reservation.getRoomListing().getRoomCategoryId());
        JSONArray jsonArray = new JSONArray();

        for (LocalDate night : dates) {
            jsonArray.add(night.toString());
        }
        jsonObject.put("dates", jsonArray);
        return jsonObject.toJSONString();
    }

    @Override
    public String getCloseConnectionRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requester", "customer");
        jsonObject.put("request", "close_connection");
        return jsonObject.toJSONString();
    }
}
