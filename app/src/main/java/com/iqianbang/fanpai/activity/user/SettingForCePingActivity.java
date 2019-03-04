package com.iqianbang.fanpai.activity.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.DeviceUtil;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_title;

public class SettingForCePingActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(tv_title)
    TextView tvTitle;

    private String url;
    private CustomProgressDialog progressdialog;
    public static String FLAG_GOTOSMALLTUAN = "flag_gotosmalltuan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_ce_ping);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "");
        initdata();
    }

    private void initdata() {
        //清除cookie，防止两个用户用同一个手机登录参加活动，下一个用户依然显示上一个用户的信息
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();

        String token = CacheUtils.getString("token", "");
        try {
            url = ConstantUtils.EVALUATION_URL + "?clientType=5" + "&token=" + URLEncoder.encode(token, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initwebview();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initwebview() {

        webview.clearCache(true);
        webview.clearHistory();

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
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
                progressdialog.dismiss();
                webview.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressdialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressdialog.dismiss();
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                tvTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressdialog.show();
                super.onProgressChanged(view, newProgress);
            }
        });

        webview.loadUrl(url);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        if (null != webview && webview.canGoBack()) {  //表示按返回键时的操作
            webview.goBack();   //后退
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {//webview进入二级界面按物理返回键的处理
        if (null != webview && webview.canGoBack()) {  //表示按返回键时的操作
            webview.goBack();   //后退
        } else {
            finish();
        }
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
        public void gotoHome() {
            LogUtils.i("去首页");
            Intent intent = new Intent(SettingForCePingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void gotoInvest() {
            LogUtils.i("去出借页");
            Intent intent = new Intent(SettingForCePingActivity.this, MainActivity.class);
            intent.putExtra("from",FLAG_GOTOSMALLTUAN);
            startActivity(intent);
        }
    }
}
