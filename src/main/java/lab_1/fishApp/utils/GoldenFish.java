package lab_1.fishApp.utils;

import java.io.FileNotFoundException;

public class GoldenFish extends Fish implements IBehaviour {

    public GoldenFish(double x, double y, String imagePath) throws FileNotFoundException {
        super(x,y,imagePath);
    }

}
