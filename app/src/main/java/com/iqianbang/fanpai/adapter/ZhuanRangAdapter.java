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

public class ZhuanRangAdapter extends BaseAdapter {

	private ArrayList<ZhuanRangBean> list;
	private Context mContext;
	public ZhuanRangAdapter(Context context, ArrayList<ZhuanRangBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_zhuanrang, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
			holder.tv_zhuanrang = (TextView) convertView.findViewById(R.id.tv_zhuanrang);
			holder.tv_huikuan = (TextView) convertView.findViewById(R.id.tv_huikuan);
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
		holder.tv_time_daoqi.setText("到期时间：" + zhuanRangBean.getEndTimeStr());//到期时间

		if("10".equals(zhuanRangBean.getStatus())){
			holder.iv_status.setImageResource(R.drawable.zaitou_shouyizhong);
			if(zhuanRangBean.isProCanTransfer()){//true | false  true 允许转让 ，false不允许转让
				holder.tv_zhuanrang.setVisibility(View.VISIBLE);
				holder.tv_zhuanrang.setText("*【可转让】");
			}else{
				holder.tv_zhuanrang.setVisibility(View.GONE);
			}

			if(zhuanRangBean.getBookClearAmount() > 0){//回款金额 >0 代表有回款
				holder.tv_huikuan.setVisibility(View.VISIBLE);
			}else {
				holder.tv_huikuan.setVisibility(View.GONE);
			}
		}

		if("20".equals(zhuanRangBean.getStatus())){//10  持有中  20 转让中  30 已转让 40 已结清   50 承接结清
			holder.iv_status.setImageResource(R.drawable.zaitou_shouyizhong);
			holder.tv_zhuanrang.setVisibility(View.VISIBLE);
			holder.tv_zhuanrang.setText("*【转让中】");
			if(zhuanRangBean.getBookClearAmount() > 0){//回款金额 >0 代表有回款
				holder.tv_huikuan.setVisibility(View.VISIBLE);
			}else {
				holder.tv_huikuan.setVisibility(View.GONE);
			}
		}

		if("30".equals(zhuanRangBean.getStatus())){//10  持有中  20 转让中  30 已转让 40 已结清   50 承接结清
			holder.iv_status.setImageResource(R.drawable.zaitou_yizhuanrang);
			holder.tv_zhuanrang.setVisibility(View.GONE);
			holder.tv_time_daoqi.setText("转让成功：" + zhuanRangBean.getTransferSuccessTimeStr());//转让成功
		}

		return convertView;
	}

	class NoticeHolder{
		TextView tv_name;//投资记录名称
		ImageView iv_status;//状态图标
		TextView tv_zhuanrang;//可转让
		TextView tv_huikuan;//已部分回款
		TextView tv_money;//出借金额
		TextView tv_time_chujie;//出借时间
		TextView tv_rate;//预期结算利率
		TextView tv_time_daoqi;//到期时间
	}
}
