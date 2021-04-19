package com.example.carshare;

public class Car extends CarModel {

    private String imageLink;
    private String pricePerDay;
    private String ownedBy;
    private String vehiclePlate;
    private String color;
    private String description;
    private String isRentOut;
    private String rentedBy;

    public Car() {
        super();
    }

    public Car(String imageLink, String pricePerDay, String ownedBy, String vehiclePlate, String color, String description, String isRentOut) {
        this.imageLink = imageLink;
        this.pricePerDay = pricePerDay;
        this.ownedBy = ownedBy;
        this.vehiclePlate = vehiclePlate;
        this.color = color;
        this.description = description;
        this.isRentOut = isRentOut;
    }

    @Override
    public String getImageLink() {
        return imageLink;
    }

    @Override
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(String pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsRentOut() {
        return isRentOut;
    }

    public void setIsRentOut(String isRentOut) {
        this.isRentOut = isRentOut;
    }

    public String getRentedBy() {
        return rentedBy;
    }

    public void setRentedBy(String rentedBy) {
        this.rentedBy = rentedBy;
    }
}
