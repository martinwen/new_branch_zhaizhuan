package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.iqianbang.fanpai.adapter.CashBackListAdapter;
import com.iqianbang.fanpai.adapter.zhanneixin.ZhanNeiXinAdapter;
import com.iqianbang.fanpai.bean.FtMinCashListBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CashBackCheXiaoDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CashBackListActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_shixiao)
    TextView tvShixiao;
    @BindView(R.id.tv_money_xiaofantuan)
    TextView tvMoneyXiaofantuan;
    @BindView(R.id.tv_money_zong)
    TextView tvMoneyZong;
    @BindView(R.id.tv_chexiao)
    TextView tvChexiao;
    @BindView(R.id.tv_chexiao_zong)
    TextView tvChexiaoZong;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;
    @BindView(R.id.rl_btn)
    RelativeLayout rlBtn;
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.fr_btn)
    FrameLayout frBtn;

    private PullToRefreshListView refreshListView;
    private ListView listView;
    private ArrayList<FtMinCashListBean> list = new ArrayList<FtMinCashListBean>();
    private CashBackListAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;
    public static boolean isCashBackOne = true;
    BigDecimal zong = BigDecimal.ZERO;
    private String ids;
    private Integer hasRedeemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_back_list);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解决再次进入页面时状态未改
        isCashBackOne = true;
    }

    private void initView() {
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
                if (pagenum > pages) {
                    refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                getDataFromServer();

            }
        });
        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        listView.setSelector(android.R.color.transparent);
        // 5.设置adapter
        adapter = new CashBackListAdapter(this, list);
        //计算撤销总金额的值
        adapter.setsubClickListener(new ZhanNeiXinAdapter.SubClickListener() {
            @Override
            public void OntopicClickListener() {
                zong = BigDecimal.ZERO;

                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    if (CashBackListAdapter.getIsSelected().get(i)) {
                        zong = zong.add(new BigDecimal(list.get(i).getAmount()));
                        sb.append(list.get(i).getId()).append(",");
                    }
                }
                tvChexiaoZong.setText(zong + "元");

                if(sb.toString().length()>0){
                    ids = sb.toString().substring(0,sb.toString().length()-1);
                }

            }
        });
        listView.setAdapter(adapter);
    }

    private void initData() {
        // 访问网络
        if (refreshListView.getMode() != PullToRefreshBase.Mode.DISABLED) {
            //当切换到此页时会走initdata方法，会导致数据重复添加而显示，所以需要清除数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
            //这时不能把之前的list清除掉，所以才这样判断
            list.clear();
            pagenum = 1;
        }
        getDataFromServer();

    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("status", 1 + "");
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FHREDEEMLIST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("小饭团赎回详情列表===" + string);
                        refreshListView.onRefreshComplete();
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
                                    //小饭团总资产
                                    String fhAcctBal = data.getString("fhAcctBal");
                                    tvMoneyXiaofantuan.setText(fhAcctBal + "元");
                                    //赎回中总金额
                                    String redeemingAmount = data.getString("redeemingAmount");
                                    tvMoneyZong.setText(redeemingAmount + "元");
                                    //已赎回记录数量
                                    hasRedeemNum = data.getInteger("hasRedeemNum");
                                    if(hasRedeemNum <= 0){
                                        tvShixiao.setVisibility(View.INVISIBLE);
                                    }

                                    String redeemList = data.getString("redeemList");
                                    JSONObject jsonObject = JSON.parseObject(redeemList);
                                    //总页数
                                    pages = jsonObject.getInteger("pages");

                                    //list
                                    JSONArray getList = jsonObject.getJSONArray("list");
                                    ArrayList<FtMinCashListBean> listadd = (ArrayList<FtMinCashListBean>) JSONArray.parseArray(getList.toJSONString(), FtMinCashListBean.class);
                                    list.addAll(listadd);
                                    if (null == list || list.size() == 0) {
                                        //当没有赎回记录时，隐藏入口
                                        tvChexiao.setVisibility(View.INVISIBLE);
                                        tvNone.setVisibility(View.VISIBLE);
                                        tvNone.setText("暂无赎回中记录");
                                        if(hasRedeemNum <= 0){
                                            tvNone.setText("暂无赎回记录");
                                        }
                                    }
                                    adapter.setList(list);
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
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    @OnClick({R.id.iv_back, R.id.tv_shixiao, R.id.tv_chexiao, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_shixiao:
                startActivity(new Intent(this, CashBackSuccessActivity.class));
                break;
            case R.id.tv_chexiao:
                //取消已经选中的赎回申请
                for (int i = 0; i < list.size(); i++) {// 遍历list的长度，全不选
                    CashBackListAdapter.getIsSelected().put(i, false);
                }
                //一定要先取反isCashBackOne，再notifyDataSetChanged，因adapter里有用isCashBackOne做判断
                isCashBackOne = !isCashBackOne;
                adapter.notifyDataSetChanged();
                if (isCashBackOne) {
                    tvChexiao.setText("多选撤销");
                    rlBtn.setVisibility(View.GONE);
                    if(hasRedeemNum > 0){
                        tvShixiao.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvChexiao.setText("取消多选");
                    rlBtn.setVisibility(View.VISIBLE);
                    tvShixiao.setVisibility(View.GONE);
                    //撤销总金额为0
                    zong = BigDecimal.ZERO;
                    tvChexiaoZong.setText("0元");
                }

                break;
            case R.id.tv_btn_finish:
                if(zong.compareTo(BigDecimal.ZERO)>0){
                    CashBackCheXiaoDialog cashBackCheXiaoDialog = new CashBackCheXiaoDialog(this, R.style.YzmDialog,
                            ids,"撤销赎回金额为" + zong + "元\n您确定要撤销赎回申请吗？");
                    cashBackCheXiaoDialog.show();
                }else{
                    ToastUtils.toastshort("请先选择要撤销的记录");
                }

                break;
        }
    }
}
