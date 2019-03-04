package com.iqianbang.fanpai.view.calendar_huankuan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.view.calendar.DateUtil;
import com.iqianbang.fanpai.view.calendar.MonthSignData;
import com.iqianbang.fanpai.view.calendar_huankuan.SignCalendar;

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
    private boolean isDayReturned;
    private Date date = new Date();
    private TextView tv_day;
    private ImageView iv_point;
    private ImageView iv_text_bg;
    private RelativeLayout rl_day;
    private Context mContext;
    private SignCalendar signCalendar;
    private MonthSignView monthSignView;
    private View view;
    private int dateClick;

    public DateSignView(Context context) {
        this(context, null);
    }

    public DateSignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        onCreateView();
    }

    private void onCreateView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.calendar_huankuan_day_layout, this,true);

        tv_day = (TextView)view.findViewById(R.id.tv_day);
        iv_point = (ImageView)view.findViewById(R.id.iv_point);
        iv_text_bg = (ImageView)view.findViewById(R.id.iv_text_bg);

        rl_day = (RelativeLayout)view.findViewById(R.id.rl_day);
        rl_day.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                monthSignView.regenerateChildViews(date.getDate());
                signCalendar.setDateClick(date.getDate());
            }
        });
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
     * 设置每日签到视图的数据
     * @param date 日期
     * @param signed 签到状态
     */
    public void setSignData(Date date, boolean signed, boolean isDayReturned, int dateClick) {
        this.date = date;
        this.signed = signed;
        this.isDayReturned = isDayReturned;
        this.dateClick = dateClick;
        generateChildViews();
    }

    /**
     * 生成视图
     */
    private void generateChildViews() {
        int day = date.getDate();

        Date nowDate = new Date();
        if(signCalendar.getToday() != null) {
            nowDate = signCalendar.getToday();
        }
        int result = DateUtil.compareDateDay(date, nowDate);

        tv_day.setText(day + "");
        if(signed) {//有还款
            if (isDayReturned) {//已还款
                iv_point.setBackgroundResource(R.drawable.point_gray);
            }else {//未还款
                iv_point.setBackgroundResource(R.drawable.point_red);
            }
        }

        if(result == 0){//当天日期
            iv_text_bg.setVisibility(View.VISIBLE);
        }

        if(day == dateClick){//处理点击事件
            tv_day.setTextColor(mContext.getResources().getColor(R.color.global_whitecolor));
            tv_day.setBackgroundResource(R.drawable.shape_calendar_solid_oval);
        }
    }

    public void setMonthSignView(MonthSignView monthSignView) {
        this.monthSignView = monthSignView;
    }
}
