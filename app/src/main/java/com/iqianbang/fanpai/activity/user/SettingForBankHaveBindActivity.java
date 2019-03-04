package com.iqianbang.fanpai.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingForBankHaveBindActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_banklogo)
    ImageView ivBanklogo;
    @BindView(R.id.tv_bankname)
    TextView tvBankname;
    @BindView(R.id.tv_banklimit)
    TextView tvBanklimit;
    @BindView(R.id.tv_banknum)
    TextView tvBanknum;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_change)
    TextView tvChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_bank_have_bind);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //姓名
        String realName = CacheUtils.getString(CacheUtils.REALNAME, "");
        tvName.setText(realName);

        //身份证号
        String idNo = CacheUtils.getString(CacheUtils.IDNO, "");
        tvId.setText(idNo);

        //开户行图标
        String bankPic = CacheUtils.getString(CacheUtils.BANKPIC, "");
        ImageLoader.getInstance().displayImage(bankPic, ivBanklogo);

        //开户行名称
        String bankName = CacheUtils.getString(CacheUtils.BANKNAME, "");
        tvBankname.setText(bankName);

        //开户行名称
        String cardNum = CacheUtils.getString(CacheUtils.CARDNUM, "");
        tvBanknum.setText(cardNum);

        // 银行限额
        String singleTransLimit = CacheUtils.getString(CacheUtils.SINGLETRANSLIMIT, "");
        String singleDayLimit = CacheUtils.getString(CacheUtils.SINGLEDAYLIMIT, "");
        tvBanklimit.setText("单笔：" + singleTransLimit + "元   单日：" + singleDayLimit + "元");
    }

    @OnClick({R.id.iv_back, R.id.tv_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_change:
                startActivity(new Intent(this, SettingForBankChangeActivity.class));
                break;
        }
    }
}
