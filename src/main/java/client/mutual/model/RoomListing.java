package client.mutual.model;

import java.text.DecimalFormat;

public class RoomListing {

    private long roomCategoryId;

    private DecimalFormat df;

    private String roomName;

    private String description;

    private long maxOccupants;

    private double nightlyRate;

    public RoomListing() {
        this.df = new DecimalFormat("0.00");
    }

    public RoomListing(int id, String roomName, String description, int maxOccupants, double nightlyRate) {
        this.df = new DecimalFormat("0.00");
        this.roomCategoryId = id;
        this.roomName = roomName;
        this.description = description;
        this.maxOccupants = maxOccupants;
        this.nightlyRate = nightlyRate;
    }

    public long getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(long roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMaxOccupants() {
        return maxOccupants;
    }

    public void setMaxOccupants(long maxOccupants) {
        this.maxOccupants = maxOccupants;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getNightlyRate() {
        return Double.parseDouble(df.format(nightlyRate));
    }

    public void setNightlyRate(double nightlyRate) {
        this.nightlyRate = Double.parseDouble(df.format(nightlyRate));
    }

    public String descriptionToString() {
        if (description.length() < 101) {
            return description.concat(System.lineSeparator());
        }

        int charPerLine = 100;
        StringBuilder sb = new StringBuilder(description.length() + 10);
        for (int i = charPerLine; i < description.length(); i += charPerLine) {
            sb.append(description.substring(i - 100, i)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Category: %s%sDescription: %sNightly Rate: %s%s", roomName, System.lineSeparator(), descriptionToString(), nightlyRate, System.lineSeparator());
    }
}
