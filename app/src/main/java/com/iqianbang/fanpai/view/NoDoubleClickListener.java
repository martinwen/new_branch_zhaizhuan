package com.iqianbang.fanpai.view;

import android.view.View;

import java.util.Calendar;

/**
  *  神仙有财
  *  功能描述: 防止重复点击的监听
  *   作 者:  李晓楠
  *   时 间： 2016/12/9 15:46
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 500;//防止重复的间隔
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    /**
     * 按钮点击事件
     * @param v
     */
    public void onNoDoubleClick(View v){

    }
}