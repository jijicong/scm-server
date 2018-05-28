package org.trc.biz.impl.warehouseNotice;


import com.alibaba.fastjson.JSON;
import com.qimen.api.request.EntryorderConfirmRequest;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.common.RequsetUpdateStock;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.dict.Dict;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.*;
import org.trc.exception.WarehouseNoticeDetailException;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.JDWmsConstantConfig;
import org.trc.form.warehouse.*;
import org.trc.form.wms.WmsInNoticeDetailRequest;
import org.trc.form.wms.WmsInNoticeRequest;
import org.trc.service.IQimenService;
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
import org.trc.service.util.IRealIpService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouse.IWarehouseMockService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.*;
import org.trc.util.cache.WarehouseNoticeCacheEvict;
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * @author sone
 * @date 2017/7/12
 */
@Service("warehouseNoticeBiz")
public class WarehouseNoticeBiz implements IWarehouseNoticeBiz {

    private Logger logger = LoggerFactory.getLogger(WarehouseNoticeBiz.class);
    @Autowired
    private IWarehouseNoticeService warehouseNoticeService;
    @Autowired
    private ISupplierService iSupplierService;
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
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
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private IWarehouseExtService warehouseExtService;
    @Autowired
    private IRealIpService iRealIpService;
    @Autowired
    private IWarehouseMockService warehouseMockService;

