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
import com.iqianbang.fanpai.view.dialog.CashBackCheXiaoDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class CashBackListAdapter extends BaseAdapter {

	// 用来控制CheckBox的选中状况
	private static HashMap<Integer,Boolean> isSelected;
	private ArrayList<FtMinCashListBean> list;
	private Context mContext;

	private ZhanNeiXinAdapter.SubClickListener subClickListener;
	public void setsubClickListener(ZhanNeiXinAdapter.SubClickListener topicClickListener) {
		this.subClickListener = topicClickListener;
	}

	public interface SubClickListener {
		void OntopicClickListener();
	}

	public CashBackListAdapter(Context context) {
		super();
		this.mContext = context;
	}

	public CashBackListAdapter(Context context, ArrayList<FtMinCashListBean> list) {
		super();
		this.mContext = context;
		this.list = list;
		isSelected = new HashMap<>();
	}

	public void setList(ArrayList<FtMinCashListBean> list) {
		for (int i=0;i<list.size();i++){
			if(null!=isSelected.get(i)&&isSelected.get(i)){
				isSelected.put(i,true);
			}else{
				isSelected.put(i,false);
			}

		}
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
			convertView = View.inflate(mContext, R.layout.items_cashlist, null);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_chexiao = (TextView) convertView.findViewById(R.id.tv_chexiao);
			holder.item_cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			
			convertView.setTag(holder);
		}else {
			holder = (GuanFangHolder) convertView.getTag();
		}
		final FtMinCashListBean ftMinCashListBean = list.get(position);

		if(CashBackListActivity.isCashBackOne){
			holder.tv_chexiao.setVisibility(View.VISIBLE);
			holder.item_cb.setVisibility(View.GONE);
		}else {
			holder.tv_chexiao.setVisibility(View.GONE);
			holder.item_cb.setVisibility(View.VISIBLE);
		}

		holder.tv_time.setText(ftMinCashListBean.getTransTimeStr());
		holder.tv_money.setText(ftMinCashListBean.getAmount());
//		holder.tv_chexiao.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				CashBackCheXiaoDialog cashBackCheXiaoDialog = new CashBackCheXiaoDialog(mContext, R.style.YzmDialog,
//						ftMinCashListBean.getId(),"撤销赎回金额为" + ftMinCashListBean.getAmount() + "元\n您确定要撤销赎回申请吗？");
//				cashBackCheXiaoDialog.show();
//			}
//		});

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
		CashBackListAdapter.isSelected = isSelected;
	}

	class GuanFangHolder{
		TextView tv_time;
		TextView tv_money;
		TextView tv_chexiao;
		CheckBox item_cb;
	}
}
