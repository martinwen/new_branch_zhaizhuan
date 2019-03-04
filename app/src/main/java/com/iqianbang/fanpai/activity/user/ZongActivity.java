package com.iqianbang.fanpai.activity.user;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.StrToNumber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class ZongActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.chart)
    PieChartView chart;
    @BindView(R.id.tv_xiaofantuan)
    TextView tvXiaofantuan;
    @BindView(R.id.tv_dafantuan)
    TextView tvDafantuan;
    @BindView(R.id.tv_jingyingtuan)
    TextView tvJingyingtuan;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.tv_tixian)
    TextView tvTixian;
    @BindView(R.id.tv_jiangjin)
    TextView tvJiangjin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zong);
        ButterKnife.bind(this);

        //小饭团资产
        String fhAcctBal = CacheUtils.getString(CacheUtils.FHACCTBAL, "");
        tvXiaofantuan.setText(fhAcctBal);
        //大饭团资产
        String fwAcctBal = CacheUtils.getString(CacheUtils.FWACCTBAL, "");
        tvDafantuan.setText(fwAcctBal);
        //精英团资产
        String ftAcctBal = CacheUtils.getString(CacheUtils.FTACCTBAL, "");
        tvJingyingtuan.setText(ftAcctBal);
        //账户余额
        String baseAcctBal = CacheUtils.getString(CacheUtils.BASEACCTBAL, "");
        tvZhanghu.setText(baseAcctBal);
        //提现中
        String totalCashingMoney = CacheUtils.getString(CacheUtils.TOTALCASHINGMONEY, "");
        tvTixian.setText(totalCashingMoney);
        //奖金余额
        String rewardAcctBal = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
        tvJiangjin.setText(rewardAcctBal);


        List<SliceValue> values = new ArrayList<SliceValue>();
        //小饭团资产
        SliceValue sliceValue1 = new SliceValue(StrToNumber.strTofloat(fhAcctBal), Color.parseColor("#FF652B"));
        values.add(sliceValue1);
        //大饭团资产
        SliceValue sliceValue2 = new SliceValue(StrToNumber.strTofloat(fwAcctBal), Color.parseColor("#F6B52B"));
        values.add(sliceValue2);
        //精英团资产
        SliceValue sliceValue3 = new SliceValue(StrToNumber.strTofloat(ftAcctBal), Color.parseColor("#2AB15C"));
        values.add(sliceValue3);
        //账户余额
        SliceValue sliceValue4 = new SliceValue(StrToNumber.strTofloat(baseAcctBal), Color.parseColor("#727CEA"));
        values.add(sliceValue4);
        //提现中
        SliceValue sliceValue5 = new SliceValue(StrToNumber.strTofloat(totalCashingMoney), Color.parseColor("#9BA3FC"));
        values.add(sliceValue5);
        //奖金余额
        SliceValue sliceValue6 = new SliceValue(StrToNumber.strTofloat(rewardAcctBal), Color.parseColor("#B9BDE5"));
        values.add(sliceValue6);

        PieChartData data = new PieChartData(values);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(0);

        //总资产
        data.setCenterText1("总资产（元）");
        data.setCenterText1Color(getResources().getColor(R.color.bg_one_black));
        data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));

        //金额
        String totalAssets = CacheUtils.getString(CacheUtils.TOTALASSETS, "");
        data.setCenterText2(totalAssets);
        data.setCenterText2Color(getResources().getColor(R.color.text_red_dark));
        data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));

        //中间圆的颜色和比例
//        data.setCenterCircleColor(0xFF212121);
        data.setCenterCircleScale(0.8f);

        chart.setPieChartData(data);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
