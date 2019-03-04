package com.iqianbang.fanpai.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_code;

public class SettingForAddressActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(et_code)
    EditText etCode;
    @BindView(R.id.iv_btn_finish)
    ImageView ivBtnFinish;

    private CustomProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_address);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在保存数据......");
        initData();
    }

    private void initData() {
        //默认显示已经设置过的地址
        String address = CacheUtils.getString(CacheUtils.ADDRESS, "");
        etAddress.setText(address);
        String zipCode = CacheUtils.getString(CacheUtils.ZIPCODE, "");
        etCode.setText(zipCode);
    }

    @OnClick({R.id.iv_back, R.id.iv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_btn_finish:
                getDataFromServer();
                break;
        }
    }

    private void getDataFromServer() {
        final String address = etAddress.getText().toString().trim();
        final String zipCode = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastUtils.toastshort("请输入详细地址");
            return;
        }
        if (TextUtils.isEmpty(zipCode)) {
            ToastUtils.toastshort("请输入邮政编码");
            return;
        }

        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        progressdialog.show();
        map.put("token", token);
        map.put("address", address);
        map.put("zipCode", zipCode);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SETADDRESS_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("地址信息===="+string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if("0".equals(code)){
                            CacheUtils.putString("address", address);
                            CacheUtils.putString("zipCode", zipCode);
                            ToastUtils.toastshort("保存成功");
                            finish();
                        }else{
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("保存失败");
                    }
                });
    }
}
