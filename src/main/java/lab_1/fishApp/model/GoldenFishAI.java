package lab_1.fishApp.model;

import lab_1.fishApp.utils.BaseAI;

public class GoldenFishAI extends BaseAI {

    public GoldenFishAI(int movesPerSecond, int xBound, int yBound){
        super(movesPerSecond,xBound,yBound);
    }

    public GoldenFishAI(int xBound, int yBound) {
        super(xBound,yBound);
    }

    @Override
    public void initFilter() {
        this.filter = object -> object instanceof GoldenFish;
    }

    @Override
    public void initMover() {
        int xBound = this.xBound;
        this.mover = object -> {
            GoldenFish fish = (GoldenFish) object;
            int curFishXCoord = fish.getXCoord();
            if ((curFishXCoord>=xBound-fish.getImageView().getFitWidth()-30
                    && (fish.getXVelocity()>0)) || (curFishXCoord <= 0 && (fish.getXVelocity()<0))) {
                fish.setXVelocity(-fish.getXVelocity());
            }
            int newFishXCoord = curFishXCoord + fish.getXVelocity();
            fish.setXCoord(newFishXCoord);
        };
    }
}
