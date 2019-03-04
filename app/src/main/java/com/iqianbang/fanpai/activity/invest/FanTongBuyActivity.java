package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.NormalDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_zong;

public class FanTongBuyActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.iv_usezhanghu)
    ImageView ivUsezhanghu;
    @BindView(R.id.iv_useftmin)
    ImageView ivUseftmin;
    @BindView(R.id.et_input_zhanghu)
    EditText etInputZhanghu;
    @BindView(R.id.et_input_ftmin)
    EditText etInputFtmin;
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
    @BindView(R.id.tv_zhanghuyue)
    TextView tvZhanghuyue;
    @BindView(R.id.tv_xiaofantuan)
    TextView tvXiaofantuan;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private boolean isJiangJinChecked = true;// 奖金没有勾上
    private boolean isXieYiChecked = true;// 协议已经勾上
    private String seqNo;
    private String baseAcctBal;//账户余额
    private String fhAcctBal;//饭盒资产
    private String bookAmount;//预约金额
    private String minInvestAmount;//最低出借
    private String jiangjin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantongbuy);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "确认中...");
        initData();
    }

    private void initData() {
        //从买入第一步中拿到交易流水号
        Intent intent = getIntent();
        // 交易流水号
        seqNo = intent.getStringExtra("seqNo");
        //账户余额
        baseAcctBal = intent.getStringExtra("baseAcctBal");
        tvZhanghuyue.setText("可用余额：" + baseAcctBal + "元");
        //小饭团资产
        fhAcctBal = intent.getStringExtra("fhAcctBal");
        tvXiaofantuan.setText("可用余额：" + fhAcctBal + "元");
        //预约金额
        bookAmount = intent.getStringExtra("bookAmount");
        tvMoney.setText(bookAmount);
        //获取奖金余额
        jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
        tvJiang.setText(MathUtil.subString(jiangjin, 2) + "");
        //最低出借
        minInvestAmount = intent.getStringExtra("minInvestAmount");


        // 给账户余额输入框设置监听，为了使奖金使用状态图标根据不同的输入值变化而变化
        etInputZhanghu.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etInputZhanghu.setText(s);
                        etInputZhanghu.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etInputZhanghu.setText(s);
                    etInputZhanghu.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etInputZhanghu.setText(s.subSequence(0, 1));
                        etInputZhanghu.setSelection(1);
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

                String zhanghuMoney = etInputZhanghu.getText().toString().trim();
                String fanheMoney = etInputFtmin.getText().toString().trim();

                if (StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney) >=
                        StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))) {
                    isJiangJinChecked = true;
                    ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                    // 买入总计
                    tvZong.setText(MathUtil.subDouble((StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney)
                            + StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))), 2) + "");
                    //勾选，获取奖金值
                    jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
                } else {//不勾选奖金
                    isJiangJinChecked = false;
                    ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                    // 买入总计
                    tvZong.setText(MathUtil.subDouble(StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney), 2) + "");
                    //不勾选，把奖金改为0
                    jiangjin = "0.00";
                }

            }
        });
        // 给饭盒资产输入框设置监听，为了使奖金使用状态图标根据不同的输入值变化而变化
        etInputFtmin.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etInputFtmin.setText(s);
                        etInputFtmin.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etInputFtmin.setText(s);
                    etInputFtmin.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etInputFtmin.setText(s.subSequence(0, 1));
                        etInputFtmin.setSelection(1);
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

                String fanheMoney = etInputFtmin.getText().toString().trim();
                String zhanghuMoney = etInputZhanghu.getText().toString().trim();

                if (StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney) >=
                        StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))) {
                    isJiangJinChecked = true;
                    ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                    // 买入总计
                    tvZong.setText(MathUtil.subDouble((StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney)
                            + StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))), 2) + "");
                    //勾选，获取奖金值
                    jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
                } else {//不勾选奖金
                    isJiangJinChecked = false;
                    ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                    // 买入总计
                    tvZong.setText(MathUtil.subDouble(StrToNumber.strTodouble(zhanghuMoney) + StrToNumber.strTodouble(fanheMoney), 2) + "");
                    //不勾选，把奖金改为0
                    jiangjin = "0.00";
                }

            }
        });
    }


    @OnClick({R.id.iv_back, R.id.iv_usezhanghu, R.id.iv_useftmin, R.id.iv_check_jiang, R.id.iv_check_xieyi, R.id.iv_xieyi, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_usezhanghu://勾选账户余额
                ivUsezhanghu.setImageResource(R.drawable.jy_buy_usezhanghu_ok);
                ivUseftmin.setImageResource(R.drawable.jy_buy_useftmin_font_no);
                //账户余额大于预约金额，使用账户余额=余额总值
                if (StrToNumber.strTodouble(baseAcctBal) >= StrToNumber.strTodouble(bookAmount)) {
                    etInputZhanghu.setText(bookAmount);
                    etInputFtmin.getText().clear();
                } else {//账户余额小于预约金额，使用账户余额全选
                    etInputZhanghu.setText(baseAcctBal);
                    //预约金额-账户余额>小饭团金额，使用小饭团全选
                    if (StrToNumber.strTodouble(bookAmount) - StrToNumber.strTodouble(baseAcctBal) > StrToNumber.strTodouble(fhAcctBal)) {
                        etInputFtmin.setText(fhAcctBal);
                    } else {
                        //预约金额-账户余额<小饭团金额，使用小饭团=预约金额-账户余额
                        etInputFtmin.setText((StrToNumber.strTodouble(bookAmount) - StrToNumber.strTodouble(baseAcctBal)) + "");
                    }
                }
                break;
            case R.id.iv_useftmin://勾选小饭团
                ivUsezhanghu.setImageResource(R.drawable.jy_buy_usezhanghu_no);
                ivUseftmin.setImageResource(R.drawable.jy_buy_useftmin_font_ok);
                //原理同上
                if (StrToNumber.strTodouble(fhAcctBal) >= StrToNumber.strTodouble(bookAmount)) {
                    etInputFtmin.setText(bookAmount);
                    etInputZhanghu.getText().clear();
                } else {
                    etInputFtmin.setText(fhAcctBal);
                    if (StrToNumber.strTodouble(bookAmount) - StrToNumber.strTodouble(fhAcctBal) > StrToNumber.strTodouble(baseAcctBal)) {
                        etInputZhanghu.setText(baseAcctBal);
                    } else {
                        etInputZhanghu.setText((StrToNumber.strTodouble(bookAmount) - StrToNumber.strTodouble(fhAcctBal)) + "");
                    }
                }
                break;
            case R.id.iv_check_jiang:
                String fanheMoney = etInputFtmin.getText().toString().trim();
                String zhanghuMoney = etInputZhanghu.getText().toString().trim();
                if (StrToNumber.strTodouble(fanheMoney) + StrToNumber.strTodouble(zhanghuMoney) >=
                        StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))) {
                    isJiangJinChecked = !isJiangJinChecked;
                    if (isJiangJinChecked) {//勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                        //买入总计
                        tvZong.setText(MathUtil.subDouble((StrToNumber.strTodouble(fanheMoney) + StrToNumber.strTodouble(zhanghuMoney) +
                                StrToNumber.strTodouble(CacheUtils.getString(CacheUtils.REWARDACCTBAL, ""))), 2) + "");
                        //勾选，获取奖金值
                        jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                        //买入总计
                        tvZong.setText(MathUtil.subDouble(StrToNumber.strTodouble(fanheMoney) + StrToNumber.strTodouble(zhanghuMoney), 2) + "");
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
                String fanhe_Money = etInputFtmin.getText().toString().trim();
                String zhanghu_Money = etInputZhanghu.getText().toString().trim();
                String buy_money = (StrToNumber.strTodouble(fanhe_Money) + StrToNumber.strTodouble(zhanghu_Money)) + "";
                String zong_money = (StrToNumber.strTodouble(fanhe_Money) + StrToNumber.strTodouble(zhanghu_Money) + StrToNumber.strTodouble(jiangjin)) + "";

                if (StrToNumber.strTodouble(buy_money) != 0) {
                    if (StrToNumber.strTodouble(zhanghu_Money) > StrToNumber.strTodouble(baseAcctBal)) {
                        NormalDialog normalDialog = new NormalDialog(this, R.style.YzmDialog, "您输入的账户余额大于可用余额，请重新输入。");
                        normalDialog.show();
                        return;
                    }
                    if (StrToNumber.strTodouble(fanhe_Money) > StrToNumber.strTodouble(fhAcctBal)) {
                        NormalDialog normalDialog = new NormalDialog(this, R.style.YzmDialog, "您输入的小饭团资产大于可用余额，请重新输入。");
                        normalDialog.show();
                        return;
                    }
                    if (StrToNumber.strTodouble(buy_money) < StrToNumber.strTodouble(minInvestAmount)) {
                        NormalDialog normalDialog = new NormalDialog(this, R.style.YzmDialog, "出借金额必须大于最低出借，请重新输入。");
                        normalDialog.show();
                        return;
                    }
                    if (StrToNumber.strTodouble(buy_money) > StrToNumber.strTodouble(bookAmount)) {
                        NormalDialog normalDialog = new NormalDialog(this, R.style.YzmDialog, "“使用账户余额+使用小饭团资产”需小于等于预约金额");
                        normalDialog.show();
                        return;
                    }

                    getDataFromServer(zong_money, zhanghu_Money, fanhe_Money, jiangjin, seqNo);

                } else {
                    ToastUtils.toastshort("出借金额不能为空哦");
                }
                break;
        }
    }

    private void getDataFromServer(final String zong_money, String zhanghu_Money, String fanhe_Money, String jiangjin, String seqNo) {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }

        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("balAmount", zhanghu_Money);
        map.put("fhAmount", fanhe_Money);
        map.put("rewardAmount", jiangjin);
        map.put("seqNo", seqNo);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(
                ConstantUtils.FTINVESTSTEP2_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {
                                    Intent intent2 = new Intent(FanTongBuyActivity.this, FanTongBuySuccessActivity.class);
                                    intent2.putExtra("zong_money", zong_money);
                                    startActivity(intent2);
                                } else {
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
