package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.util.ResultUtil;

@Component
@Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER)
public class AfterSaleOrderResource {

	@Resource
	IAfterSaleOrderBiz iAfterSaleOrderBiz;
	
	/**
	 * 根据订单号 查询售后单信息
	 * @param shopOrderCode
	 * @return
	 * @throws Exception
	 */
	@GetMapping(SupplyConstants.AfterSaleOrder.SELECT_ORDER_ITEM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response selectAfterSaleInfo(@BeanParam String shopOrderCode) throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectAfterSaleInfo(shopOrderCode));
	}
	
	/**
	 * 增加售后单
	 * @return
	 */
	@GetMapping(SupplyConstants.AfterSaleOrder.ADD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@BeanParam AfterSaleOrderAddDO afterSaleOrderAddDO,@Context ContainerRequestContext requestContext) throws Exception{
		iAfterSaleOrderBiz.addAfterSaleOrder(afterSaleOrderAddDO,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
		return ResultUtil.createSuccessPageResult("操作成功");
	}
	
	/**
	 * 查询快递公司列表
	 */
	@GetMapping("/selectLogisticsCompany")
	@Produces(MediaType.APPLICATION_JSON)
	public Response selectLogisticsCompany() throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectLogisticsCompany());
	}
	
	/**
	 * 查询入库仓库
	 */
	@GetMapping("/selectWarehouse")
	@Produces(MediaType.APPLICATION_JSON)
	public Response selectWarehouse() throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectWarehouse());
	}
	
}
