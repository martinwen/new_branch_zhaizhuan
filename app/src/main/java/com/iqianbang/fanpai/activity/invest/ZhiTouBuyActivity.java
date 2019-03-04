package com.iqianbang.fanpai.activity.invest;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.NomalWebviewActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.BuyAllDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.JiangJinDialog;
import com.iqianbang.fanpai.view.dialog.XieYiDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhiTouBuyActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_shengyu)
    TextView tvShengyu;
    @BindView(R.id.tv_rate_year)
    TextView tvRateYear;
    @BindView(R.id.tv_rate_zhe)
    TextView tvRateZhe;
    @BindView(R.id.et_buy)
    EditText etBuy;
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
    @BindView(R.id.tv_xieyi_jinzhi)
    TextView tvXieyiJinzhi;
    @BindView(R.id.tv_xieyi_zhuanrang)
    TextView tvXieyiZhuanrang;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private String seqNo;
    private String jiangjin;
    private double useJiangjin;
    private boolean isJiangJinChecked = false;// 奖金没有勾上
    private boolean isXieYiChecked = false;// 协议已经勾上
    private String discountRate;
    private String minInvestAmount;
    private boolean isZhangHuChecked = true;// 账户余额勾上
    private String baseBal;//账户余额
    private double useBaseBal;//账户余额
    private String buyMoney;//输入的买入金额
    private String actMoney;
    private JSONObject data;
    private String datastr;

    private String borrowSalableBal;
    private double chongzhi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhitou_buy);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();
    }

    private void initData() {

        datastr = getIntent().getStringExtra("jsonData");
        data = JSON.parseObject(datastr);


        //剩余金额
        borrowSalableBal = data.getString("borrowSalableBal");
        tvShengyu.setText(borrowSalableBal + "元");
        //历史年化
        tvRateYear.setText(MathUtil.subString(data.getString("baseRate"), 2) + "%");
        //折让率
        discountRate = data.getString("discountRate");
        tvRateZhe.setText(MathUtil.subString(discountRate, 2) + "%");

        //账户余额
        baseBal = data.getString("baseBal");
        tvZhanghu.setText("账户余额(元)：" + baseBal);
        //获取奖金余额
        jiangjin = data.getString("rewardAcctBal");
        tvJiang.setText(jiangjin + "元");

        //seqNo
        seqNo = data.getString("seqNo");

        //从买入第一步中拿到起投和单笔限额
        minInvestAmount = data.getString("minInvestAmount");
        etBuy.setHint("请输入承接金额，最低承接" + minInvestAmount + "元");

        // 给买入金额设置监听，1.保证两位有效小数，2.为了使奖金使用状态图标根据不同的输入值变化而变化
        etBuy.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etBuy.setText(s);
                        etBuy.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etBuy.setText(s);
                    etBuy.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etBuy.setText(s.subSequence(0, 1));
                        etBuy.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //承接金额
                buyMoney = etBuy.getText().toString().trim();


                if (!TextUtils.isEmpty(buyMoney)) {

                    //计算承接价格
                    actMoney = new BigDecimal(buyMoney).multiply(BigDecimal.ONE.subtract((new BigDecimal(discountRate).divide(new BigDecimal("100"))))).setScale(2, BigDecimal.ROUND_HALF_UP) + "";
                    tvBuy.setText(actMoney);

                    if (StrToNumber.strTodouble(actMoney) >=
                            StrToNumber.strTodouble(data.getString("rewardAcctBal"))) {
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);

                        //勾选，获取奖金值
                        jiangjin = data.getString("rewardAcctBal");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);

                        //不勾选，把奖金改为0
                        jiangjin = "0.00";
                    }

                } else {
                    //计算承接价格
                    actMoney = "0.00";
                    tvBuy.setText(actMoney);
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.iv_check_zhanghu, R.id.iv_check_jiang, R.id.iv_check_xieyi,
              R.id.tv_xieyi_jinzhi, R.id.tv_xieyi_zhuanrang, R.id.tv_btn_finish})

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_check_zhanghu:
                isZhangHuChecked = !isZhangHuChecked;
                if (isZhangHuChecked) {//勾选账户余额
                    ivCheckZhanghu.setImageResource(R.drawable.bind_check_ok);
                } else {//不勾选账户余额
                    ivCheckZhanghu.setImageResource(R.drawable.bind_check_no);
                }
                break;
            case R.id.iv_check_jiang:
                if (StrToNumber.strTodouble(actMoney) >=
                        StrToNumber.strTodouble(data.getString("rewardAcctBal"))) {
                    isJiangJinChecked = !isJiangJinChecked;
                    if (isJiangJinChecked) {//勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                        //勾选，获取奖金值
                        jiangjin = data.getString("rewardAcctBal");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                        //不勾选，把奖金改为0
                        jiangjin = "0.00";
                    }
                } else {
                    if (!TextUtils.isEmpty(buyMoney)) {
                        JiangJinDialog dialog = new JiangJinDialog(ZhiTouBuyActivity.this, R.style.YzmDialog);
                        dialog.show();
                    }
                }
                break;
            case R.id.iv_check_xieyi:
                isXieYiChecked = !isXieYiChecked;
                if (isXieYiChecked) {//勾选协议
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_ok);
                } else {//不勾选协议
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_no);
                }
                break;
            case R.id.tv_xieyi_jinzhi:
                Intent intent = new Intent(this, NomalWebviewActivity.class);
                intent.putExtra("url", ConstantUtils.TOAGREEFORBID_URL);
                startActivity(intent);
                break;
            case R.id.tv_xieyi_zhuanrang:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                buy();
                break;
        }
    }

    private void showConfirmDialog(String buyMoney, String actMoney, double useJiangjin, double useBaseBal) {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_zhitou_confirm, null);
        ImageView iv_cancel = (ImageView) inflate.findViewById(R.id.iv_cancel);
        //出借金额
        TextView tv_money_zong = (TextView) inflate.findViewById(R.id.tv_money_zong);
        tv_money_zong.setText(MathUtil.subString(buyMoney, 2) + "元");

        //使用小饭团
        TextView tv_money_buy = (TextView) inflate.findViewById(R.id.tv_money_buy);
        tv_money_buy.setText(MathUtil.subString(actMoney, 2) + "元");

        //奖金抵减
        TextView tv_money_jiang = (TextView) inflate.findViewById(R.id.tv_money_jiang);
        tv_money_jiang.setText(MathUtil.subDouble(useJiangjin, 2) + "元");

        //使用余额
        TextView tv_money_yue = (TextView) inflate.findViewById(R.id.tv_money_yue);
        tv_money_yue.setText(MathUtil.subDouble(useBaseBal, 2) + "元");

        TextView tv_btn_zhifu = (TextView) inflate.findViewById(R.id.tv_btn_zhifu);

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_btn_zhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromServer();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性,宽度与屏幕同宽
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置Dialog距离底部的距离
        //lp.y = 20;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        //显示对话框
        dialog.show();

    }

    private void buy() {
        if (!isXieYiChecked) {// 没有勾选协议
            XieYiDialog xieYiDialog = new XieYiDialog(this, R.style.YzmDialog, tvXieyiZhuanrang.getText().toString().trim());
            xieYiDialog.show();
            return;
        }
        if (TextUtils.isEmpty(buyMoney)) {// 承接金额为空
            ToastUtils.toastshort("承接金额不能为空");
            return;
        }
        if (StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)) {// 承接金额小于最小最低出借
            ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的承接金额");
            return;
        }
        // 出借金额超出了剩余金额
        if (StrToNumber.strTodouble(borrowSalableBal) < StrToNumber.strTodouble(buyMoney)) {
            ToastUtils.toastshort("承接金额超出了剩余金额");
            return;
        }

