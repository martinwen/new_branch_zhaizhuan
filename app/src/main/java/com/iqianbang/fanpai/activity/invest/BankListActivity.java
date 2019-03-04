package com.iqianbang.fanpai.activity.invest;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.adapter.BankListAdapter;
import com.iqianbang.fanpai.bean.BankListBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BankListActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.listview)
    ListView listview;
    private CustomProgressDialog progressdialog;
    private ArrayList<BankListBean> list = new ArrayList<BankListBean>();
    private BankListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        ButterKnife.bind(this);

        initView();
        getDataFromServer();
    }

    private void initView() {
        progressdialog = new CustomProgressDialog(this, "正在获取数据...");
        adapter = new BankListAdapter(this, list);
        listview.setAdapter(adapter);
    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        progressdialog.show();
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.BANKLIST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("银行列表=" + string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {// 验签成功
                                    ArrayList<BankListBean> listadd = (ArrayList<BankListBean>) JSONArray.parseArray(datastr, BankListBean.class);
                                    list.addAll(listadd);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    ToastUtils.toastshort("银行列表获取异常");
                                }
                            }
                        } else {
                            ToastUtils.toastshort("银行列表获取失败");
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络请求失败");
                    }
                });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
