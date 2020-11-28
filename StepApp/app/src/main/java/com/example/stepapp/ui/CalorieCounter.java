package com.example.stepapp.ui;

import com.example.stepapp.ui.home.HomeFragment;
import com.example.stepapp.ui.profile.ProfileFragment;

public class CalorieCounter {

    ProfileFragment profileFragment;

    private String weight = profileFragment.getWeightText();
    private String height = profileFragment.getHeightText();

    private int normalizeWeight(String weight){
        return Integer.parseInt(weight);
    }

    private int normalizeHeight(String Height){
        return Integer.parseInt(height);
    }



    public double caloriesBurnt(){
        return normalizeHeight(height) * normalizeWeight(weight) * HomeFragment.stepsCompleted * Math.pow(3.154,-6);

    }



}
