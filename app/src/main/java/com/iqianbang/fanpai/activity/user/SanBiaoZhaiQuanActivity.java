package com.iqianbang.fanpai.activity.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.fragment.sanbiao.SanBiaoXieYiFragment;
import com.iqianbang.fanpai.fragment.sanbiao.ZhaiQuanFragment;
import com.iqianbang.fanpai.fragment.xiangmudetailFragment.BidInfoFragment;
import com.iqianbang.fanpai.fragment.xiangmudetailFragment.DftXieYiFragment;
import com.iqianbang.fanpai.fragment.xiangmudetailFragment.XieYiFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SanBiaoZhaiQuanActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"债权详情", "出借协议"};
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zai_tou_xiang_mu_detail);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        String from = getIntent().getStringExtra("from");
        String bid = getIntent().getStringExtra("bid");
        String matchId = getIntent().getStringExtra("id");
        // 准备数据
        fragments = new ArrayList<Fragment>();
        ZhaiQuanFragment zhaiQuanFragment = new ZhaiQuanFragment();
        zhaiQuanFragment.setFrom(from);
        zhaiQuanFragment.setBorrowId(bid);
        zhaiQuanFragment.setMatchId(matchId);
        fragments.add(zhaiQuanFragment);

        SanBiaoXieYiFragment sanBiaoXieYiFragment = new SanBiaoXieYiFragment();
        sanBiaoXieYiFragment.setFrom(from);
        sanBiaoXieYiFragment.setData(matchId);
        fragments.add(sanBiaoXieYiFragment);


        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        //关联一下指示器
        tabLayout.setupWithViewPager(viewpager);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
