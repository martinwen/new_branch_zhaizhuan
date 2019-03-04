package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
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
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.RoundProgressBar;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.YzmDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhiTouBuyConfirmActivity extends BaseActivity {

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
    @BindView(R.id.tv_money_zong)
    TextView tvMoneyZong;
    @BindView(R.id.tv_money_buy)
    TextView tvMoneyBuy;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.tv_zhifu)
    TextView tvZhifu;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;


    private CustomProgressDialog progressdialog;
    private String seqNo;//交易流水号
    private String phone;
    private String investAmount;
    private String payAmount;
    private String rewardAcctBal;
    private String baseBal;
    private String rechargeAmount;
    private MyCount myCount;
    private String uuid;
    private String yanzhengma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhitou_buy_confirm);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {

        Intent intent = getIntent();

        //交易流水号
        seqNo = intent.getStringExtra("seqNo");

        //承接金额
        investAmount = intent.getStringExtra("investAmount");
        tvMoneyZong.setText(MathUtil.subString(investAmount, 2) + "元");

        //承接价格
        payAmount = intent.getStringExtra("payAmount");
        tvMoneyBuy.setText(MathUtil.subString(payAmount, 2) + "元");

        //使用奖金
        rewardAcctBal = intent.getStringExtra("rewardAcctBal");
        tvJiang.setText(MathUtil.subString(rewardAcctBal, 2) + "元");

        //使用余额
        baseBal = intent.getStringExtra("baseBal");
        tvYue.setText(MathUtil.subString(baseBal, 2) + "元");

        //卡支付金额
        rechargeAmount = intent.getStringExtra("rechargeAmount");
        tvZhifu.setText(MathUtil.subString(rechargeAmount, 2) + "元");

        //开户行图标
        String bankPic = CacheUtils.getString(CacheUtils.BANKPIC, "");
        ImageLoader.getInstance().displayImage(bankPic, ivBanklogo);

        //开户行名称
        String bankName = CacheUtils.getString(CacheUtils.BANKNAME, "");
        tvBankname.setText(bankName);

        //银行单笔限额
        String singleTransLimit = CacheUtils.getString(CacheUtils.SINGLETRANSLIMIT, "");
        //银行单日限额
        String singleDayLimit = CacheUtils.getString(CacheUtils.SINGLEDAYLIMIT, "");

        tvBanklimit.setText("单笔：" + singleTransLimit + "元   单日：" + singleDayLimit + "元");

        //姓名
        String realName = CacheUtils.getString(CacheUtils.REALNAME, "");
        tvName.setText(realName);

        //身份证号
        String idNo = CacheUtils.getString(CacheUtils.IDNO, "");
        tvId.setText(idNo);

        //卡号
        String cardNum = CacheUtils.getString(CacheUtils.CARDNUM, "");
        tvBanknum.setText(cardNum);

        //手机号
        phone = CacheUtils.getString(CacheUtils.CARDPHONE, "");
        tvPhone.setText(phone);

        //一进来买入确认界面就获取uuid
        getUuid(0);
        //一进来买入确认界面，服务端发送验证码，但是重新获取是自己调用接口了
        getCode();
    }

    private void getCode() {
        ivYanzheng.setVisibility(View.GONE);
        myCount = new MyCount(60000, 1000);// 设置倒计时时间为60秒，间隔为1秒
        myCount.start();
    }

    @OnClick({R.id.iv_back, R.id.iv_yanzheng, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_yanzheng:
                YzmDialog yzmDialog = new YzmDialog(this, R.style.YzmDialog, phone);
                yzmDialog
                        .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                            @Override
                            public void OnYzmDialogDismiss(String answer) {
                                progressdialog.show();
                                progressdialog.setContent("正在获取验证码...");
                                String token = CacheUtils.getString("token", "");
                                Map<String, String> map = SortRequestData.getmap();
                                map.put("phone", phone);
                                map.put("seqNo", seqNo);
                                map.put("captchaResult", answer);
                                map.put("token", token);
                                String requestData = SortRequestData.sortString(map);
                                String signData = SignUtil.sign(requestData);
                                map.put("sign", signData);
                                VolleyUtil.sendJsonRequestByPost(ConstantUtils.RETRIEVEMSG_URL, null, map, new HttpBackBaseListener() {

                                    @Override
                                    public void onSuccess(String string) {
                                        progressdialog.dismiss();
                                        JSONObject json = JSON.parseObject(string);
                                        String code = json.getString("code");
                                        if ("0".equals(code)) {
                                            ivYanzheng.setVisibility(View.GONE);
                                            myCount = new MyCount(60000, 1000);// 设置倒计时时间为60秒，间隔为1秒
                                            myCount.start();
                                        } else {
                                            ToastUtils.toastshort("获取验证码失败");
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        progressdialog.dismiss();
                                        ToastUtils.toastshort("获取验证码失败");
                                    }
                                });
                            }
                        });
                yzmDialog.show();
                break;
            case R.id.tv_btn_finish:
                yanzhengma = etCode.getText().toString().trim();
                if (StringUtils.isBlank(yanzhengma)) {
                    ToastUtils.toastshort("请输入验证码！");
                    etCode.setFocusable(true);
                    etCode.setFocusableInTouchMode(true);
                    etCode.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    return;
                }
                requestServer();
                break;
        }
    }

    private void requestServer() {
        //获取uuid
        if (!progressdialog.isShowing()) {
            progressdialog.show();
            progressdialog.setContent("买入确认中...");
        }
        final long startTime = System.currentTimeMillis();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("seqNo", seqNo);
        map.put("smsCode", yanzhengma);
        map.put("uuid", uuid);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFT_SUBMITINVEST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("买入确认=====接口===" + string);
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime > 12000) {
                            progressdialog.dismiss();
                            Intent intent = new Intent(ZhiTouBuyConfirmActivity.this, BuyShenQingActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            progressdialog.dismiss();
                            JSONObject json = JSON.parseObject(string);
                            String code = json.getString("code");

                            if ("0".equals(code)) {

                                Intent intent = new Intent(ZhiTouBuyConfirmActivity.this, ZhiTouBuySuccessActivity.class);
                                intent.putExtra("investAmount", investAmount);
                                startActivity(intent);
                                finish();
                            } else if ("4003".equals(code)) {

                                Intent intent = new Intent(ZhiTouBuyConfirmActivity.this, BuyShenQingActivity.class);
                                startActivity(intent);
                                finish();

                            } else if ("1002".equals(code)) {//1002表示uuid失效，重新获取uuid
                                uuid = null;
                                getUuid(1);

                            } else {
                                String msg = json.getString("msg");
                                ToastUtils.toastshort(msg);
                            }
                        }
                    }


                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络错误！");
                    }
                });

    }

    private void getUuid(final int i) {
        //获取uuid
        if (!progressdialog.isShowing()) {
            progressdialog.show();
            progressdialog.setContent("买入确认中...");
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
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            uuid = json.getString("data");
                            if (i == 1) {
                                requestServer();
                            }

                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络错误！");
                    }
                });
    }

    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //倒计时完要做的事情
            tvCount.setClickable(true);
            // 让获取图片重新显示
            ivYanzheng.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCount.setText(millisUntilFinished / 1000 + "");//60秒倒计时
            pwSpinner.setProgress((int) (millisUntilFinished / 1000));//圆弧进度条
            tvCount.setClickable(false);
        }
    }
}
