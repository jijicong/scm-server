package org.trc.biz.impl.warehouseNotice;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.impl.purchase.PurchaseOrderAuditBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.category.Brand;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.PurchaseOrderStatusEnum;
import org.trc.enums.WarehouseNoticeEnum;
import org.trc.enums.WarehouseNoticeStatusEnum;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.service.IQimenService;
import org.trc.service.System.IWarehouseService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;

import com.github.pagehelper.PageHelper;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.EntryorderCreateRequest.EntryOrder;
import com.qimen.api.request.EntryorderCreateRequest.OrderLine;
import com.qimen.api.request.EntryorderCreateRequest.ReceiverInfo;
import com.qimen.api.request.EntryorderCreateRequest.SenderInfo;
import com.qimen.api.response.EntryorderCreateResponse;

import tk.mybatis.mapper.entity.Example;

/**
 * Created by sone on 2017/7/12.
 */
@Service("warehouseNoticeBiz")
public class WarehouseNoticeBiz implements IWarehouseNoticeBiz {

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderAuditBiz.class);
    @Resource
    private IWarehouseNoticeService warehouseNoticeService;
    @Resource
    private ISupplierService iSupplierService;
    @Resource
    private IPurchaseOrderService purchaseOrderService;
    @Resource
    private IWarehouseService warehouseService;
    @Resource
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Resource
    private IPurchaseDetailService purchaseDetailService;
    @Resource
    private IWarehouseNoticeDetailsService warehouseNoticeDetailsService;
    @Resource
    private IPurchaseGroupService purchaseGroupService;
    @Resource
    private ILogInfoService logInfoService;
    @Resource
    private ISkusService skusService;
    @Resource
    private IBrandService brandService;
    @Resource
    private ICategoryService categoryService;
    @Resource
    private IConfigBiz configBiz;
    @Resource
    private IPurchaseOrderBiz purchaseOrderBiz;
    @Resource
    private IQimenService qimenService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, 
    		Pagenation<WarehouseNotice> page,AclUserAccreditInfo aclUserAccreditInfo) {

        AssertUtil.notNull(aclUserAccreditInfo,"查询订单分页中,获得授权信息失败");
        String  channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        AssertUtil.notBlank(channelCode,"未获得授权");
        PageHelper.startPage(page.getPageNo(), page.getPageSize());//--此设置只对如下第一个将要执行的sql起作用
        Map<String, Object> map = new HashMap<>();
        map.put("warehouseNoticeCode",form.getWarehouseNoticeCode());
        map.put("supplierName", form.getSupplierName());
        map.put("status", form.getWarehouseNoticeStatus());
        map.put("purchaseOrderCode", form.getPurchaseOrderCode());
        map.put("purchaseType",form.getPurchaseType());
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
        if(!StringUtils.isBlank(form.getEndDate())){
            Date date = null;
            try {
                date = sdf.parse(form.getEndDate());
            }catch (ParseException e){
                String msg = "入库通知单列表查询,截止日期的格式不正确";
                logger.error(msg);
                throw  new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_QUERY_EXCEPTION,msg);
            }
            date =DateUtils.addDays(date,1);
            form.setEndDate(sdf.format(date));
        }
        map.put("endDate",form.getEndDate());
        map.put("startDate",form.getStartDate());
        map.put("channelCode",channelCode);
        List<WarehouseNotice> pageDateList = warehouseNoticeService.selectWarehouseNoticeList(map);
        if(CollectionUtils.isEmpty(pageDateList)){
            page.setTotalCount(0);
            return page;
        }
        entryHandleUserName(pageDateList);
        _renderPurchaseOrders(pageDateList);
        page.setResult(pageDateList);
        int count = warehouseNoticeService.selectCountWarehouseNotice(map);
        page.setTotalCount(count);
        return page;
    }
    private void entryHandleUserName(List<WarehouseNotice> list) {
        Set<String> userIdsSet = new HashSet<>();
        for (WarehouseNotice warehouseNotice : list) {
            userIdsSet.add(warehouseNotice.getCreateOperator());
        }
        String[] userIdArr = new String[userIdsSet.size()];
        userIdsSet.toArray(userIdArr);
        Map<String, AclUserAccreditInfo> mapTemp = userAccreditInfoService.selectByIds(userIdArr);
        for (WarehouseNotice warehouseNotice : list) {
            if (!StringUtils.isBlank(warehouseNotice.getCreateOperator())) {
                if (mapTemp != null) {
                    AclUserAccreditInfo aclUserAccreditInfo = mapTemp.get(warehouseNotice.getCreateOperator());
                    if (aclUserAccreditInfo != null) {
                        warehouseNotice.setCreateOperator(aclUserAccreditInfo.getName());
                    }
                }
            }
        }
    }
    private void  _renderPurchaseOrders(List<WarehouseNotice> WarehouseNoticeList){

        for(WarehouseNotice  warehouseNotice: WarehouseNoticeList){
            //赋值仓库名称
            Warehouse warehouse = new Warehouse();
            warehouse.setCode(warehouseNotice.getWarehouseCode());
            Warehouse entityWarehouse = warehouseService.selectOne(warehouse);
            warehouseNotice.setWarehouseName(entityWarehouse.getName());
        }

    }
    //设置查询条件
    /*private Example setCriterias(WarehouseNoticeForm trc){
        Example example  = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(trc.getWarehouseNoticeCode())){
            criteria.andLike("warehouseNoticeCode",trc.getWarehouseNoticeCode());
        }
        if(StringUtils.isNotBlank(trc.getPurchaseOrderCode())){
            criteria.andLike("purchaseOrderCode",trc.getPurchaseOrderCode());
        }
        if(StringUtils.isNotBlank(trc.getPurchaseType())){
            criteria.andEqualTo("purchaseType",trc.getPurchaseType());
        }
        if(StringUtils.isNotBlank(trc.getWarehouseNoticeStatus())){
            criteria.andEqualTo("status",trc.getWarehouseNoticeStatus());
        }
        if(StringUtils.isNotBlank(trc.getSupplierName())){
            Example example2  = new Example(WarehouseNotice.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andLike("supplierName",trc.getSupplierName());
            List<Supplier> suppliers = iSupplierService.selectByExample(example);
            if(!CollectionUtils.isEmpty(suppliers)){
                List<String> supplierCodes = new ArrayList<String>();
                for (Supplier supplier:suppliers) {
                    supplierCodes.add(supplier.getSupplierCode());
                }
                criteria.andNotIn("supplierCode",supplierCodes);
            }else{
                return null;
            }
        }
        if (!StringUtils.isBlank(trc.getStartDate())) {
            criteria.andGreaterThan("updateTime", trc.getStartDate());
        }
        if (!StringUtils.isBlank(trc.getEndDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
            Date date = null;
            try {
                date = sdf.parse(trc.getEndDate());
            }catch (ParseException e){
                String msg = "采购订单列表查询,截止日期的格式不正确";
                logger.error(msg);
                throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION,msg);
            }
            date =DateUtils.addDays(date,2);
            trc.setEndDate(sdf.format(date));
            criteria.andLessThan("updateTime", trc.getEndDate());
        }
        return example;
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiptAdviceInfo(WarehouseNotice warehouseNotice, AclUserAccreditInfo aclUserAccreditInfo) {
        //提运单号可能会修改
        AssertUtil.notNull(warehouseNotice,"入库通知单的信息为空!");

        WarehouseNotice entryWarehouseNotice = warehouseNoticeService.selectByPrimaryKey(warehouseNotice.getId());

        if(entryWarehouseNotice.getTakeGoodsNo() == null){
            if(warehouseNotice.getTakeGoodsNo() != null){
                handleTakeGoodsNo(warehouseNotice);
            }
        }
        if(entryWarehouseNotice.getTakeGoodsNo() != null){
            if(warehouseNotice.getTakeGoodsNo() != null){
                if(!warehouseNotice.getTakeGoodsNo().equals(entryWarehouseNotice.getTakeGoodsNo())){
                    handleTakeGoodsNo(warehouseNotice);
                }
            }
            if(warehouseNotice.getTakeGoodsNo() == null){
                handleTakeGoodsNo(warehouseNotice);
            }
        }
        //发送入库通知
        receiptAdvice(warehouseNotice,aclUserAccreditInfo);
    }

    void handleTakeGoodsNo(WarehouseNotice warehouseNotice){
        WarehouseNotice updateWareshouseNotice = new WarehouseNotice();
        updateWareshouseNotice.setId(warehouseNotice.getId());
        updateWareshouseNotice.setTakeGoodsNo(warehouseNotice.getTakeGoodsNo());
        warehouseNoticeService.updateByPrimaryKeySelective(updateWareshouseNotice);//更改入库通知单的提运单号
        //这里没有记录日志
        PurchaseOrder updatePurchaseOrder = new PurchaseOrder();
        updatePurchaseOrder.setTakeGoodsNo(warehouseNotice.getTakeGoodsNo());
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseOrderCode",warehouseNotice.getPurchaseOrderCode());
        purchaseOrderService.updateByExampleSelective(updatePurchaseOrder,example);//更改采购单的提运单号
        //这里没有记录日志
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiptAdvice(WarehouseNotice warehouseNotice,AclUserAccreditInfo aclUserAccreditInfo) {
        /*
        执行通知收货，关联的操作
            1.更改采购单的是否通知入库 为已通知（1）
           2.更改入库通知单的状态 为待仓库反馈（1）
           3.调用仓储的入库通知的接口，给仓库发入库通知单----------（跟产品原型不符，已经迁移到，采购单点击入库通知，既生成入库明细）
                {
                此时，生成入库通知明细
                该入库通知明细，只所以没有在生成scm的入库通知单的时候，生成。
                是因为，如果该入库通知，在没有执行入库通知入库的条件下，是可以被采购单管理页面给（作废掉），这种情况下，生成的入库通知明细是（无用的）
                }
          */
        AssertUtil.notNull(warehouseNotice,"入库通知的信息为空");
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.HAVE_NOTIFIED.getCode());
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseOrderCode",warehouseNotice.getPurchaseOrderCode());
        criteria.andEqualTo("status", PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.selectByExample(example);
        if(CollectionUtils.isEmpty(purchaseOrders)){
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,"查询采购单失败！");
        }
        int count = purchaseOrderService.updateByExampleSelective(purchaseOrder,example);

        if(count != 1){
            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态已作废,无法进行入库通知的操作",warehouseNotice.getPurchaseOrderCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }

        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode",warehouseNotice.getWarehouseNoticeCode());
        warehouseNoticeCriteria.andEqualTo("status",WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
        List<WarehouseNotice> noticeList = warehouseNoticeService.selectByExample(warehouseNoticeExample);
        if (CollectionUtils.isEmpty(noticeList)) { 
        	String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]的状态已不符合修改条件,无法进行入库通知的操作",warehouseNotice.getWarehouseNoticeCode());
        	logger.error(msg);
        	throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }
        // 调用奇门接口，通知仓库创建入口通知单
        entryOrderCreate(warehouseNotice);
        
        /*PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseOrderCode(warehouseNotice.getPurchaseOrderCode());
        List<PurchaseDetail> purchaseDetails = purchaseDetailService.select(purchaseDetail);
        if(CollectionUtils.isEmpty(purchaseDetails)){
            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态没有查到对应的采购商品,请核实该入库明细",warehouseNotice.getPurchaseOrderCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }
        insertWarehouseNoticeDetail(purchaseDetails,warehouseNotice.getWarehouseNoticeCode());*/
        //todo
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(warehouseNotice,warehouseNotice.getId().toString(),userId, 
        		LogOperationEnum.NOTICE_RECEIVE.getMessage(),null,null);
        logInfoService.recordLog(purchaseOrder,purchaseOrders.get(0).getId().toString(),userId, 
        		LogOperationEnum.NOTICE_RECEIVE.getMessage(),null,null);
        purchaseOrderBiz.cacheEvitForPurchaseOrder();

    }


    /**
     * 调用奇门接口，通知仓库创建入口通知单
     * @param warehouseNotice
     */
    private void entryOrderCreate(WarehouseNotice notice) {
    	String noticeCode = notice.getWarehouseNoticeCode(); // 入库通知单号
    	WarehouseNoticeDetails detail = new WarehouseNoticeDetails();
    	detail.setWarehouseNoticeCode(noticeCode);
    	List<WarehouseNoticeDetails> detailsList = warehouseNoticeDetailsService.select(detail);
    	if (!CollectionUtils.isEmpty(detailsList)) {
    		EntryorderCreateRequest req = new EntryorderCreateRequest();
    		/**
    		 * 入库单信息
    		 **/
    		EntryOrder order = new EntryOrder();
    		order.setEntryOrderCode(notice.getWarehouseNoticeCode());
    		order.setOwnerCode(notice.getOwnerCode());
    		order.setPurchaseOrderCode(notice.getPurchaseOrderCode()); //scm采购单编号
    		order.setWarehouseCode(notice.getQimenWarehouseCode());// 奇门仓库编号
    		order.setOrderCreateTime(DateUtils.dateToString(notice.getCreateTime(), DateUtils.DATETIME_FORMAT));
    		order.setOrderType("CGRK"); // 采购入库
    		order.setExpectStartTime(notice.getRequriedReceiveDate());
    		order.setExpectEndTime(notice.getEndReceiveDate());
    		order.setExpressCode(notice.getTakeGoodsNo());
    		order.setSupplierCode(notice.getSupplierCode());
    		order.setSupplierName(notice.getSupplierName());
    		
    		/**
    		 * 发件人信息
    		 **/
    		SenderInfo sender = new SenderInfo();
    		sender.setName(notice.getSender()); //发件人
    		sender.setMobile(notice.getSenderNumber());// 发件方手机
    		sender.setProvince(notice.getSenderProvince());
    		sender.setCity(notice.getSenderCity());
    		sender.setDetailAddress(notice.getSenderAddress());
    		order.setSenderInfo(sender);
    		/**
    		 * 发件人信息
    		 **/
    		ReceiverInfo receiver = new ReceiverInfo();
    		receiver.setName(notice.getReceiver());// 收货人
    		receiver.setMobile(notice.getReceiverNumber());
    		receiver.setProvince(notice.getReceiverProvince());
    		receiver.setCity(notice.getReceiverCity());
    		receiver.setDetailAddress(notice.getReceiverAddress());
    		order.setReceiverInfo(receiver);
    		/**
    		 * 备注
    		 **/
    		order.setRemark(notice.getRemark());
    		
    		/**
    		 * 入库单详情
    		 **/
    		List<OrderLine> lines = new ArrayList<>();
    		for (WarehouseNoticeDetails wnd : detailsList) {
    			OrderLine line = new OrderLine();
    			line.setOwnerCode(wnd.getOwnerCode());
    			line.setItemCode(wnd.getSkuCode());
    			line.setItemId(wnd.getItemId());
    			line.setPlanQty(wnd.getPurchasingQuantity());
    			line.setInventoryType("ZP");
    			line.setProductDate(DateUtils.dateToNormalString(wnd.getProductionDate()));
    			line.setExpireDate(DateUtils.dateToNormalString(wnd.getExpiredDate()));
    			line.setProductCode(wnd.getProductionCode());
    			line.setBatchCode(wnd.getBatchNo());
    			lines.add(line);
    		}
    		req.setEntryOrder(order);
    		req.setOrderLines(lines);
    		String SUCCESS_CODE = "200";
    		AppResult<EntryorderCreateResponse> result = qimenService.entryOrderCreate(req);
    		if (StringUtils.equals(result.getAppcode(), SUCCESS_CODE)) { // 成功
    			/** 
    			 *   仓库入库通知单接收成功
    			 *	 1.更新入库单为待仓库反馈状态
    			 *	 2.更新入库明细表中的商品为待仓库反馈状态
    			 *	 3.更新相应sku的在途库存数
    			 **/
    			postEntryOrderCreate(notice, WarehouseNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode(), true);
    		} else { 
    			// 仓库接收失败
    			postEntryOrderCreate(notice, WarehouseNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode(), false);
    			throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, result.getDatabuffer());
    		}
    		
    	} else {
    		throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION,"商品明细为空!");
    	}
	}
    
    private void postEntryOrderCreate(WarehouseNotice notice, String status, boolean needUpdateStock) {
    	// 更新入库单为待仓库反馈状态
        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode", notice.getWarehouseNoticeCode());
        WarehouseNotice updateNotice = new WarehouseNotice();
        updateNotice.setStatus(status);
        updateNotice.setUpdateTime(Calendar.getInstance().getTime());
        warehouseNoticeService.updateByExampleSelective(updateNotice,warehouseNoticeExample);
        // 更新入库明细表中的商品为待仓库反馈状态
    	Example detailsExample = new Example(WarehouseNoticeDetails.class);
    	Example.Criteria detailsCriteria = detailsExample.createCriteria();
    	detailsCriteria.andEqualTo("warehouseNoticeCode", notice.getWarehouseNoticeCode());
    	WarehouseNoticeDetails details = new WarehouseNoticeDetails();
    	details.setStatus(Integer.parseInt(status));
    	warehouseNoticeDetailsService.updateByExampleSelective(details, detailsExample);
    	// 仓库接收成功时，更新相应sku的在途库存数
    	if (needUpdateStock) {
    		
    	}
    	
    }
    
	@Override
    public WarehouseNotice findfindWarehouseNoticeById(Long id) {

        AssertUtil.notNull(id,"入库通知单的主键id为空,入库通知单查询失败");
        WarehouseNotice warehouseNotice = warehouseNoticeService.selectByPrimaryKey(id);
        AssertUtil.notNull(warehouseNotice,"入库通知单查询失败!");
        //，，采购人名称，到货仓储名称
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(warehouseNotice.getSupplierCode());
        supplier = iSupplierService.selectOne(supplier);
        AssertUtil.notBlank(supplier.getSupplierName(),"供应商名称查询失败");
        warehouseNotice.setSupplierName(supplier.getSupplierName());//供应商名称

        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setCode(warehouseNotice.getPurchaseGroupCode());
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        AssertUtil.notBlank(purchaseGroup.getName(),"采购组名称查询失败");
        warehouseNotice.setPurchaseGroupName(purchaseGroup.getName());//采购组名称

        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setUserId(warehouseNotice.getPurchasePersonId());
        aclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
        AssertUtil.notNull(aclUserAccreditInfo.getName(),"采购人名称查询失败");
        warehouseNotice.setPurchasePersonName(aclUserAccreditInfo.getName());

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(warehouseNotice.getWarehouseCode());
        warehouse = warehouseService.selectOne(warehouse);
        AssertUtil.notNull(warehouse.getName(),"仓库名称查询失败");
        warehouseNotice.setWarehouseName(warehouse.getName());

        //purchaseTypeName

        List<Dict> dicts = configBiz.findDictsByTypeNo(SupplyConstants.SelectList.PURCHASE_TYPE);
        for(Dict dict: dicts){
            if(warehouseNotice.getPurchaseType().equals(dict.getValue())){
                warehouseNotice.setPurchaseTypeName(dict.getName());
            }
        }
        return warehouseNotice;

    }

    @Override
    public List<WarehouseNoticeDetails> warehouseNoticeDetailList(Long warehouseNoticeId) {

        AssertUtil.notNull(warehouseNoticeId,"入库通知的id为空");

        WarehouseNotice warehouseNotice = warehouseNoticeService.selectByPrimaryKey(warehouseNoticeId);

        AssertUtil.notNull(warehouseNotice,"查询入库通知信息为空");

        //根据入库通知的编码，查询所有的入库通知明细

        WarehouseNoticeDetails warehouseNoticeDetails = new WarehouseNoticeDetails();

        warehouseNoticeDetails.setWarehouseNoticeCode(warehouseNotice.getWarehouseNoticeCode());

        List<WarehouseNoticeDetails> warehouseNoticeDetailsList = warehouseNoticeDetailsService.select(warehouseNoticeDetails);

        _renderPurchaseOrder(warehouseNoticeDetailsList);

        return warehouseNoticeDetailsList;

    }
    private void  _renderPurchaseOrder(List<WarehouseNoticeDetails> warehouseNoticeDetailsList){
        //价格转化成元
        for (WarehouseNoticeDetails warehouseNoticeDetails : warehouseNoticeDetailsList) {
            //为品牌名称赋值
            Brand brand = brandService.selectByPrimaryKey(warehouseNoticeDetails.getBrandId());
            AssertUtil.notNull(brand,"查询品牌信息失败!");
            warehouseNoticeDetails.setBrandName(brand.getName());
            //为三级分类赋值
            String allCategoryName = categoryService.selectAllCategoryName(warehouseNoticeDetails.getCategoryId());
            AssertUtil.notBlank(allCategoryName,"获得分类的全路径失败!");
            warehouseNoticeDetails.setAllCategoryName(allCategoryName);
            //价格转化成元
            warehouseNoticeDetails.setPurchasePriceT(new BigDecimal(warehouseNoticeDetails.getPurchasePrice()).divide(new BigDecimal(100)));

        }


    }

}
