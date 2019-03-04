package com.iqianbang.fanpai.adapter.zhanneixin;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.ZhanNeiXinBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ZhanNeiXinAdapter extends BaseAdapter {

	// 用来控制CheckBox的选中状况
	private static HashMap<Integer,Boolean> isSelected;
	private ArrayList<ZhanNeiXinBean> list;
	private Context mContext;
	private SubClickListener subClickListener;
	public void setsubClickListener(SubClickListener topicClickListener) {
		this.subClickListener = topicClickListener;
	}

	public interface SubClickListener {
		void OntopicClickListener();
	}
	public ZhanNeiXinAdapter(Context context, ArrayList<ZhanNeiXinBean> list) {
		super();
		this.mContext = context;
		this.list = list;
		isSelected = new HashMap<>();
	}

	public void setList(ArrayList<ZhanNeiXinBean> list, boolean selectAll) {
		for (int i=0;i<list.size();i++){
			if(selectAll){
				isSelected.put(i,true);
			}else {
				if(null!=isSelected.get(i)&&isSelected.get(i)){
					isSelected.put(i,true);
				}else{
					isSelected.put(i,false);
				}
			}

		}
		this.list = list;
	}

	public ZhanNeiXinAdapter(Context context) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		GuanFangHolder holder = null;
		if (convertView == null) {
			holder = new GuanFangHolder();
			convertView = View.inflate(mContext, R.layout.items_zhanneixin, null);
			holder.notice_name = (TextView) convertView.findViewById(R.id.notice_name);
			holder.notice_point = (ImageView) convertView.findViewById(R.id.notice_point);
			holder.notice_time = (TextView) convertView.findViewById(R.id.notice_time);
			holder.notice_title = (TextView) convertView.findViewById(R.id.notice_title);
			holder.item_cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			
			convertView.setTag(holder);
		}else {
			holder = (GuanFangHolder) convertView.getTag();
		}
		ZhanNeiXinBean zhanNeiXinBean = list.get(position);

		holder.notice_name.setText("【"+zhanNeiXinBean.getTitle()+"】");
		holder.notice_time.setText(zhanNeiXinBean.getAddTime());
		holder.notice_title.setText(zhanNeiXinBean.getContent());
		if ("1".equals(zhanNeiXinBean.getStatus())) {//未读
			holder.notice_point.setVisibility(View.VISIBLE);
			holder.notice_time.setTextColor(Color.parseColor("#ffffff"));
			holder.notice_title.setTextColor(Color.parseColor("#ffffff"));
			holder.notice_name.setTextColor(Color.parseColor("#ffffff"));
		}else {//已读
			holder.notice_point.setVisibility(View.INVISIBLE);
			holder.notice_time.setTextColor(Color.parseColor("#868686"));
			holder.notice_title.setTextColor(Color.parseColor("#868686"));
			holder.notice_name.setTextColor(Color.parseColor("#868686"));
		}

		// 根据isSelected来设置checkbox的选中状况
		holder.item_cb.setOnCheckedChangeListener(null);

		if(getIsSelected().size()>0){
			holder.item_cb.setChecked(getIsSelected().get(position));
		}

		holder.item_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isSelected.get(position)){
					isSelected.put(position, false);
				}else{
					isSelected.put(position, true);
				}
				if (null!=subClickListener) {
					subClickListener.OntopicClickListener();
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	public static HashMap<Integer,Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
		ZhanNeiXinAdapter.isSelected = isSelected;
	}

	class GuanFangHolder{
		TextView notice_name;
		ImageView notice_point;
		TextView notice_time;
		TextView notice_title;
		CheckBox item_cb;
	}
}
