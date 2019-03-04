package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.service.RefreshTokenReceiver;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.RefTokenUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.SettingOutDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.qiyukf.unicorn.api.Unicorn;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_bank)
    RelativeLayout rlBank;
    @BindView(R.id.rl_fixlogin)
    RelativeLayout rlFixlogin;
    @BindView(R.id.rl_fixtixian)
    RelativeLayout rlFixtixian;
    @BindView(R.id.rl_fixgesture)
    RelativeLayout rlFixgesture;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.rl_out)
    RelativeLayout rlOut;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.rl_ceping)
    RelativeLayout rlCeping;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;

    private CustomProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "...");
    }

    @OnClick({R.id.iv_back, R.id.rl_bank, R.id.rl_fixlogin, R.id.rl_fixtixian, R.id.rl_fixgesture, R.id.rl_address,
            R.id.rl_out, R.id.rl_phone, R.id.rl_ceping,R.id.rl_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_bank:
                requestServer();
                break;
            case R.id.rl_phone:
                startActivity(new Intent(this, SettingForPhoneActivity.class));
                break;
            case R.id.rl_email:
                String email = CacheUtils.getString(CacheUtils.EMAIL,"");
                if("".equals(email)){
                    startActivity(new Intent(this,SettingForEmailNotBindActivity.class));
                }else {
                    startActivity(new Intent(this,SettingForEmailActivity.class));
                }
                break;
            case R.id.rl_fixlogin:
                startActivity(new Intent(this, SettingForLoginActivity.class));
                break;
            case R.id.rl_fixtixian:
                startActivity(new Intent(this, SettingForTiXianActivity.class));
                break;
            case R.id.rl_fixgesture:
                startActivity(new Intent(this, GestureActivity.class));
                break;
            case R.id.rl_address:
                startActivity(new Intent(this, SettingForAddressActivity.class));
                break;
            case R.id.rl_ceping:
                startActivity(new Intent(this, SettingForCePingActivity.class));
                break;
            case R.id.rl_out:
                SettingOutDialog settingOutDialog = new SettingOutDialog(this, R.style.YzmDialog);
                settingOutDialog.show();
                settingOutDialog.setOnDialogDismissListener(new SettingOutDialog.OnDialogDismissListener() {

                    @Override
                    public void OnDialogDismiss() {
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
                        RefTokenUtils.stopRefresh(SettingActivity.this, RefreshTokenReceiver.class, MainActivity.REFRESH_RECEIVER);
                        //退出登录跳转到首页
                        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
                        startActivity(intent);
                        finish();
                    }
                });
                break;
        }
    }

    private void requestServer() {
        progressdialog.showis();
        String token = CacheUtils.getString("token", null);
        Map<String, String> map = SortRequestData.getmap();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.BANKCARD_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("我的银行卡===" + string);

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

                                    /*"data": {
                                        "isBindCard": "是否绑卡：true、false",
                                                "bankCode": "银行编号",
                                                "cardNum": "银行卡号",
                                                "bankName": "开户行",
                                                "bankPic": "银行卡图标",
                                                "realName": "真实姓名",
                                                "idNo": "身份证号",
                                                "singltTransLimit": "单笔限额",
                                                "singleDayLimit": "单日限额"
                                    },*/
                                    // 是否核心用户
                                    boolean isBindCard = data.getBoolean("isBindCard");
                                    if (isBindCard) {
                                        startActivity(new Intent(SettingActivity.this, SettingForBankHaveBindActivity.class));
                                    } else {
                                        startActivity(new Intent(SettingActivity.this, SettingForBankNotBindActivity.class));
                                    }

                                    // 银行卡号
                                    String cardNum = data.getString("cardNum");
                                    CacheUtils.putString("cardNum", cardNum);

                                    // 开户行
                                    String bankName = data.getString("bankName");
                                    CacheUtils.putString("bankName", bankName);

                                    // 银行卡图标
                                    String bankPic = data.getString("bankPic");
                                    CacheUtils.putString("bankPic", bankPic);

                                    // 单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    // 单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    // 姓名
                                    String realName = data.getString("realName");
                                    CacheUtils.putString("realName", realName);

                                    // 身份证
                                    String idNo = data.getString("idNo");
                                    CacheUtils.putString("idNo", idNo);


                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
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
}
