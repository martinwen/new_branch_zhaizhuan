package com.iqianbang.fanpai.activity.registerandlogin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Selection;
import android.text.Spannable;
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
import com.iqianbang.fanpai.sign.Base64;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.StringUtils;
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

public class ForgetPasswordEmailActivity extends Activity {

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.iv_eye1)
    ImageView ivEye1;
    @BindView(R.id.et_new_confirm)
    EditText etNewConfirm;
    @BindView(R.id.iv_eye2)
    ImageView ivEye2;
    @BindView(R.id.et_yanzhengma)
    EditText etYanzhengma;
    @BindView(R.id.pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.iv_finish)
    ImageView ivFinish;
    @BindView(R.id.tv_find)
    TextView tvFind;

    private CustomProgressDialog progressdialog;
    private boolean isOpen1 = false;
    private boolean isOpen2 = false;
    private String email;
    private String mima;
    private MyCount myCount;
    private String randomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_email);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在加载数据......");
        initData();
    }

    private void initData() {
        //给增加下划线
        tvFind.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //初始化密码显示状态
        etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // 密码输入框失去焦点监听，当输入格式不符合时，toast提示
        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                mima = etNewPassword.getText().toString().trim();
                if (hasFocus) {
                } else {
                    if (!isMima(mima)) {
                        ToastUtils.toastshort("密码长度需在6—15位之间");
                    }
                    if (isContainChinese(mima)) {
                        ToastUtils.toastshort("密码不能包含中文");
                    }
                }
            }
        });
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isMima(String mima) {
        return mima.length() >= 6 && mima.length() <= 15;
    }

    @OnClick({R.id.iv_clear, R.id.iv_eye1, R.id.iv_eye2, R.id.iv_yanzheng, R.id.iv_finish, R.id.tv_find})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear:
                etEmail.getText().clear();
                break;
            case R.id.iv_eye1:
                isOpen1 = !isOpen1;
                showPassWord(isOpen1, ivEye1, etNewPassword);
                break;
            case R.id.iv_eye2:
                isOpen2 = !isOpen2;
                showPassWord(isOpen2, ivEye2, etNewConfirm);
                break;
            case R.id.iv_yanzheng:
                email = etEmail.getText().toString().trim();
                if (StringUtils.isBlank(email)) {
                    ToastUtils.toastshort("请输入邮箱");
                    return;
                }
                checkEmail();
                break;
            case R.id.iv_finish:
                getPassWordAgain();
                break;
            case R.id.tv_find:
                finish();
                startActivity(new Intent(this,ForgetPasswordPhoneActivity.class));
                break;
        }
    }

    //点击验证码后判断邮箱是否存在
    protected void checkEmail() {
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
                            getcode();
                        }else{
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        ToastUtils.toastshort("网络错误");
                        return;
                    }
                });
    }

    /**
     * 获取验证码
     */
    private void getcode() {
        YzmDialog yzmDialog = new YzmDialog(this, R.style.YzmDialog, email);
        yzmDialog
                .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                    @Override
                    public void OnYzmDialogDismiss(String answer) {
                        //customProgressDialog.setContent("正在获取验证码...");
                        progressdialog.show();
                        Map<String, String> map = SortRequestData.getmap();
                        String token = CacheUtils.getString("token", null);
                        map.put("email", email);
                        map.put("sendType", 2+"");
                        map.put("codeType", 2+"");
                        map.put("captchaResult", answer);
                        map.put("token",token);
                        String requestData = SortRequestData.sortString(map);
                        String signData = SignUtil.sign(requestData);
                        map.put("sign", signData);
                        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SMS_URL, null, map, new HttpBackBaseListener() {

                            @Override
                            public void onSuccess(String string) {
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
        yzmDialog.show();

    }

    private void getPassWordAgain() {
        email = etEmail.getText().toString().trim();
        mima = etNewPassword.getText().toString().trim();
        String mima_confirm = etNewConfirm.getText().toString().trim();
        String code = etYanzhengma.getText().toString().trim();

        if (StringUtils.isBlank(email)) {
            ToastUtils.toastshort("请输入邮箱");
            return;
        }
        if (StringUtils.isBlank(mima)) {
            ToastUtils.toastshort("请输入密码！");
            return;
        }
        if (StringUtils.isBlank(code)) {
            ToastUtils.toastshort("请输入验证码！");
            return;
        }
        if (!mima.equals(mima_confirm)) {
            ToastUtils.toastshort("两次密码输入不一致，请重新输入");
            return;
        }
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        map.put("email", email);
        map.put("code", code);
        map.put("randomId", randomId);
        map.put("password", Base64.encode(SignUtil.encrypt(mima)));
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FINDPWD_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        progressdialog.dismiss();

                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            String token = json.getString("token");
                            CacheUtils.putString("token", token);
                            CacheUtils.putString(CacheUtils.LOGINPASSWORD, Base64.encode(SignUtil.encrypt(mima)));
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                            startActivity(intent);
                            finish();
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("找回密码失败");
                    }
                });
    }


    //显隐密码的方法
    private void showPassWord(boolean isOpenEye, ImageView imageView, EditText editText) {
        if (isOpenEye) {//打开眼睛图片，显示密码
            imageView.setImageResource(R.drawable.login_open);
            editText.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
        } else {//关闭眼睛图片，隐藏密码
            imageView.setImageResource(R.drawable.login_close);
            editText.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        }
        // 切换后将EditText光标置于末尾
        CharSequence charSequence = editText.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
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
            ivYanzheng.setVisibility(View.VISIBLE);//让获取图片重新显示
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCount.setText(millisUntilFinished / 1000 + "");//60秒倒计时
            tvCount.setTextColor(getResources().getColor(R.color.global_whitecolor));
            tvCount.setTextSize(10);
            pwSpinner.setProgress((int) (millisUntilFinished / 1000));//圆弧进度条
            tvCount.setClickable(false);
        }

    }
}
