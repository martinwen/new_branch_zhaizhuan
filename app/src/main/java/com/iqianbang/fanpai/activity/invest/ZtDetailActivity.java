package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CePingAgainDialog;
import com.iqianbang.fanpai.view.dialog.CePingChanceOverDialog;
import com.iqianbang.fanpai.view.dialog.CePingNotDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.IKnowDialog;
import com.iqianbang.fanpai.view.dialog.ProjectCalculatorDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZtDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_rate_base)
    TextView tvRateBase;
    @BindView(R.id.tv_time_shengyu)
    TextView tvTimeShengyu;
    @BindView(R.id.tv_money_shengyu)
    TextView tvMoneyShengyu;
    @BindView(R.id.tv_rate_zhe)
    TextView tvRateZhe;
    @BindView(R.id.tv_money_zhe)
    TextView tvMoneyZhe;
    @BindView(R.id.tv_jixi)
    TextView tvJixi;
    @BindView(R.id.tv_rule)
    TextView tvRule;
    @BindView(R.id.tv_method)
    TextView tvMethod;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_zhuanrang)
    TextView tvZhuanrang;
    @BindView(R.id.rl_zhaiquan)
    RelativeLayout rlZhaiquan;
    @BindView(R.id.iv_calculation)
    ImageView ivCalculation;
    @BindView(R.id.bt_buy)
    Button btBuy;


    private CustomProgressDialog progressdialog;
    private String transferId;
    private ProjectCalculatorDialog projectCalculatorDialog;
    private String bid;
    private String baseRate;
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zt_detail);
        progressdialog = new CustomProgressDialog(this, "买入确认中...");
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //界面填充数据
        transferId = getIntent().getStringExtra("transferId");
        getDataFromServer();

    }

    private void getDataFromServer() {
        progressdialog.show();
        String token = CacheUtils.getString("token", "");
        Map<String, String> map = SortRequestData.getmap();
        map.put("transferId", transferId);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWTRANSFERPRODETAILINFO_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("转让专区-产品详情==" + string);
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

                                    // 产品名称
                                    String borrowName = data.getString("borrowName");
                                    tvName.setText(borrowName);

                                    // 基础利率
                                    baseRate = data.getString("baseRate");
                                    tvRateBase.setText(baseRate + "%");

                                    // 剩余期限
                                    term = data.getString("term");
                                    tvTimeShengyu.setText(term + "天");

                                    // 剩余金额
                                    String borrowSalableBal = data.getString("borrowSalableBal");
                                    tvMoneyShengyu.setText(borrowSalableBal + "元");

                                    // 折让率
                                    String discountRate = data.getString("discountRate");
                                    tvRateZhe.setText(discountRate + "%");

                                    // 折后价格
                                    String discountAmount = data.getString("discountAmount");
                                    tvMoneyZhe.setText(discountAmount + "元");

                                    // 计息方式
                                    String calculateInterestTpyeStr = data.getString("calculateInterestTpyeStr");
                                    tvJixi.setText(calculateInterestTpyeStr);
                                    // 承接规则
                                    String minTransAmountStr = data.getString("minTransAmountStr");
                                    tvRule.setText(minTransAmountStr);
                                    // 回款方式
                                    String returnMoneyTypeStr = data.getString("returnMoneyTypeStr");
                                    tvMethod.setText(returnMoneyTypeStr);
                                    // 付息日期
                                    String monthRepayDayStr = data.getString("monthRepayDayStr");
                                    tvDay.setText(monthRepayDayStr);
                                    // 转让条件
                                    String transferConditionStr = data.getString("transferConditionStr");
                                    tvZhuanrang.setText(transferConditionStr);

                                    // 债权id
                                    bid = data.getString("bid");

                                    // settleSwitch为true代表结算中，为false的时候再看fwSalableSwitch 为false 代表产品关闭
                                    Boolean settleSwitch = data.getBoolean("settleSwitch");
                                    Boolean transferSalableSwitch = data.getBoolean("transferSalableSwitch");

                                    if (settleSwitch) {
                                        btBuy.setText("结算时间，暂不支持承接");
                                    } else {
                                        if (!transferSalableSwitch) {
                                            btBuy.setText("暂不支持承接");
                                        } else {
                                            btBuy.setEnabled(true);
                                            btBuy.setText("立即承接");
                                            btBuy.setBackgroundColor(getResources().getColor(R.color.text_red_dark));
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

    @OnClick({R.id.iv_back, R.id.rl_zhaiquan, R.id.iv_calculation, R.id.bt_buy})
    public void onViewClicked(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_zhaiquan:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(this, LoginActivity.class));
                } else {//登录
                    Intent intent = new Intent(this, BidInfoActivity.class);
                    intent.putExtra("bid", bid);
                    startActivity(intent);
                }
                break;
            case R.id.iv_calculation:
                if (null == projectCalculatorDialog) {
                    projectCalculatorDialog = new ProjectCalculatorDialog(this);
                    projectCalculatorDialog.setDeadline(new BigDecimal(term));
                    projectCalculatorDialog.setRate(new BigDecimal(baseRate).divide(new BigDecimal(100)));
                    projectCalculatorDialog.show();
                } else {
                    projectCalculatorDialog.show();
                }
                break;
            case R.id.bt_buy:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(this, LoginActivity.class));
                } else {//登录

                    isNeedEvaluattion();
                }
                break;
        }
    }

    private void isNeedEvaluattion() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GETEVALUATIONINFOSTATUS_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("评测结果===" + string);
                        // TODO Auto-generated method stub
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

                                    // 是否测评
                                    Boolean hasEvln = data.getBoolean("hasEvln");
                                    // 是否可出借
                                    Boolean canInvest = data.getBoolean("canInvest");
                                    // 测评剩余次数
                                    String overTimes = data.getString("overTimes");
                                    if (hasEvln) {//评测过
                                        if (canInvest) {//评测过并可出借

                                            gotoInvest();
                                        } else {//评测过不可出借
                                            if (StrToNumber.strTodouble(overTimes) > 0) {//还有评测次数
                                                CePingAgainDialog cePingAgainDialog = new CePingAgainDialog(ZtDetailActivity.this, R.style.YzmDialog, overTimes);
                                                cePingAgainDialog.show();
                                            } else {//没有评测次数
                                                CePingChanceOverDialog cePingChanceOverDialog = new CePingChanceOverDialog(ZtDetailActivity.this, R.style.YzmDialog);
                                                cePingChanceOverDialog.show();
                                            }
                                        }
                                    } else {
                                        CePingNotDialog cePingNotDialog = new CePingNotDialog(ZtDetailActivity.this, R.style.YzmDialog);
                                        cePingNotDialog.show();
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
                    }
                });
    }

    protected void gotoInvest() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("transferId", transferId);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.TRANSFER_GOTOINVEST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.longStr("转让专区买入第一步==", string);
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
                                    // 是否绑卡
                                    Boolean isBindCard = data.getBoolean("isBindCard");
                                    if (isBindCard) {//已经绑卡
                                        Intent intent = new Intent(ZtDetailActivity.this, ZhiTouBuyActivity.class);
                                        intent.putExtra("jsonData", datastr);
                                        startActivity(intent);
                                    } else {//未绑卡
                                        Intent intent = new Intent(ZtDetailActivity.this, ZhiTouBindBankActivity.class);
                                        intent.putExtra("jsonData", datastr);
                                        startActivity(intent);
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
