package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;

/**
 * 组装数据,用于请求发货通知单接口获取当前售后单是否已经完成了取消操作.
 */
@Setter
@Getter
public class AfterSaleNoticeWmsFrom {

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
}
