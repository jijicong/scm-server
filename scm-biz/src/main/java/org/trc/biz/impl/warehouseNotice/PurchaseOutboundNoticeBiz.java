package org.trc.biz.impl.warehouseNotice;

import static org.trc.biz.impl.allocateOrder.AllocateOutOrderBiz.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.OrderCancelResultEnum;
import org.trc.enums.WarehouseTypeEnum;
import org.trc.enums.warehouse.CancelOrderType;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.form.JDWmsConstantConfig;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnItem;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnOrderCreateRequest;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnOrderCreateResponse;
import org.trc.service.config.ILogInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import org.trc.service.util.IRealIpService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
@Service("purchaseOutboundNoticeBiz")
public class PurchaseOutboundNoticeBiz implements IPurchaseOutboundNoticeBiz {
	
	@Autowired
	private IPurchaseOutboundNoticeService noticeService;
	@Autowired
	private IPurchaseOutboundDetailService detailService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private ICommonService commonService;
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private IRealIpService realIpService;
    @Autowired
    private ILogInfoService logInfoService;
    
    private Logger logger = LoggerFactory.getLogger(PurchaseOutboundNoticeBiz.class);
    
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	@Override
	public Pagenation<PurchaseOutboundNotice> getPageList(PurchaseOutboundNoticeForm form,
			Pagenation<PurchaseOutboundNotice> page, String channelCode) {
		AssertUtil.notNull(channelCode, "业务线编号不能为空");
		Pagenation<PurchaseOutboundNotice> resultPage = noticeService.pageList(form, page, channelCode);
		noticeService.generateNames(resultPage);
		return resultPage;
	}

	@Override
	public PurchaseOutboundNotice getDetail(Long id) {
		PurchaseOutboundNotice notice = noticeService.selectByPrimaryKey(id);
		AssertUtil.notNull(notice, "未找到相应的退货单号");
		
		List<PurchaseOutboundDetail> skuList = detailService.selectDetailByNoticeCode(notice.getOutboundNoticeCode());
		notice.setSkuList(skuList);
		return notice;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void noticeOut(String code, AclUserAccreditInfo userInfo) {
		
		// 入参校验
		PurchaseOutboundNotice notice = checkCode(code);
		
		/**
		 * 待通知出库, 出库仓接收失败, 已取消 
		 * 上面几种状态才允许退货出库
		 */
		if (!PurchaseOutboundNoticeStatusEnum.TO_BE_NOTIFIED.getCode().equals(notice.getStatus())
				&& !PurchaseOutboundNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode().equals(notice.getStatus())
				&& !PurchaseOutboundNoticeStatusEnum.CANCEL.getCode().equals(notice.getStatus())) {
			throw new IllegalArgumentException("当前退货出库通知单状态不允许通知出库!");
		}
		
		ScmEntryReturnOrderCreateRequest request = new ScmEntryReturnOrderCreateRequest();
		String whName = commonService.getWarehoueType(notice.getWarehouseCode(), request); // 获取仓库类型，并设置到request中
		
		/**
		 * 京东仓库处理逻辑
		 */
		if (WarehouseTypeEnum.Jingdong.getCode().equals(request.getWarehouseType())) {
			BeanUtils.copyProperties(notice, request);
			request.setDeptNo(jDWmsConstantConfig.getDeptNo()); // 事业部编号
			
			/**
			 * 组装商品详情
			 */
			List<PurchaseOutboundDetail> skuList = detailService.selectDetailByNoticeCode(notice.getOutboundNoticeCode());
			AssertUtil.listNotEmpty(skuList, "退货出库通知单的商品列表为空!");
			
			ScmEntryReturnItem item = null;
			
			List<String> skuCodeList = skuList.stream().map(
					PurchaseOutboundDetail :: getSkuCode).collect(Collectors.toList());
			
			List<WarehouseItemInfo> whiList = warehouseItemInfoService.
					selectInfoListBySkuCodeAndWarehouseCode(skuCodeList, notice.getWarehouseCode());
			AssertUtil.listNotEmpty(whiList, "仓库中的商品信息为空!");
			
			List<ScmEntryReturnItem> list = new ArrayList<>();
	        for (PurchaseOutboundDetail sku : skuList) {
	        	for (WarehouseItemInfo info : whiList) {
					if (StringUtils.equals(info.getSkuCode(), sku.getSkuCode())) {
						item = new ScmEntryReturnItem();
						item.setItemId(info.getWarehouseItemId());
						item.setReturnQuantity(sku.getOutboundQuantity());
						list.add(item);
						break;
					}
				}
	        }
	        request.setEntryOrderItemList(list);
			
		} else {
			throw new IllegalArgumentException("暂时不支持自营仓库的退货出库操作!");
		}
		
		AppResult<ScmEntryReturnOrderCreateResponse> response = warehouseApiService.entryReturnOrderCreate(request);
		
        //记录操作日志 (动作：通知出库;操作人：用户姓名)
        logInfoService.recordLog(notice, notice.getId().toString(),
        		userInfo.getUserId(), LogOperationEnum.ENTRY_RETURN_NOTICE.getMessage(), "", null);
		
        PurchaseOutboundNoticeStatusEnum status = null;// 退货出库通知单状态
        String logOp = null;// 日志动作
        String errMsg = null;// 失败原因
        String wmsEntryRtCode = null; // 仓库返回的单号
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			
			status = PurchaseOutboundNoticeStatusEnum.ON_WAREHOUSE_TICKLING;
			logOp = LogOperationEnum.ENTRY_RETURN_NOTICE_SUCC.getMessage();
			ScmEntryReturnOrderCreateResponse rep = (ScmEntryReturnOrderCreateResponse) response.getResult();
			wmsEntryRtCode = rep.getWmsEntryReturnNoticeCode();
		} else {
			
			status = PurchaseOutboundNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED;
			logOp = LogOperationEnum.ENTRY_RETURN_NOTICE_FAIL.getMessage();
			errMsg = response.getDatabuffer();
		}
		
		/**
		 * 更新操作
		 */
		noticeService.updateById(status, notice.getId(), errMsg, wmsEntryRtCode);
		detailService.updateByOrderCode(status, notice.getOutboundNoticeCode());
		
		//记录操作日志 (动作：出库仓接收成功（失败）; 操作人：仓库名称; 备注：失败原因)
        logInfoService.recordLog(notice, notice.getId().toString(), whName,
        		logOp, errMsg, null);
	}

