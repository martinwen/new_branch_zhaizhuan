package com.iqianbang.fanpai.fragment.setting;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.fragment.BaseFragment;
import com.iqianbang.fanpai.sign.Base64;
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

import static com.iqianbang.fanpai.R.id.et_new_confirm;
import static com.iqianbang.fanpai.R.id.et_new_password;
import static com.iqianbang.fanpai.R.id.et_password;
import static com.iqianbang.fanpai.R.id.et_yanzhengma;
import static com.iqianbang.fanpai.R.id.iv_eye;
import static com.iqianbang.fanpai.R.id.pw_spinner;
import static com.iqianbang.fanpai.R.id.tv_count;

/**
 * Created by wenguangjun on 2017/6/28.
 */

public class LoginPhoneFragment extends BaseFragment {

    @BindView(et_password)
    EditText etPassword;
    @BindView(iv_eye)
    ImageView ivEye;
    @BindView(et_new_password)
    EditText etNewPassword;
    @BindView(R.id.iv_eye1)
    ImageView ivEye1;
    @BindView(et_new_confirm)
    EditText etNewConfirm;
    @BindView(R.id.iv_eye2)
    ImageView ivEye2;
    @BindView(et_yanzhengma)
    EditText etYanzhengma;
    @BindView(pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(tv_count)
    TextView tvCount;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.tv_yuyin)
    TextView tvYuyin;
    @BindView(R.id.iv_password)
    ImageView ivPassword;


    private CustomProgressDialog progressdialog;
    private boolean isOpen = false;
    private boolean isOpen1 = false;
    private boolean isOpen2 = false;
    private MyCount myCount;
    private String phone;
    private String randomId;

    @Override
    protected View initView() {
        progressdialog = new CustomProgressDialog(mActivity, "正在获取数据...");
        View view = View.inflate(mActivity, R.layout.fragment_login_phone, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        //获取保存在本地的账号和密码
        phone = CacheUtils.getString(CacheUtils.LOGINPHONE, "");
        //初始化密码显示状态
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());


        // 密码输入框失去焦点监听，当输入格式不符合时，toast提示
//        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                // TODO Auto-generated method stub
//                String mima = etPassword.getText().toString().trim();
//                if (hasFocus) {
//                } else {
//                    if (!isMima(mima)) {
//                        ToastUtils.toastshort("密码长度需在6—15位之间");
//                    }
//                    if (isContainChinese(mima)) {
//                        ToastUtils.toastshort("密码不能包含中文");
//                    }
//                }
//            }
//        });
        // 密码输入框失去焦点监听，当输入格式不符合时，toast提示
        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                String mima = etPassword.getText().toString().trim();
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
        // 密码输入框失去焦点监听，当输入格式不符合时，toast提示
        etNewConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                String mima = etPassword.getText().toString().trim();
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

    public static boolean isMima(String mima) {
        return mima.length() >= 6 && mima.length() <= 15;
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    @OnClick({R.id.iv_eye, R.id.iv_eye1, R.id.iv_eye2, R.id.iv_yanzheng, R.id.tv_yuyin, R.id.iv_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_eye:
                isOpen = !isOpen;
                showPassWord(isOpen, ivEye, etPassword);
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
                getCode(0);
                break;
            case R.id.tv_yuyin:
                getCode(1);
                break;
            case R.id.iv_password:
                requestServer();
                break;
        }
    }

    private void requestServer() {
        String Pwd = etPassword.getText().toString().trim();
        String inputPwd = etNewPassword.getText().toString().trim();
        final String confirmPwd = etNewConfirm.getText().toString().trim();
        String code = etYanzhengma.getText().toString().trim();
        // 先判断原密码不能为空
        if (TextUtils.isEmpty(Pwd)) {
            ToastUtils.toastshort("原密码不能为空");
            // 让EditText获取焦点
            etPassword.requestFocus();
            return;
        }
        // 新密码不能为空
        if (TextUtils.isEmpty(inputPwd)) {
            ToastUtils.toastshort("新密码不能为空");
            // 让EditText获取焦点
            etNewPassword.requestFocus();
            return;
        }
        // 确认密码不能为空
        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtils.toastshort("确认密码不能为空");
            // 让EditText获取焦点
            etNewConfirm.requestFocus();
            return;
        }
        // 判断原密码和新密码是否相同
        if (TextUtils.equals(Pwd, inputPwd)) {
            ToastUtils.toastshort("新密码与原密码相同,请重新输入");
            etNewPassword.requestFocus();
            return;
        }
        // 判断两个新密码是否相同
        if (!TextUtils.equals(inputPwd, confirmPwd)) {
            ToastUtils.toastshort("两次新密码输入不一致,请重新输入");
            etNewConfirm.requestFocus();
            return;
        }

        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("code", code);
        map.put("token", token);
        map.put("newPass", Base64.encode(SignUtil.encrypt(confirmPwd)));
        map.put("oldPass", Base64.encode(SignUtil.encrypt(Pwd)));
        map.put("randomId", randomId);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.UPDATEPWD_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("string=="+string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            CacheUtils.putString(CacheUtils.LOGINPASSWORD, Base64.encode(SignUtil.encrypt(confirmPwd)));
                            ToastUtils.toastshort("修改成功");
                            getActivity().finish();
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("修改密码失败");
                    }
                });
    }

    private void getCode(final int sendType) {
        YzmDialog yzmDialog_sms = new YzmDialog(getContext(), R.style.YzmDialog, phone);
        yzmDialog_sms
                .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                    @Override
                    public void OnYzmDialogDismiss(String answer) {
                        // TODO Auto-generated method stub
                        progressdialog.show();
                        String token = CacheUtils.getString("token", "");
                        Map<String, String> map = SortRequestData.getmap();
                        map.put("phone", phone);
                        map.put("sendType", sendType + "");
                        map.put("codeType", 3 + "");
                        map.put("captchaResult", answer);
                        map.put("token", token);
                        String requestData = SortRequestData.sortString(map);
                        String signData = SignUtil.sign(requestData);
                        map.put("sign", signData);
                        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SMS_URL, null, map, new HttpBackBaseListener() {

                            @Override
                            public void onSuccess(String string) {
                                progressdialog.dismiss();
                                JSONObject json = JSON.parseObject(string);
                                String code = json.getString("code");
                                if ("0".equals(code)) {
                                    String datastr = json.getString("data");
                                    JSONObject data = JSON.parseObject(datastr);
                                    randomId = data.getString("randomId");

                                    ivYanzheng.setVisibility(View.GONE);
                                    myCount = new MyCount(60000, 1000);// 设置倒计时时间为60秒，间隔为1秒
                                    myCount.start();
                                } else {
                                    ToastUtils.toastshort("获取验证码失败");
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
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
            if (isAdded()) {//作此判断，防止Fragment UserFragment{5323d65c} not attached to Activity
                tvCount.setTextColor(getResources().getColor(R.color.global_whitecolor));
            }
            tvCount.setTextSize(10);
            pwSpinner.setProgress((int) (millisUntilFinished / 1000));//圆弧进度条
            tvCount.setClickable(false);
        }

    }
}
