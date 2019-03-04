package com.iqianbang.fanpai.bean;

public class SanBiaoListBean {

	public String scatterInvestId;//散标id
	public String investAmount;//出借金额
	public String yearRate;//历史年化利率
	public String totalActivityRate;//活动加息
	public String startTimeStr;//出借时间
	public String endTimeStr;//到期时间
	public String settlementTimeStr;//结清时间
	public String status ;//10  持有中  20 转让中   40 已结清
	public String borrowName ;//名称
	public boolean proCanTransfer ;//true | false  true 允许转让 ，false不允许转让
	public String transferSuccessTimeStr ;//转让成功时间
	public double bookClearAmount ;//回款金额 >0 代表有回款

	public boolean isProCanTransfer() {
		return proCanTransfer;
	}

	public void setProCanTransfer(boolean proCanTransfer) {
		this.proCanTransfer = proCanTransfer;
	}

	public String getTransferSuccessTimeStr() {
		return transferSuccessTimeStr;
	}

	public void setTransferSuccessTimeStr(String transferSuccessTimeStr) {
		this.transferSuccessTimeStr = transferSuccessTimeStr;
	}


	public String getBorrowName() {
		return borrowName;
	}

	public void setBorrowName(String borrowName) {
		this.borrowName = borrowName;
	}

	public double getBookClearAmount() {
		return bookClearAmount;
	}

	public void setBookClearAmount(double bookClearAmount) {
		this.bookClearAmount = bookClearAmount;
	}

	public String getScatterInvestId() {
		return scatterInvestId;
	}

	public void setScatterInvestId(String scatterInvestId) {
		this.scatterInvestId = scatterInvestId;
	}

	public String getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(String investAmount) {
		this.investAmount = investAmount;
	}

	public String getYearRate() {
		return yearRate;
	}

	public void setYearRate(String yearRate) {
		this.yearRate = yearRate;
	}

	public String getTotalActivityRate() {
		return totalActivityRate;
	}

	public void setTotalActivityRate(String totalActivityRate) {
		this.totalActivityRate = totalActivityRate;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public String getSettlementTimeStr() {
		return settlementTimeStr;
	}

	public void setSettlementTimeStr(String settlementTimeStr) {
		this.settlementTimeStr = settlementTimeStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
