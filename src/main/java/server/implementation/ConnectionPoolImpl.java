package server.implementation;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mutual.utilities.Properties;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;
import server.api.ConnectionPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolImpl implements ConnectionPool {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    static {
        config.setJdbcUrl(Properties.jdbcUrl);
        config.setAutoCommit(false);
        config.setUsername(Properties.dbUsername);
        config.setPassword(Properties.dbPassword);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new RuntimeException("An error occurred while attempting to connect to the database");
        }
    }

    @Override
    public void shutdown() {
        dataSource.close();
    }
}
