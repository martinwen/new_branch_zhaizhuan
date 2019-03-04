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
import com.iqianbang.fanpai.activity.user.FtMinZaiTouJiLuActivity;
import com.iqianbang.fanpai.utils.NumAnim;
import com.iqianbang.fanpai.view.CircularMusicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FtMinBuySuccessActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_btn_invest)
    ImageView ivBtnInvest;
    @BindView(R.id.album_art)
    CircularMusicProgressBar albumArt;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.iv_gotohome)
    ImageView ivGotohome;

    private String zong_money;//买入的钱（包括奖金）

    public static String FLAG_MINBUYSUCCESS = "flag_minbuysuccess";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //给增加下划线
        tvCheck.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();
        zong_money = intent.getStringExtra("zong_money");

        //买入progressbar动画
        albumArt.setValue(100);

        //买入金额递增动画
        NumAnim.startAnim(tvMoney, Float.valueOf(zong_money), 1000);
    }

    @OnClick({R.id.iv_back, R.id.iv_gotohome, R.id.tv_check, R.id.iv_btn_invest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.iv_btn_invest:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", FLAG_MINBUYSUCCESS);
                startActivity(intent);
                break;
            case R.id.iv_gotohome:
                Intent intent2 = new Intent(this, MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;
            case R.id.tv_check:
                startActivity(new Intent(this, FtMinZaiTouJiLuActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", FLAG_MINBUYSUCCESS);
        startActivity(intent);
    }
}
