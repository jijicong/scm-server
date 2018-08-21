package org.trc.form.JDModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockNewResultVO {

    /**
     * 配送地址ID
     */
    private String areaId;

    /**
     * 商品编号
     */
    private String skuCode;

    /**
     * 库存状态编号 33,39,40,36,34
     */
    private String stockStateId;

    /**
     * 库存状态描述
     */
    private String stockStateDesc;

    /**
     * 剩余数量
     */
    private String remainNum;
}
