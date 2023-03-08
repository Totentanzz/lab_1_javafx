package lab_1.fishApp.controller;

import lab_1.fishApp.model.HabitatModel;
import lab_1.fishApp.utils.Fish;
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
import lab_1.fishApp.utils.GoldenFish;
import lab_1.fishApp.utils.GuppyFish;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Random;
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
    private HabitatModel habitatModel;
    private boolean startFlag;
    private long startTime;
    private Timer timer;

    public void setHabitatModel(HabitatModel model){
        this.habitatModel=model;
    }

    @FXML
    private void handleCloseRequest(){
        timer.cancel();
    }

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

    private void startSimulation() {
        startTime=System.currentTimeMillis();
        startFlag=true;
        statisticsLabel.setVisible(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    long simulationTime = (System.currentTimeMillis()-startTime)/1000+1;
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

    private void stopSimulation() {
        timer.cancel();
        timer.purge();
        timer = new Timer();
        refreshStatisticsLabel();
        showLabel(statisticsLabel);
        habitatModel.clearFishList();
        startFlag=false;
    }

    private void showLabel (Label label) {
        boolean isLabelVisible = label.isVisible();
        label.setVisible(!isLabelVisible);
        System.out.println(label.getText());
    }

    private void update(long time) throws FileNotFoundException {
        refreshImagePaneChildren(time);
        refreshTimeLabel(time);
    }

    private void refreshImagePaneChildren(long time) throws FileNotFoundException {
        double xBound=imagePane.getWidth()-120,yBound=imagePane.getHeight()-120;
        int goldenSpawnTime = habitatModel.getGoldenSpawnTime();
        int guppySpawnTime = habitatModel.getGuppySpawnTime();
        short goldenSpawnChance = habitatModel.getGoldenSpawnChance();
        short guppySpawnChance = habitatModel.getGuppySpawnChance();
        Random randomGenerator = new Random();
        int generatedDigit = randomGenerator.nextInt(100);
        if ((time%goldenSpawnTime==0)&&(generatedDigit<goldenSpawnChance)){
//            x = (int) controller.getControlPane().getWidth() + randomGenerator.
//                    nextInt((int) controller.getImagePane().getWidth()-120);
//            y = (int) controller.getControlPane().getHeight() + randomGenerator.
//                    nextInt((int)controller.getImagePane().getHeight()-120);
            Fish createdFish=habitatModel.createFish(xBound, yBound, GoldenFish.class);
            imagePane.getChildren().add(createdFish.getView());
        }
        if ((time%guppySpawnTime==0)&&(generatedDigit<guppySpawnChance)){
//            x = (int) controller.getControlPane().getWidth() + randomGenerator.
//                    nextInt((int) controller.getImagePane().getWidth()-120);
//            y = (int) controller.getControlPane().getHeight() + randomGenerator.
//                    nextInt((int)controller.getImagePane().getHeight()-120);
            Fish createdFish=habitatModel.createFish(xBound,yBound, GuppyFish.class);
            imagePane.getChildren().add(createdFish.getView());
        }
    }

    private void refreshTimeLabel(long time){
        timeLabel.setText("Simulation time: " + time);
        System.out.println("Simulation time: " + time);
    }

    private void refreshStatisticsLabel() {
        long goldAmount = habitatModel.getFishAmount(GoldenFish.class);
        long guppyAmount = habitatModel.getFishAmount(GuppyFish.class);
        statisticsLabel.setText("Golden fish (pepe-clown): " + goldAmount + "\n" +
                                "Guppy fish (pepe-dancer): " + guppyAmount);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        timer = new Timer();
    }
}