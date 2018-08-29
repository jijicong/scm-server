package org.trc.domain.afterSale;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 售后单明细表
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Table(name="after_sale_order_detail")
@Setter
@Getter
public class AfterSaleOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
	private String id;
    /**
     * 售后单code(对应售后主表)
     */
    @Column(name="after_sale_code")
	private String afterSaleCode;
    /**
     * 店铺订单编号
     */
    @Column(name="shop_order_code")
	private String shopOrderCode;
    /**
     * 渠道商品订单号
     */
    @Column(name="order_item_code")
	private String orderItemCode;
    /**
     * skucode
     */
	@Column(name="sku_code")
	private String skuCode;
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
     * 商品数量
     */
	private int num;
    /**
     * 最大可退数量
     */
	@Column(name="max_return_num")
	private int maxReturnNum;
	/**
     * 拟退货数量
     */
	@Column(name="return_num")
	private int returnNum;
	/**
     * 正品入库数量
     */
	@Column(name="in_num")
	private int inNum;
	/**
     * 残品入库数量
     */
	@Column(name="defective_in_num")
	private int defectiveInNum;
	/**
     * 退款金额
     */
	@Column(name="refund_amont")
	private int refundAmont;
    
    /**
     * 商品图片,多张图片逗号分隔
     */
	@Column(name="picture")
	private String picture;
    /**
     * 创建时间
     */
	@Column(name="create_time")
	private Date createTime;
    /**
     * 修改时间
     */
	@Column(name="update_time")
	private Date updateTime;


	
}
