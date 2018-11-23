package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class NotificationCountModel {

    @SerializedName("totalNotify")
    private String totalCount = "";

    @SerializedName("iot_device_sn")
    private String iotSensorSn ="";

    @SerializedName("label")
    private String label;

    @SerializedName("description")
    private String description;

    @SerializedName("notifications_count")
    private String notificationsCount = "";

    @SerializedName("_time")
    private long installedTimeStamp = -1;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getIotSensorSn() {
        return iotSensorSn;
    }

    public void setIotSensorSn(String iotSensorSn) {
        this.iotSensorSn = iotSensorSn;
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

    public String getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(String notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    public long getInstalledTimeStamp() {
        return installedTimeStamp;
    }

    public void setInstalledTimeStamp(long installedTimeStamp) {
        this.installedTimeStamp = installedTimeStamp;
    }
}
