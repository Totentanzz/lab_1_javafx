package lab_1.fishApp.web;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class TCPServer implements ClientListListener {

    private static TCPServerThreadFactory serverThreadFactory;
    private static TCPServerThreadData serverThreadData;
    private static ClientServerListener serverThreadDataListener;
    private static ServerSocket serverSocket;

    public TCPServer(Config serverConfig) {
        ThreadGroup serverThreadGroup = new ThreadGroup("serverThreadGroup");
        serverThreadFactory = new TCPServerThreadFactory(serverThreadGroup,"client");
        serverThreadData = TCPServerThreadData.getInstance();
        try {
            serverSocket = new ServerSocket(serverConfig.getInt("port"));
            serverThreadDataListener = new ClientServerListener(this,1000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader inBuffer = new BufferedReader(input);
            System.out.println("Starting server client listener...");
            serverThreadDataListener.start();
            System.out.println("Server client listener was started");
            System.out.println("Server was started\n\n");
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for new connection...");
                Socket clientSocket = serverSocket.accept();
                TCPSingleServerThread newServerThread = serverThreadFactory.newServerThread(clientSocket);
                newServerThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Stopping server...");
            this.stop();
            System.out.println("Server was stopped");
        }
    }

    public void stop() {
        try {
            serverThreadDataListener.stopListener();
            serverSocket.close();
            System.out.println("Server was stopped");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Creating a server...\nSearching config file...");
        File configFile = new File("src/main/resources/lab_1/fishApp/webConfig/web.conf");
        System.out.println("Config file was detected. Loading configurations...");
        Config serverConfig = ConfigFactory.parseFile(configFile).getConfig("server");
        TCPServer tcpServer = new TCPServer(serverConfig);
        System.out.println("Configuration was loaded successfully");
        System.out.println("Starting server on " + serverConfig.getString("ip")
                + ":" + serverConfig.getInt("port"));
        tcpServer.start();
    }

    @Override
    public void handleClientDTO() {
        System.out.println("Checking list of connections for updates");
        if (serverThreadData.isUpdated()) {
            System.out.println("\n\nList of connections gotten updates. Sending to all clients...");
            LinkedList<TCPSingleServerThread> clientThreadList = serverThreadData.getServerThreadList();
            System.out.println("List of clients threads was gotten");
            LinkedList<String> clientNames = serverThreadData.getClientNameList();
            System.out.println("List of clients names was gotten");
            System.out.println("Sending clients names...");
            clientThreadList.stream().forEach(clientThread -> clientThread.sendConnectedClients(clientNames));
            System.out.println("Client names were sent. Setting list of connections to default state...");
            serverThreadData.setToDefaultState();
            System.out.println("List of connections was set to default state\n\n");
        }
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed();
    }
}
