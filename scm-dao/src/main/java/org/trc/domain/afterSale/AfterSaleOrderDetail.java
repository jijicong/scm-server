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
     * 拟退货数量
     */
	@Column(name="sku_num")
	private Integer skuNum;
    /**
     * 拟退款金额
     */
	@Column(name="sku_money")
	private BigDecimal skuMoney;
    /**
     * 商品类型（0表示自采商品，1表示代发商品）
     */
	@Column(name="sku_type")
	private Integer skuType;
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
