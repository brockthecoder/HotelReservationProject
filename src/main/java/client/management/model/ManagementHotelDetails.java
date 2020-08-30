package client.management.model;

import client.customer.model.HotelDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManagementHotelDetails extends HotelDetails {

    private ManagementAccount hotelOwner;

    private Set<RoomCategory> roomCategories;

    public ManagementHotelDetails() {
        super();
    }

    public Set<RoomCategory> getRoomCategories() {
        return roomCategories;
    }

    public void setRoomCategories(Set<RoomCategory> roomCategories) {
        this.roomCategories = roomCategories;
    }

    public ManagementAccount getHotelOwner() {
        return hotelOwner;
    }

    public void addAmenity(AmenityType amenityType) {
        this.getAmenities().add(amenityType);
    }

    public void addRoomCategory(RoomCategory roomCategory) {
        roomCategories.add(roomCategory);
    }

    public void addOperatingHours(OperatingHours hours) {
        this.getOperatingHours().add(hours);
    }

    public void setHotelOwner(ManagementAccount hotelOwner) {
        this.hotelOwner = hotelOwner;
    }

    private String roomCategoriesToString() {
        if (roomCategories == null || roomCategories.size() == 0) {
            return "No room categories".concat(System.lineSeparator());
        }
        StringBuilder sb = new StringBuilder();
        for (RoomCategory roomCategory : roomCategories) {
            sb.append(roomCategory.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String toString() {
        return String.format("%s%sRoom categories:%s%s%s", super.toString(), System.lineSeparator(), System.lineSeparator(), System.lineSeparator(), roomCategoriesToString());
    }
}
