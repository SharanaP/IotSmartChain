package com.example.sharan.iotsmartchain.model;

import com.example.sharan.iotsmartchain.model.DataModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sharan on 20-03-2018.
 */

public class AuthResponse {
    @SerializedName("status")
    int mStatus;

    @SerializedName("data")
    DataModel mData;

    @SerializedName("message")
    String mMessage;

    public int getStatus() {
        return mStatus;
    }

    public DataModel getData() {
        return mData;
    }

    public String getMsg() {
        return mMessage;
    }
}
