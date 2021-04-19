package com.example.carshare;

public class Brand {

    private String imageLink;
    private String brandName;

    public Brand() {}

    public Brand(String imageLink, String brandName) {
        this.imageLink = imageLink;
        this.brandName = brandName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
