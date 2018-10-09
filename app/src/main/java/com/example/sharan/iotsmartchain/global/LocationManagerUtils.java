package com.example.sharan.iotsmartchain.global;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManagerUtils extends BaseActivity implements LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static String TAG = LocationManagerUtils.class.getSimpleName();
    //gps and network is enabled or not
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private boolean locationPermissionGranted = false;
    private Context context;
    private Activity activity;
    //dialog
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private boolean isLocationCheck = false;

    //location
    private LocationManager locationManager = null;
    private Criteria criteria = null;
    private String mProvider;
    private double latitude;
    private double longitude;
    private Geocoder geocoder;
    private List<Address> addresses;

    public LocationManagerUtils(Context context) {
        this.context = context;
        this.activity = (Activity) context;
        //init LocationManager, Criteria and Provider
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        mProvider = locationManager.getBestProvider(criteria, false);
    }

    //initialize location manager
    public boolean initLocationManager() {
        boolean isFlag = false;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isFlag = true;
        }

        if (criteria == null) {
            criteria = new Criteria();
            isFlag = true;
        }

        if (mProvider == null) {
            mProvider = locationManager.getBestProvider(criteria, false);
            isFlag = true;
        }
        return isFlag;
    }

    //check location permission
    public boolean checkPermissionLM() {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting for location permission");
            //request permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            isLocationCheck = false;
        } else if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Permission already granted");
            isLocationCheck = true;

            //manually turn on location
            showLocationSettingDialog();
        }
        return isLocationCheck;
    }

    //Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult : requestCode " + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, "permission granted : location  " + permissions[0]);
                    isLocationCheck = true;
                    locationPermissionGranted = true;
                    //check location
                    showLocationSettingDialog();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permission denied : location" + permissions[0]);
                    isLocationCheck = false;
                    locationPermissionGranted = false;
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    //check location is turn on or not
    private void CheckGpsNetwork() {
        if (locationManager == null)
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        Log.e(TAG, "LOCATION MANAGER START SERVICE ");
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "gps_enabled : " + gps_enabled);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d(TAG, "network_enabled : " + network_enabled);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //show dialog : To turn on location
    public void showLocationSettingDialog() {
        if (locationManager == null)
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        Log.e(TAG, "LOCATION MANAGER START SERVICE ");
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "gps_enabled : " + gps_enabled);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d(TAG, "network_enabled : " + network_enabled);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
//                            Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            context.startActivity(myIntent);

                            activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 222);

                            //TODO get gps
                            getLocationLatLng();

                        }
                    });
            dialog.setNegativeButton(context.getString(R.string.dialog_cancel_button),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            paramDialogInterface.dismiss();
                        }
                    });
            dialog.show();
        } else {
            Log.e(TAG, "Location already turn on");
            //TODO get gps
            getLocationLatLng();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222) {
            Log.d(TAG, " RESULT location enabled");
        }
    }

    public void getLocationLatLng() {
        Log.e(TAG, "Check init location manager, criteria and Provider");
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (criteria == null) criteria = new Criteria();
        if (mProvider == null) mProvider = locationManager.getBestProvider(criteria, false);

        if (mProvider != null && !mProvider.equals("")) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "checkSelfPermission : before ocationManager.getLastKnownLocation ");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(mProvider);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0, 0, this);
            } else {
                //TODO IllegalArgumentException issue
                try {
                    locationManager.requestLocationUpdates(mProvider, 10000, 1, this);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(context, "No Location Provider Found Check Your Code",
                        Toast.LENGTH_SHORT).show();

            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "checkSelfPermission after once again LocationManager.getLastKnownLocation");
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (latitude != -1) setLatitude(latitude);
                if (longitude != -1) setLongitude(longitude);

                mProvider = "Latitude : " + latitude + " Longitude : " + longitude;
                Log.e(TAG, mProvider);
                // textViewResult.setText(mProvider);

                //init GeoCoder
                geocoder = new Geocoder(context, Locale.getDefault());

                Log.e("latitude", "inside latitude--" + latitude);
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0) {
                    setAddresses(addresses);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    //  textViewResult.setText("Latitude : "+latitude+"\nLongitude : "+longitude+"\n"+address + " " + city + " " + country);
                }
            } else {

            }
        } else {
            Log.e(TAG, "Provider is null");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            if (latitude != -1) setLatitude(latitude);
            if (longitude != -1) setLongitude(longitude);
            mProvider = "Latitude : " + latitude + " Longitude : " + longitude;
            Log.e(TAG, mProvider);
            //  textViewResult.setText(mProvider);

            //init GeoCoder
            geocoder = new Geocoder(context, Locale.getDefault());

            Log.e("latitude", "inside latitude--" + latitude);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                setAddresses(addresses);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                //  textViewResult.setText("Latitude : "+latitude+"\n
                // Longitude : "+longitude+"\n"+address + " " + city + " " + country);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged : provider : " + provider);
        Log.d(TAG, "onStatusChanged : status : " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled : provider : " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled : provider : " + provider);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Geocoder getGeocoder() {
        return geocoder;
    }

    public void setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
