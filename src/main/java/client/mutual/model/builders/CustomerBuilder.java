package client.mutual.model.builders;

import client.mutual.model.Customer;

public class CustomerBuilder {

    private Customer customer;

    public CustomerBuilder() {
        customer = new Customer();
    }

    public CustomerBuilder withFirstName(String firstName) {
        customer.setFirstName(firstName);
        return this;
    }

    public CustomerBuilder withLastName(String lastName) {
        customer.setLastName(lastName);
        return this;
    }

    public CustomerBuilder withPhoneNumber(String phoneNumber) {

        customer.setPhoneNumber(phoneNumber);
        return this;
    }

    public CustomerBuilder withEmail(String email) {

        customer.setEmail(email);
        return this;
    }

    public CustomerBuilder withStreetAddress(String streetAddress) {

        customer.setStreetAddress(streetAddress);
        return this;
    }

    public CustomerBuilder withState(String state) {

        customer.setState(state);
        return this;
    }

    public CustomerBuilder withCity(String city) {

        customer.setCity(city);
        return this;
    }

    public Customer build() {
        return customer;
    }

}
