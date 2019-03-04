package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.JiaXiPiaoFwBuyAdapter;
import com.iqianbang.fanpai.bean.JiaXiBean;
import com.iqianbang.fanpai.view.dialog.FanJuFwBuyDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JiaXiPiaoUseActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView refreshListView;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;

    private ListView listView;
    private ArrayList<JiaXiBean> list;
    private JiaXiPiaoFwBuyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jia_xi_piao_use);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        getDataFromServer();

        String fwAddRateId = getIntent().getStringExtra("fwAddRateId");

        // 3.获取refreshableView,其实就是ListView
        listView = refreshListView.getRefreshableView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final JiaXiBean jiaXiBean = list.get(position - 1);
                if ("1".equals(jiaXiBean.getUpgrade())) {//不可升级:1，可升级:2
                    Intent intent = getIntent();
                    intent.putExtra("jiaxipiaoValue",jiaXiBean.getReward());
                    intent.putExtra("fwAddRateId",jiaXiBean.getId());
                    intent.putExtra("jiaxipiaoDay",jiaXiBean.getDays());
                    intent.putExtra("jiaxipiaoIsCheck",jiaXiBean.getIsCheck());
                    setResult(2, intent);
                    finish();
                } else if ("2".equals(jiaXiBean.getUpgrade())) {
                    FanJuFwBuyDialog fanJuDialog = new FanJuFwBuyDialog(JiaXiPiaoUseActivity.this, R.style.YzmDialog);
                    fanJuDialog.show();
                    fanJuDialog.setOnFanJuFwBuyDialogDismissListener(new FanJuFwBuyDialog.OnFanJuFwBuyDialogDismissListener() {
                        @Override
                        public void OnFanJuFwBuyDialogDismiss() {
                            Intent intent = getIntent();
                            intent.putExtra("jiaxipiaoValue",jiaXiBean.getReward());
                            intent.putExtra("fwAddRateId",jiaXiBean.getId());
                            intent.putExtra("jiaxipiaoDay",jiaXiBean.getDays());
                            intent.putExtra("jiaxipiaoIsCheck",jiaXiBean.getIsCheck());
                            setResult(2, intent);
                            finish();
                        }
                    });
                }

            }
        });
        listView.setSelector(android.R.color.transparent);
        // 5.设置adapter
        adapter = new JiaXiPiaoFwBuyAdapter(this, list, fwAddRateId);
        listView.setAdapter(adapter);
    }

    protected void getDataFromServer() {
        String datastr = getIntent().getStringExtra("jsonData");
        JSONObject data = JSON.parseObject(datastr);
        list = (ArrayList<JiaXiBean>)JSONArray.parseArray(
                data.getJSONArray("addRateTicketList").toJSONString(),JiaXiBean.class);
    }

    @OnClick({R.id.iv_back, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_btn_finish:
                Intent intent = getIntent();
                intent.putExtra("jiaxipiaoValue",0);
                intent.putExtra("fwAddRateId","");
                intent.putExtra("jiaxipiaoDay","");
                intent.putExtra("jiaxipiaoIsCheck","");
                setResult(2, intent);
                finish();
                break;
        }
    }
}
