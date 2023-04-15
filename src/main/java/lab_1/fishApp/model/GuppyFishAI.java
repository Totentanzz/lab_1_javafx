package lab_1.fishApp.model;

import javafx.application.Platform;
import lab_1.fishApp.utils.BaseAI;

public class GuppyFishAI extends BaseAI {

    public GuppyFishAI(int movesPerSecond, int xBound, int yBound){
        super(movesPerSecond,xBound,yBound);
    }

    public GuppyFishAI(int xBound, int yBound) {
        super(xBound,yBound);
    }
    @Override
    public void initFilter() {
        this.filter = object -> object instanceof GuppyFish;
    }

    @Override
    public void initMover() {
        int yBound = this.yBound;
        this.mover = object -> {
            GuppyFish fish = (GuppyFish) object;
            int curFishYCoord = fish.getYCoord();
            if ((curFishYCoord>=yBound-fish.getImageView().getFitHeight()-30
                    && (fish.getYVelocity()>0)) || (curFishYCoord <= 0 && (fish.getYVelocity()<0))) {
                fish.setYVelocity(-fish.getYVelocity());
            }
            int newFishYCoord = curFishYCoord + fish.getYVelocity();
            Platform.runLater(()->fish.setYCoord(newFishYCoord));
        };
    }
}
