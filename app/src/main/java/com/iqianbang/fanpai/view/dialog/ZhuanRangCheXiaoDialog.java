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
import com.iqianbang.fanpai.activity.user.SanBiaoProjectActivity;
import com.iqianbang.fanpai.activity.user.ZhuanRangProjectActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

public class ZhuanRangCheXiaoDialog extends Dialog implements
		View.OnClickListener {

	private CustomProgressDialog progressdialog;
	private String id;
	private String transSeq;
	private String holdMoney;
	private String from;
	private String url;

	public ZhuanRangCheXiaoDialog(Context context) {
		super(context);
	}

	public ZhuanRangCheXiaoDialog(Context context, int theme, String id, String transSeq, String holdMoney, String from) {
		super(context, theme);
		this.id = id;
		this.transSeq = transSeq;
		this.holdMoney = holdMoney;
		this.from = from;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_zhuanrang_chexiao);
		
		progressdialog = new CustomProgressDialog(getContext(), "正在加载数据...");
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		TextView tv_money = (TextView) findViewById(R.id.tv_money);
		tv_money.setText(holdMoney + "元");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			 dismiss();
			 break;
		case R.id.btn_ok:
			 dismiss();
			 getDataFromServer();
			 break;

		default:
			break;
		}
	}

	private void getDataFromServer() {

		if (!progressdialog.isShowing()) {
			progressdialog.show();
		}
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		map.put("token", token);
		map.put("proCode", from);
		map.put("transSeq", transSeq);
		if("scatter".equals(from)){
			map.put("scatterInvestId", id);
			url = ConstantUtils.CANCELTRANSFEROPERSCATTERPRO_URL;
		}
		if("transfer".equals(from)){
			map.put("transferInvestId", id);
			url = ConstantUtils.CANCELTRANSFEROPERTRANSFERPRO_URL;
		}
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(url, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("转让===" + string);
						progressdialog.dismiss();
						JSONObject json = JSON.parseObject(string);
						String code = json.getString("code");

						if ("0".equals(code)) {
							String datastr = json.getString("data");
							JSONObject data = JSON.parseObject(datastr);
							//成功撤销金额
							String holdMoney = data.getString("holdMoney");
							IKnowDialog iKnowDialog = new IKnowDialog(getContext(), R.style.YzmDialog,
									"成功撤回金额为" + holdMoney + "元");
							iKnowDialog.show();
							iKnowDialog.setOnIKnowDialogDismissListener(new IKnowDialog.OnIKnowDialogDismissListener() {
								@Override
								public void OnIKnowDialogDismiss() {
									if("scatter".equals(from)){
										Intent intent = new Intent(getContext(), SanBiaoProjectActivity.class);
										intent.putExtra("id", id);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										getContext().startActivity(intent);
									}
									if("transfer".equals(from)){
										Intent intent = new Intent(getContext(), ZhuanRangProjectActivity.class);
										intent.putExtra("id", id);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										getContext().startActivity(intent);
									}
								}
							});
						} else {
							String msg = json.getString("msg");
							ToastUtils.toastshort(msg);
						}
					}

					@Override
					public void onError(VolleyError error) {
						progressdialog.dismiss();
						ToastUtils.toastshort("网络请求失败");
					}
				});
	}
}

