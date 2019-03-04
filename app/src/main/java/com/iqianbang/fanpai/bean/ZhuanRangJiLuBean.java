package com.iqianbang.fanpai.bean;

public class ZhuanRangJiLuBean {
	//转让成功金额
	private String tranAmount;
	//折扣率
	private String discountRate;
	//转让发起时间
	private String tranTimeStr;
	// 1-待承接(默认、部分承接)；2-已承接(全部被承接) -已全部转让成功 ；
	// 3-超时下架(1：超时下架；2：标的还款前一天，下架) 4  撤回
	private String status;

	public String getTranAmount() {
		return tranAmount;
	}

	public void setTranAmount(String tranAmount) {
		this.tranAmount = tranAmount;
	}

	public String getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(String discountRate) {
		this.discountRate = discountRate;
	}

	public String getTranTimeStr() {
		return tranTimeStr;
	}

	public void setTranTimeStr(String tranTimeStr) {
		this.tranTimeStr = tranTimeStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
