package org.trc.domain.afterSale;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.ws.rs.FormParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 退货入库单
 * </p>
 *
 * @author wangjie
 * @since 2018-08-29
 */
@Table(name="after_sale_warehouse_notice")
@Setter
@Getter
public class AfterSaleWarehouseNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
	@FormParam("id")
	private String id;
    
    /**
     * 入库单编号
     */
    @ApiModelProperty(value="入库单编号")
	@FormParam("warehouseNoticeCode")
	@Column(name="warehouse_notice_code")
	private String warehouseNoticeCode;
	/**
     * 售后单编号
     */
    @ApiModelProperty(value="售后单编号")
	@FormParam("afterSaleCode")
	@Column(name="after_sale_code")
	private String afterSaleCode;
    /**
     * 订单编号
     */
    @ApiModelProperty(value="订单编号")
	@FormParam("shopOrderCode")
	@Column(name="shop_order_code")
	private String shopOrderCode;
	/**
     * 系统订单号
     */
    @ApiModelProperty(value="系统订单号")
	@FormParam("scmShopOrderCode")
	@Column(name="scm_shop_order_code")
	private String scmShopOrderCode;
    /**
     * 业务线编码
     */
    @ApiModelProperty(value="业务线编码")
	@FormParam("channelCode")
	@Column(name="channel_code")
	private String channelCode;
    /**
     * 销售渠道编码
     */
    @ApiModelProperty(value="销售渠道编码")
	@FormParam("sellCode")
	@Column(name="sell_code")
	private String sellCode;
    /**
     * 入库仓库编号
     */
    @ApiModelProperty(value="入库仓库编号")
	@FormParam("warehouseCode")
	@Column(name="warehouse_code")
	private String warehouseCode;
    /**
     * 入库仓库名称
     */
    @ApiModelProperty(value="入库仓库名称")
	@FormParam("warehouseName")
	@Column(name="warehouse_name")
	private String warehouseName;
	
	/**
     * 发件人
     */
    @ApiModelProperty(value="发件人")
	@FormParam("sender")
	@Column(name="sender")
	private String sender;
	/**
     * 收货人手机
     */
    @ApiModelProperty(value="收货人手机")
	@FormParam("receiverNumber")
	@Column(name="receiver_number")
	private String receiverNumber;
	/**
     * 收货人
     */
    @ApiModelProperty(value="收货人")
	@FormParam("receiver")
	@Column(name="receiver")
	private String receiver;
	/**
     * 发件人所在省
     */
    @ApiModelProperty(value="发件人所在省")
	@FormParam("senderProvince")
	@Column(name="sender_province")
	private String senderProvince;
	/**
     * 发件人所在城市
     */
    @ApiModelProperty(value="发件人所在城市")
	@FormParam("senderCity")
	@Column(name="sender_city")
	private String senderCity;
	/**
     * 发件人手机
     */
    @ApiModelProperty(value="发件人手机")
	@FormParam("senderNumber")
	@Column(name="sender_number")
	private String senderNumber;
	/**
     * 发件方详细地址
     */
    @ApiModelProperty(value="发件方详细地址")
	@FormParam("senderAddress")
	@Column(name="sender_address")
	private String senderAddress;
	/**
     * 收件方省份
     */
    @ApiModelProperty(value="收件方省份")
	@FormParam("receiverProvince")
	@Column(name="receiver_province")
	private String receiverProvince;
	/**
     * 收件方地址
     */
    @ApiModelProperty(value="收件方地址")
	@FormParam("receiverAddress")
	@Column(name="receiver_address")
	private String receiverAddress;
	/**
     * 收件方城市
     */
    @ApiModelProperty(value="收件方城市")
	@FormParam("receiverCity")
	@Column(name="receiver_city")
	private String receiverCity;
	/**
     * 商品行数(sku数量)
     */
    @ApiModelProperty(value="商品行数(sku数量)")
	@FormParam("skuNum")
	@Column(name="sku_num")
	private Integer skuNum;
	
	/**
     * 总入库数量
     */
    @ApiModelProperty(value="总入库数量")
	@FormParam("totalInNum")
	@Column(name="total_in_num")
	private Integer totalInNum;
	
	/**
     * 正品入库总量
     */
    @ApiModelProperty(value="正品入库总量")
	@FormParam("inNum")
	@Column(name="in_num")
	private Integer inNum;
	
	/**
     * 残品入库总量
     */
    @ApiModelProperty(value="残品入库总量")
	@FormParam("defectiveInNum")
	@Column(name="defective_in_num")
	private Integer defectiveInNum;
	
	/**
     * 状态 0未到货 1已到货待理货 2入库完成 3已取消
     */
    @ApiModelProperty(value="状态 0未到货 1已到货待理货 2入库完成 3已取消")
	@FormParam("status")
	@Column(name="status")
	private Integer status;
	
	/**
     * 仓库接收入库通知失败原因
     */
    @ApiModelProperty(value="仓库接收入库通知失败原因")
	@FormParam("failureCause")
	@Column(name="failure_cause")
	private String failureCause;
	
    /**
     * 操作人
     */
    @ApiModelProperty(value="操作人")
	@FormParam("operator")
	private String operator;
    
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
	@FormParam("remark")
	private String remark;
	
	/**
     * 创建人
     */
    @ApiModelProperty(value="创建人")
	@FormParam("createOperator")
	@Column(name="create_operator")
	private String createOperator;
    /**
     * 添加时间
     */
    @ApiModelProperty(value="添加时间")
	@FormParam("createTime")
	@Column(name="create_time")
	private Date createTime;
    /**
     * 入库时间
     */
    @ApiModelProperty(value="入库时间")
	@FormParam("warehouseTime")
	@Column(name="warehouse_time")
	private Date warehouseTime;


	
}
