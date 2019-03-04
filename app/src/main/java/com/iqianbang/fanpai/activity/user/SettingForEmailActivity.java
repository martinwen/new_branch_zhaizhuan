package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.CacheUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_email;


public class SettingForEmailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_change)
    TextView tvChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_email);
        ButterKnife.bind(this);

        String email = CacheUtils.getString(CacheUtils.EMAIL, "");
        tvEmail.setText("您当前的邮箱为\n" + email);
    }

    @OnClick({R.id.iv_back, R.id.tv_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_change:
                finish();
                startActivity(new Intent(this, SettingForEmailNotBindActivity.class));
                break;
        }
    }
}
