package org.trc.dbUnit.AfterSale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.trc.biz.trc.ITrcBiz;
import org.trc.form.afterSale.TaiRanAfterSaleOrderDetail;
import org.trc.form.afterSale.TairanAfterSaleOrderDO;
import org.trc.service.BaseTest;


public class TaiRanAfterSale extends BaseTest{

	@Resource
	private ITrcBiz trcBiz;
	@Test
	public void testInsert() throws Exception {
		TairanAfterSaleOrderDO afterSaleOrder=new TairanAfterSaleOrderDO();
	    afterSaleOrder.setRequestNo(new Date().getTime()+"");
	    afterSaleOrder.setShopOrderCode("7774561469");
	    afterSaleOrder.setReturnScene(1);
	    afterSaleOrder.setAfterSaleType(1);
	    afterSaleOrder.setReturnWarehouseCode("CK00273");
	    
	    List<TaiRanAfterSaleOrderDetail> list=new ArrayList<>();
	    TaiRanAfterSaleOrderDetail detail=new TaiRanAfterSaleOrderDetail();
	    detail.setSkuCode("SP0201808070000833");
	    detail.setRefundAmont(new BigDecimal(1));
	    list.add(detail);
	    
	    afterSaleOrder.setAfterSaleOrderDetailList(list);

	    trcBiz.afterSaleCreate(afterSaleOrder);
	}
}
