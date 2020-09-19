package server.api;

import client.management.model.*;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ManagementResponseBuilder {

    String buildSignInAttemptResponse(ManagementAccount account);

    String buildNewAccountResponse(long accountId);

    String buildDeleteHotelResponse(boolean wasSuccess);

    String buildUpcomingCheckInsResponse(List<CustomerReservation> reservations);

    String buildGenericAvailabilityResponse(List<RoomListing> availability);

    String buildNewReservationResponse(long reservationId);

    String buildHotelDetailUpdateResponse(boolean wasSuccess);

    String buildRoomCategoryResponse(Set<RoomCategory> roomCategories);

    String buildReservationSearchResponse(List<CustomerReservation> matchingReservations);

    String buildNewHotelResponse(long newHotelId);

    String buildCategorySpecificAvailabilityResponse(Map<LocalDate, AvailabilityListing> availability);

    String buildAvailabilityUpdateResponse(boolean wasSuccess);

    String buildAmenityResponse(Set<AmenityType> amenities);

    String buildOperatingHoursResponse(Set<OperatingHours> operatingHours);
}
