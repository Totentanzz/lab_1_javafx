package lab_1.fishApp;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import java.util.stream.Stream;

public class DialogWindow<T> extends Dialog<T> {

    private TextFlow textFlow;
    private Text text;

    public DialogWindow(DialogType dialogType) {
        this(dialogType,"",new ImageView(),new ImageView());
    }

    public DialogWindow(DialogType dialogType, String contentText, ImageView firstView, ImageView secondView){
        this.getDialogPane().setPrefWidth(600);
        this.getDialogPane().setPrefHeight(300);
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
                this.getDialogPane().setContent(contentBox);
                this.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                this.getDialogPane().setPrefWidth(contentBox.getPrefWidth());
                this.getDialogPane().setPrefHeight(contentBox.getPrefHeight());
                this.setTitle("Statistics window");
                break;
            case CONSOLE:
                TextArea consoleField = new TextArea();
                consoleField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                        "-fx-font-size: 16; -fx-control-inner-background: black; -fx-font-weight: bold");
                this.getDialogPane().setContent(consoleField);
                this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                this.setTitle("Console window");
        }
    }

    public static enum DialogType {
        STATISTICS,
        CONSOLE;
        private DialogType(){

        }
    }
}
