package com.example.sharan.iotsmartchain.model;

public class AnalyticsDataModel {
    private String _Id;
    private String type;
    private String temperature;
    private String numOfDoorOpen;
    private String humidity;
    private String timeStamp;
    private String details;

    public String get_Id() {
        return _Id;
    }

    public void set_Id(String _Id) {
        this._Id = _Id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getNumOfDoorOpen() {
        return numOfDoorOpen;
    }

    public void setNumOfDoorOpen(String numOfDoorOpen) {
        this.numOfDoorOpen = numOfDoorOpen;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


}
