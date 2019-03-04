package com.iqianbang.fanpai.activity.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.fragment.guanfang.GongSiFragment;
import com.iqianbang.fanpai.fragment.guanfang.PingTaiFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GuanFangActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"平台公告", "公司动态"};
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_fang);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        // 准备数据
        fragments = new ArrayList<Fragment>();
        fragments.add(new PingTaiFragment());
        fragments.add(new GongSiFragment());

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
