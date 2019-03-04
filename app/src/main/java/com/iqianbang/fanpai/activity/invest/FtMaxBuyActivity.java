package com.iqianbang.fanpai.activity.invest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.iqianbang.fanpai.view.dialog.BuyAllDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.view.dialog.HasHongBaoOrJiaXiPiaoDialogForFWbuy;
import com.iqianbang.fanpai.view.dialog.IKnowDialog;
import com.iqianbang.fanpai.view.dialog.JiangJinDialog;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FtMaxBuyActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_shengyu)
    TextView tvShengyu;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_term)
    TextView tvTerm;
    @BindView(R.id.et_buy)
    EditText etBuy;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.iv_check_zhanghu)
    ImageView ivCheckZhanghu;
    @BindView(R.id.tv_jiang)
    TextView tvJiang;
    @BindView(R.id.iv_check_jiang)
    ImageView ivCheckJiang;
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

    private CustomProgressDialog progressdialog;
    private PopupWindow pop;// 银行选择的弹窗
    private ListView listView;// 银行选择的listview
    private String seqNo;
    private String jiangjin;
    private boolean isJiangJinChecked = true;// 奖金没有勾上
    private boolean isXieYiChecked = true;// 协议已经勾上
    private String term;
    private String jiaxi = null;
    private String minInvestAmount;
    private String maxInvestAmount;
    private boolean isZhangHuChecked = true;// 账户余额勾上
    private boolean isZhaiZhuanOpen = true;// 债转勾上
    private String baseBal;//账户余额
    private String buyMoney;//输入的买入金额
    private JSONObject data;
    private String datastr;
    private ArrayList<HongBaoBean> listHongBao;
    private ArrayList<HongBaoBean> listHongBaoCanShow = new ArrayList<>();
    private int redBagTicketListCount = 0;
    private String borrowSalableBal;
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
        setContentView(R.layout.activity_buy_ftmax);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();
    }

    private void initData() {

        datastr = getIntent().getStringExtra("jsonData");
        data = JSON.parseObject(datastr);
        listHongBao = (ArrayList<HongBaoBean>) JSONArray.parseArray(
                data.getJSONArray("redBagTicketList").toJSONString(), HongBaoBean.class);

        //剩余金额
        borrowSalableBal = data.getString("borrowSalableBal");
        tvShengyu.setText(borrowSalableBal + "元");
        //结算利率
        jiaxi = data.getString("expectedRate");
        tvRate.setText(MathUtil.subString(jiaxi,2) + "%");
        //锁定期
        term = data.getString("term");
        String lockExpireTime = data.getString("lockExpireTime");
        tvTerm.setText(term + "天（锁定期至" + lockExpireTime + "）");
        //账户余额
        baseBal = data.getString("baseBal");
        tvZhanghu.setText("账户余额（元）：" + baseBal);
        //获取奖金余额
        jiangjin = data.getString("rewardAcctBal");
        tvJiang.setText(jiangjin + "元");
        //加息票数量
        String addRateTicketListCount = data.getString("addRateTicketListCount");
        tvJiaxiPiao.setText(addRateTicketListCount + "张可用");
        //红包数量
        redBagTicketListCount = data.getInteger("redBagTicketListCount");
        tvHongbaoPiao.setText(redBagTicketListCount + "张可用");
        //seqNo
        seqNo = data.getString("seqNo");

        //从买入第一步中拿到起投和单笔限额
        minInvestAmount = data.getString("minInvestAmount");
        etBuy.setHint("请输入出借金额，最低出借" + minInvestAmount + "元");
        maxInvestAmount = data.getString("maxInvestAmount");

        // 给买入金额设置监听，1.保证两位有效小数，2.为了使奖金使用状态图标根据不同的输入值变化而变化
        etBuy.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etBuy.setText(s);
                        etBuy.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etBuy.setText(s);
                    etBuy.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etBuy.setText(s.subSequence(0, 1));
                        etBuy.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //只要值改变，红包id和值就清零，并重新计算可用红包张数
                redbagTicketId = "";
                reward = "";
                tvHongbao.setText("0.00元");
                redBagTicketListCount = 0;
                if (listHongBaoCanShow.size() > 0) {
                    listHongBaoCanShow.clear();
                }


                buyMoney = etBuy.getText().toString().trim();

                if (!TextUtils.isEmpty(buyMoney)) {

                    if (StrToNumber.strTodouble(buyMoney) >=
                            StrToNumber.strTodouble(data.getString("rewardAcctBal"))) {
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);

                        //勾选，获取奖金值
                        jiangjin = data.getString("rewardAcctBal");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);

                        //不勾选，把奖金改为0
                        jiangjin = "0.00";
                    }

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
        });
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
                LogUtils.i("选完加息票的fwAddRateId=="+fwAddRateId);
                jiaxipiaoValue = data.getDoubleExtra("jiaxipiaoValue",0);
                jiaxipiaoDay = data.getStringExtra("jiaxipiaoDay");
                jiaxipiaoIsCheck = data.getStringExtra("jiaxipiaoIsCheck");

                jiaxi = MathUtil.subDouble((StrToNumber.strTodouble(JSON.parseObject(datastr).getString("expectedRate"))
                        + jiaxipiaoValue), 2) + "";
                tvRate.setText(jiaxi + "%");

                //1.与锁定期一致 0.不一致
                if ("1".equals(jiaxipiaoIsCheck)) {
                    tvJiaxiDay.setVisibility(View.VISIBLE);
                    tvJiaxiDay.setText("（可加息" + term + "天）");
                }else {
                    if(jiaxipiaoValue>0){
                        tvJiaxiDay.setVisibility(View.VISIBLE);
                        tvJiaxiDay.setText("（可加息" + jiaxipiaoDay + "天）");
                    }else {
                        tvJiaxiDay.setVisibility(View.GONE);
                    }
                }

                //选中加息票回来后改变加息票的值
                tvJiaxiRate.setText(MathUtil.subDouble(jiaxipiaoValue,2) + "%");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.iv_back, R.id.tv_rate, R.id.iv_check_zhanghu, R.id.iv_check_jiang, R.id.tv_jiaxi_piao
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
            case R.id.iv_check_zhanghu:
                isZhangHuChecked = !isZhangHuChecked;
                if (isZhangHuChecked) {//勾选账户余额
                    ivCheckZhanghu.setImageResource(R.drawable.bind_check_ok);
                } else {//不勾选账户余额
                    ivCheckZhanghu.setImageResource(R.drawable.bind_check_no);
                }
                break;
            case R.id.iv_check_jiang:
                if (StrToNumber.strTodouble(buyMoney) >=
                        StrToNumber.strTodouble(data.getString("rewardAcctBal"))) {
                    isJiangJinChecked = !isJiangJinChecked;
                    if (isJiangJinChecked) {//勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_ok);
                        //勾选，获取奖金值
                        jiangjin = data.getString("rewardAcctBal");
                    } else {//不勾选奖金
                        ivCheckJiang.setImageResource(R.drawable.bind_check_no);
                        //不勾选，把奖金改为0
                        jiangjin = "0.00";
                    }
                } else {
                    if (!TextUtils.isEmpty(buyMoney)) {
                        JiangJinDialog dialog = new JiangJinDialog(FtMaxBuyActivity.this, R.style.YzmDialog);
                        dialog.show();
                    }
                }
                break;
            case R.id.tv_hongbao_piao:
                if (!TextUtils.isEmpty(buyMoney)) {
                    if (StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)) {
                        ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的出借金额");
                    } else {
                        Intent intent = new Intent(this, HongBaoUseActivity.class);
                        intent.putExtra("listHongBaoCanShow", (Serializable) listHongBaoCanShow);
                        intent.putExtra("redbagTicketId", redbagTicketId);
                        startActivityForResult(intent, 1);
                    }

                } else {
                    ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的出借金额");
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
                if (isXieYiChecked) {//勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_ok);
                } else {//不勾选奖金
                    ivCheckXieyi.setImageResource(R.drawable.xieyi_check_no);
                }
                break;
            case R.id.tv_xieyi_one:
                Intent intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
                break;
            case R.id.tv_xieyi_two:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
                break;
            case R.id.tv_xieyi_three:
                intent = new Intent(this, InvestXieYiActivity.class);
                intent.putExtra("type","3");
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

    private void showConfirmDialog(final String investTotalAmount, String jiangjin, String baseBal, String jiaxi, String term) {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_ftmax_confirm, null);
        ImageView iv_cancel = (ImageView) inflate.findViewById(R.id.iv_cancel);
        //出借金额
        TextView tv_money_buy = (TextView) inflate.findViewById(R.id.tv_money_buy);
        tv_money_buy.setText(MathUtil.subString(investTotalAmount,2) + "元");
        //奖金抵减
        TextView tv_money_jiang = (TextView) inflate.findViewById(R.id.tv_money_jiang);
        tv_money_jiang.setText(jiangjin + "元");
        //使用余额
        TextView tv_money_yue = (TextView) inflate.findViewById(R.id.tv_money_yue);
        tv_money_yue.setText(MathUtil.subDouble((StrToNumber.strTodouble(investTotalAmount) - StrToNumber.strTodouble(jiangjin)),2) + "元");
        //预期年化
        TextView tv_rate_all = (TextView) inflate.findViewById(R.id.tv_rate_all);
        tv_rate_all.setText(MathUtil.subString(jiaxi,2) + "%");
        //锁定期限
        TextView tv_day = (TextView) inflate.findViewById(R.id.tv_day);
        tv_day.setText(term + "天");
        TextView tv_btn_zhifu = (TextView) inflate.findViewById(R.id.tv_btn_zhifu);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_btn_zhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromServer();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性,宽度与屏幕同宽
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置Dialog距离底部的距离
        //lp.y = 20;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        //显示对话框
        dialog.show();

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
        if (StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)) {// 出借金额小于最小最低出借
            ToastUtils.toastshort("请输入不小于" + minInvestAmount + "的出借金额");
            return;
        }
        if (StrToNumber.strTodouble(buyMoney) > StrToNumber.strTodouble(maxInvestAmount)) {// 出借金额大于单笔最大出借
            ToastUtils.toastshort("单笔出借金额请小于" + maxInvestAmount);
            return;
        }
        // 出借金额超出了剩余金额
        if (StrToNumber.strTodouble(borrowSalableBal) - StrToNumber.strTodouble(buyMoney) < 0 ) {
            ToastUtils.toastshort("出借金额超出了剩余金额");
            return;
        }
        BigDecimal remainder = new BigDecimal(buyMoney).divideAndRemainder(new BigDecimal("100"))[1];
        if (remainder.compareTo(BigDecimal.ZERO)!=0) {
            ToastUtils.toastshort("出借金额必须为100的整数倍");
            return;
        }

        // 为避免剩余金额<1000元时其他用户无法出借
        if (StrToNumber.strTodouble(borrowSalableBal) - StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)
                &&StrToNumber.strTodouble(borrowSalableBal) != StrToNumber.strTodouble(buyMoney)) {
            BuyAllDialog buyAllDialog = new BuyAllDialog(this, R.style.YzmDialog, minInvestAmount);
            buyAllDialog.show();
            return;
        }

        if ((data.getDouble("addRateTicketListCount") > 0 && jiaxipiaoValue == 0) ||
                (listHongBaoCanShow.size() > 0 && TextUtils.isEmpty(redbagTicketId))) {
            HasHongBaoOrJiaXiPiaoDialogForFWbuy hasHongBaoOrJiaXiPiaoDialog = new HasHongBaoOrJiaXiPiaoDialogForFWbuy(
                    FtMaxBuyActivity.this, R.style.YzmDialog,"您未使用红包或加息票，确定出借吗？");
            hasHongBaoOrJiaXiPiaoDialog.show();
            hasHongBaoOrJiaXiPiaoDialog.setOnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener(new HasHongBaoOrJiaXiPiaoDialogForFWbuy.
                    OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener() {
                @Override
                public void OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismiss() {
                    if(isJiangJinChecked){
                        if(StrToNumber.strTodouble(buyMoney) == StrToNumber.strTodouble(jiangjin)){
                            showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                        }else {
                            if(isZhangHuChecked&&StrToNumber.strTodouble(baseBal) >= (StrToNumber.strTodouble(buyMoney) - StrToNumber.strTodouble(jiangjin))){
                                showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                            }else {
                                getDataFromServer();
                            }
                        }
                    }else{
                        if(isZhangHuChecked&&StrToNumber.strTodouble(baseBal) >= StrToNumber.strTodouble(buyMoney)){
                            showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                        }else {
                            getDataFromServer();
                        }
                    }

                }
            });
        } else {
            if(isJiangJinChecked){
                if(StrToNumber.strTodouble(buyMoney) == StrToNumber.strTodouble(jiangjin)){
                    showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                }else {
                    if(isZhangHuChecked&&StrToNumber.strTodouble(baseBal) >= (StrToNumber.strTodouble(buyMoney) - StrToNumber.strTodouble(jiangjin))){
                        showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                    }else {
                        getDataFromServer();
                    }
                }
            }else{
                if(isZhangHuChecked&&StrToNumber.strTodouble(baseBal) >= StrToNumber.strTodouble(buyMoney)){
                    showConfirmDialog(buyMoney, jiangjin, baseBal, jiaxi, term);
                }else {
                    getDataFromServer();
                }
            }
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
        map.put("amount", buyMoney);
        map.put("reward", jiangjin);
        map.put("seqNo", seqNo);
        map.put("fwAddRateId", fwAddRateId);
        map.put("redbagTicketId", redbagTicketId);
        map.put("isUsedBal", isZhangHuChecked ? "1" : "0");
        map.put("isAutoTransfer", "1" );
//        map.put("isAutoTransfer", isZhaiZhuanOpen ? "1" : "0");
        LogUtils.i("fwAddRateId=="+fwAddRateId+"   redbagTicketId=="+redbagTicketId);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFT_CONFIRMINVEST_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("大饭团立即买入===" + string);
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

                                    // 真实姓名
                                    String realName = data.getString("realName");
                                    CacheUtils.putString("realName", realName);

                                    // 身份证号
                                    String idNo = data.getString("idNo");
                                    CacheUtils.putString("idNo", idNo);

                                    // 银行卡号
                                    String cardNum = data.getString("cardNum");
                                    CacheUtils.putString("cardNum", cardNum);

                                    // 银行预留手机号
                                    String cardPhone = data.getString("cardPhone");
                                    CacheUtils.putString("cardPhone", cardPhone);

                                    // 开户行图标
                                    String bankPic = data.getString("bankPic");
                                    CacheUtils.putString("bankPic", bankPic);

                                    //银行名称
                                    String bankName = data.getString("bankName");
                                    CacheUtils.putString("bankName", bankName);

                                    //单笔限额
                                    String singleTransLimit = data.getString("singleTransLimit");
                                    CacheUtils.putString("singleTransLimit", singleTransLimit);

                                    //单日限额
                                    String singleDayLimit = data.getString("singleDayLimit");
                                    CacheUtils.putString("singleDayLimit", singleDayLimit);

                                    // 使用余额
                                    String baseBal = data.getString("baseBal");

                                    // 充值金额
                                    String rechargeAmount = data.getString("rechargeAmount");

                                    // 买入总计
                                    String investTotalAmount = data.getString("investTotalAmount");

                                    // 奖金
                                    String rewardAcctBal = data.getString("rewardAcctBal");

                                    // 交易流水号
                                    String seqNo = data.getString("seqNo");

                                    // 锁定期限
                                    String term = data.getString("term");

                                    // 是否余额支付
                                    Boolean isBalPay = data.getBoolean("isBalPay");
                                    //若买入金额小于等于账户余额，则直接进入“买入成功”页面
                                    if (isBalPay) {
                                        Intent intent = new Intent(FtMaxBuyActivity.this, FtMaxBuySuccessActivity.class);
                                        intent.putExtra("zong_money", investTotalAmount);
                                        startActivity(intent);
                                    } else {//若买入金额大于账户余额，进入“买入确认”页面
                                        Intent intent = new Intent(FtMaxBuyActivity.this, FtMaxBuyConfirmActivity.class);
                                        intent.putExtra("et_buy_money", baseBal);// 使用余额
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("et_jiangjin_money", rewardAcctBal);
                                        intent.putExtra("zong_money", investTotalAmount);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("jiaxi", jiaxi);// 预期年化
                                        intent.putExtra("investDays", term);//出借天数
                                        startActivity(intent);
                                    }


                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            if (msg.startsWith("为避免剩余金额")) {
                                BuyAllDialog buyAllDialog = new BuyAllDialog(FtMaxBuyActivity.this, R.style.YzmDialog, minInvestAmount);
                                buyAllDialog.show();
                            } else {
                                ToastUtils.toastshort(msg);
                            }
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
