package com.iqianbang.fanpai.activity.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.DeviceUtil;
import com.iqianbang.fanpai.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_weixin)
    RelativeLayout rlWeixin;
    @BindView(R.id.ll_weixin)
    LinearLayout llWeixin;
    @BindView(R.id.rl_location)
    RelativeLayout rlLocation;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.tv_verson)
    TextView tvVerson;
    @BindView(R.id.iv_arrow_weixin)
    ImageView ivArrowWeixin;
    @BindView(R.id.iv_arrow_location)
    ImageView ivArrowLocation;
    @BindView(R.id.tv_phone)
    TextView tvPhone;

    private boolean isWeixinExpend = false;//微信公众号条目初始为关闭状态
    private boolean isLocationExpend = false;//定位我们条目初始为关闭状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //获得版本
        tvVerson.setText(DeviceUtil.getVersionname());
    }

    @OnClick({R.id.iv_back, R.id.tv_phone,R.id.rl_weixin, R.id.rl_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_phone:
                String number = tvPhone.getText().toString().trim();
                //用intent启动拨打电话
                LogUtils.i("打电话啊~~~~~~~~~~~"+number);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
                startActivity(intent);
                break;
            case R.id.rl_weixin:
                isWeixinExpend = !isWeixinExpend;
                if (isWeixinExpend) {
                    llWeixin.setVisibility(View.VISIBLE);
                    ivArrowWeixin.setImageResource(R.drawable.arrow_up);
                    //打开的同时关闭位置条目
                    if (isLocationExpend) {
                        llLocation.setVisibility(View.GONE);
                        ivArrowLocation.setImageResource(R.drawable.arrow_down);
                        isLocationExpend = !isLocationExpend;
                    }
                } else {
                    llWeixin.setVisibility(View.GONE);
                    ivArrowWeixin.setImageResource(R.drawable.arrow_down);
                }
                break;
            case R.id.rl_location:
                isLocationExpend = !isLocationExpend;
                if (isLocationExpend) {
                    llLocation.setVisibility(View.VISIBLE);
                    ivArrowLocation.setImageResource(R.drawable.arrow_up);
                    //打开的同时关闭微信条目
                    if (isWeixinExpend) {
                        llWeixin.setVisibility(View.GONE);
                        ivArrowWeixin.setImageResource(R.drawable.arrow_down);
                        isWeixinExpend = !isWeixinExpend;
                    }
                } else {
                    llLocation.setVisibility(View.GONE);
                    ivArrowLocation.setImageResource(R.drawable.arrow_down);
                }
                break;
        }
    }
}
