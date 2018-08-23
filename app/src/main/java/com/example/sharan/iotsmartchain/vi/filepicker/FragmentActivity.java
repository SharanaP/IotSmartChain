package com.example.sharan.iotsmartchain.vi.filepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sharan.iotsmartchain.fp.utils.FragmentUtil;
import com.example.sharan.iotsmartchain.R;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_fp);

        initView();
    }

    private void initView() {
        CallerFragment callerFragment = new CallerFragment();
        FragmentUtil.addFragment(this, R.id.container,callerFragment);
    }
}
