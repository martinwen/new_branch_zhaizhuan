package com.iqianbang.fanpai.activity.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.DeviceUtil;
import com.iqianbang.fanpai.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignForNotCoreActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webview)
    WebView webview;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signfornotcore);
        ButterKnife.bind(this);
        initData();
    }

    public void initData() {
        String token = CacheUtils.getString("token", "");
        try {
            url = getIntent().getStringExtra("coreUserUrl")+"?token="+ URLEncoder.encode(token, "UTF-8")+"&version="+ DeviceUtil.APP_VERSIONNAME;
            LogUtils.i("url="+url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initwebview();
    }

    @SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
    private void initwebview() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //开启本地dom缓存
        webSettings.setDomStorageEnabled(true);

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
        });
        webview.loadUrl(url);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
