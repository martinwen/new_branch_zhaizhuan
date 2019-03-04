package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.FuTouListActivity;
import com.iqianbang.fanpai.bean.FWBean;
import com.iqianbang.fanpai.view.dialog.IKnowDialog;
import com.iqianbang.fanpai.view.dialog.ZhuanRangDialog;

import java.util.ArrayList;

public class FanWanAdapter extends BaseAdapter {

	private ArrayList<FWBean> list;
	private Context mContext;
	public FanWanAdapter(Context context, ArrayList<FWBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_fanwan, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//			holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.tv_days = (TextView) convertView.findViewById(R.id.tv_days);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_rate = (TextView) convertView.findViewById(R.id.tv_rate);
			holder.tv_leiji = (TextView) convertView.findViewById(R.id.tv_leiji);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		
		final FWBean fwBean = list.get(position);
		holder.tv_name.setText(fwBean.getCollectionName());//投资记录名称
		holder.tv_time.setText("出借时间：" + fwBean.getTransTime());//出借时间
		holder.tv_money.setText("出借金额：" + fwBean.getInvestAmount()+"元");//出借金额
		holder.tv_rate.setText("历史年化：" + fwBean.getYearRate()+"%");//预期结算利率
		holder.tv_leiji.setText("累计收益：" + fwBean.getAccumulativeIncome()+"元");//累计收益

		//10持有中、20转让中、30转让成功、40已结清
		String status = fwBean.getStatus();
		if ("10".equals(status)) {//10持有中
			holder.tv_days.setText("* 锁定期至" + fwBean.getLockExpireTime());
//			if("1".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮
//				holder.tv_status.setVisibility(View.VISIBLE);
//				holder.tv_status.setText("预约复投");
//				holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.global_whitecolor));
//				holder.tv_status.setEnabled(true);
//				holder.tv_status.setBackgroundResource(R.drawable.shape_bg_btn_red_zhuanrang);
//			}
//
//			if("2".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮 2、 无任何标记 3、展示已预约复投
//				holder.tv_status.setVisibility(View.GONE);
//			}
//
//			if("3".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮 2、 无任何标记 3、展示已预约复投
//				holder.tv_status.setVisibility(View.VISIBLE);
//				holder.tv_status.setText("已预约复投");
//				holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
//				holder.tv_status.setEnabled(false);
//				holder.tv_status.setBackgroundResource(R.drawable.shape_bg_btn_gray_zhuanrang);
//			}
		}

		if ("20".equals(status)) {//20转让中
			holder.tv_days.setText("* 项目转让中");
//			if("1".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮
//				holder.tv_status.setVisibility(View.VISIBLE);
//				holder.tv_status.setText("立即复投");
//				holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.global_whitecolor));
//				holder.tv_status.setEnabled(true);
//				holder.tv_status.setBackgroundResource(R.drawable.shape_bg_btn_red_zhuanrang);
//			}
//
//			if("2".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮 2、 无任何标记 3、展示已预约复投
//				holder.tv_status.setVisibility(View.GONE);
//			}
//
//			if("3".equals(fwBean.getShowBookType())){//预约类型  1、展示复投按钮 2、 无任何标记 3、展示已预约复投
//				holder.tv_status.setVisibility(View.VISIBLE);
//				holder.tv_status.setText("已预约复投");
//				holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.text_red_dark));
//				holder.tv_status.setEnabled(false);
//				holder.tv_status.setBackgroundResource(R.drawable.shape_bg_btn_gray_zhuanrang);
//			}
		}



//		holder.tv_status.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(mContext, FuTouListActivity.class);
//				intent.putExtra("dftId",fwBean.getId());
//				mContext.startActivity(intent);
//			}
//		});
		return convertView;
	}

	class NoticeHolder{
		TextView tv_name;//投资记录名称
//		TextView tv_status;//转让按钮
		TextView tv_days;//（锁定期至xxx）
		TextView tv_time;//出借时间
		TextView tv_money;//出借金额
		TextView tv_rate;//预期结算利率
		TextView tv_leiji;//累计收益
	}
}
