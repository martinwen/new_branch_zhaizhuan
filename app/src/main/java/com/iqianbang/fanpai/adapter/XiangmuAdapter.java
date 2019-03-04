package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.XiangMuBean;

import java.util.ArrayList;


public class XiangmuAdapter extends BaseAdapter {

	private ArrayList<XiangMuBean> list;
	private Context mContext;
	public XiangmuAdapter(Context context, ArrayList<XiangMuBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_xiangmu, null);
			holder.tv_bianhao = (TextView) convertView.findViewById(R.id.tv_bianhao);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		XiangMuBean xiangMuBean = list.get(position);
		holder.tv_bianhao.setText("项目编号："+xiangMuBean.getBorrowCode());
		holder.tv_money.setText("出借金额（元）："+xiangMuBean.getBorrowMoney()+"");

		return convertView;
	}

	class NoticeHolder{
		TextView tv_bianhao;
		TextView tv_money;

	}
}
