package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.form.afterSale.AfterSaleOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

@Api(value = "售后单管理")
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
		return ResultUtil.createSuccessResult("查询成功",iAfterSaleOrderBiz.selectAfterSaleInfo(shopOrderCode));
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
		return ResultUtil.createSuccessResult("操作成功","");
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
	@ApiOperation(value = "售后单分页查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "startDate", value = "创建时间（开始）", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "endDate", value = "创建时间（结束)", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "scmShopOrderCode", value = "系统订单号", paramType = "query", dataType = "query", required = false),
			@ApiImplicitParam(name = "afterSaleCode", value = "售后单编号", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "returnWarehouseCode", value = "退货收货仓库编码", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "waybillNumber", value = "运单号", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "receiverName", value = "收货人姓名", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "receiverPhone", value = "收货人电话号码", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "skuName", value = "sku名称 ", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "skuCode", value = "skuCode ", paramType = "query", dataType = "String", required = false),
			@ApiImplicitParam(name = "pageNo", value = "页码 ", paramType = "query", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "pageSize", value = "每页记录条数 ", paramType = "query", dataType = "Integer", required = true),
			@ApiImplicitParam(name = "start", value = "开始记录行数", paramType = "query", dataType = "Integer", required = true)
	})
	public Response queryAfterSaleOrderPage(@BeanParam AfterSaleOrderForm form, @BeanParam Pagenation<AfterSaleOrder> page, @Context ContainerRequestContext requestContext){
		return  ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.afterSaleOrderPage(form , page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
	}


	/**
	 * @Description: 售后单导出
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
	@GET
	@Path("/exportAfterSaleOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "售后单导出")
	public Response exportAfterSaleOrderVO(@BeanParam AfterSaleOrderForm form, @BeanParam Pagenation<AfterSaleOrder> page, @Context ContainerRequestContext requestContext) throws Exception{
		return  ResultUtil.createSuccessPageResult(iAfterSaleOrderBiz.exportAfterSaleOrderVO(form , page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
	}

    /**
     * @Description: 检查店铺订单是否可以常见售后单
     * @Author: hzluoxingcheng
     * @Date: 2018/8/30
     */ 
	@GET
	@Path("checkOrder/{shopOrderCode}")
	@ApiOperation(value = "检查订单是否可以创建售后单")
	@Produces(MediaType.APPLICATION_JSON)
	public Response orderDetail(@ApiParam(value = "店铺订单编号") @PathParam("shopOrderCode") String shopOrderCode){
		return ResultUtil.createSuccessResult("售后单详情查询成功", iAfterSaleOrderBiz.checkOrder(shopOrderCode));
	}












}
