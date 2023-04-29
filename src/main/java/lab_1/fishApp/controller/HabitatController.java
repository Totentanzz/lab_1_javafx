package lab_1.fishApp.controller;

import com.typesafe.config.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import lab_1.fishApp.utils.DialogWindow;
import lab_1.fishApp.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lab_1.fishApp.utils.BaseAI;
import lab_1.fishApp.utils.ConfigUpdater;
import lab_1.fishApp.utils.FileExplorer;
import lab_1.fishApp.utils.UniqueNumberGenerator;
import lab_1.fishApp.web.Client;
import lab_1.fishApp.web.ClientListListener;
import lab_1.fishApp.web.ClientServerListener;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HabitatController implements Initializable {

    @FXML
    public Button clientsButton;
    @FXML
    private Label clientsLabel;
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
    private CheckBox goldenFishThreadBox;
    @FXML
    private CheckBox guppyFishThreadBox;
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
    private MenuBar menuBar;
    private Habitat habitat;
    private Client client;
    private boolean startFlag;
    private Duration simulationTime;
    private Timer timer;
    private TimerTask simulationTask;
    private UniqueNumberGenerator uniqueNumberGenerator;
    private ConfigUpdater configUpdater;
    private GoldenFishAI goldenFishAI;
    private GuppyFishAI guppyFishAI;
    private ClientServerListener clientListener;


    public void setHabitatModel(Habitat model) {
        this.habitat = model;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleShown(WindowEvent windowEvent) {
        this.initConfig();
        clientListener.start();
    }

    @FXML
    private void handleCloseRequest() {
        timer.cancel();
        client.stopConnection();
        this.clientListener.stopListener();
        this.saveConfig(new File("src/main/resources/lab_1/fishApp/clientConfig/default.conf"));
        this.mainStage.close();
    }

    @FXML
    private void setActionOnKey(KeyEvent keyEvent) throws FileNotFoundException {
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey == KeyCode.B && !startFlag) {
            System.out.println("Started simulating");
            startButton.fire();
        } else if (pressedKey == KeyCode.E && startFlag) {
            stopButton.fire();
            System.out.println("Simulation has stopped");
        } else if (pressedKey == KeyCode.T) {
            timeToggleGroup.selectToggle(timeLabel.isVisible() ? timeToggleGroup
                    .getToggles().get(1) : timeToggleGroup.getToggles().get(0));
        }
    }

    @FXML
    private void saveConfigToFile(ActionEvent actionEvent) {
        startFlag = false;
        Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
        File savedFile = FileExplorer.getFile(FileExplorer.ActionType.SAVE,
                FileExplorer.FileType.CONFIG, this.mainScene.getWindow());
        this.saveConfig(savedFile);
        startFlag = startButton.isDisabled();
        checkBoxesAndAI();
    }

    private void saveConfig(File file) {
        if (file != null || file.exists()) {
            Config config = ModelData.getInstance().getConfig();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(config.root().render(ConfigRenderOptions.concise()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void loadConfigFromFile(ActionEvent actionEvent) {
        startFlag = false;
        Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
        File loadedFile = FileExplorer.getFile(FileExplorer.ActionType.LOAD,
                FileExplorer.FileType.CONFIG, this.mainScene.getWindow());
        if (loadedFile != null) {
            Config appConfig = ConfigFactory.parseFile(loadedFile);
            this.loadConfig(appConfig);
        }
        startFlag = startButton.isDisabled();
        checkBoxesAndAI();
    }

    private void loadConfig(Config appConfig) {
        this.goldenBox.setValue(appConfig.getString("goldenSpawnChance"));
        this.guppyBox.setValue(appConfig.getString("guppySpawnChance"));
        this.goldenSpawnSpinner.getValueFactory().setValue(appConfig.getInt("goldenSpawnTime"));
        this.guppySpawnSpinner.getValueFactory().setValue(appConfig.getInt("guppySpawnTime"));
        this.goldenLifeSpinner.getValueFactory().setValue(appConfig.getInt("goldenLifeTime"));
        this.guppyLifeSpinner.getValueFactory().setValue(appConfig.getInt("guppyLifeTime"));
        this.timeToggleGroup.getToggles().get(0).setSelected(appConfig.getBoolean("timeRadioButton"));
        this.checkBox.setSelected(appConfig.getBoolean("infoCheckBox"));
        this.goldenFishThreadBox.setSelected(appConfig.getBoolean("goldenFishThreadBox"));
        this.guppyFishThreadBox.setSelected(appConfig.getBoolean("guppyFishThreadBox"));
        if (appConfig.getBoolean("timeRadioButton"))
            this.timeToggleGroup.selectToggle(this.timeToggleGroup.getToggles().get(0));
        else
            this.timeToggleGroup.selectToggle(this.timeToggleGroup.getToggles().get(1));
        configUpdater.update();
    }

    @FXML
    private void SerializeAndSave(ActionEvent actionEvent) {
        startFlag = false;
        Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
        File savedFile = FileExplorer.getFile(FileExplorer.ActionType.SAVE,
                FileExplorer.FileType.OBJECT,this.mainScene.getWindow());
        if (savedFile!=null) {
            try {
                FileOutputStream fileOut = new FileOutputStream(savedFile);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject( ModelData.getInstance().getFishList());
                out.close();
                fileOut.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        startFlag = startButton.isDisabled();
        checkBoxesAndAI();
    }

    @FXML
    private void DeserializeAndLoad(ActionEvent actionEvent) {
        startFlag = false;
        Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
        File loadedFile = FileExplorer.getFile(FileExplorer.ActionType.LOAD,
                FileExplorer.FileType.OBJECT,this.mainScene.getWindow());
        ModelData modelData = ModelData.getInstance();
        if (loadedFile!=null) {
            try {
                FileInputStream fileIn = new FileInputStream(loadedFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                LinkedList<Fish> loadedList = (LinkedList<Fish>) in.readObject();
                imagePane.getChildren().clear();
                modelData.clearCollections();
                loadedList.stream().forEach(fish->{
                    fish.setBirthTime((int)this.simulationTime.toSeconds());
                    modelData.getFishList().add(fish);
                    modelData.getIdSet().add(fish.getId());
                    modelData.getBirthTimeTree().put(fish.getId(), fish.getBirthTime());
                    imagePane.getChildren().add(fish.getImageView());
                });
                in.close();
                fileIn.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        startFlag = startButton.isDisabled();
        if (startFlag) checkBoxesAndAI();
    }

    private void startSimulation() {
        Platform.runLater(() -> imagePane.getChildren().removeIf(node -> (node instanceof ImageView)));
        simulationTime = Duration.ZERO;
        startFlag = true;
        statisticsLabel.setVisible(false);
        timer.schedule(simulationTask, 0, 1000);
        Stream.of(goldenFishAI, guppyFishAI).forEach(ai -> {
            ai.startAI();
            ai.pauseAI();
        });
        checkBoxesAndAI();
    }

    private void stopSimulation() throws FileNotFoundException, InterruptedException {
        Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
        if (checkBox.isSelected()) {
            startFlag = false;
            refreshStatisticsLabel();
            ImageView goldView = new GoldenFish().getImageView();
            ImageView guppyView = new GuppyFish().getImageView();
            System.out.println(statisticsLabel.getText());
            DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.STATISTICS,
                    statisticsLabel.getText(), goldView, guppyView,null);
            window.initOwner(mainStage);
            if (window.showAndWait().get() == ButtonType.OK) {
                stopAndClear();
            } else {
                startFlag = true;
                checkBoxesAndAI();
            }
        } else {
            refreshStatisticsLabel();
            stopAndClear();
        }
    }

    private void stopAndClear() {
        showLabel(statisticsLabel);
        synchronized (habitat.getFishData()) {
            habitat.getFishData().clearCollections();
        }
        startFlag = false;
        simulationTask.cancel();
        initTimers();
    }

    private void showLabel(Label label) {
        boolean isLabelVisible = label.isVisible();
        label.setVisible(!isLabelVisible);
    }

    private void update(long time) throws FileNotFoundException {
        refreshImagePaneChildren(time);
        refreshTimeLabel(time);
        removeDeadFish(time);
    }

    private void refreshImagePaneChildren(long time) throws FileNotFoundException {
        double xBound = imagePane.getWidth() - 120, yBound = imagePane.getHeight() - 120;
        int goldenSpawnTime = habitat.getGoldenSpawnTime();
        int guppySpawnTime = habitat.getGuppySpawnTime();
        short goldenSpawnChance = habitat.getGoldenSpawnChance();
        short guppySpawnChance = habitat.getGuppySpawnChance();
        Random randomGenerator = new Random();
        int generatedDigit = randomGenerator.nextInt(100);
        synchronized (habitat.getFishData().getFishList()) {
            if ((time % goldenSpawnTime == 0) && (generatedDigit < goldenSpawnChance)) {
                Fish createdFish = habitat.createFish(xBound, yBound, uniqueNumberGenerator.getNext(),
                        (int) this.simulationTime.toSeconds(), GoldenFish.class);
                Platform.runLater(() -> imagePane.getChildren().add(createdFish.getImageView()));
            }
            if ((time % guppySpawnTime == 0) && (generatedDigit < guppySpawnChance)) {
                Fish createdFish = habitat.createFish(xBound, yBound, uniqueNumberGenerator.getNext(),
                        (int) this.simulationTime.toSeconds(), GuppyFish.class);
                Platform.runLater(() -> imagePane.getChildren().add(createdFish.getImageView()));
            }
        }
    }

    private void refreshTimeLabel(long time) {
        Platform.runLater(() -> timeLabel.setText("Simulation time: " + time));
    }

    private void removeDeadFish(long time) {
        ModelData modelData = habitat.getFishData();
        ObservableList<Node> imageViews = imagePane.getChildren();
        LinkedList<Fish> curFishList = modelData.getFishList();
        LinkedList<Fish> deadFish = curFishList.stream().filter(obj -> {
            int lifeTime = (obj instanceof GoldenFish) ?
                    habitat.getGoldenLifeTime() : habitat.getGuppyLifeTime();
            return (obj.getBirthTime() + lifeTime <= time);
        }).collect(Collectors.toCollection(LinkedList::new));
        deadFish.forEach(obj -> {
            int objId = obj.getId();
            modelData.getIdSet().remove(objId);
            modelData.getBirthTimeTree().remove(objId);
            synchronized (curFishList) {
                curFishList.remove(obj);
            }
            Platform.runLater(() -> imageViews.remove(obj.getImageView()));
        });
    }

    private void refreshClientsLabel() {
        LinkedList<String> clientList = this.client.getClientNames();
        Platform.runLater(()->{
            this.clientsLabel.setText(String.join("\n",clientList));
        });
    }

    private void refreshStatisticsLabel() {
        long goldAmount = habitat.getFishAmount(GoldenFish.class);
        long guppyAmount = habitat.getFishAmount(GuppyFish.class);
        statisticsLabel.setText("Golden fish (pepe-clown): " + goldAmount + "\n" + "Guppy fish (pepe-dancer): " + guppyAmount);
    }

    private Config toConfig() {
        Config configFile = ConfigFactory.empty();
        configFile = configFile.withValue("timeRadioButton", ConfigValueFactory
                .fromAnyRef(timeToggleGroup.getToggles().get(0).isSelected()));
        configFile = configFile.withValue("infoCheckBox", ConfigValueFactory
                .fromAnyRef(checkBox.isSelected()));
        configFile = configFile.withValue("goldenFishThreadBox", ConfigValueFactory
                .fromAnyRef(goldenFishThreadBox.isSelected()));
        configFile = configFile.withValue("guppyFishThreadBox", ConfigValueFactory
                .fromAnyRef(guppyFishThreadBox.isSelected()));
        return configFile;
    }

    private void initTimers() {
        simulationTask = new TimerTask() {
            @Override
            public void run() {
                if (startFlag) {
                    simulationTime = simulationTime.add(Duration.seconds(1));
                    try {
                        update((long) simulationTime.toSeconds());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        timer = new Timer();
    }

    private void initSpinners() {
        goldenSpawnSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            habitat.setGoldenSpawnTime(newValue);
            configUpdater.update();
        });
        goldenLifeSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            habitat.setGoldenLifeTime(newValue);
            configUpdater.update();
        });
        guppySpawnSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            habitat.setGuppySpawnTime(newValue);
            configUpdater.update();
        });
        guppyLifeSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            habitat.setGuppyLifeTime(newValue);
            configUpdater.update();
        });
        Stream.of(goldenSpawnSpinner, goldenLifeSpinner, guppySpawnSpinner, guppyLifeSpinner)
                .forEach(spinner -> spinner.getEditor().setTextFormatter(new TextFormatter<Integer>(
                        change -> {
                            String input = change.getControlNewText();
                            if (input.matches("[0-9]*")) {
                                try {
                                    int value = Integer.parseInt(input);
                                    if (value > 0 && value <= 1000) {
                                        return change;
                                    }
                                } catch (NumberFormatException exception) {
                                }
                            }
                            return null;
                        })));
    }

    private void initRadioButtons() {
        ObservableList<Toggle> toggles = timeToggleGroup.getToggles();
        toggles.get(0).selectedProperty().addListener(observable -> {
            timeLabel.setVisible(true);
            configUpdater.update();
        });
        toggles.get(1).selectedProperty().addListener(observable -> {
            timeLabel.setVisible(false);
        });
    }

    private void initCheckBoxes() {
        goldenFishThreadBox.setOnAction(action -> {
            if (goldenFishThreadBox.isSelected()) goldenFishAI.resumeAI();
            else goldenFishAI.pauseAI();
            configUpdater.update();
        });
        guppyFishThreadBox.setOnAction(action -> {
            if (guppyFishThreadBox.isSelected()) guppyFishAI.resumeAI();
            else guppyFishAI.pauseAI();
            configUpdater.update();
        });
        checkBox.setOnAction(action -> {
            configUpdater.update();
        });
        Stream.of(goldenFishThreadBox, guppyFishThreadBox).forEach(obj -> obj.setSelected(true));
    }

    private void initComboBoxes() {
        ObservableList<String> boxChoices = IntStream.rangeClosed(0, 100).filter(i -> i % 10 == 0).mapToObj(
                Integer::toString).collect(Collectors.toCollection(FXCollections::observableArrayList));
        Stream.of(goldenBox, guppyBox).peek(box -> box.setItems(boxChoices)).forEach(box ->
                box.getSelectionModel().select(10));
        goldenBox.setOnAction(action-> {
            habitat.setGoldenSpawnChance(Short.valueOf(goldenBox.getValue()));
            configUpdater.update();
        });
        guppyBox.setOnAction(action-> {
            habitat.setGuppySpawnChance(Short.valueOf(guppyBox.getValue()));;
            configUpdater.update();
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
                if (!startFlag) {
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
            startFlag = false;
            Stream.of(goldenFishAI, guppyFishAI).forEach(BaseAI::pauseAI);
            DialogWindow<ButtonType> window = new DialogWindow<>(DialogWindow.DialogType.OBJECTS);
            window.initOwner(mainStage);
            window.showAndWait();
            startFlag = true;
            checkBoxesAndAI();
        });
    }

    private void checkBoxesAndAI() {
        if (goldenFishThreadBox.isSelected()) goldenFishAI.resumeAI();
        if (guppyFishThreadBox.isSelected()) guppyFishAI.resumeAI();
    }

    private void initConfig() {
        Config appConfig = null;
        File configFile = new File("src/main/resources/lab_1/fishApp/clientConfig/default.conf");
        if (configFile.exists()) {
            appConfig = ConfigFactory.parseFile(configFile);
        }
        else {
            appConfig = createConfig();
        }
        loadConfig(appConfig);
        ModelData.getInstance().setConfig(appConfig);
    }

    private Config createConfig() {
        Config modelConfig = habitat.toConfig();
        Config controllerConfig = this.toConfig();
        Config newConfig = modelConfig.withFallback(controllerConfig);
        return newConfig;
    }

    private void initClientListener() {
        clientListener = new ClientServerListener(new ClientListListener() {
            @Override
            public void handleClientListChanges() {
                if (client.isUpdated()) {
                    LinkedList<String> newClientNames = client.getClientNames();
                    ModelData.getInstance().setClientsNames(newClientNames);
                    Platform.runLater(() -> {
                        clientsLabel.setText(String.join(", ",newClientNames));
                    });
                }
                client.setToDefaultState();
            }

            @Override
            public boolean isClosed() {
                return false;
            }
        },1000);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startFlag = false;
        uniqueNumberGenerator = new UniqueNumberGenerator(0, 1000000);
        configUpdater = new ConfigUpdater(()->{
                Config updatedConfig = createConfig();
                ModelData.getInstance().setConfig(updatedConfig);
        });
        initTimers();
        initSpinners();
        initRadioButtons();
        initCheckBoxes();
        initComboBoxes();
        initStartButtons();
        initClientListener();
        goldenFishAI = new GoldenFishAI((int) imagePane.getPrefWidth(), (int) imagePane.getPrefHeight());
        guppyFishAI = new GuppyFishAI((int) imagePane.getPrefWidth(), (int) imagePane.getPrefHeight());;
    }

}