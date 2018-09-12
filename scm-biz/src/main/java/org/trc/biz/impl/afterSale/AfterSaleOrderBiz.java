package org.trc.biz.impl.afterSale;

import com.google.common.collect.Lists;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.biz.afterSale.IAfterSaleOrderDetailBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.System.SellChannel;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderStatusEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleTypeEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleWarehouseNoticeStatusEnum;
import org.trc.enums.AfterSaleOrderEnum.launchTypeEnum;
import org.trc.enums.AfterSaleOrderEnum.returnSceneEnum;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.form.*;
import org.trc.form.afterSale.*;
import org.trc.form.returnIn.ReturnInDetailWmsResponseForm;
import org.trc.form.returnIn.ReturnInWmsResponseForm;
import org.trc.form.warehouse.ScmReturnInOrderDetail;
import org.trc.form.warehouse.ScmReturnOrderCreateRequest;
import org.trc.service.ITrcService;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeDetailService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeService;
import org.trc.service.category.IBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.outbound.OutboundDetailService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.trc.biz.impl.jingdong.JingDongBizImpl.EXCEL;


@Service("afterSaleOrderBiz")
public class AfterSaleOrderBiz implements IAfterSaleOrderBiz{

	private Logger logger = LoggerFactory.getLogger(AfterSaleOrderBiz.class);

	@Resource
	private IOrderItemService orderItemService;
	@Resource
	private IAfterSaleOrderService afterSaleOrderService;
	@Resource
	private IAfterSaleOrderDetailService afterSaleOrderDetailService;
	@Resource
	private IShopOrderService shopOrderService;
	@Autowired
    private ISerialUtilService serialUtilService;
	@Resource
	private ILogisticsCompanyService logisticsCompanyService;
	@Resource
	private IWarehouseInfoService warehouseInfoService;
	@Resource
	private IPlatformOrderService platformOrderService;
	@Resource
	private IAfterSaleWarehouseNoticeService  afterSaleWarehouseNoticeService;
	@Resource
	private IAfterSaleWarehouseNoticeDetailService  afterSaleWarehouseNoticeDetailService;
	@Resource
	private IItemsService itemsService;
	@Resource
	private IBrandService brandService;
	@Resource
	private IWarehouseOrderService warehouseOrderService;
	@Resource
	private IWarehouseApiService warehouseApiService;
	@Autowired
	private ISkusService skusService;
	@Autowired
	private ILogInfoService logInfoService;

	@Autowired
	private IAfterSaleOrderDetailBiz afterSaleOrderDetailBiz;
	@Autowired
    private ISellChannelService sellChannelService;
	@Autowired
	private ITrcService trcService;
	@Autowired
	private IOutBoundOrderService outBoundOrderService;
	@Autowired
	private IOutboundDetailService outboundDetailService;
	
	private TrcConfig trcConfig;


	private static final String AFTER_SALE_ORDER_DETAIL_ID="AFTERD-";
	private static final String AFTER_SALE_ORDER_ID="AFTERO-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_ID="AFTERW-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID="AFTERN-";

