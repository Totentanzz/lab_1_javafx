package lab_1.fishApp.web;

import lab_1.fishApp.model.Fish;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPSingleServerThread extends Thread {

    private Socket clientSocket;
    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;
    private TCPServerThreadData serverThreadData;
    private Connection dbConnection;
    private static String dbUrl = "jdbc:postgresql://localhost:5433/ObjectDatabase";
    private static String dbUsername = "postgres";
    private static String dbPassword = "1234";


    public TCPSingleServerThread(Socket socket, ThreadGroup serverThreadGroup, String threadName) {
        super(serverThreadGroup,threadName);
        this.setDaemon(true);
        this.serverThreadData = TCPServerThreadData.getInstance();
        this.clientSocket = socket;
        try {
            this.objectOutStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String hostAddress = this.clientSocket.getInetAddress().getHostAddress();
        try {
            System.out.println("\n\nConnection received from client " + hostAddress);
            TCPServerThreadData serverThreadData = TCPServerThreadData.getInstance();
            System.out.println("Adding client " + hostAddress + " to connected clients list");
            serverThreadData.addServerThread(this);
            System.out.println("List of connections was updated\n\n");
            dbConnection = this.connectToDatabase(dbUrl,dbUsername,dbPassword);
            while (!clientSocket.isClosed()) {
                System.out.println("Server reading from client " + hostAddress);
                ClientDTO readedObject = (ClientDTO) objectInStream.readObject();
                System.out.println("\n\nReceived DTO from client: " + hostAddress);

                if (readedObject.getDtoType().equals(ClientDTO.dtoType.CLIENT_REQUEST) &&
                        readedObject.getDtoObject().equals(ClientDTO.dtoObject.CONFIG)) {
                    String targetClientName = readedObject.getClientName();
                    TCPSingleServerThread targetThread = serverThreadData.getServerThread(targetClientName);
                    ClientDTO dtoObject = ClientDTO.builder()
                            .dtoType(ClientDTO.dtoType.SERVER_REQUEST)
                            .dtoObject(ClientDTO.dtoObject.CONFIG)
                            .clientName(this.getClientName()).build();
                    targetThread.sendDtoObject(dtoObject);
                }
                else if (readedObject.getDtoType().equals(ClientDTO.dtoType.CLIENT_REPLY) &&
                        readedObject.getDtoObject().equals(ClientDTO.dtoObject.CONFIG)) {
                    String targetClientName = readedObject.getClientName();
                    TCPSingleServerThread targetThread = serverThreadData.getServerThread(targetClientName);
                    ClientDTO dtoObject = ClientDTO.builder()
                            .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                            .dtoObject(ClientDTO.dtoObject.CONFIG)
                            .clientConfig(readedObject.getClientConfig())
                            .clientName(readedObject.getClientName()).build();
                    targetThread.sendDtoObject(dtoObject);
                }
                else if (readedObject.getDtoType().equals(ClientDTO.dtoType.CLIENT_REQUEST) &&
                        readedObject.getDtoObject().equals(ClientDTO.dtoObject.OBJECTS) &&
                        readedObject.getDtoOperation().equals(ClientDTO.dtoOperation.UPLOAD)) {
                    System.out.println("Uploading objects to DataBase");
                    uploadObjectsToDatabase(readedObject.getClientObjects());
                }
                else if (readedObject.getDtoType().equals(ClientDTO.dtoType.CLIENT_REQUEST) &&
                        readedObject.getDtoObject().equals(ClientDTO.dtoObject.OBJECTS) &&
                        readedObject.getDtoOperation().equals(ClientDTO.dtoOperation.DOWNLOAD)) {
                    System.out.println("Downloading objects from database");
                    byte[] fishData =  downloadObjectsFromDatabase();
                    ClientDTO dtoObject = ClientDTO.builder()
                            .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                            .dtoObject(ClientDTO.dtoObject.OBJECTS)
                            .clientObjects(fishData).build();
                    System.out.println("Sending objects to client");
                    sendDtoObject(dtoObject);
                }

                System.out.println("Server Wrote message to client\n\n");
            }

        } catch (EOFException e) {
        } catch (IOException | ClassNotFoundException e) {
        } catch (SQLException exc) {
            System.out.println("Something went wrong during database connection");
            exc.printStackTrace();
        } finally {
            stopConnection();
            try {
                Objects.requireNonNull(dbConnection).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection connectToDatabase(String url, String username, String password) throws SQLException {
        Connection dbConnection = DriverManager.getConnection(url,username,password);
        if (dbConnection!=null) {
            System.out.println("Connection to database established");
        } else {
            System.out.println("Connection is NULL");
        }
        return dbConnection;
    }

    public void stopConnection() {
        String hostAddress = this.clientSocket.getInetAddress().getHostAddress();
        System.out.println("\n\nClosing connections & channels");
        try {
            objectOutStream.close();
            objectInStream.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Removing client " + hostAddress + " from connected clients list");

        serverThreadData.removeServerThread(this);

        System.out.println("List of connections was updated");
        System.out.println("Closing connections & channels - DONE\n\n");
    }

    public String getClientName() {
        String threadName = this.getName();
        String clientName = null;
        Pattern pattern = Pattern.compile("client-(\\d+)");
        Matcher matcher = pattern.matcher(threadName);
        if (matcher.find()) {
            clientName = matcher.group();
        }
        return clientName;
    }

    public void sendDtoObject(ClientDTO clientDTO) {
        try {
            this.objectOutStream.writeObject(clientDTO);
            this.objectOutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendConnectedClients(LinkedList<String> clientNameList) {
        LinkedList<String> serverClientList = new LinkedList<>(clientNameList);
        String threadClientName = this.getClientName();
        serverClientList.remove(threadClientName);
        ClientDTO clientDTO = ClientDTO.builder()
                .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                .dtoObject(ClientDTO.dtoObject.CLIENT_LIST)
                .clientList(serverClientList).build();
        try {
            this.objectOutStream.writeObject(clientDTO);
            this.objectOutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadObjectsToDatabase(byte[] objectList) {
        String query = "INSERT INTO objects (clientname, objectsfile) VALUES (?, ?) ON CONFLICT" +
                " (clientname) DO UPDATE SET objectsfile = excluded.objectsfile";
        System.out.println("Trying to make preparedStatement");
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            System.out.println("Object list was written to bytes");
            statement.setString(1, this.getClientName());
            statement.setBytes(2, objectList);
            System.out.println("String and bytes for statement were set");
            statement.executeUpdate();
            System.out.println("Executed SQL command");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] downloadObjectsFromDatabase() {
        String query = "SELECT objectsfile FROM objects WHERE clientname = ?";
        System.out.println("Trying to make preparedStatement");
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setString(1, this.getClientName());
            System.out.println("Statement was created. Executing...");
            ResultSet result = statement.executeQuery();
            System.out.println("Statement has been executed");
            if (result.next()) {
                byte[] byteData = result.getBytes("objectsfile");
                System.out.println("Got objects data in bytes format. Returning...");
                result.close();
                statement.close();
                return byteData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        }
        return null;
    }
}
