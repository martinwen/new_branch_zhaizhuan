package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TiXianJinDuActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_banklogo)
    ImageView ivBanklogo;
    @BindView(R.id.tv_bankname)
    TextView tvBankname;
    @BindView(R.id.tv_weihao)
    TextView tvWeihao;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    public static String FLAG_TIXIANSUCCESS = "flag_tixiansuccess";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_xian_jin_du);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //开户行图标
        String bankPic = CacheUtils.getString(CacheUtils.BANKPIC, "");
        ImageLoader.getInstance().displayImage(bankPic, ivBanklogo);

        //开户行名称
        String bankName = CacheUtils.getString(CacheUtils.BANKNAME, "");
        tvBankname.setText(bankName);

        //姓名
        String realName = CacheUtils.getString(CacheUtils.REALNAME, "");
        tvName.setText(realName);

        //身份证号
        String idNo = CacheUtils.getString(CacheUtils.IDNO, "");
        tvId.setText(idNo);

        //卡号
        String cardNum = CacheUtils.getString(CacheUtils.CARDNUM, "");
        tvWeihao.setText("(尾号：" + cardNum.substring(cardNum.length() - 4, cardNum.length()) + ")");

        //到帐金额
        String arriveAmount = getIntent().getStringExtra("arriveAmount");
        LogUtils.i("arriveAmount==" + arriveAmount);
        tvMoney.setText(arriveAmount);

        //到账时间
        String arriveTime = getIntent().getStringExtra("arriveTime");
        tvTime.setText(arriveTime);
    }

    @OnClick({R.id.iv_back, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_btn_finish:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", FLAG_TIXIANSUCCESS);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", FLAG_TIXIANSUCCESS);
        startActivity(intent);
    }
}
