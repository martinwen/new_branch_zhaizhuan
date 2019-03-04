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
import com.iqianbang.fanpai.fragment.FtMaxAllJiLuFragment;
import com.iqianbang.fanpai.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FtMaxZaiTouJiLuActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"全部", "出借", "收益", "回款"};
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zai_tou_ji_lu);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        String transUuid = getIntent().getStringExtra("transUuid");
        LogUtils.i("transUuid=="+transUuid);
        // 准备数据
        fragments = new ArrayList<Fragment>();
        FtMaxAllJiLuFragment ftMaxAllJiLuFragment = new FtMaxAllJiLuFragment();
        ftMaxAllJiLuFragment.setData("11", transUuid);

        FtMaxAllJiLuFragment ftMaxTouRuFragment = new FtMaxAllJiLuFragment();
        ftMaxTouRuFragment.setData("12", transUuid);

        FtMaxAllJiLuFragment ftMaxShouYiFragment = new FtMaxAllJiLuFragment();
        ftMaxShouYiFragment.setData("13", transUuid);

        FtMaxAllJiLuFragment ftMaxShuHuiFragment = new FtMaxAllJiLuFragment();
        ftMaxShuHuiFragment.setData("14", transUuid);

        fragments.add(ftMaxAllJiLuFragment);
        fragments.add(ftMaxTouRuFragment);
        fragments.add(ftMaxShouYiFragment);
        fragments.add(ftMaxShuHuiFragment);

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
