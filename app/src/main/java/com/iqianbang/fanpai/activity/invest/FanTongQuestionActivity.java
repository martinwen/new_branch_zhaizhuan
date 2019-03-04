package com.iqianbang.fanpai.activity.invest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.DeviceUtil;
import com.iqianbang.fanpai.utils.LogUtils;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_title;

public class FanTongQuestionActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(tv_title)
    TextView tvTitle;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webview)
    WebView webview;
    private String minBookAmount;
    private String maxBookAmount;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantongquestion);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        minBookAmount = intent.getStringExtra("minBookAmount");
        maxBookAmount = intent.getStringExtra("maxBookAmount");

        String token = CacheUtils.getString("token", null);

        try {
            url = ConstantUtils.TOANSWERPAGE_URL+ "?clientType=5" + "&token=" + URLEncoder.encode(token, "UTF-8");

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
        webview.addJavascriptInterface(new JavaScriptInterface(), "fanfan");
        //开启本地dom缓存
        webSettings.setDomStorageEnabled(false);
        webSettings.setAppCacheEnabled(false);


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
            /**
             * 网页加载标题回调
             * @param view
             * @param title 网页标题
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                tvTitle.setText(title);
            }


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


    class JavaScriptInterface {


        JavaScriptInterface() {
        }

        @JavascriptInterface
        public void yuyue() {
            LogUtils.i("回答正确进入到饭桶预约界面");
            Intent intent = new Intent(FanTongQuestionActivity.this, FanTongYuYueActivity.class);
            intent.putExtra("minBookAmount", minBookAmount);
            intent.putExtra("maxBookAmount", maxBookAmount);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void ftinvest() {
            LogUtils.i("三次答题机会用完了就会调用此方法，跳到首页");
            Intent intent = new Intent(FanTongQuestionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
