package lab_1.fishApp.utils;

import java.io.FileNotFoundException;

public class GuppyFish extends Fish implements IBehaviour {

    public GuppyFish(double x, double y, String imagePath) throws FileNotFoundException {
        super(x,y,imagePath);
    }

}
