package org.trc.form.warehouse.entryReturnOrder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmEntryReturnDetailItem implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1191164771460485801L;

	/**
     * 仓储系统商品编码
     */
    private String itemId;

    /**
     * 应收商品数量
     * @return
     */
    private Long planQty;

    /**
     * 实收数量
     * @return
     */
    private Long actualQty;

    /**
     * 商品状态
     */
    private String goodsStatus;

}
