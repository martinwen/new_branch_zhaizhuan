package com.iqianbang.fanpai.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMinUseablefragment;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMinUsingfragment;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMinUsedfragment;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutActivity extends BaseActivity {

    private String[] titles = new String[]{"AAAAA", "BBBBB", "CCCCC"};
    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        initData();
    }

    private void initData() {
        // 准备数据
        fragments = new ArrayList<Fragment>();
        fragments.add(new FtMinUseablefragment());
        fragments.add(new FtMinUsingfragment());
        fragments.add(new FtMinUsedfragment());

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
}
