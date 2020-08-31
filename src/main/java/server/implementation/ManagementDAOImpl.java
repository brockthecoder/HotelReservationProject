package server.implementation;

import client.management.model.*;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.ReservationPaymentInfo;
import client.mutual.model.RoomListing;
import mutual.model.AvailabilityUpdateRequest;
import mutual.model.CategorySpecificAvailabilityQuery;
import mutual.model.enums.CreditCardType;
import mutual.model.enums.UpdateAction;
import server.api.ConnectionPool;
import server.api.ManagementDAO;
import server.api.MutualDAO;
import server.utilities.SQLStatements;

import java.sql.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;

public class ManagementDAOImpl implements ManagementDAO {

    private final ConnectionPool connectionPool;
    private MutualDAO mutualDAO;
    private DecimalFormat df;

    public ManagementDAOImpl(ConnectionPool connectionPool, MutualDAO mutualDAO) {
        this.df = new DecimalFormat("0.00");
        this.connectionPool = connectionPool;
        this.mutualDAO = mutualDAO;
    }

    @Override
    public ManagementAccount getAccount(ManagementAccount accountDetails) {
        if (accountDetails == null || accountDetails.getEmail() == null || accountDetails.getPassword() == null) {
            return null;
        }
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement accountSelectStatement = connection.prepareStatement(SQLStatements.loginIsValid)) {
                accountSelectStatement.setString(1, accountDetails.getEmail());
                accountSelectStatement.setString(2, accountDetails.getPassword());
                ResultSet resultSet = accountSelectStatement.executeQuery();
                if (resultSet.next()) {
                    accountDetails.setId(resultSet.getLong(1));

                    try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectAccount)) {
                        selectStatement.setString(1, accountDetails.getEmail());
                        selectStatement.setString(2, accountDetails.getPassword());
                        ResultSet fullResultSet = selectStatement.executeQuery();
                        parseManagementSignIn(fullResultSet, accountDetails);
                        connection.commit();
                        return accountDetails;
                    }
                }
                connection.rollback();
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to load account from database");
            return null;
        }
    }

    private void parseManagementSignIn(ResultSet resultSet, ManagementAccount accountDetails) throws SQLException {
        accountDetails.setHotels(new ArrayList<>());
        while (resultSet.next()) {
            ManagementHotelDetails hotel = new ManagementHotelDetails();
            hotel.setId(resultSet.getLong(2));
            hotel.setName(resultSet.getString(3));
            hotel.setDescription(resultSet.getString(4));
            hotel.setStreetAddress(resultSet.getString(5));
            hotel.setCity(resultSet.getString(6));
            hotel.setState(resultSet.getString(7));
            hotel.setPhoneNumber(resultSet.getString(8));
            hotel.setCheckInAge(resultSet.getLong(9));
            hotel.setNumOfFloors(resultSet.getLong(10));
            hotel.setCheckInTime(resultSet.getTime(11).toLocalTime());
            hotel.setCheckOutTime(resultSet.getTime(12).toLocalTime());
            hotel.setAmenities(getAmenities(hotel.getId()));
            hotel.setRoomCategories(getRoomCategories(hotel.getId()));
            hotel.setOperatingHours(getOperatingHours(hotel.getId()));
            accountDetails.getHotels().add(hotel);
        }


    }

    @Override
    public long newAccount(ManagementAccount accountDetails) {
        if (accountDetails == null || accountDetails.getEmail() == null || accountDetails.getPassword() == null) {
            return -1;
        }
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.findIfAccountExists)) {
                selectStatement.setString(1, accountDetails.getEmail());
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                if (resultSet.getLong(1) > 0) {
                    connection.commit();
                    return -1;
                }
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(SQLStatements.insertNewAccount)) {
                insertStatement.setString(1, accountDetails.getEmail());
                insertStatement.setString(2, accountDetails.getPassword());
                ResultSet resultSet = insertStatement.executeQuery();
                resultSet.next();
                long accountId =  resultSet.getLong(1);
                connection.commit();
                return accountId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to insert new account into database");
            return -1;
        }
    }

    @Override
    public boolean deleteHotel(long hotelId) {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.deleteHotel)) {
                selectStatement.setLong(1, hotelId);
                int numOfHotelsDeleted = selectStatement.executeUpdate();
                connection.commit();
                return numOfHotelsDeleted >= 1;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to delete hotel from database");
            return false;
        }
    }

    @Override
    public List<CustomerReservation> getReservations(ManagementReservationQuery query) {
        if (query == null || query.getIdentifier() == null || query.getIdentifierType() == null) {
            return null;
        }

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement selectStatement = null;
            switch (query.getIdentifierType()) {
                case RESERVATION_NUMBER:
                    long reservationId = Long.parseLong(query.getIdentifier());
                    selectStatement = connection.prepareStatement(SQLStatements.selectReservationsByIdAndHotelId);
                    selectStatement.setLong(1, reservationId);
                    selectStatement.setLong(2, query.getHotelId());
                    break;
                case EMAIL:
                    selectStatement = connection.prepareStatement(SQLStatements.selectReservationsByEmailAndHotelID);
                    selectStatement.setString(1, query.getIdentifier());
                    selectStatement.setLong(2, query.getHotelId());
                    break;
                case FULL_NAME:
                    String[] fullName = query.getIdentifier().split(" ");
                    selectStatement = connection.prepareStatement(SQLStatements.selectReservationsByFullNameAndHotelId);
                    selectStatement.setString(1, fullName[0]);
                    selectStatement.setString(2, fullName[1]);
                    selectStatement.setLong(3, query.getHotelId());
                    break;
            }
            ResultSet resultSet = selectStatement.executeQuery();
            List<CustomerReservation> reservations = getCustomerReservationsFromResultSet(resultSet, query.getHotelId());
            selectStatement.close();
            connection.commit();
            return reservations;
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException | SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to delete hotel from database");
            return null;
        }
    }

    private List<CustomerReservation> getCustomerReservationsFromResultSet(ResultSet resultSet, long hotelId) throws SQLException {
        List<CustomerReservation> reservations = new ArrayList<>();

        while (resultSet.next()) {
            CustomerReservation reservation = new CustomerReservation();
            Customer customer = new Customer();
            RoomListing roomListing = new RoomListing();
            ReservationPaymentInfo paymentInfo = new ReservationPaymentInfo();
            reservation.setId(resultSet.getLong(1));
            customer.setFirstName(resultSet.getString(2));
            customer.setLastName(resultSet.getString(3));
            customer.setPhoneNumber(resultSet.getString(4));
            customer.setEmail(resultSet.getString(5));
            customer.setStreetAddress(resultSet.getString(6));
            customer.setState(resultSet.getString(7));
            customer.setCity(resultSet.getString(8));
            reservation.setCustomer(customer);
            reservation.setCheckInDate(resultSet.getDate(9).toLocalDate());
            reservation.setCheckOutDate(resultSet.getDate(10).toLocalDate());
            roomListing.setRoomCategoryId(resultSet.getLong(11));
            roomListing.setRoomName(resultSet.getString(12));
            roomListing.setDescription(resultSet.getString(13));
            roomListing.setMaxOccupants(resultSet.getLong(14));
            roomListing.setNightlyRate(resultSet.getDouble(15));
            reservation.setRoomListing(roomListing);
            reservation.setTotal(resultSet.getDouble(16));
            paymentInfo.setCardNumber(resultSet.getString(17));
            paymentInfo.setCvv(resultSet.getString(18));
            paymentInfo.setCardHolderName(resultSet.getString(19));
            paymentInfo.setExpirationDate(resultSet.getString(20));
            paymentInfo.setZipCode(resultSet.getString(21));
            paymentInfo.setCreditCardType(CreditCardType.valueOf(resultSet.getString(22)));
            reservation.setPaymentInfo(paymentInfo);
            reservation.setHotel(mutualDAO.getHotel(hotelId));
            reservations.add(reservation);
        }
        return reservations;
    }

    @Override
    public List<CustomerReservation> getReservations(long hotelId) {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectUpcomingReservations)) {
                selectStatement.setLong(1, hotelId);
                ResultSet resultSet = selectStatement.executeQuery();
                List<CustomerReservation> reservations = getCustomerReservationsFromResultSet(resultSet, hotelId);
                connection.commit();
                return reservations;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to load reservations from database");
            return null;
        }
    }

    @Override
    public List<RoomListing> getAvailability(ManagementRoomQuery query) {
        if (query == null || query.getHotel() == null|| query.getCheckInDate() == null || query.getCheckOutDate() == null) {
            return null;
        }
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectAvailabilityByQuery)) {
                selectStatement.setDate(1, Date.valueOf(query.getCheckInDate()));
                Date lastNight = Date.valueOf(query.getCheckOutDate().minus(Period.ofDays(1)));
                selectStatement.setDate(2, lastNight);
                selectStatement.setLong(3, query.getHotel().getId());
                selectStatement.setLong(4, query.getNumOfPeople());
                long numOfNights = query.getCheckOutDate().toEpochDay() - query.getCheckInDate().toEpochDay();
                selectStatement.setLong(5, numOfNights);
                ResultSet resultSet = selectStatement.executeQuery();
                List<RoomListing> availability = getRoomListingsFromResultSet(resultSet, numOfNights);
                connection.commit();
                return availability;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to load availability from database");
            return null;
        }

    }

    private List<RoomListing> getRoomListingsFromResultSet(ResultSet resultSet, long numOfNights) throws SQLException {
        if (resultSet == null) {
            return null;
        }
        List<RoomListing> roomListings = new ArrayList<>();

        while (resultSet.next()) {
            RoomListing roomListing = new RoomListing();
            roomListing.setRoomCategoryId(resultSet.getLong(1));
            roomListing.setRoomName(resultSet.getString(2));
            roomListing.setDescription(resultSet.getString(3));
            roomListing.setMaxOccupants(resultSet.getLong(4));
            roomListing.setNightlyRate(resultSet.getDouble(5) / numOfNights);
            roomListings.add(roomListing);
        }
        return roomListings;
    }

    @Override
    public Map<LocalDate, AvailabilityListing> getAvailability(CategorySpecificAvailabilityQuery query) {
        if (query == null || query.getEndDate() == null || query.getStartDate() == null) {
            return null;
        }

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectAvailabilityByRoomCategory)) {
                selectStatement.setLong(1, query.getRoomCategoryId());
                selectStatement.setDate(2, Date.valueOf(query.getStartDate()));
                selectStatement.setDate(3, Date.valueOf(query.getEndDate()));
                ResultSet resultSet = selectStatement.executeQuery();
                Map<LocalDate, AvailabilityListing> availabilityMap = new HashMap<>();
                while (resultSet.next()) {
                    LocalDate key = resultSet.getDate(1).toLocalDate();
                    AvailabilityListing availabilityListing = new AvailabilityListing();
                    availabilityListing.setNumOfRooms(resultSet.getLong(2));
                    availabilityListing.setNightlyRate(resultSet.getDouble(3));
                    availabilityMap.put(key, availabilityListing);
                }
                connection.commit();
                return availabilityMap;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to load availability from database");
            return null;
        }

    }

    @Override
    public long newReservation(CustomerReservation reservationDetails) {
        return mutualDAO.newReservation(reservationDetails);
    }

    @Override
    public boolean updateHotel(HotelDetailChange detailChange) {
        if (detailChange == null || detailChange.getPropertyModificationType() == null || detailChange.getNewValue() == null) {
            return false;
        }
        PreparedStatement updateStatement = null;
        try (Connection connection = connectionPool.getConnection()) {
            switch (detailChange.getPropertyModificationType()) {
                case UPDATE_NAME:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelName);
                    updateStatement.setString(1, (String) detailChange.getNewValue());
                    System.out.println((String) detailChange.getNewValue());
                    updateStatement.setLong(2, detailChange.getHotelId());
                    System.out.println("Hotel Id: " + detailChange.getHotelId());
                    break;
                case UPDATE_DESCRIPTION:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelDescription);
                    updateStatement.setString(1, (String) detailChange.getNewValue());
                    updateStatement.setLong(2, detailChange.getHotelId());
                    break;
                case UPDATE_PHONE_NUMBER:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelPhoneNumber);
                    updateStatement.setString(1, (String) detailChange.getNewValue());
                    updateStatement.setLong(2, detailChange.getHotelId());
                    break;
                case UPDATE_CHECK_IN_AGE:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelCheckInAge);
                    updateStatement.setLong(1, (Long) detailChange.getNewValue());
                    updateStatement.setLong(2, detailChange.getHotelId());
                    break;
                case UPDATE_CHECK_OUT_TIME:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelCheckOutTime);
                    updateStatement.setTime(1, Time.valueOf((LocalTime) detailChange.getNewValue()));
                    updateStatement.setLong(2, detailChange.getHotelId());
                    break;
                case UPDATE_CHECK_IN_TIME:
                    updateStatement = connection.prepareStatement(SQLStatements.updateHotelCheckInTime);
                    updateStatement.setTime(1, Time.valueOf((LocalTime) detailChange.getNewValue()));
                    updateStatement.setLong(2, detailChange.getHotelId());
                    break;
                case UPDATE_AMENITIES:
                    updateStatement = connection.prepareStatement(SQLStatements.deleteHotelAmenities);
                    updateStatement.setLong(1, detailChange.getHotelId());
                    updateStatement.executeUpdate();
                    updateStatement.close();
                    Set<AmenityType> newAmenities = (Set<AmenityType>) detailChange.getNewValue();
                    for (AmenityType amenityType : newAmenities) {
                        updateStatement = connection.prepareStatement(SQLStatements.insertNewAmenity);
                        updateStatement.setLong(1, detailChange.getHotelId());
                        updateStatement.setString(2, amenityType.name());
                        updateStatement.executeUpdate();
                        updateStatement.close();
                    }
                    connection.commit();
                    return true;
                case UPDATE_OPERATING_HOURS:
                    updateStatement = connection.prepareStatement(SQLStatements.deleteHotelOperatingHours);
                    updateStatement.setLong(1, detailChange.getHotelId());
                    updateStatement.executeUpdate();
                    updateStatement.close();
                    Set<OperatingHours> newOperatingHours = (Set<OperatingHours>) detailChange.getNewValue();
                    for (OperatingHours operatingHours : newOperatingHours) {
                        updateStatement = connection.prepareStatement(SQLStatements.insertNewOperatingHours);
                        updateStatement.setTime(1, Time.valueOf(operatingHours.getOpeningTime()));
                        updateStatement.setTime(2, Time.valueOf(operatingHours.getClosingTime()));
                        updateStatement.setInt(3, operatingHours.getDayOfWeek().getValue());
                        updateStatement.setLong(4, detailChange.getHotelId());
                        updateStatement.executeUpdate();
                        updateStatement.close();
                    }
                    connection.commit();
                    return true;
                case REMOVE_ROOM_CATEGORY:
                    updateStatement = connection.prepareStatement(SQLStatements.deleteRoomCategory);
                    updateStatement.setLong(1, (Long) detailChange.getNewValue());
                    break;
                case ADD_ROOM_CATEGORY:
                    RoomCategory newRoomCategory = (RoomCategory) detailChange.getNewValue();
                    updateStatement = connection.prepareStatement(SQLStatements.insertNewRoomCategory);
                    updateStatement.setString(1, newRoomCategory.getName());
                    updateStatement.setString(2, newRoomCategory.getDescription());
                    updateStatement.setLong(3, newRoomCategory.getMaxOccupants());
                    updateStatement.setLong(4, detailChange.getHotelId());
                    break;
                case UPDATE_ROOM_CATEGORY:
                    RoomCategory updatedRoomCategory = (RoomCategory) detailChange.getNewValue();
                    updateStatement = connection.prepareStatement(SQLStatements.updateRoomCategory);
                    updateStatement.setString(1, updatedRoomCategory.getName());
                    updateStatement.setString(2, updatedRoomCategory.getDescription());
                    updateStatement.setLong(3, updatedRoomCategory.getMaxOccupants());
                    updateStatement.setLong(4, updatedRoomCategory.getId());
                    break;
            }
            int rowsUpdated = updateStatement.executeUpdate();
            connection.commit();
            return rowsUpdated > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while attempting to update hotel details in database");
            return false;
        }

    }

    @Override
    public Set<RoomCategory> getRoomCategories(long hotelId) {
        if (hotelId == 0) {
            return null;
        }
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectHotelRoomCategories)) {
                selectStatement.setLong(1, hotelId);
                ResultSet resultSet = selectStatement.executeQuery();
                Set<RoomCategory> roomCategories = new HashSet<>();
                while (resultSet.next()) {
                    RoomCategory roomCategory = new RoomCategory();
                    roomCategory.setId(resultSet.getLong(1));
                    roomCategory.setName(resultSet.getString(2));
                    roomCategory.setDescription(resultSet.getString(3));
                    roomCategory.setMaxOccupants(resultSet.getLong(4));
                    roomCategories.add(roomCategory);
                }
                connection.commit();
                return roomCategories;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the room categories from the database");
            return null;
        }
    }

    @Override
    public long newHotel(ManagementHotelDetails hotelDetails) {
        if (hotelDetails == null) {
            return -1;
        }

        try (Connection connection = connectionPool.getConnection()) {
            long hotelId;
                try (PreparedStatement insertStatement = connection.prepareStatement(SQLStatements.insertNewHotel)) {
                    insertStatement.setString(1, hotelDetails.getName());
                    insertStatement.setString(2, hotelDetails.getDescription());
                    insertStatement.setString(3, hotelDetails.getStreetAddress());
                    insertStatement.setString(4, hotelDetails.getState());
                    insertStatement.setString(5, hotelDetails.getCity());
                    insertStatement.setString(6, hotelDetails.getPhoneNumber());
                    insertStatement.setLong(7, hotelDetails.getHotelOwner().getId());
                    insertStatement.setLong(8, hotelDetails.getCheckInAge());
                    insertStatement.setLong(9, hotelDetails.getNumOfFloors());
                    insertStatement.setTime(10, Time.valueOf(hotelDetails.getCheckInTime()));
                    insertStatement.setTime(11, Time.valueOf(hotelDetails.getCheckOutTime()));
                    ResultSet resultSet = insertStatement.executeQuery();
                    resultSet.next();
                    hotelId = resultSet.getLong(1);
                }
                PreparedStatement insertStatement;
                for (RoomCategory roomCategory : hotelDetails.getRoomCategories()) {
                    insertStatement = connection.prepareStatement(SQLStatements.insertNewRoomCategory);
                    insertStatement.setString(1, roomCategory.getName());
                    insertStatement.setString(2, roomCategory.getDescription());
                    insertStatement.setLong(3, roomCategory.getMaxOccupants());
                    insertStatement.setLong(4, hotelId);
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }
                for (AmenityType amenity : hotelDetails.getAmenities()) {
                    insertStatement = connection.prepareStatement(SQLStatements.insertNewAmenity);
                    insertStatement.setLong(1, hotelId);
                    insertStatement.setString(2, amenity.name());
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }
                for (OperatingHours operatingHours : hotelDetails.getOperatingHours()) {
                    insertStatement = connection.prepareStatement(SQLStatements.insertNewOperatingHours);
                    insertStatement.setTime(1, Time.valueOf(operatingHours.getOpeningTime()));
                    insertStatement.setTime(2, Time.valueOf(operatingHours.getClosingTime()));
                    insertStatement.setInt(3, operatingHours.getDayOfWeek().getValue());
                    insertStatement.setLong(4, hotelId);
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }
                connection.commit();
                return hotelId;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while attempting to insert new hotel into database");
            return -1;
        }
    }

    @Override
    public boolean updateAvailability(AvailabilityUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getUpdates() == null) {
            return false;
        }
        Connection connection = connectionPool.getConnection();
        PreparedStatement updateStatement = null;
        try {
            for (AvailabilityUpdateTuple update : updateRequest.getUpdates()) {

                switch (update.getAction()) {
                    case REMOVE:
                        updateStatement = connection.prepareStatement(SQLStatements.removeRoomAvailabilitySingleNight);
                        updateStatement.setLong(1, update.getNumOfRooms());
                        updateStatement.setLong(2, updateRequest.getRoomCategoryId());
                        updateStatement.setDate(3, Date.valueOf(update.getDate()));
                        break;
                    case ADD:
                        updateStatement = connection.prepareStatement(SQLStatements.addRoomAvailabilitySingleNight);
                        updateStatement.setLong(1, updateRequest.getRoomCategoryId());
                        updateStatement.setDouble(2, Double.parseDouble(df.format(update.getNightlyRate())));
                        updateStatement.setDate(3, Date.valueOf(update.getDate()));
                        updateStatement.setLong(4, update.getNumOfRooms());
                        updateStatement.setDouble(5, update.getNightlyRate());
                        updateStatement.setLong(6, update.getNumOfRooms());
                        break;
                }
                updateStatement.executeUpdate();
                System.out.println("Executed Update: ");
                System.out.println(update);
                updateStatement.close();
            }
            if (updateRequest.getUpdates().stream().anyMatch((AvailabilityUpdateTuple u) -> u.getAction() == UpdateAction.ADD)) {
                try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.cleanUpAvailability)) {
                    deleteStatement.executeUpdate();
                }
            }

            connection.commit();
            return true;
        }
        catch (SQLException e) {
            try {
                connection.rollback();
            }
            catch (SQLException se) {
                System.out.println("An error occurred while attempting to roll back the transaction");
            }
            e.printStackTrace();
            System.out.println("An error occurred while attempting to update availability in the database");
            return false;
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e) {
                System.out.println("An error occurred while closing the connection");
                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<AmenityType> getAmenities(long hotelId) {
        return mutualDAO.getAmenities(hotelId);
    }

    @Override
    public Set<OperatingHours> getOperatingHours(long hotelId) {
        return mutualDAO.getOperatingHours(hotelId);
    }
}
