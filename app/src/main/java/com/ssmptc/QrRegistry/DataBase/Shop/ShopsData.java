package com.ssmptc.QrRegistry.DataBase.Shop;

public class ShopsData {
    String id,shopName,category,location,ownerName,phoneNumber,password,email,licenseNumber,description,workingTime,workingDays;

    public ShopsData() {
    }

    public ShopsData(String id, String shopName, String category, String location, String ownerName, String phoneNumber, String password, String email, String licenseNumber, String description, String workingTime, String workingDays) {
        this.id = id;
        this.shopName = shopName;
        this.category = category;
        this.location = location;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.licenseNumber = licenseNumber;
        this.description = description;
        this.workingTime = workingTime;
        this.workingDays = workingDays;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(String workingTime) {
        this.workingTime = workingTime;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }
}
