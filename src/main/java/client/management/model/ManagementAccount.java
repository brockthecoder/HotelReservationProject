package client.management.model;

import java.util.List;

public class ManagementAccount {

    private long id;

    private String email;

    private String password;

    private List<ManagementHotelDetails> hotels;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ManagementHotelDetails> getHotels() {
        return hotels;
    }

    public void setHotels(List<ManagementHotelDetails> hotels) {
        this.hotels = hotels;
    }

}
