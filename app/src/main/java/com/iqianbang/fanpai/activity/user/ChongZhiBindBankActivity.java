package com.iqianbang.fanpai.activity.user;

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
import com.iqianbang.fanpai.activity.invest.BankListActivity;
import com.iqianbang.fanpai.adapter.BankAdapter;
import com.iqianbang.fanpai.bean.BankListBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
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

public class ChongZhiBindBankActivity extends BaseActivity {
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
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_chongzhi)
    TextView tvChongzhi;
    @BindView(R.id.tv_bankname)
    TextView tvBankname;
    @BindView(R.id.rl_bankname)
    RelativeLayout rlBankname;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;
    private ArrayList<BankListBean> list = new ArrayList<BankListBean>();
    private BankAdapter adapter;
    private PopupWindow pop;// 银行选择的弹窗
    private ListView listView;// 银行选择的listview

    private String phone;
    private String seqNo;
    private String bankName;
    private String bankCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chong_zhi_bind_bank);
        ButterKnife.bind(this);

        phone = CacheUtils.getString(CacheUtils.LOGINPHONE, "");
        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();

    }

    private void initData() {

        //初始化选中银行下拉列表
        getBanks();

        Intent intent = getIntent();
        //交易流水号
        seqNo = intent.getStringExtra("seqNo");

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

        //默认按钮不可点击
//        ivBtnFinish.setBackgroundResource(R.drawable.chongzhi_btn_normal);
//        ivBtnFinish.setClickable(false);
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

    @OnClick({R.id.iv_back, R.id.tv_check, R.id.rl_bankname, R.id.tv_btn_finish})
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
            case R.id.tv_btn_finish:
                bind();
                break;
        }
    }

    private void bind() {
        final String name = etName.getText().toString().trim();
        final String number = etId.getText().toString().trim();
        final String card = etCard.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String buy_money = etMoney.getText().toString().trim();

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
            ToastUtils.toastshort("充值金额不能为空");
            return;
        }

        progressdialog.show();
        // 绑卡之前 获取UUID
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
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
                                    // 调用添加接口
                                    Map<String, String> map = SortRequestData.getmap();
                                    String token = CacheUtils.getString("token", null);
                                    map.put("token", token);
                                    map.put("realName", name);
                                    map.put("idNo", number);
                                    map.put("cardNum", card);
                                    map.put("cardPhone", phone);
                                    map.put("amount", buy_money);
                                    map.put("reward", "0");
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

                                                                // 充值金额
                                                                String rechargeAmount = data.getString("rechargeAmount");

                                                                // 渠道（融宝reapal或丰付sumapay）
                                                                String channelCode = data.getString("channelCode");

                                                                if ("CMB".equals(bankCode) && "reapal".equals(channelCode)) {
                                                                    //如果是招商银行 则跳转鉴权界面

                                                                    Intent intent = new Intent(ChongZhiBindBankActivity.this, CMDAuthenticationActivity.class);
                                                                    intent.putExtra("rechargeAmount", rechargeAmount);
                                                                    intent.putExtra("seqNo", seqNo);
                                                                    intent.putExtra("bindId", bindId);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    // 把这些数据传递给绑定银行卡确定页面
                                                                    Intent intent = new Intent(ChongZhiBindBankActivity.this, ChongZhiConfirmActivity.class);
                                                                    intent.putExtra("rechargeAmount", rechargeAmount);
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
                                                    ToastUtils.toastshort("添加失败！");

                                                }
                                            });
                                } else {
                                    progressdialog.dismiss();
                                    ToastUtils.toastshort("添加失败！");
                                }
                            }
                        } else {
                            progressdialog.dismiss();
                            ToastUtils.toastshort("添加失败！");
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("添加失败！");
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
                                    adapter = new BankAdapter(ChongZhiBindBankActivity.this, list);
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
            if (list.size() == 0) {
                return;
            }
            System.out.println("点击条目了：" + position);
            BankListBean bankListBean = list.get(position);
            bankName = bankListBean.getBankName();
            bankCode = bankListBean.getBankCode();
            tvBankname.setText(bankName);
            pop.dismiss();// 选中银行弹窗消失
        }

    }

}
