package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.util.ResultUtil;

@Component
@Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER)
public class AfterSaleOrderResource {

	@Resource
	IAfterSaleOrderBiz iAfterSaleOrderBiz;
	
	@GetMapping(SupplyConstants.AfterSaleOrder.SELECT_ORDER_ITEM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response selectAfterSaleInfo(@BeanParam String shopOrderCode) throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectAfterSaleInfo(shopOrderCode));
	}
	
	
//	@GetMapping(SupplyConstants.AfterSaleOrder.ADD)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response add() {
//		return ResultUtil.createSuccessPageResult();
//	}
	
}
