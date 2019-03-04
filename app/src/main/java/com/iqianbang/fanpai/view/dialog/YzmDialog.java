package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.volley.BaseUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

public class YzmDialog extends Dialog implements
		android.view.View.OnClickListener {

	private String phone;
	private ImageView iv_yanzhengma_code;//验证码图片
	private EditText et_answer;//答案输入框
    private OnYzmDialogDismissListener listener;
    
	public YzmDialog(Context context) {
		super(context);
	}
	
	public YzmDialog(Context context, int theme) {
		super(context, theme);
	}

	public YzmDialog(Context context, int theme, String phone) {
		super(context, theme);
		this.phone = phone;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_yzm);
		
		initDialog();
		//访问网络，获取验证码图片
		Map<String, String> map = SortRequestData.getmap();
		
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		String sParams = BaseUtil.mapToStringParams(map);
		String url= ConstantUtils.CAPTCHA_URL + "?" + sParams;
		ImageLoader.getInstance().displayImage(url, iv_yanzhengma_code);
	}
	
	
  
	private void initDialog() {
		TextView btnOk = (TextView) findViewById(R.id.btn_ok);
		TextView btnCancel = (TextView) findViewById(R.id.btn_cancel);
		iv_yanzhengma_code = (ImageView) findViewById(R.id.iv_yanzhengma_code);//验证码图片
		et_answer = (EditText) findViewById(R.id.et_answer);//答案输入框
		
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		iv_yanzhengma_code.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			
			String answer = et_answer.getText().toString().trim();
			if (!TextUtils.isEmpty(answer)) {
				dismiss();
				if (listener != null) {
					listener.OnYzmDialogDismiss(answer);
				}
			}else {
				ToastUtils.toastshort("输入不正确");
			}
			break;
		case R.id.btn_cancel:
			dismiss();
			break;
		case R.id.iv_yanzhengma_code:
			//访问网络，获取验证码图片
			Map<String, String> map = SortRequestData.getmap();
			
			String requestData = SortRequestData.sortString(map);
			String signData = SignUtil.sign(requestData);
			map.put("sign", signData);
			String sParams = BaseUtil.mapToStringParams(map);
			String url= ConstantUtils.CAPTCHA_URL + "?" + sParams;
			ImageLoader.getInstance().displayImage(url, iv_yanzhengma_code);
			break;

		default:
			break;
		}
	}
	public interface OnYzmDialogDismissListener{
    	void OnYzmDialogDismiss(String answer);
    }
	
	public void setOnYzmDialogDismissListener(OnYzmDialogDismissListener listener) {
		this.listener = listener;
	}
}

