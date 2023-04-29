package lab_1.fishApp;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import lab_1.fishApp.controller.HabitatController;
import lab_1.fishApp.model.Habitat;
import lab_1.fishApp.web.Client;

import java.io.File;

public class FishApp extends Application {

    private HabitatController habitatController;
    private Habitat habitat = new Habitat();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client thisClient = new Client();
        File serverConfigFile = new File("src/main/resources/lab_1/fishApp/webConfig/web.conf");
        Config serverConfig = ConfigFactory.parseFile(serverConfigFile).getConfig("server");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/fishApp.fxml"));
        primaryStage = loader.load();
        habitatController = loader.getController();
        habitatController.setHabitatModel(habitat);
        habitatController.setClient(thisClient);
        thisClient.startConnection(serverConfig);
        //Scene scene = new Scene(root.getScene().getRoot());
        //primaryStage.setScene(scene);
        //.setWidth(1400);
        //primaryStage.setHeight(900);
        primaryStage.setTitle("FishApp");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
