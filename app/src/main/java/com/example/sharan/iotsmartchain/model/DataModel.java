package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sharan on 20-03-2018.
 *
 */

/*{"statusCode":"200","body":{"message":{"isEmailExisted":-1,"isPhoneExisted":-1},
"email":"sharan.pallada@gmail.com","userId":"d7aa4080-a53c-11e8-b852-237d818e0c0c","status":"true"},
"headers":{"Content-Type":"application/json"}}*/


//OLD {"tokenid":"95d4f181-5499-494b-a7a6-c4126753085f","message":"Successfully Registered","email":"sharan@gmail.com"}

public class DataModel {
    @SerializedName("email")
    String mEmailId;

    @SerializedName("userId")
    String mToken;

    @SerializedName("status")
    boolean mStatus;

    @SerializedName("message")
    String mMessage;

    @SerializedName("statusCode")
    String mStatusCode;

    @SerializedName("isEmailExisted")
    String emailIsExisted;

    @SerializedName("isPhoneExisted")
    String phoneIsExisted;

    @SerializedName("name")
    String mName;

    @SerializedName("phone")
    String mPhone;

    public String getName() {
        return mName;
    }

    public String getPhone() {
        return mPhone;
    }

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

    public String getStatusCode() {
        return mStatusCode;
    }

    public String getEmailIsExisted() {
        return emailIsExisted;
    }

    public String getPhoneIsExisted() {
        return phoneIsExisted;
    }
}
