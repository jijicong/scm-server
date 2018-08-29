package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 16:25
 * @Description: 售后商品子表展示类
 */
@Setter
@Getter
public class AfterSaleOrderDetailVO {

    /**
     * 售后单code
     */
    @ApiModelProperty(" 售后单code")
    private String afterSaleCode;

    @ApiModelProperty("sku名称")
    private String skuName;

    @ApiModelProperty("skuCode")
    private String skuCode;

    @ApiModelProperty("spuCode")
    private String spuCode;

    /**
     * 商品规格描述
     */
    @ApiModelProperty("商品规格描述")
    private String specNatureInfo;

    /**
     * 拟退货数量
     */
    @ApiModelProperty("拟退货数量")
    private Integer returnNum;

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    private Integer refundAmont;

    /**
     * 发货仓库编码
     */
    @ApiModelProperty("发货仓库编码")
    private String deliverWarehouseCode;

    /**
     * 发货仓库名称
     */
    @ApiModelProperty("发货仓库名称")
    private String deliverWarehouseName;



}
