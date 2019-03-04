package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.InvestJiLuBean;

import java.util.ArrayList;


public class InvestJiLuAdapter extends BaseAdapter {

	private ArrayList<InvestJiLuBean> list;
	private Context mContext;
	public InvestJiLuAdapter(Context context, ArrayList<InvestJiLuBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public InvestJiLuAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_investjilu, null);
			holder.jilu_phone = (TextView) convertView.findViewById(R.id.jilu_phone);
			holder.jilu_money = (TextView) convertView.findViewById(R.id.jilu_money);
			holder.jilu_date = (TextView) convertView.findViewById(R.id.jilu_date);
			holder.jilu_time = (TextView) convertView.findViewById(R.id.jilu_time);

			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		InvestJiLuBean investJiLuBean = list.get(position);
		holder.jilu_phone.setText(investJiLuBean.getUserName());
		holder.jilu_money.setText(investJiLuBean.getAmount()+"元");
		holder.jilu_date.setText(investJiLuBean.getTransDateStr());
		holder.jilu_time.setText(investJiLuBean.getTransTimeStr());

		return convertView;
	}

	class NoticeHolder{
		TextView jilu_phone;
		TextView jilu_money;
		TextView jilu_date;
		TextView jilu_time;
	}
}
