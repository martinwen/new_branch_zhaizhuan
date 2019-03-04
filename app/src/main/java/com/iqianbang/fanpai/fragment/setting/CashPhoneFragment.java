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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wenguangjun on 2017/6/28.
 */

public class CashPhoneFragment extends BaseFragment {

    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_yanzhengma)
    EditText etYanzhengma;
    @BindView(R.id.pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.tv_yuyin)
    TextView tvYuyin;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.iv_eye1)
    ImageView ivEye1;


    private CustomProgressDialog progressdialog;
    private boolean isOpen = false;
    private boolean isOpen1 = false;
    private MyCount myCount;
    private String phone;
    private String randomId;

    @Override
    protected View initView() {
        progressdialog = new CustomProgressDialog(mActivity, "正在获取数据...");
        View view = View.inflate(mActivity, R.layout.fragment_tixian_phone, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

        //获取保存在本地的账号
        phone = CacheUtils.getString(CacheUtils.LOGINPHONE, "");
        //初始化密码显示状态
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R.id.iv_yanzheng, R.id.tv_yuyin, R.id.iv_btn, R.id.iv_eye, R.id.iv_eye1})
    public void onClick(View view) {
        String idNo = etId.getText().toString().trim();
        switch (view.getId()) {
            case R.id.iv_eye:
                isOpen = !isOpen;
                showPassWord(isOpen, ivEye, etPassword);
                break;
            case R.id.iv_eye1:
                isOpen1 = !isOpen1;
                showPassWord(isOpen1, ivEye1, etNewPassword);
                break;
            case R.id.iv_yanzheng:
                if (TextUtils.isEmpty(idNo)) {// 身份证号为空
                    ToastUtils.toastshort("请先输入身份证号哦");
                    return;
                }
                check(idNo, 0);
                break;
            case R.id.tv_yuyin:
                if (TextUtils.isEmpty(idNo)) {// 身份证号为空
                    ToastUtils.toastshort("请先输入身份证号哦");
                    return;
                }
                check(idNo, 1);
                break;
            case R.id.iv_btn:
                String inputPwd = etPassword.getText().toString().trim();
                String confirmPwd = etNewPassword.getText().toString().trim();
                String code = etYanzhengma.getText().toString().trim();
                requestServer(idNo, inputPwd, confirmPwd, code);
                break;
        }
    }

    private void requestServer(String idNo, final String inputPwd, String confirmPwd, String code) {
        // 身份证号不能为空
        if (TextUtils.isEmpty(idNo)) {
            ToastUtils.toastshort("身份证号不能为空");
            // 让EditText获取焦点
            etId.requestFocus();
            return;
        }
        // 新密码不能为空
        if (TextUtils.isEmpty(inputPwd)) {
            ToastUtils.toastshort("密码不能为空");
            // 让EditText获取焦点
            etPassword.requestFocus();
            return;
        }
        // 确认密码不能为空
        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtils.toastshort("密码不能为空");
            // 让EditText获取焦点
            etNewPassword.requestFocus();
            return;
        }
        // 判断两个新密码是否相同
        if (!TextUtils.equals(inputPwd, confirmPwd)) {
            ToastUtils.toastshort("两次新密码输入不一致,请重新输入");
            etNewPassword.requestFocus();
            return;
        }
        // 手机验证码不能为空
        if (TextUtils.isEmpty(code)) {
            ToastUtils.toastshort("手机验证码不能为空");
            // 让EditText获取焦点
            etYanzhengma.requestFocus();
            return;
        }
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("idNo", idNo);
        map.put("code", code);
        map.put("randomId", randomId);
        map.put("password", Base64.encode(SignUtil.encrypt(inputPwd)));
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SETCASHPWD_URL, null,
                map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        // TODO Auto-generated method stub
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            ToastUtils.toastshort("设置密码成功");
                            getActivity().finish();
                        } else {
                            ToastUtils.toastshort("密码设置失败！");
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("密码设置失败！");
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

    private void check(String idNo, final int sendType) {
        String token = CacheUtils.getString("token", "");
        Map<String, String> map = SortRequestData.getmap();
        map.put("idNo", idNo);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);

        VolleyUtil.sendJsonRequestByPost(ConstantUtils.VERIFYUSERIDNO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            YzmDialog yzmDialog_yuyin = new YzmDialog(getContext(), R.style.YzmDialog, phone);
                            yzmDialog_yuyin
                                    .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                                        @Override
                                        public void OnYzmDialogDismiss(String answer) {
                                            progressdialog.show();
                                            progressdialog.setContent("正在获取语音验证码...");
                                            String token = CacheUtils.getString("token", "");
                                            Map<String, String> map = SortRequestData.getmap();
                                            map.put("phone", phone);
                                            map.put("sendType", sendType + "");
                                            map.put("codeType", 4 + "");
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
                            yzmDialog_yuyin.show();
                        } else {
                            ToastUtils.toastshort("身份证不正确，请重新输入");
                            return;
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        LogUtils.i("网络错误");
                    }
                });
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
