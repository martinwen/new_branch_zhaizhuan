package com.iqianbang.fanpai.activity.registerandlogin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.iqianbang.fanpai.activity.home.ScanActivity;
import com.iqianbang.fanpai.sign.Base64;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.RoundProgressBar;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.YzmDialog;
import com.iqianbang.fanpai.view.lockPattern.GestureCreateActivity;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.et_yaoqing;
import static com.iqianbang.fanpai.R.id.tv_count;

public class RegisterStep2Activity extends BaseActivity {

    @BindView(et_yaoqing)
    EditText etYaoqing;
    @BindView(R.id.iv_yaoqing)
    ImageView ivYaoqing;
    @BindView(R.id.et_yanzheng)
    EditText etYanzheng;
    @BindView(R.id.iv_yanzheng)
    ImageView ivYanzheng;
    @BindView(R.id.iv_finish)
    ImageView iv_finish;
    @BindView(R.id.pw_spinner)
    RoundProgressBar pwSpinner;
    @BindView(tv_count)
    TextView tvCount;
    @BindView(R.id.tv_yuyin)
    TextView tvYuyin;

    private CustomProgressDialog progressdialog;
    private String phone;
    private String mima;
    private String yanzhengma;
    private String yaoqingma;
    private MyCount myCount;// 自定义倒计时类
    private String randomId;

    private static final int MY_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在获取短信验证码...");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        mima = intent.getStringExtra("mima");
    }

    /**
     * 扫描结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && resultCode == 0x02 && data != null) {
            if (data.getExtras().containsKey("result")) {
                // 让扫描出来的邀请码显示在这里
                String url = data.getExtras().getString("result");
                //截取字符串,从InviteFriendActivity中拿到url中的inviteCode
                int i = url.indexOf("ukey=");
                LogUtils.i("index=" + i);
                String inviteCode = url.substring(i + 5, i + 11);
                // 通过url来获取相应的邀请码
                etYaoqing.setText(inviteCode);
            }
        }
    }

    @OnClick({R.id.iv_yaoqing, R.id.iv_yanzheng, R.id.tv_yuyin, R.id.iv_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_yaoqing:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST);

                } else {
                    Intent intent = new Intent(this, ScanActivity.class);
                    startActivityForResult(intent, 0x01);
                }
                break;
            case R.id.iv_yanzheng:
                //sendType=0代表获取短信验证码
                getCode(0);
                break;
            case R.id.tv_yuyin:
                //sendType=1代表获取语音验证码
                getCode(1);
                break;
            case R.id.iv_finish:// 完成注册
                register();

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent, 0x01);
            } else {
                // Permission Denied
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                ToastUtils.toastshort("获取摄像头的权限被拒绝");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCode(final int type) {
        YzmDialog yzmDialog_yuyin = new YzmDialog(this, R.style.YzmDialog, phone);
        yzmDialog_yuyin
                .setOnYzmDialogDismissListener(new YzmDialog.OnYzmDialogDismissListener() {

                    @Override
                    public void OnYzmDialogDismiss(String answer) {
                        //调用接口调用语音验证码
                        progressdialog.show();
                        Map<String, String> map = SortRequestData.getmap();
                        map.put("phone", phone);
                        map.put("sendType", type + "");
                        map.put("codeType", 0 + "");
                        map.put("captchaResult", answer);
                        map.put("token", "");
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

                                    ivYanzheng.setVisibility(View.GONE);//获取验证码图片消失，倒计时显示
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
                                ToastUtils.toastshort("error--获取语音验证码失败");
                            }
                        });

                    }
                });
        yzmDialog_yuyin.show();
    }

    // 注册
    private void register() {
        yanzhengma = etYanzheng.getText().toString().trim();
        yaoqingma = etYaoqing.getText().toString().trim();

        if (TextUtils.isEmpty(yanzhengma)) {// 验证码为空
            ToastUtils.toastshort("验证码不能为空");
            return;
        }

        progressdialog.show();
        progressdialog.setContent("正在注册....");
        //获取UUID
        Map<String, String> map = SortRequestData.getmap();
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(
                ConstantUtils.GETUUID_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {// 验签成功
                                    progressdialog.show();
                                    progressdialog.setContent("正在注册....");
                                    Map<String, String> map = SortRequestData.getmap();
                                    map.put("phone", phone);
                                    map.put("code", yanzhengma);
                                    map.put("password", Base64.encode(SignUtil.encrypt(mima)));
                                    map.put("inviteCode", yaoqingma);
                                    map.put("randomId", randomId);
                                    map.put("uuid", datastr);
                                    String requestData = SortRequestData.sortString(map);
                                    String signData = SignUtil.sign(requestData);
                                    map.put("sign", signData);

                                    VolleyUtil.sendJsonRequestByPost(ConstantUtils.REGISTER_URL, null, map,
                                            new HttpBackBaseListener() {

                                                @Override
                                                public void onSuccess(String string) {
                                                    progressdialog.dismiss();
                                                    JSONObject json = JSON.parseObject(string);
                                                    String code = json.getString("code");
                                                    if ("0".equals(code)) {
                                                        String token = json.getString("token");
                                                        CacheUtils.putString("token", token);
                                                        CacheUtils.putString(CacheUtils.LOGINPHONE, phone);
                                                        CacheUtils.putString(CacheUtils.LOGINPASSWORD, Base64.encode(SignUtil.encrypt(mima)));
                                                        // 跳转到设置手势密码界面
                                                        Intent intent = new Intent(RegisterStep2Activity.this, GestureCreateActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                                                        startActivity(intent);
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
                                } else {
                                    progressdialog.dismiss();
                                    ToastUtils.toastshort("注册失败！");
                                }
                            }
                        } else {
                            progressdialog.dismiss();
                            ToastUtils.toastshort("注册失败！");
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("注册失败！");
                    }
                });

    }

    // 自定义倒计时类
    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 倒计时完要做的事情
            tvCount.setClickable(true);
            // 让获取图片重新显示
            ivYanzheng.setVisibility(View.VISIBLE);
            // 倒计时完让语音验证码可点击
            tvYuyin.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCount.setText(millisUntilFinished / 1000 + "");// 60秒倒计时
            tvCount.setTextColor(getResources().getColor(R.color.global_whitecolor));
            tvCount.setTextSize(9);
            pwSpinner.setProgress((int) (millisUntilFinished / 1000));// 圆弧进度条
            tvCount.setClickable(false);
            //获取验证码过程使语音验证码点击失效
            tvYuyin.setClickable(false);
        }
    }
}
