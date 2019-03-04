package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.FWBean;

import java.util.ArrayList;

public class FanWanJieQingAdapter extends BaseAdapter {

	private ArrayList<FWBean> list;
	private Context mContext;
	public FanWanJieQingAdapter(Context context, ArrayList<FWBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_fanwanjieqing, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_rate = (TextView) convertView.findViewById(R.id.tv_rate);
			holder.tv_leiji = (TextView) convertView.findViewById(R.id.tv_leiji);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}

		FWBean fwBean = list.get(position);
		holder.tv_name.setText(fwBean.getCollectionName());//投资记录名称
		holder.tv_time.setText("出借时间：" + fwBean.getTransTime());//出借时间
		holder.tv_money.setText("出借金额：" + fwBean.getInvestAmount()+"元");//出借金额
		holder.tv_rate.setText("历史年化：" + fwBean.getYearRate()+"%");//预期结算利率
		holder.tv_leiji.setText("已结收益：" + fwBean.getAccumulativeIncome()+"元");//已结收益
		
		return convertView;
	}

	class NoticeHolder{
		TextView tv_name;//投资记录名称
		TextView tv_time;//出借时间
		TextView tv_money;//出借金额
		TextView tv_rate;//预期结算利率
		TextView tv_leiji;//累计收益
	}
}
