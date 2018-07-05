package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScmOrderCancelRequest extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1967200040087867279L;

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
     *调拨入库单号
     */
    private String allocateInOrderCode;
    
    /**
     *调拨出库单号
     */
    private String allocateOutOrderCode;
    
    /**
     *取消的单据类型
     * 0：发货单 
     * 1：采购单 
     * 2：调拨出库单 
     * 3：调拨入库单
     */
    private String orderType;
    

}
