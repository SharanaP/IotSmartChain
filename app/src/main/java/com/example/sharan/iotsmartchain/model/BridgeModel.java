package com.example.sharan.iotsmartchain.model;

import java.io.Serializable;

public class BridgeModel implements Serializable {

    private String _Id;
    private String gatewayUid;
    private String gatewayLabel;
    private double latitude;
    private double longitude;
    private String address;
    private String status;
    private boolean isInstalled;
    private boolean isConfigure;
    private String service;
    private String characteristic;
    private long timeStamp;

    public String get_Id() {
        return _Id;
    }

    public void set_Id(String _Id) {
        this._Id = _Id;
    }

    public String getGatewayUid() {
        return gatewayUid;
    }

    public void setGatewayUid(String gatewayUid) {
        this.gatewayUid = gatewayUid;
    }

    public String getGatewayLabel() {
        return gatewayLabel;
    }

    public void setGatewayLabel(String gatewayLabel) {
        this.gatewayLabel = gatewayLabel;
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

    public boolean isConfigure() {
        return isConfigure;
    }

    public void setConfigure(boolean configure) {
        isConfigure = configure;
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

    @Override
    public String toString() {
        return "BridgeModel{" +
                "_Id='" + _Id + '\'' +
                ", gatewayUid='" + gatewayUid + '\'' +
                ", gatewayLabel='" + gatewayLabel + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", isInstalled=" + isInstalled +
                ", isConfigure=" + isConfigure +
                ", service='" + service + '\'' +
                ", characteristic='" + characteristic + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
