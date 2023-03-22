package lab_1.fishApp.utils;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    private long startTime;
    private long pausedTime;
    private TimerTask timerTask;
    private Timer timer;
    private boolean isRunning;

    public CustomTimer() {
        timer = new Timer();
    }

    public void start(int delay, int period) {
        if (!isRunning) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = (currentTime - startTime) / 1000;
                    System.out.println("Elapsed time: " + elapsedTime + " seconds");
                }
            };

            startTime = System.currentTimeMillis();
            timer.scheduleAtFixedRate(timerTask, delay, period);
            isRunning = true;
        }
    }

    public void pause() {
        if (isRunning) {
            timerTask.cancel();
            pausedTime = System.currentTimeMillis();
            isRunning = false;
        }
    }

    public void stop() {
        timerTask.cancel();
        isRunning = false;
    }

    public void start() {
        if (!isRunning) {
            long delay = pausedTime - startTime;
            long period = 1000; // 1 second
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = (currentTime - startTime) / 1000;
                    System.out.println("Elapsed time: " + elapsedTime + " seconds");
                }
            };

            timer.scheduleAtFixedRate(timerTask, delay, period);
            isRunning = true;
        }
    }
}
