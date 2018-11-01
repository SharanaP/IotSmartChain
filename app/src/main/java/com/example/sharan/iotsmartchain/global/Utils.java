package com.example.sharan.iotsmartchain.global;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;

import java.io.BufferedReader;
import java.io.File;
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
    private static String TAG = Utils.class.getSimpleName();

    private ProgressDialog progressDialog;

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
    public static String getDataFormat() {
        String dataStr = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy, hh:mm a");
        dataStr = simpleDateFormat.format(new Date());
        Log.d("getDataFormat", " :: " + dataStr);
        return dataStr;
    }

    /*Convert time from long to time format string */
    public static String convertTime(long time) {
        String dateString = new SimpleDateFormat("yyyy MMM dd, HH:mm aa").format(new Date(time));
        Log.d("Utils", "dateString : " + dateString);

        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MMM dd, hh:mm aa");
        return format.format(date);
    }

    public static String getChatTimeStamp(long time) {
        String dateString = new SimpleDateFormat("yyyy MMM dd, HH:mm aa").format(new Date(time));
        Log.d("Utils", "dateString : " + dateString);

        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static String pickUpColorCode(String pos) {
        String selectedColor = "";
        ArrayList<String> listOfColor = new ArrayList<String>();

        listOfColor.add("#FF6D00");
        listOfColor.add("#00C853");
        listOfColor.add("#AA00EF");
        listOfColor.add("#FFD600");
        listOfColor.add("#00B8D4");
        listOfColor.add("#00C853");
        listOfColor.add("#6200EA");
        listOfColor.add("#304FFE");
        listOfColor.add("#AEFA00");
        listOfColor.add("#64DD17");


        if (pos.contains("0")) {
            selectedColor = listOfColor.get(0);
        } else if (pos.contains("1")) {
            selectedColor = listOfColor.get(1);
        } else if (pos.contains("2")) {
            selectedColor = listOfColor.get(2);
        } else if (pos.contains("3")) {
            selectedColor = listOfColor.get(3);
        } else if (pos.contains("4")) {
            selectedColor = listOfColor.get(4);
        } else if (pos.contains("5")) {
            selectedColor = listOfColor.get(5);
        } else if (pos.contains("6")) {
            selectedColor = listOfColor.get(6);
        } else if (pos.contains("7")) {
            selectedColor = listOfColor.get(7);
        } else if (pos.contains("8")) {
            selectedColor = listOfColor.get(8);
        } else if (pos.contains("9")) {
            selectedColor = listOfColor.get(9);
        }

        return selectedColor;
    }

    /*Device serial num*/
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        Log.d("device ID : ", telephonyManager.getDeviceId());
        return telephonyManager.getDeviceId();
    }

    /*Get device name*/
    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        String deviceMan = Build.MANUFACTURER;
        Log.d("deviceName : ", deviceMan + " " + deviceName);
        return deviceMan + " " + deviceName;
    }

    /*Count down timer */
    public static CountDownTimer showTimeCountDowner(TextView countDownTextView, int minutes) {
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

    /*Close soft key broad */
    public static boolean DisableSoftKeyBoard(Activity activity) {
        boolean isHidden = false;
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        InputMethodManager imm = null;
        if (view != null) {
            imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            isHidden = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            isHidden = false;
        }
        Log.d(TAG, "DisableSoftKeyBoard :: " + isHidden);
        return isHidden;
    }

    /*Open or enable soft key broad*/
    public static boolean EnableSoftKeyBoard(Activity activity, View view) {
        boolean isShown = false;
        if (view == null) view = activity.getCurrentFocus();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        try {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                //if is close then open
                if (DisableSoftKeyBoard(activity))
                    isShown = imm.showSoftInput(view, 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "EnableSoftKeyBoard :: " + isShown);
        return isShown;
    }

    /*Show internet check dialog*/
    public static void ShowAlertDialog(Activity activity, String dialogMsg, String btnLabel) {
        // create a Dialog component
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setTitle("");
        ImageView imageViewIcon = (ImageView) dialog.findViewById(R.id.imageview_dialog_icon);
        TextView textViewTitle = (TextView) dialog.findViewById(R.id.textview_dialog_msg);
        Button buttonTryAgain = (Button) dialog.findViewById(R.id.button_try_again);
        textViewTitle.setText(dialogMsg);
        buttonTryAgain.setText(btnLabel);
        dialog.setCancelable(false);
        buttonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*Show custom snack bar */
    public static Snackbar SnackBarView(Activity activity, CoordinatorLayout coordinatorLayout,
                                        String message, ALERTCONSTANT alertconstant) {
        //Init snack bar
        Snackbar sb = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View view1 = sb.getView();

        //default if alert constant  value is not match
        view1.setBackgroundColor(activity.getResources().getColor(R.color.qr_code_finder_laser));
        TextView tv = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(activity.getResources().getColor(R.color.white));

        switch (alertconstant) {
            case INFO:
                view1.setBackgroundColor(activity.getResources().getColor(R.color.qr_code_finder_laser));
                tv.setTextColor(activity.getResources().getColor(R.color.white));
                break;
            case SUCCESS:
                view1.setBackgroundColor(activity.getResources().getColor(R.color.color_text));
                tv.setTextColor(activity.getResources().getColor(R.color.white));
                break;
            case WARNING:
                view1.setBackgroundColor(activity.getResources().getColor(R.color.color_yellow));
                tv.setTextColor(activity.getResources().getColor(R.color.white));
                break;
            case ERROR:
                view1.setBackgroundColor(activity.getResources().getColor(R.color.color_red));
                tv.setTextColor(activity.getResources().getColor(R.color.white));
                break;
            case NONE: default:
                view1.setBackgroundColor(activity.getResources().getColor(R.color.color_black_dark));
                tv.setTextColor(activity.getResources().getColor(R.color.white));
                break;
        }
        tv.setSingleLine(false);
        tv.setMaxLines(5);
        sb.show();//finally show snack bar

        return sb;
    }

    /*Clear application cache*/
    public static void clearApplicationData(Activity activity) {
        File cache = activity.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i(TAG, "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    /*delete dir*/
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
