package com.example.fishapp.lab_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class FishApp extends Application {

    private static HabitatController mainController;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        stage = loader.load();
        mainController = loader.getController();
//        Scene scene = new Scene(stage1.getScene().getRoot());
//        this.initialize(stage, pane);
//        //setActionsOnKeys(scene);
//        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.show();
    }

    public static HabitatController getMainController(){
        return mainController;
    }

    public static void main(String[] args) {
        launch();
        mainController.getTimer().cancel();
    }
}
