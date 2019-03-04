package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.utils.NumAnim;
import com.iqianbang.fanpai.view.CircularMusicProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChongZhiSuccessActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.album_art)
    CircularMusicProgressBar albumArt;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private String zong_money;//充值的钱
    public static String FLAG_CHONGZHISUCCESS = "flag_chongzhisuccess";
    public static String FLAG_GOTOINVEST = "flag_gotoinvest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chong_zhi_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        zong_money = intent.getStringExtra("zong_money");

        //买入progressbar动画
        albumArt.setValue(100);

        //买入金额递增动画
        NumAnim.startAnim(tvMoney, Float.valueOf(zong_money), 1000);
    }

    @OnClick({R.id.iv_back, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", FLAG_CHONGZHISUCCESS);
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("from",FLAG_GOTOINVEST);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", FLAG_CHONGZHISUCCESS);
        startActivity(intent);
    }
}
