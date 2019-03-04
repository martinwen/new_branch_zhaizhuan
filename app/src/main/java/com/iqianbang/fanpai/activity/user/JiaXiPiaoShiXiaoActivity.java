package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMaxUsedfragment;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMinUsedfragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JiaXiPiaoShiXiaoActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_min)
    TextView tvTitleMin;
    @BindView(R.id.tv_title_max)
    TextView tvTitleMax;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.switch_fragments)
    FrameLayout switchFragments;

    private Fragment[] mPages = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jia_xi_piao_shixiao);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        //从加息票页面跳转过来
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");

        // 准备数据
        FtMinUsedfragment ftMinUsedfragment = new FtMinUsedfragment();
        mPages[0] = ftMinUsedfragment;

        FtMaxUsedfragment ftMaxUsedfragment = new FtMaxUsedfragment();
        mPages[1] = ftMaxUsedfragment;

        //初始化界面
        if("min".equals(from)){
            llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_min);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.switch_fragments, mPages[0])
                    .commit();
        }else{
            llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_max);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.switch_fragments, mPages[1])
                    .commit();
        }

    }

    @OnClick({R.id.iv_back, R.id.tv_title_min, R.id.tv_title_max})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_title_min:
                llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_min);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.switch_fragments, mPages[0])
                        .commit();
                break;
            case R.id.tv_title_max:
                llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_max);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.switch_fragments, mPages[1])
                        .commit();
                break;
        }
    }
}
