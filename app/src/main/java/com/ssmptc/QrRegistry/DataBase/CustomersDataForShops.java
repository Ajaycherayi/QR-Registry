package com.ssmptc.QrRegistry.DataBase;

public class CustomersDataForShops {
    String name,email,phoneNo,currentDate,currentTime;


    public CustomersDataForShops(String name, String email, String phoneNo, String currentDate, String currentTime) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

}
