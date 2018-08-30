package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfterSaleDetailTabVO {


    /**
     * 售后单编号
     */
    @ApiModelProperty("售后单编号")
    private String afterSaleCode;

    /**
     * sku名称
     */
    @ApiModelProperty("sku名称")
    private String skuName;

    /**
     * skuCode
     */
    @ApiModelProperty("skuCode")
    private String skuCode;

    /**
     * 发起类型
     */
    @ApiModelProperty("发起类型(0系统发起,1手动新建)")
    private Integer launchType;

    /**
     * 售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）
     */
    @ApiModelProperty("售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）")
    private Integer status;

    /**
     * 售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）
     */
    @ApiModelProperty("售后类型(0取消发货,1退货")
    private Integer afterSaleType;


    /**
     * 正品入库数量
     */
    @ApiModelProperty(value="正品入库数量")
    private Integer inNum;
    /**
     * 残品入库数量
     */
    @ApiModelProperty(value="残品入库数量")
    private Integer defectiveInNum;

    /**
     * 退货收货仓库编码
     */
    @ApiModelProperty(value="退货收货仓库编码")
    private String returnWarehouseCode;

    /**
     * 退货收货仓库名称
     */
    @ApiModelProperty(value="退货收货仓库名称")
    private String returnWarehouseName;

    /**
     * 快递公司编码
     */
    @ApiModelProperty(value="快递公司编码")
    private String logisticsCorporationCode;

    /**
     * 快递公司名称
     */
    @ApiModelProperty(value="快递公司名称")
    private String logisticsCorporation;
    /**
     * 运单号
     */
    @ApiModelProperty("运单号")
    private String waybillNumber;


}
