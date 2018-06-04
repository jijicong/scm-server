package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScmEntryOrderItem {
    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 仓储系统商品编码(仓库商品ID，
     首次发送时不用填写，后续发送必须填写)
     */
    private String itemId;

    /**
     * 商品数量
     * @return
     */
    private Long planQty;

    /**
     * 库存类型
     * @return
     */
    private String inventoryType;

    /**
     * 商品状态
     * @return
     */
    private String goodsStatus;
    
    /**********************************
     **********************************
      **********************************/
    
    
    /**
     * 采购单编号
     * @return
     */
    private String purchaseOrderCode;

    /**
     * sku名称
     * @return
     */
    private String skuName;

    /**
     * 商品sku编码
     * @return
     */
    private String skuCode;

    /**
     * 规格描述
     * @return
     */
    private String specInfo;

    /**
     * 条形码
     * @return
     */
    private String barCode;

    /**
     * 采购数量
     * @return
     */
//    private Long purchasingQuantity;

    /**
     * 批次号
     * @return
     */
    private String batchNo;

    /**
     * 生产编码
     * @return
     */
    private String productionCode;

    /**
     * 生产日期
     * @return
     */
    private String productionDate;

    /**
     * 理论保质期
     * @return
     */
    private Long expireDay;

    /**
     * 商品货号
     * @return
     */
    private String skuNo;
}
