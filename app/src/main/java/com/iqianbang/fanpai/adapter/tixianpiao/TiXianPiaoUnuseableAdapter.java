package com.iqianbang.fanpai.adapter.tixianpiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.TiXianBean;

import java.util.ArrayList;


public class TiXianPiaoUnuseableAdapter extends BaseAdapter {

	private ArrayList<TiXianBean> list;
	private Context mContext;
	
	public TiXianPiaoUnuseableAdapter(Context context, ArrayList<TiXianBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public TiXianPiaoUnuseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_tixianpiao_unusable, null);
			holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);
			holder.tv_jiaxi_rate = (TextView) convertView.findViewById(R.id.tv_jiaxi_rate);
			holder.tv_jiaxi_endtime = (TextView) convertView.findViewById(R.id.tv_jiaxi_endtime);
			holder.tv_jiaxi_info = (TextView) convertView.findViewById(R.id.tv_jiaxi_info);
			holder.tv_jiaxi_method = (TextView) convertView.findViewById(R.id.tv_jiaxi_method);
			holder.iv_use = (ImageView) convertView.findViewById(R.id.iv_use);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		TiXianBean tiXianBean = list.get(position);
		//提现名称
		holder.tv_jiaxi_name.setText(tiXianBean.getName());
		
		//提现票值
		holder.tv_jiaxi_rate.setText(tiXianBean.getAmount()+"元");
		
		//特别说明
		holder.tv_jiaxi_info.setText("特别说明   "+tiXianBean.getSpecialDesc());
		
		//获得来源
		holder.tv_jiaxi_method.setText("获得来源   "+tiXianBean.getFromSource());
		
		//结束时间    "2"使用时间     "3"已过期（失效）
		if ("2".equals(tiXianBean.getStatus())) {
			holder.iv_use.setImageResource(R.drawable.jiaxipiao_btn_used);
			holder.tv_jiaxi_endtime.setText("使用时间   "+tiXianBean.getUseTime());
		}else if ("3".equals(tiXianBean.getStatus())) {
			holder.iv_use.setImageResource(R.drawable.jiaxipiao_btn_passed);
			holder.tv_jiaxi_endtime.setText("失效时间   "+tiXianBean.getExpireTime());
		}
		
		return convertView;
	}


	class NoticeHolder{
		TextView tv_jiaxi_name;
		TextView tv_jiaxi_rate;
		TextView tv_jiaxi_endtime;
		TextView tv_jiaxi_info;
		TextView tv_jiaxi_method;
		ImageView iv_use;
	}
}
