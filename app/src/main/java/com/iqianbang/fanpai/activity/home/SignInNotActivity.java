package com.iqianbang.fanpai.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.user.ShengJiActivity;
import com.iqianbang.fanpai.bean.SignBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.calendar.DateSignData;
import com.iqianbang.fanpai.view.calendar.MonthSignData;
import com.iqianbang.fanpai.view.calendar.SignCalendar;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInNotActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.my_sign_calendar)
    SignCalendar signCalendar;
    @BindView(R.id.tv_ruhe)
    TextView tvRuhe;
    private CustomProgressDialog progressdialog;
    private String currentTime;
    private ArrayList<SignBean> list = new ArrayList<SignBean>();
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int showyear;
    private int showmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_not);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }


    private void initData() {

        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        showyear = calendar.get(Calendar.YEAR);
        showmonth = calendar.get(Calendar.MONTH);

        currentTime = currentYear + "-" + (currentMonth + 1);
        LogUtils.i("当前年月日==" + currentYear + "==" + currentMonth + "==" + currentDay);

        ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
        MonthSignData monthData = new MonthSignData();
        monthData.setYear(currentYear);
        monthData.setMonth(currentMonth);
        ArrayList<DateSignData> signDates = new ArrayList<DateSignData>();
        monthData.setSignDates(signDates);
        monthDatas.add(monthData);
        signCalendar.setSignDatas(monthDatas);

        //左侧箭头显示上一个月数据
        signCalendar.setLeftArrowClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(showyear, showmonth, 19);
                calendar.add(Calendar.MONTH, -1);    //得到前一个月

                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                LogUtils.i("当前年月日==" + showyear + "==" + showmonth + "==" + currentDay);
                getDataFromServer(showyear + "-" + (showmonth + 1), showyear, showmonth, currentDay);
            }
        });

        //右侧箭头显示下一个月数据
        signCalendar.setRightArrowClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(showyear, showmonth, 19);
                calendar.add(Calendar.MONTH, +1);    //得到后一个月

                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                LogUtils.i("当前年月日==" + showyear + "==" + showmonth + "==" + currentDay);
                getDataFromServer(showyear + "-" + (showmonth + 1), showyear, showmonth, currentDay);
            }
        });

        //查询对应月份的数据
        getDataFromServer(currentTime, currentYear, currentMonth, currentDay);

    }

    private void getDataFromServer(String date, final int year, final int month, final int currentDay) {
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

                                    //list
                                    JSONArray getList = data.getJSONArray("signInList");
                                    list = (ArrayList<SignBean>) JSONArray.parseArray(getList.toJSONString(), SignBean.class);
                                    LogUtils.i("list长度==" + list.size());
                                    ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
                                    MonthSignData monthData = new MonthSignData();
                                    monthData.setYear(year);
                                    monthData.setMonth(month);
                                    ArrayList<DateSignData> signDates = new ArrayList<DateSignData>();
                                    if (list.size() != 0) {
                                        for (int i = 0; i < list.size(); i++) {
                                            DateSignData dateSignData = new DateSignData(year, month, list.get(i).getSignTime());
                                            dateSignData.setPrize(list.get(i).isPrize());
                                            signDates.add(dateSignData);
                                        }
                                    }
                                    monthData.setSignDates(signDates);
                                    monthDatas.add(monthData);

                                    Date today = new Date(currentYear - 1900, currentMonth, currentDay);
                                    signCalendar.setToday(today);
                                    signCalendar.setSignDatas(monthDatas);

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


    @OnClick({R.id.iv_back, R.id.tv_ruhe})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_ruhe:
                gotoUpgrade();
                break;
        }
    }

    private void gotoUpgrade() {
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


                                    JSONObject upgrade = data.getJSONObject("upgrade");
                                    //升级说明
                                    JSONObject upgradeDescData = upgrade.getJSONObject("upgradeDescData");
                                    String normalInfo = upgradeDescData.getString("0");
                                    String coreInfo = upgradeDescData.getString("1");
                                    String highcoreInfo = upgradeDescData.getString("2");

                                    Intent intent = new Intent(SignInNotActivity.this, ShengJiActivity.class);
                                    intent.putExtra("normalInfo", normalInfo);
                                    intent.putExtra("coreInfo", coreInfo);
                                    intent.putExtra("highcoreInfo", highcoreInfo);
                                    startActivity(intent);
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
                        ToastUtils.toastshort("加载数据异常！");
                    }
                });
    }
}
