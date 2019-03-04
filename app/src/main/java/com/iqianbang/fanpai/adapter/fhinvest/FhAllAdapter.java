package com.iqianbang.fanpai.adapter.fhinvest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.ShouYiBean;
import com.iqianbang.fanpai.utils.MathUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class FhAllAdapter extends BaseAdapter {

	private ArrayList<ShouYiBean> list;
	private Context mContext;

	public FhAllAdapter(Context context, ArrayList<ShouYiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}

	public FhAllAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_fh, null);
			holder.iv_allmoney = (ImageView) convertView.findViewById(R.id.iv_allmoney);//图标
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);//标题
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);//时间
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);//金额
			
			holder.fl_info = (FrameLayout) convertView.findViewById(R.id.fl_info);
			holder.tv_chuli = (TextView) convertView.findViewById(R.id.tv_chuli);//处理中
			holder.rl_info = (RelativeLayout) convertView.findViewById(R.id.rl_info);//到帐xxx元，手续费xxx元
			holder.tv_detail1 = (TextView) convertView.findViewById(R.id.tv_detail1);
			holder.tv_detail2 = (TextView) convertView.findViewById(R.id.tv_detail2);
			holder.tv_detail3 = (TextView) convertView.findViewById(R.id.tv_detail3);
			holder.tv_detail4 = (TextView) convertView.findViewById(R.id.tv_detail4);
			holder.tv_detail5 = (TextView) convertView.findViewById(R.id.tv_detail5);
			
			convertView.setTag(holder);
		} else {
			holder = (NoticeHolder) convertView.getTag();
		}

		ShouYiBean shouYiBean = list.get(position);
		//201指饭盒，501饭桶
		if ("201".equals(shouYiBean.getTransType())||"501".equals(shouYiBean.getTransType())){
			holder.fl_info.setVisibility(View.VISIBLE);
			holder.rl_info.setVisibility(View.VISIBLE);
			holder.tv_chuli.setVisibility(View.INVISIBLE);
			holder.tv_detail1.setText("现金");
			holder.tv_detail2.setText((MathUtil.subDouble((shouYiBean.getAmount() - shouYiBean.getFee()), 2))+"");
			holder.tv_detail3.setText("元  奖金");
			holder.tv_detail4.setText((MathUtil.subDouble(shouYiBean.getFee(), 2))+"");
			holder.tv_detail5.setText("元");
		}else {
			//其他类型，金额显示为主颜色
			holder.fl_info.setVisibility(View.GONE);
		}
		//左侧小图片
		ImageLoader.getInstance().displayImage(shouYiBean.getIconUrl(),holder.iv_allmoney);
		//右侧金额
		holder.tv_money.setText(""+MathUtil.subDouble(shouYiBean.getAmount(), 2));

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
		RelativeLayout rl_info;
		
		TextView tv_detail1;
		TextView tv_detail2;
		TextView tv_detail3;
		TextView tv_detail4;
		TextView tv_detail5;
		
		FrameLayout fl_info;
		TextView tv_chuli;

	}
}
