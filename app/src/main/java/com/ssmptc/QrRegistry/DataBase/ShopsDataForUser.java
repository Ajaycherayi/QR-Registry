package com.ssmptc.QrRegistry.DataBase;

public class ShopsDataForUser {
    String id,shopId,name,category,ownerName,location,phoneNumber,email,days,time,description,images;

    public ShopsDataForUser() {
    }

    public ShopsDataForUser(String id, String shopId, String name, String category, String ownerName, String location, String phoneNumber, String email, String days, String time, String description, String images) {
        this.id = id;
        this.shopId = shopId;
        this.name = name;
        this.category = category;
        this.ownerName = ownerName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.days = days;
        this.time = time;
        this.description = description;
        this.images = images;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
