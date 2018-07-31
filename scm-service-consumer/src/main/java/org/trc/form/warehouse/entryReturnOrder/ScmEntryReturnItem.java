package org.trc.form.warehouse.entryReturnOrder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 * 退货出库单商品详情
 */
@Setter
@Getter
public class ScmEntryReturnItem implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4760236109724876235L;

	/**
     * 退货仓商品编号
     */
    private String itemId;
    
    /**
     * 退货数量
     */
    private Long returnQuantity;
}
