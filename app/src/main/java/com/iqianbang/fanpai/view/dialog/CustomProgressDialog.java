package com.iqianbang.fanpai.view.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.global.FanPaiApplication;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 通过帧动画 实现 类似58同城 类似的加载网络的进度条
 */
public class CustomProgressDialog extends ProgressDialog {

	private AnimationDrawable mAnimation;
	private Context mContext;
	private ImageView loadingimage;
	private String mLoadingTip;
	private TextView loadingtext;
	private int mResid;//帧动画资源的id
	public CustomProgressDialog(Context context, String content) {
	    this(context,content, R.drawable.loadanim);
	 }
    /**
     * @param context  上下文环境
     * @param content  要显示的文字内容
     * @param id       帧动画资源的ID
     */
	public CustomProgressDialog(Context context, String content, int id) {
		super(context);
		this.mContext = context;
		this.mLoadingTip = content;
		this.mResid = id;
	}
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initData();
	}

	private void initData() {

		loadingimage.setBackgroundResource(mResid);
		// 通过ImageView对象拿到背景显示的AnimationDrawable
		mAnimation = (AnimationDrawable) loadingimage.getBackground();
		// 为了防止在onCreate方法中只显示第一帧的解决方案之一
		loadingimage.post(new Runnable() {
			@Override
			public void run() {
				mAnimation.start();
			}
		});
		loadingtext.setText(mLoadingTip);

	}

	public void setContent(String str) {
		loadingtext.setText(str);
	}

	private void initView() {
		setContentView(R.layout.dialog_progress);
		/** 设置透明度 */
		Window window = getWindow();
		window.setBackgroundDrawableResource(R.drawable.a);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 1.0f;// 透明度
		lp.dimAmount = 0.1f;// 黑暗度
		/*lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height=WindowManager.LayoutParams.MATCH_PARENT;*/
		window.setAttributes(lp);
		//window.setContentView(R.layout.dialog_progress);//背景
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		loadingtext = (TextView) findViewById(R.id.loadingtext);
		loadingimage = (ImageView) findViewById(R.id.loadingimage);
	}


	//showis（）方法：进入界面2秒内返回数据不显示加载进度条，超过2秒就显示
	private boolean canshow;
	public void showis() {
		canshow=true;
		Timer tExit = new Timer();
		tExit.schedule(new TimerTask() {
			@Override
			public void run() {
				if(canshow){
					FanPaiApplication.mainHandler.post(new Runnable() {
						@Override
						public void run() {
							show();
						}
					});
				}
			}
		}, 2000);
	}

	@Override
	public void dismiss() {
		canshow=false;
		if(isShowing())
			super.dismiss();
	}

}
