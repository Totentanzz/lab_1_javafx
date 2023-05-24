package lab_1.fishApp.web;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class TCPServerThreadData implements Updateable  {

    private static volatile TCPServerThreadData serverThreadData;
    private LinkedList<TCPSingleServerThread> serverThreadList;
    private LinkedList<String> clientNameList;
    private boolean updatedFlag;

    private TCPServerThreadData(){
        this.serverThreadList = new LinkedList<>();
        this.clientNameList = new LinkedList<>();
        this.updatedFlag = false;
    }

    public static TCPServerThreadData getInstance() {
        TCPServerThreadData localServerThreadData = serverThreadData;
        if (localServerThreadData == null){
            synchronized (TCPServerThreadData.class){
                localServerThreadData = serverThreadData;
                if (localServerThreadData ==null){
                    serverThreadData = localServerThreadData = new TCPServerThreadData();
                }
            }
        }
        return localServerThreadData;
    }

    public LinkedList<TCPSingleServerThread> getServerThreadList() {
        return this.serverThreadList;
    }

    public LinkedList<String> getClientNameList() {
        return this.clientNameList;
    }

    public synchronized TCPSingleServerThread getServerThread(String clientName) {
        TCPSingleServerThread targetThread = serverThreadData.getServerThreadList().stream()
                .filter(serverThread -> serverThread.getClientName().equals(clientName))
                .collect(Collectors.toList()).get(0);
        return targetThread;
    }

    public synchronized void addServerThread(TCPSingleServerThread serverThread) {
        String clientName = serverThread.getClientName();
        this.serverThreadList.add(serverThread);
        this.clientNameList.add(clientName);
        this.setUpdated();
        System.out.println(this.serverThreadList);
    }

    public synchronized void removeServerThread(TCPSingleServerThread serverThread) {
        String clientName = serverThread.getClientName();
        this.serverThreadList.remove(serverThread);
        this.clientNameList.remove(clientName);
        this.setUpdated();
        System.out.println(this.serverThreadList);
    }

    public boolean isUpdated() {
        return this.updatedFlag;
    }

    public void setToDefaultState() {
        this.updatedFlag = false;
    }

    public void setUpdated() {
        this.updatedFlag = true;
    }

}
