package client.management.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ManagementRequestHandler {

    long createNewAccount(ManagementAccount account);

    ManagementAccount signIn(ManagementAccount account);

    boolean deleteHotel(ManagementHotelDetails hotel);

    List<CustomerReservation> getUpcomingCheckIns(ManagementHotelDetails hotelDetails);

    List<RoomListing> getAvailability(ManagementRoomQuery query);

    long newReservation(CustomerReservation reservation);

    boolean updateHotelDetail(ManagementHotelDetails hotel, HotelDetailChange detailChange);

    Set<RoomCategory> getRoomCategories(ManagementHotelDetails hotel);

    List<CustomerReservation> reservationSearch(ManagementReservationQuery query, ManagementHotelDetails hotel);

    long createNewHotel(ManagementHotelDetails hotel);

    Map<LocalDate, AvailabilityListing> getAvailabilityForCategory(ManagementHotelDetails hotel, LocalDate startDate, LocalDate endDate, RoomCategory category);

    boolean updateAvailability(ManagementHotelDetails hotel, RoomCategory roomCategory, List<AvailabilityUpdateTuple> datesAndRoomCount);

    void closeConnection();

    Set<OperatingHours> getOperatingHours(long hotelID);

    Set<AmenityType> getAmenities(long hotelID);
}
