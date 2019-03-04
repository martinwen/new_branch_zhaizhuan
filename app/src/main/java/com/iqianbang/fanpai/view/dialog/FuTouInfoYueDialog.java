package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class FuTouInfoYueDialog extends Dialog implements View.OnClickListener {

	private String maxReturnAmount;
	private String minInvestAmount;
	private String scale;

	public FuTouInfoYueDialog(Context context) {
		super(context);
	}

	public FuTouInfoYueDialog(Context context, int theme, String maxReturnAmount, String minInvestAmount, String scale) {
		super(context, theme);
		this.maxReturnAmount = maxReturnAmount;
		this.minInvestAmount = minInvestAmount;
		this.scale = scale;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_futouinfoyue);
		initDialog();
	}



	private void initDialog() {
		ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
		iv_close.setOnClickListener(this);
		TextView tv_content_a = (TextView) findViewById(R.id.tv_content_a);
		TextView tv_content_b = (TextView) findViewById(R.id.tv_content_b);
		TextView tv_content_c = (TextView) findViewById(R.id.tv_content_c);
		tv_content_a.setText("回款本金 = 本金*" + scale +"%（预约成功后转入账户余额）");
		tv_content_b.setText("剩余本金 < " + minInvestAmount +"元，则不享受本金回款");
		tv_content_c.setText("活动专享标的每日本金回款限额为" + maxReturnAmount +"元");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			dismiss();
			break;

		default:
			break;
		}
	}
	
}

