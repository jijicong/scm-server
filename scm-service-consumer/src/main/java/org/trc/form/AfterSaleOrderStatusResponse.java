package org.trc.form;

import lombok.Getter;
import lombok.Setter;

/**
 * 售后单状态返回
 */
@Getter
@Setter
public class AfterSaleOrderStatusResponse {

    /**
     * 售后单号
     */
    private String afterSaleCode;

    /**
     * 售后单状态: 0-待客户发货,1-客户已经发货,2-已经完成,3-已经取消,4-取消中,5-申请失败
     */
    private Integer status;

}