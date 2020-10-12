package server.implementation;

import server.api.ServerCommunicationSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerCommunicationSocketImpl implements ServerCommunicationSocket {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ServerCommunicationSocketImpl(Socket socket) throws IOException {
        this.socket = socket;
        try {
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("New connection: ");
            System.out.println(inputStream.readObject().toString());
            outputStream.flush();
        }
        catch (IOException e) {
            System.out.println("An exception occurred while attempting to initialize the communication socket");
            throw e;
        }
        catch (ClassNotFoundException e) {
            System.out.println("An exception occurred while reading from the input stream");
            e.printStackTrace();
            throw new IOException("Exception in socket initialization");
        }
    }

    @Override
    public String getRequest() {

        try {
            Object request = inputStream.readObject();
            if (request instanceof String) {
                return (String) request;
            }
            else {
                throw new ClassCastException("Received an invalid request from the client...unable to process");
            }
        }
        catch (IOException e) {
            throw new RuntimeException("An error occurred while reading from the communication socket");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Received an invalid request from the client...unable to process");
        }
    }

    @Override
    public void sendResponse(String jsonResponse) {
        try {
            outputStream.writeObject(jsonResponse);
        }
        catch (IOException e) {
            throw new RuntimeException("An error occurred while attempting to send the response to the client");
        }
    }

    @Override
    public void close() {
        try {
            System.out.println("Closing connection to: " + socket.getInetAddress());
            socket.close();
        }
        catch (IOException e) {
            System.err.println("An error occurred while attempting to close the socket connection");
            e.printStackTrace();
        }
    }
}
