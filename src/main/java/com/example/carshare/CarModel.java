package com.example.carshare;

public class CarModel extends Brand {

    private String carModelName;
    private String maxHorsepower;
    private String transmission;
    private String capacity;

    public CarModel() { super(); }

    public CarModel(String carModelName, String maxHorsepower, String transmission, String capacity) {
        this.carModelName = carModelName;
        this.maxHorsepower = maxHorsepower;
        this.transmission = transmission;
        this.capacity = capacity;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
    }

    public String getMaxHorsepower() {
        return maxHorsepower;
    }

    public void setMaxHorsepower(String maxHorsepower) {
        this.maxHorsepower = maxHorsepower;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
