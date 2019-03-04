package com.iqianbang.fanpai.activity.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.FtMaxListAdapter;
import com.iqianbang.fanpai.adapter.FuTouListAdapter;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.bean.FuTouListBean;
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
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Map;

public class FuTouListActivity extends BaseActivity {

    private ArrayList<FuTouListBean> mList = new ArrayList<>();
    private FuTouListAdapter mAdapter;
    private ListView mListView;
    private RefreshLayout mRefreshLayout;

    private int pagenum = 1;
    private int pagesize = 10;
    private int pages;
    private boolean isRefersh = true;
    private String dftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_tou_list);

        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        dftId = getIntent().getStringExtra("dftId");
        mAdapter = new FuTouListAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pagenum = 1;
                isRefersh = true;
                mRefreshLayout.setNoMoreData(false);
                mList.clear();
                getDataFromServer();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pagenum++;
                isRefersh = false;
                getDataFromServer();
            }
        });
        mRefreshLayout.autoRefresh();
    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("pageNum", pagenum+"");
        map.put("pageSize", pagesize+"");
        map.put("dftId", dftId);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWREPEATEDPROLISTINFO_URL, null, map,
            new HttpBackBaseListener() {

                @Override
                public void onSuccess(String string) {

                    LogUtils.i("大饭团（集合标）列表==" + string);
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
                                JSONArray getList=data.getJSONArray("list");
                                ArrayList<FuTouListBean> listadd = (ArrayList<FuTouListBean>) JSONArray.parseArray(getList.toJSONString(), FuTouListBean.class);
                                mList.addAll(listadd);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.toastshort("加载数据异常！");
                            }
                        }
                    } else {
                        String msg = json.getString("msg");
                        ToastUtils.toastshort(msg);
                    }

                    if (isRefersh) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }

                    if (pagenum >= pages) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    if (isRefersh) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                    ToastUtils.toastshort("网络连接失败，请检查网络");
                }
            });
    }
}
