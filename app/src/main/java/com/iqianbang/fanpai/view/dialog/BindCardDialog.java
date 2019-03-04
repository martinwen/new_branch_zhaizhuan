package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.ChongZhiBindBankActivity;


public class BindCardDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	public BindCardDialog(Context context) {
		super(context);
	}
	
	public BindCardDialog(Context context, int theme) {
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
			 Intent intent = new Intent(getContext(), ChongZhiBindBankActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 getContext().startActivity(intent);
			 break;
		case R.id.btn_cancel://取消
			 dismiss();
			 break;

		default:
			break;
		}
	}
}

