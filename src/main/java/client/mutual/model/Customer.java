package client.mutual.model;

public class Customer {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String streetAddress;

    private String state;

    private String city;

    public Customer() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String toString() {
        return String.format("First name: %s%sLast name: %s%sPhone number: %s%sEmail address: %s%sAddress: %s, %s, %s%s",
                firstName, System.lineSeparator(), lastName, System.lineSeparator(), phoneNumber, System.lineSeparator(),
                email, System.lineSeparator(), streetAddress, city, state, System.lineSeparator());
    }
}
