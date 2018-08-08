package com.example.sharan.iotsmartchain.model;

import com.google.gson.annotations.SerializedName;

public class GetChatHistoryData {

    private String _id;
    private long mTimeStamp;
    private String mChannelName;
    private String mMessage;
    private String mAuthor;
    private String mSenderName;
    private boolean isGroup;
    private boolean isAttachment;
    private String attachmentURL;
    private long attachmentSize;
    private String attachmentName;

    @SerializedName("groupName")
    private String mGroupName;

    //new inputs
    private String mImgFilePath;

    public GetChatHistoryData() {
        _id = "";
        mTimeStamp = -1;
        mChannelName = "";
        mMessage = "";
        mAuthor = "";
        mSenderName = "";
        isGroup = false;
        mGroupName = "";
        mImgFilePath = null;
        isAttachment = false;
        attachmentURL = "";
        attachmentSize = -1;
        attachmentName = "";
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public void setSenderName(String mSenderName) {
        this.mSenderName = mSenderName;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isAttachment() {
        return isAttachment;
    }

    public void setAttachment(boolean attachment) {
        isAttachment = attachment;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public long getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public String getImgFilePath() {
        return mImgFilePath;
    }

    public void setImgFilePath(String mImgFilePath) {
        this.mImgFilePath = mImgFilePath;
    }
}
