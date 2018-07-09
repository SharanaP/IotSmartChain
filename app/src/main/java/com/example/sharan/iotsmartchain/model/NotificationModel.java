package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    @SerializedName("messageId")
    private String messageId;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("message")
    private String message;

    @SerializedName("imageURL")
    private String imageURL;

    @SerializedName("timeStamp")
    private String timeStamp;

    @SerializedName("isRead")
    private boolean isRead;

    private boolean isStatus;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "messageId='" + messageId + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", message='" + message + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", isRead=" + isRead +
                ", isStatus=" + isStatus +
                '}';
    }
}

