package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.iqianbang.fanpai.adapter.youxiangou.YouXianGouUseableAdapter;
import com.iqianbang.fanpai.bean.YouXianGouBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.FloatDragYxhDuiHuanView;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.YouXianGouDuiHuanDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YouXianGouActivity extends BaseActivity {

    @BindView(R.id.iv_invest_not)
    ImageView ivInvestNot;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_shixiao)
    TextView tvShixiao;

    private PullToRefreshListView refreshListView;
    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<YouXianGouBean> list = new ArrayList<YouXianGouBean>();
    private YouXianGouUseableAdapter adapter;
    private int pagenum=1;
    private int pagesize=20;
    private int pages;
    private PopupWindow pop;

    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_xian_gou);
        rl = (RelativeLayout) findViewById(R.id.rl);
        ButterKnife.bind(this);


        progressdialog = new CustomProgressDialog(this, "正在获取数据...");
        initView();
        initData();
    }

    private void initView(){
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
        adapter=new YouXianGouUseableAdapter(this, list);
        listView.setAdapter(adapter);
    }
    private void initData() {
        // 访问网络
        if (refreshListView.getMode()!= PullToRefreshBase.Mode.DISABLED) {
            //当切换到此页时会走initdata方法，会导致数据重复添加而显示，所以需要清除数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
            //这时不能把之前的list清除掉，所以才这样判断
            list.clear();
            pagenum = 1;
        }
        getDataFromServer();

        //兑换优先购
//        FloatDragYxhDuiHuanView.addFloatDragView(this, rl, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                YouXianGouDuiHuanDialog youXianGouDuiHuanDialog = new YouXianGouDuiHuanDialog(YouXianGouActivity.this, R.style.YzmDialog);
//                youXianGouDuiHuanDialog.show();
//            }
//        });
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("status", 1+"");
        map.put("pageNum", pagenum+"");
        map.put("pageSize", pagesize+"");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.PREFERTICKET_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("优先购可使用==="+string);
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
                                    ArrayList<YouXianGouBean> listadd = (ArrayList<YouXianGouBean>) JSONArray.parseArray(getList.toJSONString(), YouXianGouBean.class);
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

    @OnClick({R.id.iv_back, R.id.tv_title, R.id.tv_shixiao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_shixiao:
                startActivity(new Intent(this, YouXianGouShiXiaoActivity.class));
                break;
            case R.id.tv_title:
                View inflate = View.inflate(this, R.layout.activity_jiaxipiao_pop, null);
                TextView tv_up = (TextView) inflate.findViewById(R.id.tv_up);
                tv_up.setText("加息票");
                tv_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(YouXianGouActivity.this,JiaXiPiaoActivity.class));
                        finish();
                    }
                });
                TextView tv_middle = (TextView) inflate.findViewById(R.id.tv_middle);
                tv_middle.setText("红包");
                tv_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(YouXianGouActivity.this,HongBaoActivity.class));
                        finish();
                    }
                });
                TextView tv_down = (TextView) inflate.findViewById(R.id.tv_down);
                tv_down.setText("提现票");
                tv_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(YouXianGouActivity.this,TiXianPiaoActivity.class));
                        finish();
                    }
                });
                pop = new PopupWindow(inflate, dip2px(100), dip2px(125));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(tvTitle, dip2px(-25), 0);
                break;
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
