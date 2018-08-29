package org.trc.domain.afterSale;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;


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
@Table(name="after_sale_warehouse_info")
@Setter
@Getter
public class AfterSaleWarehouseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	private String id;
    /**
     * 入库单编号
     */
	@Column(name="warehouse_info_code")
	private String warehouseInfoCode;
    /**
     * 订单编号
     */
	@Column(name="shop_order_code")
	private String shopOrderCode;
    /**
     * 仓库编号
     */
	@Column(name="warehouse_code")
	private String warehouseCode;
    /**
     * 仓库名称
     */
	@Column(name="warehouse_name")
	private String warehouseName;
    /**
     * 联系人姓名
     */
	@Column(name="contacts_name")
	private String contactsName;
    /**
     * 状态 0未到货 1已到货待理货 2入库完成 3已取消
     */
	private Integer status;
    /**
     * 联系人电话
     */
	@Column(name="contacts_phone")
	private String contactsPhone;
    /**
     * 操作人
     */
	private String operator;
    /**
     * 确认到货备注
     */
	@Column(name="arrival_remark")
	private String arrivalRemark;
    /**
     * 入库备注
     */
	@Column(name="warehouse_remark")
	private String warehouseRemark;
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
