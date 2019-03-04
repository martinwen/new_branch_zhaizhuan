package com.iqianbang.fanpai.activity.home;

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
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.adapter.NoticeAdapter;
import com.iqianbang.fanpai.bean.NoticeBean;
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

public class MessageActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;

    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<NoticeBean> list = new ArrayList<NoticeBean>();
    private NoticeAdapter adapter;
    private int pagenum=1;
    private int pagesize=20;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        ButterKnife.bind(this);
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
                if(pagenum>pages){
                    refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                getDataFromServer();

            }
        });
        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        // 5.设置adapter
        adapter = new NoticeAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new NoticeListener());
        // 访问网络
//        getDataFromServer();
    }

    class NoticeListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            NoticeBean noticeBean = list.get(position - 1);
            //公告详情页面
            String id_items = noticeBean.getId();
            Intent intent = new Intent(MessageActivity.this,MessageDetailActivity.class);
            intent.putExtra("id_items", id_items);
            startActivity(intent);
        }

    }

    private void getDataFromServer() {

        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
//		if (TextUtils.isEmpty(token)) {
//			return;
//		}
        map.put("pageNum", pagenum+"");
        map.put("pageSize", pagesize+"");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.LIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("站内信===" + string);
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
                                    //当前页码
                                    String pageNum = data.getString("pageNum");
                                    //每页条数
                                    String pageSize = data.getString("pageSize");
                                    //总页数
                                    pages = data.getInteger("pages");
                                    // 总条数
                                    int total = data.getInteger("total");
                                    //list
                                    JSONArray getList=data.getJSONArray("list");
                                    ArrayList<NoticeBean> listadd = (ArrayList<NoticeBean>) JSONArray.parseArray(getList.toJSONString(), NoticeBean.class);
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
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {//为了刷新首页界面，站内信已读红点消失
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//为了刷新首页界面，站内信已读红点消失
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
        startActivity(intent);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        pagenum=1;
        list.clear();
        getDataFromServer();
    }
}
