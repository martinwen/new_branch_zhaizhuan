package com.iqianbang.fanpai.adapter.tixianpiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.TiXianBean;

import java.util.ArrayList;


public class TiXianPiaoUseableAdapter extends BaseAdapter {

	private ArrayList<TiXianBean> list;
	private Context mContext;
	
	public TiXianPiaoUseableAdapter(Context context, ArrayList<TiXianBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public TiXianPiaoUseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_tixianpiao_usable, null);
			holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);
			holder.tv_jiaxi_rate = (TextView) convertView.findViewById(R.id.tv_jiaxi_rate);
			holder.tv_jiaxi_endtime = (TextView) convertView.findViewById(R.id.tv_jiaxi_endtime);
			holder.tv_jiaxi_info = (TextView) convertView.findViewById(R.id.tv_jiaxi_info);
			holder.tv_jiaxi_method = (TextView) convertView.findViewById(R.id.tv_jiaxi_method);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		TiXianBean tiXianBean = list.get(position);

		//以空格截取字符串
		String splitCreateTime[] = tiXianBean.getCreateTime().split(" ");
		String createTime = splitCreateTime[0];

		String splitExpiredTime[] = tiXianBean.getExpireTime().split(" ");
		String expiredTime = splitExpiredTime[0];
		//提现名称
		holder.tv_jiaxi_name.setText(tiXianBean.getName());
		
		//提现票值
		holder.tv_jiaxi_rate.setText(tiXianBean.getAmount()+"元");

		//有效时间
		holder.tv_jiaxi_endtime.setText("有效时间   "+createTime+"至"+expiredTime);
		
		//特别说明
		holder.tv_jiaxi_info.setText("特别说明   "+tiXianBean.getSpecialDesc());
		
		//获得来源
		holder.tv_jiaxi_method.setText("获得来源   "+tiXianBean.getFromSource());
		
		return convertView;
	}


	class NoticeHolder{
		TextView tv_jiaxi_name;
		TextView tv_jiaxi_rate;
		TextView tv_jiaxi_endtime;
		TextView tv_jiaxi_info;
		TextView tv_jiaxi_method;
	}
}
