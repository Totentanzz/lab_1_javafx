package lab_1.fishApp.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class Habitat {

    private int goldenSpawnTime, guppySpawnTime;
    private int goldenLifeTime,guppyLifeTime;
    private short goldenSpawnChance, guppySpawnChance;
    private short goldenMaxVelocity, guppyMaxVelocity;
    private static ModelData modelData;

    public Habitat() {
        modelData = ModelData.getInstance();
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

    public ModelData getFishData() {
        return this.modelData;
    }

    public long getFishAmount(Class clazz){
        long fishAmount = -1;
        if (clazz==GoldenFish.class || clazz== GuppyFish.class){
            fishAmount= modelData.getFishList().stream().filter(obj -> obj.getClass()==clazz).count();
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
                modelData.getFishList().add(goldenFish);
                modelData.getIdSet().add(id);
                modelData.getBirthTimeTree().put(id, birthTime);
                createdFish = goldenFish;
            }
            else if (clazz==GuppyFish.class){
                GuppyFish guppyFish = new GuppyFish(x,y,xVelocity,yVelocity,id,birthTime);
                modelData.getFishList().add(guppyFish);
                modelData.getIdSet().add(id);
                modelData.getBirthTimeTree().put(id, birthTime);
                createdFish = guppyFish;
            }
        return createdFish;
    }

    public Config toConfig() {
        AtomicReference<Config> configRef = new AtomicReference<>(ConfigFactory.empty());
        Field[] fieldArray = this.getClass().getDeclaredFields();
        Arrays.stream(fieldArray).filter(field -> field.getName() != "modelData")
                .forEach(field -> {
                    try {
                        configRef.getAndSet(configRef.get().withValue(field.getName(),
                                ConfigValueFactory.fromAnyRef(field.get(this))));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        return configRef.get();
    }

}