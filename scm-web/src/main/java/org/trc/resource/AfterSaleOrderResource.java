package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.form.afterSale.AfterSaleOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

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
	@GET
	@Path(SupplyConstants.AfterSaleOrder.SELECT_ORDER_ITEM)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "根据订单号 查询售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopOrderCode", value = "订单号", paramType = "path", dataType = "String", required = true),
    })
	public Response selectAfterSaleInfo(@QueryParam("shopOrderCode") String shopOrderCode) throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectAfterSaleInfo(shopOrderCode));
	}
	
	/**
	 * 增加售后单
	 * @return
	 */
	@GET
	@Path(SupplyConstants.AfterSaleOrder.ADD)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "增加售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopOrderCode", value = "订单号", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "picture", value = "商铺图片路径（多个图片用逗号分隔开）", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "memo", value = "备注", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "returnWarehouseCode", value = "退货收货仓库编码", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "returnAddress", value = "退货详细地址", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "logisticsCorporationCode", value = "快递公司编码", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "logisticsCorporation", value = "快递公司名称", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "waybillNumber", value = "运单号", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "afterSaleOrderDetailList", value = "售后单详情 ", paramType = "path", dataType = "List", required = true)
    })
	public Response add(AfterSaleOrderAddDO afterSaleOrderAddDO,@Context ContainerRequestContext requestContext) throws Exception{
		iAfterSaleOrderBiz.addAfterSaleOrder(afterSaleOrderAddDO,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
		return ResultUtil.createSuccessPageResult("操作成功");
	}
	
	/**
	 * 查询快递公司列表
	 */
	@GET
	@Path("selectLogisticsCompany")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查询快递公司列表")
	public Response selectLogisticsCompany() throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectLogisticsCompany());
	}
	
	/**
	 * 查询入库仓库
	 */
	@GET
	@Path("selectWarehouse")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查询入库仓库")
	public Response selectWarehouse() throws Exception{
		return ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.selectWarehouse());
	}

	/**
	 * @Description: 售后单分页查询
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
	@GET
	@Path("/queryAfterSaleOrderPage")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "售后单分页查询", response = AfterSaleOrder.class)
	public Response queryAfterSaleOrderPage(@BeanParam AfterSaleOrderForm form, @BeanParam Pagenation<AfterSaleOrder> page, @Context ContainerRequestContext requestContext){
		return  ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.afterSaleOrderPage(form , page));
	}


	/**
	 * @Description: 售后单导出
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
	@GET
	@Path("/exportAfterSaleOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "售后单导出", response = AfterSaleOrder.class)
	public Response exportAfterSaleOrderVO(@BeanParam AfterSaleOrderForm form, @BeanParam Pagenation<AfterSaleOrder> page, @Context ContainerRequestContext requestContext) throws Exception{
		return  ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.exportAfterSaleOrderVO(form , page));
	}



}
