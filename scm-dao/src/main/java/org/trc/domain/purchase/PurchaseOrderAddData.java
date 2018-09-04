package org.trc.domain.purchase;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import javax.ws.rs.FormParam;

/**
 * Created by sone on 2017/6/20.
 */
@Setter
@Getter
public class PurchaseOrderAddData extends PurchaseOrder{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1550957835369944676L;
	@FormParam("gridValue")
    @Transient
    private String gridValue;

    @FormParam("qiNiuValue")
    @Transient
    private String qiNiuValue;
}
