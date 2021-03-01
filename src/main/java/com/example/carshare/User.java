package com.example.carshare;

public class User {

    private String username;
    private String email;
    private String phone;
    private String isCarOwner;

    public User() {}

    public User(String username, String email, String phone, String isCarOwner) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.isCarOwner = isCarOwner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
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

    public String getIsCarOwner() { return isCarOwner; }

    public void setIsCarOwner(String isCarOwner) { this.isCarOwner = isCarOwner; }
}
