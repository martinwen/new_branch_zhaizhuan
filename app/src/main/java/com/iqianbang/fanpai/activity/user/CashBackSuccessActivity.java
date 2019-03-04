package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.iqianbang.fanpai.adapter.CashBackListAdapter;
import com.iqianbang.fanpai.adapter.CashBackSuccessAdapter;
import com.iqianbang.fanpai.adapter.zhanneixin.ZhanNeiXinAdapter;
import com.iqianbang.fanpai.bean.FtMinCashListBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CashBackSuccessActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    private PullToRefreshListView refreshListView;
    private ListView listView;
    private ArrayList<FtMinCashListBean> list = new ArrayList<FtMinCashListBean>();
    private CashBackSuccessAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_back_success);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        refreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
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
                getDataFromServer();

            }
        });
        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        listView.setSelector(android.R.color.transparent);
        // 5.设置adapter
        adapter = new CashBackSuccessAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void initData() {
        // 访问网络
        if (refreshListView.getMode() != PullToRefreshBase.Mode.DISABLED) {
            //当切换到此页时会走initdata方法，会导致数据重复添加而显示，所以需要清除数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
            //这时不能把之前的list清除掉，所以才这样判断
            list.clear();
            pagenum = 1;
        }
        getDataFromServer();

    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("status", 2 + "");
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FHREDEEMLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("小饭团赎回详情列表===" + string);
                        refreshListView.onRefreshComplete();
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

                                    String redeemList = data.getString("redeemList");
                                    JSONObject jsonObject = JSON.parseObject(redeemList);
                                    //总页数
                                    pages = jsonObject.getInteger("pages");

                                    //list
                                    JSONArray getList = jsonObject.getJSONArray("list");
                                    ArrayList<FtMinCashListBean> listadd = (ArrayList<FtMinCashListBean>) JSONArray.parseArray(getList.toJSONString(), FtMinCashListBean.class);
                                    list.addAll(listadd);
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
                        refreshListView.onRefreshComplete();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
