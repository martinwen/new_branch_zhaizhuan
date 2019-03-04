package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.YouXianGouActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;


public class YouXianGouDuiHuanDialog extends Dialog implements
		View.OnClickListener {

	private EditText et_content;
	private TextView tv_msg;
	private String regex = "^[0-9A-Za-z]{10}$"; //由10位数字或字母组成

	public YouXianGouDuiHuanDialog(Context context) {
		super(context);
	}

	public YouXianGouDuiHuanDialog(Context context, int theme) {
		super(context, theme);
	}

	public YouXianGouDuiHuanDialog(Context context, int theme, String msg) {
		super(context, theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_youxiangouduihuan);
		
		initDialog();
	}
	
	
  
	private void initDialog() {
		ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
		iv_close.setOnClickListener(this);
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(this);
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				tv_msg.setVisibility(View.GONE);
			}
		});

		tv_msg = (TextView) findViewById(R.id.tv_msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			 dismiss();
			 break;
		case R.id.btn_ok:
			String convertCode = et_content.getText().toString().trim();
			if(TextUtils.isEmpty(convertCode)){
				tv_msg.setVisibility(View.VISIBLE);
				tv_msg.setText("兑换码不能为空哦");
			}else{
				if(convertCode.matches(regex)){
					getTicket(convertCode);
				}else{
					tv_msg.setVisibility(View.VISIBLE);
					tv_msg.setText("您输入的兑换码不正确");
				}
			}

			 break;

		default:
			break;
		}
	}

	private void getTicket(String convertCode) {
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		// 如果没有登录，直接return，不访问网络了
		if (TextUtils.isEmpty(token)) {
			return;
		}
		map.put("code", convertCode);
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.PREFERTICKET_EXCHANGE_URL,
				null, map, new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("string=="+string);
						JSONObject json = JSON.parseObject(string);
						String code = json.getString("code");

						if ("0".equals(code)) {

							ToastUtils.toastshort("兑换成功！");
							//弹窗小时，刷新加息票界面
							dismiss();
							Intent intent = new Intent(getContext(), YouXianGouActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							getContext().startActivity(intent);
						} else {
							tv_msg.setVisibility(View.VISIBLE);
							String msg = json.getString("msg");
							tv_msg.setText(msg);

						}

					}

					@Override
					public void onError(VolleyError error) {
						// TODO Auto-generated method stub
						ToastUtils.toastshort("加载数据失败！");
					}

				});
	}
	
}

