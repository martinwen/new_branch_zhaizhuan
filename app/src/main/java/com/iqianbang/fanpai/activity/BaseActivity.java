package com.iqianbang.fanpai.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity {
    //设置系统字体大小时app字体不会随着变化而变化
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }


    //友盟统计需要在所有的Activity中都调用 MobclickAgent.onResume() 和MobclickAgent.onPause()方法
    public void onResume() {
        super.onResume();
        //方便有盟统计
        MobclickAgent.onResume(this);
        //方便百度统计
        StatService.onResume(this);
    }
    public void onPause() {
        super.onPause();
        //方便有盟统计
        MobclickAgent.onPause(this);
        //方便百度统计
        StatService.onResume(this);
    }
}
