package server.implementation;

import client.customer.model.HotelDetails;
import client.management.model.AmenityType;
import client.management.model.OperatingHours;
import client.mutual.model.CustomerReservation;
import server.api.ConnectionPool;
import server.api.MutualDAO;
import server.utilities.SQLStatements;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

public class MutualDAOImpl implements MutualDAO {

    private ConnectionPool connectionPool;

    public MutualDAOImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public HotelDetails getHotel(long hotelId) {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectHotel)) {
                selectStatement.setLong(1, hotelId);
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                HotelDetails hotel = new HotelDetails();
                hotel.setId(hotelId);
                hotel.setName(resultSet.getString(1));
                hotel.setDescription(resultSet.getString(2));
                hotel.setStreetAddress(resultSet.getString(3));
                hotel.setState(resultSet.getString(4));
                hotel.setCity(resultSet.getString(5));
                hotel.setPhoneNumber(resultSet.getString(6));
                hotel.setCheckInAge(resultSet.getLong(7));
                hotel.setNumOfFloors(resultSet.getLong(8));
                hotel.setCheckInTime(resultSet.getTime(9).toLocalTime());
                hotel.setCheckOutTime(resultSet.getTime(10).toLocalTime());
                hotel.setAmenities(getAmenities(hotelId));
                hotel.setOperatingHours(getOperatingHours(hotelId));
                connection.commit();
                return hotel;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the hotel from the database");
            return null;
        }
    }

    @Override
    public long newReservation(CustomerReservation reservationDetails) {
        if (reservationDetails == null || reservationDetails.getHotel() == null || reservationDetails.getCustomer() == null ||
                reservationDetails.getCheckInDate() == null || reservationDetails.getCheckOutDate() == null ||
                reservationDetails.getRoomListing() == null || reservationDetails.getPaymentInfo() == null
        ) {
            return -1;
        }
        LocalDate lastNight = reservationDetails.getCheckOutDate().minus(Period.ofDays(1));
        long numOfNights = reservationDetails.getCheckOutDate().toEpochDay() - reservationDetails.getCheckInDate().toEpochDay();
        Connection connection = connectionPool.getConnection();
        try {
            long customerId;
            try (PreparedStatement insertStatement = connection.prepareStatement(SQLStatements.insertNewCustomer)) {
                insertStatement.setString(1, reservationDetails.getCustomer().getFirstName());
                insertStatement.setString(2, reservationDetails.getCustomer().getLastName());
                insertStatement.setString(3, reservationDetails.getCustomer().getPhoneNumber());
                insertStatement.setString(4, reservationDetails.getCustomer().getEmail());
                insertStatement.setString(5, reservationDetails.getCustomer().getStreetAddress());
                insertStatement.setString(6, reservationDetails.getCustomer().getState());
                insertStatement.setString(7, reservationDetails.getCustomer().getCity());
                ResultSet resultSet = insertStatement.executeQuery();
                resultSet.next();
                customerId = resultSet.getLong(1);
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(SQLStatements.verifyRoomAvailability)){
                insertStatement.setLong(1, reservationDetails.getRoomListing().getRoomCategoryId());
                insertStatement.setDate(2, Date.valueOf(reservationDetails.getCheckInDate()));
                insertStatement.setDate(3, Date.valueOf(lastNight));
                ResultSet resultSet = insertStatement.executeQuery();
                resultSet.next();
                long numOfNightsAvailable = resultSet.getLong(1);
                if (numOfNights > numOfNightsAvailable) {
                    connection.rollback();
                    return -1;
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.removeAvailabilityForSingleRoom)) {
                deleteStatement.setLong(1, reservationDetails.getRoomListing().getRoomCategoryId());
                deleteStatement.setDate(2, Date.valueOf(reservationDetails.getCheckInDate()));
                deleteStatement.setDate(3, Date.valueOf(lastNight));
                long numOfNightsDeleted = deleteStatement.executeUpdate();
                if (numOfNightsDeleted < numOfNights) {
                    connection.rollback();
                    return -1;
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.cleanUpAvailability)) {
                deleteStatement.executeUpdate();
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(SQLStatements.insertNewReservation)) {
                insertStatement.setLong(1, customerId);
                insertStatement.setDate(2, Date.valueOf(reservationDetails.getCheckInDate()));
                insertStatement.setDate(3, Date.valueOf(reservationDetails.getCheckOutDate()));
                insertStatement.setLong(4, reservationDetails.getRoomListing().getRoomCategoryId());
                insertStatement.setDouble(5, reservationDetails.getTotal());
                insertStatement.setDouble(6, reservationDetails.getRoomListing().getNightlyRate());
                insertStatement.setString(7, reservationDetails.getPaymentInfo().getCardNumber());
                insertStatement.setString(8, reservationDetails.getPaymentInfo().getCvv());
                insertStatement.setString(9, reservationDetails.getPaymentInfo().getZipCode());
                insertStatement.setString(10, reservationDetails.getPaymentInfo().getCardHolderName());
                insertStatement.setString(11, reservationDetails.getPaymentInfo().getExpirationDate());
                insertStatement.setString(12, reservationDetails.getPaymentInfo().getCreditCardType().name());
                insertStatement.setLong(13, reservationDetails.getHotel().getId());
                ResultSet resultSet = insertStatement.executeQuery();
                resultSet.next();
                long reservationId = resultSet.getLong(1);
                connection.commit();
                return reservationId;
            }
        }
        catch (SQLException e) {
            try {
                connection.rollback();
            }
            catch (SQLException s) {
                s.printStackTrace();
                System.out.println("Error occurred while rolling back transaction");
            }
            return -1;
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e) {
                System.out.println("Error while closing connection");
            }
        }

    }

    @Override
    public Set<OperatingHours> getOperatingHours(long hotelId) {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectHotelOperatingHours)) {
                Set<OperatingHours> operatingHours = new HashSet<>();
                selectStatement.setLong(1, hotelId);
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    OperatingHours hours = new OperatingHours();
                    hours.setOpeningTime(resultSet.getTime(1).toLocalTime());
                    hours.setClosingTime(resultSet.getTime(2).toLocalTime());
                    hours.setDayOfWeek(DayOfWeek.of(resultSet.getInt(3)));
                    operatingHours.add(hours);
                }
                connection.commit();
                return operatingHours;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the operating hours from the database");
            return null;
        }
    }

    @Override
    public Set<AmenityType> getAmenities(long hotelId) {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectHotelAmenities)) {
                Set<AmenityType> amenities = new HashSet<>();
                selectStatement.setLong(1, hotelId);
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    AmenityType amenity = AmenityType.valueOf(resultSet.getString(1));
                    amenities.add(amenity);
                }
                connection.commit();
                return amenities;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the amenities from the database");
            return null;
        }
    }
}
