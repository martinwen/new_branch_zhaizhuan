package com.iqianbang.fanpai.fragment.jiaxipiaoFragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.adapter.jiaxipiao.JiaXiPiaoFhUnusedAdapter;
import com.iqianbang.fanpai.bean.JiaXiBean;
import com.iqianbang.fanpai.fragment.BaseFragment;
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

/**
 * Created by WenGuangJun on 2017/2/19.
 */

public class FtMinUsedfragment extends BaseFragment {
    @BindView(R.id.iv_invest_not)
    ImageView ivInvestNot;
    private PullToRefreshListView refreshListView;
    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<JiaXiBean> list;
    private JiaXiPiaoFhUnusedAdapter adapter;
    private int pagenum=1;
    private int pagesize=20;
    private int pages;
    @Override
    protected View initView() {
        list = new ArrayList<JiaXiBean>();
        progressdialog = new CustomProgressDialog(mActivity, "正在获取数据...");
        View view = View.inflate(mActivity, R.layout.fragment_jiaxipiao_used, null);
        ButterKnife.bind(this, view);
        refreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
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
        listView.setSelector(android.R.color.transparent);
        // 5.设置adapter
        adapter=new JiaXiPiaoFhUnusedAdapter(mActivity, list);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void initData() {
        // 访问网络
        LogUtils.i("小饭团失效的initData");
        if (refreshListView.getMode()!= PullToRefreshBase.Mode.DISABLED) {
            //当切换到此页时会走initdata方法，会导致数据重复添加而显示，所以需要清楚数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
            //这时不能把之前的list清除掉，所以才这样判断
            list.clear();
            pagenum = 1;
        }
        getDataFromServer();
    }


    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("status", 3+"");
        map.put("proCode", "fh");
        map.put("pageNum", pagenum+"");
        map.put("pageSize", pagesize+"");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.RATECOUPON_URL,
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
                                    ArrayList<JiaXiBean> listadd = (ArrayList<JiaXiBean>) JSONArray.parseArray(getList.toJSONString(), JiaXiBean.class);
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
