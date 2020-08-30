package client.management.api;

import client.management.model.AvailabilityListing;
import client.management.model.ManagementAccount;
import client.management.model.ManagementHotelDetails;
import client.management.model.RoomCategory;
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

}
