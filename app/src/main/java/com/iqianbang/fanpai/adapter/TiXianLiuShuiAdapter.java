package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.TiXianLiuShuiBean;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class TiXianLiuShuiAdapter extends BaseAdapter implements StickyListHeadersAdapter {

	private List<TiXianLiuShuiBean> list;
	private Context mContext;
	private int clickPosition = -1;

	public TiXianLiuShuiAdapter(Context context, List<TiXianLiuShuiBean> list) {
		super();
		this.mContext = context;
		this.list = list;
	}

	public TiXianLiuShuiAdapter(Context context) {
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
				TiXianHolder holder = null;
				if (convertView == null) {
					holder = new TiXianHolder();
					convertView = View.inflate(mContext, R.layout.items_ti_xian_liu_shui, null);
					holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
					holder.iv_banklogo = (ImageView) convertView.findViewById(R.id.iv_banklogo);
					holder.tv_bankname = (TextView) convertView.findViewById(R.id.tv_bankname);
					holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
					holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
					holder.tv_fee = (TextView) convertView.findViewById(R.id.tv_fee);
					holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);


					holder.ll_hide = (LinearLayout) convertView.findViewById(R.id.ll_hide);
					holder.iv_jindu = (ImageView) convertView.findViewById(R.id.iv_jindu);
					holder.tv_third = (TextView) convertView.findViewById(R.id.tv_third);
					holder.tv_bank = (TextView) convertView.findViewById(R.id.tv_bank);
					holder.tv_time1 = (TextView) convertView.findViewById(R.id.tv_time1);
					holder.tv_time2 = (TextView) convertView.findViewById(R.id.tv_time2);
					holder.tv_time3 = (TextView) convertView.findViewById(R.id.tv_time3);

					convertView.setTag(holder);
				} else {
					holder = (TiXianHolder) convertView.getTag();
				}

				TiXianLiuShuiBean tiXianLiuShuiBean = list.get(position);

				//左侧银行logo
				ImageLoader.getInstance().displayImage(tiXianLiuShuiBean.getBankPic(),holder.iv_banklogo);
				//银行名称
				holder.tv_bankname.setText(tiXianLiuShuiBean.getBankName()+"（"+tiXianLiuShuiBean.getCardNum()+"）");
				//提现申请时间
				String[] split = tiXianLiuShuiBean.getTransTime().split(" ");
				holder.tv_time.setText(split[0]);
				//提现金额
				holder.tv_money.setText(tiXianLiuShuiBean.getAmount()+"元");
		;
				//提现手续费
				holder.tv_fee.setText("手续费："+tiXianLiuShuiBean.getFee()+"元");
				//提现状态:"0"申请中  "1"申请成功
				if("0".equals(tiXianLiuShuiBean.getStatus())){
					holder.iv_status.setImageResource(R.drawable.tixianliushui_ing_status);
					holder.iv_jindu.setImageResource(R.drawable.tixianliushui_ing_jindu);
					holder.tv_third.setText("第三方支付处理中");
					holder.tv_bank.setText("银行未处理");
				}else if("1".equals(tiXianLiuShuiBean.getStatus())){
					holder.iv_status.setImageResource(R.drawable.tixianliushui_ok_status);
					holder.iv_jindu.setImageResource(R.drawable.tixianliushui_ok_jindu);
					holder.tv_third.setText("第三方支付处理完成");
					holder.tv_bank.setText("银行处理完成");
				}
				//提现时间
				holder.tv_time1.setText(tiXianLiuShuiBean.getTransTime());
				holder.tv_time2.setText(tiXianLiuShuiBean.getThirdTime());
				holder.tv_time3.setText(tiXianLiuShuiBean.getAccountTime());


				if(clickPosition == position){
					if (holder.ll_hide.getVisibility() == View.VISIBLE) {
						holder.ll_hide.setVisibility(View.GONE);
						clickPosition=-1;
					}else{
						holder.ll_hide.setVisibility(View.VISIBLE);
					}
				}else{
					holder.ll_hide.setVisibility(View.GONE);
				}

				holder.ll_item.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						clickPosition = position;
						notifyDataSetChanged();
					}
				});
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		TiXianHeaderHolder holder;
		if (convertView == null) {
			holder = new TiXianHeaderHolder();
			convertView = View.inflate(mContext,R.layout.items_ti_xian_liu_shui_title, null);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (TiXianHeaderHolder) convertView.getTag();
		}

		TiXianLiuShuiBean tiXianLiuShuiBean = list.get(position);
		holder.tv_title.setText(tiXianLiuShuiBean.getTransMonth());
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		TiXianLiuShuiBean tiXianLiuShuiBean = list.get(position);
		Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher mat = pat.matcher(tiXianLiuShuiBean.getTransMonth());
		String repickStr = mat.replaceAll("");
		return Long.valueOf(repickStr);
	}

	class TiXianHeaderHolder {
		TextView tv_title;
	}

	class TiXianHolder {
		LinearLayout ll_item;//点击条目展开或隐藏
		ImageView iv_banklogo;
		TextView tv_bankname;
		TextView tv_time;
		TextView tv_money;
		TextView tv_fee;
		ImageView iv_status;


		LinearLayout ll_hide;
		ImageView iv_jindu;
		TextView tv_third;
		TextView tv_bank;
		TextView tv_time1;
		TextView tv_time2;
		TextView tv_time3;

	}
}
