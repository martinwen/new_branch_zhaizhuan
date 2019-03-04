package com.iqianbang.fanpai.bean;

public class BiaoDeBean {
    //标的ID
    private String bid;
    //标的名称
    private String borrowName;
    //借款金额
    private String packageMoney;
    //标的期限
    private String borrowTerm;

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }


    public String getPackageMoney() {
        return packageMoney;
    }

    public void setPackageMoney(String packageMoney) {
        this.packageMoney = packageMoney;
    }


    public String getBorrowTerm() {
        return borrowTerm;
    }

    public void setBorrowTerm(String borrowTerm) {
        this.borrowTerm = borrowTerm;
    }
}
