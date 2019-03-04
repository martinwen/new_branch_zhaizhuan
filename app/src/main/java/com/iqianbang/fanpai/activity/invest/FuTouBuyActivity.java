package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.bean.HongBaoBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.FuTouDialog;
import com.iqianbang.fanpai.view.dialog.FuTouInfoBuyDialog;
import com.iqianbang.fanpai.view.dialog.FuTouInfoYueDialog;
import com.iqianbang.fanpai.view.dialog.HasHongBaoOrJiaXiPiaoDialogForFWbuy;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FuTouBuyActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_shengyu)
    TextView tvShengyu;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_term)
    TextView tvTerm;

    @BindView(R.id.tv_jiaxi_rate)
    TextView tvJiaxiRate;
    @BindView(R.id.tv_jiaxi_day)
    TextView tvJiaxiDay;
    @BindView(R.id.tv_jiaxi_piao)
    TextView tvJiaxiPiao;
    @BindView(R.id.tv_hongbao)
    TextView tvHongbao;
    @BindView(R.id.tv_hongbao_piao)
    TextView tvHongbaoPiao;
    @BindView(R.id.iv_zhaizhuan)
    ImageView ivZhaizhuan;
    @BindView(R.id.iv_check_xieyi)
    ImageView ivCheckXieyi;
    @BindView(R.id.tv_xieyi_one)
    TextView tvXieyiOne;
    @BindView(R.id.tv_xieyi_two)
    TextView tvXieyiTwo;
    @BindView(R.id.tv_xieyi_three)
    TextView tvXieyiThree;
    @BindView(R.id.tv_btn_finish)
    TextView tvBtnFinish;
    @BindView(R.id.tv_money_futou)
    TextView tvMoneyFutou;
    @BindView(R.id.tv_money_zhuanru)
    TextView tvMoneyZhuanru;
    @BindView(R.id.tv_money_benxi)
    TextView tvMoneyBenxi;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_info)
    ImageView ivInfo;


    private CustomProgressDialog progressdialog;
    private PopupWindow pop;// 银行选择的弹窗
    private ListView listView;// 银行选择的listview
    private String seqNo;

    private boolean isXieYiChecked = true;// 协议已经勾上
    private String term;
    private String jiaxi = null;
    private boolean isZhaiZhuanOpen = true;// 债转勾上
    private String buyMoney;//输入的买入金额
    private JSONObject data;
    private String datastr;
    private ArrayList<HongBaoBean> listHongBao;
    private ArrayList<HongBaoBean> listHongBaoCanShow = new ArrayList<>();
    private int redBagTicketListCount = 0;
    private String borrowSalableBal;//剩余金额
    //加息票相关的
    private String fwAddRateId = "";
    private double jiaxipiaoValue = 0;
    private String jiaxipiaoDay = "";
    private String jiaxipiaoIsCheck = "";
    //红包相关的
    private String redbagTicketId = "";
    private String reward = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futou_buy);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在复投......");
        initData();
    }

    private void initData() {

        datastr = getIntent().getStringExtra("jsonData");
        data = JSON.parseObject(datastr);

        String isActivity = getIntent().getStringExtra("isActivity");
        if("0".equals(isActivity)){// 0 非活动标
            tvTitle.setText("复投");
            ivInfo.setVisibility(View.GONE);
        }else if("1".equals(isActivity)) {// 1  活动标
            tvTitle.setText("复投-活动专享");
            ivInfo.setVisibility(View.VISIBLE);
        }

        listHongBao = (ArrayList<HongBaoBean>) JSONArray.parseArray(
                data.getJSONArray("redBagTicketList").toJSONString(), HongBaoBean.class);

        //剩余金额
        borrowSalableBal = data.getString("borrowSalableBal");
        tvShengyu.setText(borrowSalableBal + "元");
        //结算利率
        jiaxi = data.getString("expectedRate");
        tvRate.setText(MathUtil.subString(jiaxi, 2) + "%");
        //锁定期
        term = data.getString("term");
        String lockExpireTime = data.getString("lockExpireTime");
        tvTerm.setText(term + "天（锁定期至" + lockExpireTime + "）");
        //复投金额
        buyMoney = data.getString("repeatMoney");
        tvMoneyFutou.setText(MathUtil.subString(buyMoney, 2) + "元");
        //转入余额
        String leftMoney = data.getString("leftMoney");
        tvMoneyZhuanru.setText(Html.fromHtml(leftMoney + "元" +
                "<font color='#919191'><small>（复投成功后转入账户余额）</small></font>"));
        //累计本息和
        String principalAndInterest = data.getString("principalAndInterest");
        tvMoneyBenxi.setText(principalAndInterest + "元");
        //加息票数量
        String addRateTicketListCount = data.getString("addRateTicketListCount");
        tvJiaxiPiao.setText(addRateTicketListCount + "张可用");

        //seqNo
        seqNo = data.getString("seqNo");


        if (!TextUtils.isEmpty(buyMoney)) {
            for (int i = 0; i < listHongBao.size(); i++) {
                if ((StrToNumber.strTodouble(buyMoney)) >=
                        StrToNumber.strTodouble(listHongBao.get(i).getUseCondition())) {
                    redBagTicketListCount++;
                    listHongBaoCanShow.add(listHongBao.get(i));
                }
            }

            tvHongbaoPiao.setText(redBagTicketListCount + "张可用");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 选完红包的处理
        if (1 == requestCode) {
            if (1 == resultCode) {
                reward = data.getStringExtra("reward");
                redbagTicketId = data.getStringExtra("redbagTicketId");

                //使用红包
                if (!TextUtils.isEmpty(reward)) {
                    tvHongbao.setText(MathUtil.subString(reward, 2) + "元");
                } else {
                    tvHongbao.setText("0.00元");
                }

            }
        }
        // 选完加息票的处理
        if (2 == requestCode) {
            if (2 == resultCode) {
                fwAddRateId = data.getStringExtra("fwAddRateId");
                LogUtils.i("选完加息票的fwAddRateId==" + fwAddRateId);
                jiaxipiaoValue = data.getDoubleExtra("jiaxipiaoValue", 0);
                jiaxipiaoDay = data.getStringExtra("jiaxipiaoDay");
                jiaxipiaoIsCheck = data.getStringExtra("jiaxipiaoIsCheck");

                jiaxi = MathUtil.subDouble((StrToNumber.strTodouble(JSON.parseObject(datastr).getString("expectedRate"))
                        + jiaxipiaoValue), 2) + "";
                tvRate.setText(jiaxi + "%");

                //1.与锁定期一致 0.不一致
                if ("1".equals(jiaxipiaoIsCheck)) {
                    tvJiaxiDay.setVisibility(View.VISIBLE);
                    tvJiaxiDay.setText("（可加息" + term + "天）");
                } else {
                    if (jiaxipiaoValue > 0) {
                        tvJiaxiDay.setVisibility(View.VISIBLE);
                        tvJiaxiDay.setText("（可加息" + jiaxipiaoDay + "天）");
                    } else {
                        tvJiaxiDay.setVisibility(View.GONE);
                    }
                }

                //选中加息票回来后改变加息票的值
                tvJiaxiRate.setText(MathUtil.subDouble(jiaxipiaoValue, 2) + "%");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.iv_back, R.id.tv_rate, R.id.iv_info, R.id.tv_jiaxi_piao
            , R.id.tv_hongbao_piao, R.id.iv_zhaizhuan, R.id.iv_check_xieyi,
            R.id.tv_xieyi_one, R.id.tv_xieyi_two, R.id.tv_xieyi_three, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_rate:
                JSONArray activityList = JSONArray.parseArray(data.getString("activityList"));
                String[] listdata = new String[activityList.size() + 1];
                //基础利率
                listdata[0] = "历史基础年化结算利率：" + data.getString("baseRate") + "%";
                //活动利率
                if (activityList.size() > 0) {
                    for (int i = 0; i < activityList.size(); i++) {
                        JSONObject json = (JSONObject) activityList.get(i);
                        String name = json.getString("name");
                        String activityRate = json.getString("activityRate");
                        listdata[i + 1] = name + "：" + activityRate + "%";
                    }
                }

                //若有加息票，得加上加息利率
                if (jiaxipiaoValue > 0) {
                    listdata = insertStr(listdata, "使用加息票：" + jiaxipiaoValue + "%");
                }

                // 初始化Listview
                listView = initListview(listdata);

                pop = new PopupWindow(listView, LockPatternUtil.dip2px(this, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(tvRate, LockPatternUtil.dip2px(this, -50), 8);
                break;
            case R.id.iv_info:
                FuTouInfoBuyDialog fuTouInfoBuyDialog = new FuTouInfoBuyDialog(this, R.style.YzmDialog,
                        data.getString("maxReturnAmount"), data.getString("minInvestAmount"), data.getString("scale"));
                fuTouInfoBuyDialog.show();
                break;
            case R.id.tv_hongbao_piao:
                if (!TextUtils.isEmpty(buyMoney)) {
                    Intent intent = new Intent(this, HongBaoUseActivity.class);
                    intent.putExtra("listHongBaoCanShow", (Serializable) listHongBaoCanShow);
                    intent.putExtra("redbagTicketId", redbagTicketId);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.tv_jiaxi_piao:
                if (data.getDouble("addRateTicketListCount") > 0) {
                    Intent intent = new Intent(this, JiaXiPiaoUseActivity.class);
                    intent.putExtra("jsonData", datastr);
                    intent.putExtra("fwAddRateId", fwAddRateId);
                    startActivityForResult(intent, 2);
                } else {
                    ToastUtils.toastshort("无可用加息票哦");
                }

                break;
            case R.id.iv_zhaizhuan:
                isZhaiZhuanOpen = !isZhaiZhuanOpen;
                if (isZhaiZhuanOpen) {//勾选账户余额
                    ivZhaizhuan.setImageResource(R.drawable.zhaizhuan_open);
                    tvBtnFinish.setBackgroundColor(getResources().getColor(R.color.text_red_dark));
                } else {//不勾选账户余额
                    ivZhaizhuan.setImageResource(R.drawable.zhaizhuan_close);
                    tvBtnFinish.setBackgroundColor(getResources().getColor(R.color.text_graycolor));
                }
                break;
            case R.id.iv_check_xieyi:
                isXieYiChecked = !isXieYiChecked;
                if (isXieYiChecked) {//勾选协议
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_ok);
                } else {//不勾选协议
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_no);
                }
                break;
            case R.id.tv_xieyi_one:
                Intent intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.tv_xieyi_two:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            case R.id.tv_xieyi_three:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type", "3");
                startActivity(intent);
                break;
            case R.id.tv_btn_finish:
                buy();
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
        listView = new ListView(this);
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

    private void buy() {
        if (!isZhaiZhuanOpen) {// 没有债转
            ToastUtils.toastlong("请授权平台锁定期结束后自动发起债权转让");
            return;
        }
        if (!isXieYiChecked) {// 没有勾选协议
            ToastUtils.toastshort("请同意出借协议");
            return;
        }
        if (TextUtils.isEmpty(buyMoney)) {// 充值金额为空
            ToastUtils.toastshort("出借金额不能为空");
            return;
        }
        // 复投金额超出了剩余金额
        if (StrToNumber.strTodouble(borrowSalableBal) < StrToNumber.strTodouble(buyMoney)) {
            ToastUtils.toastshort("复投金额超出了剩余金额");
            return;
        }

        if ((data.getDouble("addRateTicketListCount") > 0 && jiaxipiaoValue == 0) ||
                (listHongBaoCanShow.size() > 0 && TextUtils.isEmpty(redbagTicketId))) {
            HasHongBaoOrJiaXiPiaoDialogForFWbuy hasHongBaoOrJiaXiPiaoDialog = new HasHongBaoOrJiaXiPiaoDialogForFWbuy(
                    FuTouBuyActivity.this, R.style.YzmDialog, "您未使用红包或加息票，确定复投吗？");
            hasHongBaoOrJiaXiPiaoDialog.show();
            hasHongBaoOrJiaXiPiaoDialog.setOnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener(new HasHongBaoOrJiaXiPiaoDialogForFWbuy.
                    OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener() {
                @Override
                public void OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismiss() {

                    FuTouDialog fuTouDialog = new FuTouDialog(FuTouBuyActivity.this, R.style.YzmDialog,
                            "您确定要复投吗？");
                    fuTouDialog.show();
                    fuTouDialog.setOnDialogDismissListener(new FuTouDialog.OnDialogDismissListener() {
                        @Override
                        public void OnDialogDismiss() {
                            getDataFromServer();
                        }
                    });

                }
            });
        } else {
            FuTouDialog fuTouDialog = new FuTouDialog(FuTouBuyActivity.this, R.style.YzmDialog,
                    "您确定要复投吗？");
            fuTouDialog.show();
            fuTouDialog.setOnDialogDismissListener(new FuTouDialog.OnDialogDismissListener() {
                @Override
                public void OnDialogDismiss() {
                    getDataFromServer();
                }
            });
        }
    }


    private void getDataFromServer() {
        //获取uuid
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("seqNo", seqNo);
        map.put("fwAddRateId", fwAddRateId);
        map.put("redbagTicketId", redbagTicketId);
        map.put("isAutoTransfer", "1");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.TRANSCONFIRMINVEST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("复投立即买入===" + string);
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

                                    Intent intent = new Intent(FuTouBuyActivity.this, FuTouBuySuccessActivity.class);
                                    intent.putExtra("from", "ft_buy");
                                    intent.putExtra("zong_money", buyMoney);
                                    startActivity(intent);

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
                        ToastUtils.toastshort("网络请求失败");
                    }
                });
    }
}
