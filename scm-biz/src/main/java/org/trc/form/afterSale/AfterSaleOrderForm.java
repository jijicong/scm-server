package org.trc.form.afterSale;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

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
    @ApiParam("系统订单号")
    @QueryParam("scmShopOrderCode")
    private String scmShopOrderCode;

    /**
     * 店铺订单编号
     */
    @ApiParam("店铺订单编号")
    @QueryParam("shopOrderCode")
    private String shopOrderCode;

    /**
     * 售后单编号
     */
    @ApiParam("售后单编号")
    @QueryParam("afterSaleCode")
    private String afterSaleCode;

    /**
     * sku名称
     */
    @ApiParam("sku名称")
    @QueryParam("skuName")
    private String skuName;

    /**
     * skuCode
     */
    @ApiParam("skuCode")
    @QueryParam("skuCode")
    private String skuCode;

    /**
     * 退货收货仓库编码
     */
    @ApiParam("退货收货仓库编码")
    @QueryParam("returnWarehouseCode")
    private String returnWarehouseCode;

    /**
     * 运单号
     */
    @ApiParam("运单号")
    @QueryParam("waybillNumber")
    private String waybillNumber;

    /**
     * 收货人姓名
     */
    @ApiParam("收货人姓名")
    @QueryParam("receiverName")
    private String receiverName;

    /**
     * 会员名称
     */
    @ApiParam("会员名称")
    @QueryParam("userName")
    private String userName;

    /**
     * 收货人电话号码
     */
    @ApiParam("收货人电话号码")
    @QueryParam("receiverPhone")
    private String receiverPhone;


    @ApiParam("售后单状态（（0待客户发货，1客户已经发货，2已经完成，3已经取消")
    @QueryParam("status")
    private Integer status;



















}
