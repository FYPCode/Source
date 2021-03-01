package com.example.carshare;

public class Car {

    private String carId;
    private String imageLink;
    private String carModel;
    private String vehicleRegistrationPlate;
    private String description;
    private String isRentOut;

    public Car() {}

    public Car(String carId, String imageLink, String carModel, String vehicleRegistrationPlate, String description, String isRentOut) {
        this.carId = carId;
        this.imageLink = imageLink;
        this.carModel = carModel;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
        this.description = description;
        this.isRentOut = isRentOut;
    }

    public String getCarId() { return carId; }

    public void setCarId(String carId) { this.carId = carId; }

    public String getImageLink() { return imageLink; }

    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public String getCarModel() { return carModel; }

    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getVehicleRegistrationPlate() { return vehicleRegistrationPlate; }

    public void setVehicleRegistrationPlate(String vehicleRegistrationPlate) { this.vehicleRegistrationPlate = vehicleRegistrationPlate; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getIsRentOut() { return isRentOut; }

    public void setIsRentOut(String isRentOut) { this.isRentOut = isRentOut; }
}
