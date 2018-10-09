package com.example.sharan.iotsmartchain.IotDeviceConfigure;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;

import java.util.List;

public class WifiSecondDemo extends ListActivity {
    private static String TAG = WifiSecondDemo.class.getSimpleName();

    private WifiManager mainWifiObj;
    private WifiScanReceiver wifiReciever;
    private Toolbar toolbar;
    private RelativeLayout relativeLayoutMain;
    private TextView textViewOnOff;
    private ListView list;
    private Switch aSwitch;

    private String wifis[];

    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_demo);

        toolbar = (Toolbar)findViewById(R.id.toolbar_wifi);
        relativeLayoutMain =  (RelativeLayout)findViewById(R.id.relativeLayout_wifi);
        textViewOnOff = (TextView)findViewById(R.id.textView_wifi);
        aSwitch = (Switch)findViewById(R.id.switch_wifi);

        //init default listview
        list=getListView();

        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();

        // listening to single list item on click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String ssid = ((TextView) view).getText().toString();
                connectToWifi(ssid);
                Toast.makeText(WifiSecondDemo.this,"Wifi SSID : "+ssid, Toast.LENGTH_SHORT).show();

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Based switch show list of all wifi
                if(isChecked){
                    textViewOnOff.setText("On");
                    mainWifiObj.startScan();
                    list.setVisibility(View.VISIBLE);
                    Snackbar.make(relativeLayoutMain, "Turn On the list Of Wi-Fi networks", Snackbar.LENGTH_LONG).show();
                }else{
                    textViewOnOff.setText("Off");
                    list.setVisibility(View.INVISIBLE);
                    Snackbar.make(relativeLayoutMain, "Turn Off", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
            }

            for(ScanResult sr : wifiScanList){
                Log.e(TAG, ""+sr.toString());
            }

            String filtered[] = new String[wifiScanList.size()];
            int counter = 0;
            for (String eachWifi : wifis) {

                Log.e(TAG, ""+eachWifi.toString());

                String[] temp = eachWifi.split(",");

                filtered[counter] = temp[0].substring(5).trim();
                //+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();
                // 0->SSID, 2->Key Management 3-> Strength

                counter++;

            }

            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.row_item_wifi_connect,
                    R.id.label, filtered));

        }
    }

    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = mainWifiObj.addNetwork(wifiConfig);
        mainWifiObj.disconnect();
        mainWifiObj.enableNetwork(netId, true);
        mainWifiObj.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        mainWifiObj.addNetwork(conf);
    }

    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect_wifi);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);
        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}