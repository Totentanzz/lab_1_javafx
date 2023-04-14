package lab_1.fishApp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Fish {

    private ImageView imageView;
    private DoubleProperty xCoord, yCoord;
    private int xVelocity, yVelocity;
    private int birthTime;
    private int id;

    Fish(String imagePath) throws FileNotFoundException {
        this(0,0,0,0,0,0,imagePath);
    }

    Fish(double x, double y, int xVelocity, int yVelocity, int id, int birthTime, String imagePath) throws FileNotFoundException {
        Image fishImage = new Image(new FileInputStream(imagePath));
        this.imageView = new ImageView(fishImage);
        this.imageView.setPreserveRatio(true);
        this.imageView.setX(x);
        this.imageView.setY(y);
        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);
        this.birthTime=birthTime;
        this.id=id;
        this.xCoord=this.imageView.xProperty();;
        this.yCoord=this.imageView.yProperty();
        this.xVelocity=xVelocity;
        this.yVelocity=yVelocity;
    }

    Fish(Fish object) {
        this.imageView = new ImageView(object.getImageView().getImage());
        this.birthTime = object.birthTime;
        this.id = object.id;
        this.xCoord = object.xCoord;
        this.yCoord = object.yCoord;
        this.xVelocity = object.xVelocity;
        this.yVelocity = object.yVelocity;
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
        return (int)this.xCoord.get();
    }

    public void setXCoord(int newXCoord) {
        this.xCoord.set(newXCoord);
    }

    public int getYCoord() {
        return (int)this.yCoord.get();
    }

    public void setYCoord(int newYCoord) {
        this.yCoord.set(newYCoord);
    }

    public void setXVelocity(int newXVelocity) {this.xVelocity=newXVelocity;}

    public int getXVelocity() {return this.xVelocity;}

    public void setYVelocity(int newYVelocity) {this.yVelocity=newYVelocity;}

    public int getYVelocity() {return this.yVelocity;}

}
