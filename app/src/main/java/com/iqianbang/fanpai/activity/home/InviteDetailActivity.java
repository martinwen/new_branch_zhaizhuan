package com.iqianbang.fanpai.activity.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.RewardDetailAdapter;
import com.iqianbang.fanpai.bean.InviteDetailBean;
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

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;

    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<InviteDetailBean> list = new ArrayList<InviteDetailBean>();
    private RewardDetailAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_detail);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {
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
        // 5.设置adapter
        adapter = new RewardDetailAdapter(this, list);
        listView.setAdapter(adapter);
        // 访问网络
        getDataFromServer();
    }

    private void getDataFromServer() {
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MY_INVITE_DETAIL_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        refreshListView.onRefreshComplete();
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
                                    LogUtils.i("邀请详情返回数据" + datastr);
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
                                    ArrayList<InviteDetailBean> listadd = (ArrayList<InviteDetailBean>) JSONArray.parseArray(getList.toJSONString(), InviteDetailBean.class);
                                    list.addAll(listadd);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            ToastUtils.toastshort("加载数据失败！");
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

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
