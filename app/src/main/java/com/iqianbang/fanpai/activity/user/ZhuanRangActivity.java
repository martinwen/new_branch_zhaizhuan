package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.NomalWebviewActivity;
import com.iqianbang.fanpai.activity.invest.InvestXieYiActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CashBackDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.XieYiDialog;
import com.iqianbang.fanpai.view.dialog.ZhuanRangDialog;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhuanRangActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_jiekuan)
    TextView tvJiekuan;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_shengyu)
    TextView tvShengyu;
    @BindView(R.id.tv_daoqi)
    TextView tvDaoqi;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_rate_zhe)
    TextView tvRateZhe;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.tv_money_zhe)
    TextView tvMoneyZhe;
    @BindView(R.id.tv_money_fee)
    TextView tvMoneyFee;
    @BindView(R.id.iv_check_xieyi)
    ImageView ivCheckXieyi;
    @BindView(R.id.tv_xieyi_jinzhi)
    TextView tvXieyiJinzhi;
    @BindView(R.id.tv_xieyi)
    TextView tvXieyi;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;
    @BindView(R.id.rl_rate)
    RelativeLayout rlRate;



    private CustomProgressDialog progressdialog;
    private boolean isXieYiChecked = false;// 协议已经勾上
    private PopupWindow pop;
    private String[] list_day;
    private String transferFeeRate2;
    private String holdMoney;
    private String discountRate = "0.00";
    private String from;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanrang);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在赎回中...");
        initData();
    }

    private void initData() {
        from = getIntent().getStringExtra("from");
        id = getIntent().getStringExtra("id");
        getDataFromServer();
    }

    @OnClick({R.id.iv_back, R.id.rl_rate, R.id.tv_money_fee, R.id.iv_check_xieyi,
              R.id.tv_xieyi_jinzhi, R.id.tv_xieyi, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_rate:
                ListView listView = initListview();
                // 点击时，弹出Popupwindow
                pop = new PopupWindow(listView, rlRate.getWidth(), LockPatternUtil.dip2px(this, 180));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(rlRate, 0, -rlRate.getHeight());
                break;
            case R.id.tv_money_fee:
                // 点击时，弹出Popupwindow
                TextView textView = new TextView(this);
                textView.setText("转让手续费为转让成功金额的" + transferFeeRate2 + "，从每笔转让成功的回款中扣取。");
                textView.setTextColor(getResources().getColor(R.color.bg_two_black));
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(10,10,10,10);
                textView.setBackgroundResource(R.drawable.shape_jiaxi_popbg);
                pop = new PopupWindow(textView, LockPatternUtil.dip2px(this, 180), LockPatternUtil.dip2px(this, 70));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(tvMoneyFee, -LockPatternUtil.dip2px(this, 40), 10);
                break;
            case R.id.iv_check_xieyi:
                isXieYiChecked = !isXieYiChecked;
                if (isXieYiChecked) {//勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_ok);
                } else {//不勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_no);
                }
                break;
            case R.id.tv_xieyi_jinzhi:
                Intent intent = new Intent(this, NomalWebviewActivity.class);
                intent.putExtra("url", ConstantUtils.TOAGREEFORBID_URL);
                startActivity(intent);
                break;
            case R.id.tv_xieyi:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                if (!isXieYiChecked) {// 没有勾选协议
                    XieYiDialog xieYiDialog = new XieYiDialog(this, R.style.YzmDialog, tvXieyi.getText().toString().trim());
                    xieYiDialog.show();
                    return;
                }
                ZhuanRangDialog zhuanRangDialog = new ZhuanRangDialog(this, R.style.YzmDialog, id, holdMoney, discountRate, from);
                zhuanRangDialog.show();

                break;
        }
    }

    private ListView initListview() {
        ListView listView = new ListView(this);
        // 去掉点击效果
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去掉分割线
        listView.setDividerHeight(0);
        // 去掉滑动块
        listView.setVerticalScrollBarEnabled(false);
        // 设置背景
        listView.setBackgroundResource(R.drawable.zr_bg);
        // 设置Adapter
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.items_zr, R.id.tv_day, list_day));
        // 设置条目点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());

        return listView;
    }

    // 根据点击选择投资天数
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtils.i("点击条目了：" + list_day[position]);
            // 选中天数后弹窗消失
            if (pop != null) {
                pop.dismiss();
            }

            discountRate = list_day[position];
            //选择折让率后赋值，并计算转让价格
            tvRateZhe.setText(list_day[position] + "%");
            BigDecimal moneyFinal = new BigDecimal(holdMoney).multiply(BigDecimal.ONE.subtract((new BigDecimal(list_day[position]).divide(new BigDecimal("100"))))).setScale(2,BigDecimal.ROUND_HALF_UP);
            tvMoneyZhe.setText("转让价格约为：" + moneyFinal + "元");
        }
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("type", "1");//1： 收益中  2 已结清
        map.put("investRecordId", id);
        map.put("proCode", from);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.TRANSFERPAGEINFO_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.longStr("散标-出借记录列表===", string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign,
                                        datastr);
                                if (isSuccess) {// 验签成功

                                    JSONObject data = JSON.parseObject(datastr);

                                    //债权名称
                                    String borrowName = data.getString("borrowName");
                                    if("scatter".equals(from)){
                                        tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                    }
                                    tvName.setText(borrowName);
                                    //借款期限
                                    String borrowDays = data.getString("borrowDays");
                                    tvJiekuan.setText("借款期限：" + borrowDays + "天");
                                    //基础结算利率
                                    String baseRate = data.getString("baseRate");
                                    tvRate.setText("基础结算利率：" + baseRate + "%");
                                    //剩余期限
                                    String remainDays = data.getString("remainDays");
                                    tvShengyu.setText("剩余期限：" + remainDays + "天");
                                    //到期时间
                                    String planDeadlineStr = data.getString("planDeadlineStr");
                                    tvDaoqi.setText("到期时间：" + planDeadlineStr);
                                    //转让金额
                                    holdMoney = data.getString("holdMoney");
                                    tvMoney.setText(holdMoney + "元");
                                    tvMoneyZhe.setText("转让价格约为：" + holdMoney + "元");
                                    //手续费(小数形式)
                                    String transferFeeRate1 = data.getString("transferFeeRate1");
                                    BigDecimal fee = new BigDecimal(holdMoney).multiply(new BigDecimal(transferFeeRate1)).setScale(2, BigDecimal.ROUND_HALF_UP);
                                    tvMoneyFee.setText(fee + "元");
                                    //手续费(整数形式)
                                    transferFeeRate2 = data.getString("transferFeeRate2");
                                    //list
                                    JSONArray discountRateList = data.getJSONArray("discountRateList");
                                    if (discountRateList.size() > 0) {
                                        list_day = new String[discountRateList.size()];
                                        for (int i = 0; i < discountRateList.size(); i++) {
                                            JSONObject jsonObj = (JSONObject) discountRateList.get(i);
                                            String discountRate = jsonObj.getString("discountRate");
                                            list_day[i] = discountRate;
                                        }
                                    }
                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

}
