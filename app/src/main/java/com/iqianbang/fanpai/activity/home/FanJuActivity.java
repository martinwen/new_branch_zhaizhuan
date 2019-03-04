package com.iqianbang.fanpai.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.activity.registerandlogin.RegisterStep1Activity;
import com.iqianbang.fanpai.fragment.HomeFragment;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.DeviceUtil;
import com.iqianbang.fanpai.utils.LogUtils;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_title;


/**
 * 发现模块的网页url
 *
 * @author lxn
 */
public class FanJuActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(tv_title)
    TextView tvTitle;

    private String url;
    private String inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        initdata();
    }

    public void initdata() {

        //清除cookie，防止两个用户用同一个手机登录参加活动，下一个用户依然显示上一个用户的信息
        CookieSyncManager.createInstance(FanJuActivity.this);
        CookieManager.getInstance().removeAllCookie();

        inviteCode = CacheUtils.getString(CacheUtils.INVITECODE, "");
        String token = CacheUtils.getString("token", "");
        try {
            url = ConstantUtils.DAYDINNERINDEX_URL + "?clientType=5" + "&token=" + URLEncoder.encode(token, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initwebview();
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        if (null != webview && webview.canGoBack()) {  //表示按返回键时的操作
            webview.goBack();   //后退
        } else {
            finish();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initwebview() {

        webview.clearCache(true);
        webview.clearHistory();

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        //与webview交互
        webview.addJavascriptInterface(new JavaScriptInterface(), "fantuan");
        //没有缓存（防止从第二个界面goback时闪现白屏现象）
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);

                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                progressbar.setVisibility(View.GONE);
                webview.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressbar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressbar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //设置标题
                tvTitle.setText(title);
            }
        });

        webview.loadUrl(url);
    }

    /**
     * 与webview 交互使用的
     *
     * @author lxn
     */
    class JavaScriptInterface {


        JavaScriptInterface() {
        }

        @JavascriptInterface
        public void register() {
            LogUtils.i("从活动注册");
            //定义一个flag，用于标记这个注册是活动页面中的注册，与正常注册区分
//            ConstantUtils.loginflag = 1;
            Intent intent = new Intent(FanJuActivity.this, RegisterStep1Activity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void login() {
            LogUtils.i("从活动登录");
            //定义一个flag，用于标记这个登录是活动页面中的登录，与正常登录区分
//            ConstantUtils.loginflag = 1;
            Intent intent = new Intent(FanJuActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void ftMinInvest() {
            Intent intent = new Intent(FanJuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", HomeFragment.FLAG_GOTOSMALLTUAN);
            startActivity(intent);
        }

        @JavascriptInterface
        public void ftMaxInvest() {
            Intent intent = new Intent(FanJuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", HomeFragment.FLAG_GOTOBIGTUAN);
            startActivity(intent);
        }

        @JavascriptInterface
        public void ftJyInvest() {
            Intent intent = new Intent(FanJuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", HomeFragment.FLAG_GOTOJYTUAN);
            startActivity(intent);
        }

        @JavascriptInterface
        public void shareFanJu(String shareUrl, String shareTitle, String shareDesc, String shareLogo) {
            LogUtils.i("从后台拿到的活动分享数据====" + shareUrl + "===" + shareTitle + "===" + shareDesc + "===" + shareLogo);
            if (shareUrl != null && shareTitle != null && shareDesc != null && shareLogo != null) {

                FragmentManager fm = getSupportFragmentManager();
                ShareFragFanJu shareFragFanJu = new ShareFragFanJu();
                shareFragFanJu.show(fm, null);
                shareFragFanJu.setData(shareUrl, shareTitle, shareLogo, shareDesc);
            }
        }

        @JavascriptInterface
        public void gotoIndex() {
            LogUtils.i("从活动进入首页");
            //设置标记，其他情况无论在那个界面停留时点击通知栏都进入到首页
//            ConstantUtils.touziflag = 0;
            Intent intent = new Intent(FanJuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onResume() {//登录后回来需要刷新界面
        // TODO Auto-generated method stub
        super.onResume();
        initdata();
    }

    @Override
    public void onBackPressed() {//webview进入二级界面按物理返回键的处理
        if (null != webview && webview.canGoBack()) {  //表示按返回键时的操作
            webview.goBack();   //后退
        } else {
            finish();
        }
    }
}
