package server.utilities;

public class SQLStatements {

    public static final String selectAccount =
            "select management_users.id as \"management_user_id\", hotels.id as \"hotel_id\", hotels.name as \"hotel_name\", \n" +
                    "hotels.description as \"hotel_description\", hotels.street_address, hotels.city, hotels.state,hotels.phone_number, \n" +
                    "hotels.check_in_age, hotels.num_of_floors, hotels.check_in_time, hotels.check_out_time\n" +
                    "from management_users\n" +
                    "inner join hotels on hotels.management_user_id = management_users.id\n" +
                    "where management_users.email = ? AND management_users.password = ?";

    public static final String findIfAccountExists = "SELECT COUNT(*) FROM management_users WHERE management_users.email = ?";

    public static final String loginIsValid = "select id from management_users where email = ? AND password = ?";

    public static final String insertNewAccount =
            "INSERT INTO management_users(email, password) " +
                    "VALUES(?, ?) RETURNING id";


    public static final String deleteHotel = "DELETE FROM hotels WHERE id = ?";

    public static final String selectReservationsByIdAndHotelId =
            "select customer_reservations.id, customers.first_name, customers.last_name, " +
                    "customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city, check_in_date, check_out_date, " +
                    "room_category_id, room_categories.name, room_categories.description, " +
                    "room_categories.max_occupants, customer_reservations.nightly_rate, " +
                    "total_price, credit_card_number, cvv, card_holder_name, card_expiration_date, " +
                    "credit_card_zip_code, card_type " +
                    "from customer_reservations " +
                    "inner join customers on customers.id = customer_reservations.customer_id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "where customer_reservations.id = ? AND customer_reservations.hotel_id = ?";

    public static final String selectReservationsByFullNameAndHotelId =
            "select customer_reservations.id, customers.first_name, customers.last_name, " +
                    "customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city, check_in_date, check_out_date, " +
                    "room_category_id, room_categories.name, room_categories.description, " +
                    "room_categories.max_occupants, customer_reservations.nightly_rate, " +
                    "total_price, credit_card_number, cvv, card_holder_name, card_expiration_date, " +
                    "credit_card_zip_code, card_type " +
                    "from customer_reservations " +
                    "inner join customers on customers.id = customer_reservations.customer_id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "where customers.first_name = ? AND customers.last_name = ? " +
                    "AND customer_reservations.hotel_id = ?";

    public static final String selectReservationsByEmailAndHotelID =
            "select customer_reservations.id, customers.first_name, customers.last_name, " +
                    "customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city, check_in_date, check_out_date, " +
                    "room_category_id, room_categories.name, room_categories.description, " +
                    "room_categories.max_occupants, customer_reservations.nightly_rate, " +
                    "total_price, credit_card_number, cvv, card_holder_name, card_expiration_date, " +
                    "credit_card_zip_code, card_type " +
                    "from customer_reservations " +
                    "inner join customers on customers.id = customer_reservations.customer_id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "where customers.email = ? AND customer_reservations.hotel_id = ?";


    public static final String selectUpcomingReservations =
            "select customer_reservations.id, customers.first_name, customers.last_name, " +
                    "customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city, check_in_date, check_out_date, " +
                    "room_category_id, room_categories.name, room_categories.description, " +
                    "room_categories.max_occupants, customer_reservations.nightly_rate, " +
                    "total_price, credit_card_number, cvv, card_holder_name, card_expiration_date, " +
                    "credit_card_zip_code, card_type " +
                    "from customer_reservations " +
                    "inner join customers on customers.id = customer_reservations.customer_id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "where check_in_date <= (current_date + 7) AND customer_reservations.hotel_id = ?";

    public static final String selectAvailabilityByQuery =
            "select room_categories.id, room_categories.name, room_categories.description, room_categories.max_occupants, sum(nightly_rate) " +
                    "from availability_listings " +
                    "inner join room_categories on availability_listings.room_category_id = room_categories.id " +
                    "where (avail_date between ? AND ?) AND (room_categories.hotel_id = ?) AND (room_categories.max_occupants >= ?) " +
                    "group by room_categories.id having count(availability_listings.id) = ?";

