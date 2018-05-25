package org.trc.form.warehouse.allocateOrder;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 * 调拨单商品
 */
@Setter
@Getter
public class ScmAllocateOrderItem {
	
    private String allocateOrderCode;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 商品名称
     */
    private String skuName;
    
    /**
     * 商品货号
     */
    private String itemNo;
    
    /**
     * 条形码
     */
    private String barCode;

    /**
     * 商品规格描述
     */
    private String specNatureInfo;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;
    
    /**
     * 调拨库存类型:0-正品,1-残次品
     */
    private String inventoryType;
    
    /**
     * 计划调拨数量
     */
    private Long planAllocateNum;

}
