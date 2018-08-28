package com.example.sharan.iotsmartchain.global;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Sharan on 19-03-2018.
 */

public class Utils {

    /*Fragment replace*/
    public static void replaceFragment(final Activity activity, final Fragment fragment,
                                       final int layoutResourceId, final boolean addToBackstack) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(layoutResourceId, fragment);
        if (addToBackstack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
         fragmentTransaction.commit();
    }

    /*Show Progress */
    public static void showProgress(final Context context, final View fragmentContainer,
                                    final View progressView, final boolean show) {
        showView(fragmentContainer, !show);
        animate(context, fragmentContainer, !show);
        showView(progressView, show);
        animate(context, progressView, show);
    }

    /*Show progress visible or gone*/
    private static void showView(final View view, final boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /*Animation */
    private static void animate(final Context context, final View view, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in the
        // progress spinner.
        int shortAnimTime = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        ViewPropertyAnimator animator = view.animate();
        animator.setDuration(shortAnimTime);
        if (show) {
            animator.alpha(1);
        } else {
            animator.alpha(0);
        }
        animator.setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(final Animator animation) {
                showView(view, show);
            }
        });
    }

    /**
     * @param r Reader
     * @return String
     */
    public static String readerToString(BufferedReader r) {
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /*Get Current data and time */
    public static String getDataFormat(){
        String dataStr = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy, hh:mm a");
        dataStr = simpleDateFormat.format(new Date());
        Log.d("getDataFormat", " :: "+dataStr);
        return dataStr;
    }

    public static String convertTime(long time){
        String dateString = new SimpleDateFormat("yyyy MMM dd, HH:mm aa").format(new Date(time));
        Log.d("Utils", "dateString : "+dateString);

        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MMM dd, HH:mm aa");
        return format.format(date);
    }

    public static String getChatTimeStamp(long time){
        String dateString = new SimpleDateFormat("yyyy MMM dd, HH:mm aa").format(new Date(time));
        Log.d("Utils", "dateString : "+dateString);

        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static String pickUpColorCode(String pos){
        String selectedColor ="";
        ArrayList<String> listOfColor = new ArrayList<String>();

        listOfColor.add("#FF6D00");listOfColor.add("#00C853");listOfColor.add("#AA00EF");
        listOfColor.add("#FFD600"); listOfColor.add("#00B8D4"); listOfColor.add("#00C853");
        listOfColor.add("#6200EA"); listOfColor.add("#304FFE");listOfColor.add("#AEFA00");
        listOfColor.add("#64DD17");


        if(pos.contains("0")){
            selectedColor = listOfColor.get(0);
        }else if(pos.contains("1")){
            selectedColor = listOfColor.get(1);
        }else if(pos.contains("2")){
            selectedColor = listOfColor.get(2);
        }else if(pos.contains("3")){
            selectedColor = listOfColor.get(3);
        }else if(pos.contains("4")){
            selectedColor = listOfColor.get(4);
        }else if(pos.contains("5")){
            selectedColor = listOfColor.get(5);
        }else if(pos.contains("6")){
            selectedColor = listOfColor.get(6);
        }else if(pos.contains("7")){
            selectedColor = listOfColor.get(7);
        }else if(pos.contains("8")){
            selectedColor = listOfColor.get(8);
        }else if(pos.contains("9")){
            selectedColor = listOfColor.get(9);
        }

        return selectedColor;
    }

//    @SuppressLint("HardwareIds")
//    public static String getDeviceId(Context context){
//        String android_name="";
//        return android_name = Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        Log.d("device ID : ", telephonyManager.getDeviceId());
        return telephonyManager.getDeviceId();
    }


    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        String deviceMan = Build.MANUFACTURER;
        Log.d("deviceName : ", deviceMan + " " + deviceName);
        return deviceMan + " " + deviceName;
    }

    public static CountDownTimer showTimeCountDowner(TextView countDownTextView, int minutes){
         int milliseconds = minutes * 60 * 1000;

        CountDownTimer countDownTimer = new CountDownTimer(milliseconds, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                countDownTextView.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                countDownTextView.setText("Time Up!");
            }
        };

        return countDownTimer;
    }

}
