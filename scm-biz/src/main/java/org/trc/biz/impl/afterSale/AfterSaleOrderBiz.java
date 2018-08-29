package org.trc.biz.impl.afterSale;

import com.google.common.collect.Lists;
import com.qimen.api.response.WarehouseinfoQueryResponse.WarehouseInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.biz.afterSale.IAfterSaleOrderDetailBiz;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.System.SellChannel;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.ShopOrder;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderDetailTypeEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderStatusEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.afterSale.*;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.GuidUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


@Service("afterSaleOrderBiz")
public class AfterSaleOrderBiz implements IAfterSaleOrderBiz{

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

	@Autowired
	private IGoodsBiz goodsBiz;

	@Autowired
	private IAfterSaleOrderDetailBiz afterSaleOrderDetailBiz;
	@Autowired
    private ISellChannelService sellChannelService;

	private static final String AFTER_SALE_ORDER_DETAIL_ID="AFTERD-";
	private static final String AFTER_SALE_ORDER_ID="AFTER-";
	
	@Override
	public List<AfterSaleOrderItemVO> selectAfterSaleInfo(String shopOrderCode) throws Exception{
		//根据订单号查询子订单信息
		OrderItem selectOrderItem = new OrderItem();
		selectOrderItem.setShopOrderCode(shopOrderCode);
		List<OrderItem> orderItemList=orderItemService.select(selectOrderItem);
		AssertUtil.notNull(orderItemList, "没有该订单的数据!");
		
		List<AfterSaleOrderItemVO> afterSaleOrderItemVOList=new ArrayList<>();
		for(OrderItem orderItem:orderItemList) {
			AfterSaleOrderItemVO vo=new AfterSaleOrderItemVO();
			BeanUtils.copyProperties(orderItem, vo);
			//下单的数量-退货数量
			int orderNum=orderItem.getNum();
			int refundNum=getAlreadyRefundNum(orderItem);
			vo.setCanRefundNum(orderNum-refundNum);
			afterSaleOrderItemVOList.add(vo);
		}
		return afterSaleOrderItemVOList;
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
				num=num+afterSaleOrderDetail.getSkuNum();
			}
			
		}
		return num;
	}

	@Override
	public void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo) {
		String shopOrderCode=afterSaleOrderAddDO.getShopOrderCode();
		ShopOrder shopOrderselect=new ShopOrder();
		shopOrderselect.setShopOrderCode(shopOrderCode);
		ShopOrder shopOrder=shopOrderService.selectOne(shopOrderselect);
		AssertUtil.notNull(shopOrder, "根据该订单号查询到的订单为空!");
		
		String afterSaleCode = serialUtilService.generateCode(SupplyConstants.Serial.AFTER_SALE_LENGTH, 
        		SupplyConstants.Serial.AFTER_SALE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		
		AfterSaleOrder afterSaleOrder=new AfterSaleOrder();
		String afterSaleOrderId=GuidUtil.getNextUid(AFTER_SALE_ORDER_ID);
		afterSaleOrder.setId(afterSaleOrderId);
		afterSaleOrder.setAfterSaleCode(afterSaleCode);
		afterSaleOrder.setShopOrderCode(shopOrderCode);
		afterSaleOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleOrder.setPicture(afterSaleOrderAddDO.getPicture());
		afterSaleOrder.setMemo(afterSaleOrderAddDO.getMemo());
		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogistics_corporation_code());
		afterSaleOrder.setLogisticsCorporation(afterSaleOrderAddDO.getLogistics_corporation());
		afterSaleOrder.setExpressNumber(afterSaleOrderAddDO.getExpress_number());
		afterSaleOrder.setWmsCode(afterSaleOrderAddDO.getWms_code());
		afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setCreateOperator(aclUserAccreditInfo.getName());
		afterSaleOrder.setUpdateOperator(aclUserAccreditInfo.getName());
		afterSaleOrder.setUpdateTime(new Date());
		
		List<AfterSaleOrderDetail> details=afterSaleOrderAddDO.getAfterSaleOrderDetailList();
		AssertUtil.notEmpty(details, "售后单子订单为空!");
		List<AfterSaleOrderDetail> detailList=new ArrayList<>();
		for(AfterSaleOrderDetail afterSaleOrderDetailDO:details) {
			String skuCode=afterSaleOrderDetailDO.getSkuCode();
			int skuNum=afterSaleOrderDetailDO.getSkuNum();
			BigDecimal skuMoney=afterSaleOrderDetailDO.getSkuMoney();
			
			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setShopOrderCode(shopOrderCode);
			orderItemSelect.setSkuCode(skuCode);
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			
			AfterSaleOrderDetail afterSaleOrderDetail=new AfterSaleOrderDetail();
			String afterSaleOrderDetailId=GuidUtil.getNextUid(AFTER_SALE_ORDER_DETAIL_ID);
			afterSaleOrderDetail.setId(afterSaleOrderDetailId);
			afterSaleOrderDetail.setShopOrderCode(shopOrderCode);
			afterSaleOrderDetail.setOrderItemCode(orderItem.getOrderItemCode());
			afterSaleOrderDetail.setSkuCode(skuCode);
			afterSaleOrderDetail.setSkuMoney(skuMoney);
			if (orderItem.getSkuCode().startsWith("SP0")) {
				afterSaleOrderDetail.setSkuType(AfterSaleOrderDetailTypeEnum.STATUS_0.getCode());
			}else {
				afterSaleOrderDetail.setSkuType(AfterSaleOrderDetailTypeEnum.STATUS_1.getCode());
			}
			afterSaleOrderDetail.setCreateTime(new Date());
			afterSaleOrderDetail.setUpdateTime(new Date());
			detailList.add(afterSaleOrderDetail);
		}
		
		afterSaleOrderService.insert(afterSaleOrder);
		afterSaleOrderDetailService.insertList(detailList);
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
		//warehouseInfo.set
		return null;
	}

    /**
     * @Description: 售后单分页查询
     * @Author: hzluoxingcheng
     * @Date: 2018/8/29
     */
	@Override
	public Pagenation<AfterSaleOrderVO> afterSaleOrderPage(AfterSaleOrderForm form, Pagenation<AfterSaleOrder> page){
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
		String userName = form.getUserName();
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
			afterSaleOrderDetailForm.setSkuCode(skuCode);
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
			criteria.andEqualTo("waybillNumber",expressNumber);
		}
		//店铺订单编号
		if(StringUtils.isNotBlank(shopOrderCode)){
			criteria.andEqualTo("shopOrderCode",shopOrderCode);
		}
		//售后字表是否经过查询的标记
		if(cildSearchFlag){
			criteria.andIn("shopOrderCode",afterSaleCodeSet);
		}
		//售后单状态
		if(!Objects.equals(null,status)){
			criteria.andEqualTo("status",status);
		}
		//按创建时间倒叙排序
		example.orderBy("createTime").desc();
		afterSaleOrderService.pagination(example, page, form);
		//售后单主表查询结果
		List<AfterSaleOrder> result = page.getResult();
		List<String> shopOrderCodeList = Lists.newArrayList();
		for(AfterSaleOrder afterOrder : result){
			shopOrderCodeList.add(afterOrder.getAfterSaleCode());
		}

        //说明查询没有先查询售后子表
		if(!cildSearchFlag && Objects.equals(null,detailList)){
			AfterSaleOrderDetailForm cldAfterSaleOrderDetailForm = new AfterSaleOrderDetailForm();
			//赋值售后单的编号的列表
			cldAfterSaleOrderDetailForm.setAfterSaleCodeList(shopOrderCodeList);
			detailList = afterSaleOrderDetailBiz.queryListByCondition(cldAfterSaleOrderDetailForm);
		}
		List<AfterSaleOrderVO> newResult = Lists.newArrayList();
		//循环主售后单数据，进行数据组装
		for(AfterSaleOrder asd: result){
//			AfterSaleOrderVO sordvo = new AfterSaleOrderVO();
//			BeanUtils.copyProperties(asd,sordvo);
//			//赋值仓库名称
//			sordvo.setWmsName(wmsMap.get(asd.getWmsCode()));
//			sordvo.setAfterSaleOrderDetailVOList(detVoMap.get(asd.getAfterSaleCode()));
//			newResult.add(sordvo);
		}
		Pagenation<AfterSaleOrderVO> pvo = new Pagenation<AfterSaleOrderVO>();
		//BeanUtils.copyProperties(page,pvo);
		pvo.setResult(newResult);
		return pvo;
	}


	@Override
	public AfterSaleDetailVO queryAfterSaleOrderDetail(Long id) {
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

}
