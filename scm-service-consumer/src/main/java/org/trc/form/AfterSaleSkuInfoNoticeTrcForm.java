package org.trc.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AfterSaleSkuInfoNoticeTrcForm extends TrcParam {

    /**
     * sku编码
     */
    private String skuCode;
    
    /**
     * 商品订单号
     */
    private String orderItemCode;

    /**
     * 退款金额
     */
    private BigDecimal refundAmont;

    /**
     * 退货数量
     */
    private Integer returnNum;


}
