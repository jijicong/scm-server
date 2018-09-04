package org.trc.biz.impl.afterSale;

import com.google.common.collect.Lists;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
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
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderStatusEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleWarehouseNoticeStatusEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ShopOrderStatusEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.afterSale.*;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeDetailService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeService;
import org.trc.service.category.IBrandService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.service.util.ISerialUtilService;
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

	@Autowired
	private IGoodsBiz goodsBiz;

	@Autowired
	private ISkusService skusService;

	@Autowired
	private IAfterSaleOrderDetailBiz afterSaleOrderDetailBiz;
	@Autowired
    private ISellChannelService sellChannelService;


	private static final String AFTER_SALE_ORDER_DETAIL_ID="AFTEROD-";
	private static final String AFTER_SALE_ORDER_ID="AFTERO-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_ID="AFTERW-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID="AFTERWN-";

	@Override
	public List<AfterSaleOrderItemVO> selectAfterSaleInfo(String scmShopOrderCode) throws Exception{
		//根据订单号查询子订单信息
		OrderItem selectOrderItem = new OrderItem();
		selectOrderItem.setScmShopOrderCode(scmShopOrderCode);
		List<OrderItem> orderItemList=orderItemService.select(selectOrderItem);
		AssertUtil.notNull(orderItemList, "没有该订单的数据!");

		List<AfterSaleOrderItemVO> afterSaleOrderItemVOList=new ArrayList<>();
		for(OrderItem orderItem:orderItemList) {
			AfterSaleOrderItemVO vo=new AfterSaleOrderItemVO();
			BeanUtils.copyProperties(orderItem, vo);
			//下单的数量-退货数量
			int orderNum=orderItem.getNum();
			int refundNum=getAlreadyRefundNum(orderItem);
			vo.setMaxReturnNum(orderNum-refundNum);
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
				int inNum=afterSaleOrderDetail.getInNum()==null?0:afterSaleOrderDetail.getInNum();
				int defectiveInNum=afterSaleOrderDetail.getDefectiveInNum()==null?0:afterSaleOrderDetail.getDefectiveInNum();;
				num=num+inNum+defectiveInNum;
			}

		}
		return num;
	}

	@Override
	public void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo) {
		String scmShopOrderCode=afterSaleOrderAddDO.getScmShopOrderCode();
		ShopOrder shopOrderselect=new ShopOrder();
		shopOrderselect.setScmShopOrderCode(scmShopOrderCode);
		ShopOrder shopOrder=shopOrderService.selectOne(shopOrderselect);
		AssertUtil.notNull(shopOrder, "根据该订单号"+scmShopOrderCode+"查询到的订单为空!");


		String afterSaleCode = serialUtilService.generateCode(SupplyConstants.Serial.AFTER_SALE_LENGTH,
        		SupplyConstants.Serial.AFTER_SALE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		String warehouseNoticeCode = serialUtilService.generateCode(SupplyConstants.Serial.WAREHOUSE_NOTICE_LENGTH,
        		SupplyConstants.Serial.WAREHOUSE_NOTICE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		//售后单
		AfterSaleOrder afterSaleOrder=getAfterSaleOrder(afterSaleCode,shopOrder,afterSaleOrderAddDO,aclUserAccreditInfo);
		
		//退货入库单
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=getAfterSaleWarehouseNotice(afterSaleCode,warehouseNoticeCode,shopOrder,afterSaleOrderAddDO,aclUserAccreditInfo);
		

		List<AfterSaleOrderDetail> details=afterSaleOrderAddDO.getAfterSaleOrderDetailList();
		AssertUtil.notEmpty(details, "售后单子订单为空!");
		for(AfterSaleOrderDetail afterSaleOrderDetailDO:details) {

			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setScmShopOrderCode(scmShopOrderCode);
			orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			//售后单子单
			getAfterSaleOrderDetail(orderItem,afterSaleOrderDetailDO,afterSaleCode);
			//退货入库单子单
			getAfterSaleWarehouseNoticeDetail(orderItem,warehouseNoticeCode);
		}

		afterSaleOrderService.insert(afterSaleOrder);
		afterSaleWarehouseNoticeService.insert(afterSaleWarehouseNotice);
		
		
	}

	private void getAfterSaleWarehouseNoticeDetail(OrderItem orderItem,
			String warehouseNoticeCode) {
		
		AfterSaleWarehouseNoticeDetail afterSaleWarehouseNoticeDetail=new AfterSaleWarehouseNoticeDetail();
		String afterSaleWarehouseNoticeDetailId=GuidUtil.getNextUid(AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID);
		afterSaleWarehouseNoticeDetail.setId(afterSaleWarehouseNoticeDetailId);
		afterSaleWarehouseNoticeDetail.setWarehouseNoticeCode(warehouseNoticeCode);
		afterSaleWarehouseNoticeDetail.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleWarehouseNoticeDetail.setOrderItemCode(orderItem.getOrderItemCode());
		afterSaleWarehouseNoticeDetail.setSkuCode(orderItem.getSkuCode());
		afterSaleWarehouseNoticeDetail.setSkuName(orderItem.getItemName());
		afterSaleWarehouseNoticeDetail.setBarCode(orderItem.getBarCode());
		afterSaleWarehouseNoticeDetail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
		afterSaleWarehouseNoticeDetail.setPicture(orderItem.getPicPath());
		afterSaleWarehouseNoticeDetail.setCreateTime(new Date());
		afterSaleWarehouseNoticeDetail.setUpdateTime(new Date());
		afterSaleWarehouseNoticeDetail.setBrandName(getBrandName(orderItem.getSpuCode()));
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

	private AfterSaleWarehouseNotice getAfterSaleWarehouseNotice(String afterSaleCode, String warehouseNoticeCode,
			ShopOrder shopOrder, AfterSaleOrderAddDO afterSaleOrderAddDO, AclUserAccreditInfo aclUserAccreditInfo) {
		
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
		WarehouseInfo selectWarehouse=new WarehouseInfo();
		selectWarehouse.setCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		WarehouseInfo warehouseInfo=warehouseInfoService.selectOne(selectWarehouse);
		if(warehouseInfo!=null) {
			afterSaleWarehouseNotice.setReceiverNumber(warehouseInfo.getWarehouseContactNumber());
			afterSaleWarehouseNotice.setReceiver(warehouseInfo.getWarehouseContact());
			afterSaleWarehouseNotice.setReceiverProvince(warehouseInfo.getProvince());
			afterSaleWarehouseNotice.setReceiverAddress(warehouseInfo.getAddress());
			afterSaleWarehouseNotice.setReceiverCity(warehouseInfo.getCity());
		}
		afterSaleWarehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_0.getCode());
		afterSaleWarehouseNotice.setOperator(aclUserAccreditInfo.getName());
		afterSaleWarehouseNotice.setRemark(afterSaleOrderAddDO.getMemo());
		afterSaleWarehouseNotice.setCreateOperator(aclUserAccreditInfo.getUserId());
		afterSaleWarehouseNotice.setCreateTime(new Date());
		return afterSaleWarehouseNotice;
	}

	private AfterSaleOrder getAfterSaleOrder(String afterSaleCode, ShopOrder shopOrder,
			AfterSaleOrderAddDO afterSaleOrderAddDO, AclUserAccreditInfo aclUserAccreditInfo) {
		
		PlatformOrder platformOrderSelect=new PlatformOrder();
		platformOrderSelect.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
		platformOrderSelect.setChannelCode(shopOrder.getChannelCode());
		PlatformOrder platformOrder=platformOrderService.selectOne(platformOrderSelect);
		AssertUtil.notNull(platformOrder, "根据该平台订单编码"+shopOrder.getPlatformOrderCode()+"查询到的平台订单信息为空!");
		
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
		afterSaleOrder.setReceiverProvince(platformOrder.getReceiverProvince());
		afterSaleOrder.setReceiverCity(platformOrder.getReceiverCity());
		afterSaleOrder.setReceiverDistrict(platformOrder.getReceiverDistrict());
		afterSaleOrder.setReceiverAddress(platformOrder.getReceiverAddress());
		afterSaleOrder.setReceiverName(platformOrder.getReceiverName());
		afterSaleOrder.setReceiverIdCard(platformOrder.getReceiverIdCard());
		afterSaleOrder.setReceiverPhone(platformOrder.getReceiverPhone());
		afterSaleOrder.setReceiverEmail(platformOrder.getReceiverEmail());
		afterSaleOrder.setPayTime(shopOrder.getPayTime());
		afterSaleOrder.setReturnWarehouseCode(afterSaleOrderAddDO.getReturnWarehouseCode());
		afterSaleOrder.setReturnAddress(afterSaleOrderAddDO.getReturnAddress());
		afterSaleOrder.setMemo(afterSaleOrderAddDO.getMemo());
		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogisticsCorporationCode());
		afterSaleOrder.setLogisticsCorporation(afterSaleOrderAddDO.getLogisticsCorporation());
		afterSaleOrder.setWaybillNumber(afterSaleOrderAddDO.getWaybillNumber());
		if(StringUtils.isNotBlank(afterSaleOrderAddDO.getLogisticsCorporationCode()) && StringUtils.isNotBlank(afterSaleOrderAddDO.getWaybillNumber())) {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_1.getCode());
		}else {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
		}
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
		afterSaleOrder.setUpdateOperator(aclUserAccreditInfo.getUserId());
		afterSaleOrder.setUpdateTime(new Date());
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
	public boolean checkOrder(String shopOrderCode,AclUserAccreditInfo aclUserAccreditInfo) {
		Example example = new Example(ShopOrder.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("shopOrderCode", shopOrderCode);
		List<String> statusList = Lists.newArrayList();
		//待发货
		statusList.add(ShopOrderStatusEnum.IS_NOT_SEND.getCode());
		//部分发货
		statusList.add(ShopOrderStatusEnum.IS_PART_SEND.getCode());
		//全部发货
		statusList.add(ShopOrderStatusEnum.IS_ALL_SEND.getCode());
		criteria.andIn("supplierOrderStatus",statusList);
		//业务线
		criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
		List<ShopOrder>  orderList = shopOrderService.selectByExample(example);

		if(Objects.equals(null,orderList) || orderList.isEmpty()){
			return false;
		}
//		//根据订单编号查询售后单是否存在
//		AfterSaleOrder safterSaleOrder = new AfterSaleOrder();
//		safterSaleOrder.setShopOrderCode(shopOrderCode);
//		List<AfterSaleOrder> searAfterSaleOrderList = afterSaleOrderService.select(safterSaleOrder);
//        if(Objects.equals(null,searAfterSaleOrder) || ){
//			return true;
//		}
//		//根据订单号查询子订单信息，获取所有skucode
//		OrderItem selectOrderItem = new OrderItem();
//		selectOrderItem.setShopOrderCode(shopOrderCode);
//		List<OrderItem> orderItemList=orderItemService.select(selectOrderItem);
//		AssertUtil.notNull(orderItemList, "没有该订单的数据!");
//
//		//循环获取skucode
//		List<String> skuCodeLidt = Lists.newArrayList();
//		//key是skucode，value是购买数量
//		Map<String,Integer> numMap = new HashMap<>();
//		for(OrderItem it:orderItemList){
//			skuCodeLidt.add(it.getSkuCode());
//			numMap.put(it.getSkuCode(),it.getNum());
//		}
//		//根据skucode集合以及订单编号查询已经创建未取消的售后信息记录
//		Example detailExample = new Example(AfterSaleOrderDetail.class);
//		Example.Criteria detailCriteria = example.createCriteria();
//		detailCriteria.andEqualTo("shopOrderCode",shopOrderCode);
//		detailCriteria.andIn("skuCode",skuCodeLidt);
//		List<AfterSaleOrderDetail> dlist = afterSaleOrderDetailService.selectByExample(detailCriteria);
//		if(Objects.equals(null,dlist) || dlist.isEmpty()){
//			return true;
//		}
//		Map<String,List<AfterSaleOrderDetail>> newMap = new HashMap<>();
//		for(AfterSaleOrderDetail d:dlist){
//			String skuCode = d.getSkuCode();
//			List<AfterSaleOrderDetail> vdlist = newMap.get(skuCode);
//			if(Objects.equals(null,vdlist)){
//				vdlist = Lists.newArrayList();
//			}
//			vdlist.add(d);
//			newMap.put(skuCode,);
//		}
        return true;
	}

}
