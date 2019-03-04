package com.iqianbang.fanpai.global;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.iqianbang.fanpai.qiyu.UILImageLoader;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.view.lockPattern.GestureLoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;

import cn.jpush.android.api.JPushInterface;


public class FanPaiApplication extends Application {

	public static Context context;//全局的上下文
	public static Handler mainHandler;//全局的主线程的Handler
	public int count = 0; 
	private long startTime;

	@Override
	public void onCreate() {
		super.onCreate();

		//初始化Context
		context = this;

		//初始化Handler
		mainHandler = new Handler();

		//初始化ImageLoader,其实就是初始化缓存目录，内存缓存大小，磁盘缓存大小等
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));


		// 初始化七鱼客服，appKey 可以在七鱼管理系统-\>设置-\>APP接入 页面找到
		Unicorn.init(this, "19ef9ee0987d58a6321130692d56df37", options(), new UILImageLoader());


		//初始化JPush
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

		//程序在后台运行三分钟后重新进入时需要手势密码登录
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			


			@Override
			public void onActivityStopped(Activity activity) {
				count--;
				startTime = System.currentTimeMillis();

			}
			
			@Override
			public void onActivityStarted(Activity activity) {

				String token = CacheUtils.getString("token", "");
				boolean gestureDisable = CacheUtils.getBoolean("gestureDisable", false);
				long endTime = System.currentTimeMillis();
                if (count == 0) {
                	if (startTime!=0) {
                		//时间大于3分钟并且手势密码没有失效
                		if (endTime - startTime > 180000&& StringUtils.isNotBlank(token)&&!gestureDisable) {
							LogUtils.i("token!=null");
							Intent intent = new Intent(context,GestureLoginActivity.class);
							intent.setFlags(10000);
							activity.startActivity(intent);
                		}
					}
                }
                count++;
				
			}
			
			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityResumed(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityPaused(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityDestroyed(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private YSFOptions options() {
		YSFOptions options = new YSFOptions();
		options.statusBarNotificationConfig = new StatusBarNotificationConfig();
		options.savePowerConfig = new SavePowerConfig();
		return options;
	}
	
}
