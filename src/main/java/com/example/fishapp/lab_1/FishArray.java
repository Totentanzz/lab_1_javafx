package com.example.fishapp.lab_1;

import java.util.ArrayList;

public class FishArray {

    private FishArray(){
    }
    public static ArrayList<Fish> getInstance(){
        return fishArrayList;
    }
    private static ArrayList<Fish> fishArrayList = new ArrayList<>();
}
