package server.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.CategorySpecificAvailabilityQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ManagementDAO {

    ManagementAccount getAccount(ManagementAccount accountDetails);

    Set<AmenityType> getAmenities(long hotelId);

    Set<OperatingHours> getOperatingHours(long hotelId);

    long newAccount(ManagementAccount accountDetails);

    boolean deleteHotel(long hotelId);

    List<CustomerReservation> getReservations(ManagementReservationQuery query);

    List<CustomerReservation> getReservations(long hotelId);

    List<RoomListing> getAvailability(ManagementRoomQuery query);

    Map<LocalDate, AvailabilityListing> getAvailability(CategorySpecificAvailabilityQuery query);

    long newReservation(CustomerReservation reservationDetails);

    boolean updateHotel(HotelDetailChange detailChange);

    Set<RoomCategory> getRoomCategories(long hotelId);

    long newHotel(ManagementHotelDetails hotelDetails);

    boolean updateAvailability(AvailabilityUpdateRequest updateRequest);

}
