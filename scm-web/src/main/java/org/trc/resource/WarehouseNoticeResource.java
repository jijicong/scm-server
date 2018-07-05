package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.enums.DistributeLockEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sone on 2017/7/11.
 */
@Component
@Path(SupplyConstants.WarehouseNotice.ROOT)
public class WarehouseNoticeResource {
	
	private Logger logger = LoggerFactory.getLogger(WarehouseNoticeResource.class);
	
    @Resource
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @Autowired
    private RedisLock redisLock;


    /**
     * 入库通知的分页查询
     * @param form
     * @param page
     * @param requestContext
     * @return
     */
    @GET
    @Path(SupplyConstants.WarehouseNotice.WAREHOUSE_NOTICE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseNoticePage(@BeanParam WarehouseNoticeForm form, @BeanParam Pagenation<WarehouseNotice> page,@Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(warehouseNoticeBiz.warehouseNoticePage(form,page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));

    }

    /**
     * 入库单列表页面通知收货操作
     * @param warehouseNotice
     * @param requestContext
     * @return
     */
    @PUT
    @Path(SupplyConstants.WarehouseNotice.RECEIPT_ADVICE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiptAdvice(@BeanParam WarehouseNotice warehouseNotice, @Context ContainerRequestContext requestContext){
        String identifier = "";
    	identifier = redisLock.Lock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode() + 
    			warehouseNotice.getWarehouseNoticeCode(), 0, 10000);
    	if (StringUtils.isBlank(identifier)) {
    		throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, "请不要重复操作!");
    	}
    	try {
    		warehouseNoticeBiz.receiptAdvice(warehouseNotice,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	} finally {
        	String noticeCode = warehouseNotice.getWarehouseNoticeCode();
    		try {
    			if (redisLock.releaseLock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode() 
    					+ noticeCode, identifier)) {
    				logger.info("warehouseNoticeCode:{} 入库通知，解锁成功，identifier:{}", noticeCode, identifier);
    			} else {
    				logger.error("warehouseNoticeCode:{} 入库通知，解锁失败，identifier:{}", noticeCode, identifier);
    			}
    			
    		} catch (Exception e) {
    			logger.error("warehouseNoticeCode:{} 入库通知，解锁失败，identifier:{}, err:{}", 
    					noticeCode, identifier, e.getMessage());
    			e.printStackTrace();
    		}
    	}
        return ResultUtil.createSuccessResult("操作成功","");

    }

    /**
     * 入库通知单详情页的入库通知操作
     * @param warehouseNotice
     * @param requestContext
     * @return
     */
    @PUT
    @Path(SupplyConstants.WarehouseNotice.RECEIPT_ADVICE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiptAdviceInfo(@BeanParam WarehouseNotice warehouseNotice,@Context ContainerRequestContext requestContext){
        String identifier = "";
    	identifier = redisLock.Lock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode() + 
    			warehouseNotice.getWarehouseNoticeCode(), 0, 10000);
    	if (StringUtils.isBlank(identifier)) {
    		throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, "请不要重复操作!");
    	}
    	try {
    		//入库通知单详情页的入库通知操作
    		warehouseNoticeBiz.receiptAdviceInfo(warehouseNotice,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    	} finally {
        	String noticeCode = warehouseNotice.getWarehouseNoticeCode();
    		try {
    			if (redisLock.releaseLock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode() 
    					+ noticeCode, identifier)) {
    				logger.info("warehouseNoticeCode:{} 入库通知，解锁成功，identifier:{}", noticeCode, identifier);
    			} else {
    				logger.error("warehouseNoticeCode:{} 入库通知，解锁失败，identifier:{}", noticeCode, identifier);
    			}
    			
    		} catch (Exception e) {
    			logger.error("warehouseNoticeCode:{} 入库通知，解锁失败，identifier:{}, err:{}", 
    					noticeCode, identifier, e.getMessage());
    			e.printStackTrace();
    		}
    	}
        return ResultUtil.createSuccessResult("操作成功","");

    }

    /**
     * 根据入库单id获取入库单详情，不包括商品明细
     * @param id
     * @return
     */
    @GET
    @Path(SupplyConstants.WarehouseNotice.WAERHOUSE_NOTICE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWarehouseNoticeInfoById(@PathParam("id") Long id){

        return ResultUtil.createSuccessResult("查询入库通知单信息成功",warehouseNoticeBiz.findfindWarehouseNoticeById(id));

    }

    /**
     * 根据入库单code获取入库单商品明细0
     * @param warehouseNotice
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.WarehouseNotice.WAREHOUSE_NOTICE_DETAIL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseNoticeDetailList(@QueryParam("warehouseNotice") Long warehouseNotice)throws Exception{
        //"根据入库通知单的id，查询入库明细成功",
        return ResultUtil.createSuccessPageResult(warehouseNoticeBiz.warehouseNoticeDetailList(warehouseNotice));
    }

    /**
	 * 入库通知单：取消收货flag=0 ;重新收货flag=1
	 */
	@PUT
	@Path(SupplyConstants.WarehouseNotice.CANCEL)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancel(@PathParam("warehouseNoticeCode")String warehouseNoticeCode,@FormParam("flag") String flag,@FormParam("cancelReason") String cancelReason,@Context ContainerRequestContext requestContext){
		String identifier = "";
		AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
		identifier=redisLock.Lock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode()+"cancel"+
		warehouseNoticeCode,0,10000);
		if (StringUtils.isBlank(identifier)){
			throw new RuntimeException("重复操作！");
		}
		try {
			warehouseNoticeBiz.cancel(warehouseNoticeCode,flag,cancelReason,aclUserAccreditInfo);
		}
		finally {
			try {
				if(redisLock.releaseLock(DistributeLockEnum.WAREHOUSE_NOTICE_CREATE.getCode()+"cancel"+
						warehouseNoticeCode,identifier)){
					logger.info("warehouseNoticeCode:{}入库通知，解锁成功，identifier:{}",warehouseNoticeCode,identifier);
				}else {
					logger.error("warehouseNoticeCode:{}入库通知，解锁失败，identifier:{}",warehouseNoticeCode,identifier);
				}
			}catch (Exception e){
				logger.error("warehouseNoticeCode:{}入库通知，解锁失败，identifier:{},err:{}",warehouseNoticeCode,identifier,e.getMessage());
				e.printStackTrace();
			}
		}

		return ResultUtil.createSuccessResult("操作成功","");
	}

}
