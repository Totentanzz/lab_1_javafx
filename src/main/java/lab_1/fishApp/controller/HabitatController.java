package lab_1.fishApp.controller;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lab_1.fishApp.model.HabitatModel;
import lab_1.fishApp.model.Fish;
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
import lab_1.fishApp.model.GoldenFish;
import lab_1.fishApp.model.GuppyFish;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    @FXML
    private Button baton;

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

    private String interpolateColor(String startColor, String endColor, double value){
        Color start = Color.web(startColor);
        Color end = start.interpolate(Color.web(endColor),value);
        String webColor = String.format("#%02X%02X%02X",
                (int)(end.getRed()*255),
                (int)(end.getGreen()*255),
                (int)(end.getBlue()*255));
        return webColor;
    }

    private ArrayList<String> getInterpolateColorArray(String startColor, String endColor, int steps){
        double stepFactor = 1/((double)steps-1);
        ArrayList<String> colorArray = new ArrayList<>(steps);
        for (int i=0;i<steps;++i){
            colorArray.add(interpolateColor(startColor, endColor,stepFactor*i));
        }
        return colorArray;
    }

    private ArrayList<KeyValue> colorsToKeyValues(StringProperty styleProperty, String propertyRegex,  ArrayList<String> colorArray){
        int colorAmount = colorArray.size();
        ArrayList<KeyValue> keyValues = new ArrayList<>(colorAmount);
        String propertyName = propertyRegex.split("#")[0];
        for (int i=0;i<colorAmount;++i){
           // KeyValue newKeyValue = new KeyValue(styleProperty,styleProperty.getValue().replaceAll("-fx-background-color: #.{6}","-fx-background-co"))
            KeyValue newKeyValue = new KeyValue(styleProperty,styleProperty.getValue().replaceFirst(propertyRegex,propertyName+colorArray.get(i)));
            keyValues.add(newKeyValue);
        }
        return keyValues;
    }

    private Timeline colorFillTransition(StringProperty styleProperty, String propertyRegex,
                                         String startColor, String endColor, int colorAmount){
        ArrayList<KeyValue> keyValues = colorsToKeyValues(styleProperty,propertyRegex,
                                        getInterpolateColorArray(startColor,endColor,colorAmount));
        Timeline colorFill = new Timeline();
        int keyAmounts = keyValues.size();
        double stepFactor = 1/((double)keyAmounts-1);
        for (int i=0;i<keyAmounts;++i){
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(stepFactor*i),keyValues.get(i));
            colorFill.getKeyFrames().add(keyFrame);
        }
        return colorFill;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        timer = new Timer();
        String style = startButton.getStyle().replace(";","\n");
        String stra = style.lines().filter(str->str.contains("-fx-background-color")).collect(Collectors.joining());
        String strr = stra.replaceAll("[^0-9]+","");
        String newa = startButton.getStyle().replaceAll(stra,"-fx-background-color: #aaaaaa");
        StringProperty styleProp = startButton.styleProperty();
        Timeline line = new Timeline();
        //KeyValue first = new KeyValue(styleProp,startButton.getStyle().replaceAll(stra,"-fx-background-color: #aaaaaa"));


        Timeline bodyFill = colorFillTransition(styleProp,"-fx-background-color: #.{6}","#333333","#8fc866",30);
        Timeline textFill = colorFillTransition(styleProp,"-fx-text-fill: #.{6}","#8fc866","white",30);
        textFill.setRate(4);
        bodyFill.setRate(4);
        //ParallelTransition parallel = new ParallelTransition(textFill,bodyFill);
        ParallelTransition paral = new ParallelTransition();
        paral.getChildren().addAll(textFill,bodyFill);
        startButton.setOnAction(actionEvent -> {
            paral.play();
        });



        //String backgroundColor = style.lines().
    }
}