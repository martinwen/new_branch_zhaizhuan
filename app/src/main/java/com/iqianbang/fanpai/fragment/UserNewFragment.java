package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiBindBankActivity;
import com.iqianbang.fanpai.activity.user.JiangJinActivity;
import com.iqianbang.fanpai.activity.user.SanBiaoZaiTouActivity;
import com.iqianbang.fanpai.activity.user.SettingActivity;
import com.iqianbang.fanpai.activity.user.SettingForTiXianActivity;
import com.iqianbang.fanpai.activity.user.TiXianActivity;
import com.iqianbang.fanpai.activity.user.TiXianPiaoActivity;
import com.iqianbang.fanpai.activity.user.YueActivity;
import com.iqianbang.fanpai.activity.user.ZhanNeiXinActivity;
import com.iqianbang.fanpai.activity.user.ZhuanRangZaiTouActivity;
import com.iqianbang.fanpai.activity.user.ZongNewActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.calendar_huankuan.CalendarHuanKuanActivity;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Administrator on 2016/12/13.
 */

public class UserNewFragment extends BaseFragment {

    @BindView(R.id.iv_zhanneixin)
    ImageView ivZhanneixin;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_vip)
    ImageView ivVip;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_zong)
    TextView tvZong;
    @BindView(R.id.tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.iv_chongzhi)
    ImageView ivChongzhi;
    @BindView(R.id.iv_tixian)
    ImageView ivTixian;
    @BindView(R.id.rl_tixianpiao)
    RelativeLayout rlTixianpiao;
    @BindView(R.id.rl_yue)
    RelativeLayout rlYue;
    @BindView(R.id.tv_zhuanrang)
    TextView tvZhuanrang;
    @BindView(R.id.rl_zhuanrang)
    RelativeLayout rlZhuanrang;
    @BindView(R.id.tv_zhitou)
    TextView tvZhitou;
    @BindView(R.id.rl_zhitou)
    RelativeLayout rlZhitou;
    @BindView(R.id.rl_calendar)
    RelativeLayout rlCalendar;

    private CustomProgressDialog progressdialog;
    private boolean isOpen = true;
    private String totalAssets;
    private String scatterTotalAssets;
    private String totalScatterHasClearIncome;
    private String baseAcctBal;
    private String rewardAcctBal;
    private String totalCashingMoney;
    private String transferTotalAssets;
    private String totalTransferHasClearIncome;


    @Override
    protected View initView() {
        LogUtils.i("走我的页initView");
        View view = View.inflate(mActivity, R.layout.fragment_usernew, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.i("走我的页initData");
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        String token = CacheUtils.getString("token", "");
        if (!TextUtils.isEmpty(token)) {//登录状态
            // 访问网络
            getDataFromServer();
        }

    }

    public int dp2px(int dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        String token = CacheUtils.getString("token", "");
        if (!TextUtils.isEmpty(token)) {//登录状态
            refreshDataFromServer(token);
        }

    }

    private void refreshDataFromServer(String token) {

        Map<String, String> map = SortRequestData.getmap();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.NEWASSETSPAGE_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("我的页面new==返回刷新=" + string);
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


//                                    // 提现票数量
//                                    String cashTicketNum = data.getString("cashTicketNum");
//                                    tvTixianpiao.setText(cashTicketNum + "张");


                                    // 是否有未读的站内信
                                    Boolean hasNews = data.getBoolean("hasNews");
                                    if (hasNews) {
                                        ivZhanneixin.setImageResource(R.drawable.user_zhanneixin_hasnew);
                                    } else {
                                        ivZhanneixin.setImageResource(R.drawable.user_zhanneixin_nonew);
                                    }

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
                        ToastUtils.toastshort("网络连接失败，请检查网络");
                    }
                });
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
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.NEWASSETSPAGE_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("我的页面new===" + string);

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

                                    // 真实姓名
                                    String realName = data.getString("realName");
                                    CacheUtils.putString("realName", realName);

                                    // 会员等级
                                    String memberLevel = data.getString("memberLevel");
                                    if ("0".equals(memberLevel)) {
                                        ivVip.setImageResource(R.drawable.user_ic_normal);
                                    } else if ("1".equals(memberLevel)) {
                                        ivVip.setImageResource(R.drawable.user_ic_core);
                                    } else if ("2".equals(memberLevel)) {
                                        ivVip.setImageResource(R.drawable.user_ic_highcore);
                                    }

                                    // 手机号
                                    String phone = data.getString("phone");
                                    if (!TextUtils.isEmpty(phone)) {
                                        tvPhone.setText(fixNum(phone));
                                    }

                                    // 总资产
                                    totalAssets = data.getString("totalAssets");

                                    // 散标资产
                                    scatterTotalAssets = data.getString("scatterTotalAssets");
                                    // 散标已结收益
                                    totalScatterHasClearIncome = data.getString("totalScatterHasClearIncome");

                                    // 债转专区资产
                                    transferTotalAssets = data.getString("transferTotalAssets");
                                    // 债转专区已结收益
                                    totalTransferHasClearIncome = data.getString("totalTransferHasClearIncome");

                                    // 账户余额
                                    baseAcctBal = data.getString("baseAcctBal");

                                    // 奖金余额
                                    rewardAcctBal = data.getString("rewardAcctBal");

                                    // 提现中
                                    totalCashingMoney = data.getString("totalCashingMoney");

                                    // 已结收益
                                    String totalHasClearIncome = data.getString("totalHasClearIncome");

