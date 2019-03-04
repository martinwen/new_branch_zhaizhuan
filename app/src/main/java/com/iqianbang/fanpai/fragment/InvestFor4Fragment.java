package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.home.IntroduceActivity;
import com.iqianbang.fanpai.activity.invest.FtMinBindBankActivity;
import com.iqianbang.fanpai.activity.invest.FtMinBuyActivity;
import com.iqianbang.fanpai.activity.invest.FtMinRateActivity;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WenGuangJun on 2017/3/11.
 */

public class InvestFor4Fragment extends BaseFragment {
    @BindView(R.id.tv_shouyi_add)
    TextView tvShouyiAdd;
    @BindView(R.id.tv_invest_min)
    TextView tvInvestMin;
    @BindView(R.id.tv_invest_max)
    TextView tvInvestMax;
    @BindView(R.id.tv_shouyi_xiangqing)
    TextView tvShouyiXiangqing;
    @BindView(R.id.tv_shouyi_base)
    TextView tvShouyiBase;
    @BindView(R.id.tv_invest_shengyu)
    TextView tvInvestShengyu;
    @BindView(R.id.iv_invest)
    TextView ivInvest;

    private CustomProgressDialog progressdialog;

    @Override
    protected View initView() {
        LogUtils.i("走饭盒的initView");
        View view = View.inflate(mActivity, R.layout.fragment_invest_4, null);
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
            LogUtils.i("走饭盒的setUserVisibleHint");
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
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FHINVESTINFO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("饭盒出借界面===" + string);

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

                                    // 饭盒基础年化收益率
                                    String fhBaseRate = data.getString("fhBaseRate");
                                    tvShouyiBase.setText(fhBaseRate + "%");

                                    // 饭盒加息收益率
                                    String fhAddRate = data.getString("fhAddRate");
                                    tvShouyiAdd.setText("+" + fhAddRate + "%");

                                    // 饭盒项目余额
                                    String fhBorrowSalableBal = data.getString("fhBorrowSalableBal");
                                    tvInvestShengyu.setText(fhBorrowSalableBal + "元");

                                    if (TextUtils.isEmpty(token)) {//没有登录显示登录按钮
                                        ivInvest.setText("登录");
                                    } else {
                                        if ("0.00".equals(fhBorrowSalableBal) || "0".equals(fhBorrowSalableBal)) {
                                            ivInvest.setText("已售罄");
                                            ivInvest.setBackgroundResource(R.drawable.shape_bg_btn_gray);
                                            ivInvest.setClickable(false);
                                        } else {
                                            ivInvest.setText("立即出借");
                                        }
                                    }


                                    // 饭盒最低出借
                                    String fhInvsetSalableBal = data.getString("fhInvsetSalableBal");
                                    tvInvestMin.setText(fhInvsetSalableBal + "元");

                                    // 饭盒单笔最大出借额
                                    String fhMaxInvestAmount = data.getString("fhMaxInvestAmount");

                                    // 每人限额
                                    String fhBalMaxAmount = data.getString("fhBalMaxAmount");
                                    tvInvestMax.setText(fhBalMaxAmount + "万元");

                                    // 是否新手
                                    boolean isNewUser = data.getBoolean("isNewUser");

                                    // 优先购悬浮窗显示开关
                                    boolean fhYxgSalableSwitch = data.getBoolean("fhYxgSalableSwitch");

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

    @OnClick({R.id.tv_shouyi_xiangqing,
            R.id.iv_invest, R.id.iv_mintomax})
    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.tv_shouyi_xiangqing:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {//登录
                    startActivity(new Intent(mActivity, FtMinRateActivity.class));
                }
                break;
            case R.id.iv_invest:
                if (TextUtils.isEmpty(token)) {//未登录
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {//登录
                    isNeedEvaluattion();
                }
                break;
            case R.id.iv_mintomax:
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", HomeFragment.FLAG_GOTOBIGTUAN);
                startActivity(intent);
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
                                            //小饭团买入第一步
                                            requestData();
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

    private void requestData() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("type", "fh");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GOTOINVEST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("立即出借=买入第一步==" + string);
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

                                    // 真实姓名
                                    String realName = data.getString("realName");

                                    // 身份证号
                                    String idNo = data.getString("idNo");

                                    // 银行名称
                                    String bankName = data.getString("bankName");

                                    // 银行编号
                                    String bankCode = data.getString("bankCode");

                                    // 银行卡号
                                    String cardNum = data.getString("cardNum");

                                    // 银行卡预留手机号
                                    String cardPhone = data.getString("cardPhone");

                                    // 账户余额
                                    String baseBal = data.getString("baseBal");

                                    // 项目余额
                                    String borrowSalableBal = data.getString("borrowSalableBal");

                                    // 单笔最小出借金额（最低出借）
                                    String minInvestAmount = data.getString("minInvestAmount");

                                    // 单笔最大出借金额（单笔限额）,后台返回的数据单位为元，实际前端需要的是万元，所以转了单位
                                    String maxInvestAmount = data.getBigDecimal("maxInvestAmount").divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).toPlainString();

                                    // 单人最大出借金额（单人限额）,后台返回的数据单位为元，实际前端需要的是万元，所以转了单位
                                    String fhBalMaxAmount = data.getBigDecimal("fhBalMaxAmount").divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).toPlainString();

                                    // 奖金余额
                                    String rewardAcctBal = data.getString("rewardAcctBal");
                                    CacheUtils.putString("rewardAcctBal", rewardAcctBal);

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");

                                    //银行单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    //银行单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    // 是否新手标
                                    Boolean isNewUser = data.getBoolean("isNewUser");

                                    //3:换卡（身份证号和姓名不允许输入），4新绑卡
                                    String isEnable = data.getString("isEnable");

                                    //类型：同参数
                                    String type = data.getString("type");

                                    // 是否绑卡
                                    Boolean isBindCard = data.getBoolean("isBindCard");
                                    if (isBindCard) {//已经绑卡
                                        Intent intent = new Intent(mActivity, FtMinBuyActivity.class);
                                        intent.putExtra("baseBal", baseBal);
                                        intent.putExtra("borrowSalableBal", borrowSalableBal);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("isNewUser", isNewUser);
                                        intent.putExtra("minInvestAmount", minInvestAmount);
                                        intent.putExtra("maxInvestAmount", maxInvestAmount);
                                        intent.putExtra("fhBalMaxAmount", fhBalMaxAmount);
                                        startActivity(intent);
                                    } else {//未绑卡
                                        Intent intent = new Intent(mActivity, FtMinBindBankActivity.class);
                                        intent.putExtra("realName", realName);
                                        intent.putExtra("idNo", idNo);
                                        intent.putExtra("bankName", bankName);
                                        intent.putExtra("bankCode", bankCode);
                                        intent.putExtra("cardNum", cardNum);
                                        intent.putExtra("cardPhone", cardPhone);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("isNewUser", isNewUser);
                                        intent.putExtra("isEnable", isEnable);
                                        intent.putExtra("minInvestAmount", minInvestAmount);
                                        intent.putExtra("maxInvestAmount", maxInvestAmount);
                                        intent.putExtra("fhBalMaxAmount", fhBalMaxAmount);
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
                    }
                });
    }
}