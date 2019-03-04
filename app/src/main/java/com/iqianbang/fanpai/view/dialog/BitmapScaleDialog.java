package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.iqianbang.fanpai.R;


public class BitmapScaleDialog extends Dialog {

	private Bitmap cBitmap;

	public BitmapScaleDialog(Context context) {
		super(context);
	}

	public BitmapScaleDialog(Context context, int theme, Bitmap bitmap) {
		super(context, theme);
		this.cBitmap = bitmap;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bitmapscale);
		initDialog();
	}



	private void initDialog() {
		ImageView iv_bitmap = (ImageView) findViewById(R.id.iv_bitmap);
		iv_bitmap.setImageBitmap(cBitmap);
	}


}

