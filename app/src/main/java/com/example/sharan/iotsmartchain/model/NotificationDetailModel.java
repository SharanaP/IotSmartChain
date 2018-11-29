package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class NotificationDetailModel implements Serializable {
    @SerializedName("notify_id")
    private String _id;
    @SerializedName("gateway_sn")
    private String gatewaySn;
    @SerializedName("iot_device_sn")
    private String iotDeviceSn;
    @SerializedName("label")
    private String label;
    @SerializedName("description")
    private String description;
    @SerializedName("is_read")
    private boolean isRead;
    @SerializedName("notify_type")
    private int notifyType;
    @SerializedName("details")
    private String details;
    @SerializedName("timestamp")
    private long timeStamp;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;

    private String imageURL;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGatewaySn() {
        return gatewaySn;
    }

    public void setGatewaySn(String gatewaySn) {
        this.gatewaySn = gatewaySn;
    }

    public String getIotDeviceSn() {
        return iotDeviceSn;
    }

    public void setIotDeviceSn(String iotDeviceSn) {
        this.iotDeviceSn = iotDeviceSn;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "NotificationDetailModel{" +
                "_id='" + _id + '\'' +
                ", gatewaySn='" + gatewaySn + '\'' +
                ", iotDeviceSn='" + iotDeviceSn + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", isRead=" + isRead +
                ", notifyType=" + notifyType +
                ", details='" + details + '\'' +
                ", timeStamp=" + timeStamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}
