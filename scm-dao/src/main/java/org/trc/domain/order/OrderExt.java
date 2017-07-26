package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.MoneySerializer;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hzwdx on 2017/6/26.
 */
public class OrderExt extends OrderBase{

    // 订单总金额(商品单价*数量),单位/分
    @Transient
    private BigDecimal totalFee;
    // 实付金额,订单最终总额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @Transient
    private BigDecimal payment;

    // 邮费分摊,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @Transient
    private BigDecimal postageFee;

    // 订单总税费,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @Transient
    private BigDecimal totalTax;

    /**
     * 订单商品明细列表
     */
    private List<OrderItem> orderItemList;

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getPostageFee() {
        return postageFee;
    }

    public void setPostageFee(BigDecimal postageFee) {
        this.postageFee = postageFee;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
