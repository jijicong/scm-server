package org.trc.form.external;

import java.math.BigDecimal;

/**
 * Created by hzwyz on 2017/10/9 0009.
 */
public class BalanceDetailDTO {

    private Long id;
    //京东订单号
    private String orderId;

    //业务类型名
    private String tradeTypeName;

    //订单创建时间
    private String createdDate;

    //京东PIN
    private String pin;

    //业务号
    private String tradeNo;

    //账户类型
    private String accountType;

    //备注信息
    private String notePub;

    //收入
    private BigDecimal income;

    //支出
    private BigDecimal outcome;

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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getOutcome() {
        return outcome;
    }

    public void setOutcome(BigDecimal outcome) {
        this.outcome = outcome;
    }
}
