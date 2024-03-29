package lab_1.fishApp.utils;

import lab_1.fishApp.model.Fish;
import lab_1.fishApp.model.ModelData;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseAI {
    private final Object locker;
    private boolean stopFlag;
    private Thread moveThread;
    private LinkedList<Fish> objectList;
    protected Predicate filter;
    protected Consumer mover;
    protected int xBound, yBound, movesPerSecond;

    public BaseAI(int movesPerSecond, int xBound, int yBound) {
        this.objectList = ModelData.getInstance().getFishList();
        this.stopFlag = false;
        this.xBound = xBound;
        this.yBound = yBound;
        this.movesPerSecond = movesPerSecond;
        this.locker = new Object();
        this.initFilter();
        this.initMover();
        this.initMoveThread();
        moveThread.setDaemon(true);
    }

    public BaseAI(int xBound,int yBound) {
        this(60,xBound,yBound);
    }

    private void initMoveThread(){
        moveThread = new Thread(()->{
            double frequency = 1000/movesPerSecond;
            double frequencyDelay = (frequency - (int)frequency)*1000000;
            while (true) {
                synchronized (locker) {
                    while (stopFlag) {
                        try {
                            locker.wait();
                        } catch (InterruptedException e){
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                synchronized (ModelData.getInstance().getFishList()) {
                    if (!objectList.isEmpty()) {
                       objectList.stream().filter(filter).forEach(mover);
                    }
                }
                try {
                    Thread.sleep((int)frequency,(int)frequencyDelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public abstract void initFilter();

    public abstract void initMover();

    public void startAI() {
        if (moveThread.getState()==Thread.State.NEW){
            this.moveThread.start();
        }
        else this.resumeAI();
    }

    public void pauseAI() {
        stopFlag = true;
    }

    public void resumeAI() {
        synchronized (locker) {
            stopFlag = false;
            locker.notifyAll();
        }
    }

    public Thread.State getState() {
        return this.moveThread.getState();
    }

}
