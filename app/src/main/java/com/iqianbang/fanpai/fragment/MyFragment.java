package com.iqianbang.fanpai.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.find.AboutUsActivity;
import com.iqianbang.fanpai.activity.find.HelpActivity;
import com.iqianbang.fanpai.activity.find.WebviewActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.adapter.FindAdapter;
import com.iqianbang.fanpai.bean.FindBean;
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
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/12/13.
 */

public class MyFragment extends BaseFragment {

    FragmentManager fm;
    FragmentTransaction ft;
    Fragment mCurrentFragment;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_my, null);
        return view;
    }

    @Override
    public void initData() {
        // 准备数据
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        UserNewFragment userNewFragment = new UserNewFragment();
        UserFragment userFragment = new UserFragment();

        /**
         * 在这里进行添加页面，设置fragment的TAG值
         */
        ft.add(R.id.fr, userNewFragment, "userNewFragment").add(R.id.fr, userFragment, "userFragment")
                .hide(userFragment)
                .commit();

    }

    /**
     * 主Fragment进行控制切换fragment
     *
     * @param fromTag
     * @param toTag
     */
    public void switchFragment(String fromTag, String toTag) {
        Fragment from = fm.findFragmentByTag(fromTag);
        Fragment to = fm.findFragmentByTag(toTag);
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            FragmentTransaction transaction = fm.beginTransaction();
            if (!to.isAdded()) {//判断是否被添加到了Activity里面去了
                transaction.hide(from).add(R.id.fr, to).commit();
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
    }

}
