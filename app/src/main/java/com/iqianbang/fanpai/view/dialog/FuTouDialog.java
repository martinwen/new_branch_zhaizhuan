package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iqianbang.fanpai.R;


public class FuTouDialog extends Dialog implements View.OnClickListener {

	private OnDialogDismissListener listener;
	private String msg;

	public FuTouDialog(Context context) {
		super(context);
	}

	public FuTouDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_futou);
		initDialog();
	}



	private void initDialog() {
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText(msg);
		TextView btn_ok = (TextView) findViewById(R.id.btn_ok);
		TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			dismiss();
			if (listener!=null) {
				listener.OnDialogDismiss();
			}
			break;
		case R.id.btn_cancel:
			dismiss();
			break;

		default:
			break;
		}
	}
	
	public interface OnDialogDismissListener{
    	void OnDialogDismiss();
    }
	
	public void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
		this.listener = onDialogDismissListener;
	}
}

