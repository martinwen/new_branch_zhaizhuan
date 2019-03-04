package com.iqianbang.fanpai.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cunoraz.gifview.library.GifView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.global.FanPaiApplication;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.UpdateDialogUtil;
import com.iqianbang.fanpai.view.lockPattern.GestureLoginActivity;


public class EnterActivity extends BaseActivity {

	public static String IS_APP_FIRST_OPEN  = "is_app_first_open";// 是否应用第一次打开
	private UpdateDialogUtil upta;
	private int duration = 1500;
	private ImageView iv_success;
	private static final int MY_PERMISSIONS_REQUEST = 2;
	private GifView iv_gif;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

//		iv_success = (ImageView) findViewById(R.id.iv_success);//动画背景
		//动画背景
		iv_gif = (GifView) findViewById(R.id.gif);
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED){

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST);
		} else{
			checkUpdate();

		}

	}

	private void checkUpdate() {
		//背景侦动画
//		iv_success.setBackgroundResource(R.drawable.enteranim);
//		AnimationDrawable mAnimation = (AnimationDrawable)iv_success.getBackground();
//		mAnimation.start();
//
//		for(int i=0;i<mAnimation.getNumberOfFrames();i++){
//
//            duration += mAnimation.getDuration(i);
//
//        }

		iv_gif.setVisibility(View.VISIBLE);

		upta = new UpdateDialogUtil(this);
		upta.goupdate();
		upta.setNoUpdateListener(new UpdateDialogUtil.NoUpdateListener() {

            @Override
            public void noupdate() {

                // 根据保存的应用是否是第一次打开boolean值，判断进入什么界面
                boolean isAppFirstOpen = CacheUtils.getBoolean(IS_APP_FIRST_OPEN, true);
                if(isAppFirstOpen){// 应用第一次打开，进入引导界面
                    startActivity(new Intent(EnterActivity.this,GuideActivity.class));
                    finish();
                }else{// 应用不是第一次打开，进入主界面
                    FanPaiApplication.mainHandler.postDelayed(new  Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                            goMain();

                        }
                    }, duration);
                }
            }
        });
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		if (requestCode == MY_PERMISSIONS_REQUEST){

			//背景侦动画
			checkUpdate();

			return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	/**
	 *  欢迎页完成之后的判断
	 */
	private void goMain() {
		//得到当前用户的手势密码
		String gesturePassword_string = CacheUtils.getString(CacheUtils.BYTE, "");
        if(null==gesturePassword_string||"".equals(gesturePassword_string)){
			//没设置过手势密码（注册后没有输手势密码，登录后退出登录，手势密码输错超过5次）
        	String token = CacheUtils.getString("token", "");
    		// 从未登录过或者登录过，但是登录后退出了，直接跳首页
    		if (TextUtils.isEmpty(token)) {
    			startActivity(new Intent(this,MainActivity.class));
    		}else{// 登录过，注册后没有输手势密码或者手势密码输错超过5次
    			boolean gestureDisable = CacheUtils.getBoolean("gestureDisable", false);
    			if (gestureDisable) {//当手势密码输错超过5次，手势密码失效的情况
					Intent intent = new Intent(this, LoginActivity.class);
					intent.setFlags(10);
					startActivity(intent);
				}else {//这种情况是注册或登录后没有设置手势密码就退出，当再次进入app时跳设置手势密码界面
					startActivity(new Intent(this,LoginActivity.class));
				}
    		}
			
			finish();
        
        }else{
        	//设置过手势密码并且手势密码没有清除掉（没有退出登录，输错次数不超过5次）
			startActivity(new Intent(this,GestureLoginActivity.class));
			finish();
        }
	}
}
