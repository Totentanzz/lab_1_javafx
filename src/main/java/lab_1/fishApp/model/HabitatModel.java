package lab_1.fishApp.model;

import java.io.FileNotFoundException;
import java.util.*;


public class HabitatModel {

    private static final String goldenImagePath, guppyImagePath;
    private int goldenSpawnTime, guppySpawnTime;
    private short goldenSpawnChance, guppySpawnChance;
    private static ArrayList<Fish> fishArrayList;

    public HabitatModel() {
        goldenSpawnTime = 3;
        guppySpawnTime = 5;
        goldenSpawnChance = 100;
        guppySpawnChance = 100;
    }

    static{
        goldenImagePath = "src/main/resources/lab_1/fishApp/gifs/clown-pepe.gif";
        guppyImagePath = "src/main/resources/lab_1/fishApp/gifs/dance-pepe.gif";
        fishArrayList = FishArray.getInstance().getFishArrayList();
    }

    public void setGoldenSpawnTime(int newTime){
        goldenSpawnTime=newTime;
    }
    public int getGoldenSpawnTime(){
        return goldenSpawnTime;
    }
    public void setGuppySpawnTime(int newTime){
        guppySpawnTime=newTime;
    }
    public int getGuppySpawnTime(){
        return guppySpawnTime;
    }

    public void setGoldenSpawnChance(short newChance) {
        if (newChance>=0 && newChance<=100){
            goldenSpawnChance=newChance;
        }
    }
    public short getGoldenSpawnChance(){
        return goldenSpawnChance;
    }
    public void setGuppySpawnChance(short newChance) {
        if (newChance>=0 && newChance<=100){
            guppySpawnChance=newChance;
        }
    }
    public short getGuppySpawnChance(){
        return guppySpawnChance;
    }

    public ArrayList<Fish> getFishArrayList(){
        return fishArrayList;
    }

    public void addFishToList(Fish fish){
        fishArrayList.add(fish);
    }

    public void clearFishList(){
        fishArrayList.clear();
    }

    public long getFishAmount(Class clazz){
        long fishAmount = -1;
        if (clazz==GoldenFish.class || clazz== GuppyFish.class){
            fishAmount=fishArrayList.stream().filter(obj -> obj.getClass()==clazz).count();
        }
        return fishAmount;
    }

    public Fish createFish(double xBound, double yBound,Class clazz) throws FileNotFoundException {
        Random randomGenerator = new Random();
        int x=randomGenerator.nextInt((int)xBound);
        int y=randomGenerator.nextInt((int)yBound);
        Fish createdFish = null;
        if (clazz==GoldenFish.class){
            GoldenFish goldenFish = new GoldenFish(x,y,goldenImagePath);
            addFishToList(goldenFish);
            createdFish = goldenFish;
        }
        else if (clazz==GuppyFish.class){
            GuppyFish guppyFish = new GuppyFish(x,y,guppyImagePath);
            addFishToList(guppyFish);
            createdFish = guppyFish;
        }
        return createdFish;
    }

}