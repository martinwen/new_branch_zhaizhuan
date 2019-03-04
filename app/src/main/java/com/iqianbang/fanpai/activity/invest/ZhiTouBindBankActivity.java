package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.iqianbang.fanpai.adapter.BankAdapter;
import com.iqianbang.fanpai.bean.BankListBean;
import com.iqianbang.fanpai.bean.HongBaoBean;
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
import com.iqianbang.fanpai.view.dialog.HasHongBaoOrJiaXiPiaoDialogForFWbuy;
import com.iqianbang.fanpai.view.dialog.JiangJinDialog;
import com.iqianbang.fanpai.view.dialog.XieYiDialog;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZhiTouBindBankActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.tv_bankname)
    TextView tvBankname;
    @BindView(R.id.rl_bankname)
    RelativeLayout rlBankname;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.et_card)
    EditText etCard;
    @BindView(R.id.et_phone)
    EditText etPhone;
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
    @BindView(R.id.iv_check_xieyi)
    ImageView ivCheckXieyi;
    @BindView(R.id.tv_xieyi_jinzhi)
    TextView tvXieyiJinzhi;
    @BindView(R.id.tv_xieyi_zhuanrang)
    TextView tvXieyiZhuanrang;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;



    private CustomProgressDialog progressdialog;
    private PopupWindow pop;// 银行选择的弹窗
    private ListView listView;// 银行选择的listview
    private ArrayList<BankListBean> list = new ArrayList<>();
    private BankAdapter adapter;
    private String seqNo;
    private boolean isXieYiChecked = false;// 协议已经勾上
    private String discountRate;
    private String minInvestAmount;
    private String buyMoney;//输入的买入金额
    private String actMoney;
    private String bankName;
    private String bankCode;
    private JSONObject data;
    private String datastr;
    private String borrowSalableBal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhitou_bind_bank);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();
    }

    private void initData() {
            /*姓名：韩梅梅
        身份证号：210302196001012114
        电话号：13220482188
        短信验证码:123456
        储蓄卡 :
        卡号：6214242710498301(建设银行) 或者 6214242710498509(招商银行)*/

        /*etName.setText("文广军");
        etId.setText("430903198807216932");
        etPhone.setText("17610770721");
        etCard.setText("6212260200104685207");*/

        //初始化选中银行下拉列表
        getBanks();

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
        //seqNo
        seqNo = data.getString("seqNo");


        //从买入第一步中拿到起投和单笔限额
        minInvestAmount = data.getString("minInvestAmount");
        etBuy.setHint("请输入承接金额，最低承接" + minInvestAmount + "元");

        //从买入第一步中拿到isEnable,  3:换卡（身份证号和姓名不允许输入），4:新绑卡
        String isEnable = data.getString("isEnable");

        if ("3".equals(isEnable)) {

            //姓名（用于解绑后再次绑卡时显示）
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证（用于解绑后再次绑卡时显示）
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }
        } else if ("4".equals(isEnable)) {

            //姓名（用于新绑卡）
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
            }

            //身份证（用于新绑卡）
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
            }

            //银行名称
            String bankName = data.getString("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
            }

            //银行卡号
            String cardNum = data.getString("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
            }

            //银行卡预留手机号
            String cardPhone = data.getString("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
            }
        } else if ("5".equals(isEnable)) {//（用于丰付切换到融宝过来的）

            //姓名
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }

            //银行名称
            String bankName = data.getString("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
                rlBankname.setEnabled(false);
            }

            //银行编号
            bankCode = data.getString("bankCode");

            //银行卡号
            String cardNum = data.getString("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
                etCard.setEnabled(false);
            }

            //银行卡预留手机号
            String cardPhone = data.getString("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
                etPhone.setEnabled(false);
            }
        }

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

                buyMoney = etBuy.getText().toString().trim();

                if (!TextUtils.isEmpty(buyMoney)) {

                    //计算承接价格
                    actMoney = new BigDecimal(buyMoney).multiply(BigDecimal.ONE.subtract((new BigDecimal(discountRate).divide(new BigDecimal("100"))))) + "";
                    tvBuy.setText(actMoney);
                }else{
                    //计算承接价格
                    actMoney = "0.00";
                    tvBuy.setText(actMoney);
                }
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_check, R.id.rl_bankname, R.id.iv_check_xieyi,
              R.id.tv_xieyi_jinzhi, R.id.tv_xieyi_zhuanrang, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check:
                startActivity(new Intent(this, BankListActivity.class));
                break;
            case R.id.rl_bankname:
                // 初始化Listview
                ListView listView = initListview();
                // 点击时，弹出Popupwindow
                pop = new PopupWindow(listView, LockPatternUtil.dip2px(this, 200), LockPatternUtil.dip2px(this, 300));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(rlBankname, LockPatternUtil.dip2px(this, 100), 0);
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
            case R.id.tv_xieyi_zhuanrang:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                bind();
                break;
        }
    }

    private void bind() {
        // 绑定银行卡界面输入的参数
        final String name = etName.getText().toString().trim();
        final String number = etId.getText().toString().trim();
        final String card = etCard.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();

        if (!isXieYiChecked) {// 没有勾选协议
            XieYiDialog xieYiDialog = new XieYiDialog(this, R.style.YzmDialog, tvXieyiZhuanrang.getText().toString().trim());
            xieYiDialog.show();
            return;
        }
        if (TextUtils.isEmpty(name)) {// 姓名为空
            ToastUtils.toastshort("姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(number)) {// 身份证号为空
            ToastUtils.toastshort("身份证号不能为空");
            return;
        }
        if (TextUtils.isEmpty(bankCode)) {// 未选择银行
            ToastUtils.toastshort("请选择银行名称");
            return;
        }
        if (TextUtils.isEmpty(card)) {// 银行卡号为空
            ToastUtils.toastshort("银行卡号不能为空");
            return;
        }
        if (TextUtils.isEmpty(phone)) {// 银行预留手机号为空
            ToastUtils.toastshort("银行预留手机号不能为空");
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

        getDataFromServer(name, number, card, phone);

    }


    private void getDataFromServer(final String name, final String number, final String card, final String phone) {
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("realName", name);
        map.put("idNo", number);
        map.put("cardNum", card);
        map.put("cardPhone", phone);
        map.put("bankName", bankName);
        map.put("bankCode", bankCode);
        map.put("amount", buyMoney);
        map.put("payAmount", actMoney);
        map.put("reward", "0.00");
        map.put("seqNo", seqNo);
        map.put("isUsedBal", "0");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.TRANSFER_BINDCARD_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("大饭团绑卡接口===" + string);
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
                                if (isSuccess) {// 验签成功
                                    JSONObject data = JSON.parseObject(datastr);

                                    String seqNo = data.getString("seqNo");
                                    String bankCode = data.getString("bankCode");
                                    String bindId = data.getString("bindId");

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

                                    //银行图标
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

                                    // 渠道（融宝reapal或丰付sumapay）
                                    String channelCode = data.getString("channelCode");

                                    if ("CMB".equals(bankCode) && "reapal".equals(channelCode)) {
                                        //如果是招商银行 则跳转鉴权界面
                                        Intent intent = new Intent(ZhiTouBindBankActivity.this,
                                                ZhiTouCMDAuthenticationActivity.class);
                                        intent.putExtra("investAmount", investAmount);//买入债权金额
                                        intent.putExtra("payAmount", payAmount);//买入实际支付金额
                                        intent.putExtra("rewardAcctBal", rewardAcctBal);//奖金
                                        intent.putExtra("baseBal", baseBal);// 使用余额
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("bindId", bindId);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // 把这些数据传递给绑定银行卡确定页面
                                        Intent intent = new Intent(ZhiTouBindBankActivity.this,
                                                ZhiTouBuyConfirmActivity.class);
                                        intent.putExtra("investAmount", investAmount);//买入债权金额
                                        intent.putExtra("payAmount", payAmount);//买入实际支付金额
                                        intent.putExtra("rewardAcctBal", rewardAcctBal);//奖金
                                        intent.putExtra("baseBal", baseBal);// 使用余额
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("seqNo", seqNo);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    ToastUtils.toastshort("添加失败！");
                                }
                            }

                        } else {
                            String msg = json.getString("msg");
                            if (msg.startsWith("为避免剩余金额")) {
                                BuyAllDialog buyAllDialog = new BuyAllDialog(ZhiTouBindBankActivity.this,
                                        R.style.YzmDialog, minInvestAmount);
                                buyAllDialog.show();
                            } else {
                                ToastUtils.toastshort(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络请求失败！");

                    }
                });
    }

    private void getBanks() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        progressdialog.show();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.BANKLIST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
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
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {// 验签成功
                                    ArrayList<BankListBean> listadd = (ArrayList<BankListBean>) JSONArray.parseArray(datastr, BankListBean.class);
                                    list.addAll(listadd);
                                    adapter = new BankAdapter(ZhiTouBindBankActivity.this, list);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    ToastUtils.toastshort("银行列表获取异常");
                                }
                            }
                        } else {
                            ToastUtils.toastshort("银行列表获取失败");
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络请求失败");
                    }
                });
    }

    private ListView initListview() {
        listView = new ListView(this);
        // 去掉分割线
        listView.setDividerHeight(0);
        // 去掉滑动块
        listView.setVerticalScrollBarEnabled(false);
        // 设置背景
//        listView.setBackgroundResource(R.drawable.listview_background);
        listView.setBackgroundColor(getResources().getColor(R.color.global_bg_blackcolor));
        // 设置Adapter
        listView.setAdapter(adapter);
        // 设置条目点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());
        return listView;
    }

    // 根据点击选中银行
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            LogUtils.i("点击条目了：" + position);
            if (list.size() == 0) {
                return;
            }
            BankListBean bankListBean = list.get(position);
            bankName = bankListBean.getBankName();
            bankCode = bankListBean.getBankCode();
            tvBankname.setText(bankName);
            pop.dismiss();// 选中银行弹窗消失
        }
    }
}
