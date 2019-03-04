package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.registerandlogin.LoginActivity;
import com.iqianbang.fanpai.global.FanPaiApplication;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.view.dialog.GestureCountDialog;
import com.iqianbang.fanpai.view.dialog.GestureDialog;
import com.iqianbang.fanpai.view.lockPattern.GestureCreateActivity;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.view.lockPattern.LockPatternView;

import java.util.List;


public class GestureActivity extends BaseActivity implements OnClickListener {

//    private static final String TAG = "LoginGestureActivity";

    private LockPatternView lockPatternView;
    private TextView messageTv;
    private TextView forgetGesture;
    private ImageView iv_back;

//    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;
    private String gesturePassword_string;
    private int count = 5;
    private boolean gestureDisable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gesture);
        
        initView();
    }

    private void initView() {
    	lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
    	messageTv = (TextView) findViewById(R.id.messageTv);
    	forgetGesture = (TextView) findViewById(R.id.forgetGesture);
    	forgetGesture.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    	
    	
//        aCache = ACache.get(GestureActivity.this);
        //得到当前用户的手势密码
//        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
    	gesturePassword_string = CacheUtils.getString(CacheUtils.BYTE,"");
    	gesturePassword = gesturePassword_string.getBytes();
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
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
//						aCache.clear();
                    	CacheUtils.putString("token", null);
                    	CacheUtils.putString(CacheUtils.BYTE, "");
                    	gestureDisable = true;
                    	CacheUtils.putBoolean("gestureDisable", gestureDisable);
                    	
						GestureCountDialog gestureCountDialog = new GestureCountDialog(GestureActivity.this, R.style.YzmDialog);
						gestureCountDialog.setCanceledOnTouchOutside(false);
						gestureCountDialog.setCancelable(false);
						gestureCountDialog.setOnGestureCountDialogDismissListener(new GestureCountDialog.OnGestureCountDialogDismissListener() {
							
							@Override
							public void OnGestureCountDialogDismiss() {
								Intent intent = new Intent(GestureActivity.this,LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
								startActivity(intent);
								finish();
							}
						});
						gestureCountDialog.show();
					}
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

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
            		messageTv.setText("已超过错误次数限制");
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
//        Toast.makeText(GestureActivity.this, "success", Toast.LENGTH_SHORT).show();
    	FanPaiApplication.mainHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				finish();
				startActivity(new Intent(GestureActivity.this,GestureCreateActivity.class));
			}
		}, 1000);
    	//dialog消失跳转到手势密码设置
    }

    /**
     * 忘记手势密码（去账号登录界面）
     */
    @Override
	public void onClick(View v) {
      GestureDialog gestureDialog = new GestureDialog(this, R.style.YzmDialog);
      gestureDialog.setOnGestureDialogDismissListener(new GestureDialog.OnGestureDialogDismissListener() {
		
		@Override
		public void OnGestureDialogDismiss() {
			finish();
	        Intent intent = new Intent(GestureActivity.this, GestureCreateActivity.class);
	        startActivity(intent);
		}
	});
      gestureDialog.show();
	}

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_fix_default, R.color.text_graycolor),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.bg_title_red),
        //密码输入正确
        CORRECT(R.string.gesture_fix_default, R.color.text_graycolor);

        Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }
}
