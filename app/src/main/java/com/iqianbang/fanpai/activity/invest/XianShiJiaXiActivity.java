
package com.iqianbang.fanpai.activity.invest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.DeviceUtil;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iqianbang.fanpai.R.id.tv_title;


public class XianShiJiaXiActivity extends BaseActivity {

	@BindView(R.id.iv_back)
	ImageView ivBack;
	@BindView(tv_title)
	TextView tvTitle;
	@BindView(R.id.progressbar)
	ProgressBar progressbar;
	@BindView(R.id.webview)
	WebView webview;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_xianshijiaxi);
		ButterKnife.bind(this);
		initData();
	}

	private void initData() {
		String token = CacheUtils.getString("token", "");
		try {
			url = getIntent().getStringExtra("fwLimitAddRateUrl");
			if (url.contains("?")) {
				url = url + "&clientType=5" + "&token=" + URLEncoder.encode(token, "UTF-8") + "&device_id=" + DeviceUtil.DEVICE_ID + "&system_type=android"
						+ "&system_version=" + DeviceUtil.OS_VERSION + "&app_version=" + DeviceUtil.APP_VERSIONNAME;
			} else {
				url = url + "?clientType=5" + "&token=" + URLEncoder.encode(token, "UTF-8") + "&device_id=" + DeviceUtil.DEVICE_ID + "&system_type=android"
						+ "&system_version=" + DeviceUtil.OS_VERSION + "&app_version=" + DeviceUtil.APP_VERSIONNAME;
			}
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

}
