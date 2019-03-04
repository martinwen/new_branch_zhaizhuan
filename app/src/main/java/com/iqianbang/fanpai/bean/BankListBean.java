package com.iqianbang.fanpai.bean;

public class BankListBean {

	public String bankCode;
	public String bankName;
	public String bankPic;
	public float quickSingleLimit;
	public float quickDailyLimit;
	public float webSingleLimit;
	public float webDailyLimit;

	public float getQuickSingleLimit() {
		return quickSingleLimit;
	}

	public void setQuickSingleLimit(float quickSingleLimit) {
		this.quickSingleLimit = quickSingleLimit;
	}

	public float getWebDailyLimit() {
		return webDailyLimit;
	}

	public void setWebDailyLimit(float webDailyLimit) {
		this.webDailyLimit = webDailyLimit;
	}

	public float getWebSingleLimit() {
		return webSingleLimit;
	}

	public void setWebSingleLimit(float webSingleLimit) {
		this.webSingleLimit = webSingleLimit;
	}

	public float getQuickDailyLimit() {
		return quickDailyLimit;
	}

	public void setQuickDailyLimit(float quickDailyLimit) {
		this.quickDailyLimit = quickDailyLimit;
	}

	public String getCardIconUrl() {
		return bankPic;
	}
	public void setCardIconUrl(String cardIconUrl) {
		this.bankPic = cardIconUrl;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}
