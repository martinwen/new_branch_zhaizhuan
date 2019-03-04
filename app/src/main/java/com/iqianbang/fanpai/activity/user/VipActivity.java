package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.invest.FanTongBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuySuccessActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_name;


public class VipActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.vip_normal)
    TextView vipNormal;
    @BindView(R.id.vip_core)
    TextView vipCore;
    @BindView(R.id.vip_highcore)
    TextView vipHighcore;
    @BindView(R.id.tv_to_core)
    TextView tvToCore;
    @BindView(R.id.tv_to_highcore)
    TextView tvToHighcore;
    @BindView(R.id.arrow_l)
    ImageView arrowL;
    @BindView(R.id.arrow_m)
    ImageView arrowM;
    @BindView(R.id.arrow_r)
    ImageView arrowR;
    @BindView(R.id.iv_icon1)
    ImageView ivIcon1;
    @BindView(R.id.iv_icon2)
    ImageView ivIcon2;
    @BindView(R.id.iv_icon3)
    ImageView ivIcon3;
    @BindView(R.id.iv_icon4)
    ImageView ivIcon4;
    @BindView(R.id.iv_icon5)
    ImageView ivIcon5;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_logo)
    TextView tvLogo;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_huiyuan)
    ImageView ivHuiyuan;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.iv_shengji)
    ImageView ivShengji;
    @BindView(R.id.tv_icon5_font)
    TextView tvIcon5Font;
    @BindView(R.id.ll_core)
    LinearLayout llCore;
    @BindView(R.id.tv_info)
    TextView tvInfo;


    private PopupWindow pop;// 银行选择的弹窗
    private CustomProgressDialog progressdialog;
    private String userMemberLevel;
    private String normalInfo;
    private String coreInfo;
    private String highcoreInfo;

//    private String normalFhRate;
//    private String coreFhRate;
//    private String highcoreFhRate;

    private String normalBirthday;
    private String coreBirthday;
    private String highcoreBirthday;

    private String normalCashTicket;
    private String coreCashTicket;
    private String highcoreCashTicket;

    private String normalInviteReward;
    private String coreInviteReward;
    private String highcoreInviteReward;

    private String normalSecrectPrize;
    private String coreSecrectPrize;
    private String highcoreSecrectPrize;
//    private String normalQuotaData;
//    private String coreQuotaData;
//    private String highcoreQuotaData;
    private String normalFwRate;
    private String coreFwRate;
    private String highcoreFwRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        getDataFromServer();
    }

    private void getDataFromServer() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MEMBEREQUITY_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("会员体系===" + string);
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

                                    //用户会员等级头部信息
                                    JSONObject user = data.getJSONObject("user");
                                    //会员名称
                                    String memberName = user.getString("memberName");
                                    //手机号和登录后的logo
                                    String userPhone = user.getString("userPhone");
                                    if (!TextUtils.isEmpty(memberName)) {//不为空则为登录状态，显示电话
                                        ivLogo.setImageResource(R.drawable.user_logo_ing);
                                        tvLogo.setVisibility(View.INVISIBLE);
                                        llPhone.setVisibility(View.VISIBLE);
                                        tvPhone.setText(userPhone);
                                    }

                                    //不同等级显示不同的会员logo
                                    userMemberLevel = user.getString("userMemberLevel");
                                    if ("0".equals(userMemberLevel)) {
                                        ivHuiyuan.setImageResource(R.drawable.vip_h_normal);
                                        //箭头的显隐
                                        arrowM.setVisibility(View.INVISIBLE);
                                        arrowR.setVisibility(View.INVISIBLE);
                                        //会员权益图标
                                        ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                                        ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                                        ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                                        ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                                        ivIcon5.setVisibility(View.INVISIBLE);
                                    } else if ("1".equals(userMemberLevel)) {
                                        ivHuiyuan.setImageResource(R.drawable.vip_h_core);
                                        //箭头的显隐
                                        arrowL.setVisibility(View.INVISIBLE);
                                        arrowM.setVisibility(View.VISIBLE);
                                        arrowR.setVisibility(View.INVISIBLE);
                                        //会员权益图标
                                        ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                                        ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                                        ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                                        ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                                        ivIcon5.setImageResource(R.drawable.vip_icon_5_ok);
                                        ivIcon5.setVisibility(View.VISIBLE);
                                        tvIcon5Font.setVisibility(View.VISIBLE);
                                    } else if ("2".equals(userMemberLevel)) {
                                        ivHuiyuan.setImageResource(R.drawable.vip_h_highcore);
                                        //箭头的显隐
                                        arrowL.setVisibility(View.INVISIBLE);
                                        arrowM.setVisibility(View.INVISIBLE);
                                        arrowR.setVisibility(View.VISIBLE);
                                        //会员权益图标
                                        ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                                        ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                                        ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                                        ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                                        ivIcon5.setImageResource(R.drawable.vip_icon_5_ok);
                                        ivIcon5.setVisibility(View.VISIBLE);
                                        tvIcon5Font.setVisibility(View.VISIBLE);
                                    }


                                    JSONObject upgrade = data.getJSONObject("upgrade");
                                    //升级说明
                                    JSONObject upgradeDescData = upgrade.getJSONObject("upgradeDescData");
                                    normalInfo = upgradeDescData.getString("0");
                                    coreInfo = upgradeDescData.getString("1");
                                    highcoreInfo = upgradeDescData.getString("2");

                                    // 升级出借金额
                                    JSONObject upgradeInfoData = upgrade.getJSONObject("upgradeInfoData");
                                    String coreMoney = upgradeInfoData.getString("1");
                                    if (!"".equals(coreMoney)) {//升级到核心会员
                                        tvToCore.setVisibility(View.VISIBLE);
                                        tvToCore.setText("再出借大饭团\n" + coreMoney + "元");
                                        //给文字增加下划线
                                        tvToCore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                                    }
                                    String highcoreMoney = upgradeInfoData.getString("2");
                                    String dftUpgradeHighDays = upgrade.getString("dftUpgradeHighDays");
                                    if (!"".equals(highcoreMoney)) {//升级到高级核心会员
                                        tvToHighcore.setVisibility(View.VISIBLE);
                                        tvToHighcore.setText("再出借"+dftUpgradeHighDays+"天及以上\n大饭团" + highcoreMoney + "万元");
                                        //给文字增加下划线
                                        tvToHighcore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                                    }

                                    // 会员权益
                                    JSONObject memberEquity = data.getJSONObject("memberEquity");
                                    // 小饭团收益率
