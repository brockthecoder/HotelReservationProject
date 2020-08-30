package client.mutual.model;

import mutual.model.enums.CreditCardType;

public class ReservationPaymentInfo {

    private String cardNumber;

    private String cvv;

    private String zipCode;

    private String cardHolderName;

    private String expirationDate;

    private CreditCardType creditCardType;


    public ReservationPaymentInfo() {
    }

    public ReservationPaymentInfo(String cardNumber, String cvv, String zipCode, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.zipCode = zipCode;
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CreditCardType getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(CreditCardType creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String toString() {
        return String.format("Cardholder: %s%sCard Number: %s%sExpiration Date: %s%sCVV: %s%sZip Code: %s%s",
                cardHolderName, System.lineSeparator(), cardNumber, System.lineSeparator(), expirationDate, System.lineSeparator(),
                cvv, System.lineSeparator(), zipCode, System.lineSeparator());
    }
 }
