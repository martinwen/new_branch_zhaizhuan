package com.iqianbang.fanpai.view.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;

import java.util.Date;

/**
 * @Title: DateSignView
 * @Package cn.wecoder.signcalendar.library
 * @Description: 一天签到视图
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class DateSignView extends LinearLayout {
    private boolean signed;
    private boolean isPrized;
    private Date date = new Date();
    private TextView mDayView;
//    private TextView mSignView;
    private Context mContext;
    private SignCalendar signCalendar;

    public DateSignView(Context context) {
        this(context, null);
    }

    public DateSignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 设置签到日历
     * @param signCalendar 签到日历
     */
    public void setSignCalendar(SignCalendar signCalendar) {
        this.signCalendar = signCalendar;
    }

    /**
     * 得到日期
     * @return 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 得到签到状态
     * @return 签到状态
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * 设置每日签到视图的数据
     * @param date 日期
     * @param signed 签到状态
     */
    public void setSignData(Date date, boolean signed, boolean isPrized) {
        this.date = date;
        this.signed = signed;
        this.isPrized = isPrized;
        generateChildViews();
    }

    /**
     * 生成视图
     */
    private void generateChildViews() {
        int day = date.getDate();
        TextView dayView = new TextView(mContext);
//        TextView signView = new TextView(mContext);
        dayView.setGravity(Gravity.CENTER);
//        signView.setGravity(Gravity.CENTER);
        //修改day字体大小为13sp，原始为20sp
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_day_text_size));
//        dayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_day_text_size));
//        signView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_sign_text_size));
        Date nowDate = new Date();
        if(signCalendar.getToday() != null) {
            nowDate = signCalendar.getToday();
        }
        int result = DateUtil.compareDateDay(date, nowDate);
        if(result == 1) {
            dayView.setText(day + "");
            dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unreach_day));
//            signView.setText(R.string.cal_unreach);
//            signView.setTextColor(mContext.getResources().getColor(R.color.cal_unreach_text));
        }else {
            if(signed) {//已签到
            	
                if(result == 0) {
//                    setBackgroundResource(R.drawable.cal_today_bg);
                	setBackgroundResource(R.drawable.cal_signed_bg);
                	//setBackgroundResource(R.color.text_black);
                    dayView.setText(day + "");
//                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_today_day));
                    //修改颜色
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day));
//                    signView.setText(R.string.cal_signed);
//                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_today_text));
                }else if(result == -1) {
//                    setBackgroundResource(R.drawable.cal_signed_bg);
                	setBackgroundResource(R.drawable.cal_signed_bg);
                	
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day));
//                    signView.setText(R.string.cal_signed);
//                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_signed_text));
                }
                if (isPrized) {
                	dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day));
            		setBackgroundResource(R.drawable.sign_jiang);
//                    dayView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,mContext.getResources().getDrawable(R.drawable.sign_jiang));
				}
            }else {
                if(result == 0) {//当天
//                    setBackgroundResource(R.drawable.cal_today_bg);
                	setBackgroundResource(R.drawable.cal_today_bg);
                	//setBackgroundResource(R.color.text_black);
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_today_text));
//                    signView.setText(R.string.cal_today);
//                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_today_text));
                }else if(result == -1) {//未到
                    dayView.setText(day + "");
                    dayView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_day));
//                    signView.setText(R.string.cal_unsign);
//                    signView.setTextColor(mContext.getResources().getColor(R.color.cal_unsigned_text));
                }
            }
        }
        mDayView = dayView;
//        mSignView = signView;
        addView(mDayView);
//        addView(mSignView);
    }
}
