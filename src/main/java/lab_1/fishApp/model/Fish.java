package lab_1.fishApp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;

public abstract class Fish implements Serializable {


    protected String imagePath;
    private transient ImageView imageView;
    private transient DoubleProperty xCoord, yCoord;
    private int xVelocity, yVelocity;
    private int birthTime;
    private int id;

    Fish() throws FileNotFoundException {
        this(0,0,0,0,0,0);
    }

    Fish(double x, double y, int xVelocity, int yVelocity, int id, int birthTime) throws FileNotFoundException {
        initImagePath();
        this.imageView = new ImageView( new Image(new FileInputStream(this.imagePath)));
        this.imageView.setPreserveRatio(true);
        this.imageView.setX(x);
        this.imageView.setY(y);
        this.imageView.setFitWidth(90);
        this.imageView.setFitHeight(90);
        this.birthTime=birthTime;
        this.id=id;
//        this.xCoord = new SimpleDoubleProperty(x);
//        this.yCoord = new SimpleDoubleProperty(y);
//        imageView.xProperty().bind(xCoord);
//        imageView.yProperty().bind(yCoord);
        this.xCoord=this.imageView.xProperty();
        this.yCoord=this.imageView.yProperty();
        this.xVelocity=xVelocity;
        this.yVelocity=yVelocity;
    }

    Fish(Fish object) {
        this.imagePath = object.imagePath;
        this.imageView = new ImageView(object.getImageView().getImage());
        this.birthTime = object.birthTime;
        this.id = object.id;
        this.xCoord = object.xCoord;
        this.yCoord = object.yCoord;
        this.xVelocity = object.xVelocity;
        this.yVelocity = object.yVelocity;
    }

    protected abstract void initImagePath();

    public void getPath() {
        System.out.println(imagePath);
    }

    public ImageView getImageView(){
        return this.imageView;
    }

    private String _getImageView(){
        return this.imagePath;
    }

    public void setImageView(ImageView imageView) {
        this.imageView=imageView;
    }

    private void _setImageView(String imagePath) {
        this.imagePath = imagePath;
        try {
            this.imageView = new ImageView(new Image(new FileInputStream(imagePath)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    private int _getXCoord() {return (int)this.xCoord.get();}

    public void setXCoord(int newXCoord) {
        this.xCoord.set(newXCoord);
    }

    private int _getYCoord() {return (int)this.yCoord.get();}

    public int getYCoord() {return (int)this.yCoord.get();}

    public void setYCoord(int newYCoord) {
        this.yCoord.set(newYCoord);
    }

    public void setXVelocity(int newXVelocity) {this.xVelocity=newXVelocity;}

    public int getXVelocity() {return this.xVelocity;}

    public void setYVelocity(int newYVelocity) {this.yVelocity=newYVelocity;}

    public int getYVelocity() {return this.yVelocity;}

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(imageView.getFitWidth());
        out.writeDouble(imageView.getFitHeight());
        out.writeDouble(xCoord.get());
        out.writeDouble(yCoord.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.imageView = new ImageView(new Image(new FileInputStream(this.imagePath)));
        this.imageView.setPreserveRatio(true);
        this.imageView.setFitWidth(in.readDouble());
        this.imageView.setFitHeight(in.readDouble());
        this.imageView.setX(in.readDouble());
        this.imageView.setY(in.readDouble());
        this.xCoord = this.imageView.xProperty();
        this.yCoord = this.imageView.yProperty();
    }

}
