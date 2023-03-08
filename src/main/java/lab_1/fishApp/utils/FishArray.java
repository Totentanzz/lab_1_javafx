package lab_1.fishApp.utils;

import java.util.ArrayList;

public class FishArray {

    private static volatile FishArray fishArray;
    private ArrayList<Fish> fishArrayList;

    private FishArray(){
        fishArrayList = new ArrayList<>();
    }
    public static FishArray getInstance(){
        FishArray localFishArray = fishArray;
        if (localFishArray==null){
            synchronized (FishArray.class){
                localFishArray = fishArray;
                if (localFishArray==null){
                    fishArray = localFishArray = new FishArray();
                }
            }
        }
        return localFishArray;
    }

    public ArrayList<Fish> getFishArrayList(){
        return fishArrayList;
    }
}
