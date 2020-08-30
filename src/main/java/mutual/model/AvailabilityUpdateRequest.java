package mutual.model;

import client.management.model.AvailabilityUpdateTuple;

import java.util.List;

public class AvailabilityUpdateRequest {

    private long roomCategoryId;

    private long hotelID;

    private List<AvailabilityUpdateTuple> updates;

    public long getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(long roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public long getHotelID() {
        return hotelID;
    }

    public void setHotelID(long hotelID) {
        this.hotelID = hotelID;
    }

    public List<AvailabilityUpdateTuple> getUpdates() {
        return updates;
    }

    public void setUpdates(List<AvailabilityUpdateTuple> updates) {
        this.updates = updates;
    }
}
