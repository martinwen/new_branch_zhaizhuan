package com.iqianbang.fanpai.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.iqianbang.fanpai.global.FanPaiApplication.context;

public class CacheUtils {
	private static String FILE_NAME = "fanpai";
	private static SharedPreferences sp;
	
	public static String BYTE="BYTE";// 手势密码常量
	public static String INVITECODE="inviteCode";//邀请码
	public static String BASEACCTBAL="baseAcctBal";// 账户余额
	public static String REWARDUSEDAMOUNT="rewardUsedAmount";// 已使用奖金
	public static String BANKNAME="bankName";// 开户行名称
	public static String BANKPIC="bankPic";// 开户行图标
	public static String REALNAME="realName";//姓名
	public static String IDNO="idNo";//身份证号
	public static String ADDRESS="address";// 详细地址
	public static String ZIPCODE="zipCode";// 邮政编码
	public static String CARDNUM="cardNum";// 银行卡号
	public static String CARDPHONE="cardPhone";// 银行预留手机号
	public static String LOGINPHONE="loginphone";//登录手机号
	public static String LOGINPASSWORD="loginpassword";//登录密码
	public static String REWARDACCTBAL="rewardAcctBal";// 奖金余额
	public static String SINGLETRANSLIMIT="singleTransLimit";// 银行单笔限额
	public static String SINGLEDAYLIMIT="singleDayLimit";// 银行单日限额
	public static String TOTALASSETS="totalAssets";// 总资产
	public static String FHACCTBAL="fhAcctBal";// 饭盒余额
	public static String FWACCTBAL="fwAcctBal";// 饭碗余额
	public static String FTACCTBAL="ftAcctBal";// 饭桶余额
	public static String FHYESTERDAYINCOME="fhYesterdayIncome";// 饭盒昨日收益
	public static String FHTOTALINCOME="fhTotalIncome";// 饭盒累计收益
	public static String DFTYESTERDAYINCOME="dftYesterdayIncome";// 大饭团昨日收益
	public static String DFTTOTALINCOME="dftTotalIncome";// 饭碗已赚收益
	public static String FTYESTERDAYINCOME="ftYesterdayIncome";// 饭桶昨日收益
	public static String FTTOTALINCOME="ftTotalIncome";// 饭桶累计收益
	public static String TOTALRECHARGEAMOUNT="totalRechargeAmount";// 充值总金额
	public static String TOTALCASHAMOUNT="totalCashAmount";// 提现总金额
	public static String TOTALCASHINGMONEY="totalCashingMoney";// 提现总金额
	public static String EMAIL="email";// 邮箱

	
	
	// 保存boolean值
	public static void putBoolean(String key, boolean value) {
		if (sp == null) {
			sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	// 取boolean值
	public static boolean getBoolean(String key, boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	
	public static void putString(String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	public static String getString(String key, String defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	
}
