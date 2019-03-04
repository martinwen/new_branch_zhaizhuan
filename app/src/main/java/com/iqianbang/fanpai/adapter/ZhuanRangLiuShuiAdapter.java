package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.ShouYiBean;
import com.iqianbang.fanpai.utils.MathUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ZhuanRangLiuShuiAdapter extends BaseAdapter {

	private ArrayList<ShouYiBean> list;
	private Context mContext;

	public ZhuanRangLiuShuiAdapter(Context context, ArrayList<ShouYiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}

	public ZhuanRangLiuShuiAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_adapter_zhuanrang, null);
			holder.iv_allmoney = (ImageView) convertView.findViewById(R.id.iv_allmoney);//图标
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);//标题
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);//时间
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);//金额
			
			holder.tv_detail = (TextView) convertView.findViewById(R.id.tv_detail);
			convertView.setTag(holder);
		} else {
			holder = (NoticeHolder) convertView.getTag();
		}

		ShouYiBean shouYiBean = list.get(position);
		
		if ("801".equals(shouYiBean.getTransType())){
			holder.tv_detail.setVisibility(View.VISIBLE);
			holder.tv_detail.setText("现金" + MathUtil.subDouble((shouYiBean.getActualAmount() - shouYiBean.getFee()), 2)
					+ "元  奖金" + MathUtil.subDouble(shouYiBean.getFee(), 2)+ "元");
		}else if("852".equals(shouYiBean.getTransType())){
			holder.tv_detail.setVisibility(View.VISIBLE);
			holder.tv_detail.setText("回款" + MathUtil.subDouble(shouYiBean.getActualAmount(), 2)
					+ "元  手续费" + MathUtil.subDouble(shouYiBean.getFee(), 2)+ "元");
		}
		else {
			//其他类型，金额显示为主颜色
			holder.tv_detail.setVisibility(View.GONE);
		}
		//左侧小图片
		ImageLoader.getInstance().displayImage(shouYiBean.getIconUrl(),holder.iv_allmoney);

		holder.tv_money.setText(MathUtil.subDouble(shouYiBean.getAmount(), 2) + "");

		holder.tv_title.setText(shouYiBean.getTransName());
		//时间
		holder.tv_time.setText(shouYiBean.getTransTime());

		return convertView;
	}

	class NoticeHolder {
		ImageView iv_allmoney;
		TextView tv_title;
		TextView tv_time;
		TextView tv_money;

		TextView tv_detail;
	}
}
