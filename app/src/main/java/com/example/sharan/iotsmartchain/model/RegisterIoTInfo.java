package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class RegisterIoTInfo {

    private String sensorUID;

    @SerializedName("name")
    private String sensorName;

    @SerializedName("details")
    private String sensorDetails;

    @SerializedName("topic")
    private String sensorTopic;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("status")
    private String sensorStatus;

    private String timeStamp;

    public String getSensorUID() {
        return sensorUID;
    }

    public void setSensorUID(String sensorUID) {
        this.sensorUID = sensorUID;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorDetails() {
        return sensorDetails;
    }

    public void setSensorDetails(String sensorDetails) {
        this.sensorDetails = sensorDetails;
    }

    public String getSensorTopic() {
        return sensorTopic;
    }

    public void setSensorTopic(String sensorTopic) {
        this.sensorTopic = sensorTopic;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSensorStatus() {
        return sensorStatus;
    }

    public void setSensorStatus(String sensorStatus) {
        this.sensorStatus = sensorStatus;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "RegisterIoTInfo{" +
                "sensorUID='" + sensorUID + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", sensorDetails='" + sensorDetails + '\'' +
                ", sensorTopic='" + sensorTopic + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", sensorStatus='" + sensorStatus + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
