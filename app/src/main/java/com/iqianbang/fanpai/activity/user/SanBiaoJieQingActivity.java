package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.iqianbang.fanpai.adapter.FanWanJieQingAdapter;
import com.iqianbang.fanpai.adapter.SanBiaoJieQingAdapter;
import com.iqianbang.fanpai.bean.FWBean;
import com.iqianbang.fanpai.bean.SanBiaoListBean;
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

public class SanBiaoJieQingActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;
    @BindView(R.id.iv_invest_not)
    ImageView ivInvestNot;

    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<SanBiaoListBean> list = new ArrayList<>();
    private SanBiaoJieQingAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftmax_jie_qing);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "");
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
        listView.setSelector(android.R.color.transparent);

        adapter = new SanBiaoJieQingAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new FanWanListener());

        //访问网络，初始化界面数据
        getDataFromServer();
    }

    class FanWanListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SanBiaoListBean sanBiaoListBean = list.get(position - 1);
            Intent intent = new Intent(SanBiaoJieQingActivity.this, SanBiaoProjectActivity.class);
            intent.putExtra("id", sanBiaoListBean.getScatterInvestId());
            startActivity(intent);
        }
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("type", "2");//1： 收益中/转让中/转让成功  2 已结清
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWSCATTERINVESTRECORDLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("散标-结清出借记录列表===" + string);
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

                                    JSONObject data = JSON.parseObject(datastr);

                                    //总页数
                                    pages = data.getInteger("pages");
                                    //list
                                    JSONArray getList = data.getJSONArray("list");
                                    ArrayList<SanBiaoListBean> listadd = (ArrayList<SanBiaoListBean>) JSONArray.parseArray(getList.toJSONString(), SanBiaoListBean.class);
                                    list.addAll(listadd);
                                    if(null == list || list.size() == 0){
                                        ivInvestNot.setVisibility(View.VISIBLE);
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
                        refreshListView.onRefreshComplete();
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }
}
