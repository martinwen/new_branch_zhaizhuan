package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.view.dialog.CashOkDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FtMinZaiTouActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_zaitou)
    TextView tvZaitou;
    @BindView(R.id.tv_zuori)
    TextView tvZuori;
    @BindView(R.id.tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.rl_touzixiangmu)
    RelativeLayout rlTouzixiangmu;
    @BindView(R.id.rl_touzijilu)
    RelativeLayout rlTouzijilu;
    @BindView(R.id.rl_shuhui)
    RelativeLayout rlShuhui;
    @BindView(R.id.iv_btn_shuhui)
    ImageView ivBtnShuhui;
    @BindView(R.id.iv_btn_invest)
    ImageView ivBtnInvest;

    public static String FLAG_FTMINGOTOINVEST = "flag_ftmingotoinvest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftmin_zai_tou);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        String fhAcctBal = CacheUtils.getString(CacheUtils.FHACCTBAL, "");
        tvZaitou.setText(fhAcctBal + "元");
        String fhYesterdayIncome = CacheUtils.getString(CacheUtils.FHYESTERDAYINCOME, "");
        tvZuori.setText(fhYesterdayIncome);
        String fhTotalIncome = CacheUtils.getString(CacheUtils.FHTOTALINCOME, "");
        tvLeiji.setText(fhTotalIncome);
    }

    @OnClick({R.id.iv_back, R.id.rl_touzixiangmu, R.id.rl_touzijilu, R.id.rl_shuhui, R.id.iv_btn_shuhui, R.id.iv_btn_invest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_touzixiangmu:
                Intent intent = new Intent(this, ZaiTouXiangMuActivity.class);
                intent.putExtra("proCode", getIntent().getStringExtra("proCode"));
                startActivity(intent);
                break;
            case R.id.rl_touzijilu:
                startActivity(new Intent(this, FtMinZaiTouJiLuActivity.class));
                break;
            case R.id.rl_shuhui:
                startActivity(new Intent(this, CashBackListActivity.class));
                break;
            case R.id.iv_btn_shuhui:
                CashOkDialog cashOkDialog = new CashOkDialog(this, R.style.YzmDialog);
                cashOkDialog.show();
                break;
            case R.id.iv_btn_invest:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("from", FLAG_FTMINGOTOINVEST);
                startActivity(intent1);
                break;
        }
    }

}
