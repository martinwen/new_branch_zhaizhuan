package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.iqianbang.fanpai.adapter.HongBaoFwBuyAdapter;
import com.iqianbang.fanpai.bean.HongBaoBean;
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

import static android.R.attr.type;

public class HongBaoUseActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private ListView listView;
    private HongBaoFwBuyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hong_bao_use);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        final ArrayList<HongBaoBean> list = (ArrayList<HongBaoBean>) getIntent().getSerializableExtra(
                "listHongBaoCanShow");

        String redbagTicketId = getIntent().getStringExtra("redbagTicketId");

        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                HongBaoBean hongBaoBean = list.get(position - 1);
                Intent intent = getIntent();
                intent.putExtra("redbagTicketId", hongBaoBean.getId());
                intent.putExtra("reward", hongBaoBean.getReward());
                setResult(1, intent);
                finish();
            }
        });
        listView.setSelector(android.R.color.transparent);
        // 5.设置adapter
        adapter = new HongBaoFwBuyAdapter(this, list,redbagTicketId);
        listView.setAdapter(adapter);
    }


    @OnClick({R.id.iv_back, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_btn_finish:
                Intent intent = getIntent();
                intent.putExtra("redbagTicketId", "");
                intent.putExtra("reward", "");
                setResult(1, intent);
                finish();
                break;
        }
    }
}
