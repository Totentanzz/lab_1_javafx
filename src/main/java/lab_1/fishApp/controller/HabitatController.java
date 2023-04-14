package lab_1.fishApp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import lab_1.fishApp.DialogWindow;
import lab_1.fishApp.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lab_1.fishApp.utils.BaseAI;
import lab_1.fishApp.utils.UniqueNumberGenerator;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private Button objectsButton;
    @FXML
    private Label statisticsLabel;
    @FXML
    private CheckBox checkBox;
    @FXML
    private ToggleGroup timeToggleGroup;
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
    private Spinner<Integer> goldenSpawnSpinner;
    @FXML
    private Spinner<Integer> goldenLifeSpinner;
    @FXML
    private Spinner<Integer> guppySpawnSpinner;
    @FXML
    private Spinner<Integer> guppyLifeSpinner;
    @FXML
    private Button but;
    private HabitatModel habitatModel;
    private boolean startFlag;
    private Duration simulationTime;
    private Timer timer;
    private TimerTask simulationTask;
    private UniqueNumberGenerator uniqueNumberGenerator;
    private GoldenFishAI goldenFishAI;
    private GuppyFishAI guppyFishAI;

    public void setHabitatModel(HabitatModel model){
        this.habitatModel=model;
    }

    @FXML
    private void handleCloseRequest(){
        timer.cancel();
    }

    @FXML
    private void setActionOnKey(KeyEvent keyEvent) throws FileNotFoundException {
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey==KeyCode.B && !startFlag) {
            System.out.println("Started simulating");
            startButton.fire();
        }
        else if (pressedKey==KeyCode.E && startFlag){
            stopButton.fire();
            System.out.println("Simulation has stopped");
        }
        else if (pressedKey==KeyCode.T){
            timeToggleGroup.selectToggle(timeLabel.isVisible() ? timeToggleGroup
                    .getToggles().get(1) : timeToggleGroup.getToggles().get(0));
        }
    }

    @FXML
    private void setGoldenChance(){
        habitatModel.setGoldenSpawnChance(Short.valueOf(goldenBox.getValue()));
    }

    @FXML
    private void setGuppyChance(){
        habitatModel.setGuppySpawnChance(Short.valueOf(guppyBox.getValue()));
    }

    private void startSimulation() {
        Platform.runLater(()->imagePane.getChildren().removeIf(node -> (node instanceof ImageView)));
        simulationTime = Duration.ZERO;
        startFlag = true;
        statisticsLabel.setVisible(false);
        timer.schedule(simulationTask,0,1000);
        Stream.of(goldenFishAI,guppyFishAI).forEach(BaseAI::startAI);
    }

    private void stopSimulation() throws FileNotFoundException, InterruptedException {
        Stream.of(goldenFishAI,guppyFishAI).forEach(BaseAI::pauseAI);
        if (checkBox.isSelected()) {
            startFlag=false;
            refreshStatisticsLabel();
            ImageView goldView = new GoldenFish().getImageView();
            ImageView guppyView = new GuppyFish().getImageView();
            System.out.println(statisticsLabel.getText());
            DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.STATISTICS,
                    statisticsLabel.getText(),goldView,guppyView);
            window.initOwner(mainStage);
            if (window.showAndWait().get()==ButtonType.OK){
                stopAndClear();
            }
            else {
                startFlag=true;
                Stream.of(goldenFishAI,guppyFishAI).forEach(BaseAI::resumeAI);
            }
        }
        else {
            refreshStatisticsLabel();
            stopAndClear();
        }
    }

    private void stopAndClear() {
        showLabel(statisticsLabel);
        synchronized (habitatModel.getFishData()) {
            habitatModel.getFishData().clearData();
        }
        startFlag=false;
        simulationTask.cancel();
        initTimerTask();
    }

    private void showLabel (Label label) {
        boolean isLabelVisible = label.isVisible();
        label.setVisible(!isLabelVisible);
        System.out.println(label.getText());
    }

    private void update(long time) throws FileNotFoundException {
        refreshImagePaneChildren(time);
        refreshTimeLabel(time);
        removeDeadFish(time);
    }

    private void refreshImagePaneChildren(long time) throws FileNotFoundException {
        double xBound=imagePane.getWidth()-120,yBound=imagePane.getHeight()-120;
        int goldenSpawnTime = habitatModel.getGoldenSpawnTime();
        int guppySpawnTime = habitatModel.getGuppySpawnTime();
        short goldenSpawnChance = habitatModel.getGoldenSpawnChance();
        short guppySpawnChance = habitatModel.getGuppySpawnChance();
        Random randomGenerator = new Random();
        int generatedDigit = randomGenerator.nextInt(100);
        synchronized (habitatModel.getFishData().fishList){
            if ((time%goldenSpawnTime==0)&&(generatedDigit<goldenSpawnChance)){
                Fish createdFish=habitatModel.createFish(xBound, yBound, uniqueNumberGenerator.getNext(),
                        (int) this.simulationTime.toSeconds(), GoldenFish.class);
                Platform.runLater(()->imagePane.getChildren().add(createdFish.getImageView()));
            }
            if ((time%guppySpawnTime==0)&&(generatedDigit<guppySpawnChance)){
                Fish createdFish=habitatModel.createFish(xBound,yBound,uniqueNumberGenerator.getNext(),
                        (int) this.simulationTime.toSeconds(), GuppyFish.class);
                Platform.runLater(()->imagePane.getChildren().add(createdFish.getImageView()));
            }
        }
    }

    private void refreshTimeLabel(long time){
        Platform.runLater(()->timeLabel.setText("Simulation time: " + time));
    }

    private void removeDeadFish(long time) {
        synchronized (habitatModel.getFishData().fishList){
            FishData fishData = habitatModel.getFishData();
            ObservableList<Node> imageViews = imagePane.getChildren();
            LinkedList<Fish> deadFish = habitatModel.getFishData().fishList.stream().filter(obj -> {
                int lifeTime = (obj instanceof GoldenFish) ?
                        habitatModel.getGoldenLifeTime() : habitatModel.getGuppyLifeTime();
                return (obj.getBirthTime() + lifeTime <= time);
            }).collect(Collectors.toCollection(LinkedList::new));
            deadFish.forEach(obj -> {
                int objId = obj.getId();
                fishData.idSet.remove(objId);
                fishData.birthTimeTree.remove(objId);
                // synchronized (habitatModel.getFishData()) {
                fishData.fishList.remove(obj);
                //}
                Platform.runLater(()->imageViews.remove(obj.getImageView()));
            });
        }
    }

    private void refreshStatisticsLabel() {
        long goldAmount = habitatModel.getFishAmount(GoldenFish.class);
        long guppyAmount = habitatModel.getFishAmount(GuppyFish.class);
        statisticsLabel.setText("Golden fish (pepe-clown): " + goldAmount + "\n" + "Guppy fish (pepe-dancer): " + guppyAmount);
    }

    private void initTimerTask(){
        simulationTask = new TimerTask() {
            @Override
            public void run() {
                if (startFlag) {
                    simulationTime = simulationTime.add(Duration.seconds(1));
                    try {
                        update((long)simulationTime.toSeconds());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    private void initSpinners(){
        goldenSpawnSpinner.valueProperty().addListener((observableValue, oldValue, newValue) ->{
            habitatModel.setGoldenSpawnTime(newValue);
        });
        goldenLifeSpinner.valueProperty().addListener((observableValue,oldValue,newValue)->{
            habitatModel.setGoldenLifeTime(newValue);
        });
        guppySpawnSpinner.valueProperty().addListener((observableValue, oldValue, newValue) ->{
            habitatModel.setGuppySpawnTime(newValue);
        });
        guppyLifeSpinner.valueProperty().addListener((observableValue,oldValue,newValue)->{
            System.out.println(habitatModel.getGuppyLifeTime());
            habitatModel.setGuppyLifeTime(newValue);
            System.out.println(habitatModel.getGuppyLifeTime());
        });
        Stream.of(goldenSpawnSpinner,goldenLifeSpinner,guppySpawnSpinner,guppyLifeSpinner)
                .forEach(spinner->spinner.getEditor().setTextFormatter(new TextFormatter<Integer>(
                        change -> {
                    String input = change.getControlNewText();
                    if (input.matches("[0-9]*")){
                        try{
                            int value = Integer.parseInt(input);
                            if (value>0 && value<=1000){
                                return change;
                            }
                        } catch (NumberFormatException exception){}
                    }
                    return null;
                })));
    }

    private void initRadioButtons() {
        ObservableList<Toggle> toggles = timeToggleGroup.getToggles();
        showLabel(timeLabel);
        toggles.get(0).selectedProperty().addListener(observable -> {
            timeLabel.setVisible(true);
        });
        toggles.get(1).selectedProperty().addListener(observable -> {
            timeLabel.setVisible(false);
        });
    }

    private void initStartButtons() {
        startButton.setOnAction(actionEvent -> {
            startSimulation();
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });
        stopButton.setOnAction(actionEvent -> {
            try {
                stopSimulation();
                if (!startFlag){
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        objectsButton.setOnAction(actionEvent -> {
          //  if (startFlag) {
                startFlag=false;
                Stream.of(goldenFishAI,guppyFishAI).forEach(BaseAI::pauseAI);
          //  }
            DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.OBJECTS);
            window.initOwner(mainStage);
            window.showAndWait();
           // if (!startFlag){
                startFlag=true;
                Stream.of(goldenFishAI,guppyFishAI).forEach(BaseAI::resumeAI);
          // }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        uniqueNumberGenerator = new UniqueNumberGenerator(0,1000000);
        timer = new Timer();
        initTimerTask();
        initSpinners();
        initRadioButtons();
        initStartButtons();
        ObservableList<String> boxChoices = IntStream.rangeClosed(0,100).filter(i->i%10==0).mapToObj(
                Integer::toString).collect(Collectors.toCollection(FXCollections::observableArrayList));
        Stream.of(goldenBox,guppyBox).peek(box->box.setItems(boxChoices)).forEach(box->
                box.getSelectionModel().select(10));
        goldenFishAI = new GoldenFishAI((int)imagePane.getPrefWidth(),(int)imagePane.getPrefHeight());
        guppyFishAI = new GuppyFishAI((int)imagePane.getPrefWidth(),(int)imagePane.getPrefHeight());
    }

}