package com.iqianbang.fanpai.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.NomalWebviewActivity;
import com.iqianbang.fanpai.utils.LogUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by wangjian on 2017/1/4.
 */

public class JpushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtils.i("接收到推送下来的通知");

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtils.i("用户点击打开了通知");
            //收到的通知内容
//            String message = bundle.getString(JPushInterface.EXTRA_ALERT);
//            LogUtils.i("message:" + message);
            //解析json
            String string = bundle.getString(JPushInterface.EXTRA_EXTRA);//json串
            LogUtils.i("string==" + string);
            try {
                JSONObject jsonObject = new JSONObject(string);
                String type = jsonObject.getString("type");
                String msg = jsonObject.getString("msg");

                Intent i = new Intent(context, MainActivity.class);
                i.putExtras(bundle);
                i.putExtra("type", type);
                i.putExtra("msg", msg);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}