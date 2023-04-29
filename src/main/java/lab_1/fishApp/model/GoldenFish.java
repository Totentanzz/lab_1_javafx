package lab_1.fishApp.model;

import java.io.FileNotFoundException;

public class GoldenFish extends Fish {

    public GoldenFish() throws FileNotFoundException {
        super();
    }

    public GoldenFish(double x, double y,int xVelocity,int yVelocity, int id, int birthTime) throws FileNotFoundException {
        super(x,y,xVelocity,yVelocity,id,birthTime);
    }

    public GoldenFish(GoldenFish object) {
        super(object);
    }

    @Override
    protected void initImagePath() {
        this.imagePath = "src/main/resources/lab_1/fishApp/gifs/clown-pepe.gif";
    }
}
