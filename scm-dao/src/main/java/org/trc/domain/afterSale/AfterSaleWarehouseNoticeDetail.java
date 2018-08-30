package org.trc.domain.afterSale;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;


import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 退货入库单子表
 * </p>
 *
 * @author wangjie
 * @since 2018-08-29
 */
@Table(name="after_sale_warehouse_notice_detail")
@Setter
@Getter
public class AfterSaleWarehouseNoticeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	private String id;
    /**
     * 订单编号
     */
	@Column(name="shop_order_code")
	private String shopOrderCode;
    /**
     * 入库单编号
     */
	@Column(name="warehouse_notice_code")
	private String warehouseNoticeCode;
	/**
     * 渠道商品订单号
     */
	@Column(name="order_item_code")
	private String orderItemCode;
    /**
     * skuCode
     */
	@Column(name="sku_code")
	private String skuCode;
	/**
     * 商品名称
     */
	@Column(name="sku_name")
	private String skuName;
	/**
     * 条形码
     */
	@Column(name="bar_code")
	private String barCode;
	/**
     * 商品规格描述
     */
	@Column(name="spec_nature_info")
	private String specNatureInfo;
    /**
     * 拟退货数量
     */
	@Column(name="return_num")
	private Integer returnNum;
	/**
     * 总入库数量
     */
	@Column(name="total_in_num")
	private Integer totalInNum;
    /**
     * 正品入库数量
     */
	@Column(name="in_num")
	private Integer inNum;
    /**
     * 残品入库数量
     */
	@Column(name="defective_in_num")
	private Integer defectiveInNum;
	/**
     * 商品图片,多张图片逗号分隔
     */
	@Column(name="picture")
	private String picture;
    /**
     * 修改时间
     */
	@Column(name="update_time")
	private Date updateTime;
    /**
     * 添加时间
     */
	@Column(name="create_time")
	private Date createTime;


	
}
