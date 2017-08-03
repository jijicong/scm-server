package org.trc.form.JDModel;

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
    private Double amount;

    //业务类型名
    private String tradeTypeName;

    //业务类型
    private String tradeType;

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

    private Integer state;

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
