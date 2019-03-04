package com.iqianbang.fanpai.bean;


import java.io.Serializable;


public class FloatRateBean implements Serializable {

	/*"invest_days": "出借天数",
                "float_rate": "浮动利率",
                "home_day": "默认出借天数：true|false"*/
	public boolean home_day;
	public int invest_days;
	public String float_rate;

	public boolean isHome_day() {
		return home_day;
	}

	public void setHome_day(boolean home_day) {
		this.home_day = home_day;
	}

	public int getInvest_days() {
		return invest_days;
	}

	public void setInvest_days(int invest_days) {
		this.invest_days = invest_days;
	}

	public String getFloat_rate() {
		return float_rate;
	}

	public void setFloat_rate(String float_rate) {
		this.float_rate = float_rate;
	}

}
