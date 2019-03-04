package com.iqianbang.fanpai.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.BidInfoActivity;
import com.iqianbang.fanpai.bean.BiaoDeBean;

import java.util.ArrayList;

/**
 * @author lijinliu
 * @date 20180129
 * 标的列表adapter
 */
public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.Myholder> {

    private FragmentActivity mContext;
    private ArrayList<BiaoDeBean> list;

    public BidListAdapter(FragmentActivity activity, ArrayList<BiaoDeBean> list) {
        this.mContext=activity;
        this.list = list;
    }

    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.items_bidinfo, parent, false);
        Myholder myholder = new Myholder(view);
        return myholder;

    }

    @Override
    public void onBindViewHolder(Myholder holder, int position) {
        final BiaoDeBean biaoDeBean = list.get(position);
        holder.tv_name.setText(biaoDeBean.getBorrowName());
        holder.tv_money.setText("金额："+biaoDeBean.getPackageMoney()+"元");
        holder.tv_term.setText("期限："+biaoDeBean.getBorrowTerm()+"天");

        ViewGroup parent =  (ViewGroup) holder.tv_name.getParent();
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BidInfoActivity.class);
                intent.putExtra("bid", biaoDeBean.getBid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Myholder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private TextView tv_money;
        private TextView tv_term;

        public Myholder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
            tv_term = (TextView) itemView.findViewById(R.id.tv_term);
        }
    }
}


