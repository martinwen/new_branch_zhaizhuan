package com.iqianbang.fanpai.activity.registerandlogin;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.NomalWebviewActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.XieYiDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_phone;

public class RegisterStep1Activity extends BaseActivity {

    @BindView(et_phone)
    EditText etPhone;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.iv_register)
    ImageView iv_register;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.tv_xieyi_jinzhi)
    TextView tvXieyiJinzhi;
    @BindView(R.id.login_layout)
    LinearLayout loginLayout;
    @BindView(R.id.tv_xieyi)
    TextView tvXieyi;
    @BindView(R.id.tv_gotologin)
    TextView tvGotologin;

    private CustomProgressDialog progressdialog;
    private boolean isOpen = false;//控制登录密码显隐
    private boolean isGou = false;// 协议前面的对勾，默认勾上
    private String phone;//电话号码
    private String mima;//密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在注册中...");
        initData();
        addLayoutListener(loginLayout, iv_register);
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
                        main.scrollTo(0, srollHeight + 100);
                    }
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    private void initData() {
        //给(已有账户？去登陆)增加下划线
        tvGotologin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
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
                    } else {
                        //检查手机号是否存在
                        checkPhone();
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
                    if (isContainChinese(mima)) {
                        ToastUtils.toastshort("密码不能包含中文");
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

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    //输入手机号框失去焦点后判断手机号是否存在
    protected void checkPhone() {
        Map<String, String> map = SortRequestData.getmap();
        map.put("phone", phone);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);

        VolleyUtil.sendJsonRequestByPost(ConstantUtils.VERIFYPHONE_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            //手机号没注册过
                        } else {
                            //手机号注册了或输入格式不正确等等情况
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });
    }

    @OnClick({R.id.iv_clear, R.id.iv_eye, R.id.iv_register, R.id.iv_check,
              R.id.tv_xieyi_jinzhi, R.id.tv_xieyi, R.id.tv_gotologin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear:
                etPhone.getText().clear();
                break;
            case R.id.iv_eye:
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
            case R.id.iv_register:
                phone = etPhone.getText().toString().trim();
                mima = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {// 手机号码为空
                    ToastUtils.toastshort("请输入手机号码");
                    return;
                }

                if (!isMima(mima)) {
                    ToastUtils.toastshort("密码长度需在6—15位之间");
                    return;
                }

                if (!isGou) {
                    XieYiDialog xieYiDialog = new XieYiDialog(this, R.style.YzmDialog, tvXieyi.getText().toString().trim());
                    xieYiDialog.show();
                    return;
                }

                checkJump();
                break;
            case R.id.iv_check:
                isGou = !isGou;
                if (isGou) {
                    ivCheck.setImageResource(R.drawable.login_register_check_ok);
                } else {
                    ivCheck.setImageResource(R.drawable.login_register_check_no);
                }
                break;
            case R.id.tv_xieyi_jinzhi:
                Intent intent = new Intent(this, NomalWebviewActivity.class);
                intent.putExtra("url", ConstantUtils.TOAGREEFORBID_URL);
                startActivity(intent);
                break;
            case R.id.tv_xieyi:
                startActivity(new Intent(this, XieYiActivity.class));
                break;
            case R.id.tv_gotologin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void checkJump() {
        Map<String, String> map = SortRequestData.getmap();
        map.put("phone", phone);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);

        VolleyUtil.sendJsonRequestByPost(ConstantUtils.VERIFYPHONE_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");
                        if ("0".equals(code)) {
                            //手机号没注册过
                            LogUtils.i("手机号没注册过");
                            Intent intent = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("mima", mima);
                            startActivity(intent);
                        } else {
                            //手机号注册了或输入格式不正确等等情况
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });
    }
}