    @Value("${mock.outer.interface}")
    private String mockOuterInterface;

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
    @Cacheable(value = SupplyConstants.Cache.WAREHOUSE_NOTICE)
    public Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form,
    		Pagenation<WarehouseNotice> page, AclUserAccreditInfo aclUserAccreditInfo) {

        AssertUtil.notNull(aclUserAccreditInfo, "获取用户信息失败!");
        //获得渠道的编码
        String channelCode = aclUserAccreditInfo.getChannelCode();
        AssertUtil.notBlank(channelCode, "业务线编码为空!");
        Example example = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        //仓库反馈入库单号
        if (!StringUtils.isBlank(form.getEntryOrderId())) {
            criteria.andLike("entryOrderId", "%" + form.getEntryOrderId() + "%");
        }
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
                if(null == tmpUser){
                    logger.error(String.format("根据创建人编码%s查询创建人名称信息为空", notice.getCreateOperator()));
                }else {
                    notice.setCreateOperator(tmpUser.getName());
                }
    	        Supplier supplier = new Supplier();
    	        supplier.setSupplierCode(notice.getSupplierCode());
    	        supplier = iSupplierService.selectOne(supplier);
                if(null == supplier){
                    logger.error(String.format("根据供应商编码%s查询供应商信息为空", notice.getSupplierCode()));
                }else {
                    notice.setSupplierName(supplier.getSupplierName());
                }
    	        WarehouseInfo warehouse = new WarehouseInfo();
    	        warehouse.setCode(notice.getWarehouseCode());
    	        warehouse = warehouseInfoService.selectOne(warehouse);
                if(null == warehouse){
                    logger.error(String.format("根据仓库编码%s查询仓库信息为空", notice.getWarehouseCode()));
                }else {
                    notice.setWarehouseName(warehouse.getWarehouseName());
                }
    		}
    	}
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @WarehouseNoticeCacheEvict
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
    @WarehouseNoticeCacheEvict
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
            updateSkuStockTable(warehouseNotice, warehouseNoticeDetails, defectiveQuantity, normalQuantity);

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
    @WarehouseNoticeCacheEvict
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
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.selectByExample(example);
        // 采购单的状态未置过
        if (!CollectionUtils.isEmpty(purchaseOrders)) {
        	purchaseOrderService.updateByExampleSelective(purchaseOrder,example);
        }
    	List<WarehouseNotice> noticeList = new ArrayList<>();
        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode", warehouseNotice.getWarehouseNoticeCode());
        List<String> statusList = Arrays.asList(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode(),
                WarehouseNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode());
        warehouseNoticeCriteria.andIn("status", statusList);
        noticeList = warehouseNoticeService.selectByExample(warehouseNoticeExample);
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
    }


    /**
     * 调用奇门接口，通知仓库创建入库通知单
     *
     * @param userId
     * @param notice
     */
    private void entryOrderCreate(WarehouseNotice notice, String userId) {
        String noticeCode = notice.getWarehouseNoticeCode(); // 入库通知单号
        ScmEntryOrderCreateRequest scmEntryOrderCreateRequest = new ScmEntryOrderCreateRequest();
        WarehouseTypeEnum warehouseTypeEnum = warehouseExtService.getWarehouseType(notice.getWarehouseCode());
        scmEntryOrderCreateRequest.setWarehouseType(warehouseTypeEnum.getCode());
        //入库单信息
        scmEntryOrderCreateRequest.setEntryOrderCode(noticeCode);
        scmEntryOrderCreateRequest.setPurchaseOrderCode(notice.getPurchaseOrderCode());
        scmEntryOrderCreateRequest.setWarehouseCode(warehouseExtService.getWmsWarehouseCode(notice.getWarehouseCode()));
        scmEntryOrderCreateRequest.setOwnerCode(jDWmsConstantConfig.getDeptNo());
        scmEntryOrderCreateRequest.setOrderType(JdPurchaseOrderTypeEnum.B2C.getCode());
        scmEntryOrderCreateRequest.setBillOfLading(notice.getTakeGoodsNo());
        scmEntryOrderCreateRequest.setSupplierCode(jDWmsConstantConfig.getSupplierNo());
        scmEntryOrderCreateRequest.setSupplierName(notice.getSupplierName());
        scmEntryOrderCreateRequest.setOrderCreateTime(notice.getCreateTime());
        scmEntryOrderCreateRequest.setExpectStartTime(DateUtils.parseDateTime(notice.getRequriedReceiveDate()));
        scmEntryOrderCreateRequest.setExpectEndTime(DateUtils.parseDateTime(notice.getEndReceiveDate()));
        scmEntryOrderCreateRequest.setRemark(notice.getRemark());
        //发货人信息
        scmEntryOrderCreateRequest.setSenderName(notice.getSender());
        scmEntryOrderCreateRequest.setSenderMobile(notice.getSenderNumber());
        scmEntryOrderCreateRequest.setSenderProvince(notice.getSenderProvince());
        scmEntryOrderCreateRequest.setSenderCity(notice.getSenderCity());
        scmEntryOrderCreateRequest.setSenderDetailAddress(notice.getSenderAddress());
        //收货人信息
        scmEntryOrderCreateRequest.setReciverName(notice.getReceiver());
        scmEntryOrderCreateRequest.setReciverProvince(notice.getReceiverProvince());
        scmEntryOrderCreateRequest.setReciverCity(notice.getReceiverCity());
        scmEntryOrderCreateRequest.setReciverDetailAddress(notice.getReceiverAddress());
        scmEntryOrderCreateRequest.setReciverMobile(notice.getReceiverNumber());

        //入库单明细
        List<ScmEntryOrderItem> scmEntryOrderItemList = new ArrayList<>();
        WarehouseNoticeDetails detail = new WarehouseNoticeDetails();
        detail.setWarehouseNoticeCode(noticeCode);
        List<WarehouseNoticeDetails> detailsList = warehouseNoticeDetailsService.select(detail);
        AssertUtil.notEmpty(detailsList, String.format("发货通知单%s相关商品明细为空", noticeCode));
        for (WarehouseNoticeDetails details : detailsList) {
            ScmEntryOrderItem scmEntryOrderItem = new ScmEntryOrderItem();
            scmEntryOrderItem.setOwnerCode(details.getOwnerCode());
            scmEntryOrderItem.setItemCode(details.getSkuCode());
            scmEntryOrderItem.setItemId(details.getItemId());
            scmEntryOrderItem.setGoodsStatus(EntryOrderDetailItemStateEnum.QUALITY_PRODUCTS.getCode());
            scmEntryOrderItem.setPlanQty(details.getPurchasingQuantity()); // 采购数量
            scmEntryOrderItemList.add(scmEntryOrderItem);
        }
        scmEntryOrderCreateRequest.setEntryOrderItemList(scmEntryOrderItemList);
        AppResult<String> appResult = warehouseApiService.entryOrderCreate(scmEntryOrderCreateRequest);
        if (StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)) { // 成功
            /**
             *   仓库入库通知单接收成功
             *   1.更新入库单为待仓库反馈状态
             *   2.更新入库明细表中的商品为待仓库反馈状态
             *   3.更新相应sku的在途库存数
             **/
            postEntryOrderCreate(notice, detailsList, appResult, true);
        } else {
            // 仓库接收失败
            try {
                logInfoService.recordLog(notice, notice.getId().toString(), userId,
                        LogOperationEnum.WMS_RECEIVE_FAILED.getMessage(), appResult.getDatabuffer(), null);
            } catch (Exception e) {
                logger.error("仓库接收时，操作日志记录异常信息失败：{}", e.getMessage());
                e.printStackTrace();
            }

            postEntryOrderCreate(notice, detailsList, appResult, false);
            //throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_EXCEPTION, result.getDatabuffer());
        }

    }

    /**
     * 创建入库单后处理
     * @param notice      入库单
     * @param detailsList 入库单商品明细列表
     * @param isSuccess   是否创建成功
     */
    private void postEntryOrderCreate(WarehouseNotice notice, List<WarehouseNoticeDetails> detailsList,
                                      AppResult<String> result, boolean isSuccess) {
        String status = "";
        WarehouseNotice updateNotice = new WarehouseNotice();
        if (isSuccess) {
            updateNotice.setEntryOrderId(result.getResult().toString()); // 成功时接收仓储系统入库单编码
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
            			//stock.setChannelCode(channelCode);
            			stock.setChannelCode("TRMALL");
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
    @Cacheable(value = SupplyConstants.Cache.WAREHOUSE_NOTICE)
    public WarehouseNotice findfindWarehouseNoticeById(Long id)
    {

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

        WarehouseInfo warehouse = new WarehouseInfo();
        warehouse.setCode(warehouseNotice.getWarehouseCode());
        warehouse = warehouseInfoService.selectOne(warehouse);
        AssertUtil.notNull(warehouse, String.format("根据仓库编码%s查询仓库信息为空", warehouseNotice.getWarehouseCode()));
        warehouseNotice.setWarehouseName(warehouse.getWarehouseName());
        List<Dict> dicts = configBiz.findDictsByTypeNo(SupplyConstants.SelectList.PURCHASE_TYPE);
        for (Dict dict : dicts) {
            if (warehouseNotice.getPurchaseType().equals(dict.getValue())) {
                warehouseNotice.setPurchaseTypeName(dict.getName());
            }
        }
        return warehouseNotice;

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.WAREHOUSE_NOTICE)
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
            WarehouseInfo warehouse = new WarehouseInfo();
            warehouse.setWmsWarehouseCode(itemId);
            warehouse = warehouseInfoService.selectOne(warehouse);
            if (null != warehouse) {
                return warehouse.getCode();
            }
        }
        return "";
    }

	public void setQimenService(IQimenService service) {
		this.qimenService = service;
		
	}
    /**
     * 定时任务调用接口，更新库存信息
     * 1.查询入库通知单，状态为收货异常，待仓库反馈，部分收货的入库单
     * 2.分隔list，接口支持10个wms_order_code批量查询
     * 3.分线程处理库存，以及入库信息
     */
    @Override
    @WarehouseNoticeCacheEvict
    public void updateStock() {
        if (!iRealIpService.isRealTimerService()){
            return;
        }
        //1. 查询入库通知单，状态为待仓库反馈，部分收货的入库单,完成状态为未完成的
        // 更新入库单为 (成功：待仓库反馈状态 ；失败：仓库接收失败)
        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        List<String> stateArray = new ArrayList<>();
        //添加需要查询的状态
        stateArray.add(WarehouseNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode());
//        stateArray.add(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode());
//        stateArray.add(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode());
        warehouseNoticeCriteria.andIn("status",stateArray);
        //数据中未完成的入库通知单
        warehouseNoticeCriteria.andEqualTo("finishStatus",WarehouseNoticeFinishStatusEnum.UNFINISHED.getCode());
//        warehouseNoticeCriteria.andEqualTo("entryOrderId","EPL4418047973168");
        List<WarehouseNotice> warehouseNoticeList = warehouseNoticeService.selectByExample(warehouseNoticeExample);
        if (!AssertUtil.collectionIsEmpty(warehouseNoticeList)){
            //接口支持一次查询十个单号查询，需要分割符合条件的入库单
            List<List<WarehouseNotice>> splitWarehouseNoticeList = ListSplit.split(warehouseNoticeList,10);
            //分批调用接口
            for (List<WarehouseNotice> noticeList:splitWarehouseNoticeList) {
                scmEntryOrder(noticeList);
            }
        }else {
            logger.info("未查询到符合条件的入库通知单！");
        }
    }

    @Override
    public Response inFinishCallBack(WmsInNoticeRequest req) {
     //TODO   入库通知回调
        AssertUtil.notNull(req, "采购入库回调信息不能为空");
        String noticeCode = req.getWarehouseNoticeCode();
        //获取采购入库详情明细
        WarehouseNoticeDetails noticeDetail = new WarehouseNoticeDetails();
        noticeDetail.setWarehouseNoticeCode(noticeCode);
        List<WarehouseNoticeDetails> details = warehouseNoticeDetailsService.select(noticeDetail);

        List<WmsInNoticeDetailRequest> inNoticeDetailRequests = req.getInNoticeDetailRequests();
        if(inNoticeDetailRequests!=null && inNoticeDetailRequests.size()>0){
            for (WmsInNoticeDetailRequest inNoticeDetailRequest : inNoticeDetailRequests) {
                for (WarehouseNoticeDetails detail : details) {
                    if(StringUtils.equals(inNoticeDetailRequest.getSkuCode(),detail.getSkuCode())){
                        detail.setNormalStorageQuantity(inNoticeDetailRequest.getNormalStorageQuantity());
                        detail.setDefectiveStorageQuantity(inNoticeDetailRequest.getDefectiveStorageQuantity());
                        Long actualStorageQuantity=inNoticeDetailRequest.getNormalStorageQuantity()+inNoticeDetailRequest.getDefectiveStorageQuantity();
                        detail.setActualStorageQuantity(actualStorageQuantity);
                        detail.setActualInstockTime(inNoticeDetailRequest.getActualInstockTime());
                    }


                }
            }
        }



        return null;
    }

    private void scmEntryOrder(List<WarehouseNotice> noticeList) {
        //1.组装wms_order_code
        List<String> wmsOrderCodeList = new ArrayList<>();
        for (WarehouseNotice warehouseNotice : noticeList) {
            wmsOrderCodeList.add(warehouseNotice.getEntryOrderId());
        }
        ScmEntryOrderDetailRequest entryOrderDetailRequest = new ScmEntryOrderDetailRequest();
        entryOrderDetailRequest.setEntryOrderCode(StringUtils.join(wmsOrderCodeList, SupplyConstants.Symbol.COMMA));
        AppResult appResult = null;
        if(StringUtils.equals(mockOuterInterface, ZeroToNineEnum.ONE.getCode())){//仓库接口mock
            appResult = warehouseMockService.entryOrderDetail(entryOrderDetailRequest);
        }else{
            appResult = warehouseApiService.entryOrderDetail(entryOrderDetailRequest);
        }
        List<ScmEntryOrderDetailResponse> scmEntryOrderDetailResponseListRequest = (List<ScmEntryOrderDetailResponse>) appResult.getResult();
        for (WarehouseNotice warehouseNotice : noticeList) {
            //获取当前入库单对应的入库查询结果
            List<ScmEntryOrderDetailResponse> scmEntryOrderDetailResponseList = new ArrayList<>();
            for (ScmEntryOrderDetailResponse entryOrderDetail : scmEntryOrderDetailResponseListRequest) {
                if (StringUtils.equals(entryOrderDetail.getPoOrderNo(),warehouseNotice.getEntryOrderId())){
                    scmEntryOrderDetailResponseList.add(entryOrderDetail);
                }
            }
            //处理库存信息
            if (!AssertUtil.collectionIsEmpty(scmEntryOrderDetailResponseList)) {
                for (ScmEntryOrderDetailResponse entryOrderDetail : scmEntryOrderDetailResponseList) {
                    if (!StringUtils.equals(entryOrderDetail.getStatus(), "70")) {
                        //如果不是70的完成状态,当前采购单入库详情就跳过处理
                        continue;
                    }
                    /* 定位到入库通知单 */
                    WarehouseNotice noticeOrder = new WarehouseNotice();
                    noticeOrder.setEntryOrderId(entryOrderDetail.getPoOrderNo());
                    noticeOrder = warehouseNoticeService.selectOne(noticeOrder);
                    //查询到入库通知单,查询到关联的入库通知单详情
                    if (null != noticeOrder) {
                        //记录部分收货的通知单详情
                        List<WarehouseNoticeDetails> partialNoticeDetailList = new ArrayList<>();
                        //异常入库的通知单详情
                        Set<String> exceptionSku = new HashSet<>();
                        Set<String> exceptionSkuCount = new HashSet<>();
                        //记录全部收货的通知单详情
                        List<WarehouseNoticeDetails> allNoticeDetailList = new ArrayList<>();
                        //查询入库通知单编号为entryOrderDetail.getEntryOrderCode()的入库通知单详情
                        WarehouseNoticeDetails warehouseNoticeDetail = new WarehouseNoticeDetails();
                        warehouseNoticeDetail.setWarehouseNoticeCode(noticeOrder.getWarehouseNoticeCode());
                        List<WarehouseNoticeDetails> warehouseNoticeDetailsList = warehouseNoticeDetailsService.select(warehouseNoticeDetail);
                        List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList = entryOrderDetail.getScmEntryOrderDetailResponseItemList();
                        if (!AssertUtil.collectionIsEmpty(warehouseNoticeDetailsList) && !AssertUtil.collectionIsEmpty(scmEntryOrderDetailResponseItemList)) {
                            for (WarehouseNoticeDetails warehouseDetail : warehouseNoticeDetailsList) {
                                //获取当前入库单详情的库存情况,目前只有两种状态
                                Map<String, Long> stockMap = delStock(scmEntryOrderDetailResponseItemList, warehouseDetail);
                                //判断入库为0的时候 直接跳过
                                if (stockMap.get("defectiveQ").longValue() == 0L && stockMap.get("normalQ").longValue() == 0L) {
                                    continue;
                                }
                                Long oldDefectiveQ = warehouseDetail.getDefectiveStorageQuantity() == null ? 0 : warehouseDetail.getDefectiveStorageQuantity();
                                Long oldNormalQ = warehouseDetail.getNormalStorageQuantity() == null ? 0 : warehouseDetail.getNormalStorageQuantity();
                                //判断状态
                                judgeWarehouseNoticeDetailState(stockMap, warehouseDetail);
                                //更新库存
                                updateSkuStockTable(warehouseNotice, warehouseDetail, stockMap.get("defectiveQ") - oldDefectiveQ, stockMap.get("normalQ") - oldNormalQ);
                                //分批记录收货状态
                                if (StringUtils.equals(String.valueOf(warehouseDetail.getStatus()), WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode())) {
                                    //通知单详情收货异常
                                    //1.残品入库
                                    if (StringUtils.equals(warehouseDetail.getInstockException(), "存在残品入库.")) {
                                        exceptionSku = new HashSet<>();
                                        exceptionSku.add(warehouseDetail.getSkuCode());
                                    }
                                    //2.正品入库数量大于采购数量
                                    if (StringUtils.equals(warehouseDetail.getInstockException(), "入库数量大于采购数量.")) {
                                        exceptionSkuCount = new HashSet<>();
                                        exceptionSkuCount.add(warehouseDetail.getSkuCode());
                                    }
                                }
                                if (StringUtils.equals(String.valueOf(warehouseDetail.getStatus()), WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode())) {
                                    //通知单详情部分收货
                                    partialNoticeDetailList.add(warehouseDetail);
                                }
                                if (StringUtils.equals(String.valueOf(warehouseDetail.getStatus()), WarehouseNoticeStatusEnum.ALL_GOODS.getCode())) {
                                    //通知单详情全部收货
                                    allNoticeDetailList.add(warehouseDetail);
                                }
                            }
                        } else {
                            logger.error("本地未查询到通知单编号为" + entryOrderDetail.getEntryOrderCode() + "的入库通知单详情,反馈的通知单详情为空");
                        }

                        //判断通知单状态,如果有异常记录备注
                        //状态判断有多种情况
                        //异常状态
                        if (!AssertUtil.collectionIsEmpty(exceptionSku) || !AssertUtil.collectionIsEmpty(exceptionSkuCount)) {
                            List<String> exceptionCauseList = new ArrayList<>();
                            if (!AssertUtil.collectionIsEmpty(exceptionSku)) {
                                exceptionCauseList.add("SKU[" + StringUtils.join(exceptionSku, SupplyConstants.Symbol.COMMA) + "]存在残品入库。");
                            }
                            if (!AssertUtil.collectionIsEmpty(exceptionSkuCount)) {
                                exceptionCauseList.add("SKU[" + StringUtils.join(exceptionSkuCount, SupplyConstants.Symbol.COMMA) + "]正品入库数量大于实际采购数量。");

                            }
                            warehouseNotice.setExceptionCause(((warehouseNotice.getExceptionCause()) == null ? "" : (warehouseNotice.getExceptionCause() + ",")) + StringUtils.join(exceptionCauseList, SupplyConstants.Symbol.COMMA));
                            warehouseNotice.setStatus(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode());
                        } else if (!AssertUtil.collectionIsEmpty(partialNoticeDetailList)) {
                            //部分收货
                            warehouseNotice.setStatus(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode());

                        } else if (!AssertUtil.collectionIsEmpty(allNoticeDetailList)) {
                            //全部收货
                            warehouseNotice.setStatus(WarehouseNoticeStatusEnum.ALL_GOODS.getCode());
                        }
                        //更新入库通知单
                        //把完成状态修改为完成
                        warehouseNotice.setFinishStatus(WarehouseNoticeFinishStatusEnum.FINISHED.getCode());
                        int count = warehouseNoticeService.updateByPrimaryKey(warehouseNotice);
                        if (count == 0) {
                            String msg = "修改入库通知单" + JSON.toJSONString(warehouseNotice) + "数据库操作失败";
                            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
                        }

                        //更新完成 记录日志
                        if (StringUtils.equals(warehouseNotice.getStatus(), WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode())) {
                            //获取异常日志
                            try {
                                logInfoService.recordLog(warehouseNotice, warehouseNotice.getId().toString(),
                                        "warehouse",
                                        WarehouseNoticeStatusEnum.getWarehouseNoticeStatusEnumByCode(warehouseNotice.getStatus()).getName()
                                        , getExceptionLog(warehouseNotice, warehouseNoticeDetailsList), null);
                            } catch (Exception e) {
                                logger.error("查询仓库详情，操作日志记录异常信息失败：{}", e.getMessage());
                            }
                        } else {
                            try {
                                logInfoService.recordLog(warehouseNotice, warehouseNotice.getId().toString(),
                                        "warehouse", WarehouseNoticeStatusEnum.getWarehouseNoticeStatusEnumByCode(warehouseNotice.getStatus()).getName(), "", null);
                            } catch (Exception e) {
                                logger.error("查询仓库详情，操作日志记录异常信息失败：{}", e.getMessage());
                            }
                        }
                    } else {
                        logger.error("未查询到通知单编号为" + entryOrderDetail.getEntryOrderCode() + "的入库通知单");
                    }
                }
            }
        }
    }

    private String getExceptionLog(WarehouseNotice warehouseNotice, List<WarehouseNoticeDetails> warehouseNoticeDetailsList) {
        List<String> errorList = new ArrayList<>();
        for (WarehouseNoticeDetails warehouseNoticeDetail:warehouseNoticeDetailsList) {
            if (StringUtils.equals(String.valueOf(warehouseNoticeDetail.getStatus()),WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode())){
                if (warehouseNoticeDetail.getDefectiveStorageQuantity()>0){
                    String log = "商品｛"+warehouseNoticeDetail.getSkuCode()+"｝残品入库数量"+(warehouseNoticeDetail.getDefectiveStorageQuantity());
                    errorList.add(log);
                }
                if (warehouseNoticeDetail.getActualStorageQuantity()>warehouseNoticeDetail.getPurchasingQuantity()){
                    String logError = "商品｛"+warehouseNoticeDetail.getSkuCode()+"｝的实际入库数量-采购数量="+(warehouseNoticeDetail.getActualStorageQuantity()-warehouseNoticeDetail.getPurchasingQuantity());
                    errorList.add(logError);
                }
            }
        }
        return StringUtils.join(errorList,"\n");
    }

    private Map<String,Long> delStock(List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList, WarehouseNoticeDetails warehouseDetail) {
        //用Map存库存信息
        Map<String,Long> stockMap = new HashMap<>();
        //残次品入库数量
        Long defectiveQuantity = 0L;
        //正品入库数量
        Long normalQuantity = 0L;
        for (ScmEntryOrderDetailResponseItem entryOrderDetailOrder : scmEntryOrderDetailResponseItemList) {

            if (StringUtils.equals(warehouseDetail.getItemId(),entryOrderDetailOrder.getItemId())){
                //计算反馈库存
                if (StringUtils.equals(entryOrderDetailOrder.getGoodsStatus(),EntryOrderDetailItemStateEnum.QUALITY_PRODUCTS.getCode())){
                    normalQuantity = (warehouseDetail.getNormalStorageQuantity() == null ? 0 : warehouseDetail.getNormalStorageQuantity()) + (entryOrderDetailOrder.getActualQty()) + (normalQuantity);
                }else if (StringUtils.equals(entryOrderDetailOrder.getGoodsStatus(),EntryOrderDetailItemStateEnum.DEFECTIVE_PRODUCTS.getCode())){
                    defectiveQuantity =warehouseDetail.getDefectiveStorageQuantity()==null?0:warehouseDetail.getDefectiveStorageQuantity()+entryOrderDetailOrder.getActualQty()+defectiveQuantity;
                }
            }
        }
        stockMap.put("normalQ",normalQuantity);
        stockMap.put("defectiveQ",defectiveQuantity);

        return  stockMap;
    }

    /**
     * 判断入库单详情的状态
     * @param stockMap
     */
    private WarehouseNoticeDetails judgeWarehouseNoticeDetailState(Map<String,Long> stockMap,WarehouseNoticeDetails warehouseDetail) {
        //正品入库
        Long normalQuantity = stockMap.get("normalQ");
        //残次品入库
        Long defectiveQuantity = stockMap.get("defectiveQ");
        //判断收货状态
        if (defectiveQuantity > 0) {
            warehouseDetail.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
            warehouseDetail.setInstockException("存在残品入库.");
        } else if (normalQuantity < warehouseDetail.getPurchasingQuantity()) {
            warehouseDetail.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode()));
        } else if (normalQuantity > warehouseDetail.getPurchasingQuantity()) {
            warehouseDetail.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode()));
            warehouseDetail.setInstockException("入库数量大于采购数量.");
        }
        else if (normalQuantity.equals(warehouseDetail.getPurchasingQuantity())) {
            warehouseDetail.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.ALL_GOODS.getCode()));
        }
        //入库数量
        warehouseDetail.setNormalStorageQuantity(normalQuantity);
        warehouseDetail.setDefectiveStorageQuantity(defectiveQuantity);
        warehouseDetail.setActualStorageQuantity(normalQuantity+defectiveQuantity);
        //设置入库时间
        warehouseDetail.setStorageTime(Calendar.getInstance().getTime());
        //更新入库通知单
        int count = warehouseNoticeDetailsService.updateByPrimaryKey(warehouseDetail);
        if (count == 0) {
            String msg = "修改入库通知单详情" + JSON.toJSONString(warehouseDetail) + "数据库操作失败";
            throw new WarehouseNoticeDetailException(ExceptionEnum.WAREHOUSE_NOTICE_DETAIL_EXCEPTION, msg);
        }

        return warehouseDetail;
    }

    private void updateSkuStockTable(WarehouseNotice warehouseNotice, WarehouseNoticeDetails warehouseNoticeDetails, Long defectiveQuantity, Long normalQuantity) {

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
                stock.setChannelCode("TRMALL");
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
            logger.error("库存ID:"+warehouseNoticeDetails.getSkuStockId()+"未获取到锁！\n真实库存新增："+(normalQuantity + defectiveQuantity)
                    +"\n可用正品库新增："+normalQuantity+"\n残次品库存新增："+defectiveQuantity);
        }
    }

}
