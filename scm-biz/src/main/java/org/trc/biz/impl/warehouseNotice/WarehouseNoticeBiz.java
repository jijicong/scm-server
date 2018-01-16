package org.trc.biz.impl.warehouseNotice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.request.EntryorderConfirmRequest;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.EntryorderCreateRequest.EntryOrder;
import com.qimen.api.request.EntryorderCreateRequest.OrderLine;
import com.qimen.api.request.EntryorderCreateRequest.ReceiverInfo;
import com.qimen.api.request.EntryorderCreateRequest.SenderInfo;
import com.qimen.api.response.EntryorderCreateResponse;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.impl.purchase.PurchaseOrderAuditBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.common.RequsetUpdateStock;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.category.Brand;
import org.trc.domain.dict.Dict;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.*;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.service.IQimenService;
import org.trc.service.System.IWarehouseService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.config.IWarehouseNoticeCallbackService;
import org.trc.service.goods.ISkuStockService;
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
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author sone
 * @date 2017/7/12
 */
@Service("warehouseNoticeBiz")
public class WarehouseNoticeBiz implements IWarehouseNoticeBiz {

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderAuditBiz.class);
    @Autowired
    private IWarehouseNoticeService warehouseNoticeService;
    @Autowired
    private ISupplierService iSupplierService;
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @Autowired
    private IWarehouseService warehouseService;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private IPurchaseDetailService purchaseDetailService;
    @Autowired
    private IWarehouseNoticeDetailsService warehouseNoticeDetailsService;
    @Autowired
    private IPurchaseGroupService purchaseGroupService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private ISkusService skusService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IConfigBiz configBiz;
    @Autowired
    private IPurchaseOrderBiz purchaseOrderBiz;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IQimenService qimenService;
    @Autowired
    private IWarehouseNoticeCallbackService warehouseNoticeCallbackService;
    @Autowired
    private RedisLock redisLock;
    private boolean isSection = false;
    private boolean isReceivingError = false;
    private Set<String> defectiveSku;
    private Set<String> errorSku ;

    /**
     * 入库通知单分页查询
     *
     * @param form                form表单查询条件
     * @param page                分页查询的条件
     * @param aclUserAccreditInfo
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, Pagenation<WarehouseNotice> page, AclUserAccreditInfo aclUserAccreditInfo) {

        AssertUtil.notNull(aclUserAccreditInfo, "获取用户信息失败!");
        //获得渠道的编码
        String channelCode = aclUserAccreditInfo.getChannelCode();
        AssertUtil.notBlank(channelCode, "业务线编码为空!");
        Example example = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        //渠道编号
        if (!StringUtils.isBlank(channelCode)) { 
            criteria.andEqualTo("channelCode",channelCode);
        }
        //采购类型
        if (!StringUtils.isBlank(form.getPurchaseType())) {
            criteria.andEqualTo("purchaseType", form.getPurchaseType());
        }
        if (!StringUtils.isBlank(form.getWarehouseNoticeStatus())) {
            criteria.andEqualTo("status", String.valueOf(form.getWarehouseNoticeStatus()));
        }
        //采购单编号
        if (!StringUtils.isBlank(form.getWarehouseNoticeCode())) {
            criteria.andLike("warehouseNoticeCode", "%" + form.getWarehouseNoticeCode() + "%");

        }
        //入库通知单编号
        if (!StringUtils.isBlank(form.getPurchaseOrderCode())) {
            criteria.andLike("purchaseOrderCode", "%" + form.getPurchaseOrderCode() + "%");
        }
        //供应商名称
        if (!StringUtils.isBlank(form.getSupplierName())) {
            List<String> supplierCodeList = getSupplier(form.getSupplierName());
            if (!AssertUtil.collectionIsEmpty(supplierCodeList)) {
                criteria.andIn("supplierCode", supplierCodeList);
            }else {
                criteria.andEqualTo("id", 0);
            }
        }
        //最近更新日期
        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThan("updateTime", form.getStartDate());
        }
        if (!StringUtils.isBlank(form.getEndDate())) {
            criteria.andLessThan("updateTime", DateUtils.formatDateTime(DateUtils.addDays(form.getEndDate(), DateUtils.NORMAL_DATE_FORMAT, 1)));
        }
        example.orderBy("status").asc();
        example.orderBy("updateTime").desc();
        Pagenation<WarehouseNotice> pagenation = warehouseNoticeService.pagination(example, page, form);
        
        // 设置创建者用户名,供应商名，仓库名
        setObjectName(pagenation);


        return page;
    }

    private List<String> getSupplier(String supplierName){
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("supplierName", "%" + supplierName + "%");
        List<Supplier> supplierList = iSupplierService.selectByExample(example);
        List<String> supplierCodeList =  new ArrayList<>();
        if (!AssertUtil.collectionIsEmpty(supplierList)){
            for (Supplier supplier:supplierList ) {
                supplierCodeList.add(supplier.getSupplierCode());
            }
            return  supplierCodeList;
        }
        return  null;
    }

    private void setObjectName(Pagenation<WarehouseNotice> pagenation) {
    	if (null != pagenation && !CollectionUtils.isEmpty(pagenation.getResult())) {
    		for (WarehouseNotice notice : pagenation.getResult()) {
    			/**
    			 * 供应商名称 
    			 * 创建人名称
    			 * 仓库名称
    			 **/
    			AclUserAccreditInfo user = new AclUserAccreditInfo();
    			user.setUserId(notice.getCreateOperator());
    			AclUserAccreditInfo tmpUser = userAccreditInfoService.selectOne(user);
    			AssertUtil.notNull(tmpUser, "创建人名称查询失败");
    			notice.setCreateOperator(tmpUser.getName());
    			
    	        Supplier supplier = new Supplier();
    	        supplier.setSupplierCode(notice.getSupplierCode());
    	        supplier = iSupplierService.selectOne(supplier);
    	        AssertUtil.notNull(supplier, "供应商名称查询失败");
    	        notice.setSupplierName(supplier.getSupplierName());

    	        Warehouse warehouse = new Warehouse();
    	        warehouse.setCode(notice.getWarehouseCode());
    	        warehouse = warehouseService.selectOne(warehouse);
    	        AssertUtil.notNull(warehouse, "仓库名称查询失败");
    	        notice.setWarehouseName(warehouse.getName());
    			
    		}
    	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiptAdviceInfo(WarehouseNotice warehouseNotice, AclUserAccreditInfo aclUserAccreditInfo) {
        //提运单号可能会修改
        AssertUtil.notNull(warehouseNotice, "入库通知单的信息为空!");

        WarehouseNotice entryWarehouseNotice = warehouseNoticeService.selectByPrimaryKey(warehouseNotice.getId());

        if (entryWarehouseNotice.getTakeGoodsNo() == null) {
            if (warehouseNotice.getTakeGoodsNo() != null) {
                handleTakeGoodsNo(warehouseNotice);
            }
        }
        if (entryWarehouseNotice.getTakeGoodsNo() != null) {
            if (warehouseNotice.getTakeGoodsNo() != null) {
                if (!warehouseNotice.getTakeGoodsNo().equals(entryWarehouseNotice.getTakeGoodsNo())) {
                    handleTakeGoodsNo(warehouseNotice);
                }
            }
            if (warehouseNotice.getTakeGoodsNo() == null) {
                handleTakeGoodsNo(warehouseNotice);
            }
        }
        //发送入库通知
        receiptAdvice(warehouseNotice, aclUserAccreditInfo);
    }

    /**
     * 收到入库通知后,更改库存
     *
     * @param requestText
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateInStock(String requestText) {
        EntryorderConfirmRequest confirmRequest;
        XStream xstream = new XStream();
        xstream.alias("request", EntryorderConfirmRequest.class);
        xstream.alias("orderLine", EntryorderConfirmRequest.OrderLine.class);
        xstream.alias("batch", EntryorderConfirmRequest.Batch.class);
        confirmRequest = (EntryorderConfirmRequest) xstream.fromXML(requestText);
        if (null != confirmRequest) {
            //记录流水
            warehouseNoticeCallbackService.recordCallbackLog(confirmRequest.getEntryOrder().getOutBizCode(), requestText, 1, "", confirmRequest.getEntryOrder().getEntryOrderCode());
            //获取orderLines 入库单详情
            List<EntryorderConfirmRequest.OrderLine> orderLineList = confirmRequest.getOrderLines();
            //获取入库单号
            String entryOrderCode = confirmRequest.getEntryOrder().getEntryOrderCode();
            if (StringUtils.isNotBlank(entryOrderCode)) {
                //查询入库通知单
                WarehouseNotice warehouseNotice = new WarehouseNotice();
                warehouseNotice.setWarehouseNoticeCode(entryOrderCode);
                warehouseNotice = warehouseNoticeService.selectOne(warehouseNotice);
                Map<String, List<EntryorderConfirmRequest.OrderLine>> skuMap = new HashMap<>();
                if (null != warehouseNotice) {
                    //获取入库单详细信息
                    if (!AssertUtil.collectionIsEmpty(orderLineList)) {
                        for (EntryorderConfirmRequest.OrderLine orderLine : orderLineList) {
                            skuMap.put(orderLine.getItemCode(), new ArrayList<>());
                        }

                        for (String itemCode : skuMap.keySet()) {
                            List<EntryorderConfirmRequest.OrderLine> skuOrderLineList = new ArrayList<>();
                            for (EntryorderConfirmRequest.OrderLine orderLine : orderLineList) {
                                if (orderLine.getItemCode().equals(itemCode)) {
                                    skuOrderLineList.add(orderLine);
                                }
                            }
                            skuMap.put(itemCode, skuOrderLineList);
                        }
                        errorSku = new HashSet();
                        defectiveSku = new HashSet<>();
                        warehouseNotice.setStatus(getRequestDate(skuMap, warehouseNotice, defectiveSku, errorSku));
                        //获取异常入库sku,信息
                        String failureCause = "";
                        if (!AssertUtil.collectionIsEmpty(defectiveSku)) {
                            //记录残次入库信息
                            failureCause = "SKU[" + StringUtils.join(defectiveSku, SupplyConstants.Symbol.COMMA) + "]存在残品入库。";
                        }
                        if (!AssertUtil.collectionIsEmpty(errorSku)) {
                            //记录入库异常
                            failureCause = failureCause + "SKU[" + StringUtils.join(errorSku, SupplyConstants.Symbol.COMMA) + "]]正品入库数量大于实际采购数量。";
                        }
                        warehouseNotice.setExceptionCause(failureCause);
                        //记录日志
                        if (warehouseNotice.getStatus().equals(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode())) {
                            logInfoService.recordLog(warehouseNotice, String.valueOf(warehouseNotice.getId()), "warehouse", "收货异常", failureCause, null);
                        }
                        if (warehouseNotice.getStatus().equals(WarehouseNoticeStatusEnum.ALL_GOODS.getCode())) {
                            logInfoService.recordLog(warehouseNotice, String.valueOf(warehouseNotice.getId()), "warehouse", "全部收货", "", null);
                        }
                        if (warehouseNotice.getStatus().equals(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode())) {
                            logInfoService.recordLog(warehouseNotice, String.valueOf(warehouseNotice.getId()), "warehouse", "部分收货", "", null);
                        }
                        warehouseNotice.setUpdateTime(Calendar.getInstance().getTime());
                        warehouseNoticeService.updateByPrimaryKeySelective(warehouseNotice);
                    }
                } else {
                    throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_QUERY_EXCEPTION, "未查询到已经通知仓库收货的编号为" + entryOrderCode + "的入库通知单!");
                }
            }
        }
    }

    /**
     * 处理数据
     *
     * @param skuMap
     * @param warehouseNotice
     */
    private String getRequestDate(Map<String, List<EntryorderConfirmRequest.OrderLine>> skuMap, WarehouseNotice warehouseNotice, Set<String> defectiveSku, Set<String> errorSku) {
        List<WarehouseNoticeDetails> warehouseNoticeDetailsList = new ArrayList<>();
        for (String itemCode : skuMap.keySet()) {
            WarehouseNoticeDetails warehouseNoticeDetails = new WarehouseNoticeDetails();
            warehouseNoticeDetails.setSkuCode(itemCode);
            warehouseNoticeDetails.setWarehouseNoticeCode(warehouseNotice.getWarehouseNoticeCode());
            warehouseNoticeDetails = warehouseNoticeDetailsService.selectOne(warehouseNoticeDetails);
            warehouseNotice.setFailureCause(StringUtils.EMPTY);
            if (null==warehouseNoticeDetails){
                String msg = "根据SKU:"+itemCode+",入库通知单编号:"+warehouseNotice.getWarehouseNoticeCode()+".查询入库通知单详情为空";
                warehouseNotice.setFailureCause(msg);
                throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_QUERY_EXCEPTION, msg);
            }

            //残次品入库数量
            Long defectiveQuantity = 0L;
            //正品入库数量
            Long normalQuantity = 0L;
            for (EntryorderConfirmRequest.OrderLine orderLine : skuMap.get(itemCode)) {
                if (AssertUtil.collectionIsEmpty(orderLine.getBatchs())) {
                    normalQuantity = normalQuantity + orderLine.getActualQty();
                } else {
                    List<EntryorderConfirmRequest.Batch> batchList = orderLine.getBatchs();
                    for (EntryorderConfirmRequest.Batch batch:batchList) {
                        if (StringUtils.equals(batch.getInventoryType(),InventoryTypeEnum.ZP.getCode())){
                            normalQuantity = normalQuantity + batch.getActualQty();
                        }else {
                            defectiveQuantity = defectiveQuantity+batch.getActualQty();
                        }
                    }
                }
            }
            //判断收货状态
            if (defectiveQuantity > 0) {
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
                defectiveSku.add(warehouseNoticeDetails.getSkuCode());
            } else if (normalQuantity < warehouseNoticeDetails.getPurchasingQuantity()) {
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode()));
            } else if (normalQuantity > warehouseNoticeDetails.getPurchasingQuantity()) {
                errorSku.add(warehouseNoticeDetails.getSkuCode());
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
            }
            else if (normalQuantity.equals(warehouseNoticeDetails.getPurchasingQuantity())) {
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.ALL_GOODS.getCode()));
            }
            //设置入库通知的详情的入库信息.
            Long warehouseNoticeDetailDefectiveStorageQuantity = warehouseNoticeDetails.getDefectiveStorageQuantity()==null?0:warehouseNoticeDetails.getDefectiveStorageQuantity();
            Long warehouseNoticeDetailNormalStorageQuantity = warehouseNoticeDetails.getNormalStorageQuantity()==null?0:warehouseNoticeDetails.getNormalStorageQuantity();
            warehouseNoticeDetails.setDefectiveStorageQuantity(warehouseNoticeDetailDefectiveStorageQuantity + defectiveQuantity);
            warehouseNoticeDetails.setNormalStorageQuantity(warehouseNoticeDetailNormalStorageQuantity+ normalQuantity);
            warehouseNoticeDetails.setActualStorageQuantity(warehouseNoticeDetails.getNormalStorageQuantity()+warehouseNoticeDetails.getDefectiveStorageQuantity());
            if(warehouseNoticeDetails.getPurchasingQuantity().equals( warehouseNoticeDetails.getActualStorageQuantity())){
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.ALL_GOODS.getCode()));
            }
            if(warehouseNoticeDetails.getPurchasingQuantity()<( warehouseNoticeDetails.getActualStorageQuantity())){
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
                errorSku.add(warehouseNoticeDetails.getSkuCode());
            }
            if((warehouseNoticeDetails.getDefectiveStorageQuantity()==null? 0: warehouseNoticeDetails.getDefectiveStorageQuantity())>0){
                warehouseNoticeDetails.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
                defectiveSku.add(warehouseNoticeDetails.getSkuCode());
            }
            //实际入库时间
            if (normalQuantity > 0 || defectiveQuantity > 0) {
                warehouseNoticeDetails.setStorageTime(Calendar.getInstance().getTime());
            }
            warehouseNoticeDetails.setStorageTime(Calendar.getInstance().getTime());

            warehouseNoticeDetailsList.add(warehouseNoticeDetails);
            //更新采购详情
            warehouseNoticeDetailsService.updateByPrimaryKeySelective(warehouseNoticeDetails);


            //冻结库存表
            String identifier = redisLock.Lock(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + warehouseNoticeDetails.getSkuStockId(), 500, 1000);
            //修改库存
            if (StringUtils.isNotBlank(identifier)){

                SkuStock skuStock = skuStockService.selectByPrimaryKey(warehouseNoticeDetails.getSkuStockId());
                if (null != skuStock) {

                    //更新库存表
                    List<RequsetUpdateStock> stockList = new ArrayList<RequsetUpdateStock>();
                    RequsetUpdateStock stock = new RequsetUpdateStock();
                    Map<String, String> map = new HashMap<String, String>();
                    //真实库存
                    map.put("real_inventory", String.valueOf(normalQuantity));
                    //可用正品库存
//                    map.put("available_inventory", String.valueOf(normalQuantity));
                    //残次品库存
                    map.put("defective_inventory", String.valueOf(defectiveQuantity));
                    //在途库存
                    Long airInventory = skuStock.getAirInventory() - normalQuantity - defectiveQuantity;
                    if (airInventory < 0) {
                        airInventory = skuStock.getAirInventory();
                    } else {
                        airInventory = normalQuantity + defectiveQuantity;
                    }
                    map.put("air_inventory", String.valueOf(0 - airInventory));
                    stock.setChannelCode(warehouseNotice.getChannelCode());
                    stock.setSkuCode(warehouseNoticeDetails.getSkuCode());
                    stock.setWarehouseCode(warehouseNotice.getWarehouseCode());
                    stock.setStockType(map);
                    stockList.add(stock);

                    try {
                        skuStockService.updateSkuStock(stockList);

                    } catch (Exception e) {
                        logger.error("库存更新异常", e);
                    } finally {
                        //释放锁
                        if (redisLock.releaseLock(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + warehouseNoticeDetails.getSkuStockId(), identifier)) {
                            logger.info(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + warehouseNoticeDetails.getSkuStockId() + "已释放！");
                        } else {
                            logger.error(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + warehouseNoticeDetails.getSkuStockId() + "解锁失败！");
                        }
                    }
                }

            }else {
                logger.error("库存ID:"+warehouseNoticeDetails.getSkuStockId()+"未获取到锁！/n真实库存新增："+(normalQuantity + defectiveQuantity)
                +"/n可用正品库新增："+normalQuantity+"/n残次品库存新增："+defectiveQuantity);
            }
        }


        //获取采购单详情状态
        if (!AssertUtil.collectionIsEmpty(warehouseNoticeDetailsList)) {
            for (WarehouseNoticeDetails warehouseNoticeDetails : warehouseNoticeDetailsList) {
                if ((warehouseNoticeDetails.getDefectiveStorageQuantity()==null? 0: warehouseNoticeDetails.getDefectiveStorageQuantity())>0) {
                    isReceivingError = true;
                }
                if (StringUtils.equals(String.valueOf(warehouseNoticeDetails.getStatus()), WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode())) {
                    isSection = true;
                }
            }
        }

        //入库通知单状态设置,直接返回.
        if (isReceivingError) {
            return WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode();
        } else if (isSection) {
            return WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode();
        }
        if (!isReceivingError && !isSection) {
            return WarehouseNoticeStatusEnum.ALL_GOODS.getCode();
        }

        return "";
    }


    private void handleTakeGoodsNo(WarehouseNotice warehouseNotice) {
        WarehouseNotice updateWarehouseNotice = new WarehouseNotice();
        updateWarehouseNotice.setId(warehouseNotice.getId());
        updateWarehouseNotice.setTakeGoodsNo(warehouseNotice.getTakeGoodsNo());
        //更改入库通知单的提运单号
        warehouseNoticeService.updateByPrimaryKeySelective(updateWarehouseNotice);
        //这里没有记录日志
        PurchaseOrder updatePurchaseOrder = new PurchaseOrder();
        updatePurchaseOrder.setTakeGoodsNo(warehouseNotice.getTakeGoodsNo());
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //更改采购单的提运单号
        criteria.andEqualTo("purchaseOrderCode", warehouseNotice.getPurchaseOrderCode());
        purchaseOrderService.updateByExampleSelective(updatePurchaseOrder, example);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiptAdvice(WarehouseNotice warehouseNotice, AclUserAccreditInfo aclUserAccreditInfo) {
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
    	
        AssertUtil.notNull(warehouseNotice, "入库通知的信息为空");
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.HAVE_NOTIFIED.getCode());
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseOrderCode", warehouseNotice.getPurchaseOrderCode());
        criteria.andEqualTo("status", PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
        //criteria.andEqualTo("enterWarehouseNotice", WarehouseNoticeEnum.TO_BE_NOTIFIED.getCode());
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.selectByExample(example);
//        if (CollectionUtils.isEmpty(purchaseOrders)) {
//            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, "查询采购单失败！");
//        }
        // 采购单的状态未置过
        if (!CollectionUtils.isEmpty(purchaseOrders)) {
        	int count = purchaseOrderService.updateByExampleSelective(purchaseOrder,example);
        }

//        if(count != 1){
//            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态已作废,无法进行入库通知的操作",warehouseNotice.getPurchaseOrderCode());
//            logger.error(msg);
//            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
//        }

        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode", warehouseNotice.getWarehouseNoticeCode());
        //warehouseNoticeCriteria.andEqualTo("status", WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
        List<String> statusList = Arrays.asList(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode(),
        		WarehouseNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode());
        warehouseNoticeCriteria.andIn("status", statusList);
        List<WarehouseNotice> noticeList = warehouseNoticeService.selectByExample(warehouseNoticeExample);
        if (CollectionUtils.isEmpty(noticeList)) {
            String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]的状态已不符合入库条件,无法进行入库通知的操作", warehouseNotice.getWarehouseNoticeCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();

        try {
            logInfoService.recordLog(warehouseNotice, warehouseNotice.getId().toString(), userId,
                    LogOperationEnum.NOTICE_RECEIVE.getMessage(), null, null);
        } catch (Exception e) {
            logger.error("通知收货时，操作日志记录异常信息失败：{}", e.getMessage());
            e.printStackTrace();
        }


        WarehouseNotice tempNotice = noticeList.get(0);
        // 调用奇门接口，通知仓库创建入口通知单
        entryOrderCreate(tempNotice, userId);
        
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

//        logInfoService.recordLog(purchaseOrder,purchaseOrders.get(0).getId().toString(),userId, 
//        		LogOperationEnum.NOTICE_RECEIVE.getMessage(),null,null);
         purchaseOrderBiz.cacheEvitForPurchaseOrder();

    }


    /**
     * 调用奇门接口，通知仓库创建入库通知单
     *
     * @param userId
     * @param notice
     */
    private void entryOrderCreate(WarehouseNotice notice, String userId) {
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
                postEntryOrderCreate(notice, detailsList, result, true);
            } else {
                // 仓库接收失败
                try {
                    logInfoService.recordLog(notice, notice.getId().toString(), userId,
                            LogOperationEnum.WMS_RECEIVE_FAILED.getMessage(), result.getDatabuffer(), null);
                } catch (Exception e) {
                    logger.error("仓库接收时，操作日志记录异常信息失败：{}", e.getMessage());
                    e.printStackTrace();
                }

                postEntryOrderCreate(notice, detailsList, result, false);
                //throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, result.getDatabuffer());
            }

        } else {
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, "商品明细为空!");
        }
    }

    /**
     * 创建入库单后处理
     * @param notice      入库单
     * @param detailsList 入库单商品明细列表
     * @param result      external返回结果
     * @param isSuccess   是否创建成功
     */
    private void postEntryOrderCreate(WarehouseNotice notice, List<WarehouseNoticeDetails> detailsList,
                                      AppResult<EntryorderCreateResponse> result, boolean isSuccess) {
        String status = "";
        WarehouseNotice updateNotice = new WarehouseNotice();
        if (isSuccess) {
            EntryorderCreateResponse orderResp = JSONObject.toJavaObject((JSONObject) result.getResult(),
                    EntryorderCreateResponse.class);
            updateNotice.setEntryOrderId(orderResp.getEntryOrderId()); // 成功时接收仓储系统入库单编码

            status = WarehouseNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode();
            // 仓库接收成功时，更新相应sku的在途库存数(channel_code, warehouse_id, sku_code)
            String channelCode = notice.getChannelCode();
            String warehouseCode = notice.getWarehouseCode();
            // 更新在途库存
            String identifier = "";
            for (WarehouseNoticeDetails detail : detailsList) {
            	Long skuStockId = detail.getSkuStockId();
            	try  {
            		identifier = redisLock.Lock(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + skuStockId, 500, 3000);
            		if (StringUtils.isNotBlank(identifier)) {
            			List<RequsetUpdateStock> stockList = new ArrayList<RequsetUpdateStock>();
            			Map<String, String> map = new HashMap<>();
            			RequsetUpdateStock stock = new RequsetUpdateStock();
            			map.put("air_inventory", String.valueOf(detail.getPurchasingQuantity()));
            			stock.setChannelCode(channelCode);
            			stock.setSkuCode(detail.getSkuCode());
            			stock.setWarehouseCode(warehouseCode);
            			stock.setStockType(map);
            			stockList.add(stock);
            			skuStockService.updateSkuStock(stockList);
            			logger.info("skuStockId:{} 入库通知单发送，加锁成功，identifier:{}", skuStockId, identifier);
            		} else {
            			//获取锁失败
            			logger.error("通知单商品:{} 入库通知单发送，获取锁失败，skuStockId:{}，identifier:{}", 
            					JSON.toJSONString(detail), skuStockId, identifier);
            		}
            		
            	} catch (Exception e) {
            		e.printStackTrace();
            		logger.error("通知单商品:{} 发送入库通知单后，更新在途库存失败，skuStockId:{}，identifier:{}, err:{}", 
            				JSON.toJSONString(detail), skuStockId, identifier, e.getMessage());
            	} finally {
            		try {
            			if (redisLock.releaseLock(DistributeLockEnum.WAREHOSE_NOTICE_STOCK.getCode() + skuStockId, identifier)) {
            				logger.info("skuStockId:{} 入库通知单发送，解锁成功，identifier:{}", skuStockId, identifier);
            			} else {
            				logger.error("skuStockId:{} 入库通知单发送，解锁失败，identifier:{}", skuStockId, identifier);
            			}
            			
            		} catch (Exception e) {
            			logger.error("skuStockId:{} 入库通知单发送，解锁失败，identifier:{}, err:{}", 
            					skuStockId, identifier, e.getMessage());
            			e.printStackTrace();
            		}
            	}
            }
            
        } else {
            status = WarehouseNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode();
            updateNotice.setFailureCause(result.getDatabuffer()); // 失败时接收失败原因
        }
        // 更新入库单为 (成功：待仓库反馈状态 ；失败：仓库接收失败)
        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode", notice.getWarehouseNoticeCode());
        updateNotice.setStatus(status);
        updateNotice.setUpdateTime(Calendar.getInstance().getTime());
        warehouseNoticeService.updateByExampleSelective(updateNotice, warehouseNoticeExample);

        // 更新入库明细表中的商品为 (成功：待仓库反馈状态 ；失败：仓库接收失败)
        Example detailsExample = new Example(WarehouseNoticeDetails.class);
        Example.Criteria detailsCriteria = detailsExample.createCriteria();
        detailsCriteria.andEqualTo("warehouseNoticeCode", notice.getWarehouseNoticeCode());
        WarehouseNoticeDetails details = new WarehouseNoticeDetails();
        details.setStatus(Integer.parseInt(status));
        warehouseNoticeDetailsService.updateByExampleSelective(details, detailsExample);
    }

    @Override
    public WarehouseNotice findfindWarehouseNoticeById(Long id) {

        AssertUtil.notNull(id, "入库通知单的主键id为空,入库通知单查询失败");
        WarehouseNotice warehouseNotice = warehouseNoticeService.selectByPrimaryKey(id);
        AssertUtil.notNull(warehouseNotice, "入库通知单查询失败!");
        //，，采购人名称，到货仓储名称
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(warehouseNotice.getSupplierCode());
        supplier = iSupplierService.selectOne(supplier);
        AssertUtil.notBlank(supplier.getSupplierName(), "供应商名称查询失败");
        //供应商名称
        warehouseNotice.setSupplierName(supplier.getSupplierName());

        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setCode(warehouseNotice.getPurchaseGroupCode());
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        //采购组名称
        AssertUtil.notBlank(purchaseGroup.getName(), "采购组名称查询失败");
        warehouseNotice.setPurchaseGroupName(purchaseGroup.getName());

        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setUserId(warehouseNotice.getPurchasePersonId());
        aclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
        AssertUtil.notNull(aclUserAccreditInfo.getName(), "采购人名称查询失败");
        warehouseNotice.setPurchasePersonName(aclUserAccreditInfo.getName());

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(warehouseNotice.getWarehouseCode());
        warehouse = warehouseService.selectOne(warehouse);
        AssertUtil.notNull(warehouse.getName(), "仓库名称查询失败");
        warehouseNotice.setWarehouseName(warehouse.getName());
        List<Dict> dicts = configBiz.findDictsByTypeNo(SupplyConstants.SelectList.PURCHASE_TYPE);
        for (Dict dict : dicts) {
            if (warehouseNotice.getPurchaseType().equals(dict.getValue())) {
                warehouseNotice.setPurchaseTypeName(dict.getName());
            }
        }
        return warehouseNotice;

    }

    @Override
    public List<WarehouseNoticeDetails> warehouseNoticeDetailList(Long warehouseNoticeId) {

        AssertUtil.notNull(warehouseNoticeId, "入库通知的id为空");

        WarehouseNotice warehouseNotice = warehouseNoticeService.selectByPrimaryKey(warehouseNoticeId);

        AssertUtil.notNull(warehouseNotice, "查询入库通知信息为空");

        //根据入库通知的编码，查询所有的入库通知明细

        WarehouseNoticeDetails warehouseNoticeDetails = new WarehouseNoticeDetails();

        warehouseNoticeDetails.setWarehouseNoticeCode(warehouseNotice.getWarehouseNoticeCode());

        List<WarehouseNoticeDetails> warehouseNoticeDetailsList = warehouseNoticeDetailsService.select(warehouseNoticeDetails);

        _renderPurchaseOrder(warehouseNoticeDetailsList);

        return warehouseNoticeDetailsList;

    }

    private void _renderPurchaseOrder(List<WarehouseNoticeDetails> warehouseNoticeDetailsList) {
        //价格转化成元
        for (WarehouseNoticeDetails warehouseNoticeDetails : warehouseNoticeDetailsList) {
            //为品牌名称赋值
            Brand brand = brandService.selectByPrimaryKey(warehouseNoticeDetails.getBrandId());
            AssertUtil.notNull(brand, "查询品牌信息失败!");
            warehouseNoticeDetails.setBrandName(brand.getName());
            //为三级分类赋值
            String allCategoryName = categoryService.selectAllCategoryName(warehouseNoticeDetails.getCategoryId());
            AssertUtil.notBlank(allCategoryName, "获得分类的全路径失败!");
            warehouseNoticeDetails.setAllCategoryName(allCategoryName);
            // 采购总金额，价格转化成元
            //   warehouseNoticeDetails.setPurchasePriceT(new BigDecimal(warehouseNoticeDetails.getPurchaseAmount()).divide(new BigDecimal(100)));

        }

    }

    private String getItemId(EntryorderConfirmRequest.OrderLine orderLine) {
        //ownerCode 货主编码,itemId 仓储系统商品ID 需要转换成本地对应的YWX,itemId
        String itemId = orderLine.getItemId();
        if (!StringUtils.isBlank(itemId)) {
            Warehouse warehouse = new Warehouse();
            warehouse.setQimenWarehouseCode(itemId);
            warehouse = warehouseService.selectOne(warehouse);
            if (null != warehouse) {
                return warehouse.getCode();
            }
        }
        return "";
    }

	public void setQimenService(IQimenService service) {
		this.qimenService = service;
		
	}
}
