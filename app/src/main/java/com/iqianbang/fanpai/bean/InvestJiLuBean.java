package com.iqianbang.fanpai.bean;

public class InvestJiLuBean {
	//出借用户
	private String userName;
	//出借金额
	private String amount;
	//出借日期(年月日)
	private String transDateStr;
	//出借日期(时分秒)
	private String transTimeStr;
	//出借日期(年月日时分秒)
	private String transDate;

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTransDateStr() {
		return transDateStr;
	}

	public void setTransDateStr(String transDateStr) {
		this.transDateStr = transDateStr;
	}

	public String getTransTimeStr() {
		return transTimeStr;
	}

	public void setTransTimeStr(String transTimeStr) {
		this.transTimeStr = transTimeStr;
	}
}
