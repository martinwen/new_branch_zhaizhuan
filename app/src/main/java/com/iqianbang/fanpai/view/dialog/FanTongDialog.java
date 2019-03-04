package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.FtJingYingZaiTouActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

public class FanTongDialog extends Dialog implements
		android.view.View.OnClickListener {

	private CustomProgressDialog progressdialog;
	private String day;
	private String money;
	private String rate;
	private String fee;
	private String id;

	public FanTongDialog(Context context) {
		super(context);
	}
	
	public FanTongDialog(Context context, int theme, String day, String money, String rate, String fee, String id) {
		super(context, theme);
		this.day = day;
		this.money = money;
		this.rate = rate;
		this.fee = fee;
		this.id = id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fantong);
		
		progressdialog = new CustomProgressDialog(getContext(), "正在处理中...");
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView tv_day = (TextView) findViewById(R.id.tv_day);
		tv_day.setText("已投天数："+day+"天");
		TextView tv_money = (TextView) findViewById(R.id.tv_money);
		tv_money.setText("赎回本息："+money+"元");
		TextView tv_rate = (TextView) findViewById(R.id.tv_rate);
		tv_rate.setText("赎回手续费率："+rate+"%");
		TextView tv_fee = (TextView) findViewById(R.id.tv_fee);
		tv_fee.setText("赎回手续费："+fee+"元");
		TextView btn_bank = (TextView) findViewById(R.id.btn_bank);
		btn_bank.setOnClickListener(this);
		TextView btn_yue = (TextView) findViewById(R.id.btn_yue);
		btn_yue.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bank:
			 dismiss();
			 break;
		case R.id.btn_yue:
			 dismiss();
			 requestBack();
			 break;

		default:
			break;
		}
	}

	
	private void requestBack() {

		if (!progressdialog.isShowing()) {
			progressdialog.show();
		}
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", null);
		map.put("token", token);
		map.put("ftId", id);
		map.put("recToCard", false+"");
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.FTRECSUBMIT_URL, null, map,
			new HttpBackBaseListener() {

				@Override
				public void onSuccess(String string) {
					LogUtils.i("饭桶赎回==="+string);
					// TODO Auto-generated method stub
					progressdialog.dismiss();
					JSONObject json = JSON.parseObject(string);
					String code = json.getString("code");

					if ("0".equals(code)) {
						NormalDialog normalDialog = new NormalDialog(getContext(), R.style.YzmDialog, "赎回成功！资金已转入【账户余额】，您可再出借或提现到银行卡。");
						normalDialog.setCanceledOnTouchOutside(false);
						normalDialog.setCancelable(false);
						normalDialog.show();
						normalDialog.setOnNormalDialogDismissListener(new NormalDialog.OnNormalDialogDismissListener() {

							@Override
							public void OnNormalDialogDismiss() {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getContext(), FtJingYingZaiTouActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								getContext().startActivity(intent);
							}
						});
					}else {
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

