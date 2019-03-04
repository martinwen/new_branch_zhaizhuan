package com.iqianbang.fanpai.adapter.jiaxipiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.JiaXiBean;

import java.util.ArrayList;


public class JiaXiPiaoFhUnusedAdapter extends BaseAdapter {

	private ArrayList<JiaXiBean> list;
	private Context mContext;
	public JiaXiPiaoFhUnusedAdapter(Context context, ArrayList<JiaXiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public JiaXiPiaoFhUnusedAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_jiaxipiao_unused, null);
			holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);
			holder.tv_jiaxi_rate = (TextView) convertView.findViewById(R.id.tv_jiaxi_rate);
			holder.tv_jiaxi_day = (TextView) convertView.findViewById(R.id.tv_jiaxi_day);
			holder.tv_jiaxi_endtime = (TextView) convertView.findViewById(R.id.tv_jiaxi_endtime);
			holder.tv_jiaxi_info = (TextView) convertView.findViewById(R.id.tv_jiaxi_info);
			holder.tv_jiaxi_method = (TextView) convertView.findViewById(R.id.tv_jiaxi_method);
			holder.iv_unused = (ImageView) convertView.findViewById(R.id.iv_unused);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		JiaXiBean jiaXiBean = list.get(position);
		//加息名称
		holder.tv_jiaxi_name.setText(jiaXiBean.getName());
		
		//加息利率
		if (jiaXiBean.getReward()==0) {
			holder.tv_jiaxi_rate.setVisibility(View.INVISIBLE);
		}else {
			holder.tv_jiaxi_rate.setVisibility(View.VISIBLE);
			holder.tv_jiaxi_rate.setText(jiaXiBean.getReward()+"%");
		}
		
		//加息天数	
		holder.tv_jiaxi_day.setText("加息天数   "+jiaXiBean.getDays()+"天");
		
		//结束时间    "3"使用之后正常结束     "5"已过期（失效）
		if ("3".equals(jiaXiBean.getStatus())||"4".equals(jiaXiBean.getStatus())) {
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_used);
			holder.tv_jiaxi_endtime.setText("加息时间   "+jiaXiBean.getStartTime()+"至"+jiaXiBean.getEndTime());
		}else if ("5".equals(jiaXiBean.getStatus())) {
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_passed);
			holder.tv_jiaxi_endtime.setText("失效时间   "+jiaXiBean.getExpiredTime());
		}
		
		//特别说明
		holder.tv_jiaxi_info.setText("特别说明   "+jiaXiBean.getRemark());
		
		//获得来源
		holder.tv_jiaxi_method.setText("获得来源   "+jiaXiBean.getSource());
		
		
		return convertView;
	}

	class NoticeHolder{
		TextView tv_jiaxi_name;
		TextView tv_jiaxi_rate;
		TextView tv_jiaxi_day;
		TextView tv_jiaxi_endtime;
		TextView tv_jiaxi_info;
		TextView tv_jiaxi_method;
		ImageView iv_unused;
	}
}
