package lab_1.fishApp.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;
import java.util.stream.Stream;

public class ModelData {

    private static volatile ModelData modelData;
    private LinkedList<Fish> fishList;
    private HashSet<Integer> idSet;
    private TreeMap<Integer,Integer> birthTimeTree;
    private Config configFile;
    private LinkedList<String> clientsNames;

    private ModelData(){
        fishList = new LinkedList<>();
        idSet = new HashSet<>();
        birthTimeTree = new TreeMap<>();
        configFile = ConfigFactory.empty();
        clientsNames = new LinkedList<>();
    }
    public static ModelData getInstance() {
        ModelData localModelData = modelData;
        if (localModelData ==null){
            synchronized (ModelData.class){
                localModelData = modelData;
                if (localModelData ==null){
                    modelData = localModelData = new ModelData();
                }
            }
        }
        return localModelData;
    }

    public void clearCollections() {
        Stream.of(fishList,idSet,birthTimeTree.keySet(),birthTimeTree.values()).forEach(Collection::clear);
    }

    public void clearConfig() {
        configFile = ConfigFactory.empty();
    }

    public void clearAll() {
        this.clearCollections();
        this.clearConfig();
    }

    public void setConfig(Config newConfig) {
        this.configFile = newConfig.withFallback(this.configFile);
    }

    public Config getConfig() {
        return this.configFile;
    }

    public LinkedList<Fish> getFishList() {
        return this.fishList;
    }

    public HashSet<Integer> getIdSet() {
        return this.idSet;
    }

    public TreeMap<Integer,Integer> getBirthTimeTree() {
        return this.birthTimeTree;
    }

    public LinkedList<String> getClientsNames() {
        return clientsNames;
    }

    public void setClientsNames(LinkedList<String> newClientNames) {
        this.clientsNames.clear();
        this.clientsNames.addAll(newClientNames);
    }

}
