package server.api;

import client.customer.model.HotelDetails;
import client.management.model.AmenityType;
import client.management.model.OperatingHours;
import client.mutual.model.CustomerReservation;

import java.util.Set;

public interface MutualDAO {

    HotelDetails getHotel(long hotelId);

    long newReservation(CustomerReservation reservationDetails);

    Set<AmenityType> getAmenities(long hotelId);

    Set<OperatingHours> getOperatingHours(long hotelId);

}
