package org.trc.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AfterSaleNoticeTrcForm extends TrcParam {

    /**
     * 售后单号
     */
    private String afterSaleCode;

    /**
     * 店铺订单号
     */
    private String shopOrderCode;

    /**
     * 备注
     */
    private String memo;

    /**
     * 物流公司编码
     */
    private String logisticsCorporationCode;

    /**
     * 物流公司名称
     */
    private String logisticsCorporation;

    /**
     * 物流单号
     */
    private String waybillNumber;

    /**
     * 退货仓库编码
     */
    private String warehouseCode;

    /**
     * 退货仓库名称
     */
    private String warehouseName;

    /**
     * 售后上传图片路径url,多个图片路径用逗号分隔
     */
    private String picture;

    /**
     * 售后sku信息
     */
    private List<AfterSaleSkuInfoNoticeTrcForm> skus;


}
