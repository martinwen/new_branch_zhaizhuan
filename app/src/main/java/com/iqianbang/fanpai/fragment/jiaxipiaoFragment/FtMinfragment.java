package com.iqianbang.fanpai.fragment.jiaxipiaoFragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WenGuangJun on 2017/3/25.
 */

public class FtMinfragment extends BaseFragment {


    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"可使用", "使用中"};
    private List<Fragment> fragments;
    private String code;



    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_ftmin, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        // 准备数据
        fragments = new ArrayList<Fragment>();
        FtMinUseablefragment ftMinUseablefragment = new FtMinUseablefragment();
        fragments.add(ftMinUseablefragment);

        FtMinUsingfragment ftMinUsingfragment = new FtMinUsingfragment();
        fragments.add(ftMinUsingfragment);

        TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager());
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

    public void setCode(String code) {
        this.code = code;
    }

}
