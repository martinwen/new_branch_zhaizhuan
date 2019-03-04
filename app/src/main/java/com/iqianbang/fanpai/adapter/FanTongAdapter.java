package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.bean.FTBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.BindCardDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.FanTongDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;

public class FanTongAdapter extends BaseAdapter {

	private CustomProgressDialog progressdialog;
	private ArrayList<FTBean> list;
	private Context mContext;

	public FanTongAdapter(Context context, ArrayList<FTBean> list) {
		super();
		this.mContext = context;
		this.list = list;
		progressdialog = new CustomProgressDialog(mContext, "正在加载数据...");
	}


	public FanTongAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_fantong, null);
			holder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);//项目编号
			holder.tv_in_value = (TextView) convertView.findViewById(R.id.tv_in_value);//买入时间
			holder.tv_rate_value = (TextView) convertView.findViewById(R.id.tv_rate_value); //预期年化率
			holder.tv_out_value = (TextView) convertView.findViewById(R.id.tv_out_value); //已赚收益值（或赎回时间值）
			holder.tv_invest_value = (TextView) convertView.findViewById(R.id.tv_invest_value); //出借金额
			holder.tv_day_value = (TextView) convertView.findViewById(R.id.tv_day_value); //已投天数
			holder.iv_cash = (ImageView) convertView.findViewById(R.id.iv_cash); //赎回按钮
			holder.rl_over = (RelativeLayout) convertView.findViewById(R.id.rl_over); //已赎回才显示的赎回内容
			holder.tv_over = (TextView) convertView.findViewById(R.id.tv_over); //赎回手续费
			holder.tv_over_value = (TextView) convertView.findViewById(R.id.tv_over_value); //赎回手续费值
			holder.iv_isover = (ImageView) convertView.findViewById(R.id.iv_isover); //已赎回图案
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}

		final FTBean ftBean = list.get(position);
		holder.tv_code.setText(ftBean.getPro_code());//项目编号
		holder.tv_in_value.setText(ftBean.getTrans_time());//买入时间
		holder.tv_rate_value.setText(ftBean.getYear_rate()+"%");//预期年化率
		holder.tv_out_value.setText(ftBean.getTotal_interest()+"元");//已赚收益值
		holder.tv_invest_value.setText(ftBean.getInvest_amount()+"元");//出借金额
		holder.tv_day_value.setText(ftBean.getDays()+"天");//已投天数
		String status = ftBean.getStatus();
		//1出借成功；2已赎回
		if ("1".equals(status)) {
			if("1".equals(ftBean.getIn_lock_day())){//是否在锁定期 1 是 2否
				holder.iv_isover.setVisibility(View.INVISIBLE);
				holder.rl_over.setVisibility(View.VISIBLE);
				holder.iv_cash.setVisibility(View.INVISIBLE);
				holder.tv_over.setText("锁定期至");
				holder.tv_over_value.setText(ftBean.getLock_expire_date());
			}
			if("2".equals(ftBean.getIn_lock_day())){
				holder.iv_isover.setVisibility(View.INVISIBLE);
				holder.rl_over.setVisibility(View.INVISIBLE);
				holder.iv_cash.setVisibility(View.VISIBLE);
				holder.iv_cash.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 点击赎回按钮
						getInfo(ftBean.getId());
					}
				});
			}

		}else if ("2".equals(status)) {
			holder.iv_isover.setVisibility(View.VISIBLE);
			holder.rl_over.setVisibility(View.VISIBLE);
			holder.iv_cash.setVisibility(View.INVISIBLE);
			holder.tv_over.setText("赎回手续费");
			holder.tv_over_value.setText(ftBean.getRec_fee()+"元");//赎回手续费值
		}
		return convertView;
	}


	protected void getInfo(final String id) {

		progressdialog.show();
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		map.put("ftId", id);
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.QUERYFTRECFEE_URL,
				null, map, new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("饭桶赎回手续费===" + string);
						progressdialog.dismiss();
						JSONObject json = JSON.parseObject(string);
						String code = json.getString("code");

						if ("0".equals(code)) {
							String datastr = json.getString("data");
							if (StringUtils.isBlank(datastr)) {
								// datastr为空不验签
							} else {
								String sign = json.getString("sign");
								boolean isSuccess = SignUtil.verify(sign,
										datastr);
								if (isSuccess) {// 验签成功

									JSONObject data = JSON.parseObject(datastr);

									//赎回费率
									String feeRate = data.getString("feeRate");

									//赎回手续费
									String fee = data.getString("fee");

									// 出借天数
									String investDays = data.getString("investDays");

									// 赎回本息
									String totalAmount = data.getString("totalAmount");

									//是否绑卡
									Boolean isBindCard = data.getBoolean("isBindCard");
									if (isBindCard) {
										FanTongDialog fanTongDialog = new FanTongDialog(mContext,
												R.style.YzmDialog,investDays,totalAmount,feeRate,fee,id);
										fanTongDialog.show();
									}else {
										BindCardDialog bindCardDialog = new BindCardDialog(mContext,
												R.style.YzmDialog);
										bindCardDialog.show();
									}

								} else {
									ToastUtils.toastshort("加载数据异常！");
								}
							}
						}else {
							String msg = json.getString("msg");
							ToastUtils.toastshort(msg);
						}

					}

					@Override
					public void onError(VolleyError error) {
						// TODO Auto-generated method stub
						progressdialog.dismiss();
						ToastUtils.toastshort("加载数据失败！");
					}

				});
	}

	class NoticeHolder{
		TextView tv_code;//项目编号
		TextView tv_in_value;//买入时间
		TextView tv_rate_value;//预期年化率
		TextView tv_out_value;//已赚收益值（或赎回时间值）
		TextView tv_invest_value;//出借金额
		TextView tv_day_value;//已投天数
		ImageView iv_cash;//赎回按钮
		RelativeLayout rl_over;
		TextView tv_over;//赎回手续费
		TextView tv_over_value;//赎回手续费值
		ImageView iv_isover;//是否已结清
	}
}
