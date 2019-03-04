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
import com.iqianbang.fanpai.activity.invest.FtMaxBuyActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuyConfirmActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.HongBaoUseActivity;
import com.iqianbang.fanpai.activity.invest.JiaXiPiaoUseActivity;
import com.iqianbang.fanpai.global.FanPaiApplication;
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

public class HasHongBaoOrJiaXiPiaoDialogForFWbuy extends Dialog implements
		View.OnClickListener {

	private OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener listener;
	private String msg;

	public HasHongBaoOrJiaXiPiaoDialogForFWbuy(Context context) {
		super(context);
	}

	public HasHongBaoOrJiaXiPiaoDialogForFWbuy(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_hashongbaoorjiaxipiao);
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText(msg);
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
			 break;
		case R.id.btn_ok:
			 dismiss();
			 if (listener!=null) {
				listener.OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismiss();
			 }
			 break;

		default:
			break;
		}
	}

	public interface OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener{
		void OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismiss();
	}

	public void setOnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener(OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener listener) {
		this.listener = listener;
	}
}

