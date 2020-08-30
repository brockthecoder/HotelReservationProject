package server.utilities;

import mutual.model.AvailabilityUpdateRequest;
import server.api.ConnectionPool;
import server.api.ManagementDAO;
import server.api.MutualDAO;
import server.implementation.ManagementDAOImpl;

public class AvailabilityWorker implements Runnable{

    private ConnectionPool connectionPool;
    private MutualDAO mutualDAO;
    private AvailabilityUpdateRequest request;

    public AvailabilityWorker(ConnectionPool connectionPool, MutualDAO mutualDAO, AvailabilityUpdateRequest request) {
        this.connectionPool = connectionPool;
        this.mutualDAO = mutualDAO;
        this.request = request;
    }

    @Override
    public void run() {
        System.out.println("Inserting availability in thread: " + Thread.currentThread().getName());
        ManagementDAO dao = new ManagementDAOImpl(connectionPool, mutualDAO);
        dao.updateAvailability(request);
    }
}
