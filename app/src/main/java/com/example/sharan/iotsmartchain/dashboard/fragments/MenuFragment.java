package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MenuFragment extends BaseFragment{
    private static String TAG = MenuFragment.class.getSimpleName();

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

    private String mUrl, token, loginId;

    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri, mImageCaptureUri;

    private static final int REQUEST_CODE = 0x11;

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private File outPutFile = null;

    public static MenuFragment newInstance() {
        MenuFragment menuFragment = new MenuFragment();
        return menuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_db_menu, container, false);

        img_profile = (CircleImageView)rootView.findViewById(R.id.profile_photo);
        mProfileUpdate = (CircleImageView)rootView.findViewById(R.id.profile_camera);
        mTvLoginName = (TextView)rootView.findViewById(R.id.textView_login_name);
        mTvEmail = (TextView)rootView.findViewById(R.id.textView_email);
        mProfileEditIcon  = (ImageView)rootView.findViewById(R.id.imageView_update_profile);
        mScrollView = (ScrollView)rootView.findViewById(R.id.scrollView_menu);
        mRlSettings = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_setting);
        mRlFeedBack = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_feedback);
        mRlAbout = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_about);
        mRlFAQ = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_faq);
        mRlSupport = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_support);
        mRlLogout = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_logout);
        mRlCloseAccount = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_close_account);
        mTvVersion = (TextView) rootView.findViewById(R.id.textView_version);

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
               //TODO new PhotoFullPopupWindow(context, R.layout.popup_photo_full, yourImageView, URL, null);
                profileDialog();
            }
        });

        if(loginId != null || !loginId.isEmpty()){
            mTvEmail.setText(loginId);
        }

        return rootView;
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

                    try{
                        //Pick Image From Gallery
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(Intent.createChooser(i,"Complete action using"), GALLERY_CODE);
                    }catch(Exception e){
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
            System.out.println("Gallery Image URI : "+mImageCaptureUri);
            performCrop();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("DEBUG", "CAMERA_CODE");
            picUri = data.getData();
            System.out.println("Camera Image URI : "+picUri);
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

            try{
                // get the returned data
                Bundle extras = data.getExtras();
                if (extras != null) {
                    // get the cropped bitmap
                    Bitmap thePic = extras.getParcelable("data");
                    img_profile.setImageBitmap(thePic);
                }

            }catch (NullPointerException ex){

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

    private void cameraPerformCrop(){
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
            cropIntent.setType("image/*");

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
    private void profileDialog(){
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
                float imageWidthInPX = (float)image.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                image.setLayoutParams(layoutParams);
            }
        });

    }

}
