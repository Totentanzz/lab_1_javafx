package lab_1.fishApp.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Paths;

public class FileExplorer {

    private static FileChooser fileChooser;

    static {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Config files (*.conf)", "*.conf"),
                        new FileChooser.ExtensionFilter("Serialized files (*.ser)", "*.ser"));
    }

    public static File getFile(ActionType actionType, FileType fileType, Window window) {
        File file = null;
        if (fileType==FileType.CONFIG) {
            fileChooser.setInitialDirectory(Paths.get("src/main/resources/lab_1/fishApp/clientConfig")
                    .toAbsolutePath().toFile().getAbsoluteFile());
            fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));
        }
        else {
            fileChooser.setInitialDirectory(Paths.get("src/main/resources/lab_1/fishApp/objects")
                    .toAbsolutePath().toFile().getAbsoluteFile());
            fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(1));
        }
        switch (actionType) {
            case SAVE:
                file = fileChooser.showSaveDialog(window);
                break;
            case LOAD:
                file = fileChooser.showOpenDialog(window);
        }
        return file;
    }

    public static enum ActionType {
        SAVE,
        LOAD;
    }

    public static enum FileType {
        CONFIG,
        OBJECT;
    }
}
