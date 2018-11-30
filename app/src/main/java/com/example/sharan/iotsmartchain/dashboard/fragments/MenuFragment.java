package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.AboutInfoActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.FaqActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.FeedBackActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.SupportMainActivity;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;

public class MenuFragment extends BaseFragment {
    private static final int REQUEST_CODE = 0x11;
    private static final int ALL_PERMISSIONS_RESULT = 117;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private static String TAG = MenuFragment.class.getSimpleName();
    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private boolean isImageFitToScreen = false;
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
    private String mUrl, token, loginId, userName, userPhone, profileImgBase64;
    private AppLogOutAsync appLogOutAsync = null;
    private CloseAccountAsync closeAccountAsync = null;
    private Uri picUri, mImageCaptureUri;
    private File outPutFile = null;
    private String deviceId, deviceName, deviceTokenId;
    private Bitmap myBitmap;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    public static MenuFragment newInstance() {
        MenuFragment menuFragment = new MenuFragment();
        return menuFragment;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        Log.e(TAG, "img : " + img + "\nUri : " + selectedImage);
        Log.d(TAG, "\n IMG : " + img.toString());
        try {
            InputStream input = context.getContentResolver().openInputStream(selectedImage);
            ExifInterface ei;
            if (Build.VERSION.SDK_INT > 23)
                ei = new ExifInterface(input);
            else
                ei = new ExifInterface(selectedImage.getPath());

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d(TAG, "\n EI : " + ei.toString());
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        }
        return img;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
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
        profileImgBase64 = App.getSharedPrefsComponent().getSharedPrefs().getString("PROFILE", null);

        /*init and get device info and token*/
        deviceId = Utils.getDeviceId(getActivity());
        deviceName = Utils.getDeviceName();
        deviceTokenId = FirebaseInstanceId.getInstance().getToken();

        //Setup action bar title
        getActivity().setTitle((getResources().getString(R.string.app_name)));
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle("MyAccount");

        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_db_menu, container, false);

        mProgressBar = rootView.findViewById(R.id.progressbar_profile);
        mProgressView = rootView.findViewById(R.id.progressView);
        img_profile = rootView.findViewById(R.id.profile_photo);
        mProfileUpdate = rootView.findViewById(R.id.profile_camera);
        mTvLoginName = rootView.findViewById(R.id.textView_login_name);
        mTextViewPhone = rootView.findViewById(R.id.textView_mobile);
        mTvEmail = rootView.findViewById(R.id.textView_email);
        mProfileEditIcon = rootView.findViewById(R.id.imageView_update_profile);
        mScrollView = rootView.findViewById(R.id.scrollView_menu);
        mRlSettings = rootView.findViewById(R.id.relativeLayout_setting);
        mRlFeedBack = rootView.findViewById(R.id.relativeLayout_feedback);
        mRlAbout = rootView.findViewById(R.id.relativeLayout_about);
        mRlFAQ = rootView.findViewById(R.id.relativeLayout_faq);
        mRlSupport = rootView.findViewById(R.id.relativeLayout_support);
        mRlLogout = rootView.findViewById(R.id.relativeLayout_logout);
        mRlCloseAccount = rootView.findViewById(R.id.relativeLayout_close_account);
        mTvVersion = rootView.findViewById(R.id.textView_version);

