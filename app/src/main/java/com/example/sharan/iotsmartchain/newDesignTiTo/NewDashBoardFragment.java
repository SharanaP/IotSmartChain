package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.AnalyticsActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.MasterLockActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewDashBoardFragment extends BaseFragment /*implements View.OnClickListener*/ {

    private CardView cardViewHome;
    private CircleImageView iv_alert;
    private ImageView iv_masterLock;
    private ImageView iv_logs;
    private ImageView iv_listOfBridge;
    private ImageView iv_analytics;
    private ImageView iv_optional;


    public static NewDashBoardFragment newInstance() {
        NewDashBoardFragment newDashBoardFragment = new NewDashBoardFragment();
        return newDashBoardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_db, container, false);

        cardViewHome = (CardView)rootView.findViewById(R.id.cardView);
        cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Home/Away", Toast.LENGTH_SHORT).show();
            }
        });

        iv_alert = (CircleImageView)rootView.findViewById(R.id.image_alert);
        iv_alert.setOnClickListener(this::alert);

        iv_analytics = (ImageView)rootView.findViewById(R.id.image_analytics);
        iv_analytics.setOnClickListener(this::analytics);

        iv_logs = (ImageView)rootView.findViewById(R.id.image_logos);
        iv_logs.setOnClickListener(this::logs);

        iv_listOfBridge = (ImageView)rootView.findViewById(R.id.image_bridges);
        iv_listOfBridge.setOnClickListener(this::bridges);

        iv_masterLock = (ImageView)rootView.findViewById(R.id.image_master_lock);
        iv_masterLock.setOnClickListener(this::masterLock);

        iv_optional = (ImageView)rootView.findViewById(R.id.image_future);
        iv_optional.setOnClickListener(this::optional);
        return rootView;
    }

    private void optional(View view){
        Toast.makeText(view.getContext(), "Future optional ", Toast.LENGTH_SHORT).show();
    }

    private void masterLock(View view){
       // Toast.makeText(view.getContext(), "Hello masterLock ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), MasterLockActivity.class);
        startActivity(intent);
    }

    private void bridges(View view){
        //Toast.makeText(view.getContext(), "Hello bridges ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), AddBridgeActivity.class );
        startActivity(intent);
    }

    private void alert(View view){
        Toast.makeText(view.getContext(), "Not yet Alert design !!! ", Toast.LENGTH_SHORT).show();

    }

    private void logs(View view){
        Toast.makeText(view.getContext(), "Not yet Log's design", Toast.LENGTH_SHORT).show();
    }

    private void analytics(View view){
       // Toast.makeText(view.getContext(), "Hello analytics ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(view.getContext(), AnalyticsActivity.class);
        startActivity(intent);
    }

}
