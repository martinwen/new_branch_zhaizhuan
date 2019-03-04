package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_bankname;
import static com.iqianbang.fanpai.R.id.tv_name;

public class ChongZhiActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_banklogo)
    ImageView ivBanklogo;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(tv_bankname)
    TextView tvBankname;
    @BindView(R.id.tv_weihao)
    TextView tvWeihao;
    @BindView(R.id.tv_banklimit)
    TextView tvBanklimit;
    @BindView(tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_chongzhi)
    TextView tvChongzhi;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private String seqNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chong_zhi);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在充值...");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        //交易流水号
        seqNo = intent.getStringExtra("seqNo");


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

        // 银行后4位尾数
        String cardnum = CacheUtils.getString(CacheUtils.CARDNUM, "");
        tvWeihao.setText("(" + cardnum.substring(cardnum.length() - 4, cardnum.length()) + ")");

        // 银行限额
        String singleTransLimit = CacheUtils.getString(CacheUtils.SINGLETRANSLIMIT, "");
        String singleDayLimit = CacheUtils.getString(CacheUtils.SINGLEDAYLIMIT, "");
        tvBanklimit.setText("单笔：" + singleTransLimit + "元   单日：" + singleDayLimit + "元");

        // 给买入金额设置监听，为了使输入金额不超过两位有效数字
        etMoney.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etMoney.setText(s);
                        etMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etMoney.setText(s);
                    etMoney.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etMoney.setText(s.subSequence(0, 1));
                        etMoney.setSelection(1);
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
                // TODO Auto-generated method stub
                tvChongzhi.setText(etMoney.getText());
            }
        });

    }


    @OnClick({R.id.iv_back, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_btn_finish:
                String buy_money = etMoney.getText().toString().trim();
                if (!TextUtils.isEmpty(buy_money)) {

                    getDataFromServer(buy_money);

                } else {
                    ToastUtils.toastshort("请输入充值金额");
                }
                break;
        }
    }

    private void getDataFromServer(final String buy_money) {
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
                                    map.put("amount", buy_money);
                                    map.put("reward", "0");//充值没有奖金
                                    map.put("seqNo", seqNo);
                                    String requestData = SortRequestData.sortString(map);
                                    String signData = SignUtil.sign(requestData);
                                    map.put("sign", signData);
                                    VolleyUtil.sendJsonRequestByPost(ConstantUtils.CONFIRMINVEST_URL, null, map,
                                            new HttpBackBaseListener() {

                                                @Override
                                                public void onSuccess(String string) {
                                                    LogUtils.i("充值第二步===" + string);
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

                                                                // 开户行
                                                                String openBank = data.getString("openBank");
                                                                CacheUtils.putString("openBank", openBank);

                                                                // 充值金额
                                                                String rechargeAmount = data.getString("rechargeAmount");


                                                                // 银行预留手机号
                                                                String cardPhone = data.getString("cardPhone");
                                                                CacheUtils.putString("cardPhone", cardPhone);

                                                                // 交易流水号
                                                                String seqNo = data.getString("seqNo");

                                                                Intent intent3 = new Intent(ChongZhiActivity.this, ChongZhiConfirmActivity.class);
                                                                intent3.putExtra("realName", realName);
                                                                intent3.putExtra("idNo", idNo);
                                                                intent3.putExtra("cardNum", cardNum);
                                                                intent3.putExtra("openBank", openBank);
                                                                intent3.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                                                intent3.putExtra("cardPhone", cardPhone);
                                                                intent3.putExtra("seqNo", seqNo);
                                                                startActivity(intent3);
                                                                finish();

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
                                    ToastUtils.toastshort("充值失败！");
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
