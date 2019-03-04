package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.os.Bundle;
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
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FtMaxProjectActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.tv_money_buy)
    TextView tvMoneyBuy;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_leiji)
    TextView tvLeiji;
    @BindView(R.id.ll_shouyi)
    LinearLayout llShouyi;
    @BindView(R.id.tv_money_earn)
    TextView tvMoneyEarn;
    @BindView(R.id.tv_yijie)
    TextView tvYijie;
    @BindView(R.id.iv_line_xuxian)
    View ivLineXuxian;
    @BindView(R.id.tv_jinggao)
    TextView tvJinggao;
    @BindView(R.id.tv_time_chujie)
    TextView tvTimeChujie;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_whatTime)
    TextView tvWhatTime;
    @BindView(R.id.tv_time_other)
    TextView tvTimeOther;
    @BindView(R.id.iv_line_time)
    View ivLineTime;
    @BindView(R.id.ll_hongbao)
    LinearLayout llHongbao;
    @BindView(R.id.iv_line_hongbao)
    View ivLineHongbao;
    @BindView(R.id.tv_hongbao)
    TextView tvHongbao;
    @BindView(R.id.tv_yuji)
    TextView tvYuji;
    @BindView(R.id.tv_benxi)
    TextView tvBenxi;
    @BindView(R.id.tv_zijin)
    TextView tvZijin;
    @BindView(R.id.tv_zhuanrang)
    TextView tvZhuanrang;
    @BindView(R.id.ll_zhuanrang)
    LinearLayout llZhuanrang;
    @BindView(R.id.iv_line_zhuanrang)
    View ivLineZhuanrang;
    @BindView(R.id.tv_chujie)
    TextView tvChujie;
    @BindView(R.id.tv_fuwu)
    TextView tvFuwu;
    @BindView(R.id.ll_xiangmu)
    LinearLayout llXiangmu;
    @BindView(R.id.iv_line_xiangmu)
    View ivLineXiangmu;


    private CustomProgressDialog progressdialog;
    private String transUuid;
    private String transSeq;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftmax_project);
        ButterKnife.bind(this);
        progressdialog = new CustomProgressDialog(this, "");
        initData();
    }

    private void initData() {

        //访问网络，初始化界面数据
        id = getIntent().getStringExtra("id");
        getDataFromServer(id);
    }

    @OnClick({R.id.iv_back, R.id.tv_zijin, R.id.tv_zhuanrang, R.id.tv_chujie, R.id.tv_fuwu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_zijin:
                Intent intent = new Intent(this, FtMaxZaiTouJiLuActivity.class);
                intent.putExtra("transUuid", transUuid);
                startActivity(intent);
                break;
            case R.id.tv_zhuanrang:
                intent = new Intent(this, ZhuanRangJiLuActivity.class);
                intent.putExtra("transSeq", transSeq);
                startActivity(intent);
                break;
            case R.id.tv_chujie:
                intent = new Intent(this, ZaiTouXiangMuActivity.class);
                intent.putExtra("proCode", "fw");
                intent.putExtra("transSeq", transSeq);
                startActivity(intent);
                break;
            case R.id.tv_fuwu:
                intent = new Intent(this, FuWuXieYiActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
        }
    }

    private void getDataFromServer(String id) {
        progressdialog.show();
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", null);
        map.put("dftInvestRecordId", id);
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.DFTINVESTRECORDDETAIL_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("投资详情明细信息===" + string);
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
                                    String collectionName = data.getString("collectionName");
                                    tvTitle.setText(collectionName);
                                    //10持有中、20转让中、30转让成功、40已结清
                                    String status = data.getString("status");
                                    if ("10".equals(status)) {
                                        boolean isLock = data.getBoolean("isLock");
                                        if (isLock) {//true  在锁定期
                                            String lockExpireTime = data.getString("lockExpireTime");
                                            tvTimeOther.setText(lockExpireTime);
                                        } else {
                                            llTime.setVisibility(View.GONE);
                                            ivLineTime.setVisibility(View.GONE);
                                        }
                                    } else if ("20".equals(status)) {
                                        tvWhatTime.setText("转让时间");
                                        //转让时间
                                        String transferTime = data.getString("transferTime");
                                        tvTimeOther.setText(transferTime);
                                    } else if ("40".equals(status)) {
                                        //结清时去掉出借项目
                                        llXiangmu.setVisibility(View.GONE);
                                        ivLineXiangmu.setVisibility(View.GONE);
                                        tvWhatTime.setText("结清时间");
                                        //结清时间
                                        String settlementTime = data.getString("settlementTime");
                                        tvTimeOther.setText(settlementTime);
                                        ivStatus.setImageResource(R.drawable.zaitou_jieqing);
                                        tvYuji.setText("已结本息和");
                                    }

                                    //出借金额
                                    String investAmount = data.getString("investAmount");
                                    tvMoneyBuy.setText(investAmount + "元");

                                    //预期结利率
                                    String yearRate = data.getString("yearRate");
                                    tvRate.setText(yearRate + "%");

                                    //累计收益
                                    String accumulativeIncome = data.getString("accumulativeIncome");
                                    tvLeiji.setText(accumulativeIncome + "元");

                                    //已结本金
                                    String hasClearAmount = data.getString("hasClearAmount");
                                    //已结收益  已结收益  > 0 才展示
                                    String hasClearIncome = data.getString("hasClearIncome");
                                    if (StrToNumber.strTodouble(hasClearAmount) > 0 || StrToNumber.strTodouble(hasClearIncome) > 0) {
                                        tvMoneyEarn.setText(hasClearAmount + "元");
                                        tvYijie.setText(hasClearIncome + "元");
                                    } else {
                                        llShouyi.setVisibility(View.GONE);
                                    }

                                    //是否自动债转 0|1  1 显示“已授权平台自动发起债转”
                                    String autoTransfer = data.getString("autoTransfer");
                                    //isActivity:  0 非活动标  1 活动标
                                    String isActivity = data.getString("isActivity");
                                    if ("1".equals(autoTransfer)) {
                                        ivLineXuxian.setVisibility(View.VISIBLE);
                                        tvJinggao.setVisibility(View.VISIBLE);
                                        //预约类型  0 未预约  1 预约  2 转让中复投成功  3预约复投成功
                                        if("0".equals(data.getString("bookType"))){
                                            tvJinggao.setText("已授权平台自动发起债转");
                                        }else{
                                            if ("40".equals(status)) {//40已结清
                                                if("0".equals(isActivity)){// 0 非活动标
                                                    tvJinggao.setText("已成功复投" + data.getString("repeatedDays") + "天");
                                                }else if("1".equals(isActivity)) {// 1  活动标
                                                    tvJinggao.setText("已成功复投" + data.getString("repeatedDays") + "天（活动专享）");
                                                }

                                            }else{
                                                if("0".equals(isActivity)){// 0 非活动标
                                                    tvJinggao.setText("已预约锁定期结束后复投" + data.getString("repeatedDays") + "天");
                                                }else if("1".equals(isActivity)) {// 1  活动标
                                                    tvJinggao.setText("已预约锁定期结束后复投" + data.getString("repeatedDays") + "天（活动专享）");
                                                }

                                            }
                                        }
                                    }


                                    //出借时间
                                    String transTime = data.getString("transTime");
                                    tvTimeChujie.setText(transTime);

                                    //出借时间
                                    String redbagMoney = data.getString("redbagMoney");
                                    if (StrToNumber.strTodouble(redbagMoney) > 0) {
                                        tvHongbao.setText(redbagMoney + "元");
                                    } else {
                                        llHongbao.setVisibility(View.GONE);
                                        ivLineHongbao.setVisibility(View.GONE);
                                    }

                                    //预期本息和 ||已结本息和
                                    String totalPrincipalInterestAmount = data.getString("totalPrincipalInterestAmount");
                                    tvBenxi.setText(totalPrincipalInterestAmount + "元");

                                    //transUuid，资金明细用到
                                    transUuid = data.getString("transUuid");

                                    //transSeq
                                    transSeq = data.getString("transSeq");

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
