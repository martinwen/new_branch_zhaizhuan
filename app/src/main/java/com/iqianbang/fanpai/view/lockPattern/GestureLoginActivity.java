package com.iqianbang.fanpai.view.lockPattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.MainActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.GestureCountDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.List;
import java.util.Map;


/**
 * Created by Sym on 2015/12/24.
 */
public class GestureLoginActivity extends BaseActivity {

    private LockPatternView lockPatternView;
    private TextView messageTv;
    private TextView tv_user;
    private TextView forgetGesture;
    private TextView other;

    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;
    private String gesturePassword_string;
    private int count = 5;
    private String phone;
    private String password;
    private boolean gestureDisable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesturelogin);
        initView();
    }

    private void initView() {
    	//获取到注册时的手机号和登录密码
    	phone = CacheUtils.getString(CacheUtils.LOGINPHONE,"");
    	password = CacheUtils.getString(CacheUtils.LOGINPASSWORD,"");
    	
    	lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
    	messageTv = (TextView) findViewById(R.id.messageTv);
    	
    	//忘记手势密码
    	forgetGesture = (TextView) findViewById(R.id.forgetGesture);
    	forgetGesture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(GestureLoginActivity.this, LoginActivity.class);
			      startActivity(intent);
			}
		});
    	
    	//其他账号登录
    	other = (TextView) findViewById(R.id.other);
    	other.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(GestureLoginActivity.this, LoginActivity.class);
			      startActivity(intent);
			}
		});
    	
    	tv_user = (TextView) findViewById(R.id.tv_user);//手机号
    	tv_user.setText(fixNum(phone));
    	
        //得到当前用户的手势密码
    	gesturePassword_string = CacheUtils.getString(CacheUtils.BYTE,"");
    	gesturePassword = gesturePassword_string.getBytes();
        lockPatternView.setOnPatternListener(patternListener);
        //震动效果
//      lockPatternView.setTactileFeedbackEnabled(true);
        updateStatus(Status.DEFAULT);
    }

	//手机号码星号处理
	private String fixNum(String phone) {
		return phone = phone.substring(0,3) + "****" + phone.substring(7);
	}

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if(pattern != null){
                if(LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                	count--;
                    if (count<=0) {
                    	CacheUtils.putString(CacheUtils.BYTE, "");
                    	gestureDisable = true;
                    	CacheUtils.putBoolean("gestureDisable", gestureDisable);

						GestureCountDialog gestureCountDialog = new GestureCountDialog(GestureLoginActivity.this, R.style.YzmDialog);
						gestureCountDialog.setCanceledOnTouchOutside(false);
						gestureCountDialog.setCancelable(false);
						gestureCountDialog.setOnGestureCountDialogDismissListener(new GestureCountDialog.OnGestureCountDialogDismissListener() {

							@Override
							public void OnGestureCountDialogDismiss() {
								Intent intent = new Intent(GestureLoginActivity.this, LoginActivity.class);
								intent.setFlags(10);
								startActivity(intent);
								finish();
								// 访问网络，告知服务端用户已退出登录
								getDataFromServer();
							}
						});
						gestureCountDialog.show();
					}
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

	protected void getDataFromServer() {

		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", null);
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.LOGOUT_URL, null,
				map, new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						// TODO Auto-generated method stub
						LogUtils.i("调了退出登录的接口");
						LogUtils.i("string=="+string);
						JSONObject json = JSON.parseObject(string);
						String token = json.getString("token");
						CacheUtils.putString("token", token);

					}

					@Override
					public void onError(VolleyError error) {

					}
				});
	}

  //重写返回键，因为程序在后台超过三分钟后再次回到前台时会出现手势密码登录界面，为了防止用户按返回键直接进入
  	@Override
  	public void onBackPressed() {
  		Intent intent = getIntent();
  		int flags = intent.getFlags();
  		if (flags==10000) {
			LogUtils.i("flags==10000");
			return;
		}
  		super.onBackPressed();
  	}
  	
    /**
     * 更新状态
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
            	if (count<=0) {
            		messageTv.setText("输入错误超过5次咯");
				}else {
					messageTv.setText("密码错误，你还有"+count+"次解锁机会");
				}
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.OK);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
    	
		Map<String, String> map =  SortRequestData.getmap();
		map.put("phone", phone);
		map.put("password", password);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
      
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.MIMA_LOGIN_URL, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {

						JSONObject json = JSON.parseObject(string);
						String code= json.getString("code");
						if("0".equals(code)){
							String token = json.getString("token");
							CacheUtils.putString("token",token);
						    Intent intent=new Intent(GestureLoginActivity.this,MainActivity.class);
						    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
						    startActivity(intent);
						    finish();
						}else{
							String msg = json.getString("msg");
							ToastUtils.toastshort("手势密码："+msg);
							Intent intent=new Intent(GestureLoginActivity.this,MainActivity.class);
						    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
						    startActivity(intent);
						    finish();
						}
					}

					@Override
					public void onError(VolleyError error) {
						Intent intent=new Intent(GestureLoginActivity.this,MainActivity.class);
					    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
					    startActivity(intent);
					    finish();
					}
				});
    }


    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.text_graycolor),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.bg_title_red),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.text_graycolor);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }
}
