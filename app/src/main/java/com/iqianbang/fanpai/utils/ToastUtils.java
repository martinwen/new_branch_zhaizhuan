package com.iqianbang.fanpai.utils;

import android.widget.Toast;

import com.iqianbang.fanpai.global.FanPaiApplication;


public class ToastUtils {

    public static void toastshort(String str){
    	Toast.makeText(FanPaiApplication.context,str, Toast.LENGTH_SHORT).show();
    }
    public static void toastlong(String str){
    	Toast.makeText(FanPaiApplication.context,str, Toast.LENGTH_LONG).show();
    }
}
