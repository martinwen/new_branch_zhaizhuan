package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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
import com.iqianbang.fanpai.view.RoundProgressBar;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.YzmDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_yanzhengma;

public class SettingForEmailNotBindActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(et_yanzhengma)
    EditText etYanzhengma;
    @BindView(R.id.pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;

    private CustomProgressDialog progressdialog;
    private MyCount myCount;
    private String email;
    private String randomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_emailnotbind);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
    }

    @OnClick({R.id.iv_back, R.id.iv_yanzheng, R.id.iv_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_yanzheng:
                email = etEmail.getText().toString().trim();
                // 邮箱不能为空
                if (TextUtils.isEmpty(email)) {
                    ToastUtils.toastshort("请输入邮箱");
                    // 让EditText获取焦点
                    etEmail.requestFocus();
                    return;
                }
                // 邮箱格式
                if (!checkEmailReg(email)) {
                    ToastUtils.toastshort("邮箱格式不正确，请输入正确的邮箱");
                    // 让EditText获取焦点
                    etEmail.requestFocus();
                    return;
                }

                checkEmail(email);
                break;
            case R.id.iv_btn:
                email = etEmail.getText().toString().trim();
                String code = etYanzhengma.getText().toString().trim();
                // 邮箱不能为空
                if (TextUtils.isEmpty(email)) {
                    ToastUtils.toastshort("请输入邮箱");
                    // 让EditText获取焦点
                    etEmail.requestFocus();
                    return;
                }
                // 验证码不能为空
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.toastshort("请点击获取验证码");
                    // 让EditText获取焦点
                    etYanzhengma.requestFocus();
                    return;
                }
                requestServer(code);
                break;
        }
    }

    private void requestServer(String code) {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("email", email);
        map.put("code", code);
        map.put("token",token);
        map.put("randomId",randomId);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.UPDATEUSEREMAIL_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        progressdialog.dismiss();
                        LogUtils.i("绑定邮箱====="+string);
                        JSONObject json = JSON.parseObject(string);
                        String  code= json.getString("code");
                        if("0".equals(code)){
                            CacheUtils.putString(CacheUtils.EMAIL,email);
                            ToastUtils.toastshort("绑定成功");
                            Intent intent = new Intent(SettingForEmailNotBindActivity.this,SettingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                            startActivity(intent);
                        }else{
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("绑定邮箱失败");
                    }
                });
    }

    /**
     * 验证邮箱
     * @param email
     * @return
     */
    public boolean checkEmailReg(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    //点击验证码后判断邮箱是否存在
    protected void checkEmail(final String email) {
        Map<String, String> map = SortRequestData.getmap();
        map.put("email", email);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);

        VolleyUtil.sendJsonRequestByPost(ConstantUtils.ISEMAILEXIST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if("0".equals(code)){
                            ToastUtils.toastshort("该邮箱已绑定");
                        }else{
                            getcode(email);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        ToastUtils.toastshort("网络错误");
                        return;
                    }
                });
    }

    private void getcode(final String email) {
        YzmDialog yzmDialog_sms = new YzmDialog(this, R.style.YzmDialog, email);
        yzmDialog_sms
                .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                    @Override
                    public void OnYzmDialogDismiss(String answer) {
                        // TODO Auto-generated method stub
                        progressdialog.show();
                        String token = CacheUtils.getString("token", "");
                        Map<String, String> map = SortRequestData.getmap();
                        map.put("email", email);
                        map.put("sendType", 2+"");
                        map.put("codeType", 5+"");
                        map.put("captchaResult", answer);
                        map.put("token",token);
                        String requestData = SortRequestData.sortString(map);
                        String signData = SignUtil.sign(requestData);
                        map.put("sign", signData);
                        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SMS_URL, null, map, new HttpBackBaseListener() {

                            @Override
                            public void onSuccess(String string) {
                                LogUtils.i("string=="+string);
                                progressdialog.dismiss();
                                JSONObject json = JSON.parseObject(string);
                                String code = json.getString("code");
                                if("0".equals(code)){
                                    String datastr = json.getString("data");
                                    JSONObject data = JSON.parseObject(datastr);
                                    randomId = data.getString("randomId");

                                    ivYanzheng.setVisibility(View.GONE);
                                    myCount = new MyCount(60000, 1000);// 设置倒计时时间为60秒，间隔为1秒
                                    myCount.start();
                                }else{
                                    String msg = json.getString("msg");
                                    ToastUtils.toastshort(msg);
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
        yzmDialog_sms.show();
    }

    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //倒计时完要做的事情
            tvCount.setEnabled(true);
            tvCount.setClickable(true);
            ivYanzheng.setVisibility(View.VISIBLE);//让重新获取图片显示
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCount.setText(millisUntilFinished / 1000 + "");//60秒倒计时
            tvCount.setTextColor(getResources().getColor(R.color.global_whitecolor));
            tvCount.setTextSize(10);
            pwSpinner.setProgress((int)(millisUntilFinished / 1000));//圆弧进度条
            tvCount.setClickable(false);
        }

    }
}
