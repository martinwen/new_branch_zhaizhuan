package com.iqianbang.fanpai.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.invest.FtMaxBindBankActivity;
import com.iqianbang.fanpai.activity.invest.FtMaxDetailActivity;
import com.iqianbang.fanpai.activity.invest.FtMinToFtMaxActivity;
import com.iqianbang.fanpai.activity.invest.FuTouBuyActivity;
import com.iqianbang.fanpai.activity.invest.FuTouYueActivity;
import com.iqianbang.fanpai.bean.FtMaxListBean;
import com.iqianbang.fanpai.bean.FuTouListBean;
import com.iqianbang.fanpai.bean.FuTouListRateBean;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.MathUtil;
import com.iqianbang.fanpai.utils.StrToNumber;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.lockPattern.LockPatternUtil;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * 类描述：FtMaxListAdapter
 * @author WenGuangJun
 * create at 2018/5/11 19:09
 */

public class FuTouListAdapter extends BaseAdapter {

    private ArrayList<FuTouListBean> list;
    private Context mContext;

    public FuTouListAdapter(Context context, ArrayList<FuTouListBean> list) {
        super();
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoticeHolder holder = null;
        if (convertView == null) {
            holder = new NoticeHolder();
            convertView = View.inflate(mContext, R.layout.items_futoulist, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_act = (ImageView) convertView.findViewById(R.id.iv_act);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_rate_base = (TextView) convertView.findViewById(R.id.tv_rate_base);
            holder.tv_rate_add = (TextView) convertView.findViewById(R.id.tv_rate_add);
            holder.tv_wenhao = (TextView) convertView.findViewById(R.id.tv_wenhao);
            holder.tv_btn = (TextView) convertView.findViewById(R.id.tv_btn);
            convertView.setTag(holder);
        } else {
            holder = (NoticeHolder) convertView.getTag();
        }

        final FuTouListBean fuTouListBean = list.get(position);
        holder.tv_name.setText(fuTouListBean.getCollectionName());
        holder.tv_money.setText("剩余金额：" + fuTouListBean.getRemainingMoney2() + "万元");
        holder.tv_rate_base.setText(fuTouListBean.getBaseRate() + "%");
        if(StrToNumber.strTodouble(fuTouListBean.getTotalExtraRate()) > 0){
            holder.tv_rate_add.setVisibility(View.VISIBLE);
            holder.tv_rate_add.setText("+ " + fuTouListBean.getTotalExtraRate() + "%");
        }else {
            holder.tv_rate_add.setVisibility(View.GONE);
        }

        if("0".equals(fuTouListBean.getIsActivity())){// 0 非活动标
            holder.iv_act.setVisibility(View.GONE);
        }else if("1".equals(fuTouListBean.getIsActivity())) {// 1  活动标
            holder.iv_act.setVisibility(View.VISIBLE);
        }

        final String status = fuTouListBean.getStatus();
        if("10".equals(status)){//预约复投
            holder.tv_btn.setText("立即预约");
        }else if("20".equals(status)) {//立即复投
            holder.tv_btn.setText("立即复投");
        }

        holder.tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoInvest(status,fuTouListBean.getDftProId(),fuTouListBean.getDftInvestRecordId(),fuTouListBean.getIsActivity());

            }
        });

        final NoticeHolder finalHolder = holder;
        holder.tv_wenhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<FuTouListRateBean> rateList = fuTouListBean.getRateList();
                pop(status,rateList, finalHolder.tv_name);
            }
        });
        return convertView;
    }

    private void pop(String status,ArrayList<FuTouListRateBean> rateList, TextView tv_wenhao) {
        String[] listdata = new String[rateList.size()];

        //活动利率
        if (rateList.size() > 0) {
            for (int i = 0; i < rateList.size(); i++) {
                String name = rateList.get(i).getName();
                String activityRate = rateList.get(i).getActivityRate();
                listdata[i] = name + "：" + activityRate + "%";
            }
        }

        //若预约复投，得加上加息利率
        if("10".equals(status)){//预约复投
            listdata = insertStr(listdata, "可享受实际复投时的活动加息");
        }

        // 初始化Listview
        ListView listView = initListview(listdata);

        PopupWindow pop = new PopupWindow(listView, LockPatternUtil.dip2px(mContext, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置焦点
        pop.setFocusable(true);
        // 设置背景，为了点击外面时，把Popupwindow消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 展示Popupwindow,显示在输入框的下面
        pop.showAsDropDown(tv_wenhao, 0, 0);
    }

    private String[] insertStr(String[] arr, String str) {
        int size = arr.length;
        String[] tmp = new String[size + 1];
        System.arraycopy(arr, 0, tmp, 0, size);
        tmp[size] = str;
        return tmp;
    }

    private ListView initListview(String[] listdata) {
        ListView listView = new ListView(mContext);
        // 去掉点击效果
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去掉分割线
        listView.setDividerHeight(0);
        // 去掉滑动块
        listView.setVerticalScrollBarEnabled(false);
        // 设置背景
        listView.setBackgroundResource(R.drawable.shape_jiaxi_popbg);
        // 设置Adapter
        listView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.jiaxi_pop, R.id.tv_rate, listdata));

        return listView;
    }

    protected void gotoInvest(final String status, final String groupId, final String investId, final String isActivity) {
        final Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        map.put("token", token);

        String url = "";
        if("10".equals(status)){//预约复投
            map.put("dftRateId", groupId);
            url = ConstantUtils.BOOKGOTOINVEST_URL;
        }else if("20".equals(status)) {//立即复投
            map.put("groupId", groupId);
            url = ConstantUtils.TRANSGOTOINVEST_URL;
        }

        map.put("investId", investId);
        map.put("isActivity", isActivity);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(url,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        if("10".equals(status)){//预约复投
                            LogUtils.longStr("预约复投买入第一步======" , string);
                        }else if("20".equals(status)) {//立即复投
                            LogUtils.longStr("复投买入第一步======" , string);
                        }

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

                                    if("10".equals(status)){//预约复投
                                        Intent intent = new Intent(mContext, FuTouYueActivity.class);
                                        intent.putExtra("jsonData", datastr);
                                        intent.putExtra("isActivity", isActivity);
                                        mContext.startActivity(intent);
                                    }else if("20".equals(status)) {//立即复投
                                        Intent intent = new Intent(mContext, FuTouBuyActivity.class);
                                        intent.putExtra("jsonData", datastr);
                                        intent.putExtra("isActivity", isActivity);
                                        mContext.startActivity(intent);
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
                        ToastUtils.toastshort("加载数据失败！");
                    }

                });
    }

    class NoticeHolder {
        TextView tv_name;
        ImageView iv_act;
        TextView tv_money;
        TextView tv_rate_base;
        TextView tv_rate_add;
        TextView tv_wenhao;
        TextView tv_btn;
    }
}
