package server.implementation;

import client.customer.model.HotelDetails;
import client.customer.model.RoomAvailabilityQuery;
import client.mutual.model.Customer;
import client.mutual.model.CustomerReservation;
import client.mutual.model.RoomListing;
import mutual.model.CustomerReservationQuery;
import mutual.model.enums.CreditCardType;
import server.api.ConnectionPool;
import server.api.CustomerDAO;
import server.api.MutualDAO;
import server.model.ExtraNightsRequest;
import server.model.ReservationDateDetails;
import server.model.ReservationModificationRequest;
import server.utilities.SQLStatements;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CustomerDAOImpl implements CustomerDAO {

    private final ConnectionPool connectionPool;

    private MutualDAO mutualDAO;

    public CustomerDAOImpl(ConnectionPool connectionPool, MutualDAO mutualDAO) {
        this.connectionPool = connectionPool;
        this.mutualDAO = mutualDAO;
    }

    @Override
    public HotelDetails getHotel(long hotelId) {
        return mutualDAO.getHotel(hotelId);
    }

    @Override
    public long newReservation(CustomerReservation reservation) {
        return mutualDAO.newReservation(reservation);
    }

    @Override
    public List<CustomerReservation> getReservationsById(CustomerReservationQuery query) {
        try (Connection connection =  connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectReservationsByID)) {
                selectStatement.setLong(1, query.getReservationId());
                selectStatement.setString(2, query.getLastName());
                ResultSet resultSet = selectStatement.executeQuery();
                List<CustomerReservation> matchingReservations = parseReservationsResultSet(resultSet);
                connection.commit();
                return matchingReservations;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while attempting to load the reservations from the database");
            return null;
        }
    }

    private List<CustomerReservation> parseReservationsResultSet(ResultSet resultSet) {
        try {
            List<CustomerReservation> reservations = new ArrayList<>();
            while (resultSet.next()) {
                CustomerReservation reservation = new CustomerReservation();
                reservation.setId(resultSet.getLong(1));
                reservation.setCheckInDate(resultSet.getDate(2).toLocalDate());
                reservation.setCheckOutDate(resultSet.getDate(3).toLocalDate());
                RoomListing roomListing = new RoomListing();
                roomListing.setRoomCategoryId(resultSet.getLong(4));
                roomListing.setRoomName(resultSet.getString(5));
                roomListing.setDescription(resultSet.getString(6));
                roomListing.setMaxOccupants(resultSet.getLong(7));
                roomListing.setNightlyRate(resultSet.getDouble(8));
                reservation.setRoomListing(roomListing);
                reservation.setTotal(resultSet.getDouble(9));
                reservation.getPaymentInfo().setCardNumber(resultSet.getString(10));
                reservation.getPaymentInfo().setCvv(resultSet.getString(11));
                reservation.getPaymentInfo().setZipCode(resultSet.getString(12));
                reservation.getPaymentInfo().setCardHolderName(resultSet.getString(13));
                reservation.getPaymentInfo().setExpirationDate(resultSet.getString(14));
                reservation.getPaymentInfo().setCreditCardType(CreditCardType.valueOf(resultSet.getString(15)));
                HotelDetails hotel = new HotelDetails();
                hotel.setId(resultSet.getLong(16));
                hotel.setDescription(resultSet.getString(17));
                hotel.setName(resultSet.getString(18));
                hotel.setStreetAddress(resultSet.getString(19));
                hotel.setCity(resultSet.getString(20));
                hotel.setState(resultSet.getString(21));
                hotel.setPhoneNumber(resultSet.getString(22));
                hotel.setCheckInAge(resultSet.getLong(23));
                hotel.setNumOfFloors(resultSet.getLong(24));
                hotel.setCheckInTime(resultSet.getTime(25).toLocalTime());
                hotel.setCheckOutTime(resultSet.getTime(26).toLocalTime());
                hotel.setOperatingHours(mutualDAO.getOperatingHours(hotel.getId()));
                hotel.setAmenities(mutualDAO.getAmenities(hotel.getId()));
                reservation.setHotel(hotel);
                Customer customer = new Customer();
                customer.setFirstName(resultSet.getString(27));
                customer.setLastName(resultSet.getString(28));
                customer.setPhoneNumber(resultSet.getString(29));
                customer.setEmail(resultSet.getString(30));
                customer.setStreetAddress(resultSet.getString(31));
                customer.setState(resultSet.getString(32));
                customer.setCity(resultSet.getString(33));
                reservation.setCustomer(customer);
                reservations.add(reservation);
            }
            return reservations;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while parsing the reservation resultset");
            return null;
        }
    }

    @Override
    public List<CustomerReservation> getReservationsByEmail(CustomerReservationQuery query) {
        try (Connection connection =  connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectReservationsByEmail)) {
                selectStatement.setString(1, query.getEmail());
                selectStatement.setString(2, query.getLastName());
                ResultSet resultSet = selectStatement.executeQuery();
                List<CustomerReservation> matchingReservations = parseReservationsResultSet(resultSet);
                connection.commit();
                return matchingReservations;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while attempting to load the reservations from the database");
            return null;
        }
    }

    @Override
    public boolean modifyReservation(ReservationModificationRequest modificationRequest) {
        if (modificationRequest == null || modificationRequest.getModificationType() == null || modificationRequest.getReservation() == null) {
            System.err.println("There was a null value in the modification request");
            return false;
        }
        switch (modificationRequest.getModificationType()) {
            case ADD_NIGHTS:
                return addNightsToReservation(modificationRequest.getReservation());
            case REMOVE_NIGHTS:
                return removeNightsFromReservation(modificationRequest.getReservation());
            case UPDATE_PAYMENT_INFO:
                return updateReservationPaymentInfo(modificationRequest.getReservation());
            case CANCEL:
                return cancelReservation(modificationRequest.getReservation());
        }
        return false;
    }

    private boolean addNightsOfAvailability(List<LocalDate> dates, long roomCategoryId, double nightlyRate) {
        if (dates == null || dates.size() == 0) {
            return true;
        }
        try (Connection connection = connectionPool.getConnection()) {
            for (LocalDate date : dates) {
                try(PreparedStatement updateStatement = connection.prepareStatement(SQLStatements.addRoomAvailabilitySingleNight)) {
                    updateStatement.setLong(1, roomCategoryId);
                    updateStatement.setDouble(2, nightlyRate);
                    updateStatement.setDate(3, Date.valueOf(date));
                    updateStatement.setInt(4, 1);
                    updateStatement.setDouble(5, nightlyRate);
                    updateStatement.setInt(6, 1);
                    if (updateStatement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
            }
            connection.commit();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("There was an error while adding availability for a room in the database");
            return false;
        }
    }

    private boolean cancelReservation(CustomerReservation reservation) {
        if (reservation == null) {
            return false;
        }
        try (Connection connection = connectionPool.getConnection()) {
            long customerId;
            List<LocalDate> dates;
            long roomCategoryId;
            double nightlyRate;
            try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.deleteReservation)) {
                deleteStatement.setLong(1, reservation.getId());
                ResultSet resultSet = deleteStatement.executeQuery();
                if (resultSet.next()) {
                    customerId = resultSet.getLong(1);
                    LocalDate checkInDate = resultSet.getDate(2).toLocalDate();
                    LocalDate checkOutDate = resultSet.getDate(3).toLocalDate();
                    dates = LongStream.range(checkInDate.toEpochDay(), checkOutDate.toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
                    nightlyRate = resultSet.getDouble(4);
                    roomCategoryId = resultSet.getLong(5);
                }
                else {
                    connection.commit();
                    System.err.println("There were no reservations with the specified id");
                    return false;
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.deleteCustomer)) {
                deleteStatement.setLong(1, customerId);
            }
            boolean availabilityWasUpdated = addNightsOfAvailability(dates, roomCategoryId, nightlyRate);

            if (availabilityWasUpdated) {
                connection.commit();
                return true;
            }
            connection.rollback();
            return false;

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while removing a reservation from the database");
            return false;
        }
    }

    private boolean updateReservationPaymentInfo(CustomerReservation reservation) {
        if (reservation == null || reservation.getPaymentInfo() == null) {
            return false;
        }
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement updateStatement = connection.prepareStatement(SQLStatements.updatePaymentInfo)) {
                updateStatement.setString(1, reservation.getPaymentInfo().getCardNumber());
                updateStatement.setString(2, reservation.getPaymentInfo().getCvv());
                updateStatement.setString(3, reservation.getPaymentInfo().getZipCode());
                updateStatement.setString(4, reservation.getPaymentInfo().getCardHolderName());
                updateStatement.setString(5, reservation.getPaymentInfo().getCreditCardType().name());
                updateStatement.setString(6, reservation.getPaymentInfo().getExpirationDate());
                updateStatement.setLong(7, reservation.getId());
                int numOfRowsUpdated = updateStatement.executeUpdate();
                if (numOfRowsUpdated == 1) {
                    connection.commit();
                    return true;
                }
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while updating payment info in the database");
            return false;
        }
    }

    private ReservationDateDetails getDateDetails(long reservationID) throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectReservationDates)) {
                selectStatement.setLong(1, reservationID);
                ResultSet resultSet = selectStatement.executeQuery();
                if (!resultSet.next()) {
                    connection.rollback();
                    return null;
                }
                LocalDate checkInDate = resultSet.getDate(1).toLocalDate();
                LocalDate checkOutDate = resultSet.getDate(2).toLocalDate();
                List<LocalDate> dates = LongStream.range(checkInDate.toEpochDay(), checkOutDate.toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
                long roomCategoryId = resultSet.getLong(3);
                double nightlyRate = resultSet.getDouble(4);
                double total = resultSet.getDouble(5);
                connection.commit();
                return new ReservationDateDetails(dates, roomCategoryId, nightlyRate, total);
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
    }

    private boolean removeNightsFromReservation(CustomerReservation reservation) {
        if (reservation == null) {
            return false;
        }
        List<LocalDate> reservationDates = LongStream.range(reservation.getCheckInDate().toEpochDay(), reservation.getCheckOutDate().toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());

        try (Connection connection = connectionPool.getConnection()) {
            ReservationDateDetails dateDetails = getDateDetails(reservation.getId());
            try (PreparedStatement updateStatement = connection.prepareStatement(SQLStatements.updateReservationDates)) {
                updateStatement.setDate(1, Date.valueOf(reservation.getCheckInDate()));
                updateStatement.setDate(2, Date.valueOf(reservation.getCheckOutDate()));
                double total = reservationDates.size() * dateDetails.getNightlyRate();
                updateStatement.setDouble(3, total);
                updateStatement.setLong(4, reservation.getId());
                int numOfRowsUpdated = updateStatement.executeUpdate();
                if (numOfRowsUpdated != 1) {
                    connection.rollback();
                    return false;
                }
            }
            dateDetails.getDates().removeAll(reservationDates);
            boolean datesWereAddedToAvailability = addNightsOfAvailability(dateDetails.getDates(), dateDetails.getRoomCategoryId(), dateDetails.getNightlyRate());
            if (datesWereAddedToAvailability) {
                connection.commit();
                return true;
            }
            connection.rollback();
            return false;

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while removing nights from reservation in the database");
            return false;
        }
    }

    private boolean removeNightsOfAvailability(List<LocalDate> nights, long roomCategoryId) {
        if (nights == null || nights.size() == 0) {
            return true;
        }
        try (Connection connection = connectionPool.getConnection()){
            for (LocalDate night : nights) {
                try (PreparedStatement updateStatement = connection.prepareStatement(SQLStatements.removeRoomAvailabilitySingleNight)) {
                    updateStatement.setInt(1, 1);
                    updateStatement.setLong(2, roomCategoryId);
                    updateStatement.setDate(3, Date.valueOf(night));
                    updateStatement.executeUpdate();
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(SQLStatements.cleanUpAvailability)) {
                deleteStatement.executeUpdate();
            }
            connection.commit();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while removing availability from the database");
            return false;
        }
    }

    private boolean addNightsToReservation(CustomerReservation reservation) {
        if (reservation == null) {
            return false;
        }
        try {
            List<LocalDate> newDates = LongStream.range(reservation.getCheckInDate().toEpochDay(), reservation.getCheckOutDate().toEpochDay()).mapToObj(LocalDate::ofEpochDay).collect(Collectors.toList());
            ReservationDateDetails dateDetails = getDateDetails(reservation.getId());
            if (dateDetails == null) {
                return false;
            }
            newDates.removeAll(dateDetails.getDates());
            ExtraNightsRequest extraNightsRequest = new ExtraNightsRequest();
            extraNightsRequest.setRoomCategoryId(reservation.getRoomListing().getRoomCategoryId());
            extraNightsRequest.setExtraNights(newDates);
            if (Double.compare(getPriceForExtraNights(extraNightsRequest) + dateDetails.getTotal(), reservation.getTotal()) > 1) {
                return false;
            }
            try (Connection connection = connectionPool.getConnection()) {
                try (PreparedStatement updateStatement = connection.prepareStatement(SQLStatements.updateReservationDates)) {
                    updateStatement.setDate(1, Date.valueOf(reservation.getCheckInDate()));
                    updateStatement.setDate(2, Date.valueOf(reservation.getCheckOutDate()));
                    updateStatement.setDouble(3, reservation.getTotal());
                    updateStatement.setLong(4, reservation.getId());
                    int numOfRowsUpdated = updateStatement.executeUpdate();
                    if (numOfRowsUpdated != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                boolean nightsWereRemoved = removeNightsOfAvailability(newDates, reservation.getRoomListing().getRoomCategoryId());
                if (nightsWereRemoved) {
                    connection.commit();
                    return true;
                }
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while adding nights to reservation in the database");
            return false;
        }
    }

    @Override
    public Map<HotelDetails, List<RoomListing>> getRoomAvailability(RoomAvailabilityQuery query) {
        if (query == null) {
            return null;
        }
        long numOfNights = query.getCheckOutDate().toEpochDay() - query.getCheckInDate().toEpochDay();
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectAllAvailability)) {
                selectStatement.setDate(1, Date.valueOf(query.getCheckInDate()));
                selectStatement.setDate(2, Date.valueOf(query.getCheckOutDate().minusDays(1)));
                selectStatement.setString(3, query.getCity().toLowerCase());
                selectStatement.setLong(4, query.getNumOfPeople());
                selectStatement.setLong(5, numOfNights);
                ResultSet resultSet = selectStatement.executeQuery();
                Map<Long, HotelDetails> hotelMap = new HashMap<>();
                Map<HotelDetails, List<RoomListing>> availability = new HashMap<>();
                while (resultSet.next()) {
                    RoomListing roomListing = new RoomListing();
                    roomListing.setNightlyRate(resultSet.getDouble(1));
                    roomListing.setRoomCategoryId(resultSet.getLong(2));
                    roomListing.setRoomName(resultSet.getString(3));
                    roomListing.setDescription(resultSet.getString(4));
                    roomListing.setMaxOccupants(resultSet.getLong(5));
                    long hotelId = resultSet.getLong(6);
                    if (!hotelMap.containsKey(hotelId)) {
                        HotelDetails hotelDetails = mutualDAO.getHotel(hotelId);
                        List<RoomListing> roomListings = new ArrayList<>();
                        roomListings.add(roomListing);
                        availability.put(hotelDetails, roomListings);
                        hotelMap.put(hotelId, hotelDetails);
                    }
                    else {
                        availability.get(hotelMap.get(hotelId)).add(roomListing);
                    }
                }
                connection.commit();
                return availability;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while searching for availability in the database");
            return null;
        }
    }

    @Override
    public double getPriceForExtraNights(ExtraNightsRequest extraNightsRequest) {
        if (extraNightsRequest == null) {
            return -1;
        }
        if (extraNightsRequest.getExtraNights().size() == 0) {
            return 0.0;
        }
        Collections.sort(extraNightsRequest.getExtraNights());
        LocalDate firstDate = extraNightsRequest.getExtraNights().get(0);
        LocalDate lastDate = extraNightsRequest.getExtraNights().get(extraNightsRequest.getExtraNights().size() -1 );
        try (Connection connection = connectionPool.getConnection()){
            try (PreparedStatement selectStatement = connection.prepareStatement(SQLStatements.selectPriceForExtraNights)) {
                selectStatement.setLong(1, extraNightsRequest.getRoomCategoryId());
                selectStatement.setDate(2, Date.valueOf(firstDate));
                selectStatement.setDate(3, Date.valueOf(lastDate));
                selectStatement.setInt(4, extraNightsRequest.getExtraNights().size());
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    double priceForExtraNights = resultSet.getDouble(1);
                    connection.commit();
                    return priceForExtraNights;
                }
                connection.commit();
                return -1;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while get the price for the extra nights");
            return -1;
        }
    }
}
