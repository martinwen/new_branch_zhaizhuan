package com.iqianbang.fanpai.adapter.youxiangou;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.YouXianGouBean;

import java.util.ArrayList;


public class YouXianGouUnuseableAdapter extends BaseAdapter {

	private ArrayList<YouXianGouBean> list;
	private Context mContext;

	public YouXianGouUnuseableAdapter(Context context, ArrayList<YouXianGouBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public YouXianGouUnuseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_youxiangou_unusable, null);
			holder.tv_hongbao_name = (TextView) convertView.findViewById(R.id.tv_hongbao_name);
			holder.tv_hongbao_usetime = (TextView) convertView.findViewById(R.id.tv_hongbao_usetime);
			holder.tv_invest_money = (TextView) convertView.findViewById(R.id.tv_invest_money);
			holder.tv_hongbao_info = (TextView) convertView.findViewById(R.id.tv_hongbao_info);
			holder.tv_hongbao_method = (TextView) convertView.findViewById(R.id.tv_hongbao_method);
			holder.iv_unused = (ImageView) convertView.findViewById(R.id.iv_unused);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		YouXianGouBean youXianGouBean = list.get(position);

		//最低出借
		holder.tv_invest_money.setText("优先购额度   "+youXianGouBean.getAmount()+"元");

		//适用产品
		holder.tv_hongbao_info.setText("适用产品   "+youXianGouBean.getProductName());

		//获得来源
		holder.tv_hongbao_method.setText("获得来源   "+youXianGouBean.getFrom_source());

		//结束时间   2已使用、3已过期
		if ("2".equals(youXianGouBean.getStatus())) {
			//红包使用时间
			holder.tv_hongbao_usetime.setText("使用时间   "+youXianGouBean.getUseTime());
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_used);

			//把leftDrawable图片置为使用时间
			holder.tv_hongbao_usetime.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.hongbao_usetime),null,null,null);
		}else if ("3".equals(youXianGouBean.getStatus())) {
			//红包失效时间
			holder.tv_hongbao_usetime.setText("失效时间   "+youXianGouBean.getExpiredTime());
			holder.iv_unused.setImageResource(R.drawable.jiaxipiao_btn_passed);

			//把leftDrawable图片置为失效时间
			holder.tv_hongbao_usetime.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.hongbao_shixiao),null,null,null);
		}
		
		return convertView;
	}


	class NoticeHolder{
		TextView tv_hongbao_name;
		TextView tv_hongbao_usetime;
		TextView tv_invest_money;
		TextView tv_hongbao_info;
		TextView tv_hongbao_method;
		ImageView iv_unused;
	}
}
