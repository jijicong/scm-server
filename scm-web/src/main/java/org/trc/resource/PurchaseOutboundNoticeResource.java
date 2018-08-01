package org.trc.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
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
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.enums.DistributeLockEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "退货出库通知单管理")
@Component
@Path("purchaseOutboundNotice")
public class PurchaseOutboundNoticeResource {

    @Autowired
    private IPurchaseOutboundNoticeBiz noticeBiz;
    
    @Autowired
    private RedisLock redisLock;
    
    private static Logger logger = LoggerFactory.getLogger(PurchaseOutboundNoticeResource.class);

    /**
     * 查询退货出库通知单列表
     */
    @GET
    @Path("pageList")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "查询退货出库通知单列表", response = PurchaseOutboundNotice.class)
    public Response pageList(@BeanParam PurchaseOutboundNoticeForm form, @BeanParam Pagenation<PurchaseOutboundNotice> page, 
    		@Context ContainerRequestContext requestContext) {
        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj, "查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) obj;
        String channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        return ResultUtil.createSuccessPageResult(noticeBiz.getPageList(form, page, channelCode));
    }
    
    /**
     * 查询详情
     * @param id
     * @return
     */
    @GET
    @Path("detail/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据主键id查询退货出库通知单详情", response = PurchaseOutboundNotice.class)
    public Response detail(@ApiParam(value = "退货出库通知单主键") @PathParam("id") Long id) {
    	return ResultUtil.createSuccessResult("根据主键id查询退货出库通知单详情成功", noticeBiz.getDetail(id));
    	
    }
    
    /**
     * 通知出库-重新出库
     * @return
     */
    @PUT
    @Path("noticeOut/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "通知出库")
    public Response noticeOut(@ApiParam(value = "退货出库通知单号") @PathParam("code") String code, 
    		@Context ContainerRequestContext requestContext) {
        String identifier = redisLock.Lock(DistributeLockEnum.PURCHASE_OUTBOUND_NOTICE.getCode() + code, 0, 10000);
    	if (StringUtils.isBlank(identifier)) {
    		throw new RuntimeException("请不要重复操作!");
    	}
    	try {
    		noticeBiz.noticeOut(code, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	} finally {
    		try {
    			if (redisLock.releaseLock(DistributeLockEnum.PURCHASE_OUTBOUND_NOTICE.getCode() 
    					+ code, identifier)) {
    				logger.info("退货出库通知单号:{} 通知出库，解锁成功，identifier:{}", code, identifier);
    			} else {
    				logger.error("退货出库通知单号:{} 通知出库，解锁失败，identifier:{}", code, identifier);
    			}
    			
    		} catch (Exception e) {
    			logger.error("退货出库通知单号:{} 通知出库，解锁失败，identifier:{}, err:", 
    					code, identifier, e);
    			e.printStackTrace();
    		}
    	}
        return ResultUtil.createSuccessResult("操作成功","");

    }
    
    /**
     * 取消出库
     * @return
     */
    @PUT
    @Path("cancel/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "取消出库")
    public Response cancel(@ApiParam(value = "退货出库通知单号") @PathParam("code") String code, 
    		@Context ContainerRequestContext requestContext) {
        String identifier = redisLock.Lock(DistributeLockEnum.PURCHASE_OUTBOUND_NOTICE.getCode() + code, 0, 10000);
    	if (StringUtils.isBlank(identifier)) {
    		throw new RuntimeException("请不要重复操作!");
    	}
    	try {
    		noticeBiz.cancel(code, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	} finally {
    		try {
    			if (redisLock.releaseLock(DistributeLockEnum.PURCHASE_OUTBOUND_NOTICE.getCode() 
    					+ code, identifier)) {
    				logger.info("退货出库通知单号:{} 取消出库，解锁成功，identifier:{}", code, identifier);
    			} else {
    				logger.error("退货出库通知单号:{} 取消出库，解锁失败，identifier:{}", code, identifier);
    			}
    			
    		} catch (Exception e) {
    			logger.error("退货出库通知单号:{} 取消出库，解锁失败，identifier:{}, err:", 
    					code, identifier, e);
    			e.printStackTrace();
    		}
    	}
        return ResultUtil.createSuccessResult("操作成功","");

    }
    

}
