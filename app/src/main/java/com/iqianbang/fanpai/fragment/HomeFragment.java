package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.home.FanJuActivity;
import com.iqianbang.fanpai.activity.home.GuanFangActivity;
import com.iqianbang.fanpai.activity.home.InviteFriendActivity;
import com.iqianbang.fanpai.activity.home.SignInActivity;
import com.iqianbang.fanpai.activity.home.SignInNotActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/12/13.
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.tv_yaoqinghaoyou)
    TextView tvYaoqinghaoyou;
    @BindView(R.id.tv_fanju)
    TextView tvFanju;
    @BindView(R.id.tv_zhanneixin)
    TextView tvZhanneixin;
    @BindView(R.id.tv_qiandao)
    TextView tvQiandao;
    @BindView(R.id.tv_qitou)
    TextView tvQitou;
    @BindView(R.id.tv_shouyi_other)
    TextView tvShouyiOther;
    @BindView(R.id.tv_xiane)
    TextView tvXiane;
    @BindView(R.id.iv_buy)
    ImageView ivBuy;
    @BindView(R.id.iv_zhanneixin_tip)
    ImageView ivZhanneixinTip;
    @BindView(R.id.tv_limit_money)
    TextView tvLimitMoney;
    @BindView(R.id.tv_tedian)
    TextView tvTedian;
    @BindView(R.id.tv_shouyi_base)
    TextView tvShouyiBase;

    private CustomProgressDialog progressdialog;
    private String proCode;
    public static String FLAG_GOTOSMALLTUAN = "flag_gotosmalltuan";
    public static String FLAG_GOTOBIGTUAN = "flag_gotobigtuan";
    public static String FLAG_GOTOJYTUAN = "flag_gotojytuan";

    @Override
    protected View initView() {
        LogUtils.i("走首页initView");
        View view = View.inflate(mActivity, R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.i("走首页initData");
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        String token = CacheUtils.getString("token", "");
        // 访问网络，初始化默认值
        getDataFromServer(token);
    }

    private void getDataFromServer(String token) {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.INDEX_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("首页===" + string);

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

                                    // 最低出借
                                    String singleMinInvestAmount = data.getString("singleMinInvestAmount");


                                    // 推荐产品类型
                                    proCode = data.getString("proCode");
                                    if ("fh".equals(proCode)) {
                                        // 饭盒加息收益率
                                        String addRate = data.getString("addRate");
                                        tvShouyiOther.setText("+" + addRate + "%");
                                        // 基础收益率
                                        String baseRate = data.getString("baseRate");
                                        tvShouyiBase.setText(baseRate + "%");
                                        // 起投
                                        tvQitou.setText(singleMinInvestAmount + "元");
                                        // 每人限额
                                        tvLimitMoney.setText("最高限额");
                                        String balMaxAmount = data.getString("balMaxAmount");
                                        tvXiane.setText(balMaxAmount + "万元");
                                        // 特点
                                        tvTedian.setText("灵活存取");
                                    } else if ("fw".equals(proCode)) {
                                        // 饭碗加息收益率下限
                                        String minRate = data.getString("minRate");
                                        // 饭碗加息收益率上限
                                        String maxRate = data.getString("maxRate");
                                        tvShouyiOther.setText("+" + minRate + "%" + "~" + maxRate + "%");
                                        // 基础收益率
                                        String baseRate = data.getString("baseRate");
                                        tvShouyiBase.setText(baseRate + "%");
                                        // 起投
                                        tvQitou.setText(singleMinInvestAmount + "元");
                                        // 单笔限额
                                        tvLimitMoney.setText("单笔限额");
                                        String singleMaxInvestAmount = data.getString("singleMaxInvestAmount");
                                        tvXiane.setText(singleMaxInvestAmount + "万元");
                                        // 特点
                                        tvTedian.setText("期限自由");
                                    } else if ("ft".equals(proCode)) {
                                        tvShouyiOther.setVisibility(View.INVISIBLE);
                                        // 基础收益率
                                        String baseRate = data.getString("baseRate");
                                        tvShouyiBase.setText(baseRate + "%");
                                        // 起投
                                        tvQitou.setText(singleMinInvestAmount + "万元");
                                        // 单笔限额
                                        tvLimitMoney.setText("单笔限额");
                                        String singleMaxInvestAmount = data.getString("singleMaxInvestAmount");
                                        tvXiane.setText(singleMaxInvestAmount + "万元");
                                        // 特点
                                        tvTedian.setText("高端定制");
                                    }

                                    // 是否有新消息
                                    boolean hasNews = data.getBoolean("hasNews");
                                    if (hasNews) {
                                        ivZhanneixinTip.setVisibility(View.VISIBLE);
                                    } else {
                                        ivZhanneixinTip.setVisibility(View.INVISIBLE);
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

    @OnClick({R.id.tv_yaoqinghaoyou, R.id.tv_zhanneixin, R.id.tv_qiandao, R.id.iv_buy, R.id.tv_fanju})
    public void onClick(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.tv_yaoqinghaoyou:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, InviteFriendActivity.class));
                }
                break;
            case R.id.tv_zhanneixin:
                startActivity(new Intent(mActivity, GuanFangActivity.class));
                break;
            case R.id.tv_qiandao:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    //获取当前年月日
                    Calendar calendar = Calendar.getInstance();
                    int currentYear = calendar.get(Calendar.YEAR);
                    int currentMonth = calendar.get(Calendar.MONTH);
                    String date = currentYear + "-" + (currentMonth + 1);

                    isSigninable(date);
                }
                break;
            case R.id.tv_fanju:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    startActivity(new Intent(mActivity, FanJuActivity.class));
                }
                break;
            case R.id.iv_buy:
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if ("fh".equals(proCode)) {
                    intent.putExtra("from", FLAG_GOTOSMALLTUAN);
                } else if ("fw".equals(proCode)) {
                    intent.putExtra("from", FLAG_GOTOBIGTUAN);
                } else if ("ft".equals(proCode)) {
                    intent.putExtra("from", FLAG_GOTOJYTUAN);
                }
                startActivity(intent);
                break;
        }
    }

    private void isSigninable(String date) {

        progressdialog.show();
        LogUtils.i("当前签到时间===" + date);
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("date", date);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SIGNINLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("当前签到记录===" + string);
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
                                    //是否可签到
                                    boolean signInAble = data.getBoolean("signInAble");

                                    //不可签到的显示信息
                                    String coreUserUrl = data.getString("coreUserUrl");

                                    if (signInAble) {
                                        startActivity(new Intent(mActivity, SignInActivity.class));
                                    } else {
                                        Intent intent = new Intent(mActivity, SignInNotActivity.class);
                                        intent.putExtra("coreUserUrl", coreUserUrl);
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
                        // TODO Auto-generated method stub
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

}
