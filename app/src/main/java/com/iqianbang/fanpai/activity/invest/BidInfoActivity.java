package com.iqianbang.fanpai.activity.invest;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
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

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BidInfoActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_value)
    TextView tvNameValue;
    @BindView(R.id.tv_idcard)
    TextView tvIdcard;
    @BindView(R.id.tv_idcard_value)
    TextView tvIdcardValue;
    @BindView(R.id.tv_borrow_money)
    TextView tvBorrowMoney;
    @BindView(R.id.ll_borrow_money)
    LinearLayout llBorrowMoney;
    @BindView(R.id.tv_borrow_days)
    TextView tvBorrowDays;
    @BindView(R.id.ll_borrow_days)
    LinearLayout llBorrowDays;
    @BindView(R.id.tv_interest_day)
    TextView tvInterestDay;
    @BindView(R.id.ll_interest)
    LinearLayout llInterest;

    private CustomProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_info);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "数据加载中...");
        //加载数据
        String bid = getIntent().getStringExtra("bid");
        String matchId = getIntent().getStringExtra("id");
        initData(bid,matchId);
    }

    private void initData(String bid,String matchId) {
        progressdialog.show();
        String token = CacheUtils.getString("token", "");
        Map<String, String> map = SortRequestData.getmap();
        map.put("bid", bid);
        map.put("matchId", matchId);//散标必须传此字段
        if(!TextUtils.isEmpty(matchId)){
            map.put("proCode", "scatter");//散标必须传此字段
        }
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWBORROWDETAILINFO_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("债权详情==" + string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign,
                                        datastr);
                                if (isSuccess) {// 验签成功
                                    JSONObject data = JSON.parseObject(datastr);

                                    // 标的名称
                                    String borrowName = data.getString("borrowName");
                                    tvTitle.setText(borrowName);

                                    // 借款人姓名
                                    String userName = data.getString("userName");
                                    tvNameValue.setText(userName);

                                    // 借款人身份证号码
                                    String idcard = data.getString("idcard");
                                    tvIdcardValue.setText(idcard);

                                    // 借款金额
                                    String borrowMoney = data.getString("borrowMoney");
                                    tvBorrowMoney.setText(borrowMoney + "元");

                                    // 借款期限
                                    String borrowDays = data.getString("borrowDays");
                                    tvBorrowDays.setText(borrowDays + "天");

                                    // 担保公司名称
                                    String companyName = data.getString("companyName");
                                    if (TextUtils.isEmpty(companyName)) {
                                        llInterest.setVisibility(View.GONE);
                                    } else {
                                        tvInterestDay.setText(companyName);
                                    }


                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
