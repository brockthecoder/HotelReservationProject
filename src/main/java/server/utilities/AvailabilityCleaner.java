package server.utilities;

import mutual.utilities.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AvailabilityCleaner {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(Properties.jdbcUrl, Properties.dbUsername, Properties.dbPassword)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.execute(SQLStatements.cleanUpAvailability);
                connection.commit();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
