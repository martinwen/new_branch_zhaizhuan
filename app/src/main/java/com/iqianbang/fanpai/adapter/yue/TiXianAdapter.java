package com.iqianbang.fanpai.adapter.yue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.ShouYiBean;
import com.iqianbang.fanpai.utils.MathUtil;

import java.util.ArrayList;

import static com.iqianbang.fanpai.R.id.iv_allmoney;

public class TiXianAdapter extends BaseAdapter {

	private ArrayList<ShouYiBean> list;
	private Context mContext;

	public TiXianAdapter(Context context, ArrayList<ShouYiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}

	public TiXianAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_yue, null);
			holder.iv_allmoney = (ImageView) convertView.findViewById(iv_allmoney);//图标
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);//标题
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);//时间
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);//金额
			
			holder.fl_info = (FrameLayout) convertView.findViewById(R.id.fl_info);
			holder.tv_chuli = (TextView) convertView.findViewById(R.id.tv_chuli);//处理中
			holder.rl_info = (RelativeLayout) convertView.findViewById(R.id.rl_info);//到帐xxx元，手续费xxx元
			holder.tv_detail1 = (TextView) convertView.findViewById(R.id.tv_detail1);
			holder.tv_detail2 = (TextView) convertView.findViewById(R.id.tv_detail2);
			holder.tv_detail3 = (TextView) convertView.findViewById(R.id.tv_detail3);
			holder.tv_detail4 = (TextView) convertView.findViewById(R.id.tv_detail4);
			holder.tv_detail5 = (TextView) convertView.findViewById(R.id.tv_detail5);
			
			convertView.setTag(holder);
		} else {
			holder = (NoticeHolder) convertView.getTag();
		}

		ShouYiBean shouYiBean = list.get(position);
		
			//是提现类型，需要展示状态，提现金额显示为蓝色
			holder.fl_info.setVisibility(View.VISIBLE);
			holder.tv_money.setText(MathUtil.subDouble(shouYiBean.getAmount(), 2) + "");
			//状态为“0”，处理中
			if ("0".equals(shouYiBean.getStatus())) {
				holder.rl_info.setVisibility(View.INVISIBLE);
				holder.tv_chuli.setVisibility(View.VISIBLE);
				//状态为“1”，就是处理完成
			}else if ("1".equals(shouYiBean.getStatus())) {
				holder.rl_info.setVisibility(View.VISIBLE);
				holder.tv_chuli.setVisibility(View.INVISIBLE);
				holder.tv_detail1.setText("到账");
				holder.tv_detail2.setText((MathUtil.subDouble(-(shouYiBean.getAmount() - shouYiBean.getFee()), 2))+"");
				holder.tv_detail3.setText("元，手续费");
				holder.tv_detail4.setText((MathUtil.subDouble(-shouYiBean.getFee(), 2))+"");
				holder.tv_detail5.setText("元");
			}

			//左侧小图片
//			ImageLoader.getInstance().displayImage(shouYiBean.getIconUrl(),holder.iv_allmoney);
			//标题
//			ImageLoader.getInstance().displayImage(shouYiBean.getTransName(),holder.tv_title);
			holder.tv_title.setText(shouYiBean.getTransName());
			//时间
			holder.tv_time.setText(shouYiBean.getTransTime());
			
			return convertView;
	}

	class NoticeHolder {
		ImageView iv_allmoney;
		TextView tv_title;
		TextView tv_time;
		TextView tv_money;
		RelativeLayout rl_info;
		
		TextView tv_detail1;
		TextView tv_detail2;
		TextView tv_detail3;
		TextView tv_detail4;
		TextView tv_detail5;
		
		FrameLayout fl_info;
		TextView tv_chuli;

	}
}