	@Override
	public Response cancel(String code, String cancelReson, AclUserAccreditInfo useInfo) {
		
		AssertUtil.notBlank(cancelReson, "取消原因不能为空");
		// 入参校验
		PurchaseOutboundNotice notice = checkCode(code);
		
		PurchaseOutboundNoticeStatusEnum cancelSts = null;// 取消状态
    	String cancelResult = null;// 取消结果
    	ScmOrderCancelRequest request = new ScmOrderCancelRequest();
    	request.setOrderType(CancelOrderType.ENTRY_RETURN.getCode());
    	// 自营仓 和 三方仓库 统一取EntryOrderId
    	request.setOrderCode(notice.getEntryOrderId());
        commonService.getWarehoueType(notice.getWarehouseCode(), request);
    	
    	AppResult<ScmOrderCancelResponse> response = warehouseApiService.orderCancel(request);
    	
		if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			
			ScmOrderCancelResponse respResult = (ScmOrderCancelResponse)response.getResult();
			if (OrderCancelResultEnum.CANCEL_SUCC.code.equals(respResult.getFlag())) { // 取消成功
				
				cancelSts = PurchaseOutboundNoticeStatusEnum.CANCEL;
				cancelResult = OrderCancelResultEnum.CANCEL_SUCC.name;
				
			} else if (OrderCancelResultEnum.CANCELLING.code.equals(respResult.getFlag())) { // 取消中
				
				cancelSts = PurchaseOutboundNoticeStatusEnum.CANCELLING;
				cancelResult = OrderCancelResultEnum.CANCELLING.name;
				
			} else { // 取消失败
				//记录操作日志 (动作：取消出库; 操作人：仓库名称; 备注：取消原因+取消结果)
		        logInfoService.recordLog(notice, notice.getId().toString(), useInfo.getUserId(),
		        		LogOperationEnum.ENTRY_RETURN_NOTICE_CANCEL.getMessage(), 
		        		"取消原因：" + cancelReson + "，取消结果：取消失败，原因：" + respResult.getMessage(), null);
				return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "取消出库失败:" + response.getDatabuffer());
            }
		} else { // 取消异常
			
			logger.error(String.format("%s 取消出库异常:%s", code, response.getDatabuffer()));
			return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "取消出库异常:" + response.getDatabuffer());
		}
		
		/**
		 * 更新操作
		 */
		noticeService.updateById(cancelSts, notice.getId(), null, null);
		detailService.updateByOrderCode(cancelSts, notice.getOutboundNoticeCode());
		
		//记录操作日志 (动作：取消出库; 操作人：仓库名称; 备注：取消原因+取消结果)
        logInfoService.recordLog(notice, notice.getId().toString(), useInfo.getUserId(),
        		LogOperationEnum.ENTRY_RETURN_NOTICE_CANCEL.getMessage(), 
        		"取消原因：" + cancelReson + "，取消结果：" + cancelResult, null);
		
		return ResultUtil.createSuccessResult("取消操作成功","");
	}
	
	private PurchaseOutboundNotice checkCode (String code) {
		
		AssertUtil.notNull(code, "退货出库通知单编号不能为空!");
		
		List<PurchaseOutboundNotice> noticeList = noticeService.selectNoticeBycode(code);
		if (CollectionUtils.isEmpty(noticeList)) {
			throw new IllegalArgumentException("退货出库通知单编号有误!");
		} else if (noticeList.size() > 1) {
			throw new IllegalArgumentException("退货出库通知单编号重复!");
		}
		return noticeList.get(0);
	}
	
	
    //取消收货 取消中状态定时任务
    @Override
    public void retryCancelOrder() {
    	
        if (!realIpService.isRealTimerService()) {
        	return;
        }
        
        // 查询满足条件(取消中)的出库通知单
        List<PurchaseOutboundNotice> noticeList = noticeService.
        		selectNoticeByStatus(PurchaseOutboundNoticeStatusEnum.CANCELLING);

        if (CollectionUtils.isEmpty(noticeList)) {
        	return;
        }
        
        List<ScmOrderCancelRequest> requests = new ArrayList<>();
        
        ScmOrderCancelRequest cancelRequest = null;
        for (PurchaseOutboundNotice notice : noticeList) {
        	cancelRequest = new ScmOrderCancelRequest();
        	cancelRequest.setOrderCode(notice.getEntryOrderId());
        	cancelRequest.setWarehouseType("JD");
        	cancelRequest.setOrderType(CancelOrderType.PURCHASE.getCode());
            requests.add(cancelRequest);
        }

        //调用接口
        for (ScmOrderCancelRequest request : requests) {
        	cachedThreadPool.execute(() -> {
                //调用接口
                AppResult<ScmOrderCancelResponse> responseAppResult = warehouseApiService.orderCancel(request);
                //回写数据
                try {
                    this.updateCancelOrder(responseAppResult, request.getOrderCode());
                } catch (Exception e) {
                    logger.error("采购退货出库单号:{},定时任务取消入库异常：{}, 异常原因：", 
                    		request.getOrderCode(), responseAppResult.getResult(), e);
                }
        	});
        }
    }
    
    private void updateCancelOrder(AppResult<ScmOrderCancelResponse> appResult, String entryOrderCode) {
    	
        if (StringUtils.equals(appResult.getAppcode(), SUCCESS)) { // 成功
        	
        	PurchaseOutboundNoticeStatusEnum status = null;// 退货出库通知单状态
        	PurchaseOutboundNotice notice = noticeService.selectOneByEntryOrderCode(entryOrderCode);
        	
        	String logRemark = null; //日志备注
        	
            ScmOrderCancelResponse response = (ScmOrderCancelResponse)appResult.getResult();
            String flag = response.getFlag();
            
            if (StringUtils.equals(flag, OrderCancelResultEnum.CANCEL_SUCC.code)) {//取消成功
            	
            	status = PurchaseOutboundNoticeStatusEnum.CANCEL;
            	logRemark = "取消结果:取消成功";
            	
            } else if (StringUtils.equals(flag, OrderCancelResultEnum.CANCEL_FAIL.code)) { // 取消失败 状态复原
            	
            	status = PurchaseOutboundNoticeStatusEnum.ON_WAREHOUSE_TICKLING;
            	logRemark = "取消结果:取消失败；原因：" + response.getMessage();

            }
            
    		/**
    		 * 更新操作
    		 */
    		noticeService.updateById(status, notice.getId(), null, null);
    		detailService.updateByOrderCode(status, notice.getOutboundNoticeCode());
    		// 日志 admin??
            logInfoService.recordLog(notice, notice.getId().toString(), "admin",
            		LogOperationEnum.ENTRY_RETURN_NOTICE_CANCEL.getMessage(), logRemark, null);
        } else {
        	
        }
	}


    

        
}
