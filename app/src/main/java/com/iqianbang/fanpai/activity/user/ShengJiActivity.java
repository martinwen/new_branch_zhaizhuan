package com.iqianbang.fanpai.activity.user;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShengJiActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_highcore)
    TextView tvHighcore;
    @BindView(R.id.tv_core)
    TextView tvCore;
    @BindView(R.id.tv_normal)
    TextView tvNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheng_ji);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String normalInfo = getIntent().getStringExtra("normalInfo");
        if (normalInfo.contains("|")) {//接口文档说明        | 表示换行
            String normal = normalInfo.replaceAll("\\|", "\n");
            tvNormal.setText(normal);
        }else {
            tvNormal.setText(normalInfo);
        }

        String coreInfo = getIntent().getStringExtra("coreInfo");
        if (coreInfo.contains("|")) {//接口文档说明        | 表示换行
            String core = coreInfo.replaceAll("\\|", "\n");
            tvCore.setText(core);
        }else {
            tvCore.setText(coreInfo);
        }

        String highcoreInfo = getIntent().getStringExtra("highcoreInfo");
        if (highcoreInfo.contains("|")) {//接口文档说明        | 表示换行
            String highcore = highcoreInfo.replaceAll("\\|", "\n");
            tvHighcore.setText(highcore);
        }else {
            tvHighcore.setText(highcoreInfo);
        }

    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
