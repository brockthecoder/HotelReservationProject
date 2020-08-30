package server.model;

import client.mutual.model.CustomerReservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExtraNightsRequest {

    private long roomCategoryId;

    private List<LocalDate> extraNights;

    public ExtraNightsRequest() {
        extraNights = new ArrayList<>();
    }

    public long getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(long roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public List<LocalDate> getExtraNights() {
        return extraNights;
    }

    public void setExtraNights(List<LocalDate> extraNights) {
        this.extraNights = extraNights;
    }
}
