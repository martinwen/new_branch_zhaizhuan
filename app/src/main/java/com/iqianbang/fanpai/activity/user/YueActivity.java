package com.iqianbang.fanpai.activity.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.fragment.yueFragment.AllFragment;
import com.iqianbang.fanpai.fragment.yueFragment.CashFragment;
import com.iqianbang.fanpai.fragment.yueFragment.ChongZhiFragment;
import com.iqianbang.fanpai.fragment.yueFragment.TiXianFragment;
import com.iqianbang.fanpai.fragment.yueFragment.TouZiFragment;
import com.iqianbang.fanpai.utils.CacheUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YueActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_chongzhi)
    TextView tvChongzhi;
    @BindView(R.id.tv_tixian)
    TextView tvTixian;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] titles = new String[]{"全部", "充值", "提现", "出借", "回款"};
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yue);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        // 总充值
        String totalRechargeAmount = CacheUtils.getString(CacheUtils.TOTALRECHARGEAMOUNT, "0.00");
        tvChongzhi.setText(totalRechargeAmount);
        // 总提现
        String totalCashAmount = CacheUtils.getString(CacheUtils.TOTALCASHAMOUNT, "0.00");
        tvTixian.setText(totalCashAmount);

        // 准备数据
        fragments = new ArrayList<Fragment>();
        fragments.add(new AllFragment());
        fragments.add(new ChongZhiFragment());
        fragments.add(new TiXianFragment());
        fragments.add(new TouZiFragment());
        fragments.add(new CashFragment());

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
