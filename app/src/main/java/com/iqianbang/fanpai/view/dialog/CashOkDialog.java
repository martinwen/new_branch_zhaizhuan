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
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.user.CashBackActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouActivity;
import com.iqianbang.fanpai.activity.user.FtMinZaiTouActivity;
import com.iqianbang.fanpai.fragment.HomeFragment;
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

public class CashOkDialog extends Dialog implements
		View.OnClickListener {

	private CustomProgressDialog progressdialog;

	public CashOkDialog(Context context) {
		super(context);
	}

	public CashOkDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_cashok);
		
		progressdialog = new CustomProgressDialog(getContext(), "正在加载数据...");
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
		case R.id.btn_cancel:
			dismiss();
			checkFromServer();
			break;
		case R.id.btn_ok:
			dismiss();
			Intent intent = new Intent(getContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("from", HomeFragment.FLAG_GOTOBIGTUAN);
			getContext().startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void checkFromServer() {
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		// 如果没有登录，直接return，不访问网络了
		if (TextUtils.isEmpty(token)) {
			return;
		}
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.FHVERIFYINFO_URL, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {

						LogUtils.i("饭盒赎回校验==="+string);

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

									String uuid = data.getString("uuid");
									String fhTotalAssets = data.getString("fhTotalAssets");
									String recUserDailyLimit = data.getString("recUserDailyLimit");
									Intent intent = new Intent(getContext(), CashBackActivity.class);
									intent.putExtra("uuid",uuid);
									intent.putExtra("fhTotalAssets",fhTotalAssets);
									intent.putExtra("recUserDailyLimit",recUserDailyLimit);
									getContext().startActivity(intent);

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
						ToastUtils.toastshort("网络连接失败，请检查网络");
					}
				});
	}

}

