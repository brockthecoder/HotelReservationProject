package client.customer.api;

import mutual.model.CustomerReservationQuery;
import mutual.model.enums.ReservationModificationType;
import client.mutual.model.CustomerReservation;
import client.customer.model.RoomAvailabilityQuery;

import java.time.LocalDate;
import java.util.List;

public interface CustomerRequestJSONConverter {

    String getHotelJSONRequest(long id);

    String getNewReservationJSONRequest(CustomerReservation reservation);

    String getPriceForExtraNightsJSONRequest(List<LocalDate> dates, CustomerReservation reservation);

    String getReservationByIdJSONRequest(CustomerReservationQuery id);

    String getReservationByEmailJSONRequest(CustomerReservationQuery query);

    String getReservationModificationJSONRequest(CustomerReservation reservation, ReservationModificationType action);

    String getHotelAvailabilityJSONRequest(RoomAvailabilityQuery query);

    String getCloseConnectionRequest();

}
