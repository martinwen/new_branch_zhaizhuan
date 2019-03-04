package com.iqianbang.fanpai.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.adapter.BidListAdapter;
import com.iqianbang.fanpai.bean.BiaoDeBean;
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

/**
 * @author lijinliu
 * @date 20180129
 * 标的组成
 */

public class BidListFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private CustomProgressDialog progressdialog;
    private int pagenum=1;
    private int pagesize=10;
    private int pages;
    private ArrayList<BiaoDeBean> list = new ArrayList<>();
    private BidListAdapter adapter;
    private String dftProInfoId;

    @Override
    protected View initView() {
        progressdialog = new CustomProgressDialog(mActivity, "数据加载中...");

        View view = View.inflate(mActivity, R.layout.fragment_bidlist, null);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        adapter = new BidListAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 直接请求
                pagenum++;
                if (pagenum > pages) {
                    swipeRefresh.setEnabled(false);
                }
                requestServer();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        //加载数据
        if (list == null || list.size() <= 0) {
            requestServer();
        }
    }

    private void requestServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        progressdialog.showis();
        map.put("dftProInfoId", dftProInfoId);
        map.put("pageNum", pagenum+"");
        map.put("pageSize", pagesize+"");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFTPROMATHBORROWLIST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.longStr("标的组成列表===", string);
                        swipeRefresh.setRefreshing(false);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign,datastr);
                                if (isSuccess) {// 验签成功
                                    JSONObject data = JSON.parseObject(datastr);
                                    //总页数
                                    pages = data.getInteger("pages");
                                    //list
                                    JSONArray getList=data.getJSONArray("list");
                                    ArrayList<BiaoDeBean> listadd = (ArrayList<BiaoDeBean>) JSONArray.parseArray(getList.toJSONString(), BiaoDeBean.class);
                                    list.addAll(listadd);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    ToastUtils.toastshort("获取列表异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        swipeRefresh.setRefreshing(false);
                        progressdialog.dismiss();
                        ToastUtils.toastshort("获取列表失败！");
                    }
                });
    }

    public void setData(String dftProInfoId) {
        this.dftProInfoId = dftProInfoId;
    }
}
