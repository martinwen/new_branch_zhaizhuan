package com.iqianbang.fanpai.bean;

public class FHInvestingBean {

	public String borrowName;//项目名称
	public String registerAgency;//登记备案机构
	public String exchRecordNbr; //登记备案编号
	public String raiseObj; // 募集对象
	public String raiseTotalAmount;//募集资金
	public String startTimeStr; // 产品开始时间
	public String deadlineStr;//产品到期日
	public String proOverview;//项目概况
	public String transConditions;//转让条件
	public String guaranteeWay;//保障方式

	public String getBorrowName() {
		return borrowName;
	}

	public void setBorrowName(String borrowName) {
		this.borrowName = borrowName;
	}

	public String getRegisterAgency() {
		return registerAgency;
	}

	public void setRegisterAgency(String registerAgency) {
		this.registerAgency = registerAgency;
	}

	public String getExchRecordNbr() {
		return exchRecordNbr;
	}

	public void setExchRecordNbr(String exchRecordNbr) {
		this.exchRecordNbr = exchRecordNbr;
	}

	public String getRaiseObj() {
		return raiseObj;
	}

	public void setRaiseObj(String raiseObj) {
		this.raiseObj = raiseObj;
	}

	public String getRaiseTotalAmount() {
		return raiseTotalAmount;
	}

	public void setRaiseTotalAmount(String raiseTotalAmount) {
		this.raiseTotalAmount = raiseTotalAmount;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getDeadlineStr() {
		return deadlineStr;
	}

	public void setDeadlineStr(String deadlineStr) {
		this.deadlineStr = deadlineStr;
	}

	public String getProOverview() {
		return proOverview;
	}

	public void setProOverview(String proOverview) {
		this.proOverview = proOverview;
	}

	public String getTransConditions() {
		return transConditions;
	}

	public void setTransConditions(String transConditions) {
		this.transConditions = transConditions;
	}

	public String getGuaranteeWay() {
		return guaranteeWay;
	}

	public void setGuaranteeWay(String guaranteeWay) {
		this.guaranteeWay = guaranteeWay;
	}
}
