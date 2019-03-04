package com.iqianbang.fanpai.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.iqianbang.fanpai.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UDSystemActivity extends BaseActivity {


    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udsystem);
        ButterKnife.bind(this);
        tvContent.setText("饭团系统正在升级中\n请稍后访问哦");

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //	super.onBackPressed();
    }
}
