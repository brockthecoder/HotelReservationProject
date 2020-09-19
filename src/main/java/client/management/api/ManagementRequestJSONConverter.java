package client.management.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;

import java.time.LocalDate;
import java.util.List;

public interface ManagementRequestJSONConverter {

    String getNewAccountJSONRequest(ManagementAccount account);

    String getSignInJSONRequest(ManagementAccount account);

    String getHotelDeletionJSONRequest(ManagementHotelDetails hotel);

    String getUpcomingCheckInsJSONRequest(ManagementHotelDetails hotel);

    String getGenericAvailabilityJSONRequest(ManagementRoomQuery query);

    String getNewReservationJSONRequest(CustomerReservation reservation);

    String getHotelDetailUpdateJSONRequest(ManagementHotelDetails hotel, HotelDetailChange detailChange);

    String getRoomCategoryJSONRequest(ManagementHotelDetails hotel);

    String getReservationSearchJSONRequest(ManagementReservationQuery managementReservationQuery);

    String getNewHotelJSONRequest(ManagementHotelDetails hotel);

    String getCategorySpecificAvailabilityJSONRequest(ManagementHotelDetails hotel, LocalDate startDate, LocalDate endDate, RoomCategory category);

    String getAvailabilityUpdateJSONRequest(ManagementHotelDetails hotel, RoomCategory roomCategory, List<AvailabilityUpdateTuple> datesAndRoomCount);

    String getCloseConnectionRequest();

    String getOperatingHoursRequest(long hotelID);

    String getAmenityRequest(long hotelID);

}
