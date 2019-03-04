package com.iqianbang.fanpai.utils;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateUtils {

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();

	/** 格式化年月日 yyyyMMdd **/
	private static SimpleDateFormat ymdDf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 获取SimpleDateFormat
	 * @param pattern 日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException 异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern, Locale.US);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}
	/**
	 * 获取下一个日期 YYYY-MM-DD
	 * @param date
	 * @return
	 * @author zhang.cl  2015年3月26日
	 */
	public static Date nexDate(Date date){
		return addDay(formateDate(date),1);
	}
	/**
	 * 获取日期中的某数值。如获取月份
	 * @param date 日期
	 * @param dateType 日期格式
	 * @return 数值
	 */
	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * @param date 日期字符串
	 * @param dateType 类型
	 * @param amount 数值
	 * @return 计算后日期字符串
	 */
	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * @param date 日期
	 * @param dateType 类型
	 * @param amount 数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	/**
	 * 获取精确的日期
	 * @param timestamps 时间long集合
	 * @return 日期
	 */
	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0;
		Map<Long, long[]> map = new HashMap<Long, long[]>();
		List<Long> absoluteValues = new ArrayList<Long>();

		if (timestamps != null && timestamps.size() > 0) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
						absoluteValues.add(absoluteValue);
						long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };
						map.put(absoluteValue, timestampTmp);
					}
				}

				// 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
				// 因此不能将minAbsoluteValue取默认值0
				long minAbsoluteValue = -1;
				if (!absoluteValues.isEmpty()) {
					minAbsoluteValue = absoluteValues.get(0);
					for (int i = 1; i < absoluteValues.size(); i++) {
						if (minAbsoluteValue > absoluteValues.get(i)) {
							minAbsoluteValue = absoluteValues.get(i);
						}
					}
				}

				if (minAbsoluteValue != -1) {
					long[] timestampsLastTmp = map.get(minAbsoluteValue);

					long dateOne = timestampsLastTmp[0];
					long dateTwo = timestampsLastTmp[1];
					if (absoluteValues.size() > 1) {
						timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;
					}
				}
			} else {
				timestamp = timestamps.get(0);
			}
		}

		if (timestamp != 0) {
			date = new Date(timestamp);
		}
		return date;
	}

	/**
	 * 判断字符串是否为日期字符串
	 * @param date 日期字符串
	 * @return true or false
	 */
	public static boolean isDate(String date) {
		boolean isDate = false;
		if (date != null) {
			if (getDateStyle(date) != null) {
				isDate = true;
			}
		}
		return isDate;
	}

	/**
	 * 获取日期字符串的日期风格。失敗返回null。
	 * @param date 日期字符串
	 * @return 日期风格
	 */
	public static DateStyle getDateStyle(String date) {
		DateStyle dateStyle = null;
		Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
		List<Long> timestamps = new ArrayList<Long>();
		for (DateStyle style : DateStyle.values()) {
			if (style.isShowOnly()) {
				continue;
			}
			Date dateTmp = null;
			if (date != null) {
				try {
					ParsePosition pos = new ParsePosition(0);
					dateTmp = getDateFormat(style.getValue()).parse(date, pos);
					if (pos.getIndex() != date.length()) {
						dateTmp = null;
					}
				} catch (Exception e) {
				}
			}
			if (dateTmp != null) {
				timestamps.add(dateTmp.getTime());
				map.put(dateTmp.getTime(), style);
			}
		}
		Date accurateDate = getAccurateDate(timestamps);
		if (accurateDate != null) {
			dateStyle = map.get(accurateDate.getTime());
		}
		return dateStyle;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * @param date 日期字符串
	 * @return 日期
	 */
	public static Date StringToDate(String date) {
		DateStyle dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * @param date 日期字符串
	 * @param pattern 日期格式
	 * @return 日期
	 */
	public static Date StringToDate(String date, String pattern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * @param date 日期字符串
	 * @param dateStyle 日期风格
	 * @return 日期
	 */
	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle != null) {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	/**
	 * 讲日期转换成YYYY-MM-DD格式,失败返回null
	 * @return
	 * @author zhang.cl 2014年11月3日
	 */
	public static Date formateDate(Date date) {
		try {
			String dateString = getDateFormat(DateStyle.YYYY_MM_DD.getValue()).format(date);
			return getDateFormat(DateStyle.YYYY_MM_DD.getValue()).parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 判断两个日期是否为同一
	 * @param one
	 * @param two
	 * @return
	 * @author zhang.cl 2014年11月28日
	 */
	public static boolean isSameDay(Date one, Date two) {
		try {
			return formateDate(one).equals(formateDate(two));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * @param date 日期
	 * @param pattern 日期格式
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, String pattern) {
		String dateString = null;
		if (date != null) {
			try {
				dateString = getDateFormat(pattern).format(date);
			} catch (Exception e) {
			}
		}
		return dateString;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * @param date 日期
	 * @param dateStyle 日期风格
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, DateStyle dateStyle) {
		String dateString = null;
		if (dateStyle != null) {
			dateString = DateToString(date, dateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param newPattern 新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String newPattern) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param newDateStyle 新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle newDateStyle) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newDateStyle);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param olddPattern 旧日期格式
	 * @param newPattern 新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, String newPattern) {
		return DateToString(StringToDate(date, olddPattern), newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param olddDteStyle 旧日期风格
	 * @param newParttern 新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, String newParttern) {
		String dateString = null;
		if (olddDteStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newParttern);
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param olddPattern 旧日期格式
	 * @param newDateStyle 新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern, DateStyle newDateStyle) {
		String dateString = null;
		if (newDateStyle != null) {
			dateString = StringToString(date, olddPattern, newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * @param date 旧日期字符串
	 * @param olddDteStyle 旧日期风格
	 * @param newDateStyle 新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
		String dateString = null;
		if (olddDteStyle != null && newDateStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * @param date 日期
	 * @param yearAmount 增加数量。可为负数
	 * @return 增加年份后的日期字符串
	 */
	public static String addYear(String date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * @param date 日期
	 * @param yearAmount 增加数量。可为负数
	 * @return 增加年份后的日期
	 */
	public static Date addYear(Date date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * @param date 日期
	 * @param monthAmount 增加数量。可为负数
	 * @return 增加月份后的日期字符串
	 */
	public static String addMonth(String date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * @param date 日期
	 * @param monthAmount 增加数量。可为负数
	 * @return 增加月份后的日期
	 */
	public static Date addMonth(Date date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * @param date 日期字符串
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加天数后的日期字符串
	 */
	public static String addDay(String date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * @param date 日期
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * @param date 日期字符串
	 * @param hourAmount 增加数量。可为负数
	 * @return 增加小时后的日期字符串
	 */
	public static String addHour(String date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的小时。失败返回null。
	 * @param date 日期
	 * @param hourAmount 增加数量。可为负数
	 * @return 增加小时后的日期
	 */
	public static Date addHour(Date date, int hourAmount) {
		return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * @param date 日期字符串
	 * @param minuteAmount 增加数量。可为负数
	 * @return 增加分钟后的日期字符串
	 */
	public static String addMinute(String date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的分钟。失败返回null。
	 * @param date 日期
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加分钟后的日期
	 */
	public static Date addMinute(Date date, int minuteAmount) {
		return addInteger(date, Calendar.MINUTE, minuteAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * @param date 日期字符串
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加秒钟后的日期字符串
	 */
	public static String addSecond(String date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 增加日期的秒钟。失败返回null。
	 * @param date 日期
	 * @param dayAmount 增加数量。可为负数
	 * @return 增加秒钟后的日期
	 */
	public static Date addSecond(Date date, int secondAmount) {
		return addInteger(date, Calendar.SECOND, secondAmount);
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * @param date 日期字符串
	 * @return 年份
	 */
	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * @param date 日期
	 * @return 年份
	 */
	public static int getYear(Date date) {
		return getInteger(date, Calendar.YEAR);
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * @param date 日期字符串
	 * @return 月份
	 */
	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * @param date 日期
	 * @return 月份
	 */
	public static int getMonth(Date date) {
		return getInteger(date, Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * @param date 日期字符串
	 * @return 天
	 */
	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * @param date 日期
	 * @return 天
	 */
	public static int getDay(Date date) {
		return getInteger(date, Calendar.DATE);
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * @param date 日期字符串
	 * @return 小时
	 */
	public static int getHour(String date) {
		return getHour(StringToDate(date));
	}

	/**
	 * 获取日期的小时。失败返回0。
	 * @param date 日期
	 * @return 小时
	 */
	public static int getHour(Date date) {
		return getInteger(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * @param date 日期字符串
	 * @return 分钟
	 */
	public static int getMinute(String date) {
		return getMinute(StringToDate(date));
	}

	/**
	 * 获取日期的分钟。失败返回0。
	 * @param date 日期
	 * @return 分钟
	 */
	public static int getMinute(Date date) {
		return getInteger(date, Calendar.MINUTE);
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * @param date 日期字符串
	 * @return 秒钟
	 */
	public static int getSecond(String date) {
		return getSecond(StringToDate(date));
	}

	/**
	 * 获取日期的秒钟。失败返回0。
	 * @param date 日期
	 * @return 秒钟
	 */
	public static int getSecond(Date date) {
		return getInteger(date, Calendar.SECOND);
	}

	/**
	 * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
	 * @param date 日期字符串
	 * @return 日期
	 */
	public static String getDate(String date) {
		return StringToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期。默认yyyy-MM-dd格式。失败返回null。
	 * @param date 日期
	 * @return 日期
	 */
	public static String getDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * @param date 日期字符串
	 * @return 时间
	 */
	public static String getTime(String date) {
		return StringToString(date, DateStyle.HH_MM_SS);
	}

	/**
	 * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
	 * @param date 日期
	 * @return 时间
	 */
	public static String getTime(Date date) {
		return DateToString(date, DateStyle.HH_MM_SS);
	}

	
	

	/**
	 * 获得两个日期相差的天数的绝对值
	 * @param data
	 * @param otherDate
	 * @return
	 */
	public static int daysBetween(Date date,Date otherDate){
		return Math.abs(getIntervalDays(date, otherDate));
	}
	
	/**
	 * 获取两个日期相差的天数
	 * @param date 日期字符串
	 * @param otherDate 另一个日期字符串
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	/**
	 * @param date 日期
	 * @param otherDate 另一个日期
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = DateUtils.StringToDate(DateUtils.getDate(date), DateStyle.YYYY_MM_DD);
		Date otherDateTmp = DateUtils.StringToDate(DateUtils.getDate(otherDate), DateStyle.YYYY_MM_DD);
		if (dateTmp != null && otherDateTmp != null) {
			long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
			num = (int) (time / (24 * 60 * 60 * 1000));
		}
		return num;
	}

	/**
	 * 获取本月天数
	 * @param d1
	 * @return
	 * @author zhang.cl 2014年11月28日
	 */
	public static int getMonthDays(Date d1) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到下一个月日期;用于利息计算
	 * @param date
	 * @return
	 * @author zhang.cl 2014年11月28日
	 */
	public static Date getNextMonth(Date date) {
		Date nextMonthDay = addMonth(date, 1);
		if (getDay(date) > getMonthDays(nextMonthDay)) {
			nextMonthDay = addDay(nextMonthDay, 1);
		}
		return nextMonthDay;
	}

	/**
	 * 设置日期天数
	 * @param date
	 * @param day
	 * @return
	 * @author zhang.cl 2014年11月28日
	 */
	public static Date setDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取两个日期相差的天数，后一个日期的天数减去前一个日期的天数。也就是说，如果后者时间大于前者，则返回正值，否则返回负值。
	 * @param date 日期
	 * @param otherDate 另一个日期
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int calculateIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = DateUtils.StringToDate(DateUtils.getDate(date), DateStyle.YYYY_MM_DD);
		Date otherDateTmp = DateUtils.StringToDate(DateUtils.getDate(otherDate), DateStyle.YYYY_MM_DD);
		if (dateTmp != null && otherDateTmp != null) {
			long time = otherDateTmp.getTime() - dateTmp.getTime();
			num = (int) (time / (24 * 60 * 60 * 1000));
		}
		return num;
	}

	public static int getIntervalDaysToToday(Date date) {
		return getIntervalDays(date, new Date());
	}
	
	public static int getIntervalMonths(Date startDate, Date endDate){
		int i = 0;
		while(DateUtils.addDay(DateUtils.addMonth(startDate, i), -1).before(endDate)){
			i++;
		}
		return i;
	}

	/**
	 * 判断日期是否在当前的月份
	 * @return
	 */
	public static boolean isInCurrentMonth(Date date) {
		if (getMonth(new Date()) - getMonth(date) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前年
	 * @return
	 */
	public static int getCurrentYear() {
		return getYear(new Date());
	}

	/**
	 * @description 获得当前时间，格式为：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @author baiyanbing 2014年8月15日
	 */
	public static String now() {
		return DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

    /**
	 * 获取指定日期当月最后一天 date为null时为当前月
	 * 
	 * @param date
	 * @return Date
	 */
	public static String getLastDayOfMonth(Date date,DateStyle dateStyle) {
		if(date == null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		return  DateUtils.DateToString(c.getTime(), dateStyle) ;
	}
	
    /**
	 * 获取指定日期当月最后一天 date为null时为当前月
	 * 
	 * @param date
	 * @return Date
	 */
	public static Date getLastDayOfMonth(Date date) {
		if(date == null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		return c.getTime() ;
	}
	
	
    /**
	 * 获取指定日期当月最后一天 date为null时为当前月
	 * 
	 * @param date
	 * @return Date
	 */
	public static Date getLastSecondOfMonth(Date date) {
		if(date == null){
			return null;
		}
		return  getEndTime(getLastDayOfMonth(date));
	}
	
	public static String format(Date date){
		return DateUtils.DateToString(date, DateStyle.YYYY_MM_DD);
	}
	
	/**
	 * 获得指定日期的零点
	 * @param date
	 * @return
	 */
	public static Date getStartTime(Date date){
		return DateUtils.formateDate(date);
	}
	
	/**
	 * 获得指定日期的最后一秒时间
	 * @param date
	 * @return
	 */
	public static Date getEndTime(Date date){
		return DateUtils.addSecond(DateUtils.addDay(DateUtils.formateDate(date), 1), -1);
	}
	
	/**
	 * 转换unix_time
	 * @param time
	 * @param style
	 * @return
	 * @author baiyanbing 
	 * @date 2016年4月14日下午2:10:03
	 */
	public static String fromUnixTime(Integer time, DateStyle style){
		if(time == null){
			return null;
		}
		long l = time * 1000L;
		return DateToString(new Date(l), style);
	}
	
	public static Long toUnixTime(Date date){
		if(date == null){
			return null;
		}
		Long unixtime = date.getTime() / 1000L;
		return unixtime;
	}
	
	/**
	 * 转换unix_time
	 * @param time
	 * @param style
	 * @return
	 * @author baiyanbing 
	 * @date 2016年4月14日下午2:10:03
	 */
	public static Date fromUnixTime(Integer time){
		if(time == null){
			return null;
		}
		long l = time * 1000L;
		return new Date(l);
	}
	
	/**
	 * 取当前日期  加  N  天
	 * Description: 
	 * @param
	 * @return String
	 * @throws
	 * @Author xuhongwei
	 * Create Date: 2016年7月26日 下午4:20:41
	 */
	@SuppressWarnings("static-access")
	public static String getCurrentNextTRXDATE(Date date,int addDay) {
	     Calendar   calendar   =   new   GregorianCalendar(); 
	     calendar.setTime(date); 
	     calendar.add(calendar.DATE,addDay);//把日期增加 n 天.正数往后推,负数往前移动 
	     date=calendar.getTime();   
	     return ymdDf1.format(date);
	}

	/**
	 * 获得指定日期的当月最后一秒的时间
	 * @param date
	 * @return
	 */
	public static Date getLastTimeOfMonth(Date date){
		String lastDayStr = getLastDayOfMonth(date, DateStyle.YYYY_MM_DD);
		return getEndTime(StringToDate(lastDayStr));
	}
}