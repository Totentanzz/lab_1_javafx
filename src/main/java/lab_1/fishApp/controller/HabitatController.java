package lab_1.fishApp.controller;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import lab_1.fishApp.DialogWindow;
import lab_1.fishApp.model.HabitatModel;
import lab_1.fishApp.model.Fish;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lab_1.fishApp.model.GoldenFish;
import lab_1.fishApp.model.GuppyFish;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private ComboBox<String> goldenBox;
    @FXML
    private ComboBox<String> guppyBox;
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
    @FXML
    private Spinner<Integer> goldenSpinner;
    @FXML
    private Spinner<Integer> guppySpinner;
    private HabitatModel habitatModel;
    private boolean startFlag;
    private Duration simulationTime;
    //private Timer timer;
    private Timeline timer;
    private KeyFrame timerEvent;
    @FXML
    private Button baton;

    public void setHabitatModel(HabitatModel model){
        this.habitatModel=model;
    }

    @FXML
    private void handleCloseRequest(){
        timer.stop();
    }

    @FXML
    private void setActionOnKey(KeyEvent keyEvent){
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey==KeyCode.B && timer.getStatus() == Animation.Status.STOPPED) {
            System.out.println("Pressed B");
            imagePane.getChildren().removeIf(node -> (node instanceof ImageView));
            startSimulation();
        }
        else if (pressedKey==KeyCode.E && timer.getStatus() == Animation.Status.RUNNING){
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Information window");
            infoAlert.setContentText(statisticsLabel.getText());
            stopSimulation();
            System.out.println("Simulation has stopped");
        }
        else if (pressedKey==KeyCode.T){
            showLabel(timeLabel);
        }
    }

    @FXML
    private void setGoldenChance(){
        System.out.println(habitatModel.getGoldenSpawnChance());
        habitatModel.setGoldenSpawnChance(Short.valueOf(goldenBox.getValue()));
        System.out.println(habitatModel.getGoldenSpawnChance());
    }

    @FXML
    private void setGuppyChance(){
        System.out.println(habitatModel.getGuppySpawnChance());
        habitatModel.setGuppySpawnChance(Short.valueOf(guppyBox.getValue()));
        System.out.println(habitatModel.getGuppySpawnChance());
    }

    private void startSimulation() {
        simulationTime = Duration.ZERO;
        startFlag = true;
        statisticsLabel.setVisible(false);
        timer.playFromStart();
    }

    private void stopSimulation() {
        timer.pause();
        DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.STATISTICS);
        if (window.showAndWait().get()==ButtonType.CANCEL){
        }
        timer.stop();
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

    private void initTimer(){
        timerEvent = new KeyFrame(Duration.seconds(1), timerEvent->{
            simulationTime = simulationTime.add(Duration.seconds(1));
            try {
                update((long) simulationTime.toSeconds());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        timer = new Timeline(timerEvent);
        timer.setCycleCount(Animation.INDEFINITE);
    }

    private void initSpinnersListeners(){
        goldenSpinner.valueProperty().addListener((observableValue, oldValue, newValue) ->{
            System.out.println(habitatModel.getGoldenSpawnTime());
            habitatModel.setGoldenSpawnTime(newValue);
            System.out.println(habitatModel.getGoldenSpawnTime());
        });
        guppySpinner.valueProperty().addListener((observableValue, oldValue, newValue) ->{
            System.out.println(habitatModel.getGuppySpawnTime());
            habitatModel.setGuppySpawnTime(newValue);
            System.out.println(habitatModel.getGuppySpawnTime());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        initTimer();
        initSpinnersListeners();
        ObservableList<String> boxChoices = IntStream.rangeClosed(0,100).filter(i->i%10==0).mapToObj(
                Integer::toString).collect(Collectors.toCollection(FXCollections::observableArrayList));
        Stream<ComboBox> stream = Stream.of(goldenBox,guppyBox);
        stream.peek(box->box.setItems(boxChoices)).forEach(box->box.getSelectionModel().select(10));
        //Stream.of(goldenSpinner,guppySpinner).forEach();
    }

}