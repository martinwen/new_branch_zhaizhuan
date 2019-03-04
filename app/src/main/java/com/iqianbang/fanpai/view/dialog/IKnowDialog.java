package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class IKnowDialog extends Dialog implements View.OnClickListener {

	private OnIKnowDialogDismissListener listener;
	private String msg;

	public IKnowDialog(Context context) {
		super(context);
	}

	public IKnowDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_iknow);
		initDialog();
	}



	private void initDialog() {
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText(msg);
		TextView tv_over = (TextView) findViewById(R.id.tv_over);
		tv_over.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_over:
			dismiss();
			if (listener!=null) {
				listener.OnIKnowDialogDismiss();
			}
			break;

		default:
			break;
		}
	}
	
	public interface OnIKnowDialogDismissListener{
    	void OnIKnowDialogDismiss();
    }
	
	public void setOnIKnowDialogDismissListener(OnIKnowDialogDismissListener onIKnowDialogDismissListener) {
		this.listener = onIKnowDialogDismissListener;
	}
}

