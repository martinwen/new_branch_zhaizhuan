package com.iqianbang.fanpai.activity.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
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
import com.iqianbang.fanpai.view.dialog.SignDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_sign)
    ImageView ivSign;
    @BindView(R.id.my_sign_calendar)
    SignCalendar signCalendar;
    @BindView(R.id.ll_notsign)
    LinearLayout llNotsign;
    @BindView(R.id.ll_signed)
    LinearLayout llSigned;
    @BindView(R.id.tv_day)
    TextView tvDay;

    private CustomProgressDialog progressdialog;
    private String currentTime;
    private ArrayList<SignBean> list = new ArrayList<SignBean>();
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int showyear;
    private int showmonth;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    @OnClick({R.id.iv_back, R.id.iv_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_sign:
                requestServer(currentYear, currentMonth, currentDay);
                break;
        }
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
                                    //是否可签到
                                    boolean signInAble = data.getBoolean("signInAble");

                                    //不可签到的显示信息
                                    String signInMsg = data.getString("signInMsg");

                                    //防重码
                                    uuid = data.getString("uuid");

                                    //今天是否签到
                                    boolean isSign = data.getBoolean("isSign");

                                    // 连续签到天数
                                    String signTotalDays = data.getString("signTotalDays");
                                    tvDay.setText(signTotalDays+"天");


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

                                    if (signInAble) {
                                        if (isSign) {
                                            llNotsign.setVisibility(View.INVISIBLE);
                                            llSigned.setVisibility(View.VISIBLE);
                                        }else {
                                            llNotsign.setVisibility(View.VISIBLE);
                                            llSigned.setVisibility(View.INVISIBLE);
                                        }

                                    }else {
//                                        bt_sign.setText(signInMsg);
//                                        bt_sign.setBackgroundResource(R.drawable.anniu_chang_yishouqing);
//                                        bt_sign.setClickable(false);
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

    private void requestServer(final int year, final int month, final int day) {

        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("uuid", uuid);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SIGNINSUBMIT_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("签到===" + string);
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

                                    String gift = "";
                                    //list
                                    JSONArray ticketList = data.getJSONArray("ticketList");
                                    if(ticketList!=null&&ticketList.size()!=0){
                                        for (int i = 0; i < ticketList.size(); i++) {
                                            gift = gift+ ticketList.get(i) +"\n";
                                        }
                                        SignDialog dialog = new SignDialog(SignInActivity.this, R.style.YzmDialog, gift);
                                        dialog.show();
                                    }


                                    //签完到后重新查询，刷新界面
                                    getDataFromServer(currentTime, currentYear, currentMonth, currentDay);

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
