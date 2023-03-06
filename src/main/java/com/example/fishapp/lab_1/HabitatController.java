package com.example.fishapp.lab_1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HabitatController implements Initializable {
    @FXML
    private Label timeLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label statisticsLabel;
    @FXML
    private Stage mainStage;
    @FXML
    private Scene mainScene;
    @FXML
    private Pane imagePane;
    @FXML
    private Pane controlPane;
    @FXML
    private SplitPane rootPane;
    private boolean startFlag;
    private long startTime;
    private Timer timer;
    private ArrayList<Fish> fishArray;
    private static HabitatController contr;

    public Label getTimeLabel(){
        return timeLabel;
    }

    public Label getStatisticsLabel() {
        return statisticsLabel;
    }

    public Button getStartButton() {return startButton;}
    public Button getStopButton() {return stopButton;}
    public Pane getControlPane() {return controlPane;}
    public SplitPane getRootPane() {
        return rootPane;
    }

    public Pane getImagePane() {return imagePane;}
    public Timer getTimer() {return timer;}

    @FXML
    private void setActionOnKey(KeyEvent keyEvent){
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey==KeyCode.B && !startFlag) {
            System.out.println("Pressed B");
            imagePane.getChildren().removeIf(node -> (node instanceof ImageView));
            startSimulation();
        }
        else if (pressedKey==KeyCode.E && startFlag){
            stopSimulation();
            System.out.println("Simulation has stopped");
        }
        else if (pressedKey==KeyCode.T){
            showLabel(timeLabel);
        }
    }

    private void startSimulation() { //контр
        startTime=System.currentTimeMillis();
        startFlag=true;
        statisticsLabel.setVisible(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    long simulationTime = (System.currentTimeMillis()-startTime)/1000;
                    try {
                        System.out.println("Trying to update");
                        update(simulationTime);
                    } catch (FileNotFoundException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            };
        },0,1000);
    }

    private void stopSimulation() { //контр
        timer.cancel();
        timer.purge();
        timer = new Timer();
        refreshStatisticsLabel();
        showLabel(statisticsLabel);
        fishArray.clear();
        startFlag=false;
    }

    private void showLabel (Label label) { //контр
        boolean isLabelVisible = label.isVisible();
        label.setVisible(!isLabelVisible);
        System.out.println(label.getText());
    }

    private void update(long time) throws FileNotFoundException {
        Habitat.spawnFish(time);
        refreshTimeLabel(time);
    }

    private void refreshTimeLabel(long time){ //контр
        timeLabel.setText("Simulation time: " + time);
        System.out.println("Simulation time: " + time);
    }

    private void refreshStatisticsLabel() { //контр
        long goldAmount = fishArray.stream().filter(
                obj -> obj instanceof GoldenFish).count();
        long guppyAmount = fishArray.size()-goldAmount;
        statisticsLabel.setText("Golden fish: " +
                goldAmount + "\n" + "Guppy fish: " + guppyAmount);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        fishArray = FishArray.getInstance();
        timer = new Timer();
        long simTime = System.currentTimeMillis()/1000;
//        try {
//            instance.spawnFish(simTime);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            Habitat.spawnFish(simTime);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }
}