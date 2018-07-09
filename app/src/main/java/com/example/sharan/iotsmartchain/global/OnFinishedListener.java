package com.example.sharan.iotsmartchain.global;

/**
 * Created by Sharan on 19-03-2018.
 */

public interface OnFinishedListener {
    public void onSuccess(API_QUERY_TYPE api_query_type);
    public void onFailure(API_QUERY_TYPE api_query_type);
}
