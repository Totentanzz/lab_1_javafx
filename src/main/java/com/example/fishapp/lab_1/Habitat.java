package com.example.fishapp.lab_1;

import javafx.fxml.Initializable;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;


public class Habitat {

    private final static String imagePath1, imagePath2;
    private static int spawnTime1, spawnTime2;
    private static short spawnProbability1, spawnProbability2;
    private static ArrayList<Fish> fishArray;

    Habitat(){
    }

    static{
        imagePath1 = "src/main/resources/com/example/fishapp/lab_1/gifs/clown-pepe.gif";
        imagePath2 = "src/main/resources/com/example/fishapp/lab_1/gifs/dance-pepe.gif";
        spawnTime1 = 3;
        spawnTime2 = 5;
        spawnProbability1 = 100;
        spawnProbability2 = 100;
        fishArray = FishArray.getInstance();
    }

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        spawnTime1 = 3;
//        spawnTime2 = 5;
//        spawnProbability1 = 100;
//        spawnProbability2 = 100;
//        fishArray = FishArray.getInstance();
//    }

//    private void setActionsOnKeys(Scene scene){ //контроллер
//        scene.setOnKeyPressed(keyEvent -> {
//            KeyCode pressedKey = keyEvent.getCode();
//            if (pressedKey==KeyCode.B && !isStarted) {
//                System.out.println("Pressed B");
//                imagePane.getChildren().removeIf(node -> (node instanceof ImageView));
//                startSimulation();
//            }
//            else if (pressedKey==KeyCode.E && isStarted){
//                stopSimulation();
//                System.out.println("Simulation has stopped");
//            }
//            else if (pressedKey==KeyCode.T){
//                Label timeLabel = mainController.getTimeLabel();
//                showLabel(timeLabel);
//            }
//        });
//    }
//
//    private void startSimulation() { //контр
//        startTime=System.currentTimeMillis();
//        isStarted=true;
//        mainController.getStatisticsLabel().setVisible(false);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(()->{
//                    long simulationTime = (System.currentTimeMillis()-startTime)/1000;
//                    try {
//                        System.out.println("Trying to update");
//                        update(simulationTime);
//                    } catch (FileNotFoundException exception) {
//                        throw new RuntimeException(exception);
//                    }
//                });
//            };
//        },0,1000);
//    }
//
//    private void stopSimulation() { //контр
//        timer.cancel();
//        timer.purge();
//        timer = new Timer();
//        refreshStatisticsLabel();
//        showLabel(mainController.getStatisticsLabel());
//        fishArray.clear();
//        isStarted=false;
//    }
//
//    private void showLabel (Label label) { //контр
//        boolean isLableVisible = label.isVisible();
//        if (isLableVisible){
//            label.setVisible(false);
//        }
//        else {
//            label.setVisible(true);
//        }
//        System.out.println(label.getText());
//    }
//
//    private void update(long time) throws FileNotFoundException {
//        spawnFish(time);
//        refreshTimeLabel(time);
//    }

    public static void spawnFish(long time) throws FileNotFoundException {
        HabitatController controller = FishApp.getMainController();
        Random randomGenerator = new Random();
        int generatedDigit = randomGenerator.nextInt(100);
        int x,y;
        if ((time%spawnTime1==0)&&(generatedDigit<spawnProbability1)){
//            x = (int) controller.getControlPane().getWidth() + randomGenerator.
//                    nextInt((int) controller.getImagePane().getWidth()-120);
//            y = (int) controller.getControlPane().getHeight() + randomGenerator.
//                    nextInt((int)controller.getImagePane().getHeight()-120);
            x=randomGenerator.nextInt((int)controller.getImagePane().getWidth()-120);
            y=randomGenerator.nextInt((int)controller.getImagePane().getHeight()-120);
            System.out.println(x);
            System.out.println(y);
            GoldenFish fish = new GoldenFish(x,y,imagePath1);
            fishArray.add(fish);
            controller.getImagePane().getChildren().add(fish.getView());
        }
//        if ((time%spawnTime2==0)&&(generatedDigit<spawnProbability2)){
//            x = (int) controller.getControlPane().getWidth() + randomGenerator.
//                    nextInt((int) controller.getImagePane().getWidth()-120);
//            y = (int) controller.getControlPane().getHeight() + randomGenerator.
//                    nextInt((int)controller.getImagePane().getHeight()-120);
//            GuppyFish fish = new GuppyFish(x,y,imagePath2);
//            fishArray.add(fish);
//            controller.getImagePane().getChildren().add(fish.getView());
//        }
    }

//    private void refreshTimeLabel(long time){ //контр
//        Label timeText = mainController.getTimeLabel();
//        timeText.setText("Simulation time: " + time);
//        System.out.println("Simulation time: " + time);
//    }
//
//    private void refreshStatisticsLabel() { //контр
//        Label statisticsLabel = mainController.getStatisticsLabel();
//        long goldAmount = fishArray.stream().filter(
//                obj -> obj instanceof GoldenFish).count();
//        long guppyAmount = fishArray.size()-goldAmount;
//        statisticsLabel.setText("Golden fish: " +
//                goldAmount + "\n" + "Guppy fish: " + guppyAmount);
//    }

}