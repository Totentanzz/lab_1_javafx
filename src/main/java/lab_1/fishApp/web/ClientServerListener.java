package lab_1.fishApp.web;

public class ClientServerListener extends Thread {

    private ClientListListener listListener;
    private int frequency;
    private boolean stopFlag;

    public ClientServerListener(ClientListListener listListener, int period) {
        this.listListener = listListener;
        this.frequency = period;
    }


    @Override
    public void run() {
        while (!this.listListener.isClosed() && !stopFlag) {
            this.listListener.handleClientListChanges();
            try {
                Thread.sleep(this.frequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Listener has been stopped");
    }

    public void stopListener() {
        this.stopFlag = true;
    }

}
