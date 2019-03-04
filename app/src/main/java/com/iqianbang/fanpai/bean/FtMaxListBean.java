package com.iqianbang.fanpai.bean;

public class FtMaxListBean {
	private String productName;//产品名称
	private String baseRate;//基础利率
	private String totalActivityRate;//活动加息
	private int progress;//进度
	private String remainingMoney;//剩余可售金额
	private String status;//1  可售 2 预售 4 已售完,5 已截标 // 4、5对应已售完
	private String publishedTime;//预售时间
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getTotalActivityRate() {
		return totalActivityRate;
	}

	public void setTotalActivityRate(String totalActivityRate) {
		this.totalActivityRate = totalActivityRate;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getRemainingMoney() {
		return remainingMoney;
	}

	public void setRemainingMoney(String remainingMoney) {
		this.remainingMoney = remainingMoney;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublishedTime() {
		return publishedTime;
	}

	public void setPublishedTime(String publishedTime) {
		this.publishedTime = publishedTime;
	}

}
