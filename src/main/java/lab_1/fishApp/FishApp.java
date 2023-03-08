package lab_1.fishApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lab_1.fishApp.controller.HabitatController;
import lab_1.fishApp.model.HabitatModel;

public class FishApp extends Application {

    private HabitatController habitatController;
    private HabitatModel habitatModel = new HabitatModel();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/fishApp.fxml"));
        primaryStage = loader.load();
        habitatController = loader.getController();
        habitatController.setHabitatModel(habitatModel);
        //Scene scene = new Scene(root.getScene().getRoot());
        //primaryStage.setScene(scene);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        primaryStage.setTitle("FishApp");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
