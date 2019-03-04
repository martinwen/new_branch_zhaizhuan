package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.fragment.BidListFragment;
import com.iqianbang.fanpai.fragment.DftProjectInfoFragment;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CePingAgainDialog;
import com.iqianbang.fanpai.view.dialog.CePingChanceOverDialog;
import com.iqianbang.fanpai.view.dialog.CePingNotDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.IKnowDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FtMaxDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_rate_base)
    TextView tvRateBase;
    @BindView(R.id.tv_rate_add)
    TextView tvRateAdd;
    @BindView(R.id.tv_wenhao)
    TextView tvWenhao;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.tv_method)
    TextView tvMethod;
    @BindView(R.id.tv_min_money)
    TextView tvMinMoney;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tv_borrow_money)
    TextView tvBorrowMoney;
    @BindView(R.id.iv_calculation)
    ImageView ivCalculation;
    @BindView(R.id.bt_buy)
    Button btBuy;

    private String[] titles = new String[]{"项目介绍", "标的组成", "服务协议"};
    private List<Fragment> fragments;
    private CustomProgressDialog progressdialog;
    private String dftProInfoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftmax_detail);
        progressdialog = new CustomProgressDialog(this, "买入确认中...");
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //界面填充数据
        dftProInfoId = getIntent().getStringExtra("dftProInfoId");
        getDataFromServer(dftProInfoId);

        fragments = new ArrayList<Fragment>();

        DftProjectInfoFragment projectInfoFragment = new DftProjectInfoFragment();
        projectInfoFragment.setUrl(ConstantUtils.TODFTPRODUCTINTROPAGE_URL);

        BidListFragment bidListFragment = new BidListFragment();
        bidListFragment.setData(dftProInfoId);

        DftProjectInfoFragment xieyiFragment = new DftProjectInfoFragment();
        xieyiFragment.setUrl(ConstantUtils.TODFTSERVICEPROTOCOLPAGE_URL);

        fragments.add(projectInfoFragment);
        fragments.add(bidListFragment);
        fragments.add(xieyiFragment);

        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        //关联一下指示器
        tablayout.setupWithViewPager(viewpager);

    }

    private void getDataFromServer(String dftProInfoId) {
        progressdialog.show();
        String token = CacheUtils.getString("token", "");
        Map<String, String> map = SortRequestData.getmap();
        map.put("dftProInfoId", dftProInfoId);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWDFTPRODETAILINFO_URL,
            null, map, new HttpBackBaseListener() {

                @Override
                public void onSuccess(String string) {
                    LogUtils.i("（集合标）产品详情==" + string);
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

                                // 集合标id
                                String id = data.getString("id");

                                // 产品名称
                                String productName = data.getString("productName");
                                tvTitle.setText(productName);

                                // 基础利率
                                String baseRate = data.getString("baseRate");
                                tvRateBase.setText(baseRate + "%");

                                // 活动加息
                                String totalActivityRate = data.getString("totalActivityRate");
                                tvRateAdd.setText("+" + totalActivityRate + "%");

                                // 锁定期限
                                String term = data.getString("term");
                                tvTerm.setText(term + "天");

                                // 回款方式
                                String repaymentMethod = data.getString("repaymentMethod");
                                tvMethod.setText(repaymentMethod);

                                // 大饭团最低起头金额
                                String dftMinInvestAmount = data.getString("dftMinInvestAmount");
                                tvMinMoney.setText(dftMinInvestAmount + "元");

                                // 剩余金额
                                String remainingMoney = data.getString("remainingMoney");
                                // 集合标募集金额
                                String borrowMoney = data.getString("borrowMoney");
                                tvBorrowMoney.setText(remainingMoney + "/" +borrowMoney);

                                // 预售时间
                                String publishedTime = data.getString("publishedTime");

                                //  2 预售、 3  可售、  4 已售完, 5 已截标 // 4、5对应已售完
                                String status = data.getString("status");
                                // 当status 为3的时候， 才会关注settleSwitch、fwSalableSwitch开关
                                // status= 3  settleSwitch为true代表结算中，为false的时候再看fwSalableSwitch 为false 代表产品关闭
                                Boolean settleSwitch = data.getBoolean("settleSwitch");
                                Boolean fwSalableSwitch = data.getBoolean("fwSalableSwitch");

                                if("2".equals(status)){
                                    btBuy.setText(publishedTime+"开抢");
                                }

                                if("3".equals(status)){
                                    if(settleSwitch){
                                        btBuy.setText("结算时间，暂不支持买入");
                                    }else {
                                        if(!fwSalableSwitch){
                                            btBuy.setText("暂不支持买入");
                                        }else{
                                            btBuy.setEnabled(true);
                                            btBuy.setText("立即出借");
                                            btBuy.setBackgroundColor(getResources().getColor(R.color.text_red_dark));
                                        }
                                    }

                                }

                                if("4".equals(status)||"5".equals(status)){
                                    btBuy.setText("已售完，请关注其他同类产品");
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
                    ToastUtils.toastshort("加载数据失败！");
                }

            });
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_wenhao, R.id.iv_calculation, R.id.bt_buy})
    public void onViewClicked(View view) {
        String token = CacheUtils.getString("token", "");
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_wenhao:
                IKnowDialog iKnowDialog = new IKnowDialog(this, R.style.YzmDialog,
                        "历史年化结算利率为同类项目历史平均表现，以扣除平台技术服务费后的实际结算利率为准。");
                iKnowDialog.show();
                break;
            case R.id.iv_calculation:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(this, LoginActivity.class));
                } else {//登录
                    Intent intent = new Intent(this, FtMaxRateActivity.class);
                    intent.putExtra("dftProInfoId",dftProInfoId);
                    startActivity(intent);
                }
                break;
            case R.id.bt_buy:
                if (TextUtils.isEmpty(token)) {//未登录时，点击进入登录页面
                    startActivity(new Intent(this, LoginActivity.class));
                } else {//登录

                    isNeedEvaluattion();
                }
                break;
        }
    }

    private void isNeedEvaluattion() {
        if (!progressdialog.isShowing()) {
            progressdialog.showis();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GETEVALUATIONINFOSTATUS_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("评测结果===" + string);
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

                                    // 是否测评
                                    Boolean hasEvln = data.getBoolean("hasEvln");
                                    // 是否可出借
                                    Boolean canInvest = data.getBoolean("canInvest");
                                    // 测评剩余次数
                                    String overTimes = data.getString("overTimes");
                                    if (hasEvln) {//评测过
                                        if (canInvest) {//评测过并可出借

                                            gotoInvest();
                                        } else {//评测过不可出借
                                            if (StrToNumber.strTodouble(overTimes) > 0) {//还有评测次数
                                                CePingAgainDialog cePingAgainDialog = new CePingAgainDialog(FtMaxDetailActivity.this, R.style.YzmDialog, overTimes);
                                                cePingAgainDialog.show();
                                            } else {//没有评测次数
                                                CePingChanceOverDialog cePingChanceOverDialog = new CePingChanceOverDialog(FtMaxDetailActivity.this, R.style.YzmDialog);
                                                cePingChanceOverDialog.show();
                                            }
                                        }
                                    } else {
                                        CePingNotDialog cePingNotDialog = new CePingNotDialog(FtMaxDetailActivity.this, R.style.YzmDialog);
                                        cePingNotDialog.show();
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

    protected void gotoInvest() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("groupId", dftProInfoId);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFT_GOTOINVEST_URL,
        null, map, new HttpBackBaseListener() {

            @Override
            public void onSuccess(String string) {
                LogUtils.longStr("大饭团买入第一步======" , string);
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
                            // 是否绑卡
                            Boolean isBindCard = data.getBoolean("isBindCard");
                            if (isBindCard) {//已经绑卡
                                Intent intent = new Intent(FtMaxDetailActivity.this, FtMinToFtMaxActivity.class);
                                intent.putExtra("jsonData", datastr);
                                startActivity(intent);
                            } else {//未绑卡
                                Intent intent = new Intent(FtMaxDetailActivity.this, FtMaxBindBankActivity.class);
                                intent.putExtra("jsonData", datastr);
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
                ToastUtils.toastshort("加载数据失败！");
            }

        });
    }
}
