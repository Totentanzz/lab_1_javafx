package lab_1.fishApp.controller;

import javafx.application.Platform;
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
    private Spinner<Integer> goldenSpinner;
    @FXML
    private Spinner<Integer> guppySpinner;
    private HabitatModel habitatModel;
    private boolean startFlag;
    private Duration simulationTime;
    private Timer timer;
    private TimerTask simulationTask;

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
        imagePane.getChildren().removeIf(node -> (node instanceof ImageView));
        simulationTime = Duration.ZERO;
        startFlag = true;
        statisticsLabel.setVisible(false);
        initTimerTask();
    }

    private void stopSimulation() throws FileNotFoundException {
        simulationTask.cancel();
        timer.purge();
        if (checkBox.isSelected()) {
            refreshStatisticsLabel();
            ImageView goldView =habitatModel.createFish(1000,1000,GoldenFish.class).getView();
            ImageView guppyView =habitatModel.createFish(1000,1000,GuppyFish.class).getView();
            DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.STATISTICS,
                    statisticsLabel.getText(),goldView,guppyView);
            window.initOwner(mainStage);
            if (window.showAndWait().get()==ButtonType.OK){
                stopAndClear();
            }
            else {
                initTimerTask();
            }
        }
        else {
            refreshStatisticsLabel();
            stopAndClear();
        }
    }

    private void stopAndClear() {
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
            Fish createdFish=habitatModel.createFish(xBound, yBound, GoldenFish.class);
            imagePane.getChildren().add(createdFish.getView());
        }
        if ((time%guppySpawnTime==0)&&(generatedDigit<guppySpawnChance)){
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

    private void initTimerTask(){
        simulationTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    simulationTime = simulationTime.add(Duration.seconds(1));
                    try {
                        update((long) simulationTime.toSeconds());
                    } catch (FileNotFoundException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            }
        };
        timer.schedule(simulationTask,0,1000);
    }

    private void initSpinners(){
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
        Stream.of(goldenSpinner,guppySpinner).forEach(spinner->spinner.getEditor()
                .setTextFormatter(new TextFormatter<Integer>(change -> {
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
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        timer = new Timer();
        initSpinners();
        initRadioButtons();
        initStartButtons();
        ObservableList<String> boxChoices = IntStream.rangeClosed(0,100).filter(i->i%10==0).mapToObj(
                Integer::toString).collect(Collectors.toCollection(FXCollections::observableArrayList));
        Stream.of(goldenBox,guppyBox).peek(box->box.setItems(boxChoices)).forEach(box->
                box.getSelectionModel().select(10));
    }

}