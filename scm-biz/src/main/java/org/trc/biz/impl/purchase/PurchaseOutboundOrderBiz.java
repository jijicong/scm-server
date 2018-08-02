package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.taxrate.TaxRate;
import org.trc.domain.util.Area;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.*;
import org.trc.enums.purchase.*;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.enums.warehouse.PurchaseOutboundOrderTypeEnum;
import org.trc.exception.PurchaseOrderException;
import org.trc.exception.PurchaseOutboundOrderException;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.form.warehouse.ScmInventoryQueryItem;
import org.trc.form.warehouse.ScmInventoryQueryRequest;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.service.category.IBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemsService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import org.trc.service.purchase.IPurchaseOutboundOrderService;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.taxrate.TaxRateService;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.*;
import org.trc.util.cache.PurchaseOutboundOrderCacheEvict;
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
@Service("purchaseOutboundOrderBiz")
public class PurchaseOutboundOrderBiz implements IPurchaseOutboundOrderBiz {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOutboundOrderBiz.class);

    private final static Integer LENGTH = 5;

    @Autowired
    private IPurchaseOutboundOrderService purchaseOutboundOrderService;

    @Autowired
    private IPurchaseOutboundDetailService purchaseOutboundDetailService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

    @Autowired
    private ISkusService skusService;

    @Autowired
    private ILogInfoService logInfoService;

    @Autowired
    private ISupplierBrandService supplierBrandService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;

    @Autowired
    private TaxRateService taxRateService;

    @Autowired
    private ICommonService commonService;

    @Autowired
    private IWarehouseApiService warehouseApiService;

    @Autowired
    private IWarehouseNoticeService warehouseNoticeService;

    @Autowired
    private IWarehouseNoticeDetailsService warehouseNoticeDetailsService;

    @Autowired
    private IPurchaseOutboundNoticeService purchaseOutboundNoticeService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ILocationUtilService locationUtilService;

    @Autowired
    private IPurchaseOrderService purchaseOrderService;

    /**
     * 查询采购退货单列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_OUTBOUND_ORDER)
    public Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPageList(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode) {
        AssertUtil.notBlank(channelCode, "未获得授权");
        Example example = setSelectCondition(form, channelCode);
        if (example != null) {
            Pagenation<PurchaseOutboundOrder> pagination = purchaseOutboundOrderService.pagination(example, page, new QueryModel());
            if (CollectionUtils.isEmpty(pagination.getResult())) {
                return pagination;
            }
            //供应商，仓库名称
            setSupplierName(pagination);
            return pagination;
        }
        List<PurchaseOutboundOrder> purchaseOutboundOrderList = new ArrayList<>();
        page.setResult(purchaseOutboundOrderList);
        page.setTotalCount(0);
        return page;
    }

    /**
     * 采购退货单保存或提交审核
     *
     * @param form                采购退货单数据
     * @param code                保存类型 0-暂存 1-提交审核
     * @param aclUserAccreditInfo
     */
    @Override
    @PurchaseOutboundOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePurchaseOutboundOrder(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo) {
        log.info("采购退货单保存或提交审核，PurchaseOutboundOrder:{}, 当前操作:{} ", JSON.toJSONString(form), code.equals("0") ? "[0]暂存" : "[1]提交审核");
        validationRequestParam(form, aclUserAccreditInfo);
        //校验仓库是否停用
        checkWarehouse(form.getWarehouseInfoId());

        //提交审核校验必填参数
        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), code)) {
            validationParam(form);
        }
        ParamsUtil.setBaseDO(form);
        //生成采购退货单号
        String seq = serialUtilService.generateCode(LENGTH, SequenceEnum.CGTH_PREFIX.getCode(), DateUtils.dateToCompactString(form.getCreateTime()));
        AssertUtil.notBlank(seq, "获取编码失败");

        //插入采购退货单和退货详情,记录日志
        insertPurchaseOutboundOrderAndDetail(form, code, aclUserAccreditInfo, seq);

        //保存提交审核，修改采购退货单状态
        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), code)) {
            PurchaseOutboundOrder purchaseOutboundOrder = new PurchaseOutboundOrder();
            purchaseOutboundOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
            purchaseOutboundOrder.setStatus(PurchaseOutboundOrderStatusEnum.AUDIT.getCode());
            purchaseOutboundOrder.setAuditStatus(PurchaseOutboundOrderStatusEnum.AUDIT.getCode());
            purchaseOutboundOrder.setCommitAuditTime(Calendar.getInstance().getTime());
            purchaseOutboundOrder.setAuditDescription(form.getAuditDescription());
            Example example = new Example(PurchaseOutboundOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("purchaseOutboundOrderCode", seq);
            int i = purchaseOutboundOrderService.updateByExampleSelective(purchaseOutboundOrder, example);

            //记录日志
            logInfoService.recordLog(form, form.getId().toString(), aclUserAccreditInfo.getUserId(), AuditStatusEnum.COMMIT.getName(), null, ZeroToNineEnum.ZERO.getCode());
            if (i < 1) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "提交审核采购退货单失败");
            }

        }
    }

    /**
     * 更新采购退货单
     *
     * @param form                表单数据
     * @param aclUserAccreditInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseOutboundOrderCacheEvict
    public void updatePurchaseOutboundOrder(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        log.info("更新采购退货单，PurchaseOutboundOrder:{}", JSON.toJSONString(form));
        AssertUtil.notNull(form, "修改采购退货单失败,采购退货单为空");

        //校验仓库是否停用
        this.checkWarehouse(form.getWarehouseInfoId());
        //提交审核校验必填参数
        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), form.getStatus())) {
            validationParam(form);
        }
        //更新总金额
        BigDecimal totalAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(form.getPurchaseOutboundDetailList())) {
            List<PurchaseOutboundDetail> purchaseOutboundDetailList = form.getPurchaseOutboundDetailList();
            for (PurchaseOutboundDetail purchaseOutboundDetail : purchaseOutboundDetailList) {
                totalAmount = totalAmount.add(purchaseOutboundDetail.getTotalAmount());
            }
        }
        form.setTotalFee(totalAmount.setScale(3, RoundingMode.HALF_UP));

        int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(form);
        if (i < 1) {
            log.error("采购退货单更新异常, 采购退货单号:{}", form.getPurchaseOutboundOrderCode());
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, "采购退货单更新异常");
        }
        //删除退货单商品，重新添加
        Example example = new Example(PurchaseOutboundDetail.class);
        example.createCriteria().andEqualTo("purchaseOutboundOrderCode", form.getPurchaseOutboundOrderCode());
        purchaseOutboundDetailService.deleteByExample(example);

        insertPurchaseOutboundDetail(form);

        //修改操作日志
        logInfoService.recordLog(form, form.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.UPDATE.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
    }

    /**
     * 根据采购退货单Id查询采购退货单
     *
     * @param id 采购退货单Id
     * @return
     */
    @Override
    public PurchaseOutboundOrder getPurchaseOutboundOrderById(Long id) {
        AssertUtil.notNull(id, "根据采购退货单Id查询采购退货单失败，采购退货单Id为空");
        PurchaseOutboundOrder purchaseOutboundOrder = purchaseOutboundOrderService.selectByPrimaryKey(id);
        AssertUtil.notNull(purchaseOutboundOrder, "采购单货单根据主键id查询失败，没有对应采购退货单");

        Example example = new Example(PurchaseOutboundDetail.class);
        example.createCriteria().andEqualTo("purchaseOutboundOrderCode", purchaseOutboundOrder.getPurchaseOutboundOrderCode());
        List<PurchaseOutboundDetail> purchaseOutboundDetails = purchaseOutboundDetailService.selectByExample(example);
        //设置品牌名称
        purchaseOutboundDetails.forEach(purchaseOutboundDetail -> {
            Brand brand = brandService.selectByPrimaryKey(Long.valueOf(purchaseOutboundDetail.getBrandId()));
            if (brand != null) {
                purchaseOutboundDetail.setBrandName(brand.getName());
            }
        });
        purchaseOutboundOrder.setPurchaseOutboundDetailList(purchaseOutboundDetails);
        return purchaseOutboundOrder;
    }

    private Map<String, Long> selectCanBackQuantity(PurchaseOutboundOrder purchaseOutboundOrder, List<String> skus) {

        //查询退货仓库
        Example warehouseItemExample = new Example(WarehouseItemInfo.class);
        Example.Criteria warehouseItemCriteria = warehouseItemExample.createCriteria();
        warehouseItemCriteria.andEqualTo("warehouseInfoId", purchaseOutboundOrder.getWarehouseInfoId());
        warehouseItemCriteria.andEqualTo("noticeStatus", NoticsWarehouseStateEnum.SUCCESS.getCode());
        warehouseItemCriteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(warehouseItemExample);
        AssertUtil.notEmpty(warehouseItemInfoList, "退货仓库商品异常");

        List<WarehouseItemInfo> warehouseItemInfos = new ArrayList<>();
        for (String skuCode : skus) {
            for (WarehouseItemInfo warehouseItemInfo : warehouseItemInfoList) {
                if (StringUtils.equals(skuCode, warehouseItemInfo.getSkuCode())) {
                    warehouseItemInfos.add(warehouseItemInfo);
                    break;
                }
            }
        }

        Map<String, Long> inventoryInfo = new HashMap<>();
        if (!CollectionUtils.isEmpty(warehouseItemInfos)) {
            //京东接口查询库存信息
            inventoryInfo = skuInventoryQuery(warehouseItemInfos, purchaseOutboundOrder.getReturnOrderType());
        }

        return inventoryInfo;
    }

    /**
     * 获取采购退货单商品详情
     *
     * @param form 查询条件
     * @param page
     * @param skus 过滤已选择的sku
     * @return
     */
    @Override
    public Pagenation<PurchaseOutboundDetail> getPurchaseOutboundOrderDetail(PurchaseOutboundItemForm form, Pagenation<PurchaseOutboundDetail> page, String skus) {
        log.info("------getPurchaseOutboundOrderDetail, form:{}", JSON.toJSONString(form));
        validateParam(form);
        Pagenation<Skus> pagenation = new Pagenation();
        pagenation.setStart(page.getStart());
        pagenation.setPageSize(page.getPageSize());
        pagenation.setPageNo(page.getPageNo());
        List<PurchaseOutboundDetail> list = getDetails(form, skus, page, pagenation);
        page.setResult(list);
        return page;
    }

    /**
     * 采购退货单获取采购历史详情
     *
     * @param form
     * @param page
     * @return
     */
    @Override
    public Pagenation<WarehouseNoticeDetails> getPurchaseHistory(PurchaseOutboundItemForm form, Pagenation<WarehouseNoticeDetails> page) {
        validateParam(form);
        AssertUtil.notBlank(form.getSkuCode(), "skuCode不能为空");
        Example example = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("warehouseInfoId", form.getWarehouseInfoId());
        criteria.andEqualTo("supplierCode", form.getSupplierCode());
        criteria.andEqualTo("finishStatus", WarehouseNoticeFinishStatusEnum.FINISHED.getCode());
        List<WarehouseNotice> warehouseNotices = warehouseNoticeService.selectByExample(example);
        if (CollectionUtils.isEmpty(warehouseNotices)) {
            return null;
        }
        List<WarehouseNoticeDetails> details = new ArrayList<>();
        for (WarehouseNotice warehouseNotice : warehouseNotices) {
            Example detailExample = new Example(WarehouseNoticeDetails.class);
            Example.Criteria detailCriteria = detailExample.createCriteria();
            detailCriteria.andEqualTo("warehouseNoticeCode", warehouseNotice.getWarehouseNoticeCode());
            detailCriteria.andEqualTo("skuCode", form.getSkuCode());
            detailCriteria.andBetween("storageTime", form.getStartDate(), form.getEndDate());
            //退货类型是残品,查询入库单状态为入库异常
            if (StringUtils.equals(PurchaseOutboundOrderTypeEnum.SUBSTANDARD.getCode(), form.getReturnOrderType())) {
                detailCriteria.andEqualTo("status", WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode());
            }
            /**
             *退货类型是正品,查询入库单 状态入库异常和全部入库和部分入库
             * 入库异常分:
             *     1.有残次品入库
             *     2.入库数量大于采购数量
             */
            else if (StringUtils.equals(PurchaseOutboundOrderTypeEnum.QUALITY.getCode(), form.getReturnOrderType())) {
                detailCriteria.andIn("status", Arrays.asList(WarehouseNoticeStatusEnum.RECEIVE_PARTIAL_GOODS.getCode(),
                        WarehouseNoticeStatusEnum.RECEIVE_GOODS_EXCEPTION.getCode(),
                        WarehouseNoticeStatusEnum.ALL_GOODS.getCode()));
            }
            List<WarehouseNoticeDetails> warehouseNoticeDetails = warehouseNoticeDetailsService.selectByExample(detailExample);
            if (CollectionUtils.isEmpty(warehouseNoticeDetails)) {
                continue;
            }
            for (WarehouseNoticeDetails notic : warehouseNoticeDetails) {
                /**
                 * 1.退货类型是残品:(过滤掉残次品为0的商品)
                 *      如果残品数量为0，跳过当前循环
                 *
                 * 2.退货类型是正品:(过滤掉残品记录)
                 *      如果残品数量大于0，跳过当前循环
                 */
                if (((StringUtils.equals(PurchaseOutboundOrderTypeEnum.SUBSTANDARD.getCode(), form.getReturnOrderType()))
                        && (notic.getDefectiveStorageQuantity() != null && notic.getDefectiveStorageQuantity() == 0))
                        || ((StringUtils.equals(PurchaseOutboundOrderTypeEnum.QUALITY.getCode(), form.getReturnOrderType()))
                        && (notic.getDefectiveStorageQuantity() != null && notic.getDefectiveStorageQuantity() > 0))) {
                    continue;
                }

                notic.setPurchaseOrderCode(warehouseNotice.getPurchaseOrderCode());
                details.add(notic);
            }
        }

        /**
         * 分页查询结果
         */
        List<Long> warehouseNoticeDetailsIds = details.stream().map(WarehouseNoticeDetails::getId).collect(Collectors.toList());
        Example ex = new Example(WarehouseNoticeDetails.class);
        Example.Criteria criteria1 = ex.createCriteria();
        criteria1.andIn("id", warehouseNoticeDetailsIds);
        return warehouseNoticeDetailsService.pagination(ex, page, new QueryModel());
    }

    /**
     * 出库通知作废操作
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseOutboundOrderCacheEvict
    public void cancelWarahouseAdvice(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {

        Example example = new Example(PurchaseOutboundNotice.class);
        example.createCriteria().andEqualTo("purchaseOutboundOrderCode", form.getPurchaseOutboundOrderCode());
        List<PurchaseOutboundNotice> purchaseOutboundNotices = purchaseOutboundNoticeService.selectByExample(example);
        AssertUtil.notEmpty(purchaseOutboundNotices, "出库通知作废失败，没有对应的出库单信息");
        PurchaseOutboundNotice purchaseOutboundNotice = purchaseOutboundNotices.get(0);

        //对应退货出库通知单的状态=“待通知出库”或“仓库接收失败”或“已取消” 才允许作废操作
        checkNoticeStatus(purchaseOutboundNotice);

        //更改采购退货单状态
        PurchaseOutboundOrder purchaseOutboundOrder = new PurchaseOutboundOrder();
        purchaseOutboundOrder.setId(form.getId());
        purchaseOutboundOrder.setStatus(PurchaseOutboundOrderStatusEnum.DROPPED.getCode());
        //对应出库单状态为其他
        purchaseOutboundOrder.setOutboundStatus("");
        int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(purchaseOutboundOrder);
        if (i < 1) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, String.format("作废%s采购退货单操作失败", JSON.toJSONString(form.getPurchaseOutboundOrderCode())));
        }

        //同步出库单状态
        PurchaseOutboundNotice notice = new PurchaseOutboundNotice();
        notice.setId(purchaseOutboundNotice.getId());
        notice.setStatus(PurchaseOutboundNoticeStatusEnum.CANCEL.getCode());
        notice.setFinishStatus(WarehouseNoticeFinishStatusEnum.FINISHED.getCode());
        int num = purchaseOutboundNoticeService.updateByPrimaryKeySelective(notice);
        if (num < 1) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, String.format("作废%s采购单操作失败,出库通知单已经被执行操作", JSON.toJSONString(form.getPurchaseOutboundOrderCode())));
        }

        //更新出库详情
        PurchaseOutboundDetail purchaseOutboundDetail = new PurchaseOutboundDetail();
        purchaseOutboundDetail.setStatus(PurchaseOutboundDetailStatusEnum.CANCEL.getCode());
        //对应出库单状态为其他
        purchaseOutboundDetail.setOutboundStatus("");
        Example example1 = new Example(WarehouseNoticeDetails.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("outboundNoticeCode", purchaseOutboundNotice.getOutboundNoticeCode());
        int num2 = purchaseOutboundDetailService.updateByExampleSelective(purchaseOutboundDetail, example1);
        if (num2 == 0) {
            String msg = String.format("作废采购退货单操作失败,出库单%s详情状态同步失败", JSON.toJSONString(purchaseOutboundNotice.getOutboundNoticeCode()));
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, msg);
        }

        //记录操作日志
        logInfoService.recordLog(form, form.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.CANCEL.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
        logInfoService.recordLog(purchaseOutboundNotice, purchaseOutboundNotice.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.CANCEL.getMessage(), null, null);
    }


    /**
     * 更新采购退货单状态
     *
     * @param form
     * @param aclUserAccreditInfo
     * @return
     */
    @Override
    @PurchaseOutboundOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updateStatus(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(form, "状态修改失败，采购退货单信息为空");
        AssertUtil.notNull(form.getId(), "状态修改失败，采购退货单ID为空");
        PurchaseOutboundOrder purchaseOutboundOrder = purchaseOutboundOrderService.selectByPrimaryKey(form.getId());
        //暂存：的删除操作
        if (PurchaseOutboundOrderStatusEnum.HOLD.getCode().equals(purchaseOutboundOrder.getStatus())) {
            handleDeleted(form, aclUserAccreditInfo);
            return "删除成功!";
        }
        //审核驳回：的删除操作
        if (PurchaseOutboundOrderStatusEnum.REJECT.getCode().equals(purchaseOutboundOrder.getStatus())) {
            handleDeleted(form, aclUserAccreditInfo);
            return "删除成功!";
        }
        //审核通过：的作废操作
        if (PurchaseOutboundOrderStatusEnum.PASS.getCode().equals(purchaseOutboundOrder.getStatus())) {
            handleCancel(form, aclUserAccreditInfo);
            return "作废成功!";
        }
        return "操作失败!";
    }

    /**
     * 采购退货单出库通知
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    @Override
    @PurchaseOutboundOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void warehouseAdvice(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(form, "采购退货单信息为空,保存采购退货出库通知单失败");
        AssertUtil.notNull(form.getId(), "采购退货单的主键为空,保存采购退货出库通知单失败");

        //防止重复提交 20s
        String identifier = redisLock.Lock(DistributeLockEnum.PURCHASE_OUTBOUND_ORDER.getCode() + "warehouseAdvice" + form.getId(), 0, 20000);
        if (StringUtils.isBlank(identifier)) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "请不要重复操作!");
        }
        try {
            PurchaseOutboundOrder purchaseOutboundOrder = purchaseOutboundOrderService.selectByPrimaryKey(form.getId());
            AssertUtil.notNull(purchaseOutboundOrder, "根据主键查询该采购退货单为空");
            if (!StringUtils.equals(purchaseOutboundOrder.getStatus(), PurchaseOutboundOrderStatusEnum.PASS.getCode())) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "采购退货单出库通知操作失败,采购退货单未审核通过！");
            }

            PurchaseOutboundNotice notice = new PurchaseOutboundNotice();
            AssertUtil.notNull(aclUserAccreditInfo.getUserId(), "您的用户信息为空");
            notice.setCreateOperator(aclUserAccreditInfo.getUserId());

            WarehouseInfo warehouseInfo = new WarehouseInfo();
            warehouseInfo.setId(purchaseOutboundOrder.getWarehouseInfoId());
            warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);

            //初始化采购退货通知单参数
            initPurchaseOutboundNotice(purchaseOutboundOrder, notice, warehouseInfo);

            //更改采购退货单状态
            PurchaseOutboundOrder order = new PurchaseOutboundOrder();
            order.setId(purchaseOutboundOrder.getId());
            order.setStatus(PurchaseOutboundOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
            order.setOutboundStatus(PurchaseOutboundStatusEnum.WAIT.getCode());
            int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(order);
            if (i == 0) {
                String msg = "采购退货单状态更改失败";
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, msg);
            }

            //更新采购退货单商品详情状态
            PurchaseOutboundDetail purchaseOutboundDetail = new PurchaseOutboundDetail();
            purchaseOutboundDetail.setOutboundStatus(PurchaseOutboundStatusEnum.WAIT.getCode());
            purchaseOutboundDetail.setStatus(PurchaseOutboundDetailStatusEnum.TO_BE_NOTIFIED.getCode());
            Example example = new Example(PurchaseOutboundDetail.class);
            example.createCriteria().andEqualTo("purchaseOutboundOrderCode", purchaseOutboundOrder.getPurchaseOutboundOrderCode());
            int count = purchaseOutboundDetailService.updateByExampleSelective(purchaseOutboundDetail, example);
            if (count == 0) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, "采购退货单出库通知失败，对应商品信息异常");
            }

            //记录操作日志
            logInfoService.recordLog(purchaseOutboundOrder, purchaseOutboundOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.OUTBOUND_NOTICE.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
            logInfoService.recordLog(notice, notice.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.ADD.getMessage(), null, null);

        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.PURCHASE_OUTBOUND_ORDER.getCode() + "warehouseAdvice" + form.getId(), identifier)) {
                    log.info("PurchaseOutboundOrderId:{} 采购退货单出库通知，解锁成功，identifier:{}", form.getId(), identifier);
                } else {
                    log.error("PurchaseOutboundOrderId:{} 采购退货单出库通知，解锁失败，identifier:{}", form.getId(), identifier);
                }
            } catch (Exception e) {
                log.error("warehouseNoticeCode:{} 入库通知，解锁失败，identifier:{}, err:{}", form.getId(), identifier, e);
            }
        }
    }

    /**
     * 采购退货单审核操作，获取详情
     *
     * @param id 采购退货单Id
     * @return
     */
    @Override
    public PurchaseOutboundOrder getPurchaseOutboundAuditOrder(Long id) {
        AssertUtil.notNull(id, "采购退货单审核操作，获取详情单失败，采购退货单Id为空");
        PurchaseOutboundOrder purchaseOutboundOrder = purchaseOutboundOrderService.selectByPrimaryKey(id);
        AssertUtil.notNull(purchaseOutboundOrder, "采购退货单审核操作，获取详情失败，没有对应采购退货单");

        //仓库名称
        WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(purchaseOutboundOrder.getWarehouseInfoId());
        purchaseOutboundOrder.setWarehouseName(warehouseInfo.getWarehouseName());

        Example example = new Example(PurchaseOutboundDetail.class);
        example.createCriteria().andEqualTo("purchaseOutboundOrderCode", purchaseOutboundOrder.getPurchaseOutboundOrderCode());
        List<PurchaseOutboundDetail> purchaseOutboundDetails = purchaseOutboundDetailService.selectByExample(example);
        List<String> skus = purchaseOutboundDetails.stream().map(PurchaseOutboundDetail::getSkuCode).collect(Collectors.toList());


        //实时查询商品可退数量
        Map<String, Long> canBackQuantity = selectCanBackQuantity(purchaseOutboundOrder, skus);

        //设置品牌名称
        for (PurchaseOutboundDetail purchaseOutboundDetail : purchaseOutboundDetails) {
            Brand brand = brandService.selectByPrimaryKey(Long.valueOf(purchaseOutboundDetail.getBrandId()));
            if (brand != null) {
                purchaseOutboundDetail.setBrandName(brand.getName());
            }
            if (!CollectionUtils.isEmpty(canBackQuantity) && canBackQuantity.get(purchaseOutboundDetail.getSkuCode()) != null) {
                purchaseOutboundDetail.setCanBackQuantity(canBackQuantity.get(purchaseOutboundDetail.getSkuCode()));
            }
        }

        purchaseOutboundOrder.setPurchaseOutboundDetailList(purchaseOutboundDetails);
        return purchaseOutboundOrder;
    }

    /**
     * 获取采购退货单审核列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_OUTBOUND_ORDER)
    public Pagenation<PurchaseOutboundOrder> getAuditPagelist(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode) {
        AssertUtil.notBlank(channelCode, "未获得授权");
        Example example = setAuditSelectCondition(form, channelCode);
        if (example != null) {
            Pagenation<PurchaseOutboundOrder> pagination = purchaseOutboundOrderService.pagination(example, page, new QueryModel());
            if (CollectionUtils.isEmpty(pagination.getResult())) {
                return pagination;
            }
            //供应商，仓库名称
            setSupplierName(pagination);
            return pagination;
        }
        List<PurchaseOutboundOrder> purchaseOutboundOrderList = new ArrayList<>();
        page.setResult(purchaseOutboundOrderList);
        page.setTotalCount(0);
        return page;
    }

    /**
     * 采购退货单审核
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    @Override
    @PurchaseOutboundOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void auditPurchaseOrder(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        checkParam(form);
        PurchaseOutboundOrder purchaseOutboundOrder = new PurchaseOutboundOrder();
        purchaseOutboundOrder.setId(form.getId());
        purchaseOutboundOrder.setAuditOpinion(form.getAuditOpinion());
        purchaseOutboundOrder.setAuditDescription(form.getAuditDescription());
        purchaseOutboundOrder.setAuditOperator(aclUserAccreditInfo.getUserId());
        //更新采购退货单状态
        purchaseOutboundOrder.setStatus(form.getStatus());
        //更新采购退货单审核状态
        purchaseOutboundOrder.setAuditStatus(form.getAuditStatus());
        purchaseOutboundOrder.setUpdateAuditTime(Calendar.getInstance().getTime());

        int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(purchaseOutboundOrder);
        if (i == 0) {
            String msg = String.format("%s采购退货单审核失败", JSON.toJSONString(form.getPurchaseOutboundOrderCode()));
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, msg);
        }

        //记录审核日志
        if (StringUtils.equals(PurchaseOutboundOrderAuditStatusEnum.REJECT.getCode(), form.getAuditStatus())) {
            logInfoService.recordLog(purchaseOutboundOrder, purchaseOutboundOrder.getId().toString(), aclUserAccreditInfo.getUserId(), PurchaseOutboundOrderAuditStatusEnum.REJECT.getName(), form.getAuditOpinion(), null);
        } else if (StringUtils.equals(PurchaseOutboundOrderAuditStatusEnum.PASS.getCode(), form.getAuditStatus())) {
            logInfoService.recordLog(purchaseOutboundOrder, purchaseOutboundOrder.getId().toString(), aclUserAccreditInfo.getUserId(), PurchaseOutboundOrderAuditStatusEnum.PASS.getName(), form.getAuditOpinion(), null);
        }
    }

    /**
     * 获取退货仓库下拉列表
     *
     * @param channelCode
     * @return
     */
    @Override

    public List<WarehouseInfo> getWarehousesByChannelCode(String channelCode) {
        //获取已启用仓库信息
        WarehouseInfo warehouse = new WarehouseInfo();
        warehouse.setIsValid(ZeroToNineEnum.ONE.getCode());
        //运营性质(0:第三方仓库 1:自营仓库)
        //过滤掉“运营性质”为“自营仓库”的仓库
        warehouse.setOperationalNature(ZeroToNineEnum.ONE.getCode());
        //已通知仓库
        warehouse.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
        List<WarehouseInfo> warehouseList = warehouseInfoService.select(warehouse);
        AssertUtil.notEmpty(warehouseList, "无数据，请确认【仓储管理-仓库信息管理】中存在“启用”状态，并且货主仓库状态为“通知成功”的仓库！");

        return warehouseList;
    }

    /**
     * 获取供应商名称下拉列表
     * 申请状态为“审核通过”且供应商类型为“采购”的供应商名称
     *
     * @param channelCode
     * @return
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.SUPPLIER)
    public List<Supplier> getSuppliersByChannelCode(String channelCode) {
        //根据渠道用户查询对应的供应商
        AssertUtil.notBlank(channelCode, "获取渠道编号失败");
        return purchaseOrderService.findSuppliersByChannelCode(channelCode);
    }

    private void checkParam(PurchaseOutboundOrder form) {
        AssertUtil.notNull(form, "采购退货单信息为空");
        AssertUtil.notNull(form.getId(), "采购退货单ID为空");
        AssertUtil.notBlank(form.getAuditStatus(), "审核状态不能为空");

        PurchaseOutboundOrder purchaseOutboundOrder = purchaseOutboundOrderService.selectByPrimaryKey(form.getId());
        if (!StringUtils.equals(PurchaseOutboundOrderAuditStatusEnum.COMMIT.getCode(), purchaseOutboundOrder.getAuditStatus())) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "采购退货单审核失败，请查看审核状态");
        }
        //审核驳回状态，审核意见不能为空
        if (StringUtils.equals(PurchaseOutboundOrderAuditStatusEnum.REJECT.getCode(), form.getAuditStatus())) {
            AssertUtil.notBlank(form.getAuditOpinion(), "审核驳回，审核意见不能为空");
        }
    }

    private Example setAuditSelectCondition(PurchaseOutboundOrderForm form, String channelCode) {
        Example example = new Example(PurchaseOutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("channelCode", channelCode);

        if (StringUtils.isNotBlank(form.getPurchaseOutboundOrderCode())) {
            criteria.andLike("purchaseOutboundOrderCode", "%" + form.getPurchaseOutboundOrderCode() + "%");
        }

        //审核状态
        if (StringUtils.isNotBlank(form.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", form.getAuditStatus());
        }

        //供应商名称
        if (StringUtils.isNotBlank(form.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", form.getSupplierCode());
        }

        //退货类型1-正品，2-残品
        if (StringUtils.isNotBlank(form.getReturnOrderType())) {
            criteria.andEqualTo("returnOrderType", form.getReturnOrderType());
        }

        //退货仓库
        if (StringUtils.isNotBlank(form.getWarehouseInfoId())) {
            criteria.andEqualTo("warehouseInfoId", form.getWarehouseInfoId());
        }

        if (StringUtils.isNotBlank(form.getStartDate())) {
            criteria.andGreaterThan("commitAuditTime", form.getCommitAuditTime());
        }
        if (StringUtils.isNotBlank(form.getEndDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
            Date date = null;
            try {
                date = sdf.parse(form.getEndDate());
            } catch (ParseException e) {
                String msg = "采购审核列表查询,截止日期的格式不正确";
                log.error(msg);
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_QUERY_EXCEPTION, msg);
            }
            date = DateUtils.addDays(date, 1);
            form.setEndDate(sdf.format(date));
            criteria.andLessThan("commitAuditTime", form.getCommitAuditTime());
        }
        criteria.andEqualTo("isDeleted", "0");
        example.setOrderByClause("instr('1,3',`auditStatus`) ASC");
        example.orderBy("updateTime").desc();
        return example;
    }

    /**
     * 初始化采购退货通知单参数
     *
     * @param purchaseOutboundOrder
     * @param notice
     * @param warehouseInfo
     */
    private void initPurchaseOutboundNotice(PurchaseOutboundOrder purchaseOutboundOrder, PurchaseOutboundNotice notice, WarehouseInfo warehouseInfo) {
        String purchaseOutboundNoticeCode = serialUtilService.generateCode(LENGTH, SequenceEnum.TH_CKTZ_PREFIX.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        notice.setOutboundNoticeCode(purchaseOutboundNoticeCode);
        notice.setPurchaseOutboundOrderCode(purchaseOutboundOrder.getPurchaseOutboundOrderCode());
        notice.setWarehouseId(Long.valueOf(purchaseOutboundOrder.getWarehouseId()));
        notice.setWarehouseInfoId(purchaseOutboundOrder.getWarehouseInfoId());
        notice.setWarehouseCode(purchaseOutboundOrder.getWarehouseCode());
        //待通知收货
        notice.setStatus(PurchaseOutboundNoticeStatusEnum.TO_BE_NOTIFIED.getCode());
        // 默认为未完成的状态
        notice.setFinishStatus(WarehouseNoticeFinishStatusEnum.UNFINISHED.getCode());
        notice.setSupplierId(purchaseOutboundOrder.getSupplierId());
        notice.setSupplierCode(purchaseOutboundOrder.getSupplierCode());

        //提货方式1-到仓自提，2-京东配送，3-其他物流
        notice.setPickType(purchaseOutboundOrder.getPickType());
        //退货类型1-正品，2-残品
        notice.setReturnOrderType(purchaseOutboundOrder.getReturnOrderType());
        notice.setRemark(purchaseOutboundOrder.getRemark());


        //退货收货人信息
        notice.setReceiver(purchaseOutboundOrder.getReceiver());
        notice.setReceiverNumber(purchaseOutboundOrder.getReceiverNumber());

        Area area = new Area();
        area.setCode(purchaseOutboundOrder.getReceiverProvince());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "收货人所在省为空");
        notice.setReceiverProvince(area.getProvince());

        area = new Area();
        area.setCode(purchaseOutboundOrder.getReceiverCity());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "收货人所在城市为空");
        notice.setReceiverCity(area.getCity());

        area = new Area();
        area.setCode(purchaseOutboundOrder.getReceiverArea());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "收货人所在地区为空");
        notice.setReceiverArea(area.getDistrict());
        notice.setReceiverAddress(purchaseOutboundOrder.getReceiverAddress());

        int i = purchaseOutboundNoticeService.insert(notice);
        if (i == 0) {
            String msg = "保存采购退货出库通知单失败";
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, msg);
        }
    }


    /**
     * 审核通过：的作废操作
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    private void handleCancel(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        PurchaseOutboundOrder purchaseOutboundOrder = new PurchaseOutboundOrder();
        purchaseOutboundOrder.setId(form.getId());
        purchaseOutboundOrder.setStatus(PurchaseOutboundOrderStatusEnum.DROPPED.getCode());
        int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(purchaseOutboundOrder);
        if (i < 1) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_UPDATE_EXCEPTION, String.format("作废%s采购退货单操作失败", JSON.toJSONString(form.getPurchaseOutboundOrderCode())));
        }
        logInfoService.recordLog(purchaseOutboundOrder, purchaseOutboundOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.CANCEL.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
    }

    /**
     * 删除操作
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    private void handleDeleted(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {

        PurchaseOutboundOrder purchaseOutboundOrder = new PurchaseOutboundOrder();
        purchaseOutboundOrder.setId(form.getId());
        purchaseOutboundOrder.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        int i = purchaseOutboundOrderService.updateByPrimaryKeySelective(purchaseOutboundOrder);
        if (i == 0) {
            String msg = String.format("删除%s采购退货单操作失败", JSON.toJSONString(form.getPurchaseOutboundOrderCode()));
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }
        //删除商品
        PurchaseOutboundDetail purchaseOutboundDetail = new PurchaseOutboundDetail();
        purchaseOutboundDetail.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        Example example = new Example(PurchaseOutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseOutboundOrderCode", form.getPurchaseOutboundOrderCode());
        purchaseOutboundDetailService.updateByExampleSelective(purchaseOutboundDetail, example);

        //记录操作日志
        logInfoService.recordLog(purchaseOutboundOrder, purchaseOutboundOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.DELETE.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
    }

    /**
     * 对应退货出库通知单的状态=“待通知出库[0]”或“仓库接收失败[2]”或“已取消[5]” 才允许作废操作
     *
     * @param purchaseOutboundNotice
     */
    private void checkNoticeStatus(PurchaseOutboundNotice purchaseOutboundNotice) {
        if (!StringUtils.equals(PurchaseOutboundNoticeStatusEnum.TO_BE_NOTIFIED.getCode(), purchaseOutboundNotice.getStatus())
                && !StringUtils.equals(PurchaseOutboundNoticeStatusEnum.WAREHOUSE_RECEIVE_FAILED.getCode(), purchaseOutboundNotice.getStatus())
                && !StringUtils.equals(PurchaseOutboundNoticeStatusEnum.CANCEL.getCode(), purchaseOutboundNotice.getStatus())) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, String.format("作废失败，采购退货单对应的%s出库单已经被推送给仓储", purchaseOutboundNotice.getOutboundNoticeCode()));
        }
    }

    private void validateParam(PurchaseOutboundItemForm form) {
        AssertUtil.notBlank(form.getSupplierCode(), "供应商不能为空");
        AssertUtil.notBlank(form.getWarehouseInfoId(), "退货仓库不能为空");
        AssertUtil.notBlank(form.getReturnOrderType(), "退货类型不能为空");
    }

    private List<PurchaseOutboundDetail> getDetails(PurchaseOutboundItemForm form, String skus, Pagenation<PurchaseOutboundDetail> page, Pagenation<Skus> pagenation) {

        //是否条件查询的标记
        boolean flag = false;
        if (StringUtils.isNotBlank(form.getSkuCode())
                || StringUtils.isNotBlank(form.getSkuName())
                || StringUtils.isNotBlank(form.getBrandName())
                || StringUtils.isNotBlank(form.getBarCode())) {
            flag = true;
        }

        //查询供应商相关品牌
        SupplierBrand supplierBrand = new SupplierBrand();
        supplierBrand.setSupplierCode(form.getSupplierCode());
        supplierBrand.setIsValid(ValidStateEnum.ENABLE.getCode().toString());
        List<SupplierBrand> supplierBrandList = supplierBrandService.select(supplierBrand);
        AssertUtil.notEmpty(supplierBrandList, String.format("供应商%s没有关联品牌", form.getSupplierCode()));
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> brandIds = new HashSet<>();
        for (SupplierBrand sb : supplierBrandList) {
            categoryIds.add(sb.getCategoryId());
            brandIds.add(sb.getBrandId());
        }

        //查询品牌信息
        Example brandExample = new Example(Brand.class);
        Example.Criteria brandCriteria = brandExample.createCriteria();
        brandCriteria.andIn("id", brandIds);
        if (StringUtils.isNotBlank(form.getBrandName())) {
            brandCriteria.andLike("name", "%" + form.getBrandName() + "%");
        }
        List<Brand> brandList = brandService.selectByExample(brandExample);
        AssertUtil.notEmpty(brandList, String.format("根据品牌ID[%s]批量查询品牌信息为空",
                CommonUtil.converCollectionToString(new ArrayList<>(brandIds))));
        List<String> brandIdList = new ArrayList<>();
        for (Brand brand : brandList) {
            brandIdList.add(brand.getId().toString());
        }

        //查询退货仓库，退货类型对应的sku
        Example warehouseItemExample = new Example(WarehouseItemInfo.class);
        Example.Criteria warehouseItemCriteria = warehouseItemExample.createCriteria();
        warehouseItemCriteria.andEqualTo("warehouseInfoId", form.getWarehouseInfoId());
        warehouseItemCriteria.andEqualTo("noticeStatus", NoticsWarehouseStateEnum.SUCCESS.getCode());
        warehouseItemCriteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
        if (StringUtils.isNotBlank(form.getBarCode())) {
            List<String> barCodes = Arrays.asList(form.getBarCode().split(","));
            String conditionSql = setConditionSql(barCodes);
            warehouseItemCriteria.andCondition(conditionSql);
        }
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(warehouseItemExample);
        if (CollectionUtils.isEmpty(warehouseItemInfoList)) {
            if (!flag) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION,
                        "无数据，请确认【商品管理】中存在所选供应商的品牌的，且所选退货仓库在【仓库信息管理】中“通知仓库状态”为“通知成功”的启用商品！");
            }
            return new ArrayList<>();
        }
        List<String> skuCodeList = warehouseItemInfoList.stream().map(WarehouseItemInfo::getSkuCode).collect(Collectors.toList());

        //查询供应商相关商品
        Example itemExample = new Example(Items.class);
        Example.Criteria itemCriteria = itemExample.createCriteria();
        itemCriteria.andIn("categoryId", categoryIds);
        itemCriteria.andIn("brandId", brandIdList);
        itemCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Items> itemsList = itemsService.selectByExample(itemExample);
        if (CollectionUtils.isEmpty(itemsList)) {
            log.error(String.format("根据分类ID[%s]、品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                    CommonUtil.converCollectionToString(new ArrayList<>(categoryIds)), CommonUtil.converCollectionToString(new ArrayList<>(brandIds)), ValidStateEnum.ENABLE.getName()));
            if (!flag) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "无数据，请确认【商品管理】中存在所选供应商的品牌的，且状态为启用的自采商品！");
            }
            return new ArrayList<>();
        }

        //查询供应商，退货仓库对应sku
        List<Long> itemIds = new ArrayList<>();
        for (Items items : itemsList) {
            itemIds.add(items.getId());
        }
        Example skusExample = new Example(Skus.class);
        Example.Criteria skusCriteria = skusExample.createCriteria();
        skusCriteria.andIn("itemId", itemIds);
        skusCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        if (StringUtils.isNotBlank(form.getSkuCode())) {
            skusCriteria.andIn("skuCode", Arrays.asList(form.getSkuCode().split(",")));
        }
        if (StringUtils.isNotBlank(skus)) {
            skusCriteria.andNotIn("skuCode", Arrays.asList(skus.split(",")));
        }
        if (StringUtils.isNotBlank(form.getSkuName())) {
            skusCriteria.andLike("skuName", "%" + form.getSkuName() + "%");
        }
        skusCriteria.andIn("skuCode", skuCodeList);
        List<Skus> result = null;
        if (pagenation != null) {
            pagenation = skusService.pagination(skusExample, pagenation, new QueryModel());
            page.setTotalCount(pagenation.getTotalCount());
            result = pagenation.getResult();
        } else {
            result = skusService.selectByExample(skusExample);
        }
        if (CollectionUtils.isEmpty(result)) {
            if (!flag) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "无数据，请确认【商品管理】中存在所选供应商的品牌的，且状态为启用的自采商品！");
            }
            return new ArrayList<>();
        }

        return setDetails(result, warehouseItemInfoList, form.getReturnOrderType(), itemsList, brandList);
    }

    private List<PurchaseOutboundDetail> setDetails(List<Skus> result, List<WarehouseItemInfo> warehouseItemInfoList, String returnOrderType, List<Items> itemsList, List<Brand> brandList) {
        List<WarehouseItemInfo> warehouseItemInfos = new ArrayList<>();
        for (Skus sku : result) {
            for (WarehouseItemInfo warehouseItemInfo : warehouseItemInfoList) {
                if (StringUtils.equals(sku.getSkuCode(), warehouseItemInfo.getSkuCode())) {
                    warehouseItemInfos.add(warehouseItemInfo);
                    break;
                }
            }
        }
        Map<String, Long> inventoryInfo = new HashMap<>();
        if (!CollectionUtils.isEmpty(warehouseItemInfos)) {
            //京东接口查询库存信息
            inventoryInfo = skuInventoryQuery(warehouseItemInfos, returnOrderType);
        }

        //初始化退货税率
        Example example = new Example(TaxRate.class);
        example.createCriteria().andEqualTo("taxRateCode", TaxRateEnum.PURCHASE_OUTBOUND_RATE.getCode());
        List<TaxRate> taxRates = taxRateService.selectByExample(example);

        List<PurchaseOutboundDetail> list = new ArrayList<>();
        for (Skus sku : result) {
            PurchaseOutboundDetail detail = new PurchaseOutboundDetail();
            detail.setSpuCode(sku.getSpuCode());
            detail.setSkuCode(sku.getSkuCode());
            detail.setBarCode(sku.getBarCode());
            detail.setSpecNatureInfo(sku.getSpecInfo());
            detail.setSkuName(sku.getSkuName());
            detail.setBrandName(sku.getBrandName());
            detail.setReturnOrderType(returnOrderType);
            if (taxRates != null && !taxRates.isEmpty()) {
                detail.setTaxRate(taxRates.get(0).getTaxRate());
            }
            //设置可退数量
            if (!CollectionUtils.isEmpty(inventoryInfo) && inventoryInfo.get(sku.getSkuCode()) != null) {
                detail.setCanBackQuantity(inventoryInfo.get(sku.getSkuCode()));
            }

            for (WarehouseItemInfo warehouseItemInfo : warehouseItemInfos) {
                if (StringUtils.equals(sku.getSkuCode(), warehouseItemInfo.getSkuCode())) {
                    detail.setItemNo(warehouseItemInfo.getItemNo());
                    break;
                }
            }

            for (Items item : itemsList) {
                if (sku.getItemId().equals(item.getId())) {
                    detail.setCategoryId(String.valueOf(item.getCategoryId()));
                    detail.setBrandId(String.valueOf(item.getBrandId()));
                    for (Brand brand : brandList) {
                        if (item.getBrandId().equals(brand.getId())) {
                            detail.setBrandName(brand.getName());
                            break;
                        }
                    }
                    break;
                }
            }


            list.add(detail);
        }
        return list;
    }

    /**
     * 京东接口查询库存信息
     *
     * @param warehouseItemInfos
     * @param returnOrderType
     * @return
     */
    private Map<String, Long> skuInventoryQuery(List<WarehouseItemInfo> warehouseItemInfos, String returnOrderType) {
        ScmInventoryQueryRequest request = new ScmInventoryQueryRequest();
        commonService.getWarehoueType(warehouseItemInfos.get(0).getWarehouseCode(), request);

        List<ScmInventoryQueryItem> scmInventoryQueryItemList = new ArrayList<>();

        ScmInventoryQueryItem item = null;
        for (WarehouseItemInfo itemInfo : warehouseItemInfos) {
            item = new ScmInventoryQueryItem();
            item.setWarehouseCode(itemInfo.getWmsWarehouseCode());
            item.setInventoryStatus(returnOrderType);//库存状态，枚举值：1.良品；2.残品；3.样品。
            item.setInventoryType(JingdongInventoryTypeEnum.SALE.getCode());// 可销售
            item.setOwnerCode(itemInfo.getWarehouseOwnerId());// 京东仓库需要
            item.setItemCode(itemInfo.getSkuCode());
            item.setItemId(itemInfo.getWarehouseItemId());
            scmInventoryQueryItemList.add(item);
        }
        request.setScmInventoryQueryItemList(scmInventoryQueryItemList);
        AppResult<List<ScmInventoryQueryResponse>> appResult = warehouseApiService.inventoryQuery(request);
        List<ScmInventoryQueryResponse> resList;
        if (StringUtils.equals(ResponseAck.SUCCESS_CODE, appResult.getAppcode())) {
            resList = (List<ScmInventoryQueryResponse>) appResult.getResult();
            log.info("----采购退货，京东查询库存结果:{}", JSON.toJSONString(resList));
            try {
                Map<String, Long> retTempMap = resList.stream()
                        .collect(Collectors.toMap(ScmInventoryQueryResponse::getItemCode, ScmInventoryQueryResponse::getQuantity));

                Map<String, Long> retMap = new HashMap<>();
                for (WarehouseItemInfo itemInfo : warehouseItemInfos) {
                    retTempMap.forEach((k, v) -> {
                        if (StringUtils.equals(k, itemInfo.getSkuCode())) {
                            retMap.put(itemInfo.getSkuCode(), retTempMap.get(itemInfo.getWarehouseItemId()));
                        }
                    });
                }
                return retMap;
            } catch (Exception e) {
                log.error("库存查询返回的格式有误:", e);
                return null;
            }
        } else {
            log.error("采购退货，京东查询库存接口错误:{}", JSON.toJSONString(appResult));
            return null;
        }
    }

    private String setConditionSql(List<String> barCodes) {
        StringBuilder sql = new StringBuilder("(");
        for (String bc : barCodes) {
            sql.append("FIND_IN_SET('" + bc + "', `bar_code`) OR ");
        }
        String substring = sql.substring(0, sql.lastIndexOf(")") + 1);
        return substring + ")";
    }


    private void checkWarehouse(Long warehouseInfoId) {
        if (StringUtils.isNotBlank(String.valueOf(warehouseInfoId))) {
            WarehouseInfo warehouse = new WarehouseInfo();
            warehouse.setId(warehouseInfoId);
            warehouse.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouse = warehouseInfoService.selectOne(warehouse);
            if (ZeroToNineEnum.ZERO.getCode().equals(warehouse.getIsValid())) {
                log.error("仓库:{}已被停用", warehouse.getWarehouseName());
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, warehouse.getWarehouseName() + "仓库已被停用");
            }
        }
    }

    private void validationRequestParam(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(form, "采购退货单数据不能为空");
        AssertUtil.notNull(aclUserAccreditInfo, "用户信息异常");
        AssertUtil.notBlank(form.getSupplierCode(), "供应商不能为空");
        if (!CollectionUtils.isEmpty(form.getPurchaseOutboundDetailList())) {
            form.getPurchaseOutboundDetailList().forEach(purchaseOutboundDetail -> {
                AssertUtil.notNull(purchaseOutboundDetail.getSpecNatureInfo(), "商品规格不能为空");
                AssertUtil.notNull(purchaseOutboundDetail.getItemNo(), "商品货号不能为空");
                AssertUtil.notNull(purchaseOutboundDetail.getBarCode(), "商品条形码不能为空");
                AssertUtil.notNull(purchaseOutboundDetail.getSkuCode(), "商品sku编码不能为空");
                AssertUtil.notNull(purchaseOutboundDetail.getSkuName(), "商品sku名称不能为空");
                if (purchaseOutboundDetail.getPrice() != null && purchaseOutboundDetail.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货商品含税单价不能小于0");
                }
            });
        }
    }

    /**
     * 插入采购退货单和退货详情
     *
     * @param form                表单数据
     * @param code                保存类型 0-暂存 1-提交审核
     * @param aclUserAccreditInfo
     * @param seq                 采购退货通知单编号
     */
    private void insertPurchaseOutboundOrderAndDetail(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo, String seq) {
        BigDecimal totalAmount = new BigDecimal(0);
        form.setChannelCode(aclUserAccreditInfo.getChannelCode());
        form.setPurchaseOutboundOrderCode(seq);
        form.setIsValid(ValidEnum.VALID.getCode());
        form.setStatus(code);
        form.setCreateOperator(aclUserAccreditInfo.getUserId());
        if (!CollectionUtils.isEmpty(form.getPurchaseOutboundDetailList())) {
            List<PurchaseOutboundDetail> purchaseOutboundDetailList = form.getPurchaseOutboundDetailList();
            for (PurchaseOutboundDetail purchaseOutboundDetail : purchaseOutboundDetailList) {
                totalAmount = totalAmount.add(purchaseOutboundDetail.getTotalAmount());
            }
        }
        form.setTotalFee(totalAmount.setScale(3, RoundingMode.HALF_UP));
        int count = purchaseOutboundOrderService.insert(form);
        if (count < 1) {
            throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "保存采购退货单失败");
        }
        insertPurchaseOutboundDetail(form);

        //记录操作日志
        String userId = aclUserAccreditInfo.getUserId();
        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.HOLD.getCode(), code)) {
            logInfoService.recordLog(form, form.getId().toString(), userId, LogOperationEnum.ADD.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
        }
    }

    private void insertPurchaseOutboundDetail(PurchaseOutboundOrder form) {
        List<PurchaseOutboundDetail> purchaseOutboundDetailList = form.getPurchaseOutboundDetailList();
        if (!CollectionUtils.isEmpty(purchaseOutboundDetailList)) {
            for (PurchaseOutboundDetail purchaseOutboundDetail : purchaseOutboundDetailList) {
                Skus skus = new Skus();
                skus.setSkuCode(purchaseOutboundDetail.getSkuCode());
                skus.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                skus = skusService.selectOne(skus);
                AssertUtil.notNull(skus, "没有此商品信息");
                if (ZeroToNineEnum.ZERO.getCode().equals(skus.getIsValid())) {
                    String msg = String.format("商品%s已停用", purchaseOutboundDetail.getSkuCode());
                    log.error(msg);
                    throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, msg);
                }

                BigDecimal totalAmount = null;
                if (purchaseOutboundDetail.getPrice() != null && purchaseOutboundDetail.getOutboundQuantity() > 0) {
                    //单价*数量
                    totalAmount = purchaseOutboundDetail.getPrice().multiply(new BigDecimal(purchaseOutboundDetail.getOutboundQuantity()));
                }

                purchaseOutboundDetail.setPurchaseOutboundOrderCode(form.getStatus());
                purchaseOutboundDetail.setPrice(purchaseOutboundDetail.getPrice() == null ? null : purchaseOutboundDetail.getPrice().setScale(3, RoundingMode.HALF_UP));
                purchaseOutboundDetail.setTotalAmount(totalAmount == null ? null : totalAmount.setScale(3, RoundingMode.HALF_UP));
                purchaseOutboundDetail.setCreateOperator(form.getCreateOperator());
                ParamsUtil.setBaseDO(purchaseOutboundDetail);
            }
            int i = purchaseOutboundDetailService.insertList(purchaseOutboundDetailList);
            if (i < 1) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "保存采购退货单详情异常");
            }
        }
    }

    /**
     * 提交审核校验必填参数
     *
     * @param form
     */
    private void validationParam(PurchaseOutboundOrder form) {
        AssertUtil.notBlank(form.getSupplierCode(), "供应商code不能为空");
        AssertUtil.notNull(form.getWarehouseInfoId(), "退货仓库不能为空");
        AssertUtil.notBlank(form.getReturnOrderType(), "退货类型不能为空");
        AssertUtil.notBlank(form.getPickType(), "提货方式不能为空");
        AssertUtil.notBlank(form.getReceiver(), "退货收货人不能为空");
        AssertUtil.notBlank(form.getReceiverNumber(), "收货人手机号不能为空");
        AssertUtil.notBlank(form.getReturnPolicy(), "退货说明不能为空");
        AssertUtil.notEmpty(form.getPurchaseOutboundDetailList(), "退货商品不能为空");

        //当提货方式为“物流配送”时必填
        if (StringUtils.equals(PickTypeEnum.OTHER_DELIVERY.getCode(), form.getPickType())) {
            AssertUtil.notBlank(form.getReceiverProvince(), "退货省份不能为空");
            AssertUtil.notBlank(form.getReceiverCity(), "退货城市不能为空");
            AssertUtil.notBlank(form.getReceiverArea(), "退货地区不能为空");
            AssertUtil.notBlank(form.getReceiverAddress(), "退货详细地址不能为空");
        }

        for (PurchaseOutboundDetail purchaseOutboundDetail : form.getPurchaseOutboundDetailList()) {

            if (purchaseOutboundDetail.getOutboundQuantity() == null || purchaseOutboundDetail.getOutboundQuantity() < 1) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货数量不能为空，且不能小于0");
            }

            //退货数量不能大于可退数量
            if (purchaseOutboundDetail.getOutboundQuantity() > purchaseOutboundDetail.getCanBackQuantity()) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货数量不能大于当前可退数量");
            }

            if (purchaseOutboundDetail.getTaxRate() == null
                    || purchaseOutboundDetail.getTaxRate().doubleValue() < 0
                    || purchaseOutboundDetail.getTaxRate().doubleValue() > 100) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货税率不能为空，且在0~100");
            }

            if (purchaseOutboundDetail.getPrice() == null || purchaseOutboundDetail.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货含税单价不能为空，且不能小于0");
            }
        }

    }

    private void setSupplierName(Pagenation<PurchaseOutboundOrder> pagination) {
        pagination.getResult().forEach(purchaseOutboundOrder -> {
            if (StringUtils.isNotBlank(purchaseOutboundOrder.getSupplierCode())) {
                Supplier supplier = new Supplier();
                supplier.setSupplierCode(purchaseOutboundOrder.getSupplierCode());
                Supplier entitySupplier = supplierService.selectOne(supplier);
                if (null == entitySupplier) {
                    log.error(String.format("根据供应商编码%s查询供应商信息为空", purchaseOutboundOrder.getSupplierCode()));
                } else {
                    purchaseOutboundOrder.setSupplierName(entitySupplier.getSupplierName());
                }
                //仓库名称
                WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(purchaseOutboundOrder.getWarehouseInfoId());
                purchaseOutboundOrder.setWarehouseName(warehouseInfo.getWarehouseName());
            }
        });
    }

    private Example setSelectCondition(PurchaseOutboundOrderForm form, String channelCode) {

        Example example = new Example(PurchaseOutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("channelCode", channelCode);

        if (StringUtils.isNotBlank(form.getPurchaseOutboundOrderCode())) {
            criteria.andLike("purchaseOutboundOrderCode", "%" + form.getPurchaseOutboundOrderCode() + "%");
        }

        //供应商名称
        if (StringUtils.isNotBlank(form.getSupplierCode())) {
            criteria.andEqualTo("supplierCode", form.getSupplierCode());
        }

        //出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他
        if (StringUtils.isNotBlank(form.getOutboundStatus())) {
            if (StringUtils.equals(form.getOutboundStatus(), PurchaseOutboundStatusEnum.OTHER.getCode())) {
                criteria.andCondition("outbound_status = '' OR outbound_status is null");
            } else {
                criteria.andEqualTo("outboundStatus", form.getOutboundStatus());
            }
        }

        //退货类型1-正品，2-残品
        if (StringUtils.isNotBlank(form.getReturnOrderType())) {
            criteria.andEqualTo("returnOrderType", form.getReturnOrderType());
        }

        //单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废
        if (StringUtils.isNotBlank(form.getStatus())) {
            criteria.andEqualTo("status", form.getStatus());
        }

        //退货仓库
        if (StringUtils.isNotBlank(form.getWarehouseInfoId())) {
            criteria.andEqualTo("warehouseInfoId", form.getWarehouseInfoId());
        }

        if (StringUtils.isNotBlank(form.getStartDate())) {
            criteria.andGreaterThan("updateTime", form.getStartDate());
        }
        if (StringUtils.isNotBlank(form.getEndDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
            Date date = null;
            try {
                date = sdf.parse(form.getEndDate());
            } catch (ParseException e) {
                String msg = "采购订单列表查询,截止日期的格式不正确";
                log.error(msg);
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_QUERY_EXCEPTION, msg);
            }
            date = DateUtils.addDays(date, 1);
            form.setEndDate(sdf.format(date));
            criteria.andLessThan("updateTime", form.getEndDate());
        }
        criteria.andEqualTo("isDeleted", "0");
        example.setOrderByClause("instr('0,2,1,3,4,5',`status`) ASC");
        example.orderBy("updateTime").desc();
        return example;
    }
}
