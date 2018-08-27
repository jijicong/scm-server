package org.trc.biz.afterSale;

import java.util.List;

import org.trc.form.afterSale.AfterSaleOrderItemVO;

public interface IAfterSaleOrderBiz {

	List<AfterSaleOrderItemVO> selectAfterSaleInfo(String shopOrderCode) throws Exception;

	

}
