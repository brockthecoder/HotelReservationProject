package client.management.model;

public enum AmenityType {
    EXERCISE_ROOM("Exercise Room"),
    SWIMMING_POOL("Swimming Pool"),
    HOT_TUB("Hot Tub"),
    VALET_PARKING("Valet Parking"),
    DINING("Dining"),
    ROOM_SERVICE("Room Service"),
    FREE_PARKING("Free Parking"),
    DAILY_MAID_SERVICE("Daily Maid Service"),
    EXPRESS_CHECKOUT("Express Checkout"),
    FREE_INTERNET("Free Internet"),
    HIGH_SPEED_INTERNET("High Speed Internet"),
    IN_ROOM_TV("TV in room"),
    ATM("ATM"),
    NEWSPAPER("Newspaper available"),
    SAFE("Safe in room"),
    MINI_FRIDGE("Mini Fridge"),
    CONCIERGE("Concierge"),
    BUSINESS_CENTER("Business Center"),
    CLUB_LOUNGE("Club Lounge"),
    PRINTERS("Printers"),
    COMPUTERS("Computers"),
    BAR("Bar");

    private String representation;

    AmenityType(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
