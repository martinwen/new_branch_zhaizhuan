package com.iqianbang.fanpai.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.TiXianLiuShuiAdapter;
import com.iqianbang.fanpai.bean.TiXianLiuShuiBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.pickerView.TimePickerView;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TiXianLiuShuiActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_choice)
    ImageView ivChoice;
    @BindView(R.id.list)
    StickyListHeadersListView stickyList;
    @BindView(R.id.iv_tixian_not)
    ImageView ivTixianNot;

    private CustomProgressDialog progressdialog;
    private List<TiXianLiuShuiBean> list = new ArrayList<TiXianLiuShuiBean>();
    private TiXianLiuShuiAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 100;
    private int pages;

    private TimePickerView pvTime;
    private String currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_xian_liu_shui);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在获取数据...");
        initData();
    }

    private void initData() {
        //初始化时间控件
        initTimePiker();

        stickyList.setDividerHeight(0);
        stickyList.setAreHeadersSticky(false);
        // 5.设置adapter
        adapter = new TiXianLiuShuiAdapter(this, list);
        stickyList.setAdapter(adapter);
        // 访问网络
        getDataFromServer();
    }

    private void initTimePiker() {
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 6, calendar.get(Calendar.YEAR) + 14);//要在setTime 之前才有效果哦


        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                //因后台需要接收查询的时间参数，所以在这里用此方法获取到时间（"yyyy-MM"）
                currentTime = getCurrentTime(date);
                //查询对应月份的数据前需要把之前list获取到的数据清空
                list.clear();
                getDataFromServer();
            }
        });
    }

    public static String getCurrentTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    protected void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("time", currentTime);
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.CASHLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
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
                                    LogUtils.i("提现全部流水======" + datastr);
                                    JSONObject data = JSON.parseObject(datastr);
                                    //当前页码
                                    String pageNum = data.getString("pageNum");
                                    //每页条数
                                    String pageSize = data.getString("pageSize");
                                    //总页数
                                    pages = data.getInteger("pages");
                                    // 总条数
                                    int total = data.getInteger("total");
                                    //list
                                    JSONArray getList = data.getJSONArray("list");
                                    List<TiXianLiuShuiBean> listadd = (List<TiXianLiuShuiBean>) JSONArray.parseArray(getList.toJSONString(), TiXianLiuShuiBean.class);
                                    list.addAll(listadd);
                                    if (null == list || list.size() == 0) {
                                        ivTixianNot.setVisibility(View.VISIBLE);
                                    } else {
                                        ivTixianNot.setVisibility(View.GONE);
                                    }
                                    adapter.notifyDataSetChanged();
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

    @OnClick({R.id.iv_back, R.id.iv_choice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_choice:
                pvTime.show();
                break;
        }
    }
}
