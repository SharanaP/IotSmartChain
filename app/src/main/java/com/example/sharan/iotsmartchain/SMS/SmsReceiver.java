package com.example.sharan.iotsmartchain.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private static String TAG = SmsReceiver.class.getSimpleName();
    private OnSmsCatchListener<String> callback;
    private String phoneNumberFilter;
    private String filter;

    public void setCallback(OnSmsCatchListener<String> callback) {
        this.callback = callback;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Log.e(TAG, ""+bundle.toString());
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = getIncomingMessage(pdusObj[i], bundle);

                    Log.e(TAG, currentMessage.toString());

                    //TODO check it out for sub string
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress().substring(2);

                    Log.e(TAG, phoneNumber);

                    if (phoneNumberFilter != null && !phoneNumber.equals(phoneNumberFilter)) {
                       // return;
                    }

                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, message);

                    if (filter != null && !message.matches(filter)) {
                        return;
                    }

                    if (callback != null) {
                        callback.onSmsCatch(message);
                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    /*Set Phone number*/
    public void setPhoneNumberFilter(String phoneNumberFilter) {
        this.phoneNumberFilter = phoneNumberFilter;
    }

    /*Set message filter for message */
    public void setFilter(String filter) {
        this.filter = filter;
    }
}
