package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.bean.ZhuanRangListBean;
import com.iqianbang.fanpai.utils.MathUtil;

import java.util.ArrayList;

/**
 * 类描述：FtMaxListAdapter
 * @author WenGuangJun
 * create at 2018/5/11 19:09
 */

public class ZtListAdapter extends BaseAdapter {

    private ArrayList<ZhuanRangListBean> list;
    private Context mContext;

    public ZtListAdapter(Context context, ArrayList<ZhuanRangListBean> list) {
        super();
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoticeHolder holder = null;
        if (convertView == null) {
            holder = new NoticeHolder();
            convertView = View.inflate(mContext, R.layout.items_ztlist, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
            holder.tv_rate_base = (TextView) convertView.findViewById(R.id.tv_rate_base);
            holder.tv_rate_zhe = (TextView) convertView.findViewById(R.id.tv_rate_zhe);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_btn = (TextView) convertView.findViewById(R.id.tv_btn);
            convertView.setTag(holder);
        } else {
            holder = (NoticeHolder) convertView.getTag();
        }

        final ZhuanRangListBean zhuanRangListBean = list.get(position);
        holder.tv_name.setText(zhuanRangListBean.getBorrowName());
        holder.tv_day.setText(zhuanRangListBean.getTerm() + "天");
        holder.tv_rate_base.setText(zhuanRangListBean.getBaseRate() + "%");
        holder.tv_rate_zhe.setText(MathUtil.subString(zhuanRangListBean.getDiscountRate(),2) + "%");
        holder.tv_money.setText("剩余金额：" + zhuanRangListBean.getBorrowSalableBal() + "元");

        return convertView;
    }

    class NoticeHolder {
        TextView tv_name;
        TextView tv_day;
        TextView tv_rate_base;
        TextView tv_rate_zhe;
        TextView tv_money;
        TextView tv_btn;
    }
}
