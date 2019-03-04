package com.iqianbang.fanpai.activity.user;

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
import android.widget.TextView;
import android.widget.Toast;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_title;

/**
 * @author lijinliu
 * @date 20180206
 * 打开网页-获取网页标题
 */
public class SanBiaoXieYiActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(tv_title)
    TextView tvTitle;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        String token = CacheUtils.getString("token", "");
        String matchId = getIntent().getStringExtra("matchId");
        try {
            url = ConstantUtils.GOAGREEMENTORIGHT_URL + "?token=" + URLEncoder.encode(token, "UTF-8")
                    + "&matchId="+matchId  + "&proCode=scatter" + "&clientType=5";
            LogUtils.i("url=="+url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initwebview();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressbar.setVisibility(View.GONE);
                webview.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(SanBiaoXieYiActivity.this, "网络出错，请稍后重试！", Toast.LENGTH_SHORT).show();
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
                tvTitle.setText(title);
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
    public void onBackPressed() {
        if (null != webview && webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }
}
