package com.iqianbang.fanpai.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FanTongBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.FtMinBuySuccessActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouActivity;
import com.iqianbang.fanpai.activity.user.FtMinZaiTouActivity;
import com.iqianbang.fanpai.adapter.jiaxipiao.JiaXiPiaoFwUseableAdapter;
import com.iqianbang.fanpai.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/12/13.
 */

public class InvestFragment extends BaseFragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"大饭团", "小饭团", "精英团", "转让专区"};
    private List<Fragment> fragments;

    @Override
    protected View initView() {
        LogUtils.i("走产品页initView");
        View view = View.inflate(mActivity, R.layout.activity_invest, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.i("走产品页initData");
        // 准备数据
        fragments = new ArrayList<Fragment>();
        fragments.add(new InvestFor2Fragment());
        fragments.add(new InvestFor4Fragment());
        fragments.add(new InvestFor1Fragment());
        fragments.add(new InvestZhiTouFragment());

        TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(adapter);
        //关联一下指示器
        tabLayout.setupWithViewPager(viewpager);

        String from = mActivity.getIntent().getStringExtra("from");
        LogUtils.i("from==" + from);
        if (!TextUtils.isEmpty(from)) {
            if (from.equals(FtMinBuySuccessActivity.FLAG_MINBUYSUCCESS) ||
                from.equals(FtMinZaiTouActivity.FLAG_FTMINGOTOINVEST) ||
                from.equals(HomeFragment.FLAG_GOTOSMALLTUAN)) {

                viewpager.setCurrentItem(1, false);

            } else if (from.equals(FanTongBuySuccessActivity.FLAG_JINGYINGBUYSUCCESS) ||
                    from.equals(HomeFragment.FLAG_GOTOJYTUAN)) {

                viewpager.setCurrentItem(2, false);
            } else {
                viewpager.setCurrentItem(0, false);
            }
        } else {
            viewpager.setCurrentItem(0, false);
        }
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
