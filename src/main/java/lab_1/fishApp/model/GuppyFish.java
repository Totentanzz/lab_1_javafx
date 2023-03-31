package lab_1.fishApp.model;

import java.io.FileNotFoundException;

public class GuppyFish extends Fish implements IBehaviour {

    private static final String guppyImagePath = "src/main/resources/lab_1/fishApp/gifs/dance-pepe.gif";;

    public GuppyFish() throws FileNotFoundException {
        super(guppyImagePath);
    }

    public GuppyFish(double x, double y, int id, int birthTime) throws FileNotFoundException {
        super(x,y,id,birthTime,guppyImagePath);
    }

    public GuppyFish(GuppyFish object) {
        super(object);
    }
}
