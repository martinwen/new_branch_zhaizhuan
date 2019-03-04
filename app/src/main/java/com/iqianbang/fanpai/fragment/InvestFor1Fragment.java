package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.home.IntroduceActivity;
import com.iqianbang.fanpai.activity.invest.FanTongBuyActivity;
import com.iqianbang.fanpai.activity.invest.FanTongQuestionActivity;
import com.iqianbang.fanpai.activity.invest.FtJingYingRateActivity;
import com.iqianbang.fanpai.activity.invest.JiLuActivity;
import com.iqianbang.fanpai.activity.invest.WenTiActivity;
import com.iqianbang.fanpai.activity.invest.XiangMuActivity;
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
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WenGuangJun on 2017/3/11.
 */

public class InvestFor1Fragment extends BaseFragment {

    @BindView(R.id.tv_invest_min)
    TextView tvInvestMin;
    @BindView(R.id.tv_invest_max)
    TextView tvInvestMax;
    @BindView(R.id.tv_shouyi_base)
    TextView tvShouyiBase;
    @BindView(R.id.tv_shouyi_xiangqing)
    TextView tvShouyiXiangqing;
    @BindView(R.id.tv_invest_shengyu)
    TextView tvInvestShengyu;
    @BindView(R.id.iv_invest)
    TextView ivInvest;
    @BindView(R.id.tv_method)
    TextView tvMethod;

    private CustomProgressDialog progressdialog;
    private String bookStatus;
    private boolean isBuyDay;
    private String bookAmount;

