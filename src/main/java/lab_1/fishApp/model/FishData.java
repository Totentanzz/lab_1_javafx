package lab_1.fishApp.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FishData {

    private static volatile FishData fishData;
    public LinkedList<Fish> fishList;
    public HashSet<Integer> idSet;
    public TreeMap<Integer,Integer> birthTimeTree;

    private FishData(){
        fishList = new LinkedList<>();
        idSet = new HashSet<>();
        birthTimeTree = new TreeMap<>();
    }
    public static FishData getInstance() {
        FishData localFishData = fishData;
        if (localFishData ==null){
            synchronized (FishData.class){
                localFishData = fishData;
                if (localFishData ==null){
                    fishData = localFishData = new FishData();
                }
            }
        }
        return localFishData;
    }

    public void clearData() {
        Stream.of(fishList,idSet,birthTimeTree.keySet(),birthTimeTree.values()).forEach(Collection::clear);
    }

}
