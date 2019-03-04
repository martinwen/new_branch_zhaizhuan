package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.ZtDetailActivity;
import com.iqianbang.fanpai.adapter.ZtListAdapter;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.bean.ZhuanRangListBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WenGuangJun on 2017/3/11.
 */

public class InvestZhiTouFragment extends BaseFragment {

    @BindView(R.id.tv_normal)
    TextView tvNormal;
    @BindView(R.id.ll_rate)
    LinearLayout llRate;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.list_view)
    ListView mListView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.iv_invest_not)
    ImageView ivInvestNot;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_rate)
    ImageView ivRate;
    @BindView(R.id.iv_time)
    ImageView ivTime;
    Unbinder unbinder;

    private ArrayList<ZhuanRangListBean> mList = new ArrayList<>();
    private ZtListAdapter mAdapter;

    private int pagenum = 1;
    private int pagesize = 10;
    private int pages;
    private boolean isRefersh = true;

    private String currentPage = "1";
    private int currentRate = 1;
    private int currentTime = 1;

    private String discountRate = "";
    private String term = "";
    private String order = "";

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_invest_zhitou, null);
        ButterKnife.bind(this, view);
        initEvents();
        return view;
    }

    private void initEvents() {
        mAdapter = new ZtListAdapter(mActivity, mList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ZhuanRangListBean zhuanRangListBean = mList.get(i);
                Intent intent = new Intent(mActivity, ZtDetailActivity.class);
                intent.putExtra("transferId", zhuanRangListBean.getId());
                mActivity.startActivity(intent);
            }
        });

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

    @OnClick({R.id.tv_normal, R.id.ll_rate, R.id.ll_time})
    public void onViewClicked(View view) {

        tvNormal.setTextColor(getResources().getColor(R.color.text_graycolor));
        tvRate.setTextColor(getResources().getColor(R.color.text_graycolor));
        tvTime.setTextColor(getResources().getColor(R.color.text_graycolor));

        ivRate.setImageResource(R.drawable.zhitou_arrow_normal);
        ivTime.setImageResource(R.drawable.zhitou_arrow_normal);

        pagenum = 1;
        isRefersh = true;
        mRefreshLayout.setNoMoreData(false);
        mList.clear();
        discountRate = "";
        term = "";
        order = "";

        switch (view.getId()) {
            case R.id.tv_normal:
                currentPage = "1";
                currentRate = 1;
                currentTime = 1;
                break;
            case R.id.ll_rate:
                currentPage = "2";
                currentTime = 1;
                currentRate++;

                discountRate = "1";
                break;
            case R.id.ll_time:
                currentPage = "3";
                currentRate = 1;
                currentTime++;

                term = "1";
                break;
        }

        if ("1".equals(currentPage)) {//默认
            tvNormal.setTextColor(getResources().getColor(R.color.text_red_dark));
        }

        if ("2".equals(currentPage)) {
            tvRate.setTextColor(getResources().getColor(R.color.text_red_dark));

            if(currentRate%2==0){//折让率升序
                order = "asc";
                ivRate.setImageResource(R.drawable.zhitou_arrow_up);
            }else{//折让率降序
                order = "desc";
                ivRate.setImageResource(R.drawable.zhitou_arrow_down);
            }
        }

        if ("3".equals(currentPage)) {
            tvTime.setTextColor(getResources().getColor(R.color.text_red_dark));

            if(currentTime%2==0){//剩余期限升序
                order = "asc";
                ivTime.setImageResource(R.drawable.zhitou_arrow_up);
            }else{//剩余期限降序
                order = "desc";
                ivTime.setImageResource(R.drawable.zhitou_arrow_down);
            }
        }

        //刷新列表
        getDataFromServer();
    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("token", token);
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("discountRate", discountRate);
        map.put("term", term);
        map.put("order", order);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWTRANSFERPROLISTINFO_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {

                        LogUtils.i("转让专区-产品列表==" + string);
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
                                    ArrayList<ZhuanRangListBean> listadd = (ArrayList<ZhuanRangListBean>) JSONArray.parseArray(getList.toJSONString(), ZhuanRangListBean.class);
                                    mList.addAll(listadd);
                                    if (null == mList || mList.size() == 0) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