    @Override
    protected View initView() {
        LogUtils.i("走饭桶的initView");
        View view = View.inflate(mActivity, R.layout.fragment_invest_1, null);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 优点：在此请求数据,实现了数据的懒加载
     * 缺点：一次仍是三个Framgment对象，不是完全意义的懒加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            LogUtils.i("走饭桶的setUserVisibleHint");
            String token = CacheUtils.getString("token", "");
            // 访问网络，初始化默认值
            getDataFromServer(token);
        }
    }

    @Override
    public void initData() {
        progressdialog = new CustomProgressDialog(mActivity, "数据加载中...");
        String token = CacheUtils.getString("token", "");

        // 访问网络，初始化默认值
        getDataFromServer(token);
    }

    private void getDataFromServer(final String token) {
        Map<String, String> map = SortRequestData.getmap();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FTINVESTINFO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("饭桶出借界面===" + string);

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

                                    // 饭桶年华收益
                                    String ftYearRate = data.getString("ftYearRate");
                                    tvShouyiBase.setText(ftYearRate + "%");

                                    // 饭桶项目余额
                                    String ftBorrowSalableBal = data.getString("ftBorrowSalableBal");
                                    tvInvestShengyu.setText(ftBorrowSalableBal + "元");

                                    // 饭桶最低出借
                                    String ftSingleMinInvestAmount = data.getString("ftSingleMinInvestAmount");
                                    tvInvestMin.setText(ftSingleMinInvestAmount + "万元");

                                    // 出借模式
                                    String lendingMethod = data.getString("lendingMethod");
                                    tvMethod.setText(lendingMethod);

                                    // 单笔限额
                                    String ftSingleMaxInvestAmount = data.getString("ftSingleMaxInvestAmount");
                                    tvInvestMax.setText(ftSingleMaxInvestAmount + "万元");

                                    // 0未预约，1待审核，2预约成功
                                    bookStatus = data.getString("bookStatus");

                                    // 是否到预约买入时间
                                    isBuyDay = data.getBoolean("isBuyDay");

                                    // 预约金额
                                    bookAmount = data.getString("bookAmount");

                                    // 是否显示敬请期待
                                    boolean ftInvestSwitch = data.getBoolean("ftInvestSwitch");

                                    if (TextUtils.isEmpty(token)) {//没有登录显示登录按钮
                                        ivInvest.setText("登录");
                                    } else {
                                        if (ftInvestSwitch) {

                                            if ("0".equals(bookStatus)) {
                                                ivInvest.setText("预约购买");
                                            } else if ("1".equals(bookStatus)) {
                                                ivInvest.setText("已预约");
                                                ivInvest.setBackgroundResource(R.drawable.shape_bg_btn_gray);
                                                ivInvest.setClickable(false);
                                            } else if ("2".equals(bookStatus)) {
                                                if (isBuyDay) {
                                                    if ("0.00".equals(ftBorrowSalableBal) || "0".equals(ftBorrowSalableBal)) {
                                                        ivInvest.setText("已售罄");
                                                        ivInvest.setBackgroundResource(R.drawable.shape_bg_btn_gray);
                                                        ivInvest.setClickable(false);
                                                    } else {
                                                        ivInvest.setText("立即出借");
                                                    }
                                                } else {
                                                    ivInvest.setText("已预约");
                                                    ivInvest.setBackgroundResource(R.drawable.shape_bg_btn_gray);
                                                    ivInvest.setClickable(false);
                                                }
                                            }
                                        } else {
                                            tvInvestShengyu.setText("0.00元");
                                            ivInvest.setText("敬请期待");
                                            ivInvest.setClickable(false);
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
                        ToastUtils.toastshort("网络连接失败，请检查网络");
                    }
                });
    }

    @OnClick({R.id.tv_shouyi_xiangqing, R.id.iv_invest})

    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.tv_shouyi_xiangqing:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {//登录
                    startActivity(new Intent(mActivity, FtJingYingRateActivity.class));
                }
                break;
            case R.id.iv_invest:
                if (TextUtils.isEmpty(token)) {//未登录
                    startActivity(new Intent(mActivity, LoginActivity.class));
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
                                            // bookStatus 0未预约，1待审核，2预约成功
                                            if ("0".equals(bookStatus)) {
                                                //走预约方法
                                                requestBook();

                                            } else if ("2".equals(bookStatus)) {
                                                if (isBuyDay) {
                                                    //走买入方法
                                                    requestBuy();
                                                }
                                            }
                                        } else {//评测过不可出借
                                            if (StrToNumber.strTodouble(overTimes) > 0) {//还有评测次数
                                                CePingAgainDialog cePingAgainDialog = new CePingAgainDialog(mActivity, R.style.YzmDialog, overTimes);
                                                cePingAgainDialog.show();
                                            } else {//没有评测次数
                                                CePingChanceOverDialog cePingChanceOverDialog = new CePingChanceOverDialog(mActivity, R.style.YzmDialog);
                                                cePingChanceOverDialog.show();
                                            }
                                        }
                                    } else {
                                        CePingNotDialog cePingNotDialog = new CePingNotDialog(mActivity, R.style.YzmDialog);
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

    private void requestBook() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FTBOOKVERIFYINFO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("饭桶预约校验===" + string);
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

                                    // 最小预约金额
                                    String minBookAmount = data.getString("minBookAmount");
                                    // 最大预约金额
                                    String maxBookAmount = data.getString("maxBookAmount");
                                    // 最低出借
                                    String minInvestAmount = data.getString("minInvestAmount");


                                    Intent intent = new Intent(mActivity, FanTongQuestionActivity.class);
                                    intent.putExtra("minBookAmount", minBookAmount);
                                    intent.putExtra("maxBookAmount", maxBookAmount);
                                    startActivity(intent);

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

    private void requestBuy() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FTINVESTSTEP1_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("饭桶预约成功后买入===" + string);
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

                                    // 账户余额
                                    String baseAcctBal = data.getString("baseAcctBal");

                                    // 饭盒资产
                                    String fhAcctBal = data.getString("fhAcctBal");

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");

                                    // 预约金额
                                    bookAmount = data.getString("bookAmount");

                                    // 最低出借
                                    String minInvestAmount = data.getString("minInvestAmount");


                                    Intent intent = new Intent(mActivity, FanTongBuyActivity.class);
                                    intent.putExtra("seqNo", seqNo);
                                    intent.putExtra("baseAcctBal", baseAcctBal);
                                    intent.putExtra("fhAcctBal", fhAcctBal);
                                    intent.putExtra("bookAmount", bookAmount);
                                    intent.putExtra("minInvestAmount", minInvestAmount);
                                    startActivity(intent);

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
}
