package org.trc.biz.impl.warehouseNotice;


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
import org.trc.domain.category.Brand;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.warehouseInfo.WarehouseInfo;
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
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnDetailRequest;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnDetailResponse;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnItem;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnOrderCreateRequest;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnOrderCreateResponse;
import org.trc.service.category.IBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import org.trc.service.util.IRealIpService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ListSplit;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

import net.bytebuddy.description.ByteCodeElement.Token.TokenList;

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
    @Autowired
    private IBrandService brandService;
    
    private Logger logger = LoggerFactory.getLogger(PurchaseOutboundNoticeBiz.class);
    
    public static final String SUCCESS = "200";
    
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

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
        //设置品牌名称
		try {
			if (!CollectionUtils.isEmpty(skuList)) {
				List<Long> bandIdList = skuList.stream().map(item -> Long.valueOf(item.getBrandId())).collect(Collectors.toList());
				List<Brand> brandList = brandService.selectBrandList(bandIdList);
				for (PurchaseOutboundDetail sku : skuList) {
					for (Brand band : brandList) {
						if (StringUtils.equals(band.getId().toString(), sku.getBrandId())) {
							sku.setBrandName(band.getName());
							break;
						}
					}
				}
				
			}
		} catch (Exception e) {
			logger.error("获取采购退货入库单详情时，设置品牌名称异常", e);
		}
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
		WarehouseInfo whi = commonService.getWarehoueType(notice.getWarehouseCode(), request); // 获取仓库类型，并设置到request中
		
		/**
		 * 京东仓库处理逻辑
		 */
		if (WarehouseTypeEnum.Jingdong.getCode().equals(request.getWarehouseType())) {
			BeanUtils.copyProperties(notice, request);
			request.setDeptNo(jDWmsConstantConfig.getDeptNo()); // 事业部编号
			request.setWarehouseCode(whi.getWmsWarehouseCode()); // 京东事业部退库库房编号
			
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
				item = new ScmEntryReturnItem();
				item.setItemId(sku.getWarehouseItemId());
				item.setReturnQuantity(sku.getOutboundQuantity());
				list.add(item);
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
		detailService.updateByOrderCode(status, null, null, notice.getOutboundNoticeCode());
		
		//记录操作日志 (动作：出库仓接收成功（失败）; 操作人：仓库名称; 备注：失败原因)
        logInfoService.recordLog(notice, notice.getId().toString(), whi.getWarehouseName(),
        		logOp, errMsg, null);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Response cancel(String code, String cancelReson, AclUserAccreditInfo useInfo) {
		
		AssertUtil.notBlank(cancelReson, "取消原因不能为空");
		// 入参校验
		PurchaseOutboundNotice notice = checkCode(code);
		
		if (!PurchaseOutboundNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode()
				.equals(notice.getStatus())) {
			throw new IllegalArgumentException("当前退货出库通知单状态不能取消!");
		}
		
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
		detailService.updateByOrderCode(cancelSts, null, null, notice.getOutboundNoticeCode());
		
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
	
	
    /**
     * 取消收货 取消中状态定时任务
     */
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
        	cancelRequest.setOrderType(CancelOrderType.ENTRY_RETURN.getCode());
            requests.add(cancelRequest);
        }

        //调用接口
        for (ScmOrderCancelRequest request : requests) {
        	threadPool.execute(() -> {
                //调用接口
                AppResult<ScmOrderCancelResponse> responseAppResult = warehouseApiService.orderCancel(request);
                //回写数据
                try {
                	noticeService.updateCancelOrder(responseAppResult, request.getOrderCode());
                } catch (Exception e) {
                    logger.error("采购退货出库单号:{},定时任务取消入库异常, 异常原因：", 
                    		request.getOrderCode(), e);
                }
        	});
        }
    }
    
    /**
     * 退货出库通知单查询详情 定时任务
     */
    @Override
    public void entryReturnDetailQuery() {
    	
        if (!realIpService.isRealTimerService()) {
        	return;
        }
        
        // 查询满足条件(出库仓接收成功)的出库通知单
        List<PurchaseOutboundNotice> noticeList = noticeService.
        		selectNoticeByStatus(PurchaseOutboundNoticeStatusEnum.ON_WAREHOUSE_TICKLING);

        if (CollectionUtils.isEmpty(noticeList)) {
        	return;
        }
        
        // 接口支持一次查询十个单号查询，需要分割符合条件的采购
        List<List<PurchaseOutboundNotice>> splitList = ListSplit.split(noticeList, 10);
        //分批调用接口
        List<ScmEntryReturnDetailRequest> reqList = new ArrayList<>();
        ScmEntryReturnDetailRequest request = null;
        for (List<PurchaseOutboundNotice> splitOneList : splitList) {
        	String reqCodeList = splitOneList.stream()
        			.map(PurchaseOutboundNotice :: getEntryOrderId).collect(Collectors.joining(","));
        	request = new ScmEntryReturnDetailRequest();
        	request.setWmsEntryReturnNoticeCode(reqCodeList);
        	request.setWarehouseType("JD");
        	reqList.add(request);
        }
        for (ScmEntryReturnDetailRequest req : reqList) {
        	threadPool.execute(() -> {
        		AppResult responseResult = warehouseApiService.entryReturnDetail(req);
        		try {
        			 if (StringUtils.equals(responseResult.getAppcode(), ResponseAck.SUCCESS_CODE)) { // 成功
        				 List<ScmEntryReturnDetailResponse> respList = (List<ScmEntryReturnDetailResponse>) responseResult.getResult();
        				 for (ScmEntryReturnDetailResponse resp : respList) {
        					 this.updateEntryReturn(resp);
        				 }
    				 } else {
    					 logger.error("采购退货出库单号:{}定时任务查询出库单详情异常:{}", 
    							 req.getWmsEntryReturnNoticeCode(),responseResult.getDatabuffer());
    				 }
        		} catch (Exception e) {
        			logger.error("采购退货出库单号:{}定时任务查询出库单详情异常：{}, 异常原因：", 
        					req.getWmsEntryReturnNoticeCode(), e);
        		}
        	});
        }
        
    }
    
    private void updateEntryReturn(ScmEntryReturnDetailResponse resp) {
		 try {
			 noticeService.updateEntryReturn(resp);
		 } catch (Exception e) {
			logger.error("采购退货出库单号:{},定时任务查询出库单详情异常：{}, 异常原因：", 
					resp.getOutboundNoticeCode(), e);
		 }
    }
    
    

        
}
