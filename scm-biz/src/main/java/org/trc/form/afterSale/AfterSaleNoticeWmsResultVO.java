package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfterSaleNoticeWmsResultVO {

    /**
     * 售后单号
     */
    private String afterSaleCode;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 主系统订单号
     */
    private String scmShopOrderCode;
    
    /**
     * 返回结果1:成功 2：取消中 3：失败
     */
    private String flg;
    /**
     * 失败原因
     */
    private String msg;
}
