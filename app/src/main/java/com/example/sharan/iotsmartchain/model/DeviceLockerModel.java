package com.example.sharan.iotsmartchain.model;

public class DeviceLockerModel {
    private String deviceId;
    private String deviceType;
    private String message;
    private boolean isMasterLocked;
    private boolean isLocked;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMasterLocked() {
        return isMasterLocked;
    }

    public void setMasterLocked(boolean masterLocked) {
        isMasterLocked = masterLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeviceLockerModel{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", message='" + message + '\'' +
                ", isMasterLocked=" + isMasterLocked +
                ", isLocked=" + isLocked +
                ", status='" + status + '\'' +
                '}';
    }
}
