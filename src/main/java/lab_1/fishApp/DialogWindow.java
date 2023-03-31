package lab_1.fishApp;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import lab_1.fishApp.model.Fish;
import lab_1.fishApp.model.FishData;
import lab_1.fishApp.model.GoldenFish;
import lab_1.fishApp.model.GuppyFish;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DialogWindow<T> extends Dialog<T> {

    private TextFlow textFlow;
    private Text text;

    public DialogWindow(DialogType dialogType) {
        this(dialogType,"",new ImageView(),new ImageView());
    }

    public DialogWindow(DialogType dialogType, String contentText, ImageView firstView, ImageView secondView){
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setPrefWidth(600);
        dialogPane.setPrefHeight(300);
        switch (dialogType){
            case STATISTICS:
                String[] contentLines = contentText.split("\n");
                Label firstLabel = new Label(contentLines[0]);
                Label secondLabel = new Label(contentLines[1]);
                Stream.of(firstLabel,secondLabel).forEach(label->label.setStyle("-fx-font-size: 16; -fx-font-weight: bold;"));
                Stream.of(firstView,secondView).forEach(view->view.setFitHeight(29));
                HBox firstBox = new HBox(firstView,firstLabel);
                HBox secondBox = new HBox(secondView,secondLabel);
                VBox contentBox = new VBox(firstBox,secondBox);
                this.initModality(Modality.WINDOW_MODAL);
                dialogPane.setContent(contentBox);
                dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                dialogPane.setPrefWidth(contentBox.getPrefWidth());
                dialogPane.setPrefHeight(contentBox.getPrefHeight());
                this.setTitle("Statistics window");
                break;
            case OBJECTS:
                ObservableList<Fish> fishList = FXCollections.observableList(FishData.getInstance().fishList)
                        .stream().map(obj -> {
                            if (obj instanceof GoldenFish) {
                                GoldenFish newFish = new GoldenFish((GoldenFish) obj);
                                newFish.getImageView().setPreserveRatio(true);
                                newFish.getImageView().setFitHeight(29);
                                return newFish;
                            }
                            else {
                                GuppyFish newFish = new GuppyFish((GuppyFish) obj);
                                newFish.getImageView().setPreserveRatio(true);
                                newFish.getImageView().setFitHeight(29);
                                return newFish;
                            }
                        }).collect(Collectors.toCollection(FXCollections::observableArrayList));
                TableView<Fish> table = new TableView<>(fishList);
                TableColumn<Fish,ImageView> viewColumn = new TableColumn<>("views");
                TableColumn<Fish,String> classColumn = new TableColumn<>("class");
                TableColumn<Fish,Integer> xColumn = new TableColumn<>("xAxis coordinate");
                TableColumn<Fish,Integer> yColumn = new TableColumn<>("yAxis coordinate");
                TableColumn<Fish,Integer> idColumn = new TableColumn<>("id");
                TableColumn<Fish,Integer> birthTimeColumn = new TableColumn<>("birthTime");
                viewColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
                classColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClass().getSimpleName()));
                xColumn.setCellValueFactory(new PropertyValueFactory<>("xCoord"));
                yColumn.setCellValueFactory(new PropertyValueFactory<>("yCoord"));
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                birthTimeColumn.setCellValueFactory(new PropertyValueFactory<>("birthTime"));
                table.getColumns().addAll(viewColumn,classColumn,xColumn,yColumn,idColumn,birthTimeColumn);
                dialogPane.setContent(table);
                dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
                this.initModality(Modality.WINDOW_MODAL);
                this.setTitle("Table of objects");
                break;
            case CONSOLE:
                TextArea consoleField = new TextArea();
                consoleField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                        "-fx-font-size: 16; -fx-control-inner-background: black; -fx-font-weight: bold");
                dialogPane.setContent(consoleField);
                dialogPane.getButtonTypes().add(ButtonType.CLOSE);
                this.setTitle("Console window");
        }
    }

    public static enum DialogType {
        STATISTICS,
        OBJECTS,
        CONSOLE;
        private DialogType(){

        }
    }
}
