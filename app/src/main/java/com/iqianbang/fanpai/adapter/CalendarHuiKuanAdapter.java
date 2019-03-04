package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.CalendarHuiKuanDayBean;
import com.iqianbang.fanpai.bean.NoticeBean;

import java.util.ArrayList;


public class CalendarHuiKuanAdapter extends BaseAdapter {

	private ArrayList<CalendarHuiKuanDayBean> list;
	private Context mContext;
	public CalendarHuiKuanAdapter(Context context, ArrayList<CalendarHuiKuanDayBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public CalendarHuiKuanAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_calendar_huikuan, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_money_benjin = (TextView) convertView.findViewById(R.id.tv_money_benjin);
			holder.tv_money_lixi = (TextView) convertView.findViewById(R.id.tv_money_lixi);
			
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}

		CalendarHuiKuanDayBean calendarHuiKuanDayBean = list.get(position);

		//名称
		if("1".equals(calendarHuiKuanDayBean.getIsTransfer())){//是否债转 1是 0否
			holder.tv_name.setText(calendarHuiKuanDayBean.getBorrowName() + "【转】");
		}else{
			holder.tv_name.setText(calendarHuiKuanDayBean.getBorrowName());
		}

		if("1".equals(calendarHuiKuanDayBean.getStatus())){//1待还    2已还
			holder.tv_money_benjin.setText("待还本金：" + calendarHuiKuanDayBean.getCapital() + "元");
			holder.tv_money_lixi.setText("待还利息：" + calendarHuiKuanDayBean.getInterest() + "元");
		}else{
			holder.tv_money_benjin.setText("已还本金：" + calendarHuiKuanDayBean.getCapital() + "元");
			holder.tv_money_lixi.setText("已还利息：" + calendarHuiKuanDayBean.getInterest() + "元");
		}

		if(calendarHuiKuanDayBean.getCapital() > 0){
			holder.tv_money_benjin.setVisibility(View.VISIBLE);
		}else {
			holder.tv_money_benjin.setVisibility(View.GONE);
		}

		return convertView;
	}

	class NoticeHolder{
		TextView tv_name;
		TextView tv_money_benjin;
		TextView tv_money_lixi;
	}
}
