package com.iqianbang.fanpai.adapter.yue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.ShouYiBean;
import com.iqianbang.fanpai.utils.MathUtil;

import java.util.ArrayList;

public class JiangJinAdapter extends BaseAdapter {

	private ArrayList<ShouYiBean> list;
	private Context mContext;
	public JiangJinAdapter(Context context, ArrayList<ShouYiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public JiangJinAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_jiangjin, null);
			holder.jiang_name = (TextView) convertView.findViewById(R.id.jiang_name);
			holder.jiang_time = (TextView) convertView.findViewById(R.id.jiang_time);
			holder.jiang_money = (TextView) convertView.findViewById(R.id.jiang_money);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		ShouYiBean shouYiBean = list.get(position);
//		401：邀请奖金，451：使用奖金
		//左侧小图片
//		ImageLoader.getInstance().displayImage(shouYiBean.getIconUrl(), holder.jiang_name);
		//时间
		holder.jiang_time.setText(shouYiBean.getTransTime());
		if (shouYiBean.getAmount()>=0) {
			holder.jiang_name.setText("奖金发放（元）");
			holder.jiang_money.setText("+" + MathUtil.subDouble(shouYiBean.getAmount(), 2) + "");
		}else {
			holder.jiang_name.setText("奖金使用（元）");
			holder.jiang_money.setText(MathUtil.subDouble(shouYiBean.getAmount(), 2) + "");
		}

		return convertView;
	}

	class NoticeHolder{
		TextView jiang_name;
		TextView jiang_time;
		TextView jiang_money;
	}
}
