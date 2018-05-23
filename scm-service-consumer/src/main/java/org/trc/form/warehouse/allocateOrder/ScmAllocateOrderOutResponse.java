package org.trc.form.warehouse.allocateOrder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScmAllocateOrderOutResponse {
	
    /**
     * 仓库调拨出库单号，三方仓才有
     */
    private String wmsAllocateOrderOutCode;
    
}
