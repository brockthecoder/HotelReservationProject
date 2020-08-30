package client.management.model.builders;

import client.management.model.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class HotelBuilder {

    private ManagementHotelDetails hotel;

    public HotelBuilder() {
        hotel = new ManagementHotelDetails();
    }

    public HotelBuilder withName(String name) {
        hotel.setName(name);
        return this;
    }

    public HotelBuilder withDescription(String description) {
        hotel.setDescription(description);
        return this;
    }

    public HotelBuilder withPhoneNumber(String phoneNumber) {
        hotel.setPhoneNumber(phoneNumber);
        return this;
    }

    public HotelBuilder withStreetAddress(String streetAddress) {
        hotel.setStreetAddress(streetAddress);
        return this;
    }

    public HotelBuilder withState(String state) {
        hotel.setState(state);
        return this;
    }

    public HotelBuilder withCity(String city) {
        hotel.setCity(city);
        return this;
    }

    public HotelBuilder withCheckInAgeOf(int checkInAge) {
        hotel.setCheckInAge(checkInAge);
        return this;
    }

    public HotelBuilder withFloorCountOf(int floorCount) {
        hotel.setNumOfFloors(floorCount);
        return this;
    }

    public HotelBuilder withAmenities(Set<AmenityType> amenities) {
        hotel.setAmenities(amenities);
        return this;
    }

    public HotelBuilder withOperatingHours(Set<OperatingHours> operatingHours) {
        hotel.setOperatingHours(operatingHours);
        return this;
    }

    public HotelBuilder withCheckInTimeOf(LocalTime checkInTime) {
        hotel.setCheckInTime(checkInTime);
        return this;
    }

    public HotelBuilder withCheckOutTimeOf(LocalTime checkOutTime) {
        hotel.setCheckOutTime(checkOutTime);
        return this;
    }

    public HotelBuilder withRoomCategories(Set<RoomCategory> roomCategories) {
        hotel.setRoomCategories(roomCategories);
        return this;
    }

    public HotelBuilder withOwner(ManagementAccount owner) {
        hotel.setHotelOwner(owner);
        return this;
    }

    public ManagementHotelDetails build() {
        return hotel;
    }
}
