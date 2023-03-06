package com.example.fishapp.lab_1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

abstract class Fish {

    Fish(double x, double y, String imagePath) throws FileNotFoundException {
        Image fishImage = new Image(new FileInputStream(imagePath));
        fishView = new ImageView(fishImage);
        fishView.setPreserveRatio(true);
        fishView.setX(x);
        fishView.setY(y);
        fishView.setFitWidth(90);
        fishView.setFitHeight(90);
    }

    public ImageView getView(){
        return this.fishView;
    }

    private final ImageView fishView;
}
