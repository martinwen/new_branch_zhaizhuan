package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FtMaxBuyConfirmActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.HongBaoUseActivity;
import com.iqianbang.fanpai.activity.invest.JiaXiPiaoUseActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouActivity;
import com.iqianbang.fanpai.activity.user.JiaXiPiaoActivity;
import com.iqianbang.fanpai.activity.user.SanBiaoProjectActivity;
import com.iqianbang.fanpai.activity.user.SanBiaoZaiTouActivity;
import com.iqianbang.fanpai.activity.user.ZhuanRangActivity;
import com.iqianbang.fanpai.activity.user.ZhuanRangProjectActivity;
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

public class ZhuanRangDialog extends Dialog implements
		View.OnClickListener {

	private CustomProgressDialog progressdialog;
	private String id;
	private String holdMoney;
	private String discountRate;
	private String from;

	public ZhuanRangDialog(Context context) {
		super(context);
	}

	public ZhuanRangDialog(Context context, int theme, String id, String holdMoney, String discountRate, String from) {
		super(context, theme);
		this.id = id;
		this.holdMoney = holdMoney;
		this.discountRate = discountRate;
		this.from = from;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_zhuanrang);
		
		progressdialog = new CustomProgressDialog(getContext(), "正在加载数据...");
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText("您确定要将" + holdMoney + "元的出借本金\n以" + discountRate + "%的折让率发起转让吗？");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			dismiss();
			break;
		case R.id.btn_ok:
			dismiss();
			buy();
			break;
		}
	}

	protected void buy() {
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		// 如果没有登录，直接return，不访问网络了
		if (TextUtils.isEmpty(token)) {
			return;
		}
		progressdialog.show();
		map.put("token", token);
		map.put("investRecordId", id);
		map.put("holdMoney", holdMoney);
		map.put("discountRate", discountRate);
		map.put("proCode", from);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.PROTRANSOPER_URL, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {

						LogUtils.i("转让===" + string);
						progressdialog.dismiss();
						JSONObject json = JSON.parseObject(string);
						String code = json.getString("code");

						if ("0".equals(code)) {

							IKnowDialog iKnowDialog = new IKnowDialog(getContext(), R.style.YzmDialog,
									"转让申请已成功提交，您可在\n出借记录中查看转让进度");
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
						ToastUtils.toastshort("网络连接失败，请检查网络");
					}
				});

	}
}

