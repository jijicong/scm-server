package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ScmDeliveryOrderItem {

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
     * 实际成交价,实付总金额
     * @return
     */
    private BigDecimal actualPrice;

    /**
     * sku名称
     * @return
     */
    private String skuName;

    /**
     * sku规格信息
     * @return
     */
    private String specInfo;
    /**
     * 条形码
     * @return
     */
    private String barCode;
}
