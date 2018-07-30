package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class NotificationModuleData {

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("details")
    private String details;

    @SerializedName("unReadCount")
    private String unReadCount;

    @SerializedName("timeStamp")
    private String timeStamp;


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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "NotificationModuleData{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", details='" + details + '\'' +
                ", unReadCount='" + unReadCount + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
