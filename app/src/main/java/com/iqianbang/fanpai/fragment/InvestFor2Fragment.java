package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FtMaxDetailActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.activity.user.ZhanNeiXinActivity;
import com.iqianbang.fanpai.adapter.FtMaxListAdapter;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.bean.JiaXiBean;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by WenGuangJun on 2017/3/11.
 */

public class InvestFor2Fragment extends BaseFragment {

    private CustomProgressDialog progressdialog;

    private ArrayList<FtMaxListBean> mList = new ArrayList<>();
    private FtMaxListAdapter mAdapter;
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private ImageView ivInvestNot;

    private int pagenum = 1;
    private int pagesize = 10;
    private int pages;
    private boolean isRefersh = true;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_invest_2, null);
        progressdialog = new CustomProgressDialog(mActivity, "数据加载中...");
        mListView = (ListView) view.findViewById(R.id.list_view);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        ivInvestNot = (ImageView) view.findViewById(R.id.iv_invest_not);
        initEvents();
        return view;
    }

    private void initEvents() {
        mAdapter = new FtMaxListAdapter(mActivity, mList);
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
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWDFTPROLISTINFO_URL, null, map,
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
                                    ArrayList<FtMaxListBean> listadd = (ArrayList<FtMaxListBean>) JSONArray.parseArray(getList.toJSONString(), FtMaxListBean.class);
                                    mList.addAll(listadd);
                                    if(null == mList || mList.size() == 0){
                                        ivInvestNot.setVisibility(View.VISIBLE);
                                    }
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
