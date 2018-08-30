package org.trc.domain.afterSale;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


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
	private String id;
    /**
     * 入库单编号
     */
	@Column(name="warehouse_notice_code")
	private String warehouseNoticeCode;
	/**
     * 售后单编号
     */
	@Column(name="after_sale_code")
	private String afterSaleCode;
    /**
     * 订单编号
     */
	@Column(name="shop_order_code")
	private String shopOrderCode;
	/**
	 * 业务线编码
	 */
	@Column(name="channel_code")
	private String channelCode;
	/**
	 * 销售渠道编码
	 */
	@Column(name="sell_code")
	private String sellCode;
	/**
     * 系统订单号
     */
	@Column(name="scm_shop_order_code")
	private String scmShopOrderCode;
    /**
     * 入库仓库编号
     */
	@Column(name="warehouse_code")
	private String warehouseCode;
    /**
     * 入库仓库名称
     */
	@Column(name="warehouse_name")
	private String warehouseName;
	
	/**
     * 发件人
     */
	@Column(name="sender")
	private String sender;
	/**
     * 收货人手机
     */
	@Column(name="receiver_number")
	private String receiverNumber;
	/**
     * 收货人
     */
	@Column(name="receiver")
	private String receiver;
	/**
     * 发件人所在省
     */
	@Column(name="sender_province")
	private String senderProvince;
	/**
     * 发件人所在城市
     */
	@Column(name="sender_city")
	private String senderCity;
	/**
     * 发件人手机
     */
	@Column(name="sender_number")
	private String senderNumber;
	/**
     * 发件方详细地址
     */
	@Column(name="sender_address")
	private String senderAddress;
	/**
     * 收件方省份
     */
	@Column(name="receiver_province")
	private String receiverProvince;
	/**
     * 收件方地址
     */
	@Column(name="receiver_address")
	private String receiverAddress;
	/**
     * 收件方城市
     */
	@Column(name="receiver_city")
	private String receiverCity;
	/**
     * 商品行数(sku数量)
     */
	@Column(name="sku_num")
	private Integer skuNum;
	
	/**
     * 总入库数量
     */
	@Column(name="total_in_num")
	private Integer totalInNum;
	
	/**
     * 正品入库总量
     */
	@Column(name="in_num")
	private Integer inNum;
	
	/**
     * 残品入库总量
     */
	@Column(name="defective_in_num")
	private Integer defectiveInNum;
	
	/**
     * 状态 0未到货 1已到货待理货 2入库完成 3已取消
     */
	@Column(name="status")
	private Integer status;
	
	/**
     * 仓库接收入库通知失败原因
     */
	@Column(name="failure_cause")
	private String failureCause;
	
    /**
     * 操作人
     */
	private String operator;
    
    /**
     * 备注
     */
	private String remark;
	
	/**
     * 创建人
     */
	@Column(name="create_operator")
	private String createOperator;
    /**
     * 添加时间
     */
	@Column(name="create_time")
	private Date createTime;
    /**
     * 入库时间
     */
	@Column(name="warehouse_time")
	private Date warehouseTime;


	
}
