package com.ssmptc.QrRegistry.DataBase;

public class CustomersModel {

    String name,phoneNo,currentDate,currentTime;

    private boolean expandable;


    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public CustomersModel(String name, String phoneNo, String currentDate, String currentTime) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.expandable = false;

    }

    public String getCurrentTime() { return currentTime; }

    public void setCurrentTime(String currentTime) { this.currentTime = currentTime; }

    public String getCurrentDate() { return currentDate; }

    public void setCurrentDate(String currentDate) { this.currentDate = currentDate; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