//                                    JSONObject fhRateData = memberEquity.getJSONObject("fhRateData");
//                                    normalFhRate = fhRateData.getString("0");
//                                    coreFhRate = fhRateData.getString("1");
//                                    highcoreFhRate = fhRateData.getString("2");
                                    // 小饭团收益率限额
//                                    JSONObject fhQuotaData = memberEquity.getJSONObject("fhQuotaData");
//                                    normalQuotaData = fhQuotaData.getString("0");
//                                    coreQuotaData = fhQuotaData.getString("1");
//                                    highcoreQuotaData = fhQuotaData.getString("2");

                                    // 小饭团收益率
                                    JSONObject dftRateData = memberEquity.getJSONObject("dftRateData");
                                    normalFwRate = dftRateData.getString("0");
                                    coreFwRate = dftRateData.getString("1");
                                    highcoreFwRate = dftRateData.getString("2");
                                    // 生日福利
                                    JSONObject birthdayData = memberEquity.getJSONObject("birthdayData");
                                    normalBirthday = birthdayData.getString("0");
                                    coreBirthday = birthdayData.getString("1");
                                    highcoreBirthday = birthdayData.getString("2");
                                    // 提现票
                                    JSONObject cashTicketData = memberEquity.getJSONObject("cashTicketData");
                                    normalCashTicket = cashTicketData.getString("0");
                                    coreCashTicket = cashTicketData.getString("1");
                                    highcoreCashTicket = cashTicketData.getString("2");
                                    // 邀请奖励
                                    JSONObject inviteRewardData = memberEquity.getJSONObject("inviteRewardData");
                                    normalInviteReward = inviteRewardData.getString("0");
                                    coreInviteReward = inviteRewardData.getString("1");
                                    highcoreInviteReward = inviteRewardData.getString("2");
                                    // 神秘惊喜
                                    JSONObject secrectPrizeData = memberEquity.getJSONObject("secrectPrizeData");
                                    normalSecrectPrize = secrectPrizeData.getString("0");
                                    coreSecrectPrize = secrectPrizeData.getString("1");
                                    highcoreSecrectPrize = secrectPrizeData.getString("2");

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

    @OnClick({R.id.iv_back, R.id.iv_logo, R.id.tv_logo, R.id.tv_phone, R.id.iv_shengji, R.id.vip_normal,
            R.id.vip_core, R.id.vip_highcore, R.id.tv_to_core, R.id.tv_to_highcore, R.id.iv_icon1,
            R.id.iv_icon2, R.id.iv_icon3, R.id.iv_icon4, R.id.iv_icon5, R.id.tv_info})
    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_logo:
            case R.id.tv_logo:
                if (TextUtils.isEmpty(token)) {//登录状态
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.tv_info:
            case R.id.iv_shengji:
                Intent intent = new Intent(this, ShengJiActivity.class);
                intent.putExtra("normalInfo", normalInfo);
                intent.putExtra("coreInfo", coreInfo);
                intent.putExtra("highcoreInfo", highcoreInfo);
                startActivity(intent);
                break;
            case R.id.vip_normal:
                tvInfo.setVisibility(View.INVISIBLE);
                arrowL.setVisibility(View.VISIBLE);
                arrowM.setVisibility(View.INVISIBLE);
                arrowR.setVisibility(View.INVISIBLE);
                ivIcon5.setVisibility(View.INVISIBLE);
                tvIcon5Font.setVisibility(View.INVISIBLE);
                if ("0".equals(userMemberLevel)) {
                    //会员权益图标
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                } else {
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_no);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_no);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_no);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_no);
                }
                break;
            case R.id.vip_core:
                tvInfo.setVisibility(View.INVISIBLE);
                arrowL.setVisibility(View.INVISIBLE);
                arrowM.setVisibility(View.VISIBLE);
                arrowR.setVisibility(View.INVISIBLE);
                ivIcon5.setVisibility(View.VISIBLE);
                tvIcon5Font.setVisibility(View.VISIBLE);
                if ("1".equals(userMemberLevel)) {
                    //会员权益图标
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                    ivIcon5.setImageResource(R.drawable.vip_icon_5_ok);
                } else {
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_no);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_no);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_no);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_no);
                    ivIcon5.setImageResource(R.drawable.vip_icon_5_no);
                    if("0".equals(userMemberLevel)){
                        tvInfo.setVisibility(View.VISIBLE);
                        tvInfo.setText("您目前是普通会员，不能享受核心会员权益，查看会员升级说明 >");
                    }
                }
                break;
            case R.id.vip_highcore:
                tvInfo.setVisibility(View.INVISIBLE);
                arrowL.setVisibility(View.INVISIBLE);
                arrowM.setVisibility(View.INVISIBLE);
                arrowR.setVisibility(View.VISIBLE);
                ivIcon5.setVisibility(View.VISIBLE);
                tvIcon5Font.setVisibility(View.VISIBLE);
                if ("2".equals(userMemberLevel)) {
                    //会员权益图标
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_ok);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_ok);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_ok);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_ok);
                    ivIcon5.setImageResource(R.drawable.vip_icon_5_ok);
                } else {
                    ivIcon1.setImageResource(R.drawable.vip_icon_1_no);
                    ivIcon2.setImageResource(R.drawable.vip_icon_2_no);
                    ivIcon3.setImageResource(R.drawable.vip_icon_3_no);
                    ivIcon4.setImageResource(R.drawable.vip_icon_4_no);
                    ivIcon5.setImageResource(R.drawable.vip_icon_5_no);

                    if("0".equals(userMemberLevel)){
                        tvInfo.setVisibility(View.VISIBLE);
                        tvInfo.setText("您目前是普通会员，不能享受高级核心会员权益，查看会员升级说明 >");
                    }else if("1".equals(userMemberLevel)){
                        tvInfo.setVisibility(View.VISIBLE);
                        tvInfo.setText("您目前是核心会员，不能享受高级核心会员权益，查看会员升级说明 >");
                    }
                }
                break;
            case R.id.tv_to_core:
