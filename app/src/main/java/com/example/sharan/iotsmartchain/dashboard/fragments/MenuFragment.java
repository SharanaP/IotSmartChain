package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.AboutInfoActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.FaqActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.FeedBackActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.SupportChatActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.SupportMainActivity;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class MenuFragment extends BaseFragment {
    private static final int REQUEST_CODE = 0x11;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private static String TAG = MenuFragment.class.getSimpleName();
    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private CircleImageView img_profile;
    private CircleImageView mProfileUpdate;
    private TextView mTvLoginName;
    private TextView mTvEmail;
    private ImageView mProfileEditIcon;
    private ScrollView mScrollView;
    private RelativeLayout mRlSettings;
    private RelativeLayout mRlFeedBack;
    private RelativeLayout mRlAbout;
    private RelativeLayout mRlFAQ;
    private RelativeLayout mRlSupport;
    private RelativeLayout mRlLogout;
    private RelativeLayout mRlCloseAccount;
    private TextView mTvVersion;
    private TextView mTextViewPhone;
    private ProgressBar mProgressBar;
    private View mProgressView;
    private String mUrl, token, loginId, userName, userPhone;
    private AppLogOutAsync appLogOutAsync = null;
    private CloseAccountAsync closeAccountAsync = null;
    private Uri picUri, mImageCaptureUri;
    private File outPutFile = null;

    private String deviceId, deviceName, deviceTokenId;

    public static MenuFragment newInstance() {
        MenuFragment menuFragment = new MenuFragment();
        return menuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init and get url, login and token
        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        userName = App.getSharedPrefsComponent().getSharedPrefs().getString("NAME", null);
        userPhone = App.getSharedPrefsComponent().getSharedPrefs().getString("PHONE", null);

        /*init and get device info and token*/
        deviceId = Utils.getDeviceId(getActivity());
        deviceName = Utils.getDeviceName();
        deviceTokenId = FirebaseInstanceId.getInstance().getToken();

        //Setup action bar title
        getActivity().setTitle("iSmartLink");
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle("MyAccount");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_db_menu, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_profile);
        mProgressView = (View) rootView.findViewById(R.id.progressView);
        img_profile = (CircleImageView) rootView.findViewById(R.id.profile_photo);
        mProfileUpdate = (CircleImageView) rootView.findViewById(R.id.profile_camera);
        mTvLoginName = (TextView) rootView.findViewById(R.id.textView_login_name);
        mTextViewPhone = (TextView)rootView.findViewById(R.id.textView_mobile);
        mTvEmail = (TextView) rootView.findViewById(R.id.textView_email);
        mProfileEditIcon = (ImageView) rootView.findViewById(R.id.imageView_update_profile);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView_menu);
        mRlSettings = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_setting);
        mRlFeedBack = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_feedback);
        mRlAbout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_about);
        mRlFAQ = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_faq);
        mRlSupport = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_support);
        mRlLogout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_logout);
        mRlCloseAccount = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_close_account);
        mTvVersion = (TextView) rootView.findViewById(R.id.textView_version);

        /*Support Screen*/
        mRlSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSupport = new Intent(getActivity(), SupportMainActivity.class);
                startActivity(intentSupport);
            }
        });

        mRlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAbout = new Intent(getActivity(), AboutInfoActivity.class);
                startActivity(intentAbout);
            }
        });


        mRlFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO reset password link and set new password
                Intent intentFaq = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intentFaq);
            }
        });

        mRlFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO reset password link and set new password
                Intent intentFaq = new Intent(getActivity(), FaqActivity.class);
                startActivity(intentFaq);
            }
        });

        mProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUpdate(v);
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show Profile Pre-view
                profileDialog();
            }
        });

        //Set login email id and mobile number
        try {
            if (loginId != null || !loginId.isEmpty()) {
                mTvEmail.setText(loginId);
            }

            if(userPhone != null) mTextViewPhone.setText(userPhone);
            if(userName != null) mTvLoginName.setText(userName);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        mRlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLogout();//application clear loginId and token value
            }
        });

        mRlCloseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAccount();//UnRegister user account
            }
        });

        return rootView;
    }

    //Application close account and clear device INFO
    private void CloseAccount() {
        Utils.showProgress(this.getActivity(), mProgressView, mProgressBar, true);
        closeAccountAsync = new CloseAccountAsync(getActivity(), loginId);
        closeAccountAsync.execute((Void) null);
    }

    //Application clear login id and token
    private void AppLogout() {
        Utils.showProgress(this.getActivity(), mProgressView, mProgressBar, true);
        //App logout async and unregister a device ID and info
        AppLogOutAsync appLogOutAsync = new AppLogOutAsync(getActivity(), loginId);
        appLogOutAsync.execute((Void) null);
    }

    private void profileUpdate(View v) {

        final CharSequence[] items = {"Take photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set profile photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {

                    try {
                        //Pick Image From Gallery
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(Intent.createChooser(i, "Complete action using"), GALLERY_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {
            Log.d("DEBUG", "GALLERY_CODE");

            mImageCaptureUri = data.getData();
            System.out.println("Gallery Image URI : " + mImageCaptureUri);
            performCrop();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("DEBUG", "CAMERA_CODE");
            picUri = data.getData();
            System.out.println("Camera Image URI : " + picUri);
            cameraPerformCrop();
        } else if (requestCode == CROPING_CODE) {
            Log.d("DEBUG", "CROPING_CODE");

            try {
                if (outPutFile.exists()) {
                    Bitmap photo = decodeFile(outPutFile);
                    img_profile.setImageBitmap(photo);
                } else {
                    Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROP_PIC) {
            Log.d("DEBUG", "CROP_PIC");

            try {
                // get the returned data
                Bundle extras = data.getExtras();
                if (extras != null) {
                    // get the cropped bitmap
                    Bitmap thePic = extras.getParcelable("data");
                    img_profile.setImageBitmap(thePic);
                }

            } catch (NullPointerException ex) {

            }
        }
    }

    private Bitmap decodeFile(File outPutFile) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(outPutFile), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(outPutFile), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cameraPerformCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");

            // set crop properties
            cropIntent.putExtra("crop", "true");

            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);

            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("circleCrop", new String(""));

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(getActivity(), "This device doesn't support the crop action!",
                            Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * this function does the crop operation.
     */
    private void performCrop() {
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //cropIntent.setType("image/*");

            //indicate image type and Uri
            cropIntent.setDataAndType(mImageCaptureUri, "image/*");


            // set crop properties
            cropIntent.putExtra("crop", "true");

            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);

            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("circleCrop", new String(""));

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(getActivity(), "HELLO :::: This device doesn't support " +
                            "the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*Photo preview */
    private void profileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_image_preview, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                        R.drawable.profile_upload_white);
                float imageWidthInPX = (float) image.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float) icon.getHeight() / (float) icon.getWidth()));
                image.setLayoutParams(layoutParams);
            }
        });

    }

    //Application logout async
    public class AppLogOutAsync extends AsyncTask<Void, String, String> {
        private Context context;
        private String mUrl, email;
        private String retVal = "false";
        private String respMessage = "";

        public AppLogOutAsync(Context context, String email) {
            this.context = context;
            this.email = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            retVal = "false";

            mUrl = App.getAppComponent().getApiServiceUrl();

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("device", "android");
                jsonObject.put("deviceId", deviceId );
                jsonObject.put("deviceName", deviceName );
                jsonObject.put("deviceTokenId", deviceTokenId );
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "logout")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "logout");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());

            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());


                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);

                    Log.e(TAG, "authResponse :: " + TestJson.toString());
                    Log.e(TAG, "authResponse :: " + TestJson.getString("body").toString());

                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());

                    JSONObject respData = new JSONObject(strData);
                    /*{"message":"Mobile OTP Confirm","status":"true"}*/

                    Log.e(TAG, respData.getString("message"));
                    Log.e(TAG, respData.getString("status"));

                    retVal = respData.getString("status");
                    respMessage = respData.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Logout " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at logout" + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            Utils.showProgress(getActivity(), mProgressView, mProgressBar, false);
            appLogOutAsync = null;
            if (data.equalsIgnoreCase("true")) {
                // showProgress(false);
                //TODO clear the login token
                SharedPreferences.Editor
                editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                editor.putString("TOKEN", "");
                editor.putString("AUTH_EMAIL_ID", "");
                editor.putString("PHONE", "");
                editor.putString("EMAIL", "");
                editor.apply();

                // Start the login activity
                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginActivityIntent);
                getActivity().finish();
            }
        }

        @Override
        protected void onCancelled() {
            Utils.showProgress(getActivity(), mProgressView, mProgressBar, false);
            appLogOutAsync = null;
            super.onCancelled();
        }
    }

    //Un register or close account
    public class CloseAccountAsync extends AsyncTask<Void, String, String> {
        private Context context;
        private String mUrl, email;
        private String retVal = "false";
        private String respMessage = "";

        public CloseAccountAsync(Context context, String email) {
            this.context = context;
            this.email = email;

            mUrl = App.getAppComponent().getApiServiceUrl();
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", "");
            loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", "");
        }

        @Override
        protected String doInBackground(Void... voids) {

            retVal = "false";

            mUrl = App.getAppComponent().getApiServiceUrl();

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("device", "android");
                jsonObject.put("deviceId", deviceId );
                jsonObject.put("deviceName", deviceName );
                jsonObject.put("deviceTokenId", deviceTokenId );
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "close-account")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "logout");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());

            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());


                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);

                    Log.e(TAG, "authResponse :: " + TestJson.toString());
                    Log.e(TAG, "authResponse :: " + TestJson.getString("body").toString());

                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());

                    JSONObject respData = new JSONObject(strData);
                    /*{"message":"Mobile OTP Confirm","status":"true"}*/

                    Log.e(TAG, respData.getString("message"));
                    Log.e(TAG, respData.getString("status"));

                    retVal = respData.getString("status");
                    respMessage = respData.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at close account " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at close account" + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            Utils.showProgress(getActivity(), mProgressView, mProgressBar, false);

            Snackbar.make(mProgressView, respMessage, Snackbar.LENGTH_LONG).show();

            if (data.equalsIgnoreCase("true")) {

                SharedPreferences.Editor
                        editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                editor.putString("TOKEN", "");
                editor.putString("AUTH_EMAIL_ID", "");
                editor.putString("PHONE", "");
                editor.putString("EMAIL", "");
                editor.apply();

                //Call login screen
                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginActivityIntent);
                getActivity().finish();
            }
        }
    }

}
