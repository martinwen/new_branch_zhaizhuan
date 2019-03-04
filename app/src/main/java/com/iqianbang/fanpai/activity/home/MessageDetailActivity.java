package com.iqianbang.fanpai.activity.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.ImageView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.webview)
    WebView webview;
    private CustomProgressDialog progressdialog;
    private String id_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagedetail);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        progressdialog.show();

        initData();

    }

    private void initData() {
        id_items = getIntent().getStringExtra("id_items");
        requestServer(id_items);
    }

    private void requestServer(String id_items) {
        Map<String, String> map = SortRequestData.getmap();//保存提交的form表单参数
        String token = CacheUtils.getString("token", "");
        map.put("id", id_items);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost1(ConstantUtils.LIST_DETAIL_URL,
                null, JSON.toJSONString(map), new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("string=="+string);
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


    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
