package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeDO;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeVO;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 退货入库单
 * @author hzwjie
 *
 */
@Api(value = "退货入库单")
@Component
@Path("afterSaleWarehouseNotice")
public class AfterSaleWarehouseNoticeResource {
	
	@Autowired
	private IAfterSaleWarehouseNoticeBiz iAfterSaleWarehouseNoticeBiz;

	/**
	 * 退货入库单列表
	 */
	@GET
	@Path("warehouseNoticeList")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "退货入库单列表",response = AfterSaleWarehouseNotice.class)
	public Response warehouseNoticeList(@BeanParam AfterSaleWarehouseNoticeDO afterSaleWarehouseNoticeDO,@BeanParam Pagenation<AfterSaleWarehouseNotice> page,@Context ContainerRequestContext requestContext) throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleWarehouseNoticeBiz.warehouseNoticeList(afterSaleWarehouseNoticeDO,page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
	}
	
	/**
	 * 查询入库单信息
	 */
	@GET
	@Path("warehouseNoticeInfo")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查询入库单信息", response = AfterSaleWarehouseNoticeVO.class)
	public Response warehouseNoticeInfo(@ApiParam(value = "入库单编号") @QueryParam("warehouseNoticeCode") String warehouseNoticeCode) throws Exception{
		return ResultUtil.createSuccessResult("查询成功",iAfterSaleWarehouseNoticeBiz.warehouseNoticeInfo(warehouseNoticeCode));
		
	}
}
