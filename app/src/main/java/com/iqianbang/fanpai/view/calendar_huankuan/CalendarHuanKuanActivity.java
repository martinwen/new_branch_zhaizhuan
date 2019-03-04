package com.iqianbang.fanpai.view.calendar_huankuan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.CalendarHuiKuanAdapter;
import com.iqianbang.fanpai.bean.CalendarHuiKuanBean;
import com.iqianbang.fanpai.bean.CalendarHuiKuanDayBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.calendar.DateSignData;
import com.iqianbang.fanpai.view.calendar.MonthSignData;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.pickerView.TimePickerView;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarHuanKuanActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.my_sign_calendar)
    SignCalendar signCalendar;
    @BindView(R.id.tv_benyue)
    TextView tvBenyue;
    @BindView(R.id.left_arrow)
    ImageView leftArrow;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.right_arrow)
    ImageView rightArrow;
    @BindView(R.id.tv_huizong)
    TextView tvHuizong;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_count_money)
    TextView tvCountMoney;
    @BindView(R.id.tv_huikuan)
    TextView tvHuikuan;
    @BindView(R.id.tv_huikuan_money)
    TextView tvHuikuanMoney;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.rl_detail)
    RelativeLayout rlDetail;
    @BindView(R.id.ll_huizong)
    LinearLayout llHuizong;
    @BindView(R.id.tv_none)
    TextView tvNone;

    private CustomProgressDialog progressdialog;
    private ArrayList<CalendarHuiKuanBean> listData = new ArrayList<CalendarHuiKuanBean>();
    private int showyear;
    private int showmonth;
    private int currentDay;

    private TimePickerView pvTime;
    private Calendar calendar;

    private ArrayList<CalendarHuiKuanDayBean> listDay = new ArrayList<CalendarHuiKuanDayBean>();
    private ListView listView;
    private CalendarHuiKuanAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;
    private int dayClick;
    private String year_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_huankuan);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {

        //获取当前年月日
        calendar = Calendar.getInstance();
        showyear = calendar.get(Calendar.YEAR);
        showmonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        //初始化时间控件
        initTimePiker();

        //初始化时间
        year_month = getTime(calendar.getTime());
        tvTitle.setText(year_month);

        ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
        MonthSignData monthData = new MonthSignData();
        monthData.setYear(showyear);
        monthData.setMonth(showmonth);
        ArrayList<DateSignData> signDates = new ArrayList<DateSignData>();
        monthData.setSignDates(signDates);
        monthDatas.add(monthData);
        Date today = new Date(showyear - 1900, showmonth, currentDay);
        signCalendar.setToday(today);
        signCalendar.setSignDatas(monthDatas);

        signCalendar.setSignCalendarListener(new SignCalendar.SignCalendarListener() {
            @Override
            public void onDayClick(int date) {
                //显示单日的数据
                refreshListView.setVisibility(View.VISIBLE);
                //隐藏（汇总信息）
                llHuizong.setVisibility(View.GONE);
                //清除listDay,页数重新归1，refreshListView可刷新
                pagenum = 1;
                listDay.clear();
                refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

                dayClick = date;
                showDay(dayClick);
            }
        });

        //查询对应月份的数据
        getDataFromServer();

        // 1.设置刷新模式,上拉和下拉刷新都有
        refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        // 2.设置刷新监听器
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            // 下拉刷新和加载更多都会走这个方法
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // 直接请求
                pagenum++;
                if (pagenum > pages) {
                    refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                showDay(dayClick);

            }
        });
        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        refreshListView.setVisibility(View.GONE);

    }

    private void initTimePiker() {
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        pvTime.setRange(calendar.get(Calendar.YEAR) - 6, calendar.get(Calendar.YEAR) + 14);//要在setTime 之前才有效果哦

        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                calendar.setTime(date);
                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                //修改时间
                tvTitle.setText(getTime(date));
                if (year_month.equals(getTime(date))) {
                    tvBenyue.setVisibility(View.GONE);
                } else {
                    tvBenyue.setVisibility(View.VISIBLE);
                }
                //隐藏单日的数据
                refreshListView.setVisibility(View.GONE);
                //隐藏（当前日期没有回款呦）
                tvNone.setVisibility(View.GONE);
                //显示（汇总信息）
                llHuizong.setVisibility(View.VISIBLE);
                //查询对应月份的数据前需要把之前list获取到的数据清空
                getDataFromServer();
            }
        });
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy 年 MM 月");
        return format.format(date);
    }

    @OnClick({R.id.iv_back, R.id.tv_benyue, R.id.left_arrow, R.id.tv_title, R.id.right_arrow,
            R.id.tv_huizong, R.id.rl_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_benyue:
                //获取当前年月日
                calendar = Calendar.getInstance();
                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                //修改时间
                tvTitle.setText(getTime(calendar.getTime()));
                if (year_month.equals(getTime(calendar.getTime()))) {
                    tvBenyue.setVisibility(View.GONE);
                } else {
                    tvBenyue.setVisibility(View.VISIBLE);
                }
                //隐藏单日的数据
                refreshListView.setVisibility(View.GONE);
                //隐藏（当前日期没有回款呦）
                tvNone.setVisibility(View.GONE);
                //显示（汇总信息）
                llHuizong.setVisibility(View.VISIBLE);
                //查询对应月份的数据前需要把之前list获取到的数据清空
                getDataFromServer();
                break;
            case R.id.left_arrow:
                calendar.set(showyear, showmonth, 19);
                calendar.add(Calendar.MONTH, -1);    //得到前一个月

                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                //修改时间
                tvTitle.setText(getTime(calendar.getTime()));
                if (year_month.equals(getTime(calendar.getTime()))) {
                    tvBenyue.setVisibility(View.GONE);
                } else {
                    tvBenyue.setVisibility(View.VISIBLE);
                }
                //隐藏单日的数据
                refreshListView.setVisibility(View.GONE);
                //隐藏（当前日期没有回款呦）
                tvNone.setVisibility(View.GONE);
                //显示（汇总信息）
                llHuizong.setVisibility(View.VISIBLE);
                getDataFromServer();
                break;
            case R.id.tv_title:
                pvTime.show();
                break;
            case R.id.right_arrow:
                calendar.set(showyear, showmonth, 19);
                calendar.add(Calendar.MONTH, +1);    //得到后一个月

                showyear = calendar.get(Calendar.YEAR);
                showmonth = calendar.get(Calendar.MONTH);
                //修改时间
                tvTitle.setText(getTime(calendar.getTime()));
                if (year_month.equals(getTime(calendar.getTime()))) {
                    tvBenyue.setVisibility(View.GONE);
                } else {
                    tvBenyue.setVisibility(View.VISIBLE);
                }
                //隐藏单日的数据
                refreshListView.setVisibility(View.GONE);
                //隐藏（当前日期没有回款呦）
                tvNone.setVisibility(View.GONE);
                //显示（汇总信息）
                llHuizong.setVisibility(View.VISIBLE);
                getDataFromServer();
                break;
            case R.id.tv_huizong:
                //隐藏单日的数据
                refreshListView.setVisibility(View.GONE);
                //隐藏（当前日期没有回款呦）
                tvNone.setVisibility(View.GONE);
                //显示（汇总信息）
                llHuizong.setVisibility(View.VISIBLE);

                getMonthData();
                break;
            case R.id.rl_detail:
                Intent intent = new Intent(this, HuiKuanDetailActivity.class);
                intent.putExtra("year", showyear + "");
                intent.putExtra("month", (showmonth+1) + "");
                startActivity(intent);
                break;
        }
    }

    private void getMonthData() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("year", showyear + "");
        map.put("month", (showmonth + 1) + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.REPAYCALENDARSUMMARYDATA_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("当月汇总===" + string);
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

                                    //X月回款总计数量
                                    String count = data.getString("count");
                                    tvCount.setText((showmonth + 1) + "月" + count + "笔回款共计");

                                    // X月回款总计金额
                                    String totalAmount = data.getString("totalAmount");
                                    tvCountMoney.setText(totalAmount + "元");

                                    //X月已回款
                                    String hasAmount = data.getString("hasAmount");
                                    tvHuikuan.setText((showmonth + 1) + "月" + "已回款");
                                    tvHuikuanMoney.setText(hasAmount + "元");

                                    //X月回款明细
                                    tvDetail.setText((showmonth + 1) + "月" + "回款明细");

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

    private void showDay(int dayClick) {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("date", showyear + "-" + (showmonth + 1) + "-" + dayClick);
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.GETONEDAYREPAYLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("当天还款计划===" + string);
                        refreshListView.onRefreshComplete();
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

                                    //总页数
                                    pages = data.getInteger("pages");

                                    //list
                                    JSONArray getList = data.getJSONArray("list");
                                    ArrayList<CalendarHuiKuanDayBean> listAdd = (ArrayList<CalendarHuiKuanDayBean>) JSONArray.parseArray(getList.toJSONString(), CalendarHuiKuanDayBean.class);
                                    listDay.addAll(listAdd);

                                    if (listDay.size() > 0) {
                                        //隐藏（当前日期没有回款呦）
                                        tvNone.setVisibility(View.GONE);
                                    } else {
                                        //显示（当前日期没有回款呦）
                                        tvNone.setVisibility(View.VISIBLE);
                                    }
                                    // 5.设置adapter
                                    adapter = new CalendarHuiKuanAdapter(CalendarHuanKuanActivity.this, listDay);
                                    listView.setAdapter(adapter);
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
                        refreshListView.onRefreshComplete();
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("year", showyear + "");
        map.put("month", (showmonth + 1) + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.REPAYCALENDARDATA_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("当月还款计划===" + string);
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

                                    //X月回款总计数量
                                    String count = data.getString("count");
                                    tvCount.setText((showmonth + 1) + "月" + count + "笔回款共计");

                                    // X月回款总计金额
                                    String totalAmount = data.getString("totalAmount");
                                    tvCountMoney.setText(totalAmount + "元");

                                    //X月已回款
                                    String hasAmount = data.getString("hasAmount");
                                    tvHuikuan.setText((showmonth + 1) + "月" + "已回款");
                                    tvHuikuanMoney.setText(hasAmount + "元");

                                    //X月回款明细
                                    tvDetail.setText((showmonth + 1) + "月" + "回款明细");

                                    //list
                                    JSONArray getList = data.getJSONArray("dateList");
                                    listData = (ArrayList<CalendarHuiKuanBean>) JSONArray.parseArray(getList.toJSONString(), CalendarHuiKuanBean.class);

                                    ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
                                    MonthSignData monthData = new MonthSignData();
                                    monthData.setYear(showyear);
                                    monthData.setMonth(showmonth);
                                    ArrayList<DateSignData> signDates = new ArrayList<DateSignData>();
                                    if (listData.size() != 0) {
                                        for (int i = 0; i < listData.size(); i++) {
                                            DateSignData dateSignData = new DateSignData(showyear, showmonth, listData.get(i).getSignTime());
                                            dateSignData.setSignFlag(listData.get(i).getSignFlag());
                                            signDates.add(dateSignData);
                                        }
                                    }
                                    monthData.setSignDates(signDates);
                                    monthDatas.add(monthData);


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
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });

    }

}
