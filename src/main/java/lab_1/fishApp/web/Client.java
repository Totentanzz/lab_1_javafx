package lab_1.fishApp.web;

import com.typesafe.config.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

public class Client implements ClientListListener, Updateable {

    private Socket clientSocket;
    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;
    private static ClientServerListener clientListListener;
    private static LinkedList<String> clientNames;
    private static boolean updatedFlag;

    public void startConnection(Config serverConfig) {
        System.out.println("Connection is starting...");
        try {
            updatedFlag = false;
            clientSocket = new Socket(serverConfig.getString("ip"),serverConfig.getInt("port"));
            System.out.println("ClientSocket configured");
            objectOutStream = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Output stream configured");
            //objectOutStream.flush();
            objectInStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Input stream configured");
            System.out.println("Streams configured");
            clientListListener = new ClientServerListener(this,1000);
            System.out.println("ClientServerListener configured");
            clientNames = new LinkedList<>();
            System.out.println("ClientList configured");
            System.out.println("Starting client list listener...");
            clientListListener.start();
            System.out.println("Client list listener was started");
            System.out.println("Client has been connected to server successfully\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
        //System.out.println("Connection started");
    }

    public void stopConnection() {
        try {
            if (this.isConnected()) {
                System.out.println("Closing connection and streams");
                clientListListener.stopListener();
                objectOutStream.close();
                objectInStream.close();
                clientSocket.close();
                System.out.println("Connection was closed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        boolean state = false;
        if (clientSocket!=null && !clientSocket.isClosed())
            state = true;
        return state;
    }

    public void sendClientDTO(ClientDTO clientDTO) {
        System.out.println("Trying to send DTO data to server...");
        try {
            objectOutStream.writeObject(clientDTO);
            objectOutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DTO data was sent successfully");
    }

    public ClientDTO getServerReply() {
        System.out.println("Getting server reply...");
        try {
            ClientDTO readedObject = (ClientDTO) objectInStream.readObject();
            System.out.println("Server reply was gotten. Checking for validation...");
            if (readedObject==null) {
                System.out.println("Server reply is invalid. Constructing default error DTO");
                readedObject = ClientDTO.builder()
                        .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                        .serverMessage(ClientDTO.dtoReply.ERROR).build();
            } else
                System.out.println("Server reply is valid");
            System.out.println("Returning server reply");
            return readedObject;
        } catch (SocketException e) {
            stopConnection();
            return ClientDTO.builder()
                    .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                    .serverMessage(ClientDTO.dtoReply.ERROR).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public LinkedList<String> getClientNames() {
        return clientNames;
    }

    public void requestConfig(String clientName) {
        System.out.println("Buidling a DTO request from client to get config of" + clientName);
        ClientDTO clientRequest = ClientDTO.builder()
                .dtoType(ClientDTO.dtoType.CLIENT_REQUEST)
                .dtoObject(ClientDTO.dtoObject.CONFIG)
                .clientName(clientName).build();
        System.out.println("DTO request was build. Trying to send...");
        sendClientDTO(clientRequest);
    }

    @Override
    public void handleClientListChanges() {
        ClientDTO serverReply = this.getServerReply();
        String hostAddress = clientSocket.getInetAddress().getHostAddress();
        System.out.println("\n\nClient " + hostAddress + " got new client list from server");
        System.out.println("Checking for validation...");
        if (serverReply.getServerMessage().equals(ClientDTO.dtoReply.SUCCESS)) {
            System.out.println("DTO is valid. Updating...");
            clientNames = serverReply.getClientList();
            setUpdated();
            System.out.println(clientNames);
            System.out.println("Client list was updated successfully\n\n");
        }
        else {
            System.out.println("Invalid DTO object\n\n");
        }
    }

    @Override
    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    @Override
    public boolean isUpdated() {
        return updatedFlag;
    }

    @Override
    public void setUpdated() {
        updatedFlag = true;
    }

    @Override
    public void setToDefaultState() {
        updatedFlag = false;
    }
}
