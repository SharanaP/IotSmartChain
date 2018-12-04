package com.example.sharan.iotsmartchain.WifiNetwork;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class ConnectionManager {
    private static final String WPA = "WPA";
    private static final String WEP = "WEP";
    private static final String OPEN = "Open";
    private final static String TAG = "WiFiConnector";
    private Context context;
    private Activity activity;
    private WifiManager wifiManager = null;

    public ConnectionManager(Context context, WifiManager wifiManager) {
        this.context = context;
        this.activity = (Activity) context;
        this.wifiManager = wifiManager;

        if (wifiManager == null) {
            //init wifi service
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        Log.d(TAG, "ConnectionManager : " + wifiManager.toString());
    }

    public void enableWifi() {
        if (wifiManager == null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.e(TAG, "wifiManager.isWifiEnabled() : " + wifiManager.isWifiEnabled());

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(context, "WIFI Turned On", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "wifiManager setWifiEnabled is true ");

        } else {
            Log.e(TAG, "wifiManager is wifi is already enabled  : " + wifiManager.isWifiEnabled());
        }
    }

    public boolean CheckWifiStatus() {
        if (wifiManager == null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.e(TAG, "wifiManager.isWifiEnabled() : " + wifiManager.isWifiEnabled());
        return wifiManager.isWifiEnabled();
    }

    public boolean disconnectWifi() {
        if (wifiManager == null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.e(TAG, "wifiManager. disconnectWifi : " + wifiManager.disconnect());
        return wifiManager.disconnect();
    }

    public void disableWifi() {
        if (wifiManager == null)
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            Toast.makeText(context, "WIFI Turned Off", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "wifiManager setWifiEnabled is true ");

        } else {
            wifiManager.setWifiEnabled(false);
            Log.e(TAG, "wifiManager is wifi is turn off : " + wifiManager.isWifiEnabled());
        }

        Log.e(TAG, "wifiManager.disableWifi() : " + wifiManager.isWifiEnabled());

    }

    public int requestWIFIConnection(String networkSSID, String networkPass) {
        try {
            if (wifiManager == null)
                wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            //Check ssid exists
            boolean isSsidExist = scanWifi(wifiManager, networkSSID);
            if (isSsidExist) {
                Log.e(TAG, "is SSID exist : " + isSsidExist);
            } else {
                Log.e(TAG, "is SSID not exist : " + isSsidExist);
                return Constants.SSID_NOT_FOUND;
            }

            //If SSID is exist then goto wifi conf and connect
            if (isSsidExist) {
                Log.e(TAG, "Scan wifi is true");

                if (getCurrentSSID(wifiManager) != null && getCurrentSSID(wifiManager).equals("\"" + networkSSID + "\"")) {
                    // new ShowToast(context, "Already Connected With " + networkSSID);
                    Toast.makeText(context, "Already Connected With " + networkSSID, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Already wifi is connected");
                    return Constants.ALREADY_CONNECTED;
                }

                //Security type detection
                String SECURE_TYPE = checkSecurity(wifiManager, networkSSID);
                if (SECURE_TYPE == null) {
                    Toast.makeText(context, "Unable to find Security type for " + networkSSID, Toast.LENGTH_SHORT).show();
                    return Constants.UNABLE_TO_FIND_SECURITY_TYPE;
                }

                if (SECURE_TYPE.equals(WPA)) {
                    Log.e(TAG, "SECURE_TYPE == WPA :: is true");
                    return WPA(networkSSID, networkPass, wifiManager);
                } else if (SECURE_TYPE.equals(WEP)) {
                    Log.e(TAG, "SECURE_TYPE == WEP :: is true");
                    WEP(networkSSID, networkPass);
                } else {
                    Log.e(TAG, " SECURE_TYPE : " + SECURE_TYPE);
                    return OPEN(wifiManager, networkSSID);
                }

                return Constants.CONNECTION_REQUESTED;

            } else {
                return Constants.SSID_NOT_FOUND;
            }
        } catch (Exception e) {
            //  new ShowToast(context, "Error Connecting WIFI " + e);
            Toast.makeText(context, "Error Connecting WIFI " + e, Toast.LENGTH_SHORT).show();
        }
        return Constants.SSID_NOT_FOUND;
    }

    private int WPA(String networkSSID, String networkPass, WifiManager wifiManager) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + networkSSID + "\"";
        wc.preSharedKey = "\"" + networkPass + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        int networkId = wifiManager.addNetwork(wc);
        if (networkId != -1) ;
        else {
            Log.e(TAG, "Already wifi network connected");
            wifiManager.disconnect();
        }

        Log.e(TAG, "networkId assigned while adding network is " + networkId);
        //if id -1 mean "Couldn't add network with SSID"

        boolean isConnected = wifiManager.disconnect();
        Log.e(TAG, "wifi is disconnected : " + isConnected);

        if (networkId != -1) {
            boolean isEnableNet = wifiManager.enableNetwork(networkId, true);
            Log.e(TAG, "WPA : isEnableNet : " + isEnableNet);
            boolean isReconnect = wifiManager.reconnect();
            Log.e(TAG, "WPA : isReconnect : " + isReconnect);
            return Constants.CONNECTION_REQUESTED;
        } else {
            Log.e(TAG, "Couldn't add network with SSID");
            return Constants.ALREADY_CONNECTED;
        }
    }

    private void WEP(String networkSSID, String networkPass) {
        Log.e(TAG, "WEP : nthg : ");
    }

    private int OPEN(WifiManager wifiManager, String networkSSID) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + networkSSID + "\"";
        wc.hiddenSSID = true;
        wc.priority = 0xBADBAD;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int networkId = wifiManager.addNetwork(wc);
        Log.e(TAG, "OPEN : isEnableNet : " + networkId);

        boolean isConnected = wifiManager.disconnect();
        Log.e(TAG, "OPEN : wifi is connected " + isConnected);

        if (networkId != -1) {
            boolean isEnableNet = wifiManager.enableNetwork(networkId, true);
            Log.e(TAG, "OPEN : isEnableNet : " + isEnableNet);
            boolean isReconnect = wifiManager.reconnect();
            Log.e(TAG, "OPEN : isReconnect : " + isReconnect);
            return Constants.CONNECTION_REQUESTED;
        } else {
            Log.e(TAG, "OPEN : Couldn't add network with SSID");
            return Constants.COULDNOT_ADD_NETWORK;
        }
    }

    public List<ScanResult> getScannedWifiList() {
        List<ScanResult> scanList = new LinkedList<>();
        try {
            if (wifiManager == null)
                wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            scanList = wifiManager.getScanResults();
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error Connecting WIFI " + ex, Toast.LENGTH_SHORT).show();
        }
        return scanList;
    }

    boolean scanWifi(WifiManager wifiManager, String networkSSID) {
        Log.e(TAG, "scanWifi starts & networkSSID : " + networkSSID);
        List<ScanResult> scanList = wifiManager.getScanResults();
        for (ScanResult i : scanList) {
            if (i.SSID != null) {
                Log.e(TAG, "SSID: " + i.SSID);
            }

            if (i.SSID != null && i.SSID.equals(networkSSID)) {
                Log.e(TAG, "Found SSID: " + i.SSID);
                return true;
            }
        }
        Log.e(TAG, "Scan wifi SSID " + networkSSID + " not found");
        Toast.makeText(context, "SSID " + networkSSID + " Not Found", Toast.LENGTH_SHORT).show();

        return false;
    }

    public void newWifiConnection(String networkSSID, String networkPass, WifiManager wifiManager) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + networkSSID + "\"";
        wc.preSharedKey = "\"" + networkPass + "\"";

        int netId = wifiManager.addNetwork(wc);
        Log.e(TAG, "NETWORK ID :: " + netId);
        wifiManager.disconnect();
        boolean isEnableNet = wifiManager.enableNetwork(netId, true);

        Log.e(TAG, "WPA : isReconnect : " + isEnableNet);
        // boolean status = wifiManager.reconnect();
        //  Log.e(TAG, "WIFI reconnect status  :: "+status);

    }

    public String getCurrentSSID(WifiManager wifiManager) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    private String checkSecurity(WifiManager wifiManager, String ssid) {
        List<ScanResult> networkList = wifiManager.getScanResults();
        for (ScanResult network : networkList) {
            if (network.SSID.equals(ssid)) {
                String Capabilities = network.capabilities;
                if (Capabilities.contains("WPA")) {
                    return WPA;
                } else if (Capabilities.contains("WEP")) {
                    return WEP;
                } else {
                    return OPEN;
                }

            }
        }
        return null;
    }

}
