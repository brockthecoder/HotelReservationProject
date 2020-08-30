package server.api;

public interface ServerCommunicationSocket {

    String getRequest();

    void sendResponse(String jsonResponse);

    void close();
}
