package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.NoticeBean;

import java.util.ArrayList;


public class NoticeAdapter extends BaseAdapter {

	private ArrayList<NoticeBean> list;
	private Context mContext;
	public NoticeAdapter(Context context, ArrayList<NoticeBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}
	public NoticeAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_notice, null);
			holder.notice_name = (ImageView) convertView.findViewById(R.id.notice_name);
			holder.notice_time = (TextView) convertView.findViewById(R.id.notice_time);
			holder.notice_title = (TextView) convertView.findViewById(R.id.notice_title);
			
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		NoticeBean noticeBean = list.get(position);
		holder.notice_time.setText(noticeBean.getAddTimeStr());
		holder.notice_title.setText(noticeBean.getTitle());
		if ("1".equals(noticeBean.getIsRead())) {//已读
			holder.notice_name.setImageResource(R.drawable.message_read);
			holder.notice_time.setTextColor(Color.parseColor("#888888"));
			holder.notice_title.setTextColor(Color.parseColor("#888888"));
		}else {
			holder.notice_name.setImageResource(R.drawable.message_unread);
			holder.notice_time.setTextColor(Color.parseColor("#ffffff"));
			holder.notice_title.setTextColor(Color.parseColor("#ffffff"));
		}
		
		return convertView;
	}

	class NoticeHolder{
		ImageView notice_name;
		TextView notice_time;
		TextView notice_title;
	}
}
