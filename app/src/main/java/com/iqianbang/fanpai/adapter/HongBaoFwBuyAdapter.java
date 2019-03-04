package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.HongBaoUseActivity;
import com.iqianbang.fanpai.bean.HongBaoBean;

import java.util.ArrayList;


public class HongBaoFwBuyAdapter extends BaseAdapter {

	private ArrayList<HongBaoBean> list;
	private Context mContext;
	private String redbagTicketId;

	public HongBaoFwBuyAdapter(Context context, ArrayList<HongBaoBean> list,String redbagTicketId) {
		super();
		this.mContext = context;
		this.list = list;
		this.redbagTicketId = redbagTicketId;
	}
	public HongBaoFwBuyAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_hongbao_fw, null);
			holder.tv_hongbao_name = (TextView) convertView.findViewById(R.id.tv_hongbao_name);
			holder.tv_hongbao_usetime = (TextView) convertView.findViewById(R.id.tv_hongbao_usetime);
			holder.tv_invest_money = (TextView) convertView.findViewById(R.id.tv_invest_money);
			holder.tv_hongbao_info = (TextView) convertView.findViewById(R.id.tv_hongbao_info);
			holder.tv_hongbao_method = (TextView) convertView.findViewById(R.id.tv_hongbao_method);
			holder.tv_hongbao_explain = (TextView) convertView.findViewById(R.id.tv_hongbao_explain);
			holder.iv_use = (ImageView) convertView.findViewById(R.id.iv_use);
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

		holder.tv_hongbao_usetime.setText("有效时间   "+createTime+"至"+expiredTime);
		
		//最低出借
		holder.tv_invest_money.setText("最低出借   "+hongBaoBean.getUseCondition()+"元");

		//适用产品
		holder.tv_hongbao_info.setText("适用范围   "+hongBaoBean.getSuitProduct());
		
		//获得来源
		holder.tv_hongbao_method.setText("获得来源   "+hongBaoBean.getFromSource());

		//特别说明
		holder.tv_hongbao_explain.setText("特别说明   "+hongBaoBean.getSpecialDesc());

		///红包ID
		final String id = hongBaoBean.getId();

		if (id.equals(redbagTicketId)) {
			holder.iv_use.setImageResource(R.drawable.hongbao_use);
		}else {
			holder.iv_use.setImageResource(R.drawable.jiaxipiao_btn_click);
		}

		return convertView;
	}


	class NoticeHolder{
		TextView tv_hongbao_name;
		TextView tv_hongbao_usetime;
		TextView tv_invest_money;
		TextView tv_hongbao_info;
		TextView tv_hongbao_method;
		TextView tv_hongbao_explain;
		ImageView iv_use;
	}
}
