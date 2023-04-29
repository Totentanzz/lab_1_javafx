package lab_1.fishApp.model;

import java.io.FileNotFoundException;

public class GuppyFish extends Fish {

    public GuppyFish() throws FileNotFoundException {
        super();
    }

    public GuppyFish(double x, double y, int xVelocity, int yVelocity, int id, int birthTime) throws FileNotFoundException {
        super(x,y,xVelocity,yVelocity,id,birthTime);
    }

    public GuppyFish(GuppyFish object) {
        super(object);
    }

    @Override
    protected void initImagePath() {
        this.imagePath = "src/main/resources/lab_1/fishApp/gifs/dance-pepe.gif";
    }

}
