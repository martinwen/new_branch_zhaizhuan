package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.JiaXiPiaoActivity;


public class JiaXiPiaoUseSuccessDialog extends Dialog implements
		android.view.View.OnClickListener {

	private String msg;
	public JiaXiPiaoUseSuccessDialog(Context context) {
		super(context);
	}
	
	public JiaXiPiaoUseSuccessDialog(Context context, int theme) {
		super(context, theme);
	}

	public JiaXiPiaoUseSuccessDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_jiaxipiaousesuccess);
		
		initDialog();
	}
	
	
  
	private void initDialog() {
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		if (msg!=null&&msg!="") {
			tv_content.setText(msg);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			 dismiss();
			 //刷新（饭盒—可使用）界面
			 Intent intent = new Intent(getContext(), JiaXiPiaoActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 getContext().startActivity(intent);
			 break;

		default:
			break;
		}
	}
	
}

