package com.example.sharan.iotsmartchain.Services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.sharan.iotsmartchain.FireBaseMessagModule.Config;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.global.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//               //TODO scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//               //TODO handleNow();
//            }
//
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                String remoteData = remoteMessage.getData().toString();

                //This line for remove all '\' character
                remoteData = remoteData.toString().replaceAll("\\\\", "");

                Log.e(TAG, "Sh : "+remoteData);

                //Json object
                JSONObject TestJson = new JSONObject(remoteData);
                String strData = TestJson.getJSONObject("default").toString();
                     //   .getJSONObject("GCM").getJSONObject("data").toString();

                Log.e(TAG, "SH : "+strData.toString());
                JSONObject jsonObjectData = new JSONObject(strData);

//                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(jsonObjectData);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            //TODO  app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
            Log.d(TAG, "If the app is in background, firebase itself handles the notification\n" +
                    " : " + message.toString());
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            //TODO JSONObject data = json.getJSONObject(json.toString());
            String _id = json.getString("id");
            String title = json.getString("title");
            String body = json.getString("body");
            String message = json.getString("message");
            boolean isBackground = json.getBoolean("isbackground");
            String imageUrl = json.getString("imageurl");
            String timestamp = json.getString("timestamp");
          //  String unReadCount = json.getString("countValue");
            //JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "id : " + _id);
            Log.e(TAG, "title : " + title);
            Log.e(TAG, "body : " + body);
            Log.e(TAG, "message : " + message);
            Log.e(TAG, "isBackground : " + isBackground);
            // Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl : " + imageUrl);
            Log.e(TAG, "timestamp : " + timestamp);
          //  Log.e(TAG, "Unread Count Value : "+unReadCount);


            /*// app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
            resultIntent.putExtra("message", message);

            // check for image attachment
            if (TextUtils.isEmpty(imageUrl)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent,
                        imageUrl);
            }*/

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Log.e(TAG, "App not in background");
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Log.e(TAG, "App is in background");
                Intent resultIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl) && imageUrl != null) {
                    Log.d(TAG, " imageUrl is null");
                    showNotificationMessage(getApplicationContext(), body, message, timestamp, resultIntent);
                } else {
                    Log.d(TAG, " imageUrl is not null");
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), body, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        Log.e(TAG, "showNotificationMessage");
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        Log.e(TAG, "showNotificationMessageWithBigImage");
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
