package server.model;

import client.mutual.model.CustomerReservation;
import mutual.model.enums.ReservationModificationType;

public class ReservationModificationRequest {

    private CustomerReservation reservation;

    private ReservationModificationType modificationType;

    public CustomerReservation getReservation() {
        return reservation;
    }

    public void setReservation(CustomerReservation reservation) {
        this.reservation = reservation;
    }

    public ReservationModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(ReservationModificationType modificationType) {
        this.modificationType = modificationType;
    }
}
