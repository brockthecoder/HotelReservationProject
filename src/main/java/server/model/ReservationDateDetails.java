package server.model;

import java.time.LocalDate;
import java.util.List;

public class ReservationDateDetails {

    private List<LocalDate> dates;

    private long roomCategoryId;

    private double nightlyRate;

    private double total;

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public long getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(long roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public double getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(double nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ReservationDateDetails(List<LocalDate> dates, long roomCategoryId, double nightlyRate, double total) {
        this.dates = dates;
        this.roomCategoryId = roomCategoryId;
        this.nightlyRate = nightlyRate;
        this.total = total;
    }
}
