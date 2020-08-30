package server.api;

import java.sql.Connection;

public interface ConnectionPool {

    Connection getConnection();

    void shutdown();
}
