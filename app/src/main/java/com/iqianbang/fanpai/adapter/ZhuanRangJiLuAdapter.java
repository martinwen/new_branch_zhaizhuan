package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.InvestJiLuBean;
import com.iqianbang.fanpai.bean.ZhuanRangJiLuBean;

import java.util.ArrayList;


public class ZhuanRangJiLuAdapter extends BaseAdapter {

	private ArrayList<ZhuanRangJiLuBean> list;
	private Context mContext;
	public ZhuanRangJiLuAdapter(Context context, ArrayList<ZhuanRangJiLuBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public ZhuanRangJiLuAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_zhuanrangjilu, null);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_zherang = (TextView) convertView.findViewById(R.id.tv_zherang);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.iv_success = (ImageView) convertView.findViewById(R.id.iv_success);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		ZhuanRangJiLuBean zhuanRangJiLuBean = list.get(position);
		holder.tv_time.setText(zhuanRangJiLuBean.getTranTimeStr());
		holder.tv_zherang.setText(zhuanRangJiLuBean.getDiscountRate() + "%");
		holder.tv_money.setText(zhuanRangJiLuBean.getTranAmount() + "元");
		if("2".equals(zhuanRangJiLuBean.getStatus())){
			holder.iv_success.setVisibility(View.VISIBLE);
		}else {
			holder.iv_success.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class NoticeHolder{
		TextView tv_time;
		TextView tv_zherang;
		TextView tv_money;
		ImageView iv_success;
	}
}
