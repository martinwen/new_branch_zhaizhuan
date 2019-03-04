package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.HuiKuanDetailBean;
import com.iqianbang.fanpai.bean.NoticeBean;

import java.util.ArrayList;


public class HuiKuanDetailAdapter extends BaseAdapter {

	private ArrayList<HuiKuanDetailBean> list;
	private Context mContext;
	public HuiKuanDetailAdapter(Context context, ArrayList<HuiKuanDetailBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public HuiKuanDetailAdapter(Context context) {
		super();
		this.mContext = context;
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
			convertView = View.inflate(mContext, R.layout.items_huikuan_detail, null);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
			
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}

		HuiKuanDetailBean huiKuanDetailBean = list.get(position);
		holder.tv_time.setText(huiKuanDetailBean.getPlanRepayTime());
		holder.tv_money.setText(huiKuanDetailBean.getAmount());
		holder.tv_info.setText(huiKuanDetailBean.getStatusDesc());

		return convertView;
	}

	class NoticeHolder{
		TextView tv_time;
		TextView tv_money;
		TextView tv_info;
	}
}
