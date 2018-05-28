package org.trc.form.warehouse.allocateOrder;

import org.trc.form.warehouse.ScmWarehouseRequestBase;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmAllocateOrderInCancelRequest extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3338123399744967950L;

	/**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     *单据编码,发货通知单编号
     */
    private String orderCode;

    /**
     *取消原因
     */
    private String cancelReason;
    
    /**
     *调拨出库单号
     */
    private String allocateInOrderCode;
    
    
}
