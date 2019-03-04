package com.iqianbang.fanpai.fragment.sanbiao;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.fragment.BaseFragment;
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

public class ZhaiQuanFragment extends BaseFragment {

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
//    @BindView(R.id.tv_repay_method)
//    TextView tvRepayMethod;
//    @BindView(R.id.tv_repay_source)
//    TextView tvRepaySource;
//    @BindView(R.id.ll_repay_source)
//    LinearLayout llRepaySource;
//    @BindView(R.id.tv_risk_control)
//    TextView tvRiskControl;
//    @BindView(R.id.ll_risk_control)
//    LinearLayout llRiskControl;
//    @BindView(R.id.tv_check)
//    TextView tvCheck;

    private CustomProgressDialog progressdialog;
    private String from;
    private String bid;
    private String matchId;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_bid_info, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        getDataFromServer();
    }

    private void getDataFromServer() {
        progressdialog.show();
        String token = CacheUtils.getString("token", "");
        Map<String, String> map = SortRequestData.getmap();
        map.put("bid", bid);
        map.put("matchId", matchId);//散标必须传此字段
        map.put("proCode", from);//散标和转入专区必须传此字段
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWBORROWDETAILINFO_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("（集合标）债权详情==" + string);
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

    public void setBorrowId(String bid) {
        this.bid = bid;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
