package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.MoneySerializer;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by hzwdx on 2017/6/26.
 */
public class OrderExt extends OrderBase{

    // 实付金额,订单最终总额,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    @Transient
    private Long payment;

    // 邮费分摊,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    @Transient
    private Long postageFee;

    // 订单总税费,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    @Transient
    private Long totalTax;

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

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public Long getPostageFee() {
        return postageFee;
    }

    public void setPostageFee(Long postageFee) {
        this.postageFee = postageFee;
    }

    public Long getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Long totalTax) {
        this.totalTax = totalTax;
    }
}
