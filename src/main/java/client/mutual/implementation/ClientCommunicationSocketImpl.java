package client.mutual.implementation;

import mutual.utilities.Properties;
import client.mutual.api.ClientCommunicationSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientCommunicationSocketImpl implements ClientCommunicationSocket {

    Socket socket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    public ClientCommunicationSocketImpl() {
        try {
            this.socket = new Socket(Properties.hostname, Properties.port);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(getSystemInformation());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to establish socket connection to server");
        }
    }

    @Override
    public String sendRequest(String jsonRequest) {

        if (jsonRequest == null) {
            throw new NullPointerException("null json request argument");
        }
        try {
            outputStream.writeObject(jsonRequest);
        }
        catch (IOException e) {
            throw new RuntimeException("Error occurred while attempting to send request over stream");
        }
        try {
            Object response = inputStream.readObject();
            if (response instanceof String) {
                return (String) response;
            }
            else {
                throw new ClassCastException("Received an invalid response from the server...unable to process request");
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Received an invalid response from the server...unable to process request");
        }
        catch (IOException e) {
            throw new RuntimeException("An error occurred while attempting to read the response");
        }
    }

    public String getSystemInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("IP address: ").append(socket.getLocalAddress()).append(System.lineSeparator());
        sb.append("Java version: ").append(System.getProperties().get("java.version")).append(System.lineSeparator());
        sb.append("OS name: ").append(System.getProperties().get("os.name")).append(System.lineSeparator());
        sb.append("OS version: ").append(System.getProperties().get("os.version")).append(System.lineSeparator());
        sb.append("OS architecture: ").append(System.getProperties().get("os.arch")).append(System.lineSeparator());
        sb.append("User country: ").append(System.getProperties().get("user.country")).append(System.lineSeparator());
        sb.append("User language: ").append(System.getProperties().get("user.language")).append(System.lineSeparator());
        return sb.toString();
    }


    @Override
    public void close(String request) {
        try {
            outputStream.writeObject(request);
            socket.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred while closing the connection");
            e.printStackTrace();
        }
    }
}
