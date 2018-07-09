package com.example.sharan.iotsmartchain.global;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sharan on 19-03-2018.
 */
public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    @SuppressLint("MissingPermission")
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }

        // Check for network connections
        if (cm.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                cm.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                cm.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return TYPE_WIFI;
        } else if (
                cm.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        cm.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return TYPE_NOT_CONNECTED;
        }

        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

}
