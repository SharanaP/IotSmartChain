package com.example.sharan.iotsmartchain.model;

import java.io.Serializable;

public class NonSpatialModel implements Serializable {
    private String iotDeviceSerialNum;
    private String label;
    private double latitude;
    private double longitude;
    private String description;
    private String service;
    private String characteristic;
    private String message;
    private String status;
    private String timeStamp;
    private String address;

    public String getIotDeviceSerialNum() {
        return iotDeviceSerialNum;
    }

    public void setIotDeviceSerialNum(String iotDeviceSerialNum) {
        this.iotDeviceSerialNum = iotDeviceSerialNum;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "NonSpatialModel{" +
                "iotDeviceSerialNum='" + iotDeviceSerialNum + '\'' +
                ", label='" + label + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", service='" + service + '\'' +
                ", characteristic='" + characteristic + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
