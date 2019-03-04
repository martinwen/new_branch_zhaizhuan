package com.iqianbang.fanpai.bean;

public class ZhuanRangListBean {
	private String baseRate;//基础利率
	private String borrowName;//债权名字
	private String borrowSalableBal;//剩余可售
	private String discountRate;//折让率
	private String term;//剩余期限
	private String id;

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getBorrowName() {
		return borrowName;
	}

	public void setBorrowName(String borrowName) {
		this.borrowName = borrowName;
	}

	public String getBorrowSalableBal() {
		return borrowSalableBal;
	}

	public void setBorrowSalableBal(String borrowSalableBal) {
		this.borrowSalableBal = borrowSalableBal;
	}

	public String getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(String discountRate) {
		this.discountRate = discountRate;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
