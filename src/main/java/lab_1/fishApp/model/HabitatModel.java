package lab_1.fishApp.model;

import java.io.FileNotFoundException;
import java.util.*;


public class HabitatModel {

    private int goldenSpawnTime, guppySpawnTime;
    private int goldenLifeTime,guppyLifeTime;
    private short goldenSpawnChance, guppySpawnChance;
    private short goldenMaxVelocity, guppyMaxVelocity;
    private static FishData fishData;

    public HabitatModel() {
        fishData = FishData.getInstance();
        goldenSpawnTime = 3;
        guppySpawnTime = 5;
        goldenLifeTime = 15;
        guppyLifeTime = 25;
        goldenSpawnChance = 100;
        guppySpawnChance = 100;
        goldenMaxVelocity = 10;
        guppyMaxVelocity = 10;
    }

    public void setGoldenSpawnTime(int newTime){
        this.goldenSpawnTime=newTime;
    }

    public int getGoldenSpawnTime(){
        return this.goldenSpawnTime;
    }

    public void setGoldenLifeTime(int newTime) {
        this.goldenLifeTime = newTime;
    }

    public int getGoldenLifeTime(){
        return this.goldenLifeTime;
    }

    public void setGoldenSpawnChance(short newChance) {
        if (newChance>=0 && newChance<=100){
            this.goldenSpawnChance=newChance;
        }
    }
    public short getGoldenSpawnChance(){
        return this.goldenSpawnChance;
    }

    public void setGuppySpawnTime(int newTime){
        this.guppySpawnTime=newTime;
    }

    public int getGuppySpawnTime(){
        return this.guppySpawnTime;
    }

    public void setGuppyLifeTime(int newTime) {
        this.guppyLifeTime = newTime;
    }

    public int getGuppyLifeTime(){
        return this.guppyLifeTime;
    }

    public void setGuppySpawnChance(short newChance) {
        if (newChance>=0 && newChance<=100){
            this.guppySpawnChance=newChance;
        }
    }
    public short getGuppySpawnChance(){
        return this.guppySpawnChance;
    }

    public void setGoldenMaxVelocity(short newMaxVelocity) {this.goldenMaxVelocity=newMaxVelocity;}
    public short getGoldenMaxVelocity() {return this.goldenMaxVelocity;}
    public void setGuppyMaxVelocity(short newMaxVelocity) {this.guppyMaxVelocity=newMaxVelocity;}
    public short getGuppyMaxVelocity() {return this.guppyMaxVelocity;}

    public FishData getFishData() {
        return this.fishData;
    }

    public long getFishAmount(Class clazz){
        long fishAmount = -1;
        if (clazz==GoldenFish.class || clazz== GuppyFish.class){
            fishAmount=fishData.fishList.stream().filter(obj -> obj.getClass()==clazz).count();
        }
        return fishAmount;
    }

    public Fish createFish(double xBound, double yBound, int id, int birthTime, Class clazz) throws FileNotFoundException {
        Random randomGenerator = new Random();
        int x=randomGenerator.nextInt((int)xBound), xVelocity = 1+randomGenerator.nextInt(goldenMaxVelocity);
        int y=randomGenerator.nextInt((int)yBound), yVelocity = 1+randomGenerator.nextInt(guppyMaxVelocity);
        Fish createdFish = null;
            if (clazz==GoldenFish.class){
                GoldenFish goldenFish = new GoldenFish(x,y,xVelocity,yVelocity,id,birthTime);
                fishData.fishList.add(goldenFish);
                fishData.idSet.add(id);
                fishData.birthTimeTree.put(id, birthTime);
                createdFish = goldenFish;
            }
            else if (clazz==GuppyFish.class){
                GuppyFish guppyFish = new GuppyFish(x,y,xVelocity,yVelocity,id,birthTime);
                fishData.fishList.add(guppyFish);
                fishData.idSet.add(id);
                fishData.birthTimeTree.put(id, birthTime);
                createdFish = guppyFish;
            }
        return createdFish;
    }

}