package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.SanBiaoListBean;
import com.iqianbang.fanpai.bean.ZhuanRangBean;

import java.util.ArrayList;

public class ZhuanRangJieQingAdapter extends BaseAdapter {

	private ArrayList<ZhuanRangBean> list;
	private Context mContext;
	public ZhuanRangJieQingAdapter(Context context, ArrayList<ZhuanRangBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_zhuanrang_jieqing, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_time_chujie = (TextView) convertView.findViewById(R.id.tv_time_chujie);
			holder.tv_rate = (TextView) convertView.findViewById(R.id.tv_rate);
			holder.tv_time_daoqi = (TextView) convertView.findViewById(R.id.tv_time_daoqi);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		
		final ZhuanRangBean zhuanRangBean = list.get(position);
		holder.tv_name.setText(zhuanRangBean.getTransferName());//投资记录名称
		holder.tv_money.setText("出借金额：" + zhuanRangBean.getInvestAmount()+"元");//出借金额
		holder.tv_time_chujie.setText("出借时间：" + zhuanRangBean.getStartTimeStr());//出借时间
		holder.tv_rate.setText("历史年化：" + zhuanRangBean.getYearRate()+"%");//预期结算利率
		holder.tv_time_daoqi.setText("结清时间：" + zhuanRangBean.getSettlementTimeStr());//结清时间

		if("50".equals(zhuanRangBean.getStatus())){// 40 已结清   50 承接结清   60 提前还款
			holder.iv_status.setImageResource(R.drawable.zaitou_jieqing_zhuanrang);
		}
		if("60".equals(zhuanRangBean.getStatus())){// 40 已结清   50 承接结清   60 提前还款
			holder.iv_status.setImageResource(R.drawable.zaitou_jieqing_tiqian);
		}

		return convertView;
	}

	class NoticeHolder{
		TextView tv_name;//投资记录名称
		ImageView iv_status;//状态图标
		TextView tv_money;//出借金额
		TextView tv_time_chujie;//出借时间
		TextView tv_rate;//预期结算利率
		TextView tv_time_daoqi;//结清时间
	}
}
