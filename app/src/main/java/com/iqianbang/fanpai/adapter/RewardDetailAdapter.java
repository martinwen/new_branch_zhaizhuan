package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.InviteDetailBean;

import java.util.ArrayList;


public class RewardDetailAdapter extends BaseAdapter {

	private ArrayList<InviteDetailBean> list;
	private Context mContext;

	public RewardDetailAdapter(Context context, ArrayList<InviteDetailBean> list) {
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
			convertView = View.inflate(mContext, R.layout.items_rewarddetail, null);
			holder.reward_number = (TextView) convertView.findViewById(R.id.reward_number);
			holder.reward_is = (TextView) convertView.findViewById(R.id.reward_is);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		InviteDetailBean inviteDetailBean = list.get(position);
		String phone = inviteDetailBean.getPhone();
		holder.reward_number.setText(fixNum(phone));
		holder.reward_is.setText(inviteDetailBean.getIsInvested());
		return convertView;
	}

	class NoticeHolder{
		TextView reward_number;
		TextView reward_is;
	}

	//手机号码星号处理
	private String fixNum(String phone) {
		return phone.substring(0, 3) + "****" + phone.substring(7);
	}
}
