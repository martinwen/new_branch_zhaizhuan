package com.iqianbang.fanpai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FanTongBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxBuySuccessActivity;
import com.iqianbang.fanpai.activity.invest.FtMinBuySuccessActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiShenQingActivity;
import com.iqianbang.fanpai.activity.user.ChongZhiSuccessActivity;
import com.iqianbang.fanpai.activity.user.FtMaxZaiTouActivity;
import com.iqianbang.fanpai.activity.user.FtMinZaiTouActivity;
import com.iqianbang.fanpai.activity.user.SettingForCePingActivity;
import com.iqianbang.fanpai.activity.user.TiXianJinDuActivity;
import com.iqianbang.fanpai.adapter.jiaxipiao.JiaXiPiaoFwUseableAdapter;
import com.iqianbang.fanpai.fragment.FindFragment;
import com.iqianbang.fanpai.fragment.HomeFragment;
import com.iqianbang.fanpai.fragment.InvestFragment;
import com.iqianbang.fanpai.fragment.InvestZhiTouFragment;
import com.iqianbang.fanpai.fragment.MyFragment;
import com.iqianbang.fanpai.fragment.UserFragment;
import com.iqianbang.fanpai.fragment.UserNewFragment;
import com.iqianbang.fanpai.service.RefreshTokenReceiver;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.RefTokenUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CashBackDialog;
import com.iqianbang.fanpai.view.dialog.NormalDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private FrameLayout homeFragments;
    private LinearLayout homeTabs;
    public int mCurrentTab;
    private Fragment[] mPages = new Fragment[]{
            new HomeFragment(),
            new InvestFragment(),
            new FindFragment(),
            new MyFragment()
    };

    //刷新token
    public static String REFRESH_RECEIVER = "com.fanfanlicai.service.RefreshService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化每个选项卡的事件

        initView();
        initData();

        //开启AlertManager 并启动service
        String token = CacheUtils.getString("token", "");
        if(StringUtils.isNotBlank(token)){
            RefTokenUtils.startRefrsh(this, 1, RefreshTokenReceiver.class,
                    REFRESH_RECEIVER);
        }

        //激光推送消息弹窗显示
        showPushMessage();
    }

    private void showPushMessage() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String msg = intent.getStringExtra("msg");
        if("1".equals(type)){//弹窗（和后台约定好的推送规则）
            if (StringUtils.isNotBlank(msg)) {
                NormalDialog jpushDialog = new NormalDialog(this, R.style.YzmDialog, msg);
                jpushDialog.show();
            }
        }
        if("2".equals(type)){//到网址（和后台约定好的推送规则）
            Intent i = new Intent(this,NomalWebviewActivity.class);
            i.putExtra("url", msg);
            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭AlertManager 并关闭service ，省电
        RefTokenUtils.stopRefresh(this, RefreshTokenReceiver.class, REFRESH_RECEIVER);
    }

    private void initView() {
        homeFragments = (FrameLayout) findViewById(R.id.home_fragments);
        homeTabs = (LinearLayout) findViewById(R.id.home_tabs);
    }

    private void initData() {
        //根据不同的业务需求切换界面
        String from = getIntent().getStringExtra("from");
        if(!TextUtils.isEmpty(from)){
            if(from.equals(ChongZhiSuccessActivity.FLAG_CHONGZHISUCCESS)||
                    from.equals(TiXianJinDuActivity.FLAG_TIXIANSUCCESS)||
                    from.equals(CashBackDialog.FLAG_CASHBACKSUCCESS)||
                    from.equals(ChongZhiShenQingActivity.FLAG_CHONGZHISHENQING)){
                mCurrentTab = 3;
            }else if(from.equals(ChongZhiSuccessActivity.FLAG_GOTOINVEST)||
                    from.equals(FtMinZaiTouActivity.FLAG_FTMINGOTOINVEST)||
                    from.equals(FtMinBuySuccessActivity.FLAG_MINBUYSUCCESS)||
                    from.equals(FtMaxBuySuccessActivity.FLAG_MAXBUYSUCCESS)||
                    from.equals(FanTongBuySuccessActivity.FLAG_JINGYINGBUYSUCCESS)||
                    from.equals(HomeFragment.FLAG_GOTOSMALLTUAN)||
                    from.equals(HomeFragment.FLAG_GOTOBIGTUAN)||
                    from.equals(HomeFragment.FLAG_GOTOJYTUAN)||
                    from.equals(SettingForCePingActivity.FLAG_GOTOSMALLTUAN)){
                mCurrentTab = 1;
            }else{
                mCurrentTab = 0;
            }
        }

        //getChildCount获取布局的子元个数
        //getChildAt(index)指定获取第几个元素
        //setEnabled true 设置state_enable=true; false state_enable=false
        final int childCount = homeTabs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout childAt = (LinearLayout) homeTabs.getChildAt(i);
            final int curr = i;
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentTab = curr;
                    selectIndexTab(childCount);
                }
            });
        }
        selectIndexTab(childCount);
    }

    public void selectIndexTab(int childCount) {
        for (int i = 0; i < childCount; i++) {
            //获取子元素
            LinearLayout childAt = (LinearLayout) homeTabs.getChildAt(i);
            ImageView img = (ImageView) childAt.getChildAt(0);
            TextView text = (TextView) childAt.getChildAt(1);
            if (mCurrentTab == i) {
                img.setEnabled(false);
                text.setEnabled(false);
            } else {
                img.setEnabled(true);
                text.setEnabled(true);
            }
        }
        //切换 fragment
        getSupportFragmentManager().beginTransaction()//
                .replace(R.id.home_fragments, mPages[mCurrentTab])//
                .commit();//
    }

    /**
     * 双击退出应用
     */
    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event != null && keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }
        return false;
    }

    //退出app
    private void exitApp(){
        if (isExit == false) {
            isExit = true;
            ToastUtils.toastshort("再按一下退出");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        }else{
            //友盟用来保存统计数据
            MobclickAgent.onKillProcess(this);
            finish();
            System.gc();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
