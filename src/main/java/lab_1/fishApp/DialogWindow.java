package lab_1.fishApp;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class DialogWindow<T> extends Dialog<T> {

    private TextFlow textFlow;
    private Text text;

    public DialogWindow(DialogType dialogType) {
        this(dialogType,"");
    }

    public DialogWindow(DialogType dialogType, String contentText){
        TextArea consoleField = new TextArea();
        text = new Text(contentText);
        textFlow = new TextFlow(text);
        this.getDialogPane().setPrefWidth(600);
        this.getDialogPane().setPrefHeight(300);
        switch (dialogType){
            case STATISTICS:
                this.setTitle("Statistics window");
                this.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
                this.getDialogPane().setContent(textFlow);
                this.getDialogPane().setPrefWidth(textFlow.getPrefWidth());
                this.getDialogPane().setPrefHeight(textFlow.getPrefHeight());
                break;
            case CONSOLE:
                this.setTitle("Console window");
                this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                this.getDialogPane().setContent(consoleField);
                consoleField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                    "-fx-font-size: 16; -fx-control-inner-background: black; -fx-font-weight: bold");
        }
    }

    public static enum DialogType {
        STATISTICS,
        CONSOLE;
        private DialogType(){

        }
    }
}
