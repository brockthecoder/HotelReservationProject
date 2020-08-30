package client.customer.model;

import client.management.model.AmenityType;
import client.management.model.OperatingHours;
import java.time.LocalTime;
import java.util.Set;

public class HotelDetails {

    private long id;

    private String name;

    private String description;

    private String phoneNumber;

    private String streetAddress;

    private String state;

    private String city;

    private long checkInAge;

    private long numOfFloors;

    private Set<AmenityType> amenities;

    private Set<OperatingHours> operatingHours;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCheckInAge() {
        return checkInAge;
    }

    public void setCheckInAge(long checkInAge) {
        this.checkInAge = checkInAge;
    }

    public long getNumOfFloors() {
        return numOfFloors;
    }

    public void setNumOfFloors(long numOfFloors) {
        this.numOfFloors = numOfFloors;
    }

    public Set<AmenityType> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<AmenityType> amenities) {
        this.amenities = amenities;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Set<OperatingHours> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(Set<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
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

    public String streetAddressToString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(streetAddress).append(", ").append(city).append(", ").append(state);
        return sb.toString();
    }

    public String operatingHoursToString() {

        if (operatingHours == null || operatingHours.size() == 0) {
            return "There are no operating hours".concat(System.lineSeparator());
        }

        StringBuilder sb = new StringBuilder(100);
        sb.append("Operating Hours:").append(System.lineSeparator());
        operatingHours.stream().sequential().sorted().forEach((OperatingHours h) ->
        {
            sb.append(h.getDayOfWeek().toString()).append(": ")
                    .append(h.getOpeningTime().toString())
                    .append(" - ").append(h.getClosingTime().toString()).append(System.lineSeparator());
        });
        return sb.toString();
    }

    public String amenitiesToString() {
        if (amenities == null || amenities.size() == 0) {
            return "There are no amenities".concat(System.lineSeparator());
        }
        StringBuilder sb = new StringBuilder(100);
        int size = amenities.size();
        int i = 1;
        for (AmenityType amenity : amenities) {
            if (i != size) {
                sb.append(amenity.toString()).append(", ");
            }
            else {
                sb.append(amenity.toString());
            }
            i++;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s:%sDescription:%s%sPhone Number: %s%sAddress: %s%s%s%s%sCheck-In Age: %d%sFloor Count: %d%sCheck-In Time: %s%sCheck-Out Time: %s%sAmenities: %s%s",
                            name, System.lineSeparator(), System.lineSeparator(), descriptionToString(),
                            phoneNumber, System.lineSeparator(), streetAddressToString(), System.lineSeparator(),System.lineSeparator(), operatingHoursToString(), System.lineSeparator(),
                            checkInAge, System.lineSeparator(), numOfFloors, System.lineSeparator(), checkInTime.toString(), System.lineSeparator(),
                            checkOutTime.toString(), System.lineSeparator(), amenitiesToString(), System.lineSeparator());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof HotelDetails && ((HotelDetails) obj).getId() == this.id);
    }
}
