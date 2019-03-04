package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
import com.iqianbang.fanpai.adapter.BankAdapter;
import com.iqianbang.fanpai.bean.BankListBean;
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
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_card;
import static com.iqianbang.fanpai.R.id.et_name;
import static com.iqianbang.fanpai.R.id.et_phone;
import static com.iqianbang.fanpai.R.id.tv_bankname;

public class FtMinBindBankActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(et_name)
    EditText etName;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(et_card)
    EditText etCard;
    @BindView(et_phone)
    EditText etPhone;
    @BindView(R.id.et_buy)
    EditText etBuy;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.iv_check_jiang)
    ImageView ivCheckJiang;
    @BindView(R.id.iv_check_xieyi)
    ImageView ivCheckXieyi;
    @BindView(R.id.iv_xieyi)
    ImageView ivXieyi;
    @BindView(R.id.tv_zong)
    TextView tvZong;
    @BindView(tv_bankname)
    TextView tvBankname;
    @BindView(R.id.rl_bankname)
    RelativeLayout rlBankname;
    @BindView(R.id.tv_buyer_condition)
    TextView tvBuyerCondition;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private PopupWindow pop;// 银行选择的弹窗
    private ListView listView;// 银行选择的listview
    private ArrayList<BankListBean> list = new ArrayList<BankListBean>();
    private BankAdapter adapter;
    private String seqNo;
    private String jiangjin;
    private boolean isJiangJinChecked = true;// 奖金没有勾上
    private boolean isXieYiChecked = true;// 协议已经勾上
    private String bankName;
    private String bankCode;
    private String minInvestAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ft_min_bind_bank);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();
    }

    private void initData() {
        /*姓名：韩梅梅
        身份证号：210302190001012117
        电话号：13220482188
        短信验证码:123456
        储蓄卡 :
        卡号：6214242710498301(建设银行) 或者 6214242710498509(招商银行)*/

//        etName.setText("韩梅梅");
//        etId.setText("210302196001012114");
//        etPhone.setText("13220482188");
//        etCard.setText("6214242710498301");
        //初始化选中银行下拉列表
        getBanks();

        //从买入第一步中拿到交易流水号
        Intent intent = getIntent();
        seqNo = intent.getStringExtra("seqNo");

        //从买入第一步中拿到起投和单笔限额
        minInvestAmount = intent.getStringExtra("minInvestAmount");
        String maxInvestAmount = intent.getStringExtra("maxInvestAmount");
        String fhBalMaxAmount = intent.getStringExtra("fhBalMaxAmount");
        //从买入第一步中拿到是否是新手标
        boolean isNewUser = intent.getBooleanExtra("isNewUser", false);
        if (isNewUser) {
            tvBuyerCondition.setText("最低出借"+minInvestAmount + "元，新手标出借上限为" + maxInvestAmount + "万元");
        } else {
            tvBuyerCondition.setText("最低出借"+minInvestAmount + "元，单人出借限额" + fhBalMaxAmount + "万元");
        }

        //从买入第一步中拿到isEnable,  3:换卡（身份证号和姓名不允许输入），4:新绑卡
        String isEnable = intent.getStringExtra("isEnable");

        if ("3".equals(isEnable)) {//（用于解绑后再次绑卡时显示）

            //姓名
            String realName = intent.getStringExtra("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证
            String idNo = intent.getStringExtra("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }
        } else if ("4".equals(isEnable)) {//（用于新绑卡或一键注册过来的）

            //姓名
            String realName = intent.getStringExtra("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
            }

            //身份证
            String idNo = intent.getStringExtra("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
            }

            //银行名称
            String bankName = intent.getStringExtra("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
            }

            //银行卡号
            String cardNum = intent.getStringExtra("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
            }

            //银行卡预留手机号
            String cardPhone = intent.getStringExtra("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
            }
        }else if ("5".equals(isEnable)) {//（用于丰付切换到融宝过来的）

            //姓名
            String realName = intent.getStringExtra("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证
            String idNo = intent.getStringExtra("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }

            //银行名称
            String bankName = intent.getStringExtra("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
                rlBankname.setEnabled(false);
            }

            //银行编号
            bankCode = intent.getStringExtra("bankCode");

            //银行卡号
            String cardNum = intent.getStringExtra("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
                etCard.setEnabled(false);
            }

            //银行卡预留手机号
            String cardPhone = intent.getStringExtra("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
                etPhone.setEnabled(false);
            }
        }

        //获取奖金余额
        jiangjin = CacheUtils.getString(CacheUtils.REWARDACCTBAL, "");
        tvJiang.setText(MathUtil.subString(jiangjin, 2) + "");

        //给买入金额设置监听，1.保证两位有效小数，2.为了使奖金使用状态图标根据不同的输入值变化而变化
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

                String buyMoney = etBuy.getText().toString().trim();

                if (!TextUtils.isEmpty(buyMoney)) {
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
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_check, R.id.iv_check_jiang, R.id.iv_check_xieyi, R.id.iv_xieyi,
            R.id.tv_btn_finish, R.id.rl_bankname})
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
            case R.id.iv_check_jiang:
                String buyMoney = etBuy.getText().toString().trim();
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
        final String buy_money = etBuy.getText().toString().trim();

        if (!isXieYiChecked) {// 没有勾选协议
            ToastUtils.toastshort("请同意出借协议");
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
        if (TextUtils.isEmpty(buy_money)) {// 充值金额为空
            ToastUtils.toastshort("出借金额不能为空");
            return;
        } else if (StrToNumber.strTodouble(buy_money) < StrToNumber.strTodouble(minInvestAmount)) {// 出借金额小于最小最低出借
            ToastUtils.toastshort("请输入不小于"+minInvestAmount+"的出借金额");
            return;
        }

        progressdialog.show();
        // 绑卡之前 获取UUID
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        progressdialog.show();
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GETUUID_URL, null,
                map, new HttpBackBaseListener() {

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
                                    map.put("realName", name);
                                    map.put("idNo", number);
                                    map.put("cardNum", card);
                                    map.put("cardPhone", phone);
                                    map.put("amount", buy_money);
                                    map.put("reward", jiangjin);
                                    map.put("seqNo", seqNo);
                                    map.put("bankName", bankName);
                                    map.put("bankCode", bankCode);
                                    map.put("uuid", datastr);
                                    String requestData = SortRequestData.sortString(map);
                                    String signData = SignUtil.sign(requestData);
                                    map.put("sign", signData);
                                    VolleyUtil.sendJsonRequestByPost(ConstantUtils.BINDCARD_URL, null, map,
                                            new HttpBackBaseListener() {

                                                @Override
                                                public void onSuccess(String string) {
                                                    LogUtils.i("绑卡=====接口===" + string);
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

                                                                // 使用余额
                                                                String baseBal = data.getString("baseBal");

                                                                // 充值金额
                                                                String rechargeAmount = data.getString("rechargeAmount");

                                                                // 买入总计
                                                                String investTotalAmount = data.getString("investTotalAmount");

                                                                // 渠道（融宝reapal或丰付sumapay）
                                                                String channelCode = data.getString("channelCode");

                                                                if ("CMB".equals(bankCode) && "reapal".equals(channelCode)) {
                                                                    //如果是招商银行 则跳转鉴权界面
                                                                    Intent intent = new Intent(FtMinBindBankActivity.this, FtMinCMDAuthenticationActivity.class);
                                                                    intent.putExtra("et_buy_money", baseBal);
                                                                    intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                                                    intent.putExtra("et_jiangjin_money", jiangjin);
                                                                    intent.putExtra("zong_money", investTotalAmount);
                                                                    intent.putExtra("seqNo", seqNo);
                                                                    intent.putExtra("bindId", bindId);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    // 把这些数据传递给绑定银行卡确定页面
                                                                    Intent intent = new Intent(FtMinBindBankActivity.this, FtMinBuyConfirmActivity.class);
                                                                    intent.putExtra("et_buy_money", baseBal);
                                                                    intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                                                    intent.putExtra("et_jiangjin_money", jiangjin);
                                                                    intent.putExtra("zong_money", investTotalAmount);
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
                                                        ToastUtils.toastshort(msg);
                                                    }
                                                }

                                                @Override
                                                public void onError(VolleyError error) {
                                                    progressdialog.dismiss();
                                                    ToastUtils.toastshort("网络请求失败！");

                                                }
                                            });
                                } else {
                                    progressdialog.dismiss();
                                    ToastUtils.toastshort("添加失败！");
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
                                    adapter = new BankAdapter(FtMinBindBankActivity.this, list);
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
