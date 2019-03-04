package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.utils.ToastUtils;


public class XieYiDialog extends Dialog implements View.OnClickListener {

	private String msg;

	public XieYiDialog(Context context) {
		super(context);
	}

	public XieYiDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_xieyi);
		initDialog();
	}



	private void initDialog() {
		TextView tv_close = (TextView) findViewById(R.id.tv_close);
		tv_close.setOnClickListener(this);
		TextView tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setText("请您仔细阅读相关合同/文件，并在完全知晓并充分理解全部内容及其相应法律后果后，通过点击勾选予以确认签署《网络借贷风险和禁止性行为提示书》"+msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_close:
			dismiss();
			break;

		default:
			break;
		}
	}
	
}

