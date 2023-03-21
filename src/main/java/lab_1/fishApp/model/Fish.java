package lab_1.fishApp.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Fish {

    Fish(double x, double y, String imagePath) throws FileNotFoundException {
        Image fishImage = new Image(new FileInputStream(imagePath));
        this.fishView = new ImageView(fishImage);
        this.fishView.setPreserveRatio(true);
        this.fishView.setX(x);
        this.fishView.setY(y);
        this.fishView.setFitWidth(90);
        this.fishView.setFitHeight(90);
    }

    public ImageView getView(){
        return this.fishView;
    }

    private final ImageView fishView;
}
