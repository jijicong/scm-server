package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.trc.util.QueryModel;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 13:34
 * @Description: 售后单（对接界面接受参数）
 */
@Getter
@Setter
public class AfterSaleOrderForm  extends QueryModel{

    /**
     * 系统订单号
     */
    @ApiModelProperty("系统订单号")
    private String scmShopOrderCode;

    /**
     * 店铺订单编号
     */
    @ApiModelProperty("店铺订单编号")
    private String shopOrderCode;

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
     * 退货收货仓库编码
     */
    @ApiModelProperty("退货收货仓库编码")
    private String returnWarehouseCode;

    /**
     * 运单号
     */
    @ApiModelProperty("运单号")
    private String waybillNumber;

    /**
     * 收货人姓名
     */
    @ApiModelProperty("收货人姓名")
    private String receiverName;

    /**
     * 会员名称
     */
    @ApiModelProperty("会员名称")
    private String userName;

    /**
     * 收货人电话号码
     */
    @ApiModelProperty("收货人电话号码")
    private String receiverPhone;

    /**
     * 售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）
     */
    @ApiModelProperty("售后单状态（0待客户发货，1客户已经发货，2是待分配仓库,3已经完成，4已经取消）")
    private Integer status;



















}
