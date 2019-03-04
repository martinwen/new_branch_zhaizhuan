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
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
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

import static com.iqianbang.fanpai.R.id.et_input;

public class FanTongYuYueActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_limit_money)
    TextView tvLimitMoney;
    @BindView(et_input)
    EditText etInput;
    @BindView(R.id.iv_btn_finish)
    ImageView ivBtnFinish;

    private CustomProgressDialog progressdialog;
    private String minBookAmount;
    private String maxBookAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantongyuyue);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        minBookAmount = intent.getStringExtra("minBookAmount");
        maxBookAmount = intent.getStringExtra("maxBookAmount");
        tvLimitMoney.setText(minBookAmount+"元~"+maxBookAmount+"元");


        // 给输入金额设置监听，为了使输入金额不超过两位有效数字
        etInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etInput.setText(s);
                        etInput.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etInput.setText(s);
                    etInput.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etInput.setText(s.subSequence(0, 1));
                        etInput.setSelection(1);
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

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.iv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_btn_finish:
                String money = etInput.getText().toString().trim();
                if (money.isEmpty()) {
                    ToastUtils.toastshort("预约金额不能为空哦");
                }else {
                    if (StrToNumber.strTodouble(money)<StrToNumber.strTodouble(minBookAmount)) {
                        ToastUtils.toastshort("您输入的金额小于预约金额最小值，请重新输入。");
                    }else if (StrToNumber.strTodouble(money)>StrToNumber.strTodouble(maxBookAmount)) {
                        ToastUtils.toastshort("您输入的金额大于预约金额最大值，请重新输入。");
                    }else {
                        requestBook(money);
                    }
                }
                break;
        }
    }

    private void requestBook(String money) {

        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("type", "ft");
        map.put("amount", money);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.BOOK_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("饭桶预约==="+string);
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

                                    NormalDialog normalDialog = new NormalDialog(FanTongYuYueActivity.this, R.style.YzmDialog, "预约成功！客服将稍后与您联系！");
                                    normalDialog.show();
                                    normalDialog.setOnNormalDialogDismissListener(new NormalDialog.OnNormalDialogDismissListener() {

                                        @Override
                                        public void OnNormalDialogDismiss() {
                                            // TODO Auto-generated method stub
                                            //产品要求饭桶预约成功后跳转到首页
                                            Intent intent = new Intent(FanTongYuYueActivity.this,MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    });

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
}
