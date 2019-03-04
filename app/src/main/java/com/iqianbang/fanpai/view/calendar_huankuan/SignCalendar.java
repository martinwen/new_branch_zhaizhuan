package com.iqianbang.fanpai.view.calendar_huankuan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.view.calendar.MonthSignData;
import com.iqianbang.fanpai.view.calendar_huankuan.MonthSignView;
import com.iqianbang.fanpai.view.calendar.TintedBitmapDrawable;
import com.iqianbang.fanpai.view.calendar.WrapContentViewPager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Title: SignCalendar
 * @Package cn.wecoder.signcalendar.library
 * @Description: 签到日历
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class SignCalendar extends LinearLayout {
    private Context mContext;
    private WrapContentViewPager datePager;
    private int pagerIndex;
    private int monthCount;
    private ArrayList<MonthSignData> mSignDatas;
    private List<View> monthViews;
    private boolean showArrow = true;
    private Date today;
    private boolean prize;
    
    private SignCalendarListener signCalendarListener;

    public interface SignCalendarListener {
        void onDayClick(int date);
    }

    public void setSignCalendarListener(SignCalendarListener signCalendarListener) {
        this.signCalendarListener = signCalendarListener;
    }

    public void setDateClick(int date) {
        if (signCalendarListener != null) {
            signCalendarListener.onDayClick(date);
        }
    }

	public SignCalendar(Context context) {
        super(context);
    }

    public SignCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        initWeekdays();
        initDatePager();
    }

    /**
     * 得到今日日期
     * @return
     */
    public Date getToday() {
        return today;
    }

    /**
     * 设置今日日期
     * @param today
     */
    public void setToday(Date today) {
        this.today = today;
    }

    
    /**
     * 初始化日期字符视图
     */
    private void initWeekdays() {
        LinearLayout weekdaysContainer = new LinearLayout(getContext());
        weekdaysContainer.setOrientation(LinearLayout.HORIZONTAL);
//        weekdaysContainer.setBackgroundColor(mContext.getResources().getColor(R.color.cal_weekdays_background));
        LayoutParams containerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        containerParams.height = (int) mContext.getResources().getDimension(R.dimen.cal_weekdays_height);
        containerParams.topMargin = 10;
        containerParams.leftMargin = (int) mContext.getResources().getDimension(R.dimen.cal_weekdays_h_margin);
        containerParams.rightMargin = (int) mContext.getResources().getDimension(R.dimen.cal_weekdays_h_margin);
        weekdaysContainer.setLayoutParams(containerParams);

        String[] weekdaysStr = getResources().getStringArray(R.array.cal_weekdays);

        for (int i = 0; i < 7; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setText(weekdaysStr[i]);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.cal_weekdays_text_size));
            //修改颜色为红色
            view.setTextColor(mContext.getResources().getColor(R.color.bg_title_red));
//            view.setTextColor(mContext.getResources().getColor(R.color.cal_weekdays_text));
            view.setLayoutParams(new LayoutParams(0, -1, 1));
            weekdaysContainer.addView(view);
        }

        addView(weekdaysContainer);
    }

    /**
     * 初始化用于显示每月签到视图的View Pager
     */
    private void initDatePager() {
        LinearLayout dateContainer = new LinearLayout(mContext);
        LayoutParams containerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dateContainer.setLayoutParams(containerParams);
//        dateContainer.setBackgroundColor(mContext.getResources().getColor(R.color.cal_pager_background));

        datePager = new WrapContentViewPager(mContext);

        LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pagerParams.leftMargin = (int) mContext.getResources().getDimension(R.dimen.cal_pager_h_margin);
        pagerParams.rightMargin = (int) mContext.getResources().getDimension(R.dimen.cal_pager_h_margin);
        datePager.setLayoutParams(pagerParams);
        dateContainer.addView(datePager);
        addView(dateContainer);
    }

    /**
     * 给签到日历设置签到数据
     * @param signDatas 签到数据
     */
    public void setSignDatas(ArrayList<MonthSignData> signDatas) {
        mSignDatas = signDatas;
        monthCount = signDatas.size();
        monthViews = new ArrayList<View>();

        for (int i = 0; i < monthCount; i++) {
            MonthSignView monthSignView = new MonthSignView(mContext);
            monthSignView.setSignCalendar(this);
            monthSignView.setMonthSignData(signDatas.get(i));
            monthViews.add(monthSignView);
        }

        pagerIndex = monthCount - 1;
        datePager.setChildViews(monthViews);
        datePager.setAdapter(new MyPagerAdapter(monthViews));
        datePager.setCurrentItem(pagerIndex);

    }

    /**
     * ViewPager适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
    }



    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_PAGER_INDEX = "status_pager_index";

    /**
     * 销毁时存储当前页面数值
     */
    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putInt(STATUS_PAGER_INDEX, pagerIndex);
        return bundle;
    }

    /**
     * 重建时恢复当前页面数值
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            pagerIndex = bundle.getInt(STATUS_PAGER_INDEX);
            datePager.setCurrentItem(pagerIndex);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
