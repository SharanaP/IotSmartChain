package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sharan on 20-03-2018.
 *
 */



//{"tokenid":"95d4f181-5499-494b-a7a6-c4126753085f","message":"Successfully Registered","email":"sharan@gmail.com"}

public class DataModel {
    @SerializedName("email")
    String mEmailId;

    @SerializedName("tokenid")
    String mToken;

    @SerializedName("appstatus")
    boolean mStatus;

    @SerializedName("message")
    String mMessage;

    public String getEmailId() {
        return mEmailId;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
