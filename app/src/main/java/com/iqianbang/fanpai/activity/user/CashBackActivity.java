package com.iqianbang.fanpai.activity.user;

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
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CashBackDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CashBackActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_zaitou)
    TextView tvZaitou;
    @BindView(R.id.et_shuhui)
    EditText etShuhui;
    @BindView(R.id.tv_shuhui)
    TextView tvShuhui;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private CustomProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_back);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在赎回中...");
        initData();
    }

    private void initData() {
        String fhTotalAssets = getIntent().getStringExtra("fhTotalAssets");
        tvZaitou.setText(fhTotalAssets);

        String recUserDailyLimit = getIntent().getStringExtra("recUserDailyLimit");
        etShuhui.setHint("每日限额为" + recUserDailyLimit + "元");

        // 给赎回金额设置监听，为了使输入金额不超过两位有效数字
        etShuhui.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etShuhui.setText(s);
                        etShuhui.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etShuhui.setText(s);
                    etShuhui.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etShuhui.setText(s.subSequence(0, 1));
                        etShuhui.setSelection(1);
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
                tvShuhui.setText(etShuhui.getText());
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
                String money = etShuhui.getText().toString().trim();
                String uuid = getIntent().getStringExtra("uuid");
                cash(money, uuid);
                break;
        }
    }


    protected void cash(String money, String uuid) {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        if (TextUtils.isEmpty(money)) {
            ToastUtils.toastshort("赎回金额不能为空哦");
            return;
        }
        progressdialog.show();
        map.put("token", token);
        map.put("amount", money);
        map.put("uuid", uuid);
        map.put("clientType", "android");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FHRECSUBMIT_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("饭盒赎回===" + string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            //赎回成功跳出弹窗
                            CashBackDialog cashBackDialog = new CashBackDialog(CashBackActivity.this, R.style.YzmDialog);
                            cashBackDialog.setCanceledOnTouchOutside(false);
                            cashBackDialog.setCancelable(false);
                            cashBackDialog.show();
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络连接失败，请检查网络");
                    }
                });

    }
}
