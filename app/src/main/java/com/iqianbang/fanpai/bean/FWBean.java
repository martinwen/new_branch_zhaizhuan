package com.iqianbang.fanpai.bean;

public class FWBean {

	public String collectionName;//投资记录名称
	public String transTime;//出借时间
	public String lockExpireTime;//锁定日期
	public String isLock; //1 | 0  ，1 在锁定期。则展示lockExpireTime
	public String status; // 10持有中、20转让中、30转让成功、40已结清
	public String transferFlag;//1:  有转让记录  0  无转让记录  当transferFlag为1 且status为20的时候， 展示（已部分转让成功）字样
	public String investAmount; // 出借金额
	public String yearRate;//预期结利率
	public String accumulativeIncome;//累计收益
	public String recTimes;//用户本月已经转让次数
	public String recTimesLimit;//用户每月可转让次数上限
	public String id;
	public String showBookType;//预约类型  1、展示复投按钮 2、 无任何标记 3、展示已预约复投
	public String repeatedDays;//复投天数

	public String getShowBookType() {
		return showBookType;
	}

	public void setShowBookType(String showBookType) {
		this.showBookType = showBookType;
	}

	public String getRepeatedDays() {
		return repeatedDays;
	}

	public void setRepeatedDays(String repeatedDays) {
		this.repeatedDays = repeatedDays;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getLockExpireTime() {
		return lockExpireTime;
	}

	public void setLockExpireTime(String lockExpireTime) {
		this.lockExpireTime = lockExpireTime;
	}

	public String getIsLock() {
		return isLock;
	}

	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
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

	public String getAccumulativeIncome() {
		return accumulativeIncome;
	}

	public void setAccumulativeIncome(String accumulativeIncome) {
		this.accumulativeIncome = accumulativeIncome;
	}

	public String getRecTimes() {
		return recTimes;
	}

	public void setRecTimes(String recTimes) {
		this.recTimes = recTimes;
	}

	public String getRecTimesLimit() {
		return recTimesLimit;
	}

	public void setRecTimesLimit(String recTimesLimit) {
		this.recTimesLimit = recTimesLimit;
	}
}
