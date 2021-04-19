package com.example.carshare;

public class User {

    private String username;
    private String email;
    private String phone;
    private String drivingLicenseNo;
    private String expirationDate;
    private String isCarOwner;

    public User() {
    }

    public User(String username, String email, String phone, String drivingLicenseNo, String expirationDate, String isCarOwner) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.drivingLicenseNo = drivingLicenseNo;
        this.expirationDate = expirationDate;
        this.isCarOwner = isCarOwner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getIsCarOwner() {
        return isCarOwner;
    }

    public void setIsCarOwner(String isCarOwner) {
        this.isCarOwner = isCarOwner;
    }
}
