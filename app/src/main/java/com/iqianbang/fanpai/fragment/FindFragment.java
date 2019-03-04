package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.find.AboutUsActivity;
import com.iqianbang.fanpai.activity.find.HelpActivity;
import com.iqianbang.fanpai.activity.find.WebviewActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.adapter.FindAdapter;
import com.iqianbang.fanpai.bean.FindBean;
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
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/12/13.
 */

public class FindFragment extends BaseFragment {

    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.tv_kefu)
    TextView tvKefu;
    @BindView(R.id.tv_aboutus)
    TextView tvAboutus;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;
    private ArrayList<FindBean> list;
    private ListView listView;
    private FindAdapter adapter;
    private CustomProgressDialog progressdialog;
    private int pagenum = 1;
    private int pagesize = 25;
    private int pages;

    @Override
    public View initView() {
        LogUtils.i("发现页initView===");
        View view = View.inflate(mActivity, R.layout.fragment_find, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        list = new ArrayList<FindBean>();
        progressdialog = new CustomProgressDialog(mActivity, "正在加载图片...");
        LogUtils.i("发现页initData===");
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

        adapter = new FindAdapter(mActivity, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new FindListener());

        // 访问网络
        if (refreshListView.getMode() != PullToRefreshBase.Mode.DISABLED) {
            //当切换到发现页时会走initdata方法，会导致数据重复添加而显示，所以需要清除数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
            //这时不能把之前的list清除掉，所以才这样判断
            list.clear();
            pagenum = 1;
        }
        getDataFromServer();
    }

    class FindListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            String token = CacheUtils.getString("token", "");

            FindBean findBean = list.get(position-1);
            String url = findBean.getLinkUrl();
            String title = findBean.getTitle();
            String isNeedLogin = findBean.getIsNeedLogin();

            if ("1".equals(isNeedLogin)) {
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(mActivity,LoginActivity.class));
                }else {
                    //跳转到url对应的网址
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent=new Intent(mActivity,WebviewActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", title);
                        startActivity(intent);
                    }
                }
            }else{
                //跳转到url对应的网址
                if (!TextUtils.isEmpty(url)) {
                    Intent intent=new Intent(mActivity,WebviewActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            }
        }
    }

    private void getDataFromServer() {

        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        progressdialog.showis();
        map.put("type", "4");
        map.put("pagination", "true");
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FIND_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("发现页===" + string);
                        refreshListView.onRefreshComplete();
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
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
                                    JSONArray getList = data.getJSONArray("list");
                                    ArrayList<FindBean> listadd = (ArrayList<FindBean>) JSONArray.parseArray(getList.toJSONString(), FindBean.class);
                                    list.addAll(listadd);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    ToastUtils.toastshort("图片加载异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        refreshListView.onRefreshComplete();
                        progressdialog.dismiss();
                        ToastUtils.toastshort("图片加载失败！");
                    }
                });
    }

    @OnClick({R.id.tv_help, R.id.tv_kefu, R.id.tv_aboutus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_help:
                startActivity(new Intent(mActivity,HelpActivity.class));
                break;
            case R.id.tv_kefu:
                ConsultSource source = new ConsultSource(null, null, null);
                Unicorn.openServiceActivity(mActivity, // 上下文
                        "饭团客服", // 聊天窗口的标题
                        source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
                );
                break;
            case R.id.tv_aboutus:
                startActivity(new Intent(mActivity,AboutUsActivity.class));
                break;
        }
    }
}