    public static final String selectAvailabilityByRoomCategory =
            "select avail_date, num_of_rooms, nightly_rate " +
                    "from availability_listings " +
                    "where (room_category_id = ?) AND (avail_date BETWEEN ? AND ?)";

    public static final String insertNewCustomer =
            "INSERT INTO customers(first_name, last_name, phone_number, email, street_address, state, city) " +
                    "values( ?, ?, ?, ?, ?, ?, ?) returning customers.id";

    public static final String insertNewReservation =
            "insert into customer_reservations(customer_id, check_in_date, check_out_date, room_category_id, total_price, nightly_rate, credit_card_number, cvv, credit_card_zip_code, card_holder_name, card_expiration_date, card_type, hotel_id) " +
                    "values (?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning customer_reservations.id";

    public static final String verifyRoomAvailability =
            "select count(*) from availability_listings where room_category_id = ? AND avail_date BETWEEN ? AND ?";

    public static final String removeAvailabilityForSingleRoom =
            "update availability_listings set num_of_rooms = (num_of_rooms - 1) " +
                    "where room_category_id = ? AND avail_date BETWEEN ? AND ?";

    public static final String cleanUpAvailability = "delete from availability_listings where num_of_rooms <= 0 OR avail_date < current_date";

    public static final String updateHotelName = "update hotels set name = ? where hotels.id = ?";

    public static final String updateHotelDescription = "update hotels set description = ? where hotels.id = ?";

    public static final String updateHotelPhoneNumber = "update hotels set phone_number = ? where hotels.id = ?";

    public static final String updateHotelCheckInAge = "update hotels set check_in_age = ? where hotels.id = ?";

    public static final String updateHotelCheckInTime = "update hotels set check_in_time = ? where hotels.id = ?";

    public static final String updateHotelCheckOutTime = "update hotels set check_out_time = ? where hotels.id = ?";

    public static final String deleteHotelAmenities = "delete from hotel_amenities where hotel_id = ?";

    public static final String insertNewAmenity = "insert into hotel_amenities(hotel_id, amenity_name) values(?, ?)";

    public static final String deleteHotelOperatingHours = "delete from hotel_operating_hours where hotel_id = ?";

    public static final String insertNewOperatingHours =
            "insert into hotel_operating_hours(opening_time, closing_time, day_of_week, hotel_id)" +
                    "values (?, ?, ?, ?)";

    public static final String deleteRoomCategory = "delete from room_categories where id = ?";

    public static final String insertNewRoomCategory =
            "insert into room_categories(name, description, max_occupants, hotel_id) values(?, ?, ?, ?)";

    public static final String updateRoomCategory =
            "update room_categories set name = ?, description = ?, max_occupants = ? where id = ?";

    public static final String selectHotelRoomCategories =
            "select id, name, description, max_occupants from room_categories where hotel_id = ?";

    public static final String insertNewHotel =
            "insert into hotels(name, description, street_address, state, city, phone_number, management_user_id, check_in_age, num_of_floors, check_in_time, check_out_time) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning id";

    public static final String removeRoomAvailabilitySingleNight =
            "update availability_listings set num_of_rooms = (num_of_rooms - ?) where room_category_id = ? AND avail_date = ?";

    public static final String addRoomAvailabilitySingleNight =
            "insert into availability_listings(room_category_id, nightly_rate, avail_date, num_of_rooms) " +
                    "values (?, ?, ?, ?) on conflict on constraint room_category_avail_date_unique_constraint " +
                    "do update set nightly_rate = ?, num_of_rooms = (availability_listings.num_of_rooms + ?)";



    public static final String selectHotelAmenities = "select amenity_name from hotel_amenities where hotel_id = ?";

