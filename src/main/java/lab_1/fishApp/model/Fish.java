package lab_1.fishApp.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Fish {

    private ImageView imageView;
    private int xCoord, yCoord;
    private int birthTime;
    private int id;

    Fish(String imagePath) throws FileNotFoundException {
        this(0,0,0,0,imagePath);
    }

    Fish(double x, double y, int id, int birthTime, String imagePath) throws FileNotFoundException {
        Image fishImage = new Image(new FileInputStream(imagePath));
        this.imageView = new ImageView(fishImage);
        this.imageView.setPreserveRatio(true);
        this.imageView.setX(x);
        this.imageView.setY(y);
        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);
        this.birthTime=birthTime;
        this.id = id;
        this.xCoord= (int) x;
        this.yCoord= (int) y;
    }

    Fish(Fish object) {
        this.imageView = new ImageView(object.getImageView().getImage());
        this.birthTime = object.birthTime;
        this.id = object.id;
        this.xCoord = object.xCoord;
        this.yCoord = object.yCoord;
    }

    public ImageView getImageView(){
        return this.imageView;
    }

    public void setImageView(ImageView newImageView) {
        this.imageView = newImageView;
    }

    public int getBirthTime(){
        return this.birthTime;
    }

    public void setBirthTime(int newTime){
        this.birthTime=newTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public int getXCoord() {
        return this.xCoord;
    }

    public void setXCoord(int newXCoord) {
        this.xCoord=newXCoord;
    }

    public int getYCoord() {
        return this.yCoord;
    }

    public void setYcoord(int newYCoord) {
        this.yCoord=newYCoord;
    }

}
