package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.ChongZhiActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiBindBankActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;


public class BindCardYxgDialog extends Dialog implements
		View.OnClickListener {

	public BindCardYxgDialog(Context context) {
		super(context);
	}

	public BindCardYxgDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bindcard);
		
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok://去绑卡
			 dismiss();
			//充值第一步获取用户信息
			requestPut();
			 break;
		case R.id.btn_cancel://取消
			 dismiss();
			 break;

		default:
			break;
		}
	}

	private void requestPut() {
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		map.put("token", token);
		map.put("type", "recharge");
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.GOTOINVEST_URL, null, map,
				new HttpBackBaseListener() {
					@Override
					public void onSuccess(String string) {
						LogUtils.i("充值按钮===" + string);
						// TODO Auto-generated method stub
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

									// 真实姓名
									String realName = data.getString("realName");
									CacheUtils.putString("realName", realName);

									// 身份证号
									String idNo = data.getString("idNo");
									CacheUtils.putString("idNo", idNo);

									// 银行名称
									String bankPic = data.getString("bankPic");
									CacheUtils.putString("bankPic", bankPic);

									// 开户行
									String bankName = data.getString("bankName");
									CacheUtils.putString("bankName", bankName);

									// 银行卡号
									String cardNum = data.getString("cardNum");
									CacheUtils.putString("cardNum", cardNum);

									// 银行卡预留手机号
									String cardPhone = data.getString("cardPhone");

									//银行单笔限额
									String singleTransLimit = data.getString("singleTransLimit");
									CacheUtils.putString("singleTransLimit", singleTransLimit);

									//银行单日限额
									String singleDayLimit = data.getString("singleDayLimit");
									CacheUtils.putString("singleDayLimit", singleDayLimit);

									// 交易流水号
									String seqNo = data.getString("seqNo");

									//3:换卡（身份证号和姓名不允许输入），4新绑卡
									String isEnable = data.getString("isEnable");

									// 是否绑卡
									Boolean isBindCard = data.getBoolean("isBindCard");
									if (isBindCard) {//已经绑卡，去充值界面
										Intent intent = new Intent(getContext(), ChongZhiActivity.class);
										intent.putExtra("seqNo", seqNo);
										getContext().startActivity(intent);
									} else {// 如果还没有绑卡，去绑卡
										Intent intent = new Intent(getContext(), ChongZhiBindBankActivity.class);
										intent.putExtra("realName", realName);
										intent.putExtra("idNo", idNo);
										intent.putExtra("bankName", bankName);
										intent.putExtra("cardNum", cardNum);
										intent.putExtra("cardPhone", cardPhone);
										intent.putExtra("seqNo", seqNo);
										intent.putExtra("isEnable", isEnable);
										getContext().startActivity(intent);
									}

								} else {
									ToastUtils.toastshort("加载数据异常！");
								}
							}
						} else {
							String msg = json.getString("msg");
							ToastUtils.toastshort(msg);
						}
					}

					@Override
					public void onError(VolleyError error) {
						ToastUtils.toastshort("网络异常！");
					}
				});
	}
}

