package com.example.sharan.iotsmartchain.model;

public class BatteryModel {

    private String deviceId;
    private String deviceType;
    private String batteryValue;
    private String status;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(String batteryValue) {
        this.batteryValue = batteryValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BatteryModel{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", batteryValue='" + batteryValue + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
