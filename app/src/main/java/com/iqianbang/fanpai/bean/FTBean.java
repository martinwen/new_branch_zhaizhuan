package com.iqianbang.fanpai.bean;

public class FTBean {

	public String pro_code;//项目编号
	public String id;//出借记录ID
	public String trans_seq;//交易流水号
	public String days;//出借天数
	public String rec_fee; //赎回手续费
	public String status;//出借状态 :1出借成功、2已赎回
	public String invest_amount; // 出借金额
	public String total_interest;//收益
	public String trans_time;//出借时间
	public String year_rate;//年化收益率
	public String in_lock_day;//是否在锁定期 1 是 2否
	public String lock_expire_date;//锁定期

	public String getIn_lock_day() {
		return in_lock_day;
	}

	public void setIn_lock_day(String in_lock_day) {
		this.in_lock_day = in_lock_day;
	}

	public String getLock_expire_date() {
		return lock_expire_date;
	}

	public void setLock_expire_date(String lock_expire_date) {
		this.lock_expire_date = lock_expire_date;
	}


	
	public String getPro_code() {
		return pro_code;
	}
	public void setPro_code(String pro_code) {
		this.pro_code = pro_code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrans_seq() {
		return trans_seq;
	}
	public void setTrans_seq(String trans_seq) {
		this.trans_seq = trans_seq;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getRec_fee() {
		return rec_fee;
	}
	public void setRec_fee(String rec_fee) {
		this.rec_fee = rec_fee;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInvest_amount() {
		return invest_amount;
	}
	public void setInvest_amount(String invest_amount) {
		this.invest_amount = invest_amount;
	}
	public String getTotal_interest() {
		return total_interest;
	}
	public void setTotal_interest(String total_interest) {
		this.total_interest = total_interest;
	}
	public String getTrans_time() {
		return trans_time;
	}
	public void setTrans_time(String trans_time) {
		this.trans_time = trans_time;
	}
	public String getYear_rate() {
		return year_rate;
	}
	public void setYear_rate(String year_rate) {
		this.year_rate = year_rate;
	}
	

}
