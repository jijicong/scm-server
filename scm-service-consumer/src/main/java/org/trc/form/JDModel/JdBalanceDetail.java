package org.trc.form.JDModel;

import java.util.Date;

/**
 * 京东余额明细记录
 * Created by hzwyz on 2017/6/22 0022.
 */
public class JdBalanceDetail {
    //余额明细Id(主键)
    private Long id;

    //京东订单号
    private String orderId;

    //金额
    private Long amount;

    //业务类型名
    private String tradeTypeName;

    //业务类型
    private String tradeType;

    //订单创建时间
    private Date createdDate;

    //京东PIN
    private String pin;

    //业务号
    private String tradeNo;

    //账户类型
    private String accountType;

    //备注信息
    private String notePub;

    //余额明细的对账状态
    private Integer state;

    //异常说明
    private String errMsg;

    //收入
    private Long income;

    //支出
    private Long outcome;

    //检查结果 0--未检查 1--已检查
    private Integer checkState=0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getNotePub() {
        return notePub;
    }

    public void setNotePub(String notePub) {
        this.notePub = notePub;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long getIncome() {
        return income;
    }

    public void setIncome(Long income) {
        this.income = income;
    }

    public Long getOutcome() {
        return outcome;
    }

    public void setOutcome(Long outcome) {
        this.outcome = outcome;
    }

    public Integer getCheckState() {
        return checkState;
    }

    public void setCheckState(Integer checkState) {
        this.checkState = checkState;
    }
}
