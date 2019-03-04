package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouJiLuActivity;
import com.iqianbang.fanpai.activity.user.ZhuanRangLiuShuiActivity;
import com.iqianbang.fanpai.utils.NumAnim;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.CircularMusicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ZhiTouBuySuccessActivity extends BaseActivity {

    @BindView(R.id.iv_btn_invest)
    ImageView ivBtnInvest;
    @BindView(R.id.album_art)
    CircularMusicProgressBar albumArt;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.iv_gotohome)
    ImageView ivGotohome;

    private String investAmount;//买入的钱（包括奖金）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_zhitou_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        investAmount = intent.getStringExtra("investAmount");

        //买入progressbar动画
        albumArt.setValue(100);

        //买入金额递增动画
        NumAnim.startAnim(tvMoney, Float.valueOf(investAmount), 1000);
    }

    @OnClick({R.id.iv_gotohome, R.id.iv_btn_invest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_gotohome:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_btn_invest:
                startActivity(new Intent(this, ZhuanRangLiuShuiActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
