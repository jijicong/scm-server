package org.trc.form.warehouse.allocateOrder;

import java.util.List;

import org.trc.form.warehouse.ScmWarehouseRequestBase;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmJosAllocateOrderRequest extends ScmWarehouseRequestBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3209349394050183106L;

	/**
     * 事业部编号 
     */
    private String deptNo;

    /**
     * 仓间调拨单号 
     */
    private String allocateOrderCode;

    /**
     * 调入仓库编码
     */
    private String inWarehouseCode;

    /**
     * 调出仓库编码
     */
    private String outWarehouseCode;

    /**
     * 商品列表
     */
    List<ScmAllocateOrderItem> allocateOrderItemList;
}
