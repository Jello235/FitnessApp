package com.example.fitnessapp.model;

public class FitnessChoice {
    private String description;
    private String part;
    private String intensity;

    private FitnessChoice(){}

    private FitnessChoice(String description) {
        this.description = description;
        this.part = part;
        this.intensity = intensity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}
