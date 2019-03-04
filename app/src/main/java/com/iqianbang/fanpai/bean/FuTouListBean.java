package com.iqianbang.fanpai.bean;

import java.util.ArrayList;

public class FuTouListBean {
	private String dftProId;//集合标的id
	private String collectionName;//产品名称
	private String baseRate;//基础利率
	private String totalExtraRate;//活动加息
	private String remainingMoney2;//剩余可售金额
	private ArrayList<FuTouListRateBean> rateList;//加息活动List
	private String dftInvestRecordId;//投资记录id
	private String status;//10  投资持有中 20  转让中
	private String isActivity;//0 非活动标    1  活动标

	public String getIsActivity() {
		return isActivity;
	}

	public void setIsActivity(String isActivity) {
		this.isActivity = isActivity;
	}


	public String getDftProId() {
		return dftProId;
	}

	public void setDftProId(String dftProId) {
		this.dftProId = dftProId;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getTotalExtraRate() {
		return totalExtraRate;
	}

	public void setTotalExtraRate(String totalExtraRate) {
		this.totalExtraRate = totalExtraRate;
	}

	public String getRemainingMoney2() {
		return remainingMoney2;
	}

	public void setRemainingMoney2(String remainingMoney2) {
		this.remainingMoney2 = remainingMoney2;
	}

	public ArrayList<FuTouListRateBean> getRateList() {
		return rateList;
	}

	public void setRateList(ArrayList<FuTouListRateBean> rateList) {
		this.rateList = rateList;
	}

	public String getDftInvestRecordId() {
		return dftInvestRecordId;
	}

	public void setDftInvestRecordId(String dftInvestRecordId) {
		this.dftInvestRecordId = dftInvestRecordId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
