package com.iqianbang.fanpai.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
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

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingForBankNotBindActivity extends Activity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ll_bank)
    LinearLayout llBank;

    private CustomProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_bank_not_bind);
        ButterKnife.bind(this);
        progressdialog=new CustomProgressDialog(this, "...");
    }

    @OnClick({R.id.iv_back, R.id.ll_bank})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_bank:
                //充值第一步获取用户信息
                requestPut();
                break;
        }
    }

    private void requestPut() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("type", "recharge");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GOTOINVEST_URL, null, map,
                new HttpBackBaseListener() {
                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("充值按钮==="+string);
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

                                    // 最大充值金额
                                    String maxRechargeAmount = data.getString("maxRechargeAmount");

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");

                                    //银行单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    //银行单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    Intent intent = new Intent(SettingForBankNotBindActivity.this, ChongZhiBindBankActivity.class);
                                    intent.putExtra("realName", realName);
                                    intent.putExtra("idNo", idNo);
                                    intent.putExtra("seqNo", seqNo);
                                    intent.putExtra("maxRechargeAmount", maxRechargeAmount);
                                    startActivity(intent);

                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        }else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                    }
                });
    }
}
