package client.management.model;

public class RoomCategory {

    private long id;

    private String name;

    private String description;

    private long maxOccupants;

    public RoomCategory() {
    }

    public RoomCategory(RoomCategory roomCategory) {
        this.id = roomCategory.getId();
        this.name = roomCategory.getName();
        this.description = roomCategory.getDescription();
        this.maxOccupants = roomCategory.getMaxOccupants();
    }

    public RoomCategory(String name, String description, int maxOccupants) {
        this.name = name;
        this.description = description;
        this.maxOccupants = maxOccupants;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String toString() {
        return String.format("Name: %s%sDescription: %s%sMax Occupancy: %s%s", name, System.lineSeparator(), description, System.lineSeparator(), maxOccupants, System.lineSeparator());
    }
}
