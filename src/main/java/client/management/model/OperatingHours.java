package client.management.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OperatingHours implements Comparable<OperatingHours>{

    private LocalTime openingTime;

    private LocalTime closingTime;

    private DayOfWeek dayOfWeek;

    public OperatingHours(LocalTime openingTime, LocalTime closingTime, DayOfWeek dayOfWeek) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.dayOfWeek = dayOfWeek;
    }

    public OperatingHours() {
        openingTime = LocalTime.of(9, 0);
        closingTime = LocalTime.of(17, 0);
    }

    public OperatingHours(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public int compareTo(OperatingHours o) {
        return this.dayOfWeek.getValue() - o.getDayOfWeek().getValue();
    }

}
