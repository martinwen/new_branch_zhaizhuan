package com.iqianbang.fanpai.activity.invest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.iqianbang.fanpai.adapter.BankAdapter;
import com.iqianbang.fanpai.bean.BankListBean;
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

public class FtMaxBindBankActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.tv_bankname)
    TextView tvBankname;
    @BindView(R.id.rl_bankname)
    RelativeLayout rlBankname;
    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.et_card)
    EditText etCard;
    @BindView(R.id.et_phone)
    EditText etPhone;
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
    private ArrayList<BankListBean> list = new ArrayList<>();
    private BankAdapter adapter;
    private String seqNo;
    private String jiangjin;
    private boolean isJiangJinChecked = true;// 奖金没有勾上
    private boolean isXieYiChecked = true;// 协议已经勾上
    private String term;
    private String jiaxi = null;
    private String bankName;
    private String bankCode;
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
        setContentView(R.layout.activity_ft_max_bind_bank);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在添加......");
        initData();
    }

    private void initData() {
            /*姓名：韩梅梅
        身份证号：210302196001012114
        电话号：13220482188
        短信验证码:123456
        储蓄卡 :
        卡号：6214242710498301(建设银行) 或者 6214242710498509(招商银行)*/

        /*etName.setText("文广军");
        etId.setText("430903198807216932");
        etPhone.setText("17610770721");
        etCard.setText("6212260200104685207");*/

        //初始化选中银行下拉列表
        getBanks();

        datastr = getIntent().getStringExtra("jsonData");
        data = JSON.parseObject(datastr);
        listHongBao = (ArrayList<HongBaoBean>)JSONArray.parseArray(
                data.getJSONArray("redBagTicketList").toJSONString(),HongBaoBean.class);

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
        tvZhanghu.setText( "账户余额（元）：" + baseBal);
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

        //从买入第一步中拿到isEnable,  3:换卡（身份证号和姓名不允许输入），4:新绑卡
        String isEnable = data.getString("isEnable");

        if ("3".equals(isEnable)) {

            //姓名（用于解绑后再次绑卡时显示）
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证（用于解绑后再次绑卡时显示）
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }
        } else if ("4".equals(isEnable)) {

            //姓名（用于新绑卡）
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
            }

            //身份证（用于新绑卡）
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
            }

            //银行名称
            String bankName = data.getString("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
            }

            //银行卡号
            String cardNum = data.getString("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
            }

            //银行卡预留手机号
            String cardPhone = data.getString("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
            }
        } else if ("5".equals(isEnable)) {//（用于丰付切换到融宝过来的）

            //姓名
            String realName = data.getString("realName");
            if (!TextUtils.isEmpty(realName)) {
                etName.setText(realName);
                etName.setEnabled(false);
            }

            //身份证
            String idNo = data.getString("idNo");
            if (!TextUtils.isEmpty(idNo)) {
                etId.setText(idNo);
                etId.setEnabled(false);
            }

            //银行名称
            String bankName = data.getString("bankName");
            if (!TextUtils.isEmpty(bankName)) {
                tvBankname.setText(bankName);
                rlBankname.setEnabled(false);
            }

            //银行编号
            bankCode = data.getString("bankCode");

            //银行卡号
            String cardNum = data.getString("cardNum");
            if (!TextUtils.isEmpty(cardNum)) {
                etCard.setText(cardNum);
                etCard.setEnabled(false);
            }

            //银行卡预留手机号
            String cardPhone = data.getString("cardPhone");
            if (!TextUtils.isEmpty(cardPhone)) {
                etPhone.setText(cardPhone);
                etPhone.setEnabled(false);
            }
        }

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
                //只要值改变，红包id和值就清零,并重新计算可用红包张数
                redbagTicketId = "";
                reward = "";
                tvHongbao.setText("0.00元");
                redBagTicketListCount = 0;
                if(listHongBaoCanShow.size()>0){
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
                        if((StrToNumber.strTodouble(buyMoney)) >=
                                StrToNumber.strTodouble(listHongBao.get(i).getUseCondition())){
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
                if (reward != null) {
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
                    }else{
                        tvJiaxiDay.setVisibility(View.GONE);
                    }
                }

                //选中加息票回来后改变加息票的值
                tvJiaxiRate.setText(MathUtil.subDouble(jiaxipiaoValue,2) + "%");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.iv_back, R.id.tv_check, R.id.rl_bankname, R.id.tv_rate, R.id.iv_check_zhanghu,
            R.id.iv_check_jiang, R.id.tv_jiaxi_piao, R.id.tv_hongbao_piao, R.id.iv_zhaizhuan,
            R.id.iv_check_xieyi, R.id.tv_xieyi_one, R.id.tv_xieyi_two, R.id.tv_xieyi_three, R.id.tv_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check:
                startActivity(new Intent(this, BankListActivity.class));
                break;
            case R.id.rl_bankname:
                // 初始化Listview
                ListView listView = initListview();
                // 点击时，弹出Popupwindow
                pop = new PopupWindow(listView, LockPatternUtil.dip2px(this, 200), LockPatternUtil.dip2px(this, 300));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(rlBankname, LockPatternUtil.dip2px(this, 100), 0);
                break;
            case R.id.tv_rate:
                JSONArray activityList = JSONArray.parseArray(data.getString("activityList"));
                String[] listdata = new String[activityList.size()+1];
                //基础利率
                listdata[0] = "历史基础年化结算利率："+data.getString("baseRate")+"%";
                //活动利率
                if(activityList.size()>0){
                    for (int i = 0; i < activityList.size(); i++) {
                        JSONObject json = (JSONObject) activityList.get(i);
                        String name = json.getString("name");
                        String activityRate = json.getString("activityRate");
                        listdata[i+1] = name+"："+activityRate+"%";
                    }
                }

                //若有加息票，得加上加息利率
                if(jiaxipiaoValue>0){
                    listdata = insertStr(listdata,"使用加息票："+jiaxipiaoValue+"%");
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
                        JiangJinDialog dialog = new JiangJinDialog(FtMaxBindBankActivity.this, R.style.YzmDialog);
                        dialog.show();
                    }
                }
                break;
            case R.id.tv_hongbao_piao:
                if (!TextUtils.isEmpty(buyMoney)) {
                    if (StrToNumber.strTodouble(buyMoney) < StrToNumber.strTodouble(minInvestAmount)) {
                        ToastUtils.toastshort("请输入不小于"+minInvestAmount+"的出借金额");
                    } else {
                        Intent intent = new Intent(this, HongBaoUseActivity.class);
                        intent.putExtra("listHongBaoCanShow", (Serializable) listHongBaoCanShow);
                        intent.putExtra("redbagTicketId", redbagTicketId);
                        startActivityForResult(intent, 1);
                    }

                } else {
                    ToastUtils.toastshort("请输入不小于"+minInvestAmount+"的出借金额");
                }
                break;
            case R.id.tv_jiaxi_piao:
                if (data.getDouble("addRateTicketListCount")>0){
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
                bind();
                break;
        }
    }

    private String[] insertStr(String[] arr, String str)
    {
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

    private void bind() {
        // 绑定银行卡界面输入的参数
        final String name = etName.getText().toString().trim();
        final String number = etId.getText().toString().trim();
        final String card = etCard.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        //amount指输入金额和奖金之和
        String amountBuy = MathUtil.subDouble(StrToNumber.strTodouble(etBuy.getText().toString().trim())
                + StrToNumber.strTodouble(jiangjin), 2) + "";

        if (!isZhaiZhuanOpen) {// 没有债转
            ToastUtils.toastlong("请授权平台锁定期结束后自动发起债权转让");
            return;
        }

        if (!isXieYiChecked) {// 没有勾选协议
            ToastUtils.toastshort("请同意出借协议");
            return;
        }
        if (TextUtils.isEmpty(name)) {// 姓名为空
            ToastUtils.toastshort("姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(number)) {// 身份证号为空
            ToastUtils.toastshort("身份证号不能为空");
            return;
        }
        if (TextUtils.isEmpty(bankCode)) {// 未选择银行
            ToastUtils.toastshort("请选择银行名称");
            return;
        }
        if (TextUtils.isEmpty(card)) {// 银行卡号为空
            ToastUtils.toastshort("银行卡号不能为空");
            return;
        }
        if (TextUtils.isEmpty(phone)) {// 银行预留手机号为空
            ToastUtils.toastshort("银行预留手机号不能为空");
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

        if((data.getDouble("addRateTicketListCount")>0 && jiaxipiaoValue == 0)||
                (listHongBaoCanShow.size()>0 && TextUtils.isEmpty(redbagTicketId))){
            HasHongBaoOrJiaXiPiaoDialogForFWbuy hasHongBaoOrJiaXiPiaoDialog = new HasHongBaoOrJiaXiPiaoDialogForFWbuy(
                    FtMaxBindBankActivity.this,R.style.YzmDialog,"您未使用红包或加息票，确定出借吗？");
            hasHongBaoOrJiaXiPiaoDialog.show();
            hasHongBaoOrJiaXiPiaoDialog.setOnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener(new HasHongBaoOrJiaXiPiaoDialogForFWbuy.
                    OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismissListener() {
                @Override
                public void OnHasHongBaoOrJiaXiPiaoDialogForFWbuyDismiss() {
                    getDataFromServer(name, number, card, phone);
                }
            });
        }else {
            getDataFromServer(name, number, card, phone);
        }


    }


    private void getDataFromServer(final String name, final String number, final String card, final String phone) {
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);
        map.put("realName", name);
        map.put("idNo", number);
        map.put("cardNum", card);
        map.put("cardPhone", phone);
        map.put("amount", buyMoney);
//        map.put("reward", jiangjin);
        map.put("reward", "0.00");
        map.put("seqNo", seqNo);
        map.put("fwAddRateId", fwAddRateId);
        map.put("redbagTicketId", redbagTicketId);
        map.put("bankName", bankName);
        map.put("bankCode", bankCode);
//        map.put("isUsedBal", isZhangHuChecked ? "1" : "0");
        map.put("isUsedBal", "0");
        map.put("isAutoTransfer", "1" );
//        map.put("isAutoTransfer", isZhaiZhuanOpen ? "1" : "0");
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFT_BINDCARD_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("大饭团绑卡接口===" + string);
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
                                    JSONObject data = JSON.parseObject(datastr);

                                    String seqNo = data.getString("seqNo");
                                    String bankCode = data.getString("bankCode");
                                    String bindId = data.getString("bindId");

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

                                    //银行图标
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

                                    // 使用小饭团
                                    String fhAmount = data.getString("fhAmount");

                                    // 锁定期限
                                    String term = data.getString("term");

                                    // 渠道（融宝reapal或丰付sumapay）
                                    String channelCode = data.getString("channelCode");

                                    if ("CMB".equals(bankCode) && "reapal".equals(channelCode)) {
                                        //如果是招商银行 则跳转鉴权界面
                                        Intent intent = new Intent(FtMaxBindBankActivity.this,
                                                FtMaxCMDAuthenticationActivity.class);
                                        intent.putExtra("et_buy_money", baseBal);
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("et_jiangjin_money", rewardAcctBal);
                                        intent.putExtra("fhAmount", fhAmount);
                                        intent.putExtra("zong_money", investTotalAmount);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("bindId", bindId);
                                        intent.putExtra("jiaxi", jiaxi);// 预期年化
                                        intent.putExtra("investDays", term);//出借天数
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // 把这些数据传递给绑定银行卡确定页面
                                        Intent intent = new Intent(FtMaxBindBankActivity.this,
                                                FtMaxBuyConfirmActivity.class);
                                        intent.putExtra("et_buy_money", baseBal);
                                        intent.putExtra("rechargeAmount", rechargeAmount);// 充值金额
                                        intent.putExtra("et_jiangjin_money", rewardAcctBal);
                                        intent.putExtra("fhAmount", fhAmount);
                                        intent.putExtra("zong_money", investTotalAmount);
                                        intent.putExtra("seqNo", seqNo);
                                        intent.putExtra("jiaxi", jiaxi);// 预期年化
                                        intent.putExtra("investDays", term);//出借天数
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    ToastUtils.toastshort("添加失败！");
                                }
                            }

                        } else {
                            String msg = json.getString("msg");
                            if(msg.startsWith("为避免剩余金额")){
                                BuyAllDialog buyAllDialog = new BuyAllDialog(FtMaxBindBankActivity.this,
                                        R.style.YzmDialog, minInvestAmount);
                                buyAllDialog.show();
                            }else {
                                ToastUtils.toastshort(msg);
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("网络请求失败！");

                    }
                });
    }

    private void getBanks() {
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
                            boolean isSuccess = SignUtil.verify(sign, datastr);
                            if (isSuccess) {// 验签成功
                                ArrayList<BankListBean> listadd = (ArrayList<BankListBean>) JSONArray.parseArray(datastr, BankListBean.class);
                                list.addAll(listadd);
                                adapter = new BankAdapter(FtMaxBindBankActivity.this, list);
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

    private ListView initListview() {
        listView = new ListView(this);
        // 去掉分割线
        listView.setDividerHeight(0);
        // 去掉滑动块
        listView.setVerticalScrollBarEnabled(false);
        // 设置背景
//        listView.setBackgroundResource(R.drawable.listview_background);
        listView.setBackgroundColor(getResources().getColor(R.color.global_bg_blackcolor));
        // 设置Adapter
        listView.setAdapter(adapter);
        // 设置条目点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());
        return listView;
    }

    // 根据点击选中银行
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            LogUtils.i("点击条目了：" + position);
            if (list.size() == 0) {
                return;
            }
            BankListBean bankListBean = list.get(position);
            bankName = bankListBean.getBankName();
            bankCode = bankListBean.getBankCode();
            tvBankname.setText(bankName);
            pop.dismiss();// 选中银行弹窗消失
        }
    }
}
