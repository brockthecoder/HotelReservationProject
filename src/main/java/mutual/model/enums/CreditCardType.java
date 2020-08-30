package mutual.model.enums;

public enum CreditCardType {
    AMERICAN_EXPRESS("American Express"),
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    DISCOVER("Discover");

    private String description;

    CreditCardType(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
