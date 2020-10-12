package server.implementation;

import server.api.*;
import server.model.Request;

import java.io.IOException;
import java.net.Socket;

public class ClientRequestHandlerImpl implements ClientRequestHandler {

    private ServerCommunicationSocket communicationSocket;
    private ManagementRequestHandler managementRequestHandler;
    private CustomerRequestHandler customerRequestHandler;

    public ClientRequestHandlerImpl(Socket socket, ConnectionPool connectionPool) throws IOException {
        this.communicationSocket = new ServerCommunicationSocketImpl(socket);
        this.managementRequestHandler = new ManagementRequestHandlerImpl(connectionPool);
        this.customerRequestHandler = new CustomerRequestHandlerImpl(connectionPool);
    }

    @Override
    public void run() {
        boolean clientIsConnected = true;
        while (clientIsConnected) {
            Request jsonRequest = new Request(communicationSocket.getRequest());
            String response;
            if (!jsonRequest.shouldCloseConnection()) {
                switch (jsonRequest.getRequester()) {
                    case CUSTOMER:
                        response = customerRequestHandler.handle(jsonRequest.getJsonObject());
                        communicationSocket.sendResponse(response);
                        break;
                    case MANAGEMENT:
                        response = managementRequestHandler.handle(jsonRequest.getJsonObject());
                        communicationSocket.sendResponse(response);
                        break;
                }
            } else {
                communicationSocket.close();
                clientIsConnected = false;
            }
        }
    }
}
