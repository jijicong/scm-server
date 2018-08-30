package org.trc.form.afterSale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 13:34
 * @Description:
 */
@Getter
@Setter
@Api(value = "售后单展示对象")
public class AfterSaleOrderVO {

    /**
     * 创建时间（格式yyyy-mm-dd hh:mi:ss'）
     */
    @ApiModelProperty("创建时间（格式yyyy-mm-dd hh:mi:ss'）")
    private Date createTime;

    /**
     * 系统订单号
     */
    @ApiModelProperty("系统订单号")
    private String scmShopOrderCode;

    /**
     * 售后单编号
     */
    @ApiModelProperty("售后单编号")
    private String afterSaleCode;

    /**
     * 销售渠道编码
     */
    @ApiModelProperty("销售渠道编码")
    private String sellCode;

    /**
     * 销售渠道名称
     */
    @ApiModelProperty("销售渠道名称")
    private String sellCodeName;

    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    private String shopName;

    /**
     * 快递公司名称
     */
    @ApiModelProperty("快递公司名称")
    private String logisticsCorporation;

    /**
     * 运单号
     */
    @ApiModelProperty("运单号")
    private String waybillNumber;

    @ApiModelProperty("售后单状态")
    private Integer status;

    /**
     * 退货收货仓库编码
     */
    @ApiModelProperty("退货收货仓库编码")
    private String returnWarehouseCode;

    /**
     * 退货收货仓库名称
     */
    @ApiModelProperty("退货收货仓库名称")
    private String returnWarehouseName;





    /**
     * 售后单子表列表
     */
    @ApiModelProperty("售后单子表列表")
    private List<AfterSaleOrderDetailVO> afterSaleOrderDetailVOList;





}
