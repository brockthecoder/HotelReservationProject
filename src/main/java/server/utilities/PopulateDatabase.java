package server.utilities;

import client.management.model.*;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.enums.UpdateAction;
import server.api.ConnectionPool;
import server.api.ManagementDAO;
import server.api.MutualDAO;
import server.implementation.ConnectionPoolImpl;
import server.implementation.ManagementDAOImpl;
import server.implementation.MutualDAOImpl;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class PopulateDatabase {
    private static SecureRandom secureRandom = new SecureRandom();
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static void main(String[] args) {
        ConnectionPool connectionPool = new ConnectionPoolImpl();
        MutualDAO mutualDAO = new MutualDAOImpl(connectionPool);
        ManagementDAO dao = new ManagementDAOImpl(connectionPool, mutualDAO);
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsFromToday = today.plusMonths(6);
        List<LocalDate> datesToAdd = LongStream.range(today.toEpochDay(), sixMonthsFromToday.toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
        ManagementAccount account = new ManagementAccount();
        System.out.println("Loading account");
        account.setEmail("management@hotelfinder.com");
        account.setPassword("");
        account = dao.getAccount(account);
        System.out.println("Creating thread pool");
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (ManagementHotelDetails hotel : account.getHotels()) {
            System.out.println("Working on: ");
            System.out.println(hotel);
            for (RoomCategory roomCategory : hotel.getRoomCategories()) {
                AvailabilityUpdateRequest request = new AvailabilityUpdateRequest();
                request.setHotelID(hotel.getId());
                request.setRoomCategoryId(roomCategory.getId());
                request.setUpdates(new LinkedList<>());
                for (LocalDate date : datesToAdd) {
                    AvailabilityUpdateTuple updateTuple = new AvailabilityUpdateTuple();
                    updateTuple.setDate(date);
                    updateTuple.setAction(UpdateAction.ADD);
                    updateTuple.setNumOfRooms(getRoomCount());
                    updateTuple.setNightlyRate(getNightlyRate());
                    request.getUpdates().add(updateTuple);
                }
                executorService.execute(new AvailabilityWorker(connectionPool, mutualDAO, request));
            }
        }
        executorService.shutdown();
    }

    private static double getNightlyRate() {
        double nightlyRate = secureRandom.doubles(75.99, 699.99).limit(1).sum();
        return Double.parseDouble(decimalFormat.format(nightlyRate));
    }

    private static long getRoomCount() {
        return secureRandom.ints(1, 25).limit(1).sum();
    }

    private static List<ManagementHotelDetails> getHotels(ManagementAccount account) {
        Random random = new Random();
        String[] brandNames = {"Ritz-Carlton", "St. Regis", "Edition", "JW Marriott", "W", "Marriott", "Sheraton", "Westin", "Courtyard", "Moxy", "Four points"};
        String[] cities = {"New york, new york", "Los Angeles, california", "Chicago, illinois", "Houston, texas", "Phoenix, arizona", "Philadelphia, pennsylvania", "San Antonio, texas", "San Diego, california", "Dallas, texas", "San Jose, california", "Austin, texas", "Fort Worth, texas", "Jacksonville, florida", "Columbus, ohio", "Charlotte, north carolina", "San Francisco, california", "Indianapolis, Indiana", "Seattle, washington", "Denver, colorado", "Washington, district of columbia", "Boston, massachusetts", "El paso, texas", "Nashville, tennessee", "Detroit, michigan", "Portland, oregon", "Las Vegas, nevada", "Oklahoma City, oklahoma", "Memphis, tennessee", "Louisville, kentucky", "Baltimore, maryland", "Milwaukee, wisconsin", "Albuquerque, new mexico", "Tucson, arizona", "Fresno, california", "Mesa, arizona", "Atlanta, georgia", "Sacramento, california", "Kansas City, missouri", "Miami, florida", "Colorado Springs, colorado", "Raleigh, north carolina", "Omaha, nebraska", "Long Beach, california", "Virginia Beach, virginia", "Minneapolis, minnesota", "Oakland, california", "Tampa, florida", "Arlington, texas", "Tulsa, oklahoma", "Bakersfield, california", "New Orleans, louisiana", "Wichita, kansas", "Aurora, colorado", "Cleveland, ohio", "Anaheim, california", "Honolulu, hawaii", "Riverside, california", "San Juan, puerto rico", "Santa Ana, california", "Henderson, nevada", "Lexington, kentucky", "Stockton, california", "St. Paul, minnesota", "Cincinnati, ohio", "Irvine, texas", "Greensboro, north carolina", "Pittsburgh, pennsylvania", "Lincoln, nebraska", "St. Louis, missouri", "Orlando, florida", "Anchorage, alaska"};
        String[] roomCategories = {"Standard, Guest Room, 1 King", "Standard, Guest Room, 2 Queen", "Deluxe, Guest Room, 1 King", "Deluxe, Guest Room, 2 Queen", "Loft, Guest room, 1 King", "Deluxe Terrace, Guest room, 2 Queen", "Superior Suite, 1 Bedroom Suite, 1 King", "Premier Suite, 1 Bedroom Suite, 1 King"};
        Set<OperatingHours> operatingHours = getOperatingHours();
        String description = "A luxurious hotel located in ";
        LocalTime checkInTime = LocalTime.of(15, 0);
        LocalTime checkOutTime = LocalTime.of(11, 0);
        List<ManagementHotelDetails> hotelDetailsList = new LinkedList<>();
        for (String city : cities) {
            for (String name : brandNames) {
                ManagementHotelDetails managementHotelDetails = new ManagementHotelDetails();
                managementHotelDetails.setHotelOwner(account);
                managementHotelDetails.setRoomCategories(getRoomCategories());
                managementHotelDetails.setOperatingHours(getOperatingHours());
                managementHotelDetails.setAmenities(getAmenities());
                managementHotelDetails.setPhoneNumber(getPhoneNumber());
                managementHotelDetails.setCheckInAge(18);
                managementHotelDetails.setCheckInTime(checkInTime);
                managementHotelDetails.setCheckOutTime(checkOutTime);
                managementHotelDetails.setNumOfFloors(getNumberOfFloors(random));
                managementHotelDetails.setName(getName(city, name));
                managementHotelDetails.setDescription(description.concat(getCity(city).concat(", ").concat(getState(city))));
                managementHotelDetails.setCity(getCity(city));
                managementHotelDetails.setState(getState(city));
                managementHotelDetails.setStreetAddress(getStreetAddress());
                hotelDetailsList.add(managementHotelDetails);
            }
        }
        return hotelDetailsList;
    }

    private static String getState(String city) {
        int commaIndex = city.indexOf(',');
        return city.substring(commaIndex + 2, commaIndex + 3).toUpperCase().concat(city.substring(commaIndex + 3));
    }

    private static String getCity(String city) {
        int commaIndex = city.indexOf(',');
        return city.substring(0, commaIndex).toLowerCase();
    }

    private static String getStreetAddress() {
        String[] streetNames = {"Second street", "third street", "first street", "fourth street", "park avenue", "fifth street", "main street", "sixth street", "oak street", "seventh ave", "pine road", "maple drive", "cedar road", "eighth ave", "washington st",  "ninth st", "lake rd", "hill rd"};

        int selection = new Random().nextInt(streetNames.length);
        return String.valueOf(getStreetNumber(new Random())).concat(" ").concat(streetNames[selection]);
    }
    private static String getName(String city, String name) {
        int commaIndex = city.indexOf(',');
        if (name.equals("Edition")) {
            return "The ".concat(city.substring(0, commaIndex)).concat(" ").concat(name);
        }
        return  "The ".concat(name).concat(", ").concat(city.substring(0, commaIndex));
    }

    private static Set<RoomCategory> getRoomCategories() {
        String[] roomCategories = {"Standard, Guest Room, 1 King", "Standard, Guest Room, 2 Queen", "Deluxe, Guest Room, 1 King", "Deluxe, Guest Room, 2 Queen", "Loft, Guest room, 1 King", "Deluxe Terrace, Guest room, 2 Queen", "Superior Suite, 1 Bedroom Suite, 1 King", "Premier Suite, 1 Bedroom Suite, 1 King"};
        String description = "A spacious, comfortable, and relaxing room";
        Set<RoomCategory> roomCategorySet =  new HashSet<>();
        for (String category : roomCategories) {
            RoomCategory roomCategory = new RoomCategory();
            roomCategory.setName(category);
            roomCategory.setDescription(description);
            roomCategory.setMaxOccupants(4);
            roomCategorySet.add(roomCategory);
        }
        return roomCategorySet;
    }


    private static int getStreetNumber(Random random) {
        return random.ints(50, 3000).limit(1).sum();
    }

    private static int getNumberOfFloors(Random random) {
        return random.nextInt(50);
    }

    private static String getPhoneNumber() {
        int[] ints = new Random().ints(1, 10).limit(10).toArray();
        StringBuilder sb = new StringBuilder(14);
        sb.append('(').append(ints[0]).append(ints[1]).append(ints[2]).append(") ")
                .append(ints[3]).append(ints[4]).append(ints[5]).append('-').append(ints[6]).append(ints[7])
                .append(ints[8]).append(ints[9]);

        return sb.toString();
    }
    private static Set<OperatingHours> getOperatingHours() {
        Set<OperatingHours> operatingHours = new HashSet<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            OperatingHours hours = new OperatingHours();
            hours.setDayOfWeek(dayOfWeek);
            hours.setOpeningTime(LocalTime.of(5, 0));
            hours.setClosingTime(LocalTime.of(23, 0));
            operatingHours.add(hours);
        }
        return operatingHours;
    }

    private static Set<AmenityType> getAmenities() {
        Set<AmenityType> amenityTypes = new HashSet<>();
        AmenityType[] amenityTypesArray = AmenityType.values();

        new Random().ints(0, amenityTypesArray.length).limit(10).forEach((int i) -> amenityTypes.add(amenityTypesArray[i]));
        return amenityTypes;
    }
}
