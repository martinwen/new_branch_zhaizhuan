package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.activity.invest.BidInfoActivity;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.ZhuanRangCheXiaoDialog;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SanBiaoProjectActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_zhuanrang_ing)
    TextView tvZhuanrangIng;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.tv_money_buy)
    TextView tvMoneyBuy;
    @BindView(R.id.tv_yuji)
    TextView tvYuji;
    @BindView(R.id.tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_yijie)
    TextView tvYijie;

    @BindView(R.id.rl_time)
    RelativeLayout rlTime;
    @BindView(R.id.tv_huikuan_day)
    TextView tvHuikuanDay;

    @BindView(R.id.tv_time_chujie)
    TextView tvTimeChujie;

    @BindView(R.id.ll_zhuanrangshijian)
    LinearLayout llZhuanrangshijian;
    @BindView(R.id.tv_zhuanrang)
    TextView tvZhuanrang;
    @BindView(R.id.tv_zhuanrang_value)
    TextView tvZhuanrangValue;
    @BindView(R.id.iv_line_zhuanrangshijian)
    View ivLineZhuanrangshijian;

    @BindView(R.id.ll_daoqi)
    LinearLayout llDaoqi;
    @BindView(R.id.tv_daoqi)
    TextView tvDaoqi;
    @BindView(R.id.tv_daoqi_value)
    TextView tvDaoqiValue;
    @BindView(R.id.iv_line_daoqi)
    View ivLineDaoqi;

    @BindView(R.id.ll_zhuanrang)
    LinearLayout llZhuanrang;
    @BindView(R.id.iv_line_zhuanrang)
    View ivLineZhuanrang;

    @BindView(R.id.ll_zhaiquan)
    LinearLayout llZhaiquan;
    @BindView(R.id.iv_line_zhaiquan)
    View ivLineZhaiquan;

    @BindView(R.id.ll_zijin)
    LinearLayout llZijin;

    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;


    private CustomProgressDialog progressdialog;
    private String transSeq;
    private String transUuid;
    private String id;
    private String bid;
    private JSONArray activityList;
    private String status;
    private String holdMoney;//可撤销金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanbiao_project);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "");
        initData();
    }

    private void initData() {

        //访问网络，初始化界面数据
        id = getIntent().getStringExtra("id");
        getDataFromServer();
    }

    @OnClick({R.id.iv_back, R.id.tv_rate, R.id.ll_zhaiquan, R.id.ll_zhuanrang, R.id.ll_zijin
              ,R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_rate:
                String[] listdata = new String[activityList.size()];
                if (activityList.size() > 0) {
                    for (int i = 0; i < activityList.size(); i++) {
                        JSONObject json = (JSONObject) activityList.get(i);
                        String name = json.getString("name");
                        String activityRate = json.getString("activityRate");
                        listdata[i] = name + "：" + activityRate + "%";
                    }
                }

                // 初始化Listview
                ListView listView = initListview(listdata);

                PopupWindow pop = new PopupWindow(listView, LockPatternUtil.dip2px(this, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(tvRate, 0, 8);
                break;
            case R.id.ll_zhuanrang:
                Intent intent = new Intent(this, ZhuanRangJiLuActivity.class);
                intent.putExtra("transSeq", transSeq);
                startActivity(intent);
                break;
            case R.id.ll_zhaiquan:
                intent = new Intent(this, SanBiaoZhaiQuanActivity.class);
                intent.putExtra("from", "scatter");
                intent.putExtra("bid", bid);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.ll_zijin:
                intent = new Intent(this, SanBiaoLiuShuiActivity.class);
                intent.putExtra("transUuid", transUuid);
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                if ("10".equals(status)) {//10  持有中  可转让
                    intent = new Intent(this, ZhuanRangActivity.class);
                    intent.putExtra("from", "scatter");
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

                if ("20".equals(status)) {//20 转让中   撤回
                    ZhuanRangCheXiaoDialog zhuanRangCheXiaoDialog = new ZhuanRangCheXiaoDialog(this, R.style.YzmDialog, id, transSeq, holdMoney, "scatter");
                    zhuanRangCheXiaoDialog.show();
                }
                break;
        }
    }

    private String[] insertStr(String[] arr, String str) {
        int size = arr.length;
        String[] tmp = new String[size + 1];
        System.arraycopy(arr, 0, tmp, 0, size);
        tmp[size] = str;
        return tmp;
    }

    private ListView initListview(String[] listdata) {
        ListView listView = new ListView(this);
        // 去掉点击效果
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去掉分割线
        listView.setDividerHeight(0);
        // 去掉滑动块
        listView.setVerticalScrollBarEnabled(false);
        // 设置背景
        listView.setBackgroundResource(R.drawable.shape_jiaxi_popbg);
        // 设置Adapter
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.jiaxi_pop, R.id.tv_rate, listdata));

        return listView;
    }

    private void getDataFromServer() {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("scatterInvestId", id);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.SHOWSCATTERINVESTRECORDINFODETAIL_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("出借记录明细===" + string);
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
                                    //投资记录名称
                                    String borrowName = data.getString("borrowName");
                                    tvName.setText(borrowName);
                                    //出借金额
                                    String investAmount = data.getString("investAmount");
                                    tvMoneyBuy.setText(investAmount + "元");
                                    //历史年化
                                    tvRate.setText(data.getString("yearRate") + "%");
                                    //已结收益
                                    tvYijie.setText(data.getString("hasClearIncome") + "元");
                                    //是否显示下一个付息日
                                    String nextRepayDay = data.getString("nextRepayDay");
                                    if (TextUtils.isEmpty(nextRepayDay)) {
                                        rlTime.setVisibility(View.GONE);
                                    } else {
                                        tvHuikuanDay.setText("下一个付息日：" + nextRepayDay);
                                    }
                                    //出借时间
                                    String startTimeStr = data.getString("startTimeStr");
                                    tvTimeChujie.setText(startTimeStr);


                                    //10  持有中  20 转让中  30 已转让 40 已结清   50 承接结清
                                    status = data.getString("status");
                                    if ("10".equals(status)) {//xml已默认填好
                                        tvDaoqiValue.setText(data.getString("endTimeStr"));
                                        //隐藏转让时间
                                        llZhuanrangshijian.setVisibility(View.GONE);
                                        ivLineZhuanrangshijian.setVisibility(View.GONE);

                                        if(data.getBoolean("proCanTransfer")){//true | false  true 允许转让 ，false不允许转让
                                            tvBtnFinish.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if ("20".equals(status)) {
                                        tvZhuanrangIng.setVisibility(View.VISIBLE);//转让中
                                        //转让时间
                                        tvZhuanrangValue.setText(data.getString("transferTimeStr"));
                                        //到期时间
                                        tvDaoqiValue.setText(data.getString("endTimeStr"));
                                        tvBtnFinish.setText("撤回");
                                        tvBtnFinish.setVisibility(View.VISIBLE);
                                    }
                                    if ("30".equals(status)) {
                                        ivStatus.setImageResource(R.drawable.zaitou_yizhuanrang);
                                        //转让成功时间
                                        tvZhuanrang.setText("转让成功");
                                        tvZhuanrangValue.setText(data.getString("transferSuccessTimeStr"));
                                        //隐藏转让记录
                                        llDaoqi.setVisibility(View.GONE);
                                        ivLineDaoqi.setVisibility(View.GONE);
                                    }
                                    if ("40".equals(status)||"60".equals(status)) {
                                        if("40".equals(status)){
                                            ivStatus.setImageResource(R.drawable.zaitou_jieqing);
                                        }
                                        if("60".equals(status)){
                                            ivStatus.setImageResource(R.drawable.zaitou_jieqing_tiqian);
                                        }
                                        //隐藏转让时间
                                        llZhuanrangshijian.setVisibility(View.GONE);
                                        ivLineZhuanrangshijian.setVisibility(View.GONE);
                                        tvDaoqi.setText("结清时间");
                                        tvDaoqiValue.setText(data.getString("settlementTimeStr"));
                                        //隐藏债权信息
                                        llZhaiquan.setVisibility(View.GONE);
                                        ivLineZhaiquan.setVisibility(View.GONE);
                                    }
                                    if ("50".equals(status)) {
                                        ivStatus.setImageResource(R.drawable.zaitou_jieqing_zhuanrang);
                                        //转让成功时间
                                        tvZhuanrang.setText("转让成功");
                                        tvZhuanrangValue.setText(data.getString("transferSuccessTimeStr"));
                                        tvDaoqi.setText("结清时间");
                                        tvDaoqiValue.setText(data.getString("settlementTimeStr"));
                                        //隐藏债权信息
                                        llZhaiquan.setVisibility(View.GONE);
                                        ivLineZhaiquan.setVisibility(View.GONE);

                                    }

                                    if(data.getDouble("bookClearAmount") > 0){
                                        tvYuji.setText("已结本金");
                                        tvLeiji.setText(data.getString("bookClearAmount") + "元");
                                    }else{
                                        tvYuji.setText("预计总收益");
                                        tvLeiji.setText(data.getString("recInterest") + "元");
                                    }

                                    if(data.getDouble("transNum") <= 0){
                                        //隐藏转让记录
                                        llZhuanrang.setVisibility(View.GONE);
                                        ivLineZhuanrang.setVisibility(View.GONE);
                                    }


                                    //散标id
                                    id = data.getString("id");

                                    //债权id
                                    bid = data.getString("bid");

                                    //transSeq，转让记录用到
                                    transSeq = data.getString("transSeq");

                                    //transUuid，资金明细用到
                                    transUuid = data.getString("transUuid");

                                    //可撤销金额
                                    holdMoney = data.getString("holdMoney");

                                    activityList = JSONArray.parseArray(data.getString("rateList"));

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
