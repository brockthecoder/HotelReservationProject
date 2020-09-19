package client.management.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ServerManagementResponseJSONConverter {

    long parseId(String json);

    ManagementAccount parseManagementAccountSignIn(String json);

    boolean parseBoolean(String json);

    List<CustomerReservation> parseReservationList(String json, ManagementHotelDetails hotel);

    List<RoomListing> parseGenericAvailabilityList(String json);

    Set<RoomCategory> parseRoomCategories(String json);

    Map<LocalDate, AvailabilityListing> parseCategoryAvailability(String json);

    Set<OperatingHours> parseOperatingHours(String json);

    Set<AmenityType> parseAmenities(String json);
}
