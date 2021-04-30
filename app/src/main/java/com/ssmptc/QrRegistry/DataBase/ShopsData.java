package com.ssmptc.QrRegistry.DataBase;

public class ShopsData {
    String shopName,category,location,ownerName,phoneNumber,password;

    public ShopsData(String phoneNumber,String shopName,String location,String category,String ownerName,String password) {

        this.phoneNumber = phoneNumber;
        this.shopName = shopName;
        this.location = location;
        this.category = category;
        this.ownerName = ownerName;
        this.password = password;
        /*this.email = email;
        this.registeredTime = registeredTime;
        this.openTime = openTime;
        this.openDays = openDays;
        this.description = description;*/
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNo) {
        this.phoneNumber = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
