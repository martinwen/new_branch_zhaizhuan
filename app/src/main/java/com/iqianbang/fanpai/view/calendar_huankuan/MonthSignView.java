package com.iqianbang.fanpai.view.calendar_huankuan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.view.calendar.DateSignData;
import com.iqianbang.fanpai.view.calendar.DateUtil;
import com.iqianbang.fanpai.view.calendar.MonthSignData;

import java.util.Date;

/**
 * @Title: MonthSignView
 * @Package cn.wecoder.signcalendar.library
 * @Description: 一个月日期签到视图
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class MonthSignView extends LinearLayout {
    private SignCalendar signCalendar;
    private MonthSignData mMonthSignData;
    private Context mContext;
    private int dateClick;

    public MonthSignView(Context context) {
        this(context, null);
    }

    public MonthSignView(Context context, AttributeSet attrs) {
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
     * 设置每月视图的签到数据
     * @param monthSignData 每月签到数据
     */
    public void setMonthSignData(MonthSignData monthSignData) {
        mMonthSignData = monthSignData;
        generateChildViews(dateClick);
    }

    /**
     * 生成视图
     */
    private void generateChildViews(int dateClick) {
        int year = mMonthSignData.getYear();
        int month = mMonthSignData.getMonth();

        int monthDays = DateUtil.getDateNum(year, month);

        Date firstDay = new Date(year - 1900, month, 1);
        int weekday = firstDay.getDay();

        int showDays = monthDays + weekday;
        int showRows = (int) Math.ceil(showDays / 7.0);
        int showFlag = 0;
        for(int i=0; i<showRows; i++) {
            LinearLayout weekContainer = new LinearLayout(mContext);
            weekContainer.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams weekContainerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            weekContainerParams.topMargin = (int) mContext.getResources().getDimension(R.dimen.cal_date_v_margin);
            weekContainerParams.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.cal_date_v_margin);
            weekContainer.setLayoutParams(weekContainerParams);

            //修改一排日期的宽度和高度，原先为45dp，45dp
//            int width = (int) mContext.getResources().getDimension(R.dimen.cal_date_width);
//            int height = (int) mContext.getResources().getDimension(R.dimen.cal_date_height);
            int width = (int) mContext.getResources().getDimension(R.dimen.cal_date_width);
            int height = (int) mContext.getResources().getDimension(R.dimen.cal_date_height);

            for(int j=0; j<7; j++) {
                LinearLayout weekDayContainer = new LinearLayout(mContext);
                weekDayContainer.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
                weekDayContainer.setGravity(Gravity.CENTER);
                if(showFlag >= weekday && showFlag < showDays) {
                    DateSignView currentMonthDayView = new DateSignView(mContext);
                    int currentDayIndex = showFlag - weekday + 1;
//                    Date currentDay = new Date(year - 1900, month, currentDayIndex);
                    DateSignData dateSignData = new DateSignData(year,month, currentDayIndex);
                    Date currentDay = dateSignData.getDate();
                    boolean isSigned = mMonthSignData.isDaySigned(dateSignData);
                    boolean isDayReturned = mMonthSignData.isDayReturned(dateSignData);
                    currentMonthDayView.setSignCalendar(signCalendar);
                    currentMonthDayView.setMonthSignView(this);
                    currentMonthDayView.setSignData(currentDay, isSigned, isDayReturned, dateClick);
                    LayoutParams currentMonthDayViewParams = new LayoutParams(width, height);
                    currentMonthDayView.setLayoutParams(currentMonthDayViewParams);
                    currentMonthDayView.setGravity(Gravity.CENTER);
                    weekDayContainer.addView(currentMonthDayView);
                }else {
                    LinearLayout otherMonthDayView = new LinearLayout(mContext);
                    LayoutParams otherMonthDayViewParams = new LayoutParams(width, height);
                    otherMonthDayView.setLayoutParams(otherMonthDayViewParams);
                    otherMonthDayView.setGravity(Gravity.CENTER);
                    weekDayContainer.addView(otherMonthDayView);
                }
                weekContainer.addView(weekDayContainer);
                showFlag++;
            }
            addView(weekContainer);
        }
    }

    //处理点击事件
    public void regenerateChildViews(int dateClick) {
        removeAllViews();
        generateChildViews(dateClick);
    }
}
