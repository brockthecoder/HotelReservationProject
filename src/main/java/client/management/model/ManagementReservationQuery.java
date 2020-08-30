package client.management.model;

import mutual.model.enums.ReservationIdentifierType;

public class ManagementReservationQuery {

    private ReservationIdentifierType identifierType;

    private String identifier;

    private long hotelId;

    public ManagementReservationQuery() {
    }

    public ManagementReservationQuery(ReservationIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public ReservationIdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(ReservationIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }
}