//                                    // 提现票数量
//                                    String cashTicketNum = data.getString("cashTicketNum");
//                                    tvTixianpiao.setText(cashTicketNum + "张");

                                    // 充值总金额
                                    String totalRechargeAmount = data.getString("totalRechargeAmount");
                                    CacheUtils.putString("totalRechargeAmount", totalRechargeAmount);

                                    // 提现总金额
                                    String totalCashAmount = data.getString("totalCashAmount");
                                    CacheUtils.putString("totalCashAmount", totalCashAmount);

                                    // 已使用奖金
                                    String rewardUsedAmount = data.getString("rewardUsedAmount");
                                    CacheUtils.putString("rewardUsedAmount", rewardUsedAmount);

                                    // 地址
                                    String address = data.getString("address");
                                    CacheUtils.putString("address", address);

                                    // 邮编
                                    String zipCode = data.getString("zipCode");
                                    CacheUtils.putString("zipCode", zipCode);

                                    // email
                                    String email = data.getString("email");
                                    LogUtils.i("email===" + email);
                                    CacheUtils.putString("email", email);

                                    // 是否绑卡
                                    Boolean isBindCard = data.getBoolean("isBindCard");
                                    CacheUtils.putBoolean("isBindCard", isBindCard);

                                    // 切换充值按钮
                                    Boolean rechargeSwitch = data.getBoolean("rechargeSwitch");
                                    if (rechargeSwitch) {
                                        ivChongzhi.setImageResource(R.drawable.usernew_btn_chongzhi_ok);
                                    } else {
                                        ivChongzhi.setImageResource(R.drawable.usernew_btn_chongzhi_not);
                                    }

                                    // 是否有未读的站内信
                                    Boolean hasNews = data.getBoolean("hasNews");
                                    if (hasNews) {
                                        ivZhanneixin.setImageResource(R.drawable.user_zhanneixin_hasnew);
                                    } else {
                                        ivZhanneixin.setImageResource(R.drawable.user_zhanneixin_nonew);
                                    }

                                    boolean isDataVisiable = CacheUtils.getBoolean("isDataVisiable", true);
                                    if (isDataVisiable) {
                                        ivEye.setImageResource(R.drawable.user_open);
                                        if (StrToNumber.strTodouble(totalAssets) != 0) {
                                            if (isAdded()) {//作此判断，防止Fragment UserFragment{5323d65c} not attached to Activity
                                                tvZong.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrow_right), null);
                                            }
                                            tvZong.setClickable(true);
                                        } else {
                                            tvZong.setClickable(false);
                                        }
                                        tvZong.setText(totalAssets + "元");

                                        tvLeiji.setText(totalHasClearIncome + "元");
                                        tvJiang.setText(rewardAcctBal + "元");
                                        tvYue.setText(baseAcctBal + "元");
                                        tvZhitou.setText(scatterTotalAssets + "元");
                                        tvZhuanrang.setText(transferTotalAssets + "元");
                                    } else {
                                        ivEye.setImageResource(R.drawable.user_close);
                                        tvZong.setText("***");
                                        tvLeiji.setText("***");
                                        tvJiang.setText("***");
                                        tvYue.setText("***");
                                    }

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
                        ToastUtils.toastshort("网络连接失败，请检查网络");
                    }
                });
    }

    //手机号码星号处理
    private String fixNum(String phone) {
        return phone = phone.substring(0, 3) + "****" + phone.substring(7);
    }

    @OnClick({R.id.iv_vip, R.id.iv_setting, R.id.iv_eye, R.id.tv_zong, R.id.tv_change, R.id.tv_jiang,
            R.id.iv_tixian, R.id.iv_chongzhi, R.id.rl_zhitou, R.id.rl_yue,
            R.id.rl_zhuanrang, R.id.rl_tixianpiao, R.id.iv_zhanneixin, R.id.rl_calendar})
    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
