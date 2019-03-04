package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class SignDialog extends Dialog implements
		View.OnClickListener {

	private String msg;
	public SignDialog(Context context) {
		super(context);
	}

	public SignDialog(Context context, int theme) {
		super(context, theme);
	}

	public SignDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_cashback);
		
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		if (msg!=null&&msg!="") {
			tv_content.setText("恭喜您获得：\n"+msg);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			 dismiss();
			 break;

		default:
			break;
		}
	}
	
}

