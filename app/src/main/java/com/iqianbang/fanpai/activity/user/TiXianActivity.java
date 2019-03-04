package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.sign.Base64;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StrToNumber;
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

public class TiXianActivity extends BaseActivity {

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
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.tv_zhanghu_info)
    TextView tvZhanghuInfo;
    @BindView(R.id.tv_tixian)
    EditText tvTixian;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_fee_info)
    TextView tvFeeInfo;
    @BindView(R.id.tv_code)
    EditText tvCode;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.tv_zhifu)
    TextView tvZhifu;
    @BindView(R.id.tv_tixianjindu)
    TextView tvTixianjindu;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private boolean isOpen = false;//控制支付密码显隐
    private String rechBal;
    private String uuid;
    private boolean whetherNeedPops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_xian);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        //账户余额
        String baseBal = intent.getStringExtra("baseBal");
        tvZhanghu.setText(baseBal);

        //未出借金额
        rechBal = intent.getStringExtra("rechBal");
        if(StrToNumber.strTodouble(rechBal)>0){
            tvZhanghuInfo.setVisibility(View.VISIBLE);
            tvZhanghuInfo.setText("（未出借金额：" + rechBal + "元）");
        }

        //uuid
        uuid = intent.getStringExtra("uuid");

        //是否有提现弹窗
        whetherNeedPops = intent.getBooleanExtra("whetherNeedPops", false);

        //初始化密码输入形式为不可见
        tvCode.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //开户行图标
        String bankPic = CacheUtils.getString(CacheUtils.BANKPIC, "");
        ImageLoader.getInstance().displayImage(bankPic, ivBanklogo);

        //开户行名称
        String bankName = CacheUtils.getString(CacheUtils.BANKNAME, "");
        tvBankname.setText(bankName);

        //姓名
        String realName = CacheUtils.getString(CacheUtils.REALNAME, "");
        tvName.setText(realName);

        //身份证号
        String idNo = CacheUtils.getString(CacheUtils.IDNO, "");
        tvId.setText(idNo);

        //卡号
        String cardNum = CacheUtils.getString(CacheUtils.CARDNUM, "");
        tvWeihao.setText("(尾号：" + cardNum.substring(cardNum.length() - 4, cardNum.length()) + ")");

        // 给提现金额设置监听，为了使输入金额不超过两位有效数字
        tvTixian.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        tvTixian.setText(s);
                        tvTixian.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    tvTixian.setText(s);
                    tvTixian.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        tvTixian.setText(s.subSequence(0, 1));
                        tvTixian.setSelection(1);
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
                String get_money = tvTixian.getText().toString().trim();
                LogUtils.i("get_money==" + get_money);
                if (!TextUtils.isEmpty(get_money)) {

                    getCashFee(get_money);
                }else{
                    tvFeeInfo.setVisibility(View.INVISIBLE);
                    tvFee.setText("");
                    tvZhifu.setText("");
                }
            }
        });
    }

    private void getCashFee(final String get_money) {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("amount", get_money);
        map.put("useDefaultTicket", "1");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.QUERYCASHFEE_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("提现第二步获取提现手续费===" + string);
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

                                    // 提现票抵扣金额
                                    String ticketAmount = data.getString("ticketAmount");
                                    if (StrToNumber.strTodouble(ticketAmount) > 0) {
                                        tvFeeInfo.setVisibility(View.VISIBLE);
                                        tvFeeInfo.setText("（提现票抵扣" + ticketAmount + "元）");
                                    } else {
                                        tvFeeInfo.setVisibility(View.INVISIBLE);
                                    }

                                    // 提现手续费
                                    String cashFee = data.getString("cashFee");
                                    tvFee.setText(cashFee);

                                    // 到帐金额
                                    String arriveAmount = data.getString("arriveAmount");
                                    tvZhifu.setText(arriveAmount);

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

    }


    @OnClick({R.id.iv_back, R.id.tv_tixianjindu, R.id.iv_eye, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_tixianjindu:
                startActivity(new Intent(this, TiXianLiuShuiActivity.class));
                break;
            case R.id.iv_eye:
                isOpen = !isOpen;
                if (isOpen) {// 打开眼睛图片，显示密码
                    ivEye.setImageResource(R.drawable.ic_eyeopen);
                    tvCode.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                } else {// 关闭眼睛图片，隐藏密码
                    ivEye.setImageResource(R.drawable.ic_eyeclose);
                    tvCode.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                }
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = tvCode.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
            case R.id.tv_btn_finish:
                String get_money = tvTixian.getText().toString().trim();
                String password = tvCode.getText().toString().trim();
                if (TextUtils.isEmpty(get_money)) {
                    ToastUtils.toastshort("请输入提现金额");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.toastshort("请输入密码");
                    return;
                }

                cash(get_money, Base64.encode(SignUtil.encrypt(password)));

                break;
        }
    }

    private void cash(final String get_money, final String password) {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("amount", get_money);
        map.put("uuid", uuid);
        map.put("useDefaultTicket", "1");
        map.put("password", password);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SUBMITCASH_URL, null, map, new HttpBackBaseListener() {

            @Override
            public void onSuccess(String string) {
                LogUtils.i("提现==string=" + string);
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

                            //银行图标
                            String bankPic = data.getString("bankPic");
                            CacheUtils.putString("bankPic", bankPic);

                            //银行名称
                            String bankName = data.getString("bankName");
                            CacheUtils.putString("bankName", bankName);

                            //银行卡号
                            String cardNum = data.getString("cardNum");
                            CacheUtils.putString("cardNum", cardNum);

                            //单笔限额
                            String singleTransLimit = data.getString("singleTransLimit");
                            CacheUtils.putString("singleTransLimit", singleTransLimit);

                            //单日限额
                            String singleDayLimit = data.getString("singleDayLimit");
                            CacheUtils.putString("singleDayLimit", singleDayLimit);

                            //到帐时间
                            String arriveTime = data.getString("arriveTime");

                            //实际到帐金额
                            String arriveAmount = data.getString("arriveAmount");

                            Intent intent = new Intent(TiXianActivity.this, TiXianJinDuActivity.class);
                            intent.putExtra("arriveTime", arriveTime);
                            intent.putExtra("arriveAmount", arriveAmount);
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
                ToastUtils.toastshort("提现失败！");
            }
        });
    }

}
