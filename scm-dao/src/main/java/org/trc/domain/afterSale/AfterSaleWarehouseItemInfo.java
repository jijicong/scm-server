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
@Table(name="after_sale_warehouse_item_info")
@Setter
@Getter
public class AfterSaleWarehouseItemInfo implements Serializable {

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
	@Column(name="warehouse_info_code")
	private String warehouseInfoCode;
    /**
     * skuCode
     */
	@Column(name="sku_code")
	private String skuCode;
    /**
     * 拟退货数量
     */
	@Column(name="plan_num")
	private Integer planNum;
    /**
     * 正品数量
     */
	@Column(name="good_num")
	private Integer goodNum;
    /**
     * 残品数量
     */
	@Column(name="bad_num")
	private Integer badNum;
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