//                Intent intent1 = new Intent(this, MainActivity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent1.putExtra("from", FtMaxBuySuccessActivity.FLAG_MAXBUYSUCCESS);
//                startActivity(intent1);
//                break;
            case R.id.tv_to_highcore:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("from", FtMaxBuySuccessActivity.FLAG_MAXBUYSUCCESS);
                startActivity(intent1);
                break;
            case R.id.iv_icon1:
                View view1 = View.inflate(this, R.layout.vip_pop_1, null);
                TextView tv_name1 = (TextView) view1.findViewById(tv_name);
                tv_name1.setText("大饭团权益");
                TextView tv_normal = (TextView) view1.findViewById(R.id.tv_normal);
                tv_normal.setText("加息"+normalFwRate);
                TextView tv_core = (TextView) view1.findViewById(R.id.tv_core);
                tv_core.setText("加息"+coreFwRate);
                TextView tv_highcore = (TextView) view1.findViewById(R.id.tv_highcore);
                tv_highcore.setText("加息"+highcoreFwRate);
                pop = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(arrowL, LockPatternUtil.dip2px(this, -50), 0);
                break;
            case R.id.iv_icon2:
                View view2 = View.inflate(this, R.layout.vip_pop_2, null);
                TextView tv_name2 = (TextView) view2.findViewById(tv_name);
                tv_name2.setText("生日福利");
                TextView tv_context = (TextView) view2.findViewById(R.id.tv_context);
                if ("0".equals(userMemberLevel)) {
                    if (normalBirthday.contains("|")) {//接口文档说明        | 表示换行
                        String normal = normalBirthday.replaceAll("\\|", "\n");
                        tv_context.setText(normal);
                    } else {
                        tv_context.setText(normalBirthday);
                    }

                } else if ("1".equals(userMemberLevel)) {
                    if (coreBirthday.contains("|")) {//接口文档说明        | 表示换行
                        String core = coreBirthday.replaceAll("\\|", "\n");
                        tv_context.setText(core);
                    } else {
                        tv_context.setText(coreBirthday);
                    }

                } else if ("2".equals(userMemberLevel)) {
                    if (highcoreBirthday.contains("|")) {//接口文档说明        | 表示换行
                        String highcore = highcoreBirthday.replaceAll("\\|", "\n");
                        tv_context.setText(highcore);
                    } else {
                        tv_context.setText(highcoreBirthday);
                    }

                } else {//这种情况是未登录的状态，默认显示普通用户的生日福利
                    if (normalBirthday.contains("|")) {//接口文档说明        | 表示换行
                        String normal = normalBirthday.replaceAll("\\|", "\n");
                        tv_context.setText(normal);
                    } else {
                        tv_context.setText(normalBirthday);
                    }
                }

                pop = new PopupWindow(view2, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(arrowM, LockPatternUtil.dip2px(this, -50), 0);
                break;
            case R.id.iv_icon3:
                View view3 = View.inflate(this, R.layout.vip_pop_1, null);
                TextView tv_name3 = (TextView) view3.findViewById(tv_name);
                tv_name3.setText("提现票");
                TextView tv_normal3 = (TextView) view3.findViewById(R.id.tv_normal);
                tv_normal3.setText(normalCashTicket);
                TextView tv_core3 = (TextView) view3.findViewById(R.id.tv_core);
                tv_core3.setText(coreCashTicket);
                TextView tv_highcore3 = (TextView) view3.findViewById(R.id.tv_highcore);
                tv_highcore3.setText(highcoreCashTicket);
                pop = new PopupWindow(view3, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(arrowR, 0, 0);
                break;
            case R.id.iv_icon4:
                View view4 = View.inflate(this, R.layout.vip_pop_3, null);
                TextView tv_normal4 = (TextView) view4.findViewById(R.id.tv_normal);
                if (normalInviteReward.contains("|")) {//接口文档说明        | 表示换行
                    String normal = normalInviteReward.replaceAll("\\|", "\n");
                    tv_normal4.setText(normal);
                } else {
                    tv_normal4.setText(normalInviteReward);
                }
                TextView tv_core4 = (TextView) view4.findViewById(R.id.tv_core);
                if (coreInviteReward.contains("|")) {//接口文档说明        | 表示换行
                    String core = coreInviteReward.replaceAll("\\|", "\n");
                    tv_core4.setText(core);
                } else {
                    tv_core4.setText(coreInviteReward);
                }
                TextView tv_highcore4 = (TextView) view4.findViewById(R.id.tv_highcore);
                if (highcoreInviteReward.contains("|")) {//接口文档说明        | 表示换行
                    String highcore = highcoreInviteReward.replaceAll("\\|", "\n");
                    tv_highcore4.setText(highcore);
                } else {
                    tv_highcore4.setText(highcoreInviteReward);
                }

                pop = new PopupWindow(view4, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(ivIcon1, LockPatternUtil.dip2px(this, -50), 0);
                break;
            case R.id.iv_icon5:
                View view5 = View.inflate(this, R.layout.vip_pop_1, null);
                TextView tv_name5 = (TextView) view5.findViewById(tv_name);
                tv_name5.setText("神秘惊喜");
                TextView tv_normal5 = (TextView) view5.findViewById(R.id.tv_normal);
                tv_normal5.setText(normalSecrectPrize);
                TextView tv_core5 = (TextView) view5.findViewById(R.id.tv_core);
                tv_core5.setText(coreSecrectPrize);
                TextView tv_highcore5 = (TextView) view5.findViewById(R.id.tv_highcore);
                tv_highcore5.setText(highcoreSecrectPrize);
                pop = new PopupWindow(view5, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(ivIcon2, LockPatternUtil.dip2px(this, -50), 0);
                break;
        }
    }
}
