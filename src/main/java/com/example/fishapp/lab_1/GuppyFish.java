package com.example.fishapp.lab_1;

import java.io.FileNotFoundException;

public class GuppyFish extends Fish implements IBehaviour {

    GuppyFish(double x, double y, String imagePath) throws FileNotFoundException {
        super(x,y,imagePath);
    }

}
