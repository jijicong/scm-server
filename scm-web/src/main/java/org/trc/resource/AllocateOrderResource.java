package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.DistributeLockEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.AllocateOrderException;
import org.trc.form.AllocateOrder.AllocateItemForm;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;

@Component
@Path("allocateOrder")
public class AllocateOrderResource {
	
	private Logger logger = LoggerFactory.getLogger(AllocateOrderResource.class);
	
    @Resource
    private IAllocateOrderBiz allocateOrderBiz;

    @Autowired
    private RedisLock redisLock;



    /**
     * 调拨单的分页查询
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path("page")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderPage(@BeanParam AllocateOrderForm form, 
    		@BeanParam Pagenation<AllocateOrder> page){
        return ResultUtil.createSuccessPageResult(allocateOrderBiz.allocateOrderPage(form, page));

    }
    
    @GET
    @Path("auditPage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderAuditPage(@BeanParam AllocateOrderForm form, @BeanParam Pagenation<AllocateOrder> page,
    		@Context ContainerRequestContext requestContext) {
    	// 不传状态默认为待审核
    	if (StringUtils.isBlank(form.getAuditStatus())) {
    		form.setAuditStatus(AllocateOrderEnum.AllocateOrderAuditStatusEnum.WAIT_AUDIT.getCode());
    	}
        return ResultUtil.createSuccessPageResult(allocateOrderBiz.allocateOrderPage(form, page));
    }
    
    @PUT
    @Path("audit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderAudit(@PathParam("id") String orderId, 
    		@FormParam("auditOpinion") String auditOpinion, 
    		@FormParam("auditResult") String auditResult, 
    		@Context ContainerRequestContext requestContext){

        String identifier = redisLock.Lock(DistributeLockEnum.ALLOCATE_ORDER_AUDIT.getCode() + orderId,0, 10000);
        if (StringUtils.isBlank(identifier)) {
            throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, "不允许操作!");
        }
        try {
            allocateOrderBiz.allocateOrderAudit(orderId, auditOpinion, auditResult,
                    (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.ALLOCATE_ORDER_AUDIT.getCode()
                        + orderId, identifier)) {
                    logger.info("allocateCode:{} 调拨审核，解锁成功，identifier:{}", orderId, identifier);
                } else {
                    logger.error("allocateCode:{} 调拨审核，解锁失败，identifier:{}", orderId, identifier);
                }

            } catch (Exception e) {
                logger.error("allocateCode:{} 调拨审核，解锁失败，identifier:{}, err:{}",
                        orderId, identifier, e.getMessage());
                e.printStackTrace();
            }
        }
        return ResultUtil.createSuccessResult("调拨单审核操作成功","");

    }

    //调拨单管理 查询/编辑    编辑时需校验仓库是否被停用，查询时不用校正
    //调拨单审核 查询
    @GET
    @Path("editGet/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderEditGet(@PathParam("id") String orderId,@QueryParam("flag") String flag){
    	return ResultUtil.createSuccessResult("根据id查询调拨单成功", allocateOrderBiz.allocateOrderEditGet(orderId,flag));
    	
    }
    
    /**
     * 新增编辑保存
     * @param allocateOrder
     * @param delIds
     * @param isReview
     * @param skuDetail
     * @param requestContext
     * @return
     */
    @POST
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAllocateOrder(@BeanParam AllocateOrder allocateOrder, 
    		@FormParam("delsIds") String delIds, 
    		@FormParam("isReview") String isReview,
    		@FormParam("skuDetailList") String skuDetail, 
    		@Context ContainerRequestContext requestContext) {
    	allocateOrderBiz.saveAllocateOrder(allocateOrder, delIds, isReview, skuDetail, 
    			(AclUserAccreditInfo) requestContext.
    				getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("操作成功","");

    }
    
    /**
     * 删除
     * @param orderId
     * @param requestContext
     * @return
     */
    @PUT
    @Path("delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllocateOrder(@PathParam("id") String orderId,
    		@Context ContainerRequestContext requestContext) {
    	allocateOrderBiz.deleteAllocateOrder(orderId);
    	return ResultUtil.createSuccessResult("删除调拨单成功","");
    	
    }
    
    /**
     * 作废
     * @param orderId
     * @param requestContext
     * @return
     */
    @PUT
    @Path("drop/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dropAllocateOrder(@PathParam("id") String orderId,
    		@Context ContainerRequestContext requestContext) {
    	allocateOrderBiz.dropAllocateOrder(orderId, (AclUserAccreditInfo) requestContext.
				getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	return ResultUtil.createSuccessResult("作废调拨单成功","");
    	
    }

    @PUT
    @Path("setDrop/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDropAllocateOrder(@PathParam("id") String orderId,
                                      @Context ContainerRequestContext requestContext) {
        allocateOrderBiz.setDropAllocateOrder(orderId, (AclUserAccreditInfo) requestContext.
                getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("作废调拨单成功","");

    }
    
    /**
     * 通知仓库
     * @param orderId
     * @param requestContext
     * @return
     */
    @PUT
    @Path("noticeWarehouse/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response noticeWarehouse(@PathParam("id") String orderId,
    		@Context ContainerRequestContext requestContext) {
    	allocateOrderBiz.noticeWarehouse(orderId, (AclUserAccreditInfo) requestContext.
				getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	return ResultUtil.createSuccessResult("调拨单通知仓库成功","");
    	
    }
    
    @GET
    @Path("skuPage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response skuPage(@BeanParam AllocateItemForm form, @BeanParam Pagenation<AllocateSkuDetail> page,@QueryParam("skus") String skus) {
        return ResultUtil.createSuccessPageResult(allocateOrderBiz.querySkuList(form,page,skus));
    }
    
    @GET
    @Path("warehouseList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouse() {
        return allocateOrderBiz.queryWarehouse();
    }
    
    @POST
    @Path("inventoryQuery/{warehouseCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inventoryQuery(@PathParam("warehouseCode") String warehouseCode, @FormParam("queryStr") String queryStr) {
    	 return ResultUtil.createSuccessResult("", allocateOrderBiz.inventoryQuery(warehouseCode, queryStr));
    }
    
    
    


}
