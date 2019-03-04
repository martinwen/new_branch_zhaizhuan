package com.iqianbang.fanpai.fragment.xiangmudetailFragment;

import android.view.View;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.fragment.BaseFragment;
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

/**
 * Created by wenguangjun on 2017/3/2.
 */

public class XieYiFragment extends BaseFragment {

    @BindView(R.id.webview)
    WebView webview;
    private CustomProgressDialog progressdialog;
    private String matchId;
    private String proCode;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_xiangmu_xieyi, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        progressdialog.show();
        requestServer(matchId,proCode);
    }

    private void requestServer(String matchId, String proCode) {
        LogUtils.i("matchId=="+matchId+  "proCode=="+proCode);
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("matchId", matchId);
        map.put("proCode", proCode);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost1(ConstantUtils.CONTRACT_URL,
                null, JSON.toJSONString(map) , new HttpBackBaseListener() {

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

    public void setData(String matchId, String proCode) {
        this.matchId = matchId;
        this.proCode = proCode;
    }
}
