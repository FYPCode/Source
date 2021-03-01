package com.example.carshare;

import java.util.ArrayList;

public class CarOwner extends User {

    private String drivingLicenseNo;
    private String expirationDate;
    private ArrayList<Car> carList;

    public CarOwner(){
        super();
    }

    public CarOwner(String drivingLicenseNo, String expirationDate, ArrayList<Car> carList) {
        this.drivingLicenseNo = drivingLicenseNo;
        this.expirationDate = expirationDate;
        this.carList = carList;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) { this.drivingLicenseNo = drivingLicenseNo; }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ArrayList<Car> getCarList() { return carList;}

    public void setCarList(ArrayList<Car> carList) { this.carList = carList; }

}
