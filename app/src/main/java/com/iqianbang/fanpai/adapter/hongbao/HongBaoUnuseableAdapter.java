package com.iqianbang.fanpai.adapter.hongbao;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.HongBaoBean;

import java.util.ArrayList;


public class HongBaoUnuseableAdapter extends BaseAdapter {

	private ArrayList<HongBaoBean> list;
	private Context mContext;

	public HongBaoUnuseableAdapter(Context context, ArrayList<HongBaoBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public HongBaoUnuseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_hongbao_unusable, null);
			holder.tv_hongbao_name = (TextView) convertView.findViewById(R.id.tv_hongbao_name);
			holder.tv_hongbao_usetime = (TextView) convertView.findViewById(R.id.tv_hongbao_usetime);
			holder.tv_invest_money = (TextView) convertView.findViewById(R.id.tv_invest_money);
			holder.tv_hongbao_info = (TextView) convertView.findViewById(R.id.tv_hongbao_info);
			holder.tv_hongbao_method = (TextView) convertView.findViewById(R.id.tv_hongbao_method);
			holder.tv_hongbao_explain = (TextView) convertView.findViewById(R.id.tv_hongbao_explain);
			holder.iv_unused = (ImageView) convertView.findViewById(R.id.iv_unused);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		HongBaoBean hongBaoBean = list.get(position);
		//红包名称
		String name = hongBaoBean.getName()+"<font color='#FF0000'>"+hongBaoBean.getReward()+"</font>"+"元";
		holder.tv_hongbao_name.setText(Html.fromHtml(name));

		//最低出借
		holder.tv_invest_money.setText("最低出借   "+hongBaoBean.getUseCondition()+"元");

		//适用产品
		holder.tv_hongbao_info.setText("适用范围   "+hongBaoBean.getSuitProduct());

		//获得来源
		holder.tv_hongbao_method.setText("获得来源   "+hongBaoBean.getFromSource());

		//特别说明
		holder.tv_hongbao_explain.setText("特别说明   "+hongBaoBean.getSpecialDesc());

		//结束时间   2已使用、3已过期
		if ("2".equals(hongBaoBean.getStatus())) {
			//红包使用时间
			holder.tv_hongbao_usetime.setText("使用时间   "+hongBaoBean.getUseTime());
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_used);
		}else if ("3".equals(hongBaoBean.getStatus())) {
			//红包失效时间
			holder.tv_hongbao_usetime.setText("失效时间   "+hongBaoBean.getExpiredTime());
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_passed);
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
		ImageView iv_unused;
	}
}
