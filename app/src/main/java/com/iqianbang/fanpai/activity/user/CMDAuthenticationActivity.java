package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;


/**
 * 确认出借完成之后进入徽商密码输入的网页
 * @author lxn
 */
public class CMDAuthenticationActivity extends BaseActivity {

	private CustomProgressDialog progressdialog;
	private WebView webview;
	private String rechargeAmount;
	private String seqNo;
	private String bindId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmbauthentication);
		
        
		progressdialog = new CustomProgressDialog(this, "正在加载数据...");
		progressdialog.show();
		
		
		initView();
		initData();
		
	}		
		private void initView() {
			
				
			webview = (WebView) findViewById(R.id.webview);
			WebSettings webSettings = webview.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webview.addJavascriptInterface(new JavaScriptInterface(), "bankCard");
		
		}

		private void initData() {
			rechargeAmount = getIntent().getStringExtra("rechargeAmount");
			seqNo=getIntent().getStringExtra("seqNo");
			bindId=getIntent().getStringExtra("bindId");
			requestServer(seqNo, bindId);
		}
		
	private void requestServer(String seqNo, String bindId) {
		LogUtils.i("访问绑卡 ==");
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		map.put("seqNo",seqNo);
		map.put("bindId",bindId);
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost1(ConstantUtils.GOTOAUTHCMB_URL,
				null, JSON.toJSONString(map) , new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("招商银行绑卡=="+string);
						webview.loadDataWithBaseURL(null, string, "text/html", "utf-8", null);
						progressdialog.dismiss();
					}

					@Override
					public void onError(VolleyError error) {
						// TODO Auto-generated method stub
						progressdialog.dismiss();
						ToastUtils.toastshort("网络出错！");
					}

				});
	}
		
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			finish();
			// true 不传递 false 传递
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 与webview 交互使用的
	 *
	 * @author lxn
	 *
	 */
	class JavaScriptInterface {


		JavaScriptInterface() {
		}

		@JavascriptInterface
		public void handleBindCardResult(String reutlt) {
			LogUtils.i("reutlt===="+reutlt);
			if ("true".equals(reutlt)) {
				Intent intent = new Intent(CMDAuthenticationActivity.this,ChongZhiConfirmActivity.class);
				intent.putExtra("rechargeAmount",rechargeAmount);
				intent.putExtra("seqNo",seqNo);
				startActivity(intent);
				finish();
			}else {
				ToastUtils.toastshort("添加确认中...");
				finish();
			}
		}
	}
}
