package lab_1.fishApp.web;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lab_1.fishApp.model.Fish;
import lab_1.fishApp.model.ModelData;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;

public class Client implements ClientListListener, Updateable {

    private Socket clientSocket;
    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;
    private static ClientServerListener serverListener;
    private LinkedList<String> clientNames;
    private LinkedList<Fish> clientFishList;
    private Config clientConfig;
    private boolean updatedFlag;
    private updateType updateTypes;

    public void startConnection(Config serverConfig) {
        System.out.println("Connection is starting...");
        try {
            updatedFlag = false;
            clientSocket = new Socket(serverConfig.getString("ip"),serverConfig.getInt("port"));
            System.out.println("ClientSocket configured");
            objectOutStream = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Output stream configured");
            objectInStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Input stream configured");
            System.out.println("Streams configured");
            serverListener = new ClientServerListener(this,1000);
            System.out.println("ClientServerListener configured");
            clientNames = ModelData.getInstance().getClientsNames();
            System.out.println("Client list configured");
            clientConfig = ConfigFactory.parseFile(
                    new File("src/main/resources/lab_1/fishApp/clientConfig/default.conf")
            );
            System.out.println("Client config loaded");
            System.out.println("Starting client list listener...");
            serverListener.start();
            System.out.println("Client list listener was started");
            System.out.println("Client has been connected to server successfully\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopConnection() {
        try {
            if (this.isConnected()) {
                System.out.println("Closing connection and streams");
                serverListener.stopListener();
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

    public ClientDTO getServerReply() {
        System.out.println("Getting server reply...");
        try {
            ClientDTO readedObject = (ClientDTO) objectInStream.readObject();
            System.out.println("Server reply was gotten. Checking for validation...");
            if (readedObject==null) {
                System.out.println("Server reply is invalid. Constructing default error DTO");
                readedObject = ClientDTO.builder()
                        .dtoType(ClientDTO.dtoType.SERVER_ERROR)
                        .serverMessage(ClientDTO.dtoReply.ERROR).build();
            } else {
                System.out.println("Server reply is valid");
            }
            System.out.println("Returning server reply");
            return readedObject;
        } catch (SocketException e) {
            stopConnection();
            return ClientDTO.builder()
                    .dtoType(ClientDTO.dtoType.SERVER_ERROR)
                    .serverMessage(ClientDTO.dtoReply.ERROR).build();
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public LinkedList<String> getClientNames() {
        return clientNames;
    }

    public Config getClientConfig() {
        return clientConfig;
    }

    public LinkedList<Fish> getClientFishList() {return clientFishList;}

    public updateType getUpdateTypes() {
        return updateTypes;
    }

    public void sendClientDTO(ClientDTO clientDTO) {
        System.out.println("Trying to send DTO data to server...");
        try {
            objectOutStream.writeObject(clientDTO);
            objectOutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        System.out.println("DTO data was sent successfully\n\n");
    }

    public void requestConfig(String clientName) {
        System.out.println("Building a DTO request from client to get config of " + clientName);
        ClientDTO clientRequest = ClientDTO.builder()
                .dtoType(ClientDTO.dtoType.CLIENT_REQUEST)
                .dtoObject(ClientDTO.dtoObject.CONFIG)
                .clientName(clientName).build();
        System.out.println("DTO request was build. Trying to send...");
        sendClientDTO(clientRequest);
    }

    public void uploadObjects(LinkedList<Fish> fishList) {
        System.out.println("Building a DTO request from client to upload objects to database");
        ByteArrayOutputStream byteStr = new ByteArrayOutputStream();
        ObjectOutputStream objStr = null;
        try {
            objStr = new ObjectOutputStream(byteStr);
            objStr.writeObject(fishList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClientDTO clientRequest = ClientDTO.builder()
                .dtoType(ClientDTO.dtoType.CLIENT_REQUEST)
                .dtoObject(ClientDTO.dtoObject.OBJECTS)
                .dtoOperation(ClientDTO.dtoOperation.UPLOAD)
                .clientObjects(byteStr.toByteArray()).build();
        System.out.println("DTO request was build. Trying to send...");
        sendClientDTO(clientRequest);
    }

    public void downloadObjects() {
        System.out.println("Building a DTO request from client to download objects from database");
        ClientDTO clientRequest = ClientDTO.builder()
                .dtoType(ClientDTO.dtoType.CLIENT_REQUEST)
                .dtoObject(ClientDTO.dtoObject.OBJECTS)
                .dtoOperation(ClientDTO.dtoOperation.DOWNLOAD).build();
        System.out.println("DTO request was build. Trying to send...");
        sendClientDTO(clientRequest);
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
        updateTypes = null;
    }

    @Override
    public void handleClientDTO() {
        ClientDTO serverReply = getServerReply();
        String hostAddress = clientSocket.getInetAddress().getHostAddress();
        System.out.println("\n\nClient " + hostAddress + " got new DTO");
        System.out.println("Checking for type...");
        System.out.println(serverReply);
        if (serverReply.getDtoType().equals(ClientDTO.dtoType.SERVER_REPLY)
                && serverReply.getDtoObject().equals(ClientDTO.dtoObject.CLIENT_LIST)) {
            System.out.println("DTO is CLIENT_LIST reply. Updating client...");
            clientNames = serverReply.getClientList();
            this.updateTypes = Client.updateType.CLIENT_LIST;
            setUpdated();
            System.out.println(clientNames);
            System.out.println("Client list was updated successfully\n\n");
        }
        else if (serverReply.getDtoType().equals(ClientDTO.dtoType.SERVER_REPLY)
                && serverReply.getDtoObject().equals(ClientDTO.dtoObject.CONFIG)) {
            System.out.println("DTO is CONFIG reply. Updating client...");
            clientConfig = serverReply.getClientConfig();
            this.updateTypes = updateType.CONFIG;
            setUpdated();
            System.out.println(clientConfig);
            System.out.println("Client config was updated successfully\n\n");
        }
        else if (serverReply.getDtoType().equals(ClientDTO.dtoType.SERVER_REQUEST)
                && serverReply.getDtoObject().equals(ClientDTO.dtoObject.CONFIG)) {
            System.out.println("DTO is CONFIG request. Creating a client reply...");
            ModelData modelData = ModelData.getInstance();
            System.out.println("CONFIG IN MODEL DATA: " + modelData.getConfig());
            ClientDTO clientReply = ClientDTO.builder()
                    .dtoType(ClientDTO.dtoType.CLIENT_REPLY)
                    .dtoObject(ClientDTO.dtoObject.CONFIG)
                    .clientName(serverReply.getClientName())
                    .clientConfig(modelData.getConfig()).build();
            sendClientDTO(clientReply);
            System.out.println(clientReply);
            System.out.println("Client config was sent successfully\n\n");
        } else if (serverReply.getDtoType().equals(ClientDTO.dtoType.SERVER_REPLY) &&
                serverReply.getDtoObject().equals(ClientDTO.dtoObject.OBJECTS)) {
            System.out.println("DTO is OBJECTS request. Updating client...");
            byte[] objectsData = serverReply.getClientObjects();
            ObjectInputStream objectStr = null;
            try {
                objectStr = new ObjectInputStream(new ByteArrayInputStream(objectsData));
                clientFishList = (LinkedList<Fish>) objectStr.readObject();
                System.out.println("DOWNLOADED FISH LIST = " + clientFishList);
                this.updateTypes = updateType.OBJECTS;
                setUpdated();
                System.out.println("Client config was updated successfully\n\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    public enum updateType {
        CLIENT_LIST,
        OBJECTS,
        CONFIG;
    }

}
