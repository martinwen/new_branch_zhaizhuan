package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class JiangJinDialog extends Dialog implements View.OnClickListener {

	private OnGestureCountDialogDismissListener listener;

	public JiangJinDialog(Context context) {
		super(context);
	}

	public JiangJinDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_jiangjin);
		initDialog();
	}



	private void initDialog() {
		TextView tv_loginagain = (TextView) findViewById(R.id.tv_loginagain);
		tv_loginagain.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_loginagain:
			dismiss();
			if (listener!=null) {
				listener.OnGestureCountDialogDismiss();
			}
			break;

		default:
			break;
		}
	}
	
	public interface OnGestureCountDialogDismissListener{
    	void OnGestureCountDialogDismiss();
    }
	
	public void setOnGestureCountDialogDismissListener(OnGestureCountDialogDismissListener onGestureCountDialogDismissListener) {
		this.listener = onGestureCountDialogDismissListener;
	}
}

