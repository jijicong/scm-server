package org.trc.service.impl.warehouseNotice;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.stock.JdStockOutDetail;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.OrderCancelResultEnum;
import org.trc.enums.purchase.PurchaseOutboundStatusEnum;
import org.trc.enums.report.StockOperationTypeEnum;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnDetailItem;
import org.trc.form.warehouse.entryReturnOrder.ScmEntryReturnDetailResponse;
import org.trc.mapper.impower.AclUserAccreditInfoMapper;
import org.trc.mapper.purchase.PurchaseOutboundOrderMapper;
import org.trc.mapper.supplier.ISupplierMapper;
import org.trc.mapper.warehouseInfo.IWarehouseInfoMapper;
import org.trc.mapper.warehouseNotice.IPurchaseOutboundNoticeMapper;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import org.trc.service.stock.IJdStockOutDetailService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
@Service("purchaseOutboundNoticeService")
public class PurchaseOutboundNoticeService extends BaseService<PurchaseOutboundNotice, Long> implements
	IPurchaseOutboundNoticeService {

	@Autowired
	private AclUserAccreditInfoMapper userInfoMapper;
	@Autowired
	private IWarehouseInfoMapper whiMapper;
	@Autowired
	private IPurchaseOutboundNoticeMapper noticeMapper;
	@Autowired
	private ISupplierMapper supplierMapper;
	@Autowired
	private IPurchaseOutboundDetailService detailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
	private PurchaseOutboundOrderMapper purchaseOutboundOrderMapper;
    @Autowired
    private IJdStockOutDetailService jdStockOutDetailService;

    private static final String CANCELLED = "400"; //400.已取消
    private static final String FINISH = "200";  // 200.完成

    private Logger logger = LoggerFactory.getLogger(PurchaseOutboundNoticeService.class);

	@Override
	public Pagenation<PurchaseOutboundNotice> pageList (PurchaseOutboundNoticeForm form,
			Pagenation<PurchaseOutboundNotice> page, String channelCode) {

		Example example = new Example(PurchaseOutboundNotice.class);
        Example.Criteria criteria = example.createCriteria();

        //业务线编号
        criteria.andEqualTo("channelCode", channelCode);

        //退货出库单号
        if (StringUtils.isNotBlank(form.getOutboundNoticeCode())) {
            criteria.andEqualTo("outboundNoticeCode", form.getOutboundNoticeCode());
        }

        //采购退货单编号
        if (StringUtils.isNotBlank(form.getPurchaseOutboundOrderCode())) {
            criteria.andEqualTo("purchaseOutboundOrderCode", form.getPurchaseOutboundOrderCode());
        }

        //退货仓库
        if (StringUtils.isNotBlank(form.getWarehouseCode())) {
        	criteria.andEqualTo("warehouseCode", form.getWarehouseCode());
        }

        //供应商编号
        if (StringUtils.isNotBlank(form.getSupplierCode())) {
        	criteria.andEqualTo("supplierCode", form.getSupplierCode());
        }

        //出库单状态
        if (StringUtils.isNotBlank(form.getStatus())) {
        	// 已取消 （手动取消，作废）
        	if (PurchaseOutboundNoticeStatusEnum.CANCEL.getCode().equals(form.getStatus())) {
                List<String> statusList = Arrays.asList(PurchaseOutboundNoticeStatusEnum.CANCEL.getCode(),
                		PurchaseOutboundNoticeStatusEnum.DROP.getCode());
        		criteria.andIn("status", statusList);
        	} else {
        		criteria.andEqualTo("status", form.getStatus());
        	}
        }

        //创建日期开始
        if (StringUtils.isNotBlank(form.getStartDate())) {
        	criteria.andGreaterThanOrEqualTo("createTime", form.getStartDate() + " 00:00:00");
        }

        //创建日期结束
        if (StringUtils.isNotBlank(form.getEndDate())) {
        	criteria.andLessThanOrEqualTo("createTime", form.getEndDate() + " 23:59:59");
        }

        //出库单创建人
        if (StringUtils.isNotBlank(form.getCreateUser())) {
            Example userExample = new Example(AclUserAccreditInfo.class);
            Example.Criteria userCriteria = userExample.createCriteria();

            userCriteria.andLike("name", "%" + form.getCreateUser() + "%");

            List<AclUserAccreditInfo> userList = userInfoMapper.selectByExample(userExample);
            if (CollectionUtils.isEmpty(userList)) {
            	// 未查询到结果
            	return new Pagenation<>();
            } else {
            	List<String> userIdList = new ArrayList<>();
            	for (AclUserAccreditInfo user : userList) {
            		userIdList.add(user.getUserId());
            	}
            	criteria.andIn("createOperator", userIdList);
            }
        }

//		example.setOrderByClause("field(status," + PurchaseOutboundNoticeStatusEnum.TO_BE_NOTIFIED.getCode() +
//				"," + PurchaseOutboundNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode() + ") desc");
		example.setOrderByClause("status in('0','2') desc");
        example.orderBy("createTime").desc();

        Pagenation<PurchaseOutboundNotice> pageResult = this.pagination(example, page, form);
       // List<PurchaseOutboundNotice> result2 = result.getResult();
        return pageResult;


	}

	@Override
	public List<PurchaseOutboundNotice> selectNoticeBycode(String outboundNoticecode) {
		PurchaseOutboundNotice queryRecord = new PurchaseOutboundNotice();
		queryRecord.setOutboundNoticeCode(outboundNoticecode);// 系统退货出库单号
		return noticeMapper.select(queryRecord);
	}

	@Override
	public void updateById(PurchaseOutboundNoticeStatusEnum status, Long id,
			String errMsg, String wmsEntryRtCode) {
		PurchaseOutboundNotice updateRecord = new PurchaseOutboundNotice();
		updateRecord.setId(id);
		updateRecord.setStatus(status.getCode());
		updateRecord.setFailureCause(errMsg);
		updateRecord.setEntryOrderId(wmsEntryRtCode);
		noticeMapper.updateByPrimaryKeySelective(updateRecord);
	}

	@Override
	public void generateNames(Pagenation<PurchaseOutboundNotice> resultPage) {
		List<PurchaseOutboundNotice> resultList = resultPage.getResult();
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }

        Set<String> warehouseCodes = new HashSet<>(); // 仓库
        Set<String> operatorIds = new HashSet<>();  // 创建人
        Set<String> supplyCodes = new HashSet<>();  // 供应商

        List<WarehouseInfo> warehouseInfoList = null;
        List<AclUserAccreditInfo> aclUserAccreditInfoList = null;
        List<Supplier> supplierList = null;

        for (PurchaseOutboundNotice notice : resultList) {
            warehouseCodes.add(notice.getWarehouseCode());
            operatorIds.add(notice.getCreateOperator());
            supplyCodes.add(notice.getSupplierCode());
        }

        if (!warehouseCodes.isEmpty()) {
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", warehouseCodes);
            warehouseInfoList = whiMapper.selectByExample(example);
        }
        if (!operatorIds.isEmpty()) {
            Example example = new Example(AclUserAccreditInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("userId", operatorIds);
            aclUserAccreditInfoList = userInfoMapper.selectByExample(example);
        }
        if (!supplyCodes.isEmpty()) {
        	Example example = new Example(Supplier.class);
        	Example.Criteria criteria = example.createCriteria();
        	criteria.andIn("supplierCode", supplyCodes);
        	supplierList = supplierMapper.selectByExample(example);
        }

        for (PurchaseOutboundNotice notice : resultList) {
            if (!CollectionUtils.isEmpty(warehouseInfoList)) {
                for (WarehouseInfo warehouseInfo: warehouseInfoList) {
                    if(StringUtils.equals(notice.getWarehouseCode(), warehouseInfo.getCode())){
                    	notice.setWarehouseName(warehouseInfo.getWarehouseName());
                        break;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(aclUserAccreditInfoList)) {
                for (AclUserAccreditInfo userAccreditInfo: aclUserAccreditInfoList) {
                    if (StringUtils.equals(notice.getCreateOperator(), userAccreditInfo.getUserId())) {
                    	notice.setCreatorName(userAccreditInfo.getName());
                        break;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(supplierList)) {
            	for (Supplier supplier : supplierList) {
            		if (StringUtils.equals(notice.getSupplierCode(), supplier.getSupplierCode())) {
            			notice.setSupplierName(supplier.getSupplierName());
            			break;
            		}
            	}
            }
        }
	}

	@Override
	public List<PurchaseOutboundNotice> selectNoticeByStatus(PurchaseOutboundNoticeStatusEnum status) {
        PurchaseOutboundNotice queryRecord = new PurchaseOutboundNotice();
        queryRecord.setStatus(status.getCode());
        return noticeMapper.select(queryRecord);
	}


	@Override
	public PurchaseOutboundNotice selectOneByEntryOrderCode(String entryOrderCode) {
		PurchaseOutboundNotice queryRecord = new PurchaseOutboundNotice();
		queryRecord.setEntryOrderId(entryOrderCode); // 仓库反馈退货出库单号
		return noticeMapper.selectOne(queryRecord);
	}

	@Override
	@Transactional
	public void updateCancelOrder(AppResult<ScmOrderCancelResponse> appResult, String entryOrderCode) {

        if (StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)) { // 成功

        	PurchaseOutboundNoticeStatusEnum status = null;// 退货出库通知单状态
        	PurchaseOutboundNotice notice = this.selectOneByEntryOrderCode(entryOrderCode);

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
    		this.updateById(status, notice.getId(), null, null);
    		detailService.updateByOrderCode(status, notice.getOutboundNoticeCode());
    		// 日志 admin??
            logInfoService.recordLog(notice, notice.getId().toString(), "admin",
            		LogOperationEnum.ENTRY_RETURN_NOTICE_CANCEL.getMessage(), logRemark, null);
        } else {
        	logger.error("wms采购退货出库单号:{},取消出库异常：{}", entryOrderCode, appResult.getDatabuffer());
        }
	}

	@Override
	@Transactional
	public void updateEntryReturn(ScmEntryReturnDetailResponse resp) {

		 String noticeCode = resp.getOutboundNoticeCode();
		 String wmsCode = resp.getWmsEntryReturnNoticeCode();

		 if (CANCELLED.equals(resp.getStatus())) { // 已取消

			 this.updateByCode(PurchaseOutboundNoticeStatusEnum.CANCEL, null, noticeCode);
			 detailService.updateByOrderCode(PurchaseOutboundNoticeStatusEnum.CANCEL, noticeCode);
			 // 日志处理
			 try {
				 PurchaseOutboundNotice notice = this.selectOneByEntryOrderCode(wmsCode);
	             WarehouseInfo warehouse = warehouseInfoService.selectOneByCode(notice.getWarehouseCode());
		         logInfoService.recordLog(notice, notice.getId().toString(), warehouse.getWarehouseName(),
		        		LogOperationEnum.ENTRY_RETURN_NOTICE_CANCEL.getMessage(), "京东后台取消", null);
			 } catch (Exception e) {
				 logger.error("采购退货出库单定时任务获取出库取消状态时，日志记录异常，原因:", e);
			 }

		 } else if (FINISH.equals(resp.getStatus())) { // 已完成

		 	List<PurchaseOutboundDetail> detailList = detailService.selectDetailByNoticeCode(noticeCode);
		 	List<ScmEntryReturnDetailItem> itemList = resp.getItemList();

			if (CollectionUtils.isEmpty(detailList) || CollectionUtils.isEmpty(itemList)) {
				throw new IllegalArgumentException("查询采购出库单详情异常，系统或者接口返回的商品列表为空!");
			}

			if (detailList.size() != itemList.size()) {
				throw new IllegalArgumentException("系统与接口返回的商品列表长度不相等");
			}

			Map<String, PurchaseOutboundDetail> detailMap = detailList.stream()
					.collect(Collectors.toMap(PurchaseOutboundDetail :: getWarehouseItemId, detail -> detail));

			 PurchaseOutboundNoticeStatusEnum status; // 出库通知单状态
			 PurchaseOutboundStatusEnum orderStatus;	//采购退货出库状态

			 boolean exceptionFlg = false; // 是否存在出库异常的商品 - flg
			 List<String> remarkList = new ArrayList<>();  // 日志备注显示 eg. (sku编号1：出库完成/出库异常)
			 List<String> skuList = new ArrayList<>(); // 出库异常的商品编码列表
			 String exceptionCause = null;
			 Date nowTime = Calendar.getInstance().getTime();
			 for (ScmEntryReturnDetailItem item : itemList) {
				 PurchaseOutboundNoticeStatusEnum detailStatus = PurchaseOutboundNoticeStatusEnum.PASS; // 商品详情状态
				 PurchaseOutboundDetail detail = detailMap.get(item.getItemId());

				 if (detail == null) {
					throw new IllegalArgumentException("接口返回商品id有误");
				 }

				 String result = "出库完成";
				// 商品状态和数量是否相当 (枚举值：1.良品)
				 if (!StringUtils.equals(detail.getReturnOrderType(), item.getGoodsStatus())
						 || detail.getOutboundQuantity().longValue() != item.getActualQty().longValue()) {

					 detailStatus = PurchaseOutboundNoticeStatusEnum.RECEIVE_EXCEPTION; // 商品详情状态-出库异常
					 exceptionFlg = true;
					 result = "出库异常";
					 skuList.add(detail.getSkuCode());
				 }
				 remarkList.add(detail.getSkuCode() + ":" + result);
				 long actualQty = 0l;
				 // 更新商品详情
				 if (StringUtils.equals(detail.getReturnOrderType(), item.getGoodsStatus())) {
					 actualQty = item.getActualQty().longValue();
				 }
				 detailService.updateByDetailId(detailStatus, nowTime, actualQty, detail.getId());
				 // 记录库存变动明细
				 try {
					 insertStockDetail(actualQty, detail, wmsCode);
				 } catch (Exception e) {
					 logger.error("JD仓采购退货记录库存变动明细失败， 仓库反馈code:{}, e:", wmsCode, e);
				 }
			 }
			 if (!CollectionUtils.isEmpty(skuList)) {
				 exceptionCause = "[" + StringUtils.join(skuList, SupplyConstants.Symbol.COMMA) + "]实际出库数量不等于要求退货数量";
			 }
			 String remark = StringUtils.join(remarkList,"<br>");

			 // 存在"出库异常"的商品 或者 商品总数不相等时
			 if (exceptionFlg) {
				 status =  PurchaseOutboundNoticeStatusEnum.RECEIVE_EXCEPTION; // 出库通知单状态-出库异常
				 orderStatus = PurchaseOutboundStatusEnum.EXCEPTION;
			 } else {
				 status = PurchaseOutboundNoticeStatusEnum.PASS;  // 所有商品的“出库状态”均为“出库完成”，此处就更新为出库完成
				 orderStatus = PurchaseOutboundStatusEnum.FINISH;
			 }
			 // 更新采购退货出库单
			 this.updateByCode(status, exceptionCause, noticeCode);

			 //同步采购退货单出库状态
			 updatePurchaseOutboundStatus(orderStatus, wmsCode);

			 // 日志处理
			 try {
				 PurchaseOutboundNotice notice = this.selectOneByEntryOrderCode(wmsCode);
	             WarehouseInfo warehouse = warehouseInfoService.selectOneByCode(notice.getWarehouseCode());
		         logInfoService.recordLog(notice, notice.getId().toString(), warehouse.getWarehouseName(),
		        		LogOperationEnum.ENTRY_RETURN_NOTICE_FINISH.getMessage(), remark, null);

		         //采购退货单日志
				 PurchaseOutboundOrder order = selectOneOrder(notice.getPurchaseOutboundOrderCode());
				 logInfoService.recordLog(order, order.getId().toString(), warehouse.getWarehouseName(),
						 LogOperationEnum.ENTRY_RETURN_NOTICE_SYNCHRONIZE.getMessage(), remark, null);
			 } catch (Exception e) {
				 logger.error("采购退货出库单定时任务获取出库状态时，日志记录异常，原因:", e);
			 }

		 }

	}

	private PurchaseOutboundNotice insertStockDetail(long actualQty, PurchaseOutboundDetail detail, String wmsCode) {

		logger.info("JD采购出库记录库存变动明， 订单编号:{}", wmsCode);

		PurchaseOutboundNotice notice = this.selectOneByEntryOrderCode(wmsCode);
		JdStockOutDetail jdStockOutDetail = new JdStockOutDetail();
		jdStockOutDetail.setWarehouseCode(notice.getWarehouseCode());
		jdStockOutDetail.setOutboundOrderCode(notice.getOutboundNoticeCode());
		jdStockOutDetail.setStockType(detail.getReturnOrderType());

		jdStockOutDetail.setOperationType(StockOperationTypeEnum.SALES_RETURN_OUT.getCode());
		jdStockOutDetail.setWarehouseOutboundOrderCode(notice.getEntryOrderId());
		jdStockOutDetail.setPlatformOrderCode("");
		jdStockOutDetail.setSellChannelCode("");
		jdStockOutDetail.setSellCode("");
		jdStockOutDetail.setGoodsOrderCode("");
		jdStockOutDetail.setChannelCode(notice.getChannelCode());
		jdStockOutDetail.setSkuCode(detail.getSkuCode());
		jdStockOutDetail.setSpecInfo(detail.getSpecNatureInfo());
		//jdStockOutDetail.setPrice(detail.getPrice());
		//jdStockOutDetail.setTotalAmount(detail.getTotalAmount());
		jdStockOutDetail.setPlannedQuantity(detail.getOutboundQuantity());
		jdStockOutDetail.setQuantity(actualQty);
		jdStockOutDetail.setOutboundSupplierAmount(detail.getTotalAmount());
		jdStockOutDetail.setWaybillNumber("");
		jdStockOutDetail.setReceiver(notice.getReceiver());
		jdStockOutDetail.setMobile(notice.getReceiverNumber());
		jdStockOutDetail.setAddress(notice.getReceiverProvince() + notice.getReceiverCity() + notice.getReceiverArea() + notice.getReceiverAddress());
		jdStockOutDetail.setTaxRate(detail.getTaxRate());

		jdStockOutDetailService.insert(jdStockOutDetail);

		return notice;
	}

	private void updatePurchaseOutboundStatus(PurchaseOutboundStatusEnum orderStatus, String wmsCode) {
		PurchaseOutboundNotice notice = selectOneByEntryOrderCode(wmsCode);

		Example example = new Example(PurchaseOutboundOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("purchaseOutboundOrderCode", notice.getPurchaseOutboundOrderCode());

		PurchaseOutboundOrder order = new PurchaseOutboundOrder();
		order.setOutboundStatus(orderStatus.getCode());
		purchaseOutboundOrderMapper.updateByExampleSelective(order, example);
	}

	private PurchaseOutboundOrder selectOneOrder(String code) {
		PurchaseOutboundOrder order = new PurchaseOutboundOrder();
		order.setPurchaseOutboundOrderCode(code);
		return purchaseOutboundOrderMapper.selectOne(order);
	}

	private void updateByCode(PurchaseOutboundNoticeStatusEnum status, String exceptionCause, String noticeCode) {

		PurchaseOutboundNotice updateRecord = new PurchaseOutboundNotice();
		Example example = new Example(PurchaseOutboundNotice.class);
		Example.Criteria ca = example.createCriteria();
		ca.andEqualTo("outboundNoticeCode", noticeCode);
		if (StringUtils.isBlank(noticeCode)) {
			throw new IllegalArgumentException("invoking function updateByCode with noticeCode null!");
		}
		updateRecord.setStatus(status.getCode());
		updateRecord.setExceptionCause(exceptionCause);
		noticeMapper.updateByExampleSelective(updateRecord, example);
	}

}
