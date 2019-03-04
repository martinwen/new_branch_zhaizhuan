package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FtMaxDetailActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;

import java.util.ArrayList;

/**
 * 类描述：FtMaxListAdapter
 * @author WenGuangJun
 * create at 2018/5/11 19:09
 */

public class FtMaxListAdapter extends BaseAdapter {

    private ArrayList<FtMaxListBean> list;
    private Context mContext;

    public FtMaxListAdapter(Context context, ArrayList<FtMaxListBean> list) {
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
            convertView = View.inflate(mContext, R.layout.items_ftmaxlist, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_rate_base = (TextView) convertView.findViewById(R.id.tv_rate_base);
            holder.tv_rate_add = (TextView) convertView.findViewById(R.id.tv_rate_add);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.tv_btn = (TextView) convertView.findViewById(R.id.tv_btn);
            convertView.setTag(holder);
        } else {
            holder = (NoticeHolder) convertView.getTag();
        }

        final FtMaxListBean ftMaxListBean = list.get(position);
        holder.tv_name.setText(ftMaxListBean.getProductName());
        holder.tv_money.setText("剩余金额：" + ftMaxListBean.getRemainingMoney() + "元");
        holder.tv_rate_base.setText(ftMaxListBean.getBaseRate());
        holder.tv_rate_add.setText("+" + ftMaxListBean.getTotalActivityRate() + "%");
        holder.tv_progress.setText(ftMaxListBean.getProgress() + "%");
        holder.progressBar.setProgress(ftMaxListBean.getProgress());

        if("1".equals(ftMaxListBean.getStatus())){
            holder.tv_name.setBackgroundResource(R.drawable.ftmax_list_name_bg_ok);
            holder.tv_rate_base.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
            holder.tv_rate_add.setBackgroundResource(R.drawable.ftmax_list_rate_bg_ok);
            holder.tv_btn.setText("立即出借");
            holder.tv_btn.setTextColor(mContext.getResources().getColor(R.color.global_whitecolor));
            holder.tv_btn.setBackgroundResource(R.drawable.shape_bg_btn_red);
            holder.tv_progress.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_ftmaxlist_red));
        }else if("2".equals(ftMaxListBean.getStatus())) {
            holder.tv_name.setBackgroundResource(R.drawable.ftmax_list_name_bg_ok);
            holder.tv_rate_base.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
            holder.tv_rate_add.setBackgroundResource(R.drawable.ftmax_list_rate_bg_ok);
            holder.tv_btn.setText(ftMaxListBean.getPublishedTime()+"开抢");
            holder.tv_btn.setTextColor(mContext.getResources().getColor(R.color.text_graycolor));
            holder.tv_btn.setBackgroundResource(R.drawable.shape_bg_btn_gray_stroke);
            holder.tv_progress.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_ftmaxlist_red));
        }else if("4".equals(ftMaxListBean.getStatus())||"5".equals(ftMaxListBean.getStatus())){
            holder.tv_name.setBackgroundResource(R.drawable.ftmax_list_name_bg_over);
            holder.tv_rate_base.setTextColor(mContext.getResources().getColor(R.color.text_graycolor));
            holder.tv_rate_add.setBackgroundResource(R.drawable.ftmax_list_rate_bg_over);
            holder.tv_btn.setText("已售罄");
            holder.tv_btn.setTextColor(mContext.getResources().getColor(R.color.text_graycolor));
            holder.tv_btn.setBackgroundResource(R.drawable.shape_bg_btn_gray_stroke);
            holder.tv_progress.setTextColor(mContext.getResources().getColor(R.color.text_graycolor));
            holder.progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_ftmaxlist_gray));
        }
        holder.tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FtMaxDetailActivity.class);
                intent.putExtra("dftProInfoId",ftMaxListBean.getId());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class NoticeHolder {
        TextView tv_name;
        TextView tv_money;
        TextView tv_rate_base;
        TextView tv_rate_add;
        TextView tv_progress;
        ProgressBar progressBar;
        TextView tv_btn;
    }
}
