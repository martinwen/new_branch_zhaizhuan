package com.iqianbang.fanpai.adapter.youxiangou;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FtMinBuyYsgActivity;
import com.iqianbang.fanpai.bean.YouXianGouBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.BindCardYxgDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;


public class YouXianGouUseableAdapter extends BaseAdapter {

	private ArrayList<YouXianGouBean> list;
	private Context mContext;
	private CustomProgressDialog progressdialog;

	public YouXianGouUseableAdapter(Context context, ArrayList<YouXianGouBean> list) {
		super();
		this.mContext = context;
		this.list = list;
		progressdialog = new CustomProgressDialog(mContext, "正在加载数据...");
	}
	public YouXianGouUseableAdapter(Context context) {
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
			convertView = View.inflate(mContext, R.layout.items_youxiangou_usable, null);
			holder.tv_hongbao_usetime = (TextView) convertView.findViewById(R.id.tv_hongbao_usetime);
			holder.tv_invest_money = (TextView) convertView.findViewById(R.id.tv_invest_money);
			holder.tv_hongbao_info = (TextView) convertView.findViewById(R.id.tv_hongbao_info);
			holder.tv_hongbao_method = (TextView) convertView.findViewById(R.id.tv_hongbao_method);
			holder.iv_use = (ImageView) convertView.findViewById(R.id.iv_use);
			convertView.setTag(holder);
		}else {
			holder = (NoticeHolder) convertView.getTag();
		}
		final YouXianGouBean youXianGouBean = list.get(position);

		//红包有效期
		//以空格截取字符串
		String splitCreateTime[] = youXianGouBean.getCreateTime().split(" ");
		String createTime = splitCreateTime[0];

		String splitExpiredTime[] = youXianGouBean.getExpiredTime().split(" ");
		String expiredTime = splitExpiredTime[0];

		//最低出借
		holder.tv_invest_money.setText("优先购额度   "+youXianGouBean.getAmount()+"元");

		//使用时间
		holder.tv_hongbao_usetime.setText("有效时间   "+createTime+"至"+expiredTime);
		
		//适用产品
		holder.tv_hongbao_info.setText("适用产品   "+youXianGouBean.getProductName());
		
		//获得来源
		holder.tv_hongbao_method.setText("获得来源   "+youXianGouBean.getFrom_source());

		//点击使用
		holder.iv_use.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				requestPut(youXianGouBean.getAmount(),youXianGouBean.getId());

			}
		});

		return convertView;
	}

	private void requestPut(final String amount,final String id) {
		if (!progressdialog.isShowing()) {
			progressdialog.showis();
		}
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString( "token", "");
		map.put("token", token);
		map.put("type", "fh");
		map.put("preferTicketId", id);
		map.put("preferAmount", amount);
		LogUtils.i("小饭团优先购买入==="+"preferTicketId=="+id+"preferAmount=="+amount);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.GOTOINVEST_URL, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("小饭团优先购买入==="+string);
						// TODO Auto-generated method stub
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

									// 奖金余额
									String rewardAcctBal = data.getString("rewardAcctBal");
									CacheUtils.putString("rewardAcctBal",rewardAcctBal);

									// 交易流水号
									String seqNo = data.getString("seqNo");

									// 账户余额
									String baseBal = data.getString("baseBal");

									// 项目余额
									String borrowSalableBal = data.getString("borrowSalableBal");

									// 单笔最小出借金额（最低出借）
									String minInvestAmount = data.getString("minInvestAmount");

									// 是否绑卡
									Boolean isBindCard = data.getBoolean("isBindCard");
									if (isBindCard) {//已经绑卡
										Intent intent = new Intent(mContext, FtMinBuyYsgActivity.class);
										intent.putExtra("baseBal", baseBal);
										intent.putExtra("borrowSalableBal", borrowSalableBal);
										intent.putExtra("minInvestAmount", minInvestAmount);
										intent.putExtra("seqNo", seqNo);
										intent.putExtra("amount", amount);
										mContext.startActivity(intent);
									}else {//未绑卡
										BindCardYxgDialog bindCardYxgDialog = new BindCardYxgDialog(mContext, R.style.YzmDialog);
										bindCardYxgDialog.show();
									}

								} else {
									ToastUtils.toastshort("加载数据异常！");
								}
							}
						}  else if("666666".equals(code)){

						}else {
							String msg = json.getString("msg");
							ToastUtils.toastshort(msg);
						}
					}

					@Override
					public void onError(VolleyError error) {
						progressdialog.dismiss();
					}
				});
	}

	class NoticeHolder{
		TextView tv_invest_money;
		TextView tv_hongbao_usetime;
		TextView tv_hongbao_info;
		TextView tv_hongbao_method;
		ImageView iv_use;
	}
}
