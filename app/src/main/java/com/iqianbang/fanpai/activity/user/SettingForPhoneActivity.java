package com.iqianbang.fanpai.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.utils.CacheUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingForPhoneActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_change)
    TextView tvChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_phone);
        ButterKnife.bind(this);

        String phone = CacheUtils.getString(CacheUtils.LOGINPHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            tvPhone.setText("您当前的手机号码为\n" + fixNum(phone));
        }
    }

    //手机号码星号处理
    private String fixNum(String phone) {
        return phone = phone.substring(0, 3) + "****" + phone.substring(7);
    }

    @OnClick({R.id.iv_back, R.id.tv_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_change:
                startActivity(new Intent(this, SettingForPhoneChangeActivity.class));
                break;
        }
    }
}
