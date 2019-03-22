package com.example.sharan.iotsmartchain.model;

import java.io.Serializable;

public class EndNodeModel implements Serializable {
    private String _Id;
    private String bridgeUid;
    private String endNodeUid;
    private String label;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private String status;
    private boolean isInstalled;
    private long timeStamp;
    private String service;
    private String characteristic;
    private String message;

    public String get_Id() {
        return _Id;
    }

    public void set_Id(String _Id) {
        this._Id = _Id;
    }

    public String getBridgeUid() {
        return bridgeUid;
    }

    public void setBridgeUid(String bridgeUid) {
        this.bridgeUid = bridgeUid;
    }

    public String getEndNodeUid() {
        return endNodeUid;
    }

    public void setEndNodeUid(String endNodeUid) {
        this.endNodeUid = endNodeUid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    @Override
    public String toString() {
        return "EndNodeModel{" +
                "_Id='" + _Id + '\'' +
                ", bridgeUid='" + bridgeUid + '\'' +
                ", endNodeUid='" + endNodeUid + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", isInstalled=" + isInstalled +
                ", timeStamp=" + timeStamp +
                ", service='" + service + '\'' +
                ", characteristic='" + characteristic + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
