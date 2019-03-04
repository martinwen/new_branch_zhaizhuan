package com.iqianbang.fanpai.adapter.jiaxipiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.JiaXiBean;

import java.util.ArrayList;

public class JiaXiPiaoFwUseableAdapter extends BaseAdapter {

	private ArrayList<JiaXiBean> list;
	private Context mContext;

	public JiaXiPiaoFwUseableAdapter(Context context, ArrayList<JiaXiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public JiaXiPiaoFwUseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_jiaxipiao_fwusable, null);
			holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);
			holder.tv_jiaxi_rate = (TextView) convertView.findViewById(R.id.tv_jiaxi_rate);
			holder.tv_jiaxi_time = (TextView) convertView.findViewById(R.id.tv_jiaxi_time);
			holder.tv_jiaxi_endtime = (TextView) convertView.findViewById(R.id.tv_jiaxi_endtime);
			holder.tv_jiaxi_info = (TextView) convertView.findViewById(R.id.tv_jiaxi_info);
			holder.tv_jiaxi_method = (TextView) convertView.findViewById(R.id.tv_jiaxi_method);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		final JiaXiBean jiaXiBean = list.get(position);
		//加息名称
		holder.tv_jiaxi_name.setText(jiaXiBean.getName());
		
		//加息利率
		final double jiaxi_rate = jiaXiBean.getReward();
		holder.tv_jiaxi_rate.setText(jiaxi_rate+"%");
		
		//加息券ID
		final String id = jiaXiBean.getId();

		//加息天数
		holder.tv_jiaxi_time.setText("加息天数   "+jiaXiBean.getDays());

		//有效时间
		String splitExpiredTime[] = jiaXiBean.getExpiredTime().split(" ");
		String expiredTime = splitExpiredTime[0];
		holder.tv_jiaxi_endtime.setText("有效时间   "+jiaXiBean.getAddTime()+"至"+expiredTime);
		
		//适用范围
		holder.tv_jiaxi_info.setText("适用范围   "+jiaXiBean.getSuitProduct());

		//获得来源
		holder.tv_jiaxi_method.setText("获得来源   "+jiaXiBean.getSource());

		return convertView;
	}

	class NoticeHolder{
		TextView tv_jiaxi_name;
		TextView tv_jiaxi_rate;
		TextView tv_jiaxi_time;
		TextView tv_jiaxi_endtime;
		TextView tv_jiaxi_info;
		TextView tv_jiaxi_method;
	}
}
