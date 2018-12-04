package com.example.sharan.iotsmartchain.global;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class Check_SDK {
    private static String TAG = Check_SDK.class.getSimpleName();
    private Context context;

    public Check_SDK(Context context) {
        this.context = context;
    }

    public int getCurrentVersion() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Log.e(TAG, "currentApiVersion : " + currentApiVersion);
        if (currentApiVersion >= android.os.Build.VERSION_CODES.FROYO) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.GINGERBREAD) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.M) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.N) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.N_MR1) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.O) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.O_MR1) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else if (currentApiVersion >= Build.VERSION_CODES.P) {
            // Do something for froyo and above versions
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        } else {
            // do something for phones running an SDK before froyo
            Log.d(TAG, "currentApiVersion : " + currentApiVersion);
        }

        return currentApiVersion;
    }
}
