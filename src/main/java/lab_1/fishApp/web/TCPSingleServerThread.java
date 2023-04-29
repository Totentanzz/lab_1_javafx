package lab_1.fishApp.web;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPSingleServerThread extends Thread {

    private Socket clientSocket;
    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;
    private TCPServerThreadData serverThreadData;


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
            while (!clientSocket.isClosed()) {
                System.out.println("Server reading from client " + hostAddress);
                ClientDTO readedObject = (ClientDTO) objectInStream.readObject();
                System.out.println("Received DTO from client: " + hostAddress);

                if (readedObject.getDtoType().equals(ClientDTO.dtoType.CLIENT_REQUEST) &&
                         readedObject.getDtoObject().equals(ClientDTO.dtoObject.CONFIG)) {
                    String targetClientName = readedObject.getClientName();
                    TCPSingleServerThread targetThread = serverThreadData.getServerThread(targetClientName);
                    ClientDTO dtoObject = ClientDTO.builder()
                            .dtoType(ClientDTO.dtoType.SERVER_REQUEST)
                            .dtoObject(ClientDTO.dtoObject.CONFIG)
                            .clientName(targetClientName).build();
                    targetThread.sendDtoObject(dtoObject);
                }
                else if (readedObject.getDtoType().equals(ClientDTO.dtoType.SERVER_REQUEST) && readedObject.getDtoObject().equals(ClientDTO.dtoObject.CONFIG))

                System.out.println("Server Wrote message to client\n\n");
            }

        } catch (EOFException e) {
        } catch (IOException | ClassNotFoundException e) {
        } finally {
            stopConnection();
        }
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
                .serverMessage(ClientDTO.dtoReply.SUCCESS)
                .dtoObject(ClientDTO.dtoObject.CLIENT_LIST)
                .clientList(serverClientList).build();
        try {
            this.objectOutStream.writeObject(clientDTO);
            this.objectOutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
