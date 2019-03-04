package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiBindBankActivity;
import com.iqianbang.fanpai.activity.user.FtJingYingZaiTouActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouActivity;
import com.iqianbang.fanpai.activity.user.FtMinZaiTouActivity;
import com.iqianbang.fanpai.activity.user.HongBaoActivity;
import com.iqianbang.fanpai.activity.user.JiaXiPiaoActivity;
import com.iqianbang.fanpai.activity.user.JiangJinActivity;
import com.iqianbang.fanpai.activity.user.SettingActivity;
import com.iqianbang.fanpai.activity.user.SettingForTiXianActivity;
import com.iqianbang.fanpai.activity.user.TiXianActivity;
import com.iqianbang.fanpai.activity.user.TiXianPiaoActivity;
import com.iqianbang.fanpai.activity.user.VipActivity;
import com.iqianbang.fanpai.activity.user.YouXianGouActivity;
import com.iqianbang.fanpai.activity.user.YueActivity;
import com.iqianbang.fanpai.activity.user.ZhanNeiXinActivity;
import com.iqianbang.fanpai.activity.user.ZongActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/12/13.
 */

public class UserFragment extends BaseFragment {

    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.tv_zong)
    TextView tvZong;
    @BindView(R.id.tv_zuori)
    TextView tvZuori;
    @BindView(R.id.tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.iv_tixian)
    ImageView ivTixian;
    @BindView(R.id.iv_chongzhi)
    ImageView ivChongzhi;
    @BindView(R.id.tv_jiaxipiao)
    TextView tvJiaxipiao;
    @BindView(R.id.tv_hongbao)
    TextView tvHongbao;
    @BindView(R.id.tv_tixianpiao)
    TextView tvTixianpiao;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.iv_vip)
    ImageView ivVip;
    @BindView(R.id.tv_ftmin)
    TextView tvFtmin;
    @BindView(R.id.tv_ftmax)
    TextView tvFtmax;
    @BindView(R.id.tv_jingying)
    TextView tvJingying;
    @BindView(R.id.iv_zhanneixin)
    ImageView ivZhanneixin;
    @BindView(R.id.tv_youxiangou)
    TextView tvYouxiangou;
    @BindView(R.id.ll_islogin)
    LinearLayout llIslogin;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.iv_viplogin)
    ImageView ivViplogin;
    @BindView(R.id.rl_notlogin)
    RelativeLayout rlNotlogin;
    @BindView(R.id.tv_change)
    TextView tvChange;

    private CustomProgressDialog progressdialog;
    private boolean isOpen = true;
    private String totalAssets;


    @Override
    protected View initView() {
        LogUtils.i("走我的页initView");
        View view = View.inflate(mActivity, R.layout.fragment_user, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.i("走我的页initData");
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        String token = CacheUtils.getString("token", "");
        if (!TextUtils.isEmpty(token)) {//登录状态
            llIslogin.setVisibility(View.VISIBLE);
            rlNotlogin.setVisibility(View.GONE);
            // 访问网络
            getDataFromServer();
        } else {//未登录状态
            llIslogin.setVisibility(View.INVISIBLE);
            rlNotlogin.setVisibility(View.VISIBLE);
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
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MY_ACCOUNT_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("我的页面==返回刷新=" + string);
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


                                    // 加息票数量
                                    String rateTicketNum = data.getString("rateTicketNum");
                                    tvJiaxipiao.setText("加息票：" + rateTicketNum + "张");

                                    // 红包金额
                                    String redBagAmount = data.getString("redBagAmount");
                                    tvHongbao.setText("红包：" + redBagAmount + "元");

                                    // 提现票数量
                                    String cashTicketNum = data.getString("cashTicketNum");
                                    tvTixianpiao.setText("提现票：" + cashTicketNum + "张");

                                    // 提现票数量
                                    String yxgTicketNum = data.getString("yxgTicketNum");
                                    tvYouxiangou.setText("优先购：" + yxgTicketNum + "张");


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
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MY_ACCOUNT_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("我的页面===" + string);

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

                                    // 真实姓名
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
                                    CacheUtils.putString("totalAssets", totalAssets);

                                    // 总的昨日收益
                                    String yesterdayIncome1 = data.getString("yesterdayIncome1");
                                    CacheUtils.putString("yesterdayIncome1", yesterdayIncome1);

                                    // 总的累计收益
                                    String totalIncome1 = data.getString("totalIncome1");
                                    CacheUtils.putString("totalIncome1", totalIncome1);

                                    // 饭盒昨日收益
                                    String fhYesterdayIncome = data.getString("fhYesterdayIncome");
                                    CacheUtils.putString("fhYesterdayIncome", fhYesterdayIncome);

                                    // 饭盒累计收益
                                    String fhTotalIncome = data.getString("fhTotalIncome");
                                    CacheUtils.putString("fhTotalIncome", fhTotalIncome);

                                    // 饭碗昨日收益
                                    String dftYesterdayIncome = data.getString("dftYesterdayIncome");
                                    CacheUtils.putString("dftYesterdayIncome", dftYesterdayIncome);

                                    // 饭碗已赚收益
                                    String dftTotalIncome = data.getString("dftTotalIncome");
                                    CacheUtils.putString("dftTotalIncome", dftTotalIncome);

                                    // 饭桶昨日收益
                                    String ftYesterdayIncome = data.getString("ftYesterdayIncome");
                                    CacheUtils.putString("ftYesterdayIncome", ftYesterdayIncome);

                                    // 饭桶累计收益
                                    String ftTotalIncome = data.getString("ftTotalIncome");
                                    CacheUtils.putString("ftTotalIncome", ftTotalIncome);

                                    // 充值总金额
                                    String totalRechargeAmount = data.getString("totalRechargeAmount");
                                    CacheUtils.putString("totalRechargeAmount", totalRechargeAmount);

                                    // 提现总金额
                                    String totalCashAmount = data.getString("totalCashAmount");
                                    CacheUtils.putString("totalCashAmount", totalCashAmount);
                                    // 提现中
                                    String totalCashingMoney = data.getString("totalCashingMoney");
                                    CacheUtils.putString("totalCashingMoney", totalCashingMoney);

                                    // 加息票数量
                                    String rateTicketNum = data.getString("rateTicketNum");
                                    tvJiaxipiao.setText("加息票：" + rateTicketNum + "张");

                                    // 红包金额
                                    String redBagAmount = data.getString("redBagAmount");
                                    tvHongbao.setText("红包：" + redBagAmount + "元");

                                    // 提现票数量
                                    String cashTicketNum = data.getString("cashTicketNum");
                                    tvTixianpiao.setText("提现票：" + cashTicketNum + "张");

                                    // 提现票数量
                                    String yxgTicketNum = data.getString("yxgTicketNum");
                                    tvYouxiangou.setText("优先购：" + yxgTicketNum + "张");

                                    // 奖金余额
                                    String rewardAcctBal = data.getString("rewardAcctBal");
                                    CacheUtils.putString("rewardAcctBal", rewardAcctBal);


                                    // 饭盒余额
                                    String fhAcctBal = data.getString("fhAcctBal");
                                    CacheUtils.putString("fhAcctBal", fhAcctBal);
                                    // 饭碗余额
                                    String fwAcctBal = data.getString("fwAcctBal");
                                    CacheUtils.putString("fwAcctBal", fwAcctBal);
                                    // 饭桶余额
                                    String ftAcctBal = data.getString("ftAcctBal");
                                    CacheUtils.putString("ftAcctBal", ftAcctBal);
                                    // 账户余额
                                    String baseAcctBal = data.getString("baseAcctBal");
                                    CacheUtils.putString("baseAcctBal", baseAcctBal);


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

                                        if(StrToNumber.strTodouble(yesterdayIncome1)==-9999){
                                            tvZuori.setText("结算中");
                                        }else{
                                            tvZuori.setText(yesterdayIncome1 + "元");
                                        }
                                        if(StrToNumber.strTodouble(totalIncome1)==-9999){
                                            tvLeiji.setText("结算中");
                                        }else{
                                            tvLeiji.setText(totalIncome1 + "元");
                                        }

                                        tvJiang.setText(rewardAcctBal + "元");
                                        tvYue.setText(baseAcctBal + "元");
                                        tvFtmin.setText(fhAcctBal + "元");
                                        tvFtmax.setText(fwAcctBal + "元");
                                        tvJingying.setText(ftAcctBal + "元");
                                    } else {
                                        ivEye.setImageResource(R.drawable.user_close);
                                        tvZong.setText("***");
                                        tvZuori.setText("***");
                                        tvLeiji.setText("***");
                                        tvJiang.setText("***");
                                        tvYue.setText("***");
                                        tvFtmin.setText("***");
                                        tvFtmax.setText("***");
                                        tvJingying.setText("***");
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

    @OnClick({R.id.tv_login, R.id.iv_vip, R.id.iv_viplogin, R.id.iv_setting, R.id.iv_eye, R.id.tv_zong,R.id.tv_change,
            R.id.iv_tixian, R.id.iv_chongzhi, R.id.tv_jiaxipiao, R.id.tv_youxiangou, R.id.tv_jiang, R.id.tv_yue,
            R.id.tv_hongbao, R.id.tv_tixianpiao, R.id.tv_ftmin, R.id.tv_ftmax, R.id.tv_jingying, R.id.iv_zhanneixin})
    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.tv_login:
                startActivity(new Intent(mActivity, LoginActivity.class));
                break;
//            case R.id.iv_viplogin:
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fr,new UserNewFragment()).commit();
                break;
            case R.id.tv_zong:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, ZongActivity.class));
                }
                break;
            case R.id.iv_eye:
                isOpen = !isOpen;
                if (TextUtils.isEmpty(token)) {
                    if (isOpen) {//打开眼睛图片，显示
                        ivEye.setImageResource(R.drawable.user_open);
                        tvZong.setText("0.00元");
                        tvZuori.setText("0.00元");
                        tvLeiji.setText("0.00元");
                        tvJiang.setText("0.00元");
                        tvYue.setText("0.00元");
                        tvFtmin.setText("0.00元");
                        tvFtmax.setText("0.00元");
                        tvJingying.setText("0.00元");
                    } else {//关闭眼睛图片，隐藏
                        ivEye.setImageResource(R.drawable.user_close);
                        tvZong.setText("***");
                        tvZuori.setText("***");
                        tvLeiji.setText("***");
                        tvJiang.setText("***");
                        tvYue.setText("***");
                        tvFtmin.setText("***");
                        tvFtmax.setText("***");
                        tvJingying.setText("***");
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
                        tvZuori.setText("***");
                        tvLeiji.setText("***");
                        tvJiang.setText("***");
                        tvYue.setText("***");
                        tvFtmin.setText("***");
                        tvFtmax.setText("***");
                        tvJingying.setText("***");
                    }
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
            case R.id.tv_jiaxipiao:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, JiaXiPiaoActivity.class));
                }
                break;
            case R.id.tv_hongbao:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, HongBaoActivity.class));
                }
                break;
            case R.id.tv_tixianpiao:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, TiXianPiaoActivity.class));
                }
                break;
            case R.id.tv_youxiangou:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, YouXianGouActivity.class));
                }
                break;
            case R.id.tv_jiang:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, JiangJinActivity.class));
                }
                break;
            case R.id.tv_yue:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, YueActivity.class));
                }
                break;
            case R.id.tv_ftmin:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, FtMinZaiTouActivity.class);
                    intent.putExtra("proCode", "fh");
                    startActivity(intent);
                }
                break;
            case R.id.tv_ftmax:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, FtMaxZaiTouActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_jingying:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    Intent intent = new Intent(mActivity, FtJingYingZaiTouActivity.class);
                    intent.putExtra("proCode", "ft");
                    startActivity(intent);
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
