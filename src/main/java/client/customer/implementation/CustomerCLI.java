package client.customer.implementation;

import client.mutual.model.builders.ReservationBuilder;
import client.mutual.api.CLI;
import client.customer.api.CustomerRequestHandler;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.CreditCardType;
import client.customer.model.enums.CustomerSelection;
import client.customer.model.HotelDetails;
import mutual.model.enums.ReservationModificationType;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.RoomListing;
import client.mutual.model.builders.CustomerBuilder;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CustomerCLI implements CLI {

    CustomerRequestHandler requestHandler;
    Scanner input;
    DecimalFormat df;

    public CustomerCLI() {
        this.requestHandler = new CustomerRequestHandlerImpl();
        this.input = new Scanner(System.in);
        this.df = new DecimalFormat("0.00");
    }

    @Override
    public void initialize() {
        System.out.println("Welcome to HotelFinder!" + System.lineSeparator());
        while (true) {
            switch (getStartingOption()) {

                case FIND_ROOM:
                    RoomAvailabilityQuery query = getRoomQuery();
                    Map<HotelDetails, List<RoomListing>> hotelAvailability = requestHandler.getHotelAvailability(query);
                    getHotelRoomSelection(query, hotelAvailability);
                    break;
                case MANAGE_EXISTING:
                    manageReservation();
                    break;
                case EXIT:
                    requestHandler.closeConnection();
                    return;
            }
        }
    }

    private CustomerSelection getStartingOption() {

        System.out.println(System.lineSeparator() + "Please select from one of the following options:" );

        System.out.println("1) Find a room");
        System.out.println("2) Manage an existing reservation");
        System.out.println("3) Exit");

        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            if (input.hasNextLine()) {
                try {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            return CustomerSelection.FIND_ROOM;
                        case 2:
                            return CustomerSelection.MANAGE_EXISTING;
                        case 3:
                            return CustomerSelection.EXIT;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
    }

    private RoomAvailabilityQuery getRoomQuery() {

        RoomAvailabilityQuery query = new RoomAvailabilityQuery();
        System.out.println(System.lineSeparator() + "Find a room: " + System.lineSeparator());
        System.out.print("Destination city: ");
        if (input.hasNextLine()) {
            query.setCity(input.nextLine().toLowerCase());
        }
        System.out.print("Check-In Date: (yyyy-mm-dd): ");
        boolean invalid = true;
        while (invalid) {
            if (input.hasNextLine()) {
                try {
                    query.setCheckInDate(LocalDate.parse(input.nextLine()));
                    if (query.getCheckInDate().compareTo(LocalDate.now()) < 0) {
                        System.out.print("Invalid entry, try again: ");
                    }
                    else {
                        invalid = false;
                    }
                } catch (DateTimeParseException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
        invalid = true;
        System.out.print("Check-Out Date: (yyyy-mm-dd): ");
        while (invalid) {
            if (input.hasNextLine()) {
                try {
                    query.setCheckOutDate(LocalDate.parse(input.nextLine()));
                    if (query.getCheckOutDate().compareTo(LocalDate.now()) < 0 || query.getCheckOutDate().compareTo(query.getCheckInDate()) < 1) {
                        System.out.print("Invalid entry, try again: ");
                    }
                    else {
                        invalid = false;
                    }
                } catch (DateTimeParseException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
        invalid = true;
        System.out.print("How many people: ");
        while (invalid) {
            if (input.hasNextLine()) {
                try {
                    query.setNumOfPeople(Integer.parseInt(input.nextLine()));
                    invalid = false;
                } catch (NumberFormatException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
        System.out.println("Find availability...");
        return query;
    }

    private void manageReservation() {

        System.out.println(System.lineSeparator() + "Manage an existing reservation: ");
        System.out.println(System.lineSeparator() + "How would you like to locate your reservation: ");
        System.out.println("1) By Reservation Number");
        System.out.println("2) By Email Address");
        System.out.println("3) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        List<CustomerReservation> matchingReservations;
        boolean invalid = true;
        while (invalid) {
            if (input.hasNextLine()) {
                try {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1: {
                            CustomerReservationQuery customerReservationQuery = getReservationId();
                            matchingReservations = requestHandler.getReservationById(customerReservationQuery);
                            if (matchingReservations == null || matchingReservations.size() == 0) {
                                matchingReservations = getReservationAfterFailure();
                                if (matchingReservations == null || matchingReservations.size() == 0) {
                                    return;
                                }
                            }
                            getReservationModification(matchingReservations.get(0));
                            invalid = false;
                            break;
                        }
                        case 2: {
                            CustomerReservationQuery customerReservationQuery = getReservationEmail();
                            matchingReservations = requestHandler.getReservationByEmail(customerReservationQuery);
                            if (matchingReservations == null || matchingReservations.size() == 0) {
                                matchingReservations = getReservationAfterFailure();
                                if (matchingReservations == null || matchingReservations.size() == 0) {
                                    return;
                                }
                            }
                            CustomerReservation reservationSelection = getReservationSelection(matchingReservations);
                            if (reservationSelection == null) {
                                return;
                            }
                            getReservationModification(reservationSelection);
                            invalid = false;
                            break;
                        }
                        case 3:
                            return;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
    }

    private CustomerReservation getReservationSelection(List<CustomerReservation> matchingReservations) {
        if (matchingReservations.size() == 1) {
            return matchingReservations.get(0);
        }
        System.out.println(System.lineSeparator() + "Found the following reservations: ");
        int counter = 1;
        for (CustomerReservation reservation : matchingReservations) {
            System.out.println(counter + ") " + reservation.getCustomer().getFullName());
            System.out.println("   Hotel: " + reservation.getHotel().getName());
            System.out.println("   Check-In date: " + reservation.getCheckInDate().toString());
            System.out.println("   Check-Out date: " + reservation.getCheckOutDate().toString());
            counter++;
        }
        System.out.println(counter + ") Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        int selection = getNumericInput(1, counter);
        if (selection == counter) {
            return null;
        }
        return matchingReservations.get(selection - 1);
    }

    private int getNumericInput(int lowerBound, long upperBound) {

        while (true) {
            try {
                if (input.hasNextLine()) {
                    int digit = Integer.parseInt(input.nextLine());
                    if (digit >= lowerBound && digit <= upperBound) {
                        return digit;
                    }
                    else {
                        System.out.print("Invalid entry, try again: ");
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid entry, try again: ");
            }
        }
    }

    private List<CustomerReservation> getReservationAfterFailure() {
        List<CustomerReservation> matchingReservations = null;

        while (matchingReservations == null || matchingReservations.size() == 0) {
            System.out.println(System.lineSeparator() + "Unable to locate reservation");
            System.out.println("Options:");
            System.out.println("1) Try again by reservation number");
            System.out.println("2) Try again by email");
            System.out.println("3) Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1: {
                                CustomerReservationQuery customerReservationQuery = getReservationId();
                                matchingReservations = requestHandler.getReservationById(customerReservationQuery);
                                valid = true;
                                break;
                            }
                            case 2: {
                                CustomerReservationQuery customerReservationQuery = getReservationEmail();
                                matchingReservations = requestHandler.getReservationByEmail(customerReservationQuery);
                                valid = true;
                                break;
                            }
                            case 3:
                                return null;
                            default:
                                System.out.print("Invalid choice try again: ");
                        }
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
        return matchingReservations;
    }

    private void getReservationModification(CustomerReservation reservation) {
        while (true) {

            System.out.println(System.lineSeparator() + "Here are your reservation details: " + System.lineSeparator());
            System.out.println(reservation);
            System.out.println(System.lineSeparator() + "What would you like to do: " + System.lineSeparator());
            System.out.println("1) Cancel reservation");
            System.out.println("2) Change payment info");
            System.out.println("3) Change Check-In date");
            System.out.println("4) Change Check-Out date");
            System.out.println("5) Go back");
            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1:
                                confirmCancellation(reservation);
                                return;
                            case 2:
                                getNewPaymentInfo(reservation);
                                valid = true;
                                break;
                            case 3:
                                changeCheckInDate(reservation);
                                valid = true;
                                break;
                            case 4:
                                changeCheckOutDate(reservation);
                                valid = true;
                                break;
                            case 5:
                                return;
                            default:
                                System.out.print("Invalid choice try again: ");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
    }

    private void changeCheckOutDate(CustomerReservation reservation) {
        LocalDate oldCheckOutDate = reservation.getCheckOutDate();
        boolean modificationWasSuccessful = false;
        System.out.println(System.lineSeparator() + "Change Check-Out Date: " + System.lineSeparator());
        System.out.print("New Check-Out Date (yyyy-mm-dd): ");
        boolean valid = false;
        double oldTotal = reservation.getTotal();
        double oldNightlyRate = reservation.getRoomListing().getNightlyRate();
        while (!valid) {
            try {
                if (input.hasNextLine()) {
                    reservation.setCheckOutDate(LocalDate.parse(input.nextLine()));
                }
                if (reservation.getCheckOutDate().compareTo(LocalDate.now()) < 0 || reservation.getCheckOutDate().compareTo(reservation.getCheckInDate()) < 1) {
                    System.out.print("Invalid entry, try again: ");
                }
                else if (reservation.getCheckOutDate().compareTo(oldCheckOutDate) == 0) {
                    System.out.print("The date you entered is the same as is on the reservation, try again: ");
                }
                else {
                    valid = true;
                }
            }
            catch (DateTimeParseException e) {
                System.out.print("Invalid entry try again: ");
            }
        }
        if (oldCheckOutDate.compareTo(reservation.getCheckOutDate()) < 0) {
            List<LocalDate> dates = LongStream.range(oldCheckOutDate.toEpochDay(), reservation.getCheckOutDate().toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
            double priceToChangeDate = Double.parseDouble(df.format(requestHandler.getPriceForExtraNights(dates, reservation)));
            if (priceToChangeDate == -1) {
                System.out.println("Sorry, but there isn't availability for the days you're looking for");
                return;
            }
            System.out.println("It will cost $" + priceToChangeDate + " to change your Check-Out Date");
            System.out.println("Would you like to proceed:");
            System.out.println("1) Yes");
            System.out.println("2) Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
            valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1:
                                reservation.setTotal(reservation.getTotal() + priceToChangeDate);
                                long numOfNights = reservation.getCheckOutDate().toEpochDay() - reservation.getCheckInDate().toEpochDay();
                                reservation.getRoomListing().setNightlyRate(Double.parseDouble(df.format(reservation.getTotal() / numOfNights)));
                                modificationWasSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.ADD_NIGHTS);
                                valid = true;
                                break;
                            case 2:
                                return;
                            default:
                                System.out.print("Invalid choice, try again: ");
                        }
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice, try again: ");
                }
            }
        }
        else {
            System.out.println("Please confirm: ");
            System.out.println("  Old check-out date: " + oldCheckOutDate);
            System.out.println("  New check-out date: " + reservation.getCheckOutDate());
            if (getChoice("Confirm", "Go back", null) == 2) {
                reservation.setCheckOutDate(oldCheckOutDate);
                return;
            }
            long numOfNights = oldCheckOutDate.toEpochDay() - reservation.getCheckOutDate().toEpochDay();
            reservation.setTotal(reservation.getTotal() - (reservation.getRoomListing().getNightlyRate() * numOfNights));
            modificationWasSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.REMOVE_NIGHTS);
        }
        if (modificationWasSuccessful) {
            System.out.println(System.lineSeparator() + "Your Check-Out Date was successfully modified: ");
            System.out.println("   check-in date: " + reservation.getCheckInDate());
            System.out.println("   check-out date: " + reservation.getCheckOutDate());
            System.out.println("   New Total: " + reservation.getTotal() + System.lineSeparator());
        }
        else {
            reservation.getRoomListing().setNightlyRate(oldNightlyRate);
            reservation.setTotal(oldTotal);
            reservation.setCheckOutDate(oldCheckOutDate);
            System.out.println("Sorry there was a problem modifying your Check-Out Date, please try again later!");
        }
    }

    private void changeCheckInDate(CustomerReservation reservation) {
        if ((reservation.getCheckInDate().compareTo(LocalDate.now()) == 0) && (reservation.getCheckOutDate().toEpochDay() - reservation.getCheckInDate().toEpochDay() == 1)) {
            System.out.println(System.lineSeparator() + "Unable to change the check-in date, either cancel the reservation or change the check-out date first");
            return;
        }
        LocalDate oldCheckInDate = reservation.getCheckInDate();
        boolean modificationWasSuccessful = false;
        double oldTotal = reservation.getTotal();
        double oldNightlyRate = reservation.getRoomListing().getNightlyRate();
        System.out.println(System.lineSeparator() + "Change Check-In Date: " + System.lineSeparator());
        System.out.print("New Check-In Date (yyyy-mm-dd): ");
        boolean valid = false;
        while (!valid) {
            try {
                if (input.hasNextLine()) {
                    reservation.setCheckInDate(LocalDate.parse(input.nextLine()));
                }
                if (reservation.getCheckInDate().compareTo(LocalDate.now()) < 0 || reservation.getCheckInDate().compareTo(reservation.getCheckOutDate()) > -1) {
                    System.out.print("Invalid entry, try again: ");
                }
                else if (reservation.getCheckInDate().compareTo(oldCheckInDate) == 0) {
                    System.out.print("The date you entered is the same as is on the reservation, try again: ");
                }
                else {
                    valid = true;
                }
            }
            catch (DateTimeParseException e) {
                System.out.print("Invalid entry try again: ");
            }
        }
        boolean modificationSuccessful = false;
        if (oldCheckInDate.compareTo(reservation.getCheckInDate()) > 0) {
            List<LocalDate> dates = LongStream.range(reservation.getCheckInDate().toEpochDay(), oldCheckInDate.toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
            double priceToChangeDate = Double.parseDouble(df.format(requestHandler.getPriceForExtraNights(dates, reservation)));
            if (priceToChangeDate == -1) {
                System.out.println("Sorry, but there isn't availability for the days you're looking for");
                return;
            }
            System.out.println("It will cost $" + priceToChangeDate + " to change your Check-In Date");
            System.out.println("Would you like to proceed: " + System.lineSeparator());
            System.out.println("1) Yes");
            System.out.println("2) Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
            valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1:
                                reservation.setTotal(reservation.getTotal() + priceToChangeDate);
                                long numOfNights = reservation.getCheckOutDate().toEpochDay() - reservation.getCheckInDate().toEpochDay();
                                reservation.getRoomListing().setNightlyRate(Double.parseDouble(df.format(reservation.getTotal() / numOfNights)));
                                modificationSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.ADD_NIGHTS);
                                valid = true;
                                break;
                            case 2:
                                return;
                            default:
                                System.out.print("Invalid choice try again: ");
                        }
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
        else {
            System.out.println("Please confirm: ");
            System.out.println("  Old check-in date: " + oldCheckInDate);
            System.out.println("  New check-in date: " + reservation.getCheckInDate());
            if (getChoice("Confirm", "Go back", null) == 2) {
                reservation.setCheckInDate(oldCheckInDate);
                return;
            }
            long numOfNights = reservation.getCheckInDate().toEpochDay() - oldCheckInDate.toEpochDay();
            reservation.setTotal(reservation.getTotal() - (reservation.getRoomListing().getNightlyRate() * numOfNights));
            modificationSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.REMOVE_NIGHTS);
        }
        if (modificationSuccessful) {
            System.out.println(System.lineSeparator() + "Your Check-In Date was successfully modified: ");
            System.out.println("   check-in date: " + reservation.getCheckInDate());
            System.out.println("   check-out date: " + reservation.getCheckOutDate());
            System.out.println("   New Total: " + reservation.getTotal() + System.lineSeparator());
        }
        else {
            reservation.getRoomListing().setNightlyRate(oldNightlyRate);
            reservation.setTotal(oldTotal);
            reservation.setCheckInDate(oldCheckInDate);
            System.out.println("Sorry there was a problem modifying your Check-In Date, please try again! ");
        }
    }

    private int getChoice(String option1, String option2, String option3) {

        boolean threeOptions = option3 != null;

        System.out.println("1) " + option1);
        System.out.println("2) " + option2);
        if (threeOptions) {
            System.out.println("3) " + option3);
        }
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            try {
                if (input.hasNextLine()) {
                    int selection = Integer.parseInt(input.nextLine());
                    if (threeOptions) {
                        if (selection < 1 || selection > 3) {
                            System.out.print("Invalid choice, try again: ");
                        } else {
                            return selection;
                        }
                    } else {
                        if (selection < 1 || selection > 2) {
                            System.out.print("Invalid choice, try again: ");
                        } else {
                            return selection;
                        }
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid choice, try again: ");
            }
        }
    }

    private void getNewPaymentInfo(CustomerReservation reservation) {

        System.out.println(System.lineSeparator() + "Current Payment info:" + System.lineSeparator());
        System.out.println(reservation.getPaymentInfo().toString());

        System.out.println("Are you sure you would like to change something: ");
        System.out.println("1) Yes");
        System.out.println("2) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            collectNewPaymentInfo(reservation);
                            return;
                        case 2:
                            return;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid choice try again: ");
            }
        }
    }

    private void collectNewPaymentInfo(CustomerReservation reservation) {
        System.out.println();
        ReservationPaymentInfo paymentInfo = new ReservationPaymentInfo();
        System.out.print("Cardholder's Name: ");
        if (input.hasNextLine()) {
            paymentInfo.setCardHolderName(input.nextLine());
        }
        System.out.print("Card Type: ");
        paymentInfo.setCreditCardType(getCreditCardType());
        System.out.print("Card Number: ");
        if (input.hasNextLine()) {
            paymentInfo.setCardNumber(input.nextLine());
        }
        System.out.print("Expiration Date: (mm-yy): ");
        if (input.hasNextLine()) {
            paymentInfo.setExpirationDate(input.nextLine());
        }
        System.out.print("CVV: ");
        if (input.hasNextLine()) {
            paymentInfo.setCvv(input.nextLine());
        }
        System.out.print("zip code: ");
        if (input.hasNextLine()) {
            paymentInfo.setZipCode(input.nextLine());
        }
        ReservationPaymentInfo oldPaymentInfo = reservation.getPaymentInfo();
        reservation.setPaymentInfo(paymentInfo);

        System.out.println(System.lineSeparator() + "Confirm Details: ");
        System.out.println(reservation.getPaymentInfo().toString() + System.lineSeparator());
        System.out.println("1) Confirm");
        System.out.println("2) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        boolean modificationSuccessful = false;
        while (true) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            System.out.println("Updating Payment info now...");
                            modificationSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.UPDATE_PAYMENT_INFO);
                            if (modificationSuccessful) {
                                System.out.println("The payment has been successfully updated!" + System.lineSeparator());
                            } else {
                                System.out.println("Sorry there was an issue updating your payment info, please try later! ");
                                reservation.setPaymentInfo(oldPaymentInfo);
                            }
                            return;
                        case 2:
                            reservation.setPaymentInfo(oldPaymentInfo);
                            return;
                        default:
                            System.out.print("Invalid entry try again: ");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid entry try again: ");
            }
        }
    }

    private void confirmCancellation(CustomerReservation reservation) {
        System.out.println(System.lineSeparator() + "Cancel Reservation:" + System.lineSeparator());
        System.out.println("Are you sure want to cancel the following reservation: ");
        System.out.println(reservation + System.lineSeparator());
        System.out.println("1) Cancel Reservation");
        System.out.println("2) Go Back");
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                           boolean modificationWasSuccessful = requestHandler.modifyReservation(reservation, ReservationModificationType.CANCEL);
                           if (modificationWasSuccessful) {
                               System.out.println("Your reservation has been successfully canceled.");
                               System.out.println("We hope to see you book again soon!");
                           }
                           else {
                               System.out.println("There was a problem cancelling your reservation, please try again later." + System.lineSeparator());
                           }
                           return;
                        case 2:
                            return;
                        default:
                            System.out.print("Invalid entry try again: ");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid entry try again: ");
            }
        }
    }

    private CustomerReservationQuery getReservationId() {

        System.out.print("Please enter your reservation number: ");
        while (true) {
            if (input.hasNextLine()) {
                try {
                    CustomerReservationQuery query =  new CustomerReservationQuery();
                    query.setReservationId(Long.parseLong(input.nextLine()));
                    System.out.print("Please enter the last name on the reservation: ");
                    if (input.hasNextLine()) {
                        query.setLastName(input.nextLine().toLowerCase());
                    }
                    return query;
                } catch (NumberFormatException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
    }

    private CustomerReservationQuery getReservationEmail() {
        CustomerReservationQuery query = new CustomerReservationQuery();
        System.out.print("Please enter the email associated with the reservation: ");
        while (true) {
            if (input.hasNextLine()) {
                String email = input.nextLine().toLowerCase();
                if (email.contains("@")) {
                    query.setEmail(email);
                    System.out.print("Please enter the last name on the reservation: ");
                    if (input.hasNextLine()) {
                        query.setLastName(input.nextLine().toLowerCase());
                    }
                    return query;
                }
                else {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
    }

    private void getHotelRoomSelection(RoomAvailabilityQuery query , Map<HotelDetails, List<RoomListing>> availability) {
        if (availability == null || availability.size() == 0) {
            System.out.println(System.lineSeparator() + "Sorry but there aren't any hotels available in " + query.getCity() + " from " + query.getCheckInDate() + " to " + query.getCheckOutDate() + " for " + query.getNumOfPeople() + " people.");
            System.out.println("Please try again with different criteria or check back later!" + System.lineSeparator());
            return;
        }
        String message = (availability.size() == 1) ? " Property found: " : " Properties Found:";
        System.out.println(System.lineSeparator() + availability.size() + message + System.lineSeparator());
        int counter = 1;
        StringBuilder sb = new StringBuilder(100);

        HotelDetails[] hotels = availability.keySet().toArray(new HotelDetails[0]);

        for (HotelDetails hotel : hotels) {
            sb.append(counter).append(") ").append(hotel.getName()).append(System.lineSeparator())
                    .append("    description: ").append(hotel.descriptionToString()).append(System.lineSeparator());
            counter++;
        }
        sb.append(counter).append(") Cancel").append(System.lineSeparator());

        boolean foundHotelRoom = false;
        while(!foundHotelRoom) {

            System.out.println(sb);

            System.out.print(System.lineSeparator() + "Selection: ");
            int hotelSelection;
            while (true) {
                if (input.hasNextLine()) {
                    try {
                        hotelSelection = Integer.parseInt(input.nextLine());
                        if (hotelSelection == counter) {
                            return;
                        }
                        else if (hotelSelection < 1 || hotelSelection > counter) {
                            System.out.print("Invalid choice, try again: ");
                        }
                        else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("Invalid choice, try again: ");
                    }
                }
            }
            HotelDetails hotelMapKey = hotels[hotelSelection - 1];

            foundHotelRoom = showFullDetailsOfHotel(query, hotelMapKey, availability.get(hotelMapKey));
        }
    }

    private boolean showFullDetailsOfHotel(RoomAvailabilityQuery query, HotelDetails hotel, List<RoomListing> roomCategories) {

        System.out.println(System.lineSeparator() + hotel.getName() + ":" + System.lineSeparator());
        System.out.println("1) View hotel details");
        System.out.println("2) View rooms");
        System.out.println("3) go back");
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            if (input.hasNextLine()) {
                try {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            System.out.println(System.lineSeparator());
                            System.out.println(hotel);
                            System.out.println(System.lineSeparator());
                            System.out.println("1) View Rooms");
                            System.out.println("2) Go back");
                            System.out.print(System.lineSeparator() + "Selection: ");
                            boolean valid = false;
                            while (!valid) {
                                if (input.hasNextLine()) {
                                    try {
                                        switch (Integer.parseInt(input.nextLine())) {
                                            case 1:
                                                valid = true;
                                                break;
                                            case 2:
                                                return false;
                                            default:
                                                System.out.print("Invalid choice try again: ");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.print("Invalid choice try again: ");
                                    }
                                }
                            }
                        case 2:
                            return showAvailableRooms(query, hotel, roomCategories);
                        case 3:
                            return false;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
            else {
                return false;
            }
        }
    }

    private boolean showAvailableRooms(RoomAvailabilityQuery query, HotelDetails hotel, List<RoomListing> roomCategories) {

        boolean roomFound = false;
        int selection = -1;
        long numOfNights = (query.getCheckOutDate().toEpochDay() - query.getCheckInDate().toEpochDay());

        while (!roomFound) {
            System.out.println("Select a room to book at " + hotel.getName() + ":" + System.lineSeparator());

            int counter = 1;
            for (RoomListing room : roomCategories) {
                System.out.print(counter + ") " + room);
                System.out.println("Total: " + df.format(room.getNightlyRate() * numOfNights) + System.lineSeparator());
                counter++;
            }
            System.out.println(counter + ") Go back");

            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        selection = Integer.parseInt(input.nextLine());
                    }
                    if (selection == -1 || selection == counter) {
                        return false;
                    }
                    if (selection > counter) {
                        System.out.print("Invalid choice try again: ");
                    } else {
                        valid = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
            System.out.println(System.lineSeparator() + "Please confirm your room selection: " + System.lineSeparator());
            System.out.println(roomCategories.get(selection - 1).toString());
            if (getSelectionConfirmation()) {
                roomFound = getReservationDetails(query, hotel, roomCategories.get(selection - 1));
            }
        }
        return true;
    }

    private boolean getSelectionConfirmation() {

        System.out.println(System.lineSeparator());
        System.out.println("1) Confirm");
        System.out.println("2) Go back");
        System.out.print(System.lineSeparator() + "Selection: ");
        while(true) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            return true;
                        case 2:
                            return false;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid choice try again: ");
            }
        }
    }

    private boolean getReservationDetails(RoomAvailabilityQuery query, HotelDetails hotel, RoomListing roomListing) {
        while (true) {
            System.out.println(System.lineSeparator() + "Guest Information: " + System.lineSeparator());
            CustomerBuilder cb = new CustomerBuilder();
            ReservationBuilder rb = new ReservationBuilder();
            rb.withCheckInDate(query.getCheckInDate());
            rb.withCheckOutDate(query.getCheckOutDate());
            rb.inRoomCategory(roomListing);
            rb.at(hotel);
            rb.withTotal(roomListing.getNightlyRate() * (query.getCheckOutDate().toEpochDay() - query.getCheckInDate().toEpochDay()));
            System.out.print("First Name: ");
            if (input.hasNextLine()) {
                cb.withFirstName(input.nextLine().toLowerCase());
            }
            System.out.print("Last name: ");
            if (input.hasNextLine()) {
                cb.withLastName(input.nextLine().toLowerCase());
            }
            System.out.print("Email address: ");
            if (input.hasNextLine()) {
                cb.withEmail(input.nextLine().toLowerCase());
            }
            System.out.print("Phone number: ");
            if (input.hasNextLine()) {
                String phoneNumber = input.nextLine();
                while (!isValidPhoneNumber(phoneNumber)) {
                    System.out.print("Invalid entry please try again (000) 000-000: ");
                    phoneNumber = input.nextLine();
                }
                cb.withPhoneNumber(phoneNumber);
            }
            System.out.print("Street Address: ");
            if (input.hasNextLine()) {
                cb.withStreetAddress(input.nextLine());
            }
            System.out.print("City: ");
            if (input.hasNextLine()) {
                cb.withCity(input.nextLine());
            }
            System.out.print("State: ");
            if (input.hasNextLine()) {
                cb.withState(input.nextLine());
            }
            System.out.println("Next, enter payment info: " + System.lineSeparator());
            System.out.print("Cardholder's Name: ");
            if (input.hasNextLine()) {
                rb.withCreditCardHolderNameOf(input.nextLine());
            }
            System.out.print("Card Type: ");
            rb.withCreditCardType(getCreditCardType());
            System.out.print("Card Number: ");
            if (input.hasNextLine()) {
                rb.withCreditCard(input.nextLine());
            }
            System.out.print("Expiration Date: (mm-yy): ");
            if (input.hasNextLine()) {
                rb.withCreditCardExpirationDateOf(input.nextLine());
            }
            System.out.print("CVV: ");
            if (input.hasNextLine()) {
                rb.withCVV(input.nextLine());
            }
            System.out.print("Zip code: ");
            if (input.hasNextLine()) {
                rb.withCreditCardZipCode(input.nextLine());
            }
            rb.byCustomer(cb.build());
            CustomerReservation reservation = rb.build();

            System.out.println(System.lineSeparator() + "Confirm Details: ");
            System.out.println(reservation);
            System.out.println(System.lineSeparator());
            System.out.println("1) Confirm");
            System.out.println("2) Change Information");
            System.out.println("3) Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1:
                                long reservationId = requestHandler.newReservation(reservation);
                                if (reservationId == -1) {
                                    System.out.println("Sorry there was a problem completing the reservation");
                                    System.out.println("Please try again!");
                                    return false;
                                }
                                System.out.println(System.lineSeparator() + "Your reservation is confirmed, thank you for booking with us!");
                                System.out.println("Your reservation number is: " + reservationId);
                                System.out.println(System.lineSeparator() + "See you soon!");
                                return true;
                            case 2:
                                valid = true;
                                break;
                            case 3:
                                return false;
                            default:
                                System.out.print("Invalid entry try again: ");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Invalid entry try again: ");
                }
            }
        }
    }

    private CreditCardType getCreditCardType () {
        int counter = 1;
        CreditCardType[] cardTypes = CreditCardType.values();
        System.out.println();
        for (CreditCardType cardType : cardTypes) {
            System.out.println(counter + ") " + cardType.toString());
            counter++;
        }
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            try {
                if (input.hasNextLine()) {
                    int selection = Integer.parseInt(input.nextLine());
                    if (selection < 1 || selection > cardTypes.length) {
                        System.out.print("Invalid choice try again: ");
                    }
                    else {
                        return cardTypes[selection - 1];
                    }
                }
                else {
                    return cardTypes[0];
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid choice try again: ");
            }
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\([1-9]\\d{2}\\)\\s\\d{3}-\\d{4}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
