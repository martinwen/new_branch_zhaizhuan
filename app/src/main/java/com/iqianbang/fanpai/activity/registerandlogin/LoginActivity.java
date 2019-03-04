package com.iqianbang.fanpai.activity.registerandlogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.service.RefreshTokenReceiver;
import com.iqianbang.fanpai.sign.Base64;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.RefTokenUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.lockPattern.GestureCreateActivity;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.qiyukf.unicorn.api.Unicorn;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_phone;
import static com.iqianbang.fanpai.R.id.iv_eye;

public class LoginActivity extends BaseActivity {

    @BindView(et_phone)
    EditText etPhone;
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(iv_eye)
    ImageView ivEye;
    @BindView(R.id.iv_login)
    ImageView ivLogin;
    @BindView(R.id.login_layout)
    LinearLayout loginLayout;
    @BindView(R.id.tv_forget)
    TextView tvForget;
    @BindView(R.id.tv_look)
    TextView tvLook;
    @BindView(R.id.tv_goto)
    TextView tvGoto;


    private CustomProgressDialog progressdialog;
    private boolean isOpen = false;//控制登录密码显隐
    private String phone;//电话号码
    private String mima;//密码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在登录......");
        initData();
        addLayoutListener(loginLayout, tvGoto);
    }

    /**
     * 1、获取main在窗体的可视区域
     * 2、获取main在窗体的不可视区域高度
     * 3、判断不可视区域高度
     * 1、大于100：键盘显示  获取Scroll的窗体坐标
     * 算出main需要滚动的高度，使scroll显示。
     * 2、小于100：键盘隐藏
     *
     * @param main   根布局
     * @param scroll 需要显示的最下方View
     */
    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2、获取main在窗体的不可视区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节度应该和rect.bottom高度一样
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                int screenHeight = main.getRootView().getHeight();//屏幕高度
                //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
                if (mainInvisibleHeight > screenHeight / 4) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    if (srollHeight > 0) {
                        main.scrollTo(0, srollHeight);
                    }
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    private void initData() {
        //解决进入登录界面没有弹出软键盘的问题
        etPhone.setFocusable(true);
        etPhone.setFocusableInTouchMode(true);
        etPhone.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) etPhone.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etPhone, 0);
            }

        }, 500);
        //初始化密码输入形式为不可见
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // 手机号输入框失去焦点监听，当输入格式不符合时，toast提示
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                phone = etPhone.getText().toString().trim();
                if (hasFocus) {
                } else {
                    if (!isMobile(phone)) {
                        ToastUtils.toastshort("手机号格式输入不正确");
                    }
                }
            }
        });

        // 密码输入框失去焦点监听，当输入格式不符合时，toast提示
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                mima = etPassword.getText().toString().trim();
                if (hasFocus) {
                } else {
                    if (!isMima(mima)) {
                        ToastUtils.toastshort("密码长度需在6—15位之间");
                    }
                }
            }
        });
    }

    public static boolean isMobile(String phone) {
        return phone.length() == 11;
    }

    public static boolean isMima(String mima) {
        return mima.length() >= 6 && mima.length() <= 15;
    }

    //输错5次手势密码后点返回时进入主页面，
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int flags = getIntent().getFlags();
        if (flags == 10) {
            //七鱼客服退出时清除聊天记录
            Unicorn.setUserInfo(null);
            // 访问网络，告知服务端用户已退出登录
            getDataFromServer();
            // 清除掉手势密码
            CacheUtils.putString(CacheUtils.BYTE, "");
            // 退出登录，数据清空
            CacheUtils.putString(CacheUtils.INVITECODE, "");
            CacheUtils.putString(CacheUtils.BASEACCTBAL, "");
            CacheUtils.putString(CacheUtils.REWARDUSEDAMOUNT, "");
            CacheUtils.putString(CacheUtils.BANKNAME, "");
            CacheUtils.putString(CacheUtils.BANKPIC, "");
            CacheUtils.putString(CacheUtils.REALNAME, "");
            CacheUtils.putString(CacheUtils.IDNO, "");
            CacheUtils.putString(CacheUtils.ADDRESS, "");
            CacheUtils.putString(CacheUtils.ZIPCODE, "");
            CacheUtils.putString(CacheUtils.CARDNUM, "");
            CacheUtils.putString(CacheUtils.CARDPHONE, "");
            CacheUtils.putString(CacheUtils.LOGINPHONE, "");
            CacheUtils.putString(CacheUtils.LOGINPASSWORD, "");
            CacheUtils.putString(CacheUtils.REWARDACCTBAL, "");
            CacheUtils.putString(CacheUtils.SINGLETRANSLIMIT, "");
            CacheUtils.putString(CacheUtils.SINGLEDAYLIMIT, "");
            CacheUtils.putString(CacheUtils.TOTALASSETS, "");
            CacheUtils.putString(CacheUtils.FHACCTBAL, "");
            CacheUtils.putString(CacheUtils.FWACCTBAL, "");
            CacheUtils.putString(CacheUtils.FTACCTBAL, "");
            CacheUtils.putString(CacheUtils.FHYESTERDAYINCOME, "");
            CacheUtils.putString(CacheUtils.FHTOTALINCOME, "");
            CacheUtils.putString(CacheUtils.DFTYESTERDAYINCOME, "");
            CacheUtils.putString(CacheUtils.DFTTOTALINCOME, "");
            CacheUtils.putString(CacheUtils.FTYESTERDAYINCOME, "");
            CacheUtils.putString(CacheUtils.FTTOTALINCOME, "");
            CacheUtils.putString(CacheUtils.TOTALRECHARGEAMOUNT, "");
            CacheUtils.putString(CacheUtils.TOTALCASHAMOUNT, "");
            CacheUtils.putString(CacheUtils.TOTALCASHINGMONEY, "");
            CacheUtils.putString("token", "");
            RefTokenUtils.stopRefresh(LoginActivity.this, RefreshTokenReceiver.class, MainActivity.REFRESH_RECEIVER);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    protected void getDataFromServer() {

        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.LOGOUT_URL, null,
                map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        // TODO Auto-generated method stub
                        LogUtils.i("调了退出登录的接口");

                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });
    }

    @OnClick({R.id.iv_cancel, iv_eye, R.id.tv_forget, R.id.iv_login, R.id.tv_look, R.id.tv_goto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel://取消按钮
                etPhone.getText().clear();
                break;
            case iv_eye://控制登录密码显隐
                isOpen = !isOpen;
                if (isOpen) {// 打开眼睛图片，显示密码
                    ivEye.setImageResource(R.drawable.login_open);
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                } else {// 关闭眼睛图片，隐藏密码
                    ivEye.setImageResource(R.drawable.login_close);
                    etPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                }
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = etPassword.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
            case R.id.tv_forget:
                startActivity(new Intent(this, ForgetPasswordPhoneActivity.class));
                break;
            case R.id.iv_login:
                login();
                break;
            case R.id.tv_look:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.tv_goto:
                startActivity(new Intent(this, RegisterStep1Activity.class));
                break;
        }
    }

    private void login() {

        //密码登录
        mima = etPassword.getText().toString().trim();
        phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {// 手机号码为空
            ToastUtils.toastshort("手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(mima)) {// 密码为空
            ToastUtils.toastshort("密码不能为空");
            return;
        }
        postToLogin(phone, mima);
    }

    private void postToLogin(final String phone, final String mima) {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        map.put("phone", phone);
        map.put("password", Base64.encode(SignUtil.encrypt(mima)));
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);

        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MIMA_LOGIN_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        progressdialog.dismiss();

                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            //登录成功后对之前手势密码连输错误5次情况的处理
                            CacheUtils.putBoolean("gestureDisable", false);
                            //登录成功保存token和登录手机号及密码，并且跳到绘制手势密码的界面
                            String token = json.getString("token");
                            CacheUtils.putString("token", token);
                            CacheUtils.putString(CacheUtils.LOGINPHONE, phone);
                            CacheUtils.putString(CacheUtils.LOGINPASSWORD, Base64.encode(SignUtil.encrypt(mima)));
                            //登录成功后设置手势密码
                            Intent intent = new Intent(LoginActivity.this, GestureCreateActivity.class);
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
                        ToastUtils.toastshort("网络请求失败");
                    }
                });
    }
}
