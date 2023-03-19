package lab_1.fishApp.model;

import java.io.FileNotFoundException;

public class GoldenFish extends Fish implements IBehaviour {

    public GoldenFish(double x, double y, String imagePath) throws FileNotFoundException {
        super(x,y,imagePath);
    }

}