        if(img_profile != null){
            img_profile.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.profile_upload_white));
            img_profile.setFillColor(getActivity().getResources().getColor(R.color.color_grey_medium));
            img_profile.setBorderColor(getActivity().getResources().getColor(R.color.color_black_dark));
        }

        /*Support Screen*/
        mRlSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSupport = new Intent(getActivity(), SupportMainActivity.class);
                startActivity(intentSupport);
            }
        });
        /*About */
        mRlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAbout = new Intent(getActivity(), AboutInfoActivity.class);
                startActivity(intentAbout);
            }
        });
        /*Feed back*/
        mRlFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO reset password link and set new password
                Intent intentFaq = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intentFaq);
            }
        });
        /*FAQ*/
        mRlFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO reset password link and set new password
                Intent intentFaq = new Intent(getActivity(), FaqActivity.class);
                startActivity(intentFaq);
            }
        });
        /*Profile upload*/
        mProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Call : Start open camera or gallery images*/
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        /*Profile image view*/
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
            if (userPhone != null) mTextViewPhone.setText(userPhone);
            if (userName != null) mTvLoginName.setText(userName);

            //check abd set profile image
            if (profileImgBase64 != null) {
                //convert base64 to bitmap and set profile image
                Bitmap bitmap = StringToBitMap(profileImgBase64);
                img_profile.setImageBitmap(bitmap);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        /*Logout app*/
        mRlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLogout();//application clear loginId and token value
            }
        });
        /*Delete account*/
        mRlCloseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseAccount();//UnRegister user account
            }
        });

        return rootView;
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();
        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    myBitmap = rotateImageIfRequired(getActivity(), myBitmap, picUri); //TODO FileNotFoundException
                    myBitmap = getResizedBitmap(myBitmap, 500);
                    Log.e(TAG, "\nSH :\nBitMap : " + myBitmap);
                    img_profile.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                myBitmap = bitmap;
                Log.i(TAG, "\nData :\nBitMap : " + myBitmap);
                img_profile.setImageBitmap(myBitmap);
            }

            //write into shared pref
            if (myBitmap != null) {
                String profileImg = BitMapToString(myBitmap);
                Log.e(TAG, "BitMapToString :\n" + profileImg);
                //check is required
                SharedPreferences.Editor
                        editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                editor.putString("PROFILE", profileImg);
                editor.apply();
                editor.commit();
            }
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        try {            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp.replaceAll("\\s+", "");
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {
                    } else {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application." +
                                            " Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                Log.d(TAG, "permission rejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
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

    /*Photo preview */
    private void profileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_image_preview, null);
        dialogLayout.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CircleImageView image = dialogLayout.findViewById(R.id.goProDialogImage);
        ImageView imageViewClose = dialogLayout.findViewById(R.id.image_close);
        //check abd set profile image
        profileImgBase64 = App.getSharedPrefsComponent().getSharedPrefs().getString("PROFILE", null);
        Bitmap icon = StringToBitMap(profileImgBase64);
        if (profileImgBase64 != null) {
            //convert base64 to bitmap and set profile image
            Drawable drawable = new BitmapDrawable(icon);
           // dialogLayout.setBackground(drawable);
            image.setImageBitmap(icon);
        }
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void clearSharedPref() {
        SharedPreferences.Editor
                editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
        editor.putString("TOKEN", "");
        editor.putString("AUTH_EMAIL_ID", "");
        editor.putString("PHONE", "");
        editor.putString("PROFILE", "");
        editor.putString("EMAIL", "");
        editor.apply();
        editor.commit();
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
                jsonObject.put("userId", token);
                jsonObject.put("device", "android");
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceTokenId);
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
                    Log.e(TAG, "authResponse :: " + TestJson.getString("body"));

                    String strData = TestJson.getString("body");
                    Log.e(TAG, "strData :: " + strData);

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

            //Clear the login token
            clearSharedPref();

            //clear data
            Utils.clearApplicationData(getActivity());

            //Start main Activity
            Intent loginActivityIntent = new Intent(getActivity(), MainActivity.class);
            loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginActivityIntent);
            getActivity().finish();
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
                jsonObject.put("userId", token);
                jsonObject.put("device", "android");
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceTokenId);
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
                    Log.e(TAG, "authResponse :: " + TestJson.getString("body"));

                    String strData = TestJson.getString("body");
                    Log.e(TAG, "strData :: " + strData);

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

            /*Clear shared pref*/
            clearSharedPref();

            //clear data
            Utils.clearApplicationData(getActivity());

            //Call login screen
            Intent loginActivityIntent = new Intent(getActivity(), MainActivity.class);
            loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginActivityIntent);
            getActivity().finish();
            //}
        }
    }

}
