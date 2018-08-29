package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 18:05
 * @Description:
 */
@Setter
@Getter
public class ExceptorAfterSaleOrder {
    /**
     * 创建时间
     */
    private String createTime;

    /**
     *售后单状态
     */
    private Integer status;
    /**
     *系统订单号
     */
    private String scmShopOrderCode;
    /**
     * 售后单编号
     */
    private String afterSaleCode;
    /**
     * 销售渠道
     */
    private String sellCodeName;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * SKU名称
     */
    private String skuName;
    /**
     * SKU编号
     */
    private String skuCode;
    /**
     * 规格
     */
    private String specNatureInfo;
    /**
     * 拟退货数量
     */
    private Integer returnNum;
    /**
     * 退款金额
     */
    private BigDecimal refundAmont;
    /**
     * 物流公司
     */
    private String logisticsCorporation;
    /**
     * 物流单号
     */
    private String waybillNumber;
    /**
     * 退货仓/店
     */
    private String returnWarehouseName;
    /**
     * 发货仓/店
     */
    private String deliverWarehouseName;


}
