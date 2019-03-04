package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.adapter.FanTongAdapter;
import com.iqianbang.fanpai.bean.FTBean;
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

import static com.iqianbang.fanpai.R.id.tv_leiji;

public class FtJingYingZaiTouActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_zaitou)
    TextView tvZaitou;
    @BindView(R.id.tv_zuori)
    TextView tvZuori;
    @BindView(tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.ll_xiangqing)
    LinearLayout llXiangqing;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;

    public static String FLAG_JINGYINGBUYSUCCESS = "flag_jingyingbuysuccess";


    private ListView listView;
    private CustomProgressDialog progressdialog;
    private ArrayList<FTBean> list = new ArrayList<FTBean>();
    private FanTongAdapter adapter;
    private int pagenum = 1;
    private int pagesize = 20;
    private int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftjingying_zai_tou);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "");
        initData();
    }

    private void initData() {

        String ftAcctBal = CacheUtils.getString(CacheUtils.FTACCTBAL, "");
        tvZaitou.setText(ftAcctBal + "元");
        String ftYesterdayIncome = CacheUtils.getString(CacheUtils.FTYESTERDAYINCOME, "");
        tvZuori.setText(ftYesterdayIncome);
        String ftTotalIncome = CacheUtils.getString(CacheUtils.FTTOTALINCOME, "");
        tvLeiji.setText(ftTotalIncome);

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

        adapter = new FanTongAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new FanTongListener());

        //访问网络，初始化界面数据
        getDataFromServer();
    }

    class FanTongListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            FTBean ftBean = list.get(position - 1);
            if ("1".equals(ftBean.getStatus())) {
                Intent intent = new Intent(FtJingYingZaiTouActivity.this, ZaiTouXiangMuActivity.class);
                intent.putExtra("proCode", getIntent().getStringExtra("proCode"));
                intent.putExtra("transSeq", ftBean.getTrans_seq());
                startActivity(intent);
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_xiangqing, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_xiangqing:
                startActivity(new Intent(this, FtJingYingZaiTouJiLuActivity.class));
                break;
            case R.id.tv_btn_finish:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("from", FLAG_JINGYINGBUYSUCCESS);
                startActivity(intent1);
                break;
        }
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("transTime", "");
        map.put("pageNum", pagenum + "");
        map.put("pageSize", pagesize + "");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FT_INVEST_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("饭桶出借项目===" + string);
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
                                    JSONArray getList = data.getJSONArray("list");
                                    ArrayList<FTBean> listadd = (ArrayList<FTBean>) JSONArray.parseArray(getList.toJSONString(), FTBean.class);
                                    list.addAll(listadd);
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
