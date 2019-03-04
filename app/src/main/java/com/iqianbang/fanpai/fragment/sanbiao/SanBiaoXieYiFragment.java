package com.iqianbang.fanpai.fragment.sanbiao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.NomalWebviewActivity;
import com.iqianbang.fanpai.fragment.BaseFragment;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author lijinliu
 * @date 20180129
 * 标的项目介绍
 */

public class SanBiaoXieYiFragment extends BaseFragment {

    private WebView mWebview;
    private ProgressBar mProgressBar;
    private String mUrl;
    private String matchId;
    private String from;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_dftxieyi, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mWebview = (WebView) view.findViewById(R.id.webview);
        return view;
    }

    @Override
    public void initData() {
        String token = CacheUtils.getString("token", "");
        try {
            mUrl = ConstantUtils.GOAGREEMENTORIGHT_URL + "?token=" + URLEncoder.encode(token, "UTF-8")
                    + "&matchId="+matchId  + "&proCode=" + from + "&clientType=5";

            LogUtils.i("url=="+mUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initwebview();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initwebview() {

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //没有缓存（防止从第二个界面goback时闪现白屏现象）
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!mUrl.equals(url)) {
                    Intent intent = new Intent(mActivity,NomalWebviewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                    return true;
                }

                mWebview.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mProgressBar.setVisibility(View.GONE);
                mWebview.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mWebview.setVisibility(View.VISIBLE);
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebview.loadUrl(mUrl);
    }

    public void setData(String matchId) {
        this.matchId = matchId;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
