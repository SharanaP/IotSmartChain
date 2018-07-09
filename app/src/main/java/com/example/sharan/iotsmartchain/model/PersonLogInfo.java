package com.example.sharan.iotsmartchain.model;

/**
 * Created by Sharan on 19-03-2018.
 */

public class PersonLogInfo {
    String emailId;
    String tokenId;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "PersonLogInfo{" +
                "emailId='" + emailId + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
