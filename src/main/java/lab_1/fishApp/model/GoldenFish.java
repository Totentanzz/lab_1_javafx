package lab_1.fishApp.model;

import java.io.FileNotFoundException;

public class GoldenFish extends Fish implements IBehaviour {

    private static final String goldenImagePath = "src/main/resources/lab_1/fishApp/gifs/clown-pepe.gif";

    public GoldenFish() throws FileNotFoundException {
        super(goldenImagePath);
    }

    public GoldenFish(double x, double y,int xVelocity,int yVelocity, int id, int birthTime) throws FileNotFoundException {
        super(x,y,xVelocity,yVelocity,id,birthTime,goldenImagePath);
    }

    public GoldenFish(GoldenFish object) {
        super(object);
    }

}
