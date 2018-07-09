package com.example.sharan.iotsmartchain.model;

/**
 * Created by Sharan on 19-03-2018.
 */

public class DeviceInfo {
     String mDeviceId;
     String mBatteryStatus;

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public String getBatteryStatus() {
        return mBatteryStatus;
    }

    public void setBatteryStatus(String mBatteryStatus) {
        this.mBatteryStatus = mBatteryStatus;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "mDeviceId='" + mDeviceId + '\'' +
                "mBatteryStatus='" + mBatteryStatus+
                '}';
    }
}
