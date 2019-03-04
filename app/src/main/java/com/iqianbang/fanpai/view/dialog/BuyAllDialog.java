package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class BuyAllDialog extends Dialog implements View.OnClickListener {

	private String money;

	public BuyAllDialog(Context context) {
		super(context);
	}

	public BuyAllDialog(Context context, int theme, String money) {
		super(context, theme);
		this.money = money;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_buyall);
		initDialog();
	}



	private void initDialog() {
		TextView tv_over = (TextView) findViewById(R.id.tv_over);
		tv_over.setOnClickListener(this);
		TextView tv_row1 = (TextView) findViewById(R.id.tv_row1);
		tv_row1.setText("为避免剩余金额<" + money + "元时其他用户无法承接");
		TextView tv_row3 = (TextView) findViewById(R.id.tv_row3);
		tv_row3.setText("使剩余金额>=" + money + "元");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_over:
			dismiss();
			break;

		default:
			break;
		}
	}
	
}

