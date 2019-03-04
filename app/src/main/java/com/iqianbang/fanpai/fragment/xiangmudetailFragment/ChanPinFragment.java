package com.iqianbang.fanpai.fragment.xiangmudetailFragment;

import android.text.TextUtils;
import android.view.View;
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

/**
 * Created by wenguangjun on 2017/3/2.
 */

public class ChanPinFragment extends BaseFragment {

    @BindView(R.id.tv_txt1)
    TextView tvTxt1;
    @BindView(R.id.tv_txt4)
    TextView tvTxt4;
    @BindView(R.id.tv_txt5)
    TextView tvTxt5;
    @BindView(R.id.tv_txt7)
    TextView tvTxt7;
    @BindView(R.id.tv_txt8)
    TextView tvTxt8;
    @BindView(R.id.tv_txt9)
    TextView tvTxt9;
    @BindView(R.id.tv_txt10)
    TextView tvTxt10;

    private CustomProgressDialog progressdialog;
    private String borrowId;

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_xiangmu_chanpin, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        progressdialog = new CustomProgressDialog(mActivity, "正在加载数据...");
        getDataFromServer(borrowId);
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    private void getDataFromServer(String borrowId) {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        map.put("borrowId", borrowId);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.LOANDETAIL_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("饭盒出借项目===" + string);
                        // TODO Auto-generated method stub
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

                                    //项目名称
                                    String borrowName = data.getString("borrowName");
                                    tvTxt1.setText(borrowName);

                                    //募集对象
                                    String raiseObj = data.getString("raiseObj");
                                    tvTxt4.setText(raiseObj);

                                    //募集资金
                                    String raiseTotalAmount = data.getString("raiseTotalAmount");
                                    tvTxt5.setText(raiseTotalAmount+"元");

                                    //产品到期日
                                    String deadlineStr = data.getString("deadlineStr");
                                    tvTxt7.setText(deadlineStr);

                                    //项目概况
                                    String proOverview = data.getString("proOverview");
                                    tvTxt8.setText(proOverview);

                                    //转让条件
                                    String transConditions = data.getString("transConditions");
                                    tvTxt9.setText(transConditions);

                                    //保障方式
                                    String guaranteeWay = data.getString("guaranteeWay");
                                    tvTxt10.setText(guaranteeWay);

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
                        // TODO Auto-generated method stub
                        progressdialog.dismiss();
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

}
