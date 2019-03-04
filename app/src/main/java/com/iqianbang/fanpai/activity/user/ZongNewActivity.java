package com.iqianbang.fanpai.activity.user;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZongNewActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_zong)
    TextView tvZong;
    @BindView(R.id.tv_sanbiao)
    TextView tvSanbiao;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.tv_jiangjin)
    TextView tvJiangjin;
    @BindView(R.id.tv_tixian)
    TextView tvTixian;
    @BindView(R.id.tv_zhuanrang)
    TextView tvZhuanrang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zongnew);
        ButterKnife.bind(this);

        //总资产
        String totalAssets = getIntent().getStringExtra("totalAssets");
        tvZong.setText(totalAssets);
        //散标资产
        String scatterTotalAssets = getIntent().getStringExtra("scatterTotalAssets");
        tvSanbiao.setText(scatterTotalAssets);
        //债转专区资产
        String transferTotalAssets = getIntent().getStringExtra("transferTotalAssets");
        tvZhuanrang.setText(transferTotalAssets);
        //账户余额
        String baseAcctBal = getIntent().getStringExtra("baseAcctBal");
        tvZhanghu.setText(baseAcctBal);
        //奖金余额
        String rewardAcctBal = getIntent().getStringExtra("rewardAcctBal");
        tvJiangjin.setText(rewardAcctBal);
        //提现中
        String totalCashingMoney = getIntent().getStringExtra("totalCashingMoney");
        tvTixian.setText(totalCashingMoney);


    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
