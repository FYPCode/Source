package com.example.carshare;

import java.util.ArrayList;

public class CarOwner extends User {

    private String balance;
    private ArrayList<Car> carList;

    public CarOwner() {
        super();
    }

    public CarOwner(String balance, ArrayList<Car> carList) {
        this.balance = balance;
        this.carList = carList;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public ArrayList<Car> getCarList() {
        return carList;
    }

    public void setCarList(ArrayList<Car> carList) {
        this.carList = carList;
    }

}