//            case R.id.iv_vip:
//                startActivity(new Intent(mActivity, VipActivity.class));
//                break;
            case R.id.iv_zhanneixin:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, ZhanNeiXinActivity.class));
                }
                break;
            case R.id.iv_setting:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SettingActivity.class));
                }
                break;
            case R.id.tv_change:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fr, new UserFragment()).commit();
                break;
            case R.id.tv_zong:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, ZongNewActivity.class);
                    intent.putExtra("totalAssets", totalAssets);
                    intent.putExtra("scatterTotalAssets", scatterTotalAssets);
                    intent.putExtra("transferTotalAssets", transferTotalAssets);
                    intent.putExtra("baseAcctBal", baseAcctBal);
                    intent.putExtra("rewardAcctBal", rewardAcctBal);
                    intent.putExtra("totalCashingMoney", totalCashingMoney);
                    startActivity(intent);
                }
                break;
            case R.id.iv_eye:
                isOpen = !isOpen;
                if (TextUtils.isEmpty(token)) {
                    if (isOpen) {//打开眼睛图片，显示
                        ivEye.setImageResource(R.drawable.user_open);
                        tvZong.setText("0.00元");
                        tvLeiji.setText("0.00元");
                        tvJiang.setText("0.00元");
                        tvYue.setText("0.00元");
                        tvZhitou.setText("0.00元");
                        tvZhuanrang.setText("0.00元");
                    } else {//关闭眼睛图片，隐藏
                        ivEye.setImageResource(R.drawable.user_close);
                        tvZong.setText("***");
                        tvLeiji.setText("***");
                        tvJiang.setText("***");
                        tvYue.setText("***");
                        tvZhitou.setText("***");
                        tvZhuanrang.setText("***");
                    }
                } else {
                    if (isOpen) {//打开眼睛图片，显示
                        CacheUtils.putBoolean("isDataVisiable", true);
                        ivEye.setImageResource(R.drawable.user_open);

                        // 访问网络，重新获取数据
                        getDataFromServer();
                    } else {//关闭眼睛图片，隐藏
                        CacheUtils.putBoolean("isDataVisiable", false);
                        ivEye.setImageResource(R.drawable.user_close);
                        tvZong.setText("***");
                        tvLeiji.setText("***");
                        tvJiang.setText("***");
                        tvYue.setText("***");
                        tvZhitou.setText("***");
                        tvZhuanrang.setText("***");
                    }
                }
                break;
            case R.id.tv_jiang:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, JiangJinActivity.class));
                }
                break;
            case R.id.iv_tixian:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    //提现第一步获取用户信息，先拿到是否已绑卡
                    requestCash();
                }
                break;
            case R.id.iv_chongzhi:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    //充值第一步获取用户信息
                    requestPut();
                }
                break;
            case R.id.rl_yue:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, YueActivity.class));
                }
                break;
            case R.id.rl_tixianpiao:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, TiXianPiaoActivity.class));
                }
                break;
            case R.id.rl_zhitou:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, SanBiaoZaiTouActivity.class);
                    intent.putExtra("scatterTotalAssets", scatterTotalAssets);
                    intent.putExtra("totalScatterHasClearIncome", totalScatterHasClearIncome);
                    startActivity(intent);
                }
                break;
            case R.id.rl_zhuanrang:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, ZhuanRangZaiTouActivity.class);
                    intent.putExtra("transferTotalAssets", transferTotalAssets);
                    intent.putExtra("totalTransferHasClearIncome", totalTransferHasClearIncome);
                    startActivity(intent);
                }
                break;
            case R.id.rl_calendar:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, CalendarHuanKuanActivity.class));
                }
                break;
        }
    }

    private void requestCash() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.CASHVERIFYINFO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("提现第一步获取用户信息===" + string);
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

                                    //账户余额
                                    String baseBal = data.getString("baseBal");

                                    //充值未出借金额
                                    String rechBal = data.getString("rechBal");

                                    // 是否绑卡
                                    boolean isBindCard = data.getBoolean("isBindCard");

                                    // 设置提现密码
                                    boolean hasCashPwd = data.getBoolean("hasCashPwd");

                                    // uuid
                                    String uuid = data.getString("uuid");

                                    // 真实姓名
                                    String realName = data.getString("realName");
                                    CacheUtils.putString("realName", realName);

                                    // 身份证号
                                    String idNo = data.getString("idNo");
                                    CacheUtils.putString("idNo", idNo);

                                    //银行图标
                                    String bankPic = data.getString("bankPic");
                                    CacheUtils.putString("bankPic", bankPic);

                                    //银行名称
                                    String bankName = data.getString("bankName");
                                    CacheUtils.putString("bankName", bankName);

                                    //银行卡号
                                    String cardNum = data.getString("cardNum");
                                    CacheUtils.putString("cardNum", cardNum);

                                    //单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    //单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    // 是否弹出提现提示
                                    Boolean whetherNeedPops = data.getBoolean("whetherNeedPops");

                                    if (!isBindCard) {// 如果还没有绑卡，去绑卡
                                        ToastUtils.toastshort("请先出借哦！");
                                    } else {//已经绑卡，去提现界面
                                        if (hasCashPwd) {
                                            Intent intent = new Intent(mActivity, TiXianActivity.class);
                                            intent.putExtra("baseBal", baseBal);
                                            intent.putExtra("rechBal", rechBal);
                                            intent.putExtra("uuid", uuid);
                                            intent.putExtra("whetherNeedPops", whetherNeedPops);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(mActivity, SettingForTiXianActivity.class);
                                            startActivity(intent);
                                        }
                                    }

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

    private void requestPut() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("type", "recharge");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GOTOINVEST_URL, null, map,
                new HttpBackBaseListener() {
                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("充值按钮===" + string);
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

                                    // 真实姓名
                                    String realName = data.getString("realName");
                                    CacheUtils.putString("realName", realName);

                                    // 身份证号
                                    String idNo = data.getString("idNo");
                                    CacheUtils.putString("idNo", idNo);

                                    // 银行名称
                                    String bankPic = data.getString("bankPic");
                                    CacheUtils.putString("bankPic", bankPic);

                                    // 开户行
                                    String bankName = data.getString("bankName");
                                    CacheUtils.putString("bankName", bankName);

                                    // 开户行编号
                                    String bankCode = data.getString("bankCode");

                                    // 银行卡号
                                    String cardNum = data.getString("cardNum");
                                    CacheUtils.putString("cardNum", cardNum);

                                    // 银行卡预留手机号
                                    String cardPhone = data.getString("cardPhone");

                                    //银行单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    //银行单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");

                                    //3:换卡（身份证号和姓名不允许输入），4新绑卡
                                    String isEnable = data.getString("isEnable");

                                    // 是否绑卡
                                    Boolean isBindCard = data.getBoolean("isBindCard");
                                    if (isBindCard) {//已经绑卡，去充值界面
                                        Intent intent = new Intent(mActivity, ChongZhiActivity.class);
                                        intent.putExtra("seqNo", seqNo);
                                        startActivity(intent);
                                    } else {// 如果还没有绑卡，去绑卡
                                        Intent intent = new Intent(mActivity, ChongZhiBindBankActivity.class);
                                        intent.putExtra("realName", realName);
                                        intent.putExtra("idNo", idNo);
                                        intent.putExtra("bankName", bankName);
                                        intent.putExtra("bankCode", bankCode);
                                        intent.putExtra("cardNum", cardNum);
                                        intent.putExtra("cardPhone", cardPhone);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("isEnable", isEnable);
                                        startActivity(intent);
                                    }

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

}
