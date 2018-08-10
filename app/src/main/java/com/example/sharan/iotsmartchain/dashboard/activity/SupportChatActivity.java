package com.example.sharan.iotsmartchain.dashboard.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.MessageListAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.GetChatHistoryData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class SupportChatActivity extends BaseActivity {
    private static String TAG = SupportChatActivity.class.getSimpleName();

//    @BindView(R.id.constrainlayout_support) ConstraintLayout constraintLayoutSupport;
//    @BindView(R.id.toolbar) Toolbar toolbar;
//    @BindView(R.id.reyclerview_message_list) RecyclerView recyclerViewSupport;
//    @BindView(R.id.edittext_chatbox) EditText editTextMessage;
//    @BindView(R.id.button_chatbox_send) Button buttonSend;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.layout_open_chat_chatbox)LinearLayout linearLayoutChatBox;
    @BindView(R.id.button_open_channel_chat_upload)ImageButton mImageButtonUpload;
    @BindView(R.id.edittext_chat_message)EditText editTextMessage;
    @BindView(R.id.button_open_channel_chat_send)Button buttonSend;
    @BindView(R.id.recycler_open_channel_chat)RecyclerView recyclerViewSupport;
    @BindView(R.id.text_open_chat_current_event)TextView textViewCurrentEvent;


    private MessageListAdapter messageListAdapter = null;
    private List<GetChatHistoryData> messageList = new ArrayList<>();

    private String token, loginId, mUrl;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);
        injectViews();
        setupToolbar();

        //Get all
        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);


        //init adapter and display a list of support messages
        setUpAdapter();

        setUpRecyclerView();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get message and sent server and display
                String msgText =  editTextMessage.getText().toString();

                if(!msgText.isEmpty() || msgText.length() > 0 ){
                    sendUserMessage(msgText);
                }else{

                }
            }
        });

    }

    private void sendUserMessage(String msgText) {
        // local timezone
        Date now = new Date();
        Long longTime = new Long(now.getTime());

        GetChatHistoryData mGetChatHistoryData = new GetChatHistoryData();
        mGetChatHistoryData.set_id("122");//dummy
        mGetChatHistoryData.setAuthor(loginId);
        mGetChatHistoryData.setSenderName(loginId);
        mGetChatHistoryData.setChannelName("");
        mGetChatHistoryData.setMessage(msgText);
        mGetChatHistoryData.setGroup(false);
        mGetChatHistoryData.setTimeStamp(longTime);

        messageListAdapter.addFirst(mGetChatHistoryData);
        editTextMessage.setText("");
    }

    private void setUpAdapter() {
        messageListAdapter  = new MessageListAdapter(SupportChatActivity.this, messageList);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        recyclerViewSupport.setLayoutManager(mLayoutManager);
        recyclerViewSupport.setAdapter(messageListAdapter);

        // Load more messages when user reaches the top of the current message list.
        recyclerViewSupport.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (mLayoutManager.findLastVisibleItemPosition() == messageListAdapter.getItemCount() - 1) {
                  //  loadNextMessageList(CHANNEL_LIST_LIMIT);
                }
                Log.v(TAG, "onScrollStateChanged");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                //TODO call API to get updated Analytics status
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Support");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}