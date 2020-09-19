package client.management.implementation;

import client.management.model.*;
import mutual.model.enums.HotelPropertyModificationType;
import mutual.model.enums.ReservationIdentifierType;
import mutual.model.enums.UpdateAction;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.enums.CreditCardType;
import client.mutual.model.builders.CustomerBuilder;
import client.management.api.ManagementRequestHandler;
import client.mutual.api.CLI;
import client.management.model.builders.HotelBuilder;
import client.mutual.model.builders.ReservationBuilder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ManagementCLI implements CLI {

    Scanner input;
    ManagementRequestHandler requestHandler;
    Pattern phoneNumberPattern;

    public ManagementCLI() {
        input = new Scanner(System.in);
        requestHandler = new ManagementRequestHandlerImpl();
    }

    @Override
    public void initialize() {
        System.out.println("Welcome to HotelFinder Management Console!" + System.lineSeparator());
        boolean valid = false;
        ManagementAccount account;
        System.out.println("Please select from one of the following options:");
        System.out.println("1) Sign in");
        System.out.println("2) Create an account");
        System.out.println("3) Exit");
        System.out.print(System.lineSeparator() + "Selection: ");
        while (!valid) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            account = attemptSignIn();
                            if (account != null) {
                                showAccountOptions(account);
                            }
                            valid = true;
                            break;
                        case 2:
                            account = createAccount();
                            if (account != null) {
                                showAccountOptions(account);
                            }
                            valid = true;
                            break;
                        case 3:
                            valid = true;
                            break;
                        default:
                            System.out.print("Invalid choice try again: ");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid choice try again: ");
            }
        }
        requestHandler.closeConnection();
    }

    private ManagementAccount createAccount() {
        ManagementAccount account = new ManagementAccount();

        System.out.println(System.lineSeparator() + "Create Account:" + System.lineSeparator());
        boolean valid = false;
        while (true) {
            System.out.print("Please enter your email address: ");
            while (!valid) {
                if (input.hasNextLine()) {
                    account.setEmail(input.nextLine().toLowerCase());
                }
                if (account.getEmail().contains("@")) {
                    valid = true;
                }
                else {
                    System.out.print("Invalid entry, pleas try again: ");
                }
            }
            valid = false;
            while (!valid) {
                System.out.print("Please enter a password: ");
                if (input.hasNextLine()) {
                    account.setPassword(input.nextLine());
                }
                System.out.print("Please re-enter password: ");
                if (input.hasNextLine()) {
                    if (input.nextLine().equals(account.getPassword())) {
                        valid = true;
                    }
                    else {
                        System.out.println("Passwords do not match, please try again: ");
                    }
                }
            }
            System.out.println();
            long accountId = requestHandler.createNewAccount(account);
            if (accountId == -1) {
                System.out.println(System.lineSeparator() + "Error: Account with the provided email already exists" + System.lineSeparator());
                System.out.println("Would you like to:");
                System.out.println("1) Sign in");
                System.out.println("2) Use different email");
                System.out.println("3) Exit");
                System.out.print(System.lineSeparator() + "Selection: ");
                valid = false;
                while (!valid) {
                    try {
                        if (input.hasNextLine()) {
                            switch (Integer.parseInt(input.nextLine())) {
                                case 1:
                                    return attemptSignIn();
                                case 2:
                                    valid = true;
                                    break;
                                case 3:
                                    return null;
                                default:
                                    System.out.print("Invalid choice, try again: ");
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.print("Invalid choice, try again: ");
                    }
                }
            } else {
                account.setId(accountId);
                return account;
            }
        }
    }

    private ManagementAccount attemptSignIn() {
        ManagementAccount account = new ManagementAccount();
        System.out.println(System.lineSeparator() + "Sign In:" + System.lineSeparator());
        while (true) {
            System.out.print("Enter your email address: ");
            if (input.hasNextLine()) {
                account.setEmail(input.nextLine().toLowerCase());
            }
            System.out.print("Enter your password: ");
            if (input.hasNextLine()) {
                account.setPassword(input.nextLine());
            }
            ManagementAccount fullyDetailedAccount = requestHandler.signIn(account);
            if (fullyDetailedAccount == null) {
                System.out.println("Sign in attempt failed");
                System.out.println("What would you like to do:");
                System.out.println("1) Try a different combination");
                System.out.println("2) Create an account");
                System.out.println("3) Exit");
                System.out.print(System.lineSeparator() + "Selection: ");
                boolean valid = false;
                while (!valid) {
                    try {
                        if (input.hasNextLine()) {
                            switch (Integer.parseInt(input.nextLine())) {
                                case 1:
                                    valid = true;
                                    break;
                                case 2:
                                    return createAccount();
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
            else {
                return fullyDetailedAccount;
            }
        }
    }

    private void showAccountOptions(ManagementAccount account) {
        if (account == null) {
            return;
        }

        while (true) {
            System.out.println(System.lineSeparator() + "Your Account:" + System.lineSeparator());

            int counter = 1;
            if (account.getHotels() != null) {
                for (ManagementHotelDetails hotel : account.getHotels()) {
                    System.out.println(counter + ") " + hotel.getName());
                    counter++;
                }
            }
            System.out.println(counter++ + ") Add a new hotel");
            System.out.println(counter + ") Exit");
            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        int selection = Integer.parseInt(input.nextLine());
                        if (selection < 1 || selection > counter) {
                            System.out.print("Invalid choice try again: ");
                        }
                        else if (selection == counter) {
                            return;
                        }
                        else if (selection == counter - 1) {
                            addNewHotel(account);
                            valid = true;
                        }
                        else {
                            ManagementHotelDetails hotelDetails = account.getHotels().get(selection - 1);
                            if (hotelDetailsNotComplete(hotelDetails)){
                                hotelDetails.setRoomCategories(requestHandler.getRoomCategories(hotelDetails));
                                hotelDetails.setOperatingHours(requestHandler.getOperatingHours(hotelDetails.getId()));
                                hotelDetails.setAmenities(requestHandler.getAmenities(hotelDetails.getId()));
                            }
                            manageHotel(account.getHotels().get(selection - 1), account);
                            valid = true;
                        }
                    }
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid choice try again: ");
                }
            }
        }
    }

    private boolean hotelDetailsNotComplete(ManagementHotelDetails hotel) {
        if (hotel.getRoomCategories() == null || hotel.getRoomCategories().size() == 0)
            return true;
        if (hotel.getAmenities() == null || hotel.getAmenities().size() == 0)
            return true;
        if (hotel.getOperatingHours() == null || hotel.getOperatingHours().size() == 0)
            return true;

        return false;
    }


    private void manageHotel(ManagementHotelDetails hotel, ManagementAccount account) {
        while (true) {
            System.out.println(System.lineSeparator() + hotel.getName() + ":" + System.lineSeparator());
            System.out.println("1) View upcoming check-ins");
            System.out.println("2) Create new reservation");
            System.out.println("3) Manage availability");
            System.out.println("4) Search for a reservation");
            System.out.println("5) Manage hotel details");
            System.out.println("6) Delete hotel listing");
            System.out.println("7) Go back");
            System.out.print(System.lineSeparator() + "Selection: ");
            boolean valid = false;
            while (!valid) {
                try {
                    if (input.hasNextLine()) {
                        switch (Integer.parseInt(input.nextLine())) {
                            case 1:
                                viewUpcomingCheckIns(hotel);
                                valid = true;
                                break;
                            case 2:
                                createNewReservation(hotel);
                                valid = true;
                                break;
                            case 3:
                                manageAvailability(hotel);
                                valid = true;
                                break;
                            case 4:
                                searchForReservation(hotel);
                                valid = true;
                                break;
                            case 5:
                                manageHotelDetails(hotel);
                                valid = true;
                                break;
                            case 6:
                                deleteHotel(hotel, account);
                                return;
                            case 7:
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

    private void deleteHotel(ManagementHotelDetails hotel, ManagementAccount account) {

        System.out.println(System.lineSeparator() + "Remove Hotel:" + System.lineSeparator());
        System.out.println("Are you sure you wish to continue, removing the hotel" + System.lineSeparator()
                            + "will delete all reservations associated with hotel" + System.lineSeparator());

        System.out.println("1) Confirm");
        System.out.println("2) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        while (true) {
            try {
                if (input.hasNextLine()) {
                    switch (Integer.parseInt(input.nextLine())) {
                        case 1:
                            boolean wasSuccess = requestHandler.deleteHotel(hotel);
                            if (wasSuccess) {
                                account.getHotels().remove(hotel);
                                System.out.println("Hotel was successfully deleted.");
                            }
                            else {
                                System.out.println("Sorry, there was an issue deleting the hotel, please try again later!");
                            }
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

    private void viewUpcomingCheckIns(ManagementHotelDetails hotel) {

        System.out.println(System.lineSeparator() + "Upcoming Check-Ins:" + System.lineSeparator());
        List<CustomerReservation> reservations = requestHandler.getUpcomingCheckIns(hotel);
        if (reservations == null || reservations.size() == 0) {
            System.out.println("There are no upcoming check-ins at your hotel in the next 7 days" + System.lineSeparator());
        }
        else {
            int counter = 1;
            for (CustomerReservation reservation : reservations) {
                System.out.println(counter + ") Guest: " + reservation.getCustomer().getFullName());
                System.out.println("    Phone number: " + reservation.getCustomer().getPhoneNumber());
                System.out.println("    Room: " + reservation.getRoomListing().getRoomName());
                System.out.println("    Total Price: " + reservation.getTotal());
                System.out.println("    Confirmation number: " + reservation.getId());
            }

            System.out.println(System.lineSeparator());
        }
    }

    private void createNewReservation(ManagementHotelDetails hotel) {
        ManagementRoomQuery query = new ManagementRoomQuery();
        query.setHotel(hotel);
        System.out.println(System.lineSeparator() + "Create new Reservation:" + System.lineSeparator());

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
        showAvailability(query, hotel);
    }

    private void showAvailability(ManagementRoomQuery query, ManagementHotelDetails hotel) {
        List<RoomListing> availability = requestHandler.getAvailability(query);
        long stayDuration = query.getCheckOutDate().toEpochDay() - query.getCheckInDate().toEpochDay();

        if (availability == null || availability.size() == 0) {
            System.out.println("Sorry there is no availability for the specified dates");
        }
        else {
           int counter = 1;
            System.out.println(System.lineSeparator() + availability.size() + ((availability.size() == 1) ?" room was found:" : " rooms were found:"));
           for (RoomListing room : availability) {
               System.out.println(counter + ") " + room.getRoomName());
               System.out.println("    Total Price: " + room.getNightlyRate() * stayDuration);
               counter++;
           }
           System.out.println(counter + ") Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
           int selection = -1;
           while (true) {
               if (input.hasNextLine()) {
                   selection = Integer.parseInt(input.nextLine());
               }
               if (selection < 1 || selection > counter) {
                   System.out.print("Invalid choice, try again: ");
               }
               else if (selection == counter) {
                   return;
               }
               else {
                    CustomerReservation reservation = getReservationDetails(query, availability.get(selection - 1), hotel, stayDuration);
                    long reservationId = requestHandler.newReservation(reservation);
                    if (reservationId == -1) {
                        System.out.println("There was a problem creating the reservation, try again later.");
                    }
                    else {
                        System.out.println("Reservation has been confirmed.");
                        System.out.println("The confirmation number is: " + reservationId);
                    }
                   return;
               }
           }
        }
    }

    private CustomerReservation getReservationDetails(ManagementRoomQuery query, RoomListing roomListing, ManagementHotelDetails hotel, long numOfNights) {
        ReservationBuilder rb = new ReservationBuilder();
        CustomerBuilder cb = new CustomerBuilder();

        System.out.println("Guest Information: ");
        while (true) {
            System.out.print("First Name: ");
            if (input.hasNextLine()) {
                cb.withFirstName(input.nextLine().toLowerCase());
            }
            System.out.print("Last name: ");
            if (input.hasNextLine()) {
                cb.withLastName(input.nextLine().toLowerCase());
            }
            System.out.print("Email address: ");
            while (true) {
                String email = "";
                if (input.hasNextLine()) {
                    email = input.nextLine().toLowerCase();
                }
                if (email.contains("@")) {
                    cb.withEmail(email);
                    break;
                }
                System.out.print("Invalid entry, please try again: ");
            }
            System.out.print("Phone number: ");
            if (input.hasNextLine()) {
                cb.withPhoneNumber(input.nextLine());
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
            rb.withCreditCard(input.nextLine());
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
            rb.withCheckInDate(query.getCheckInDate());
            rb.withCheckOutDate(query.getCheckOutDate());
            rb.at(hotel);
            rb.inRoomCategory(roomListing);
            rb.withTotal(roomListing.getNightlyRate() * numOfNights);
            CustomerReservation reservation = rb.build();
            System.out.println(System.lineSeparator() + "Confirm Details: ");
            System.out.println(reservation);
            System.out.println("Customer Information:");
            System.out.println(reservation.getCustomer());
            System.out.println("Payment Info:");
            System.out.println(reservation.getPaymentInfo());
            int choice = getChoice("Confirm", "Change Something", "Cancel");
            switch (choice) {
                case 1:
                    return reservation;
                case 2:
                    break;
                default:
                    return null;
            }
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

    private CreditCardType getCreditCardType () {
        System.out.println();
        int counter = 1;
        CreditCardType[] cardTypes = CreditCardType.values();
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

    private void manageAvailability(ManagementHotelDetails hotel) {
        if (hotel.getRoomCategories() == null) {
            System.out.println("Sorry but there are not any room categories to manage availability for" + System.lineSeparator());
            return;
        }
        System.out.println(System.lineSeparator() + "Manage Availability:" + System.lineSeparator());
        System.out.println("What would you like to do:");
        System.out.println("1) View availability");
        System.out.println("2) Add availability");
        System.out.println("3) Remove availability");
        System.out.println("4) Go back");
        System.out.print(System.lineSeparator() + "Selection: ");
        switch (getNumericInput(1, 4)) {
            case 1:
                viewAvailability(hotel);
                break;
            case 2:
                addAvailability(hotel);
                break;
            case 3:
                removeAvailability(hotel);
                break;
            default:
                return;
        }
    }

    private void viewAvailability(ManagementHotelDetails hotel) {
        System.out.println(System.lineSeparator() + "View Availability:" + System.lineSeparator());
        if (hotel.getRoomCategories() == null || hotel.getRoomCategories().size() == 0) {
            System.out.println(System.lineSeparator() +"There aren't currently any room categories listed at " + hotel.getName() + System.lineSeparator());
            return;
        }
        RoomCategory roomCategorySelection = getRoomCategorySelection(hotel, "Which room category would you like to view availability for:");
        System.out.println(System.lineSeparator() + "What would you like to do:");
        switch (getChoice("View availability for specific dates", "View all availability", "Cancel")) {
            case 1:
                System.out.print("From which date (YYYY-MM-DD): ");
                LocalDate startDate = getDate();
                System.out.print("Until which date (YYYY-MM-DD): ");
                LocalDate endDate = getDate();
                if (startDate.compareTo(endDate) >= 0) {
                    System.out.println("Invalid dates...using defaults");
                    showRoomCategoryAvailability(LocalDate.now(), LocalDate.now().plus(Period.ofYears(10)), hotel, roomCategorySelection);
                }
                showRoomCategoryAvailability(startDate, endDate, hotel, roomCategorySelection);
                break;
            case 2:
                showRoomCategoryAvailability(LocalDate.now(), LocalDate.now().plus(Period.ofYears(10)), hotel, roomCategorySelection);
                break;
            default:
                return;
        }
    }

    private LocalDate getDate() {
        while (true) {
            if (input.hasNextLine()) {
                try {
                    return LocalDate.parse(input.nextLine());
                }
                catch (DateTimeParseException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
    }

    private Map<LocalDate, AvailabilityListing> showRoomCategoryAvailability(LocalDate startDate, LocalDate endDate, ManagementHotelDetails hotel, RoomCategory roomCategorySelection) {
        Map<LocalDate, AvailabilityListing> availability = requestHandler.getAvailabilityForCategory(hotel, startDate, endDate, roomCategorySelection);
        if (availability == null || availability.size() == 0) {
            System.out.println("There isn't availability for the selected room category" + System.lineSeparator());
            return null;
        }
        System.out.println(System.lineSeparator() + "Here is the availability for " + roomCategorySelection.getName() + ":" + System.lineSeparator());
        for (LocalDate date : availability.keySet()) {
            System.out.println(date);
            System.out.println("   Nightly Rate: $" + availability.get(date).getNightlyRate());
            System.out.println("   Number of rooms: " + availability.get(date).getNumOfRooms());
        }
        System.out.println(System.lineSeparator());
        return availability;
    }

    private RoomCategory getRoomCategorySelection(ManagementHotelDetails hotel, String message) {
        System.out.println(message);
        int counter = 1;
        RoomCategory[] roomCategories = hotel.getRoomCategories().toArray(new RoomCategory[0]);
        for (RoomCategory roomCategory : roomCategories) {
            System.out.println(counter + ") " + roomCategory.getName());
            counter++;
        }
        System.out.println(counter + ") Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        int selection = getNumericInput(1, counter);
        if (selection == counter) {
            return null;
        }
        return roomCategories[selection - 1];
    }

    private void addAvailability(ManagementHotelDetails hotel) {
        System.out.println(System.lineSeparator() + "Add Availability:" + System.lineSeparator());
        while (true) {
            RoomCategory categorySelection = getRoomCategorySelection(hotel, "Which room category would you like to add availability for:");
            if (categorySelection == null) {
                return;
            }
            addAvailabilityForRoomCategory(hotel, categorySelection);
            System.out.println("Would you like to add availability for another room category:");
            if (getChoice("Yes", "No", null) == 2) {
                return;
            }
        }
    }

    private void addAvailabilityForRoomCategory(ManagementHotelDetails hotel, RoomCategory roomCategory) {
        System.out.println("Would you like to view the current availability for the selected room category");
        switch (getChoice("Yes", "No", "Cancel")) {
            case 1:
                System.out.println("Here is the current availability:" + System.lineSeparator());
                showRoomCategoryAvailability(LocalDate.now(), LocalDate.now().plus(Period.ofYears(10)), hotel, roomCategory);
                break;
            case 2:
                break;
            default:
                return;
        }
        List<AvailabilityUpdateTuple> roomsToAdd = new ArrayList<>();
        boolean addMore = true;
        while (addMore) {
            System.out.print("Date to add (YYYY-MM-DD): ");
            LocalDate date = getDate();
            System.out.print("At what rate: ");
            double nightlyRate = getNightlyRate();
            System.out.print("Number of rooms to add for this date: ");
            int numOfRooms = getNumericInput(1, 100000);
            roomsToAdd.add(new AvailabilityUpdateTuple(UpdateAction.ADD, date, numOfRooms, nightlyRate));
            addMore = (getChoice("Add more availability for this room category", "Finish", null) == 1);
        }
        System.out.println("Attempting to add availability...");
        boolean operationWasSuccessful = requestHandler.updateAvailability(hotel, roomCategory, roomsToAdd);
        if (operationWasSuccessful) {
            System.out.println("The availability was successfully updated!" + System.lineSeparator());
            return;
        }
        System.out.println("There was a problem updating the availability, please try again later." + System.lineSeparator());
    }

    private double getNightlyRate() {
        while (true) {
            if (input.hasNextLine()) {
                try {
                    return Double.parseDouble(input.nextLine());
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
    }

    private void removeAvailability(ManagementHotelDetails hotel) {
        System.out.println(System.lineSeparator() + "Remove Availability:" + System.lineSeparator());
        while (true) {
            RoomCategory roomCategorySelection = getRoomCategorySelection(hotel, "Which room category would you like to remove availability for:");
            System.out.println("Here is the availability for " + roomCategorySelection.getName());
            Map<LocalDate, AvailabilityListing> availabilityListingMap = showRoomCategoryAvailability(LocalDate.now(), LocalDate.now().plus(Period.ofYears(10)), hotel, roomCategorySelection);
            if (getChoice("Remove availability", "Cancel", null) == 2) {
                return;
            }
            List<AvailabilityUpdateTuple> datesToRemove = new ArrayList<>();
            System.out.println(System.lineSeparator() + "Please enter the dates you would like to remove availability for:");
            System.out.print("First date (YYYY-MM-DD): ");
            LocalDate date = getDate();
            long roomsAvailable = availabilityListingMap.get(date).getNumOfRooms();
            long numOfRoomsToRemove;
            if (roomsAvailable == 0) {
                System.out.println("There aren't any rooms listed on that date");
            }
            else {
                if (roomsAvailable == 1) {
                    System.out.println("There is currently " + roomsAvailable + " room available on this date");
                }
                else {
                    System.out.println("There are currently " + roomsAvailable + " rooms available on this date");
                }
                System.out.print("Number of rooms to remove: ");
                if (availabilityListingMap.containsKey(date)) {
                    numOfRoomsToRemove = getNumericInput(1, availabilityListingMap.get(date).getNumOfRooms());
                    numOfRoomsToRemove = availabilityListingMap.get(date).getNumOfRooms() - numOfRoomsToRemove;
                    datesToRemove.add(new AvailabilityUpdateTuple(UpdateAction.REMOVE, date, numOfRoomsToRemove));
                } else {
                    getNumericInput(0, 1000);
                }
            }
            while (getChoice("Remove availability for another date", "Finish", null) == 1) {
                System.out.print("Date (YYYY-MM-DD): ");
                date = getDate();
                System.out.print("Number of rooms to remove: ");
                if (availabilityListingMap.containsKey(date)) {
                    numOfRoomsToRemove = getNumericInput(1, availabilityListingMap.get(date).getNumOfRooms());
                    numOfRoomsToRemove = availabilityListingMap.get(date).getNumOfRooms() - numOfRoomsToRemove;
                    AvailabilityUpdateTuple updateTuple = new AvailabilityUpdateTuple(UpdateAction.REMOVE, date, numOfRoomsToRemove);
                    if (!datesToRemove.contains(updateTuple)) {
                        datesToRemove.add(new AvailabilityUpdateTuple(date, numOfRoomsToRemove));
                    }
                }
                else {
                    getNumericInput(0, 1000);
                }
            }
            System.out.println("Attempting to remove availability...");
            boolean operationSuccessful = requestHandler.updateAvailability(hotel, roomCategorySelection, datesToRemove);
            if (operationSuccessful) {
                System.out.println("The selected availability was successfully removed!" + System.lineSeparator());
                return;
            }
            System.out.println("There was a problem removing the availability, please try again later." + System.lineSeparator());
        }
    }

    private void searchForReservation(ManagementHotelDetails hotel) {
        List<CustomerReservation> matchingReservations;
        System.out.println(System.lineSeparator() + "Reservation Search: ");
        while (true) {
            System.out.println(System.lineSeparator() + "How would you like to find the reservation: ");
            System.out.println("1) By reservation number");
            System.out.println("2) By guest name");
            System.out.println("3) By guest email");
            System.out.println("4) Cancel");
            System.out.print(System.lineSeparator() + "Selection: ");
            switch (getNumericInput(1, 4)) {
                case 1:
                    matchingReservations = findReservationById(hotel);
                    break;
                case 2:
                    matchingReservations = findReservationByGuestName(hotel);
                    break;
                case 3:
                    matchingReservations = findReservationByGuestEmail(hotel);
                    break;
                default:
                    return;
            }
            if (matchingReservations == null) {
                System.out.println("Sorry, there were no matching reservations found." + System.lineSeparator());
                System.out.println("Would you like to try again:");
                if (getChoice("Yes", "No", null) == 1) {
                    continue;
                }
                return;
            }
            break;
        }
        String stringToPrint = (matchingReservations.size() == 1) ? " Reservation was Found:" : " Reservations were Found:";
        System.out.println(System.lineSeparator() + matchingReservations.size() + stringToPrint + System.lineSeparator());
        int counter = 1;
        for (CustomerReservation reservation : matchingReservations) {
            System.out.println(counter + ") " + reservation.getCustomer().getFullName());
            System.out.println("   Check-In Date: " + reservation.getCheckInDate());
            System.out.println("   Room: " + reservation.getRoomListing().getRoomName());
            counter++;
        }
        System.out.println(counter + ") Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        int selection = getNumericInput(1, counter);
        if (selection != counter) {
            System.out.println(System.lineSeparator() + "Reservation Details:" + System.lineSeparator());
            System.out.println(matchingReservations.get(selection - 1));
            System.out.println("Customer details:");
            System.out.println(matchingReservations.get(selection - 1).getCustomer());
            System.out.println("Payment information: ");
            System.out.println(matchingReservations.get(selection - 1).getPaymentInfo());
        }
    }

    private List<CustomerReservation> findReservationByGuestEmail(ManagementHotelDetails hotel) {
        ManagementReservationQuery managementReservationQuery = new ManagementReservationQuery(ReservationIdentifierType.EMAIL);
        managementReservationQuery.setHotelId(hotel.getId());

        System.out.print(System.lineSeparator() + "Enter the guest's email: ");
        while (true) {
            if (input.hasNextLine()) {
                managementReservationQuery.setIdentifier(input.nextLine().toLowerCase());
            }
            if (!managementReservationQuery.getIdentifier().contains("@")) {
                System.out.print("Invalid entry, try again: ");
            } else {
                List<CustomerReservation> reservations = requestHandler.reservationSearch(managementReservationQuery, hotel);
                if (reservations == null || reservations.size() == 0) {
                    return null;
                }
                return reservations;
            }
        }
    }

    private List<CustomerReservation> findReservationByGuestName(ManagementHotelDetails hotel) {
        ManagementReservationQuery managementReservationQuery = new ManagementReservationQuery(ReservationIdentifierType.FULL_NAME);
        managementReservationQuery.setHotelId(hotel.getId());

        String firstName = "";
        String lastName = "";
        System.out.print(System.lineSeparator() + "Enter the guest's first name: ");
        if (input.hasNextLine()) {
            firstName = input.nextLine().toLowerCase();
        }
        System.out.print("Enter the guest's last name: ");
        if (input.hasNextLine()) {
            lastName = input.nextLine().toLowerCase();
        }
        String fullName = firstName.concat(" ").concat(lastName);
        managementReservationQuery.setIdentifier(fullName);
        List<CustomerReservation> reservations = requestHandler.reservationSearch(managementReservationQuery, hotel);
        if (reservations == null || reservations.size() == 0) {
            return null;
        }
        return reservations;
    }

    private List<CustomerReservation> findReservationById(ManagementHotelDetails hotel) {
        ManagementReservationQuery managementReservationQuery = new ManagementReservationQuery(ReservationIdentifierType.RESERVATION_NUMBER);
        managementReservationQuery.setHotelId(hotel.getId());

        System.out.print(System.lineSeparator() + "Enter the confirmation number: ");
        managementReservationQuery.setIdentifier(String.valueOf(getNumericInput(0, Integer.MAX_VALUE)));
        return requestHandler.reservationSearch(managementReservationQuery, hotel);
    }

    private void manageHotelDetails(ManagementHotelDetails hotel) {

        if (hotel == null) {
            throw new NullPointerException("The hotel object to update is null");
        }
        while (true) {
            System.out.println(System.lineSeparator() + "Manage hotel details:" + System.lineSeparator());
            System.out.println("What would you like to do: ");
            System.out.println("1) View hotel details");
            System.out.println("2) Change hotel name");
            System.out.println("3) Change hotel description");
            System.out.println("4) Change phone number");
            System.out.println("5) Change check-in time");
            System.out.println("6) Change check-out time");
            System.out.println("7) Change check-in age");
            System.out.println("8) Change amenities");
            System.out.println("9) Add, remove, or modify a Room category");
            System.out.println("10) Go back");
            System.out.print(System.lineSeparator() + "Selection: ");
            HotelDetailChange detailChange;
            boolean updateWasSuccessful;
            switch (getNumericInput(1, 10)) {
                case 1:
                    System.out.println(System.lineSeparator() + "Hotel Details:" + System.lineSeparator());
                    System.out.println(hotel);
                    System.out.println();
                    break;
                case 2:
                    detailChange = modifyHotelName(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setName((String) detailChange.getNewValue());
                        System.out.println("The hotel name was successfully changed!");
                    } else {
                        System.out.println("There was a problem changing the hotel name, please try again later.");
                    }
                    break;
                case 3:
                    detailChange = modifyHotelDescription(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setDescription((String) detailChange.getNewValue());
                        System.out.println("The hotel description was successfully changed!");
                    } else {
                        System.out.println("There was a problem changing the hotel description, please try again later.");
                    }
                    break;
                case 4:
                    detailChange = modifyHotelPhoneNumber(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setPhoneNumber((String) detailChange.getNewValue());
                        System.out.println("The hotel phone number was successfully changed!");
                    } else {
                        System.out.println("There was a problem updating the hotel phone number, please try again later.");
                    }
                    break;
                case 5:
                    detailChange = modifyHotelCheckInTime(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setCheckInTime((LocalTime) detailChange.getNewValue());
                        System.out.println("The hotel check-in time was successfully changed!");
                    } else {
                        System.out.println("There was a problem updating the hotel check-in time, please try again later.");
                    }
                    break;
                case 6:
                    detailChange = modifyHotelCheckOutTime(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setCheckOutTime((LocalTime) detailChange.getNewValue());
                        System.out.println("The hotel check-out time was successfully changed!");
                    } else {
                        System.out.println("There was a problem updating the hotel check-out time, please try again later.");
                    }
                    break;
                case 7:
                    detailChange = modifyCheckInAge(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setCheckInAge((int) detailChange.getNewValue());
                        System.out.println("The hotel check-in age was successfully changed!");
                    } else {
                        System.out.println("There was a problem updating the hotel check-in age, please try again later.");
                    }
                    break;
                case 8:
                    detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_AMENITIES);
                    detailChange.setHotelId(hotel.getId());
                    Set<AmenityType> updatedAmenities = getUpdatedAmenities(hotel);
                    if (updatedAmenities == null) {
                        break;
                    }
                    detailChange.setNewValue(updatedAmenities);
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        hotel.setAmenities(updatedAmenities);
                        System.out.println("The hotel's amenities were successfully updated!");
                    } else {
                        System.out.println("There was a problem updating the hotel's amenities, please try again later.");
                    }
                    break;
                case 9:
                    detailChange = modifyRoomCategories(hotel);
                    if (detailChange == null) {
                        break;
                    }
                    detailChange.setHotelId(hotel.getId());
                    updateWasSuccessful = requestHandler.updateHotelDetail(hotel, detailChange);
                    if (updateWasSuccessful) {
                        Set<RoomCategory> updatedRoomCategories = requestHandler.getRoomCategories(hotel);
                        if (updatedRoomCategories != null) {
                            hotel.setRoomCategories(updatedRoomCategories);
                        }
                        System.out.println("The hotel's room categories were successfully updated!");
                    } else {
                        System.out.println("There was a problem updating the hotel's room categories, please try again later.");
                    }
                    break;
                case 10:
                    return;
                default:
                    System.out.print("Invalid choice, try again: ");
            }
        }
    }

    private HotelDetailChange modifyRoomCategories(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange;
        System.out.println(System.lineSeparator() + "Manage Room Categories:" + System.lineSeparator());
        System.out.println("What would you like to do:");
        System.out.println("1) Add a new room category");
        System.out.println("2) Remove a room category");
        System.out.println("3) Modify an existing room category");
        System.out.println("4) View current room categories");
        System.out.println("5) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        switch (getNumericInput(1, 5)) {
            case 1:
                detailChange = new HotelDetailChange(HotelPropertyModificationType.ADD_ROOM_CATEGORY);
                RoomCategory newRoomCategory = getNewRoomCategory();
                if (newRoomCategory == null) {
                    return null;
                }
                detailChange.setNewValue(newRoomCategory);
                return detailChange;
            case 2:
                if (hotel.getRoomCategories() == null || hotel.getRoomCategories().size() == 0) {
                    System.out.println("Sorry but no room categories currently exist");
                    return null;
                }
                detailChange = new HotelDetailChange(HotelPropertyModificationType.REMOVE_ROOM_CATEGORY);
                System.out.println(System.lineSeparator() + "Remove a Room Category:" + System.lineSeparator());
                RoomCategory roomCategoryForDeletion = getRoomCategorySelection(hotel, "Please select the room category to delete:");
                if (roomCategoryForDeletion == null) {
                    return null;
                }
                detailChange.setNewValue(roomCategoryForDeletion);
                return detailChange;
            case 3:
                detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_ROOM_CATEGORY);
                if (hotel.getRoomCategories() == null || hotel.getRoomCategories().size() == 0) {
                    System.out.println("Sorry but no room categories currently exist");
                    return null;
                }
                RoomCategory modifiedRoomCategory = getRoomCategoryModification(hotel);
                if (modifiedRoomCategory == null) {
                    return null;
                }
                detailChange.setNewValue(modifiedRoomCategory);
                return detailChange;
            case 4:
                System.out.println(System.lineSeparator() + "Current Room Categories: ");
                if (hotel.getRoomCategories() == null || hotel.getRoomCategories().size() == 0) {
                    System.out.println("There are not currently any room categories listed");
                }
                else {
                    for (RoomCategory roomCategory : hotel.getRoomCategories()) {
                        System.out.println(roomCategory);
                    }
                }
                return null;
            default:
                return null;
        }
    }

    private RoomCategory getRoomCategoryModification(ManagementHotelDetails hotel) {
        System.out.println(System.lineSeparator() + "Modify an Existing Room Category:" + System.lineSeparator());
        RoomCategory selectedCategory = getRoomCategorySelection(hotel, "Please select a room category to modify:");
        if (selectedCategory == null) {
            return null;
        }
        RoomCategory modifiedRoomCategory = new RoomCategory(selectedCategory);
        System.out.println(System.lineSeparator() + "Modify " + selectedCategory.getName() + ":" + System.lineSeparator());
        System.out.println("What would you like to update:");
        System.out.println("1) Name");
        System.out.println("2) Description");
        System.out.println("3) Max Occupancy");
        System.out.println("4) Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        switch (getNumericInput(1, 4)) {
            case 1:
                if (updateRoomName(modifiedRoomCategory)) {
                    return modifiedRoomCategory;
                }
                return null;
            case 2:
                if (updateRoomDescription(modifiedRoomCategory)) {
                    return modifiedRoomCategory;
                }
                return null;
            case 3:
                if (updateRoomMaxOccupancy(modifiedRoomCategory)) {
                    return modifiedRoomCategory;
                }
                return null;
            default:
                return null;
        }
    }

    private boolean updateRoomMaxOccupancy(RoomCategory modifiedRoomCategory) {
        System.out.println();
        while (true) {
            System.out.println("Current Max Occupancy: " + modifiedRoomCategory.getMaxOccupants());
            System.out.print("New max occupancy: ");
            int newMaxOccupancy = getNumericInput(1, 100);
            System.out.println("The new max occupancy is: " + newMaxOccupancy);
            System.out.println("Please confirm: ");
            switch (getChoice("Confirm", "Enter a different number", "Cancel")) {
                case 1:
                    modifiedRoomCategory.setMaxOccupants(newMaxOccupancy);
                    return true;
                case 2:
                    break;
                default:
                    return false;
            }
        }
    }

    private boolean updateRoomDescription(RoomCategory modifiedRoomCategory) {
        System.out.println();
        while (true) {
            System.out.println("Current description:");
            System.out.println(modifiedRoomCategory.getDescription());
            System.out.print("New Description: ");
            if (input.hasNextLine()) {
                modifiedRoomCategory.setDescription(input.nextLine());
            }
            System.out.println("The new description is: " + modifiedRoomCategory.getDescription());
            System.out.println("Please confirm: ");
            switch (getChoice("Confirm", "Enter a different description", "Cancel")) {
                case 1:
                    return true;
                case 2:
                    break;
                default:
                    return false;
            }
        }
    }

    private boolean updateRoomName(RoomCategory modifiedRoomCategory) {
        System.out.println();
        while (true) {
            System.out.print("New name: ");
            if (input.hasNextLine()) {
                modifiedRoomCategory.setName(input.nextLine());
            }
            System.out.println("The new name is: " + modifiedRoomCategory.getName());
            System.out.println("Please confirm: ");
            switch (getChoice("Confirm", "Enter a different name", "Cancel")) {
                case 1:
                    return true;
                case 2:
                    break;
                default:
                    return false;
            }
        }
    }

    private RoomCategory getNewRoomCategory() {
        RoomCategory newRoomCategory = new RoomCategory();
        System.out.println(System.lineSeparator() + "Create a new Room Category" + System.lineSeparator());
        while (true) {
            System.out.print("Enter the name of the room category: ");
            if (input.hasNextLine()) {
                newRoomCategory.setName(input.nextLine());
            }
            System.out.print("Enter the description of the room category: ");
            if (input.hasNextLine()) {
                newRoomCategory.setDescription(input.nextLine());
            }
            System.out.print("Enter the max occupancy of the room: ");
            newRoomCategory.setMaxOccupants(getNumericInput(1, 100));
            System.out.println(System.lineSeparator() + "Please confirm room category details:");
            System.out.println(newRoomCategory);
            switch (getChoice("Confirm", "Change Something", "Cancel")) {
                case 1:
                    return newRoomCategory;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private Set<AmenityType> getUpdatedAmenities(ManagementHotelDetails hotel) {
        Set<AmenityType> updatedAmenities;
        System.out.println(System.lineSeparator() + "Update Hotel Amenities:" + System.lineSeparator());
        int initialLength = 0;
        System.out.println("Current amenities: ");
        if (hotel.getAmenities() == null || hotel.getAmenities().size() == 0) {
            updatedAmenities = new HashSet<>();
            System.out.println("No amenities currently");
        } else {
            updatedAmenities = new HashSet<>(hotel.getAmenities());
            hotel.getAmenities().forEach(System.out::println);
            initialLength = hotel.getAmenities().size();
        }
        int selection = 3;
        while (true) {

            if (selection == -1) {
                selection = 3;
            }
            else {
                System.out.println(System.lineSeparator() + "What would you like to do:");
                if (updatedAmenities.size() == 0) {
                    selection = getChoice("Add an amenity", "Finish", null);
                } else {
                    selection = getChoice("Add an amenity", "Remove an amenity", "Finish");
                }
            }
            switch (selection) {
                case 1:
                    AmenityType newAmenity = getNewAmenity(updatedAmenities);
                    if (newAmenity != null) {
                        updatedAmenities.add(newAmenity);
                    }
                    break;
                case 2:
                    AmenityType amenityToRemove = getAmenityToRemove(updatedAmenities);
                    if (amenityToRemove != null) {
                        updatedAmenities.remove(amenityToRemove);
                    }
                    break;
                default:
                    if (initialLength == updatedAmenities.size()) {
                        return null;
                    }
                    else {
                        return updatedAmenities;
                    }
            }
            System.out.println(System.lineSeparator() + "Would you like to do anything else: ");
            int choice = getChoice("Yes", "No", null);
            if (choice == 2) {
                selection = -1;
            }
        }
    }

    private AmenityType getAmenityToRemove(Set<AmenityType> updatedAmenities) {
        if (updatedAmenities == null) {
            return null;
        }
        AmenityType[] amenitiesArray = updatedAmenities.toArray(new AmenityType[0]);
        System.out.println(System.lineSeparator() + "Remove an amenity:" + System.lineSeparator());
        System.out.println("Select an amenity to remove: ");
        int counter = 1;
        for (AmenityType amenity : amenitiesArray) {
            System.out.println(counter + ") " + amenity);
            counter++;
        }
        System.out.println(counter + ") Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        int selection = getNumericInput(1, counter);
        return (selection == counter) ? null : amenitiesArray[selection - 1];
    }

    private AmenityType getNewAmenity(Set<AmenityType> amenities) {
        if (amenities == null) {
            return null;
        }
        System.out.println(System.lineSeparator() + "Add an amenity" + System.lineSeparator());
        List<AmenityType> availableAmenities = Arrays.stream(AmenityType.values()).filter(amenityType -> !amenities.contains(amenityType)).collect(Collectors.toList());
        int counter = 1;
        if (availableAmenities.size() == 0) {
            System.out.println("Sorry, there are not any additional amenities to add");
            return null;
        }
        System.out.println("Select an amenity to add:");
        for (AmenityType amenity : availableAmenities) {
            System.out.println(counter + ") " + amenity);
            counter++;
        }
        System.out.println(counter + ") Cancel");
        System.out.print(System.lineSeparator() + "Selection: ");
        int selection = getNumericInput(1, counter);
        return (selection == counter) ? null : availableAmenities.get(selection - 1);
    }

    private HotelDetailChange modifyCheckInAge(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_CHECK_IN_AGE);
        System.out.println(System.lineSeparator() + "Update Hotel Check-In Age:");
        System.out.println("Current check-in age: " + hotel.getCheckInAge());
        while (true) {
            System.out.print("New check-in age: ");
            int newCheckInAge = getNumericInput(1, 100);
            System.out.println("Please confirm the new check-in age");
            System.out.println("New check-in age: " + newCheckInAge);
            switch (getChoice("Confirm", "Enter a different age", "Cancel")) {
                case 1:
                    detailChange.setNewValue(newCheckInAge);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
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

    private HotelDetailChange modifyHotelCheckOutTime(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_CHECK_OUT_TIME);
        System.out.println(System.lineSeparator() + "Update Hotel Check-Out Time:");
        System.out.println("Current check-out time: " + hotel.getCheckOutTime());
        while (true) {
            System.out.print("New check-out time (HH:MM): ");
            LocalTime newCheckOutTime = getTimeInput();
            System.out.println("Please confirm the new check-out time");
            System.out.println("New check-out time: " + newCheckOutTime);
            switch (getChoice("Confirm", "Enter a different time", "Cancel")) {
                case 1:
                    detailChange.setNewValue(newCheckOutTime);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private HotelDetailChange modifyHotelCheckInTime(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_CHECK_IN_TIME);
        System.out.println(System.lineSeparator() + "Update Hotel Check-In Time:");
        System.out.println("Current check-in time: " + hotel.getCheckInTime());
        while (true) {
            System.out.print("New check-in time (HH:MM): ");
            LocalTime newCheckInTime = getTimeInput();
            System.out.println("Please confirm the new check-in time");
            System.out.println("New check-in time: " + newCheckInTime);
            switch (getChoice("Confirm", "Enter a different time", "Cancel")) {
                case 1:
                    detailChange.setNewValue(newCheckInTime);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private LocalTime getTimeInput() {
        while (true) {
            if (input.hasNextLine()) {
                try {
                    return LocalTime.parse(input.nextLine());
                }
                catch (DateTimeParseException e) {
                    System.out.print("Invalid entry, try again (HH:MM): ");
                }
            }
        }
    }

    private HotelDetailChange modifyHotelPhoneNumber(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_PHONE_NUMBER);
        System.out.println(System.lineSeparator() + "Update Hotel Phone Number:");
        System.out.println("Current phone number: " + hotel.getPhoneNumber());
        while (true) {
            System.out.print("New phone number: ");
            String newPhoneNumber = hotel.getPhoneNumber();
            if (input.hasNextLine()) {
                newPhoneNumber = input.nextLine();
            }
            System.out.println("Please confirm new phone number");
            System.out.println("New phone number: " + newPhoneNumber);
            switch (getChoice("Confirm", "Enter different phone number", "Cancel")) {
                case 1:
                    detailChange.setNewValue(newPhoneNumber);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private HotelDetailChange modifyHotelDescription(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_DESCRIPTION);
        System.out.println(System.lineSeparator() + "Update Hotel Description:");
        System.out.println("Current description: ");
        System.out.println(hotel.descriptionToString() + System.lineSeparator());
        while (true) {
            System.out.print("New description: ");
            String oldDescription = hotel.getDescription();
            if (input.hasNextLine()) {
                hotel.setDescription(input.nextLine());
            }
            System.out.println("Please confirm new description: ");
            System.out.println(hotel.descriptionToString());
            switch (getChoice("Confirm", "Enter different description", "Cancel")) {
                case 1:
                    detailChange.setNewValue(hotel.getDescription());
                    hotel.setDescription(oldDescription);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private HotelDetailChange modifyHotelName(ManagementHotelDetails hotel) {
        HotelDetailChange detailChange = new HotelDetailChange(HotelPropertyModificationType.UPDATE_NAME);
        System.out.println(System.lineSeparator() + "Update Hotel Name:");
        System.out.println("Current name: " + hotel.getName());
        while (true) {
            System.out.print("New name: ");
            String newName = hotel.getName();
            if (input.hasNextLine()) {
                newName = input.nextLine();
            }
            System.out.println("Please confirm name change");
            System.out.println("Previous name: " + hotel.getName());
            System.out.println("New name: " + newName);
            switch (getChoice("Confirm", "Re-Enter new name", "Cancel")) {
                case 1:
                    detailChange.setNewValue(newName);
                    return detailChange;
                case 2:
                    break;
                default:
                    return null;
            }
        }
    }

    private void addNewHotel(ManagementAccount account) {
        HotelBuilder hb = new HotelBuilder();
        System.out.println(System.lineSeparator() + "Create a new Hotel" + System.lineSeparator());
        hb.withName(getTextInput("Hotel name: "));
        hb.withDescription(getTextInput("Description: "));
        hb.withPhoneNumber(getPhoneNumber());
        hb.withStreetAddress(getTextInput("Street Address: "));
        hb.withState(getTextInput("State: "));
        hb.withCity(getTextInput("City: "));
        System.out.print("How many floors is the hotel: ");
        hb.withFloorCountOf(getNumericInput(1, 1000));
        System.out.print("What is the check-in time (HH:MM): ");
        hb.withCheckInTimeOf(getTimeInput());
        System.out.print("What is the minimum check-in age: ");
        hb.withCheckInAgeOf(getNumericInput(1, 100));
        System.out.print("What is the check-out time (HH:MM): ");
        hb.withCheckOutTimeOf(getTimeInput());
        hb.withOperatingHours(getOperatingHours());
        System.out.println("Next, you'll enter the room categories your hotel offers");
        hb.withRoomCategories(getRoomCategories());
        hb.withOwner(account);
        ManagementHotelDetails hotel = hb.build();
        System.out.println("Attempting to create hotel...");
        long hotelId = requestHandler.createNewHotel(hotel);
        if (hotelId == -1) {
            System.out.println("Sorry there was a problem creating the hotel, please try again later!");
        }
        else {
            hotel.setId(hotelId);
            if (account.getHotels() == null) {
                account.setHotels(new ArrayList<>());
            }
            account.getHotels().add(hotel);
            System.out.println("The hotel has successfully been created and added to your account");
        }
    }

    private Set<RoomCategory> getRoomCategories() {
        Set<RoomCategory> roomCategories = new HashSet<>();
        System.out.print("How many room categories do you have: ");
        int numOfCategories = getNumericInput(0, 1000);
        for (int i = 1; i <= numOfCategories; i++) {
            System.out.println("Please enter the details for room category #" + i + ": ");
            RoomCategory newCategory = getNewRoomCategory();
            if (newCategory != null) {
                roomCategories.add(newCategory);
            }
        }
        return roomCategories;
    }

    private Set<OperatingHours> getOperatingHours() {
        Set<OperatingHours> operatingHours = new TreeSet<>();
        System.out.println("Next, please enter your operating hours");
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            OperatingHours hours = new OperatingHours(dayOfWeek);
            System.out.println(dayOfWeek + ": ");
            System.out.print("Opening time (HH:MM): ");
            hours.setOpeningTime(getTimeInput());
            System.out.print("Closing time (HH:MM): ");
            hours.setClosingTime(getTimeInput());
            operatingHours.add(hours);
        }
        return operatingHours;
    }

    // TODO
    private boolean phoneNumberValid(String phoneNumber) {
        return true;
    }

    private String getPhoneNumber() {
        String phoneNumber = "";
        System.out.print("Enter phone number (DDD) DDD-DDDD: ");
        while(true) {
            if (input.hasNextLine()) {
                phoneNumber = input.nextLine();
            }
            if (phoneNumberValid(phoneNumber)) {
                return phoneNumber;
            }
            System.out.print("Invalid entry, try again: ");

        }
    }

    private String getTextInput(String message) {
        System.out.print(message);
        while (true) {
            if (input.hasNextLine()) {
                return input.nextLine();
            }
        }
    }
}