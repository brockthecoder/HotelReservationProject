package client.mutual.api;

public interface ClientCommunicationSocket {

    String sendRequest(String jsonRequest);

    void close(String jsonRequest);

}