	@Override
	public List<AfterSaleOrderItemVO> selectAfterSaleInfo(String scmShopOrderCode) throws Exception{
		//根据订单号查询子订单信息
		OrderItem selectOrderItem = new OrderItem();
		selectOrderItem.setScmShopOrderCode(scmShopOrderCode);
		List<OrderItem> orderItemList=orderItemService.select(selectOrderItem);
		AssertUtil.notNull(orderItemList, "没有该订单的数据!");
		
		//根据系统订单号查询发货单号
		OutboundOrder selectOutboundOrder=new OutboundOrder();
		selectOutboundOrder.setScmShopOrderCode(scmShopOrderCode);
		OutboundOrder outboundOrder=outBoundOrderService.selectOne(selectOutboundOrder);
		AssertUtil.notNull(outboundOrder, "没有该订单的发货单!");
		String outboundOrderCode=outboundOrder.getOutboundOrderCode();

		List<AfterSaleOrderItemVO> afterSaleOrderItemVOList=new ArrayList<>();
		for(OrderItem orderItem:orderItemList) {
			AfterSaleOrderItemVO vo=new AfterSaleOrderItemVO();
			BeanUtils.copyProperties(orderItem, vo);
			//实际发货的数量-退货数量
			int realSendNum=(int) getRealSendNum(outboundOrderCode,orderItem.getSkuCode());
			//已取消
			if(orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode())) {
				vo.setMaxReturnNum(0);
			}else {
				int refundNum=getAlreadyRefundNum(orderItem);
				vo.setMaxReturnNum(realSendNum-refundNum);
			}
			afterSaleOrderItemVOList.add(vo);
		}
		return afterSaleOrderItemVOList;
	}

	private long getRealSendNum(String outboundOrderCode, String skuCode) {
		OutboundDetail select=new OutboundDetail();
		select.setOutboundOrderCode(outboundOrderCode);
		select.setSkuCode(skuCode);
		OutboundDetail outboundDetail=outboundDetailService.selectOne(select);
		AssertUtil.notNull(outboundDetail, "根据发货单号"+outboundOrderCode+",skuCode"+skuCode+" 查询子发货单为空!");
		return outboundDetail.getRealSentItemNum();
	}

	/**
	 * 订单退货数量
	 * @param orderItem
	 * @return
	 */
	private int getAlreadyRefundNum(OrderItem orderItem) {
		AfterSaleOrderDetail afterSaleOrderDetailSelect=new AfterSaleOrderDetail();
		afterSaleOrderDetailSelect.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleOrderDetailSelect.setSkuCode(orderItem.getSkuCode());

		int num=0;
		List<AfterSaleOrderDetail> afterSaleOrderDetailsList=afterSaleOrderDetailService.select(afterSaleOrderDetailSelect);
		if(afterSaleOrderDetailsList!=null) {
			for(AfterSaleOrderDetail afterSaleOrderDetail:afterSaleOrderDetailsList) {
				int inNum=afterSaleOrderDetail.getInNum()==null?0:afterSaleOrderDetail.getInNum();
				int defectiveInNum=afterSaleOrderDetail.getDefectiveInNum()==null?0:afterSaleOrderDetail.getDefectiveInNum();;
				num=num+inNum+defectiveInNum;
			}

		}
		return num;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo) {
		String scmShopOrderCode=afterSaleOrderAddDO.getScmShopOrderCode();
		ShopOrder shopOrderselect=new ShopOrder();
		shopOrderselect.setScmShopOrderCode(scmShopOrderCode);
		ShopOrder shopOrder=shopOrderService.selectOne(shopOrderselect);
		AssertUtil.notNull(shopOrder, "根据该订单号"+scmShopOrderCode+"查询到的订单为空!");
		
		List<AfterSaleOrderDetail> details=afterSaleOrderAddDO.getAfterSaleOrderDetailList();
		AssertUtil.notEmpty(details, "售后单子订单为空!");
		
		PlatformOrder platformOrderSelect=new PlatformOrder();
		platformOrderSelect.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
		platformOrderSelect.setChannelCode(shopOrder.getChannelCode());
		PlatformOrder platformOrder=platformOrderService.selectOne(platformOrderSelect);
		AssertUtil.notNull(platformOrder, "根据该平台订单编码"+shopOrder.getPlatformOrderCode()+"查询到的平台订单信息为空!");
		
		WarehouseInfo selectWarehouse=new WarehouseInfo();
		AssertUtil.notBlank(afterSaleOrderAddDO.getReturnWarehouseCode(), "仓库编号为空!");
		selectWarehouse.setCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		WarehouseInfo warehouseInfo=warehouseInfoService.selectOne(selectWarehouse);
		AssertUtil.notNull(warehouseInfo,"该仓库编号"+afterSaleOrderAddDO.getReturnWarehouseCode()+"查询到的仓库为空!");

		//查询该订单创建售后单的数量
		int afterSaleNum=getCount(shopOrder);
		//售后单编号
		String afterSaleCode =SupplyConstants.Serial.AFTER_SALE_CODE+"-"+scmShopOrderCode+"-"+(afterSaleNum+1);
        			
		String warehouseNoticeCode = serialUtilService.generateCode(SupplyConstants.Serial.WAREHOUSE_NOTICE_LENGTH,
        		SupplyConstants.Serial.WAREHOUSE_NOTICE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		//售后单
		AfterSaleOrder afterSaleOrder=getAfterSaleOrder(afterSaleCode,shopOrder,afterSaleOrderAddDO,aclUserAccreditInfo,platformOrder,warehouseInfo);
		
		//退货入库单
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=getAfterSaleWarehouseNotice(afterSaleCode,warehouseNoticeCode,shopOrder,afterSaleOrderAddDO,aclUserAccreditInfo,platformOrder,warehouseInfo);
		

		for(AfterSaleOrderDetail afterSaleOrderDetailDO:details) {
			
			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setScmShopOrderCode(scmShopOrderCode);
			AssertUtil.notBlank(afterSaleOrderDetailDO.getSkuCode(), "子订单的sku不能为空!");
			orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			AssertUtil.notNull(orderItem, "更具系统订单号"+scmShopOrderCode+"和sku:"+afterSaleOrderDetailDO.getSkuCode()+"查询子订单为空!");
			//售后单子单
			getAfterSaleOrderDetail(orderItem,afterSaleOrderDetailDO,afterSaleCode);
			//退货入库单子单
			getAfterSaleWarehouseNoticeDetail(orderItem,warehouseNoticeCode,afterSaleOrderDetailDO);
		}

		afterSaleOrderService.insert(afterSaleOrder);
		afterSaleWarehouseNoticeService.insert(afterSaleWarehouseNotice);
		//通知wms，新增退货入库单
		ScmReturnOrderCreateRequest returnOrderCreateRequest=getReturnInOrder(afterSaleCode,warehouseNoticeCode,shopOrder,afterSaleOrderAddDO,aclUserAccreditInfo,platformOrder,warehouseInfo);
		warehouseApiService.returnOrderCreate(returnOrderCreateRequest);
		//通知泰然城退货入库单收货结果
		try{
			createAfterSaleNoticeTrc(afterSaleOrder,details);
		}catch (Exception e){
			logger.error("通知泰然城创建售后单异常", e);
		}
		//日志
		logInfoService.recordLog(new AfterSaleOrder(), afterSaleOrder.getId(),
				aclUserAccreditInfo.getUserId(), "创建", "", null);
	}

	private void createAfterSaleNoticeTrc(AfterSaleOrder afterSaleOrder, List<AfterSaleOrderDetail> details) {
		TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), TrcActionTypeEnum.SUBMIT_ORDER_NOTICE);
		AfterSaleNoticeTrcForm afterSaleOrderForm = (AfterSaleNoticeTrcForm) trcParam;
		AssertUtil.notNull(afterSaleOrder, "售后单信息不能为空!");
		AssertUtil.notBlank(afterSaleOrder.getAfterSaleCode(), "售后单编码不能为空");
		AssertUtil.notBlank(afterSaleOrder.getShopOrderCode(), "店铺订单号不能为空");
		AssertUtil.notBlank(afterSaleOrder.getReturnWarehouseCode(), "仓库编码不能为空");
		AssertUtil.notBlank(afterSaleOrder.getReturnWarehouseName(), "仓库名称不能为空");
		AssertUtil.notEmpty(details, "售后单商品明细不能为空");
		afterSaleOrderForm.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
		afterSaleOrderForm.setShopOrderCode(afterSaleOrder.getShopOrderCode());
		afterSaleOrderForm.setWarehouseCode(afterSaleOrder.getReturnWarehouseCode());
		afterSaleOrderForm.setWarehouseName(afterSaleOrder.getReturnWarehouseName());
		afterSaleOrderForm.setMemo(afterSaleOrder.getMemo() == null ? StringUtils.EMPTY : afterSaleOrder.getMemo());
		afterSaleOrderForm.setLogisticsCorporation(afterSaleOrder.getLogisticsCorporation() == null ? StringUtils.EMPTY : afterSaleOrder.getLogisticsCorporation());
		afterSaleOrderForm.setLogisticsCorporationCode(afterSaleOrder.getLogisticsCorporationCode() == null ? StringUtils.EMPTY : afterSaleOrder.getLogisticsCorporationCode());
		afterSaleOrderForm.setPicture(afterSaleOrder.getPicture() == null ? StringUtils.EMPTY : afterSaleOrder.getPicture());
		List<AfterSaleSkuInfoNoticeTrcForm> skus = new ArrayList<>();
		for (AfterSaleOrderDetail afterSaleOrderDetailDO : details) {
			AfterSaleSkuInfoNoticeTrcForm afterSaleSkuInfoNoticeTrcForm = new AfterSaleSkuInfoNoticeTrcForm();
			afterSaleSkuInfoNoticeTrcForm.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			afterSaleSkuInfoNoticeTrcForm.setOrderItemCode(afterSaleOrderDetailDO.getOrderItemCode());
			afterSaleSkuInfoNoticeTrcForm.setRefundAmont(afterSaleOrderDetailDO.getRefundAmont());
			afterSaleSkuInfoNoticeTrcForm.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
		}
		afterSaleOrderForm.setSkus(skus);
		trcService.createAfterSaleNotice(afterSaleOrderForm);
	}

	private int getCount(ShopOrder shopOrder) {
		AfterSaleOrder select=new AfterSaleOrder();
		select.setShopOrderCode(shopOrder.getShopOrderCode());
		List<AfterSaleOrder> list=afterSaleOrderService.select(select);
		if(list!=null ) {
			return list.size();
		}
		return 0;
	}

	private ScmReturnOrderCreateRequest getReturnInOrder(String afterSaleCode, String warehouseNoticeCode,
			ShopOrder shopOrder, AfterSaleOrderAddDO afterSaleOrderAddDO, AclUserAccreditInfo aclUserAccreditInfo,
			PlatformOrder platformOrder, WarehouseInfo warehouseInfo) {
		ScmReturnOrderCreateRequest returnOrderCreateRequest=new ScmReturnOrderCreateRequest();
		returnOrderCreateRequest.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
		returnOrderCreateRequest.setAfterSaleCode(afterSaleCode);
		returnOrderCreateRequest.setWarehouseNoticeCode(warehouseNoticeCode);
		returnOrderCreateRequest.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		returnOrderCreateRequest.setShopOrderCode(shopOrder.getShopOrderCode());
		returnOrderCreateRequest.setWarehouseCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		returnOrderCreateRequest.setSender(platformOrder.getReceiverName());
		returnOrderCreateRequest.setSenderAddress(platformOrder.getReceiverAddress());
		returnOrderCreateRequest.setSenderCity(platformOrder.getReceiverCity());
		returnOrderCreateRequest.setSenderNumber(platformOrder.getReceiverMobile());
		returnOrderCreateRequest.setSenderProvince(platformOrder.getReceiverProvince());
		returnOrderCreateRequest.setReceiver(warehouseInfo.getWarehouseContact());
		returnOrderCreateRequest.setReceiverAddress(warehouseInfo.getAddress());
		returnOrderCreateRequest.setReceiverCity(warehouseInfo.getCity());
		returnOrderCreateRequest.setReceiverNumber(warehouseInfo.getWarehouseContactNumber());
		returnOrderCreateRequest.setReceiverProvince(warehouseInfo.getProvince());
		returnOrderCreateRequest.setSkuNum(afterSaleOrderAddDO.getAfterSaleOrderDetailList().size());
		returnOrderCreateRequest.setOperator(aclUserAccreditInfo.getName());
		returnOrderCreateRequest.setRemark(afterSaleOrderAddDO.getMemo());
		returnOrderCreateRequest.setChannelCode(shopOrder.getChannelCode());
		returnOrderCreateRequest.setSellCode(shopOrder.getSellCode());
		returnOrderCreateRequest.setShopId(shopOrder.getShopId());
		returnOrderCreateRequest.setShopName(shopOrder.getShopName());
		returnOrderCreateRequest.setLogisticsCorporation(afterSaleOrderAddDO.getLogisticsCorporation());
		returnOrderCreateRequest.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogisticsCorporationCode());
		returnOrderCreateRequest.setWaybillNumber(afterSaleOrderAddDO.getWaybillNumber());
		returnOrderCreateRequest.setReturnScene(returnSceneEnum.STATUS_1.getCode());
		
		List<ScmReturnInOrderDetail> list=new ArrayList<>();
		for(AfterSaleOrderDetail afterSaleOrderDetailDO:afterSaleOrderAddDO.getAfterSaleOrderDetailList()) {
			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
			orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			
			ScmReturnInOrderDetail detail=new ScmReturnInOrderDetail();
			detail.setWarehouseNoticeCode(warehouseNoticeCode);
			detail.setShopOrderCode(shopOrder.getShopOrderCode());
			detail.setOrderItemCode(orderItem.getOrderItemCode());
			detail.setSkuCode(orderItem.getSkuCode());
			detail.setSkuName(orderItem.getItemName());
			detail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
			detail.setBarCode(orderItem.getBarCode());
			detail.setBrandName(getBrandName(orderItem.getSpuCode()));
			detail.setPicture(orderItem.getPicPath());
			detail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
			detail.setMaxReturnNum(afterSaleOrderDetailDO.getMaxReturnNum());
			list.add(detail);
		}
		
		returnOrderCreateRequest.setReturnInOrderDetailList(list);
		return returnOrderCreateRequest;
	}

	private void getAfterSaleWarehouseNoticeDetail(OrderItem orderItem,
			String warehouseNoticeCode,AfterSaleOrderDetail afterSaleOrderDetailDO) {
		
		AfterSaleWarehouseNoticeDetail afterSaleWarehouseNoticeDetail=new AfterSaleWarehouseNoticeDetail();
		String afterSaleWarehouseNoticeDetailId=GuidUtil.getNextUid(AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID);
		afterSaleWarehouseNoticeDetail.setId(afterSaleWarehouseNoticeDetailId);
		afterSaleWarehouseNoticeDetail.setWarehouseNoticeCode(warehouseNoticeCode);
		afterSaleWarehouseNoticeDetail.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleWarehouseNoticeDetail.setOrderItemCode(orderItem.getOrderItemCode());
		afterSaleWarehouseNoticeDetail.setSpuCode(orderItem.getSpuCode());
		afterSaleWarehouseNoticeDetail.setSkuCode(orderItem.getSkuCode());
		afterSaleWarehouseNoticeDetail.setSkuName(orderItem.getItemName());
		afterSaleWarehouseNoticeDetail.setBarCode(orderItem.getBarCode());
		afterSaleWarehouseNoticeDetail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
		afterSaleWarehouseNoticeDetail.setPicture(orderItem.getPicPath());
		afterSaleWarehouseNoticeDetail.setCreateTime(new Date());
		afterSaleWarehouseNoticeDetail.setUpdateTime(new Date());
		afterSaleWarehouseNoticeDetail.setBrandName(getBrandName(orderItem.getSpuCode()));
		afterSaleWarehouseNoticeDetail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
		afterSaleWarehouseNoticeDetailService.insert(afterSaleWarehouseNoticeDetail);
	}

	private void getAfterSaleOrderDetail(OrderItem orderItem,
			AfterSaleOrderDetail afterSaleOrderDetailDO,String afterSaleCode) {
		
		AfterSaleOrderDetail afterSaleOrderDetail=new AfterSaleOrderDetail();
		String afterSaleOrderDetailId=GuidUtil.getNextUid(AFTER_SALE_ORDER_DETAIL_ID);
		afterSaleOrderDetail.setId(afterSaleOrderDetailId);
		afterSaleOrderDetail.setAfterSaleCode(afterSaleCode);
		afterSaleOrderDetail.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleOrderDetail.setScmShopOrderCode(orderItem.getScmShopOrderCode());
		afterSaleOrderDetail.setBrandName(getBrandName(orderItem.getSpuCode()));
		afterSaleOrderDetail.setOrderItemCode(orderItem.getOrderItemCode());
		afterSaleOrderDetail.setSkuCode(orderItem.getSkuCode());
		afterSaleOrderDetail.setSpuCode(orderItem.getSpuCode());
		afterSaleOrderDetail.setSkuName(orderItem.getItemName());
		afterSaleOrderDetail.setBarCode(orderItem.getBarCode());
		afterSaleOrderDetail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
		afterSaleOrderDetail.setNum(orderItem.getNum());
		afterSaleOrderDetail.setMaxReturnNum(afterSaleOrderDetailDO.getMaxReturnNum());
		afterSaleOrderDetail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
		afterSaleOrderDetail.setRefundAmont(afterSaleOrderDetailDO.getRefundAmont());
		afterSaleOrderDetail.setPicture(orderItem.getPicPath());
		WarehouseOrder warehouseOrder=getWarehouseOrder(orderItem.getWarehouseOrderCode());
		afterSaleOrderDetail.setDeliverWarehouseCode(warehouseOrder.getWarehouseCode());
		afterSaleOrderDetail.setDeliverWarehouseName(warehouseOrder.getWarehouseName());
		afterSaleOrderDetail.setCreateTime(new Date());
		afterSaleOrderDetail.setUpdateTime(new Date());
		afterSaleOrderDetailService.insert(afterSaleOrderDetail);
	}

	private WarehouseOrder getWarehouseOrder(String warehouseOrderCode) {
		WarehouseOrder select=new WarehouseOrder();
		select.setWarehouseOrderCode(warehouseOrderCode);
		return warehouseOrderService.selectOne(select);
	}

	/**
	 * 根据spuCode获取brandName
	 */
	private String getBrandName(String spuCode) {
		Items selectItems=new Items();
		selectItems.setSpuCode(spuCode);
		Items items=itemsService.selectOne(selectItems);
		
		Brand selectBrand=new Brand();
		selectBrand.setId(items.getBrandId());
		return brandService.selectOne(selectBrand).getName();
	}

	private AfterSaleWarehouseNotice getAfterSaleWarehouseNotice(String afterSaleCode, String warehouseNoticeCode,ShopOrder shopOrder
			, AfterSaleOrderAddDO afterSaleOrderAddDO, AclUserAccreditInfo aclUserAccreditInfo,PlatformOrder platformOrder,WarehouseInfo warehouseInfo) {
		
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=new AfterSaleWarehouseNotice();
		String afterSaleWarehouseNoticeId=GuidUtil.getNextUid(AFTER_SALE_WAREHOUSE_NOTICE_ID);
		afterSaleWarehouseNotice.setId(afterSaleWarehouseNoticeId);
		afterSaleWarehouseNotice.setWarehouseNoticeCode(warehouseNoticeCode);
		afterSaleWarehouseNotice.setAfterSaleCode(afterSaleCode);
		afterSaleWarehouseNotice.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleWarehouseNotice.setShopOrderCode(shopOrder.getShopOrderCode());
		afterSaleWarehouseNotice.setChannelCode(shopOrder.getChannelCode());
		afterSaleWarehouseNotice.setSellCode(shopOrder.getSellCode());
		afterSaleWarehouseNotice.setShopName(shopOrder.getShopName());
		afterSaleWarehouseNotice.setShopId(shopOrder.getShopId());
		afterSaleWarehouseNotice.setWarehouseName(afterSaleOrderAddDO.getWarehouseName());
		afterSaleWarehouseNotice.setWarehouseCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		afterSaleWarehouseNotice.setSender(platformOrder.getReceiverName());
		afterSaleWarehouseNotice.setSenderAddress(platformOrder.getReceiverAddress());
		afterSaleWarehouseNotice.setSenderCity(platformOrder.getReceiverCity());
		afterSaleWarehouseNotice.setSenderNumber(platformOrder.getReceiverMobile());
		afterSaleWarehouseNotice.setSenderProvince(platformOrder.getReceiverProvince());
		afterSaleWarehouseNotice.setReceiverNumber(warehouseInfo.getWarehouseContactNumber());
		afterSaleWarehouseNotice.setReceiver(warehouseInfo.getWarehouseContact());
		afterSaleWarehouseNotice.setReceiverProvince(warehouseInfo.getProvince());
		afterSaleWarehouseNotice.setReceiverAddress(warehouseInfo.getAddress());
		afterSaleWarehouseNotice.setReceiverCity(warehouseInfo.getCity());
		afterSaleWarehouseNotice.setSkuNum(afterSaleOrderAddDO.getAfterSaleOrderDetailList().size());
		afterSaleWarehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_0.getCode());
		afterSaleWarehouseNotice.setOperator(aclUserAccreditInfo.getName());
		afterSaleWarehouseNotice.setRemark(afterSaleOrderAddDO.getMemo());
		afterSaleWarehouseNotice.setCreateOperator(aclUserAccreditInfo.getUserId());
		afterSaleWarehouseNotice.setCreateTime(new Date());
		afterSaleWarehouseNotice.setLogisticsCorporation(afterSaleOrderAddDO.getLogisticsCorporation());
		afterSaleWarehouseNotice.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogisticsCorporationCode());
		afterSaleWarehouseNotice.setWaybillNumber(afterSaleOrderAddDO.getWaybillNumber());
		afterSaleWarehouseNotice.setReturnScene(returnSceneEnum.STATUS_1.getCode());
		return afterSaleWarehouseNotice;
	}

	private AfterSaleOrder getAfterSaleOrder(String afterSaleCode, ShopOrder shopOrder,AfterSaleOrderAddDO afterSaleOrderAddDO
			, AclUserAccreditInfo aclUserAccreditInfo,PlatformOrder platformOrder,WarehouseInfo warehouseInfo) {
		
		
		AfterSaleOrder afterSaleOrder=new AfterSaleOrder();
		String afterSaleOrderId=GuidUtil.getNextUid(AFTER_SALE_ORDER_ID);
		afterSaleOrder.setId(afterSaleOrderId);
		afterSaleOrder.setAfterSaleCode(afterSaleCode);
		afterSaleOrder.setShopOrderCode(shopOrder.getShopOrderCode());
		afterSaleOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleOrder.setChannelCode(shopOrder.getChannelCode());
		afterSaleOrder.setSellCode(shopOrder.getSellCode());
		afterSaleOrder.setPicture(afterSaleOrderAddDO.getPicture());
		afterSaleOrder.setShopId(shopOrder.getShopId());
		afterSaleOrder.setShopName(shopOrder.getShopName());
		afterSaleOrder.setSender(platformOrder.getReceiverName());
		afterSaleOrder.setSenderAddress(platformOrder.getReceiverAddress());
		afterSaleOrder.setSenderCity(platformOrder.getReceiverCity());
		afterSaleOrder.setSenderNumber(platformOrder.getReceiverMobile());
		afterSaleOrder.setSenderProvince(platformOrder.getReceiverProvince());
		afterSaleOrder.setUserId(platformOrder.getUserId());
		afterSaleOrder.setUserName(platformOrder.getUserName());
		afterSaleOrder.setReceiverProvince(warehouseInfo.getProvince());
		afterSaleOrder.setReceiverCity(warehouseInfo.getCity());
		afterSaleOrder.setReceiverDistrict(warehouseInfo.getArea());
		afterSaleOrder.setReceiverAddress(warehouseInfo.getAddress());
		afterSaleOrder.setReceiverName(warehouseInfo.getWarehouseContact());
		afterSaleOrder.setReceiverPhone(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setReceiverMobile(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setPayTime(shopOrder.getPayTime());
		afterSaleOrder.setReturnWarehouseCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		afterSaleOrder.setReturnAddress(afterSaleOrderAddDO.getReturnAddress());
		afterSaleOrder.setReturnWarehouseName(afterSaleOrderAddDO.getWarehouseName());
		afterSaleOrder.setMemo(afterSaleOrderAddDO.getMemo());
		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogisticsCorporationCode());
		afterSaleOrder.setLogisticsCorporation(afterSaleOrderAddDO.getLogisticsCorporation());
		afterSaleOrder.setWaybillNumber(afterSaleOrderAddDO.getWaybillNumber());
		afterSaleOrder.setAfterSaleType(AfterSaleTypeEnum.STATUS_1.getCode());
		afterSaleOrder.setLaunchType(launchTypeEnum.STATUS_1.getCode());
		if(StringUtils.isNotBlank(afterSaleOrderAddDO.getLogisticsCorporationCode()) && StringUtils.isNotBlank(afterSaleOrderAddDO.getWaybillNumber())) {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_1.getCode());
		}else {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
		}
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
		afterSaleOrder.setUpdateOperator(aclUserAccreditInfo.getUserId());
		afterSaleOrder.setUpdateTime(new Date());
		afterSaleOrder.setReturnScene(returnSceneEnum.STATUS_1.getCode());
		return afterSaleOrder;
	}

	@Override
	public List<LogisticsCompany> selectLogisticsCompany() {
		LogisticsCompany logisticsCompany=new LogisticsCompany();
		logisticsCompany.setIsValid(ValidEnum.VALID.getCode());
		return logisticsCompanyService.select(logisticsCompany);
	}

	@Override
	public List<WarehouseInfo> selectWarehouse() {
		WarehouseInfo warehouseInfo=new WarehouseInfo();
		warehouseInfo.setIsSupportReturn(Integer.parseInt(ValidEnum.VALID.getCode()));
		warehouseInfo.setIsValid(ValidEnum.VALID.getCode());
		return warehouseInfoService.select(warehouseInfo);
	}

    /**
     * @Description: 售后单分页查询
     * @Author: hzluoxingcheng
     * @Date: 2018/8/29
     */
	@Override
	public Pagenation<AfterSaleOrderVO> afterSaleOrderPage(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page,AclUserAccreditInfo aclUserAccreditInfo){
		//创建时间(开始)
		String startTime = form.getStartDate();
		//创建时间（截止）
		String endTime = form.getEndDate();
		//系统订单号
		String scmShopOrderCode = form.getScmShopOrderCode();
		//店铺订单编号(渠道订单号)
		String shopOrderCode = form.getShopOrderCode();
		//售后单编号
		String afterSaleCode = form.getAfterSaleCode();
		//退货仓编号
		String wmsCode = form.getReturnWarehouseCode();
		//物流单号
		String expressNumber = form.getWaybillNumber();
		//客户姓名
		String receiverName = form.getReceiverName();
		//会员名
		//String userName = form.getUserName();
		//客户电话
        String receiverPhone = form.getReceiverPhone();
		//售后单状态
        Integer status = form.getStatus();

		//sku名称
		String skuName = form.getSkuName();
		//skuCode
		String skuCode = form.getSkuCode();
		AfterSaleOrderDetailForm afterSaleOrderDetailForm = new AfterSaleOrderDetailForm();
		List<AfterSaleOrderDetail>  detailList = null;
		//存放售后单字表查询到的售后单号
		Set<String> afterSaleCodeSet = new HashSet<>();
		boolean  cildSearchFlag = false;
		if(StringUtils.isNotBlank(skuName) || StringUtils.isNotBlank(skuCode)){
			afterSaleOrderDetailForm.setSkuName(skuName);
			afterSaleOrderDetailForm.setSkuCode(skuCode);
			detailList = afterSaleOrderDetailBiz.queryListByCondition(afterSaleOrderDetailForm);
			if(Objects.equals(null,detailList) || detailList.isEmpty()){
				//查询条件查询售后单字表未查询到则直接返回
         		return new Pagenation<AfterSaleOrderVO>();
			}
			for(AfterSaleOrderDetail dt: detailList){
				afterSaleCodeSet.add(dt.getAfterSaleCode());
			}
			cildSearchFlag = true;
		}

		Example example = new Example(AfterSaleOrder.class);
		Example.Criteria criteria = example.createCriteria();
		//业务线
		criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());

		//大于等于售后单的创建时间
		if (StringUtils.isNotBlank(startTime)){
			criteria.andGreaterThanOrEqualTo("createTime", startTime + " 00:00:00");
		}
		//小于等于售后单的创建时间
		if (StringUtils.isNotBlank(endTime)){
			criteria.andLessThanOrEqualTo("createTime", endTime+ " 23:59:59");
		}
		//系统订单号
		if(StringUtils.isNotBlank(scmShopOrderCode)){
			criteria.andEqualTo("scmShopOrderCode",scmShopOrderCode);
		}
		//售后单编号（渠道订单编号）
		if(StringUtils.isNotBlank(afterSaleCode)){
			criteria.andEqualTo("afterSaleCode",afterSaleCode);
		}
		//仓库编号
		if(StringUtils.isNotBlank(wmsCode)){
			criteria.andEqualTo("returnWarehouseCode",wmsCode);
		}
		//物流单号(运单号)
		if(StringUtils.isNotBlank(expressNumber)){
			criteria.andLike("waybillNumber","%"+expressNumber+"%");
		}
		//店铺订单编号
		if(StringUtils.isNotBlank(shopOrderCode)){
			criteria.andEqualTo("shopOrderCode",shopOrderCode);
		}
		//售后字表是否经过查询的标记
		if(cildSearchFlag){
			criteria.andIn("afterSaleCode",afterSaleCodeSet);
		}
		//售后单状态
		if(!Objects.equals(null,status) && status!=-1 ){
			criteria.andEqualTo("status",status);
		}
		//客户姓名
		if(StringUtils.isNotBlank(receiverName)){
			criteria.andLike("receiverName","%"+receiverName+"%");
		}
		//客户电话
		if(StringUtils.isNotBlank(receiverPhone)){
			criteria.andLike("receiverPhone","%"+receiverPhone+"%");
		}
		//按创建时间倒叙排序
		example.orderBy("createTime").desc();
		afterSaleOrderService.pagination(example, page, form);
		//售后单主表查询结果
		List<AfterSaleOrder> result = page.getResult();
		if(Objects.equals(null,result) || result.isEmpty()){
			return new Pagenation<AfterSaleOrderVO>();
		}
		List<String> shopOrderCodeList = Lists.newArrayList();
		for(AfterSaleOrder afterOrder : result){
			shopOrderCodeList.add(afterOrder.getShopOrderCode());
		}

        //说明查询没有先查询售后子表
		if(!cildSearchFlag && Objects.equals(null,detailList)){
			AfterSaleOrderDetailForm cldAfterSaleOrderDetailForm = new AfterSaleOrderDetailForm();
			//赋值售后单的编号的列表
			cldAfterSaleOrderDetailForm.setAfterShopOrderCodeList(shopOrderCodeList);
			detailList = afterSaleOrderDetailBiz.queryListByCondition(cldAfterSaleOrderDetailForm);
		}

		//根据所有skucode查询对应spucode
		List<String> skuCodeList = Lists.newArrayList();
		for(AfterSaleOrderDetail od:detailList){
			skuCodeList.add(od.getSkuCode());
		}
		//key是skucode，value是spucode
		Map<String,String> skuSpuMap = new HashMap<>();
		//查询spu列表
		List<Skus> skuModelList = queryItemsBySkuCodes(skuCodeList);
		for(Skus vsklu: skuModelList){
			skuSpuMap.put(vsklu.getSkuCode(),vsklu.getSpuCode());
		}

		//将售后单子表数据进行转换
		List<AfterSaleOrderDetailVO> detailVOList = TransfAfterSaleOrderDetailVO.getAfterSaleOrderDetailVOList(detailList,skuSpuMap);
		List<AfterSaleOrderVO> newResult = Lists.newArrayList();
		//循环主售后单数据，进行数据组装
		for(AfterSaleOrder asd: result){
			//根据仓库编号查询仓库名称
			WarehouseInfo searWarehouseInfo = warehouseInfoService.selectOneByCode(asd.getReturnWarehouseCode());
			SellChannel sellChannel = new SellChannel();
			sellChannel.setSellCode(asd.getSellCode());
			sellChannel = sellChannelService.selectOne(sellChannel);
			AfterSaleOrderVO newvo = TransfAfterSaleOrderVO.getAfterSaleOrderVO(asd,searWarehouseInfo,detailVOList,sellChannel);
			if(!Objects.equals(null,newvo)){
                newResult.add(newvo);
            }
		}
		Pagenation<AfterSaleOrderVO> pvo = new Pagenation<AfterSaleOrderVO>();
		BeanUtils.copyProperties(page,pvo);
		pvo.setResult(newResult);
		return pvo;
	}
    
	/**
	 * @Description: 根据skucode集合查询shangp列表
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/30
	 */ 
	private List<Skus> queryItemsBySkuCodes(List<String> skucodes){
		Example example = new Example(Skus.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("skuCode",skucodes);
		List<Skus> list = skusService.selectByExample(example);
		if(Objects.equals(null,list)){
			return Lists.newArrayList();
		}
		return list;
	}


	/**
	 * @Description: 售后单导出
	 * @Author: hzluoxingcheng
	 * @Date: 2018/8/29
	 */ 
    @Override
	public Response exportAfterSaleOrderVO(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page,AclUserAccreditInfo aclUserAccreditInfo) throws Exception{
			page.setPageSize(3000);
			Pagenation<AfterSaleOrderVO> pvo =  afterSaleOrderPage(form,page,aclUserAccreditInfo);
			List<AfterSaleOrderVO> result = pvo.getResult();
		    List<ExceptorAfterSaleOrder> newResult = Lists.newArrayList();
		    List<ExceptorAfterSaleOrder> newlist =  TransfExportAfterSaleOrder.getExceptorAfterSaleOrder(result);
			//开始导出商品信息
			CellDefinition createTime = new CellDefinition("createTime", "创建时间", CellDefinition.TEXT, null, 4000);
			CellDefinition statusName = new CellDefinition("statusName", "售后单状态", CellDefinition.TEXT, null, 4000);
			CellDefinition scmShopOrderCode = new CellDefinition("scmShopOrderCode", "系统订单号", CellDefinition.TEXT, null, 4000);
			CellDefinition afterSaleCode = new CellDefinition("afterSaleCode", "售后单编号", CellDefinition.TEXT, null, 4000);
			CellDefinition sellCodeName = new CellDefinition("sellCodeName", "销售渠道", CellDefinition.TEXT, null, 4000);
			CellDefinition shopName = new CellDefinition("shopName", "店铺名称", CellDefinition.TEXT, null, 4000);
			CellDefinition skuName = new CellDefinition("skuName", "SKU名称", CellDefinition.TEXT, null, 4000);
			CellDefinition skuCode = new CellDefinition("skuCode", "SKU编号", CellDefinition.TEXT, null, 4000);
			CellDefinition specNatureInfo = new CellDefinition("specNatureInfo", "规格", CellDefinition.TEXT, null, 4000);
			CellDefinition returnNum = new CellDefinition("returnNum", "拟退货数量", CellDefinition.TEXT, null, 4000);
			CellDefinition refundAmont = new CellDefinition("refundAmont", "退款金额", CellDefinition.TEXT, null, 4000);
			CellDefinition logisticsCorporation = new CellDefinition("logisticsCorporation", "物流公司", CellDefinition.TEXT, null, 4000);
		    CellDefinition waybillNumber = new CellDefinition("waybillNumber", "物流单号", CellDefinition.TEXT, null, 4000);
		    CellDefinition returnWarehouseName = new CellDefinition("returnWarehouseName", "退货仓/店", CellDefinition.TEXT, null, 4000);
			CellDefinition deliverWarehouseName = new CellDefinition("deliverWarehouseName", "发货仓/店", CellDefinition.TEXT, null, 4000);

			List<CellDefinition> cellDefinitionList = new LinkedList<>();
			cellDefinitionList.add(createTime);
			cellDefinitionList.add(statusName);
			cellDefinitionList.add(scmShopOrderCode);
			cellDefinitionList.add(afterSaleCode);
			cellDefinitionList.add(sellCodeName);
			cellDefinitionList.add(shopName);
			cellDefinitionList.add(skuName);
		   	cellDefinitionList.add(skuCode);
			cellDefinitionList.add(specNatureInfo);
			cellDefinitionList.add(returnNum);
			cellDefinitionList.add(refundAmont);
			cellDefinitionList.add(logisticsCorporation);
			cellDefinitionList.add(waybillNumber);
			cellDefinitionList.add(returnWarehouseName);
			cellDefinitionList.add(deliverWarehouseName);

		    String sheetName = "售后单数据";
			String fileName = "售后单数据"+ EXCEL;
			try {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(newlist, cellDefinitionList, sheetName);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			hssfWorkbook.write(stream);
			return Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
					.header("Cache-Control", "no-cache").build();

	}

	@Override
	public AfterSaleDetailVO queryAfterSaleOrderDetail(String id) {
		AssertUtil.notNull(id, "查询售后单详情参数id不能为空");
		AfterSaleOrder afterSaleOrder = afterSaleOrderService.selectByPrimaryKey(id);
		AssertUtil.notNull(afterSaleOrder, String.format("根据主键%s查询售后单信息为空", id));
		AfterSaleOrderDetail detail = new AfterSaleOrderDetail();
		detail.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
		List<AfterSaleOrderDetail> detailList = afterSaleOrderDetailService.select(detail);
		AssertUtil.notEmpty(detailList, String.format("根据售后单编码%s查询售后单明细为空", afterSaleOrder.getAfterSaleCode()));

        SellChannel sellChannel = new SellChannel();
        sellChannel.setSellCode(afterSaleOrder.getSellCode());
        sellChannel = sellChannelService.selectOne(sellChannel);
        AssertUtil.notNull(sellChannel, String.format("根据销售渠道编码%s查询销售渠道信息为空", afterSaleOrder.getChannelCode()));
        afterSaleOrder.setSellName(sellChannel.getSellName());

        Set<String> warehouseCodes = new HashSet<>();
        warehouseCodes.add(afterSaleOrder.getReturnWarehouseCode());
        for(AfterSaleOrderDetail detail1: detailList){
            warehouseCodes.add(detail1.getDeliverWarehouseCode());
        }
        Example example = new Example(org.trc.domain.warehouseInfo.WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouseCodes);
        List<org.trc.domain.warehouseInfo.WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        StringBuilder sb = new StringBuilder();
        for(String warehouseCode: warehouseCodes){
            boolean flag = false;
            for(org.trc.domain.warehouseInfo.WarehouseInfo warehouseInfo: warehouseInfoList){
                if(StringUtils.equals(warehouseCode, warehouseInfo.getCode())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                sb.append(warehouseCode).append(SupplyConstants.Symbol.COMMA);
            }
        }
        if(sb.length() > 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("根据仓库编码%s查询仓库信息为空", sb.substring(0, sb.length()-1)));
        }

        for(org.trc.domain.warehouseInfo.WarehouseInfo warehouseInfo: warehouseInfoList){
            if(StringUtils.equals(warehouseInfo.getCode(), afterSaleOrder.getReturnWarehouseCode())){
                afterSaleOrder.setReturnWarehouseName(warehouseInfo.getWarehouseName());
                break;
            }
        }

        for(AfterSaleOrderDetail detail1: detailList){
            for(org.trc.domain.warehouseInfo.WarehouseInfo warehouseInfo: warehouseInfoList){
                if(StringUtils.equals(warehouseInfo.getCode(), detail1.getDeliverWarehouseCode())){
                    detail1.setDeliverWarehouseName(warehouseInfo.getWarehouseName());
                    break;
                }
            }
        }

        AfterSaleDetailVO afterSaleDetailVO = new AfterSaleDetailVO();
        afterSaleDetailVO.setAfterSaleOrder(afterSaleOrder);
        afterSaleDetailVO.setAfterSaleOrderDetailList(detailList);
		return afterSaleDetailVO;
	}

    /**
     * @Description: 检查订单是否可以创建售后单
     * @Author: hzluoxingcheng
     * @Date: 2018/8/30
     */
	@Override
	public boolean checkOrder(String scmShopOrderCode,AclUserAccreditInfo aclUserAccreditInfo) {
		Example example = new Example(ShopOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("scmShopOrderCode", scmShopOrderCode);
		List<String> statusList = Lists.newArrayList();
		//部分发货
		statusList.add(OrderDeliverStatusEnum.PARTS_DELIVER.getCode());
		//全部发货
		statusList.add(OrderDeliverStatusEnum.ALL_DELIVER.getCode());
		criteria.andIn("supplierOrderStatus",statusList);
		//业务线
		criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
		List<ShopOrder>  orderList = shopOrderService.selectByExample(example);

		if(Objects.equals(null,orderList) || orderList.isEmpty()){
			return false;
		}
        return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void returnInOrderResultNotice(ReturnInWmsResponseForm req) {
		AssertUtil.notBlank(req.getAfterSaleCode(), "售后单编码不能为空");
		AssertUtil.notBlank(req.getWarehouseNoticeCode(), "退货入库单编码不能为空");
		AssertUtil.notBlank(req.getOperator(), "操作人不能为空");
		AssertUtil.notEmpty(req.getReturnInDetailWmsResponseFormList(), "退货入库单明细不能为空");
		AfterSaleOrder afterSaleOrder = new AfterSaleOrder();
		afterSaleOrder.setAfterSaleCode(req.getAfterSaleCode());
		afterSaleOrder = afterSaleOrderService.selectOne(afterSaleOrder);
		if(AfterSaleOrderStatusEnum.STATUS_3.getCode() == afterSaleOrder.getStatus()){
			if(logger.isInfoEnabled()){
				logger.info(String.format("根据售后单%s状态已经是已经完成", req.getAfterSaleCode()));
			}
			return;
		}else if(AfterSaleOrderStatusEnum.STATUS_IS_CANCELING.getCode() == afterSaleOrder.getStatus()){
			throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("根据售后单%s状态已经是已经取消", req.getAfterSaleCode()));
		}
		AssertUtil.notNull(afterSaleOrder, String.format("根据售后单号%s查询售后单信息为空", req.getAfterSaleCode()));
		AfterSaleOrderDetail afterSaleOrderDetail = new AfterSaleOrderDetail();
		afterSaleOrderDetail.setAfterSaleCode(req.getAfterSaleCode());
		List<AfterSaleOrderDetail> afterSaleOrderDetailList = afterSaleOrderDetailService.select(afterSaleOrderDetail);
		AssertUtil.notEmpty(afterSaleOrderDetailList, String.format("根据售后单号%s查询售后单商品明细信息为空", req.getAfterSaleCode()));
		for(ReturnInDetailWmsResponseForm responseForm: req.getReturnInDetailWmsResponseFormList()){
			AssertUtil.notBlank(responseForm.getWarehouseNoticeCode(), "退货入库单编码不能为空");
			AssertUtil.notBlank(responseForm.getSkuCode(), "skuCode不能为空");
			for(AfterSaleOrderDetail detail: afterSaleOrderDetailList){
				if(StringUtils.equals(responseForm.getSkuCode(), detail.getSkuCode())){
					int totalInNum = getReturnNum(responseForm.getInNum(), responseForm.getDefectiveInNum());
					AssertUtil.isTrue(detail.getMaxReturnNum().intValue() >= totalInNum, String.format("商品%s的退货入库总量%s大于最大可退数量%s", responseForm.getSkuCode(), totalInNum, detail.getMaxReturnNum()));
				}
			}
		}
		AfterSaleWarehouseNotice warehouseNotice = new AfterSaleWarehouseNotice();
		warehouseNotice.setWarehouseNoticeCode(req.getWarehouseNoticeCode());
		warehouseNotice = afterSaleWarehouseNoticeService.selectOne(warehouseNotice);
		AssertUtil.notNull(warehouseNotice, String.format("根据退货入库单编码%s查询退货入库单信息为空", req.getWarehouseNoticeCode()));
		AfterSaleWarehouseNoticeDetail warehouseNoticeDetail = new AfterSaleWarehouseNoticeDetail();
		warehouseNoticeDetail.setWarehouseNoticeCode(req.getWarehouseNoticeCode());
		List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList = afterSaleWarehouseNoticeDetailService.select(warehouseNoticeDetail);
		AssertUtil.notEmpty(warehouseNoticeDetailList, String.format("根据退货入库单编码%s查询退货入库单明细信息为空", req.getWarehouseNoticeCode()));

		Date currentTime = new Date();
		/**
		 * 更新退货入库单状态及明细
		 */
		int _inNum = 0;
		int _defectiveInNum = 0;
		for(AfterSaleWarehouseNoticeDetail detail: warehouseNoticeDetailList){
			for(ReturnInDetailWmsResponseForm responseForm: req.getReturnInDetailWmsResponseFormList()){
				if(StringUtils.equals(responseForm.getSkuCode(), detail.getSkuCode())){
					detail.setInNum(responseForm.getInNum());
					detail.setDefectiveInNum(responseForm.getDefectiveInNum());
					int totalInNum = getReturnNum(responseForm.getInNum(), responseForm.getDefectiveInNum());
					detail.setTotalInNum(totalInNum);
					if(null != responseForm.getInNum()){
						_inNum = _inNum + responseForm.getInNum().intValue();
					}
					if(null != responseForm.getDefectiveInNum()){
						_defectiveInNum = _defectiveInNum + responseForm.getDefectiveInNum().intValue();
					}
					detail.setUpdateTime(currentTime);
					afterSaleWarehouseNoticeDetailService.updateByPrimaryKey(detail);
				}
			}
		}
		warehouseNotice.setInNum(_inNum);
		warehouseNotice.setDefectiveInNum(_defectiveInNum);
		warehouseNotice.setTotalInNum(_inNum + _defectiveInNum);
		warehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_2.getCode());
		warehouseNotice.setRecordRemark(req.getRecordRemark());
		warehouseNotice.setRecordPic(req.getRecordPicture());
		warehouseNotice.setConfirmRemark(req.getConfirmRemark());
		warehouseNotice.setWarehouseTime(req.getWarehouseTime());
		afterSaleWarehouseNoticeService.updateByPrimaryKey(warehouseNotice);
		/**
		 * 更新售后单状态及明细
		 */
		afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_3.getCode());
		afterSaleOrder.setUpdateTime(currentTime);
		afterSaleOrderService.updateByPrimaryKey(afterSaleOrder);
		for(AfterSaleOrderDetail detail: afterSaleOrderDetailList){
			for(ReturnInDetailWmsResponseForm responseForm: req.getReturnInDetailWmsResponseFormList()){
				if(StringUtils.equals(responseForm.getSkuCode(), detail.getSkuCode())){
					detail.setInNum(responseForm.getInNum());
					detail.setDefectiveInNum(responseForm.getDefectiveInNum());
					detail.setUpdateTime(currentTime);
					afterSaleOrderDetailService.updateByPrimaryKey(detail);
				}
			}
		}
		/**
		 * 记录操作日志
		 */
		StringBuilder sb = new StringBuilder();
		for(ReturnInDetailWmsResponseForm responseForm: req.getReturnInDetailWmsResponseFormList()){
			if(sb.length() > 0){
				sb.append("/ ");
			}
			if(null != responseForm.getInNum() && responseForm.getInNum() > 0){
				sb.append(responseForm.getSkuCode()).append(": 正品入库:").append(responseForm.getInNum());
			}
			if(null != responseForm.getDefectiveInNum() && responseForm.getDefectiveInNum() > 0){
				sb.append(responseForm.getSkuCode()).append(": 残品入库:").append(responseForm.getDefectiveInNum());
			}
		}
		logInfoService.recordLog(afterSaleOrder,afterSaleOrder.getId().toString(), req.getOperator(), LogOperationEnum.AFTER_SALE_ORDER_IN.getMessage(), sb.toString(),null);
		/**
		 * 通知泰然城退货入库单收货结果
		 */
		try{
			returnInResultNoticeChannel(warehouseNotice, warehouseNoticeDetailList);
		}catch (Exception e){
			logger.error("通知泰然城退货入库单收货结果异常", e);
		}

	}

	/**
	 * 通知泰然城退货入库单收货结果
	 * @param afterSaleWarehouseNotice
	 * @param afterSaleWarehouseNoticeDetailList
	 */
	private void returnInResultNoticeChannel(AfterSaleWarehouseNotice afterSaleWarehouseNotice, List<AfterSaleWarehouseNoticeDetail> afterSaleWarehouseNoticeDetailList){
		ReturnInResultNoticeForm noticeForm = new ReturnInResultNoticeForm();
		noticeForm.setAfterSaleCode(afterSaleWarehouseNotice.getAfterSaleCode());
		noticeForm.setShopOrderCode(afterSaleWarehouseNotice.getShopOrderCode());
		noticeForm.setMemo(afterSaleWarehouseNotice.getRecordRemark());
		noticeForm.setRecordPic(afterSaleWarehouseNotice.getRecordPic());
		OrderItem orderItem = new OrderItem();
		orderItem.setScmShopOrderCode(afterSaleWarehouseNotice.getScmShopOrderCode());
		List<OrderItem> orderItemList = orderItemService.select(orderItem);
		AssertUtil.notEmpty(orderItemList, String.format("根据系统订单号%s查询订单商品明细为空", afterSaleWarehouseNotice.getScmShopOrderCode()));
		Map<String, List<OrderItem>> skuMap = new HashMap<>();
		List<ReturnInSkuInfo> skuInfoList = new ArrayList<>();
		for(AfterSaleWarehouseNoticeDetail detail: afterSaleWarehouseNoticeDetailList){
			ReturnInSkuInfo skuInfo = new ReturnInSkuInfo();
			skuInfo.setSkuCode(detail.getSkuCode());
			skuInfo.setSkuName(detail.getSkuName());
			skuInfo.setInNum(detail.getInNum());
			skuInfo.setDefectiveInNum(detail.getDefectiveInNum());
			if(skuMap.containsKey(skuInfo.getSkuCode())){
				continue;
			}
			skuInfoList.add(skuInfo);
			List<OrderItem> tmpList = new ArrayList<>();
			for(OrderItem _orderItem: orderItemList){
				if(StringUtils.equals(skuInfo.getSkuCode(), _orderItem.getSkuCode())){
					tmpList.add(_orderItem);
				}
			}
			skuMap.put(skuInfo.getSkuCode(), tmpList);
		}
		for(ReturnInSkuInfo skuInfo: skuInfoList){
			List<OrderItem> tmpList = skuMap.get(skuInfo.getSkuCode());
			if(tmpList.size() == 0){
				break;
			}
			if(tmpList.size() == 1){
				skuInfo.setOrderItemCode(tmpList.get(0).getOrderItemCode());
			}else if(tmpList.size() > 1){
				/***
				 * 此段逻辑是处理商品和对应的赠品sku一样的商品订单
				 */
				OrderItem tmpOrderItem = null;
				for(OrderItem orderItem2: tmpList){
					boolean flag = false;
					for(ReturnInSkuInfo _skuInfo: skuInfoList){
						if(StringUtils.equals(orderItem2.getSkuCode(), _skuInfo.getSkuCode()) &&
								StringUtils.equals(orderItem2.getOrderItemCode(), _skuInfo.getOrderItemCode())){
							flag = true;
							break;
						}
					}
					if(!flag){
						tmpOrderItem = orderItem2;
					}
				}
				if(null != tmpOrderItem){
					skuInfo.setOrderItemCode(tmpOrderItem.getOrderItemCode());
				}
			}
		}
		noticeForm.setSkus(skuInfoList);
		trcService.sendReturnInResult(noticeForm);
	}




	/**
	 * 获取退货总量
	 * @param inNum
	 * @param defectiveInNum
	 * @return
	 */
	private int getReturnNum(Integer inNum, Integer defectiveInNum){
		int _inNum = 0;
		if(null != inNum){
			_inNum = inNum.intValue();
		}
		int _defectiveInNum = 0;
		if(null != defectiveInNum){
			_defectiveInNum = defectiveInNum.intValue();
		}
		return _inNum + _defectiveInNum;
	}



}