    public static final String selectHotelOperatingHours = "select opening_time, closing_time, day_of_week from hotel_operating_hours where hotel_id = ?";

    public static final String selectHotel =
            "select name, description, street_address, state, city, phone_number, check_in_age, num_of_floors, check_in_time, check_out_time from hotels where id = ?";

    public static final String selectReservationsByID =
            "select customer_reservations.id, customer_reservations.check_in_date, customer_reservations.check_out_date, " +
                    "room_categories.id, room_categories.name, room_categories.description, room_categories.max_occupants, " +
                    "nightly_rate, customer_reservations.total_price, credit_card_number, cvv, credit_card_zip_code, card_holder_name, " +
                    "card_expiration_date, card_type, hotels.id, hotels.description, hotels.name, hotels.street_address, " +
                    "hotels.city, hotels.state, hotels.phone_number, hotels.check_in_age, hotels.num_of_floors, hotels.check_in_time, " +
                    "hotels.check_out_time, customers.first_name, customers.last_name, customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city from customer_reservations " +
                    "inner join customers on customer_reservations.customer_id = customers.id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "inner join hotels on room_categories.hotel_id = hotels.id " +
                    "where customer_reservations.id = ? AND customers.last_name = ?";

    public static final String selectReservationsByEmail =
            "select customer_reservations.id, customer_reservations.check_in_date, customer_reservations.check_out_date, " +
                    "room_categories.id, room_categories.name, room_categories.description, room_categories.max_occupants, " +
                    "nightly_rate, customer_reservations.total_price, credit_card_number, cvv, credit_card_zip_code, card_holder_name, " +
                    "card_expiration_date, card_type, hotels.id, hotels.description, hotels.name, hotels.street_address, " +
                    "hotels.city, hotels.state, hotels.phone_number, hotels.check_in_age, hotels.num_of_floors, hotels.check_in_time, " +
                    "hotels.check_out_time, customers.first_name, customers.last_name, customers.phone_number, customers.email, customers.street_address, " +
                    "customers.state, customers.city from customer_reservations " +
                    "inner join customers on customer_reservations.customer_id = customers.id " +
                    "inner join room_categories on customer_reservations.room_category_id = room_categories.id " +
                    "inner join hotels on room_categories.hotel_id = hotels.id " +
                    "where customers.email = ? AND customers.last_name = ?";

    public static final String deleteReservation = "delete from customer_reservations where id = ? returning customer_id, check_in_date, check_out_date, nightly_rate, room_category_id";

    public static final String deleteCustomer = "delete from customers where id = ?";

    public static final String updatePaymentInfo =
            "update customer_reservations set credit_card_number = ?, cvv = ?, credit_card_zip_code = ?, " +
                    "card_holder_name = ?, card_type = ?, card_expiration_date = ? where id = ?";

    public static final String selectReservationDates = "select check_in_date, check_out_date, room_category_id, nightly_rate, total_price from customer_reservations where id = ?";

    public static final String updateReservationDates = "update customer_reservations set check_in_date = ?, check_out_date = ?, total_price = ? where id = ?";

    public static final String selectAllAvailability =
            "select trunc(avg(availability_listings.nightly_rate), 2), room_categories.id, room_categories.name, " +
                    "room_categories.description, room_categories.max_occupants, room_categories.hotel_id " +
                    "from availability_listings inner join room_categories on availability_listings.room_category_id = room_categories.id " +
                    "inner join hotels on room_categories.hotel_id = hotels.id " +
                    "where (avail_date BETWEEN ? AND ?) AND hotels.city = ? AND room_categories.max_occupants >= ? " +
                    "group by room_categories.id having count(*) = ?";

    public static final String selectPriceForExtraNights =
            "select sum(availability_listings.nightly_rate) from availability_listings " +
                    "where (room_category_id = ?) AND (avail_date between ? AND ?) " +
                    "group by room_category_id having count(*) = ?";
}
