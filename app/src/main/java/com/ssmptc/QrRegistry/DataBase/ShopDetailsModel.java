package com.ssmptc.QrRegistry.DataBase;

public class ShopDetailsModel {

    String key,name,phoneNumber;

    public ShopDetailsModel() {
    }

    public ShopDetailsModel(String name, String phoneNumber, String key) {
        this.key = key;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
