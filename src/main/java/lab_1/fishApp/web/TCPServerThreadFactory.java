package lab_1.fishApp.web;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServerThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup serverThreadGroup;
    private final String threadsName;

    public TCPServerThreadFactory(ThreadGroup threadGroupName, String threadsName) {
        this.serverThreadGroup = threadGroupName;
        this.threadsName = new StringBuilder().append("serverThreadPool-")
                .append(poolNumber.getAndIncrement()).append("-").
                append(threadsName).append("-").toString();
    }


    public TCPSingleServerThread newServerThread(Socket clientSocket) {
        TCPSingleServerThread clientServerThread= new TCPSingleServerThread(clientSocket,
                this.serverThreadGroup,this.threadsName + threadNumber.getAndIncrement());
        return clientServerThread;
    }
}

