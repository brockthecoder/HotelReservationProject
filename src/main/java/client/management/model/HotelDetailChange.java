package client.management.model;

import mutual.model.enums.HotelPropertyModificationType;

public class HotelDetailChange {

    private HotelPropertyModificationType propertyModificationType;

    private Object newValue;

    private long hotelId;

    public HotelDetailChange(HotelPropertyModificationType propertyModificationType) {
        this.propertyModificationType = propertyModificationType;
    }

    public HotelDetailChange() {
    }

    public HotelDetailChange(HotelPropertyModificationType propertyModificationType, Object newValue) {
        this.propertyModificationType = propertyModificationType;
        this.newValue = newValue;
    }

    public HotelPropertyModificationType getPropertyModificationType() {
        return propertyModificationType;
    }

    public void setPropertyModificationType(HotelPropertyModificationType propertyModificationType) {
        this.propertyModificationType = propertyModificationType;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }
}
