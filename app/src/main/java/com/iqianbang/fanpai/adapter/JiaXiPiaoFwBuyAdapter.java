package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.JiaXiPiaoUseActivity;
import com.iqianbang.fanpai.bean.JiaXiBean;
import com.iqianbang.fanpai.global.FanPaiApplication;

import java.util.ArrayList;


public class JiaXiPiaoFwBuyAdapter extends BaseAdapter {

	private ArrayList<JiaXiBean> list;
	private Context mContext;
	private String fwAddRateId;

	public JiaXiPiaoFwBuyAdapter(Context context, ArrayList<JiaXiBean> list, String fwAddRateId) {
		super();
		this.mContext = context;
		this.list = list;
		this.fwAddRateId = fwAddRateId;
	}
	public JiaXiPiaoFwBuyAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_jiaxipiao_fw, null);
			holder.tv_jiaxi_name = (TextView) convertView.findViewById(R.id.tv_jiaxi_name);
			holder.tv_jiaxi_rate = (TextView) convertView.findViewById(R.id.tv_jiaxi_rate);
			holder.tv_jiaxi_time = (TextView) convertView.findViewById(R.id.tv_jiaxi_time);
			holder.tv_jiaxi_endtime = (TextView) convertView.findViewById(R.id.tv_jiaxi_endtime);
			holder.tv_jiaxi_info = (TextView) convertView.findViewById(R.id.tv_jiaxi_info);
			holder.tv_jiaxi_method = (TextView) convertView.findViewById(R.id.tv_jiaxi_method);
			holder.iv_use = (ImageView) convertView.findViewById(R.id.iv_use);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		JiaXiBean jiaXiBean = list.get(position);
		//加息名称
		holder.tv_jiaxi_name.setText(jiaXiBean.getName());
		
		//加息利率
		double jiaxi_rate = jiaXiBean.getReward();
		holder.tv_jiaxi_rate.setText(jiaxi_rate+"%");
		
		//加息券ID
		final String id = jiaXiBean.getId();
		if (id.equals(fwAddRateId)) {
			holder.iv_use.setImageResource(R.drawable.hongbao_use);
		}else {
			holder.iv_use.setImageResource(R.drawable.jiaxipiao_btn_click);
		}

		//加息天数
		//1.与锁定期一致 0.不一致
		if ("0".equals(jiaXiBean.getIsCheck())) {
			holder.tv_jiaxi_time.setText("加息天数   "+jiaXiBean.getDays()+"天");
		}else {
			holder.tv_jiaxi_time.setText("加息天数   "+jiaXiBean.getDays());
		}




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
		ImageView iv_use;
	}
}
