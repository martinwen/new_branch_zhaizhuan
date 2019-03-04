package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_zong;


public class FtMinBuyYsgActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_shengyu)
    TextView tvShengyu;
    @BindView(R.id.tv_buy)
    TextView tvBuy;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.iv_check_zhanghu)
    ImageView ivCheckZhanghu;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.iv_check_jiang)
    ImageView ivCheckJiang;
    @BindView(R.id.iv_check_xieyi)
    ImageView ivCheckXieyi;
    @BindView(R.id.iv_xieyi)
    ImageView ivXieyi;
    @BindView(tv_zong)
    TextView tvZong;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private String seqNo;
    private String jiangjin;
    private String baseBal;//账户余额
    private String buyMoney;//固定的优先购买入金额
    private boolean isJiangJinChecked = true;// 奖金没有勾上
    private boolean isZhangHuChecked = false;// 账户余额没有勾上
    private boolean isXieYiChecked = true;// 协议已经勾上
    private String minInvestAmount;//最低出借

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_yxg);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "买入确认中...");
        initData();
    }

    private void initData() {
        //从买入第一步中拿到交易流水号
        Intent intent = getIntent();
        seqNo = intent.getStringExtra("seqNo");

        //从买入第一步中拿到当前剩余金额
        String borrowSalableBal = intent.getStringExtra("borrowSalableBal");
        tvShengyu.setText(borrowSalableBal+"元");

        //从买入第一步中拿到最低出借
        minInvestAmount = intent.getStringExtra("minInvestAmount");

        //优先购金额
        buyMoney = intent.getStringExtra("amount");
        tvBuy.setText(buyMoney + "（优先购）");

        //账户余额
        baseBal = intent.getStringExtra("baseBal");
        tvZhanghu.setText("账户余额：" + baseBal);

        //获取到奖金
        jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
        tvJiang.setText(MathUtil.subString(jiangjin, 2) + "");


        if (!TextUtils.isEmpty(buyMoney)) {

            //根据输入的金额来判断是否勾选账户余额
            if (StrToNumber.strTodouble(buyMoney) > 0) {
                ivCheckZhanghu.setImageResource(R.drawable.bind_check_ok);
                isZhangHuChecked = true;
            } else {
                ivCheckZhanghu.setImageResource(R.drawable.bind_check_no);
                isZhangHuChecked = false;
            }

            //根据输入的金额来判断是否勾选奖金
            if (StrToNumber.strTodouble(buyMoney) >=
                    StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))) {
                ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                // 买入总计
                tvZong.setText(MathUtil.subDouble((StrToNumber.strTodouble(buyMoney)
                        + StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))), 2) + "");
                //勾选，获取奖金值
                jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
            } else {//不勾选奖金
                ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                // 买入总计
                tvZong.setText(MathUtil.subDouble(StrToNumber.strTodouble(buyMoney), 2) + "");
                //不勾选，把奖金改为0
                jiangjin = "0.00";
            }

        } else {
            // 买入总计
            tvZong.setText("");
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_check_zhanghu, R.id.iv_check_jiang, R.id.iv_check_xieyi, R.id.iv_xieyi, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_check_zhanghu:
                if (StrToNumber.strTodouble(baseBal) > 0) {
                    isZhangHuChecked = !isZhangHuChecked;
                    if (isZhangHuChecked) {//勾选账户余额
                        ivCheckZhanghu.setImageResource(R.drawable.bind_check_ok);
                        if (StrToNumber.strTodouble(buyMoney) == 0) {//没有输入买入金额时点击账户余额，并且账户余额不为0
                            buyMoney = baseBal;
                            tvBuy.setText(buyMoney);
                        }
                    } else {//不勾选账户余额
                        ivCheckZhanghu.setImageResource(R.drawable.bind_check_no);
                    }
                }
                break;
            case R.id.iv_check_jiang:
                if (StrToNumber.strTodouble(buyMoney) >=
                        StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))) {
                    isJiangJinChecked = !isJiangJinChecked;
                    if (isJiangJinChecked) {//勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                        //买入总计
                        tvZong.setText(MathUtil.subDouble((StrToNumber.strTodouble(buyMoney) +
                                StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))), 2) + "");
                        //勾选，获取奖金值
                        jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                        //买入总计
                        tvZong.setText(MathUtil.subDouble(StrToNumber.strTodouble(buyMoney), 2) + "");
                        //不勾选，把奖金改为0
                        jiangjin = "0.00";
                    }
                }
                break;
            case R.id.iv_check_xieyi:
                isXieYiChecked = !isXieYiChecked;
                if (isXieYiChecked) {//勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_ok);
                } else {//不勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_no);
                }
                break;
            case R.id.iv_xieyi:
                startActivity(new Intent(this, InvestXieYiActivity.class));
                break;
            case R.id.tv_btn_finish:
                if (!TextUtils.isEmpty(buyMoney)) {
                    if (StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)) {
                        ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的出借金额");
                        return;
                    }
                    if (!isXieYiChecked) {
                        ToastUtils.toastshort("请同意出借协议");
                        return;
                    }

                    getDataFromServer(buyMoney, jiangjin, seqNo);

                } else {
                    ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的出借金额");
                }
                break;
        }
    }

    private void getDataFromServer(final String buyMoney, final String jiangjin, final String seqNo) {
        //获取uuid
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        // 获取UUID 调用买入接口
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(
                ConstantUtils.GETUUID_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {// 验签成功
                                    Map<String, String> map = SortRequestData.getmap();
                                    String token = CacheUtils.getString("token", "");
                                    map.put("token", token);
                                    map.put("uuid", datastr);
                                    map.put("amount", buyMoney);
                                    map.put("reward", jiangjin);
                                    map.put("isUsedBal", isZhangHuChecked ? "1" : "0");
                                    map.put("seqNo", seqNo);
                                    String requestData = SortRequestData.sortString(map);
                                    String signData = SignUtil.sign(requestData);
                                    map.put("sign", signData);
                                    VolleyUtil.sendJsonRequestByPost(ConstantUtils.CONFIRMINVEST_URL, null, map,
                                            new HttpBackBaseListener() {

                                                @Override
                                                public void onSuccess(String string) {
                                                    LogUtils.i("饭盒立即买入===" + string);
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
                                                                CacheUtils.putString("realName", realName);

                                                                // 身份证号
                                                                String idNo = data.getString("idNo");
                                                                CacheUtils.putString("idNo", idNo);

                                                                // 银行卡号
                                                                String cardNum = data.getString("cardNum");
                                                                CacheUtils.putString("cardNum", cardNum);

                                                                // 开户行图标
                                                                String bankPic = data.getString("bankPic");
                                                                CacheUtils.putString("bankPic", bankPic);

                                                                //银行名称
                                                                String bankName = data.getString("bankName");
                                                                CacheUtils.putString("bankName", bankName);

                                                                //单笔限额
                                                                String singleTransLimit = data.getString("singleTransLimit");
                                                                CacheUtils.putString("singleTransLimit", singleTransLimit);

                                                                //单日限额
                                                                String singleDayLimit = data.getString("singleDayLimit");
                                                                CacheUtils.putString("singleDayLimit", singleDayLimit);

                                                                // 使用余额
                                                                String baseBal = data.getString("baseBal");

                                                                // 充值金额
                                                                String rechargeAmount = data.getString("rechargeAmount");

                                                                // 买入总计
                                                                String investTotalAmount = data.getString("investTotalAmount");

                                                                // 银行预留手机号
                                                                String cardPhone = data.getString("cardPhone");
                                                                CacheUtils.putString("cardPhone", cardPhone);

                                                                // 交易流水号
                                                                String seqNo = data.getString("seqNo");

                                                                // 是否余额支付
                                                                Boolean isBalPay = data.getBoolean("isBalPay");
                                                                //若买入金额小于等于账户余额，则直接进入“买入成功”页面
                                                                if (isBalPay) {
                                                                    Intent intent2 = new Intent(FtMinBuyYsgActivity.this, FtMinBuySuccessActivity.class);
                                                                    intent2.putExtra("zong_money", investTotalAmount);
                                                                    startActivity(intent2);
                                                                } else {//若买入金额大于账户余额，进入“买入确认”页面
                                                                    finish();
                                                                    Intent intent3 = new Intent(FtMinBuyYsgActivity.this, FtMinBuyConfirmActivity.class);
                                                                    intent3.putExtra("realName", realName);
                                                                    intent3.putExtra("idNo", idNo);
                                                                    intent3.putExtra("cardNum", cardNum);
                                                                    intent3.putExtra("et_buy_money", baseBal);// 使用余额
                                                                    intent3.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                                                    intent3.putExtra("et_jiangjin_money", jiangjin);
                                                                    intent3.putExtra("zong_money", investTotalAmount);
                                                                    intent3.putExtra("cardPhone", cardPhone);
                                                                    intent3.putExtra("seqNo", seqNo);
                                                                    startActivity(intent3);
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
                                                    ToastUtils.toastshort("网络请求失败");
                                                }
                                            });

                                } else {
                                    progressdialog.dismiss();
                                    ToastUtils.toastshort("买入失败！");
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
                        ToastUtils.toastshort("网络请求失败");
                    }
                });
    }
}
