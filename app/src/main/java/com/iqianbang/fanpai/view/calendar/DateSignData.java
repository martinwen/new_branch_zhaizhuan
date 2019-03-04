package com.iqianbang.fanpai.view.calendar;

import java.util.Date;

public class DateSignData {
	private Date date;
	private boolean isPrize;
	private String signFlag;//off待还     return已还

	public DateSignData(){
		
	}
	public DateSignData(int year,int month,int day){
		this.date = new Date(year - 1900, month, day);
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isPrize() {
		return isPrize;
	}
	public void setPrize(boolean isPrize) {
		this.isPrize = isPrize;
	}

	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}

	public String getSignFlag() {
		return signFlag;
	}
}
