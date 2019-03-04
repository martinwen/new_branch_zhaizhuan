package com.iqianbang.fanpai.bean;

public class TiXianLiuShuiBean {
	private String transMonth;
	private String status;
	private String amount;
	private String fee;
	private String bankPic;
	private String bankName;
	private String cardNum;
	private String transTime;
	private String thirdTime;
	private String accountTime;

	public String getTransMonth() {
		return transMonth;
	}

	public void setTransMonth(String transMonth) {
		this.transMonth = transMonth;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getBankPic() {
		return bankPic;
	}

	public void setBankPic(String bankPic) {
		this.bankPic = bankPic;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getThirdTime() {
		return thirdTime;
	}

	public void setThirdTime(String thirdTime) {
		this.thirdTime = thirdTime;
	}

	public String getAccountTime() {
		return accountTime;
	}

	public void setAccountTime(String accountTime) {
		this.accountTime = accountTime;
	}
	/*"transMonth": "提现月份",
			"status": "状态:申请中0，提现成功1",
			"amount": "提现金额",
			"bankPic": "银行图标",
			"cardNum": "银行卡号",
			"transTime": "提现申请时间",
			"thirdTime": "三方支付处理时间",
			"accountTime": "到账时间"*/

}
