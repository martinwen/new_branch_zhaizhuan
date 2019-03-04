package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.utils.NumAnim;
import com.iqianbang.fanpai.view.CircularMusicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FuTouBuySuccessActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.album_art)
    CircularMusicProgressBar albumArt;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.iv_gotohome)
    ImageView ivGotohome;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_money)
    LinearLayout llMoney;
    @BindView(R.id.tv_yuyue)
    TextView tvYuyue;

    private String zong_money;//买入的钱（包括奖金）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futou_buy_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if("ft_yue".equals(from)){
            tvTitle.setText("预约成功");
            llMoney.setVisibility(View.GONE);
        }
        if("ft_buy".equals(from)){
            tvTitle.setText("复投成功");
            tvYuyue.setVisibility(View.GONE);
        }
        zong_money = intent.getStringExtra("zong_money");

        //买入progressbar动画
        albumArt.setValue(100);

        //买入金额递增动画
        NumAnim.startAnim(tvMoney, Float.valueOf(zong_money), 1000);
    }

    @OnClick({R.id.iv_back, R.id.iv_gotohome})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_gotohome:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