//        BigDecimal remainder = new BigDecimal(buyMoney).divideAndRemainder(new BigDecimal("100"))[1];
//        if (remainder.compareTo(BigDecimal.ZERO) != 0) {
//            ToastUtils.toastshort("出借金额必须为100的整数倍");
//            return;
//        }

        // 为避免剩余金额<1000元时其他用户无法出借
        if (StrToNumber.strTodouble(borrowSalableBal) - StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)
                && StrToNumber.strTodouble(borrowSalableBal) != StrToNumber.strTodouble(buyMoney)) {
            BuyAllDialog buyAllDialog = new BuyAllDialog(this, R.style.YzmDialog, minInvestAmount);
            buyAllDialog.show();
            return;
        }

        //计算实际使用的奖金
        if (isJiangJinChecked) {
            if (StrToNumber.strTodouble(actMoney) > StrToNumber.strTodouble(jiangjin)) {
                useJiangjin = StrToNumber.strTodouble(jiangjin);
            } else {
                useJiangjin = StrToNumber.strTodouble(actMoney);
            }
        } else {
            useJiangjin = 0;
        }

        //计算实际使用的账户余额
        if (isZhangHuChecked) {
            if (StrToNumber.strTodouble(actMoney) - useJiangjin > StrToNumber.strTodouble(baseBal)) {
                useBaseBal = StrToNumber.strTodouble(baseBal);
            } else {
                useBaseBal = StrToNumber.strTodouble(actMoney) - useJiangjin;
            }
        } else {
            useBaseBal = 0;
        }

        chongzhi = StrToNumber.strTodouble(actMoney) - useJiangjin - useBaseBal;
        if (chongzhi > 0) {
            getDataFromServer();
        } else {
            showConfirmDialog(buyMoney, actMoney, useJiangjin, useBaseBal);
        }
    }


    private void getDataFromServer() {
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("amount", buyMoney);
        map.put("payAmount", actMoney);
        map.put("reward", useJiangjin + "");
        map.put("seqNo", seqNo);
        map.put("isUsedBal", isZhangHuChecked ? "1" : "0");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.TRANSFER_CONFIRMINVEST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("转入专区买入第二步===" + string);
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

                                    // 银行预留手机号
                                    String cardPhone = data.getString("cardPhone");
                                    CacheUtils.putString("cardPhone", cardPhone);

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


                                    // 买入债权金额
                                    String investAmount = data.getString("investAmount");

                                    // 买入实际支付金额
                                    String payAmount = data.getString("payAmount");

                                    // 奖金
                                    String rewardAcctBal = data.getString("rewardAcctBal");

                                    // 使用余额
                                    String baseBal = data.getString("baseBal");

                                    // 充值金额
                                    String rechargeAmount = data.getString("rechargeAmount");

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");


                                    if (chongzhi > 0) {//需要银行卡充值
                                        Intent intent = new Intent(ZhiTouBuyActivity.this, ZhiTouBuyConfirmActivity.class);
                                        intent.putExtra("investAmount", investAmount);//买入债权金额
                                        intent.putExtra("payAmount", payAmount);//买入实际支付金额
                                        intent.putExtra("rewardAcctBal", rewardAcctBal);//奖金
                                        intent.putExtra("baseBal", baseBal);// 使用余额
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("seqNo", seqNo);
                                        startActivity(intent);
                                    } else {//直接进入“买入成功”页面
                                        Intent intent = new Intent(ZhiTouBuyActivity.this, ZhiTouBuySuccessActivity.class);
                                        intent.putExtra("investAmount", investAmount);
                                        startActivity(intent);
                                    }

                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            if (msg.startsWith("为避免剩余金额")) {
                                BuyAllDialog buyAllDialog = new BuyAllDialog(ZhiTouBuyActivity.this, R.style.YzmDialog, minInvestAmount);
                                buyAllDialog.show();
                            } else {
                                ToastUtils.toastshort(msg);
                            }
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
