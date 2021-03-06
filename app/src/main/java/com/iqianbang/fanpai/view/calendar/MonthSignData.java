package com.iqianbang.fanpai.view.calendar;

import java.util.ArrayList;
import java.util.Date;

/**
 * @Title: MonthSignData
 * @Package cn.wecoder.signcalendar.library
 * @Description: 一个月签到数据
 * @Author Jim
 * @Date 15/10/20
 * @Time 下午2:50
 * @Version
 */
public class MonthSignData {
    private int mYear;
    private int mMonth;

    private ArrayList<DateSignData> signDates;

    /**
     * 得到月签到数据的月份
     * @return 签到数据的月份
     */
    public int getMonth() {
        return mMonth;
    }

    /**
     * 设置月签到数据的月份
     */
    public void setMonth(int month) {
        mMonth = month;
    }

    /**
     * 得到月签到数据的年份
     * @return 签到数据的年份
     */
    public int getYear() {
        return mYear;
    }

    /**
     * 设置月签到数据的年份
     */
    public void setYear(int year) {
        mYear = year;
    }

    /**
     * 得到月签到数据的已经签到的日期
     */
    public ArrayList<DateSignData> getSignDates() {
        return signDates;
    }

    /**
     * 设置月签到数据的已经签到的日期
     */
    public void setSignDates(ArrayList<DateSignData> signDates) {
        this.signDates = signDates;
    }

    /**
     * 某一天的签到状态
     * @param date 日期
     * @return 签到状态
     */
    public boolean isDaySigned(DateSignData dateSignData) {
        Date date = dateSignData.getDate();
        int year = date.getYear() + 1900;
        int month = date.getMonth();

        if(year == mYear && month == mMonth) {
            for (DateSignData mDate : signDates) {
                if(DateUtil.compareDateDay(mDate.getDate(), date) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 签到是否有奖
     * @param date 日期
     * @return 签到状态
     */
    public boolean isDayPrized(DateSignData dateSignData) {
    	 Date date = dateSignData.getDate();
         int year = date.getYear() + 1900;
         int month = date.getMonth();

         if(year == mYear && month == mMonth) {
             for (DateSignData mDate : signDates) {
                 if(DateUtil.compareDateDay(mDate.getDate(), date) == 0) {
                     return mDate.isPrize();
                 }
             }
         }
         return false;
    }
    /**
     * 还款状态
     * @param date 日期
     * @return 签到状态
     */
    public boolean isDayReturned(DateSignData dateSignData) {
    	 Date date = dateSignData.getDate();
         int year = date.getYear() + 1900;
         int month = date.getMonth();

         if(year == mYear && month == mMonth) {
             for (DateSignData mDate : signDates) {
                 if(DateUtil.compareDateDay(mDate.getDate(), date) == 0) {
                     return "return".equals(mDate.getSignFlag());//true 已还
                 }
             }
         }
         return false;
    }
}
