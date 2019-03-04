package com.iqianbang.fanpai.adapter.hongbao;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.HongBaoBean;

import java.util.ArrayList;


public class HongBaoUseableAdapter extends BaseAdapter {

	private ArrayList<HongBaoBean> list;
	private Context mContext;
	
	public HongBaoUseableAdapter(Context context, ArrayList<HongBaoBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public HongBaoUseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_hongbao_usable, null);
			holder.tv_hongbao_name = (TextView) convertView.findViewById(R.id.tv_hongbao_name);
			holder.tv_hongbao_usetime = (TextView) convertView.findViewById(R.id.tv_hongbao_usetime);
			holder.tv_invest_money = (TextView) convertView.findViewById(R.id.tv_invest_money);
			holder.tv_hongbao_info = (TextView) convertView.findViewById(R.id.tv_hongbao_info);
			holder.tv_hongbao_method = (TextView) convertView.findViewById(R.id.tv_hongbao_method);
			holder.tv_hongbao_explain = (TextView) convertView.findViewById(R.id.tv_hongbao_explain);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		HongBaoBean hongBaoBean = list.get(position);
		//红包名称
		String name = hongBaoBean.getName()+"<font color='#FF0000'>"+hongBaoBean.getReward()+"</font>"+"元";
		holder.tv_hongbao_name.setText(Html.fromHtml(name));
		
		//红包有效期
		//以空格截取字符串
		String splitCreateTime[] = hongBaoBean.getCreateTime().split(" ");
		String createTime = splitCreateTime[0];

		String splitExpiredTime[] = hongBaoBean.getExpiredTime().split(" ");
		String expiredTime = splitExpiredTime[0];

		//最低出借
		holder.tv_invest_money.setText("最低出借   "+hongBaoBean.getUseCondition()+"元");

		//使用时间
		holder.tv_hongbao_usetime.setText("有效时间   "+createTime+"至"+expiredTime);
		
		//适用产品
		holder.tv_hongbao_info.setText("适用范围   "+hongBaoBean.getSuitProduct());
		
		//获得来源
		holder.tv_hongbao_method.setText("获得来源   "+hongBaoBean.getFromSource());

		//特别说明
		holder.tv_hongbao_explain.setText("特别说明   "+hongBaoBean.getSpecialDesc());

		return convertView;
	}


	class NoticeHolder{
		TextView tv_hongbao_name;
		TextView tv_invest_money;
		TextView tv_hongbao_usetime;
		TextView tv_hongbao_info;
		TextView tv_hongbao_method;
		TextView tv_hongbao_explain;
	}
}
