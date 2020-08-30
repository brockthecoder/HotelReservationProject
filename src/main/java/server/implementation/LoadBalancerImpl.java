package server.implementation;

import mutual.utilities.Properties;
import server.api.ClientRequestHandler;
import server.api.ConnectionPool;
import server.api.LoadBalancer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancerImpl implements LoadBalancer {

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private ConnectionPool connectionPool;

    public LoadBalancerImpl() {
        try {
            this.serverSocket = new ServerSocket(Properties.port);
            this.executorService = Executors.newCachedThreadPool();
            this.connectionPool = new ConnectionPoolImpl();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("There was an error while attempting to initialize the server socket");
        }
    }

    @Override
    public void initialize() {
        while (true) {
            try {
                Socket newSocket = serverSocket.accept();
                ClientRequestHandler clientRequestHandler = new ClientRequestHandlerImpl(newSocket, connectionPool);
                executorService.execute(clientRequestHandler);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("There was an error while attempting to connect to a client");
            }
        }
    }
}
