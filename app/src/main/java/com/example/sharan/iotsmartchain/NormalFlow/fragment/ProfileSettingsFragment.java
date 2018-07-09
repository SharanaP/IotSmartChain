package com.example.sharan.iotsmartchain.NormalFlow.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sharan on 06-04-2018.
 */

public class ProfileSettingsFragment extends Fragment {

    CircleImageView mProfileImage;
    TextView mProfileName;

    public ProfileSettingsFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileImage = (CircleImageView)view.findViewById(R.id.profileImage);
        mProfileName = (TextView)view.findViewById(R.id.profileName);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
