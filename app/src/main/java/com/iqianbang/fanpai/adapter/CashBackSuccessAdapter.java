package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.CashBackListActivity;
import com.iqianbang.fanpai.adapter.zhanneixin.ZhanNeiXinAdapter;
import com.iqianbang.fanpai.bean.FtMinCashListBean;
import com.iqianbang.fanpai.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CashBackSuccessAdapter extends BaseAdapter {

	private ArrayList<FtMinCashListBean> list;
	private Context mContext;

	public CashBackSuccessAdapter(Context context) {
		super();
		this.mContext = context;
	}

	public CashBackSuccessAdapter(Context context, ArrayList<FtMinCashListBean> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		GuanFangHolder holder = null;
		if (convertView == null) {
			holder = new GuanFangHolder();
			convertView = View.inflate(mContext, R.layout.items_cashsuccess, null);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			convertView.setTag(holder);
		}else {
			holder = (GuanFangHolder) convertView.getTag();
		}
		FtMinCashListBean ftMinCashListBean = list.get(position);
		holder.tv_time.setText(ftMinCashListBean.getTransTimeStr());
		holder.tv_money.setText(ftMinCashListBean.getAmount());

		return convertView;
	}

	class GuanFangHolder{
		TextView tv_time;
		TextView tv_money;
	}
}
