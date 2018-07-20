package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class SpatialDataModel {

    @SerializedName("deviceId")
    private String _Id;

    @SerializedName("deviceType")
    private String type;

    @SerializedName("status")
    private String status;

    @SerializedName("timeStamp")
    private String timeStamp;

    @SerializedName("details")
    private String details;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("isSpatial")
    private String isSpatial;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String isSpatial() {
        return isSpatial;
    }

    public void setSpatial(String spatial) {
        isSpatial = spatial;
    }

    @Override
    public String toString() {
        return "SpatialDataModel{" +
                "_Id='" + _Id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", details='" + details + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", isSpatial=" + isSpatial +
                '}';
    }
}
