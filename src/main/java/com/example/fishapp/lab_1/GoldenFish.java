package com.example.fishapp.lab_1;

import java.io.FileNotFoundException;

public class GoldenFish extends Fish implements IBehaviour {

    GoldenFish(double x,double y,String imagePath) throws FileNotFoundException {
        super(x,y,imagePath);
    }

}
