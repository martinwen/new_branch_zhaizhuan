package com.iqianbang.fanpai.bean;

public class ZhuanRangBean {

	public String id;//散标id
	public String investAmount;//出借金额
	public String yearRate;//历史年化利率
	public String startTimeStr;//出借时间
	public String endTimeStr;//到期时间
	public String settlementTimeStr;//结清时间
	public String status ;//10  持有中  20 转让中   40 已结清
	public String transferName ;//名称
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


	public String getTransferName() {
		return transferName;
	}

	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	public double getBookClearAmount() {
		return bookClearAmount;
	}

	public void setBookClearAmount(double bookClearAmount) {
		this.bookClearAmount = bookClearAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
