package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.*;
import org.trc.enums.purchase.PurchaseOutboundOrderStatusEnum;
import org.trc.exception.PurchaseOutboundOrderException;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.service.category.IBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemsService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import org.trc.service.purchase.IPurchaseOutboundOrderService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.*;
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
    private ICategoryBiz categoryBiz;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private ItemsService itemsService;

    /**
     * 查询采购退货单列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    @Override
    public Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPageList(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode) {
        AssertUtil.notBlank(channelCode, "未获得授权");
        Example example = setSelectCondition(form, channelCode);
        if (example != null) {
            Pagenation<PurchaseOutboundOrder> pagination = purchaseOutboundOrderService.pagination(example, page, new QueryModel());
            if (CollectionUtils.isEmpty(pagination.getResult())) {
                return pagination;
            }
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
    //@PurchaseOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //public void savePurchaseOutboundOrder(PurchaseOutboundOrderDataForm form, String code, AclUserAccreditInfo property) {
    public void savePurchaseOutboundOrder(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo) {
        log.info("采购退货单保存或提交审核，PurchaseOutboundOrder:{}, 当前操作:{} ", JSON.toJSONString(form), code);
        validationRequestParam(form);
        //校验仓库是否停用
        checkWarehouse(form.getWarehouseId());

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

        //保存提交审核
    }

    /**
     * 更新采购退货单
     *
     * @param form                表单数据
     * @param aclUserAccreditInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePurchaseOutboundOrder(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo) {
        log.info("更新采购退货单，PurchaseOutboundOrder:{}", JSON.toJSONString(form));
        AssertUtil.notNull(form, "修改采购退货单失败,采购退货单为空");

        //校验仓库是否停用
        this.checkWarehouse(form.getWarehouseId());
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
        purchaseOutboundOrder.setPurchaseOutboundDetailList(purchaseOutboundDetails);
        return purchaseOutboundOrder;
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
    public Pagenation<PurchaseOutboundDetail> getPurchaseOutboundOrderDetail(PurchaseOutboundItemForm form, Pagenation<PurchaseDetail> page, String skus) {
        log.info("------getPurchaseOutboundOrderDetail, form:{}", JSON.toJSONString(form));
        AssertUtil.notBlank(form.getSupplierCode(), "供应商不能为空");
        AssertUtil.notBlank(form.getWarehouseInfoId(), "退货仓库不能为空");
        AssertUtil.notBlank(form.getReturnOrderType(), "退货类型不能为空");
        Pagenation<PurchaseOutboundDetail> pagenation = new Pagenation();
        pagenation.setStart(page.getStart());
        pagenation.setPageSize(page.getPageSize());
        pagenation.setPageNo(page.getPageNo());
        List<PurchaseOutboundDetail> list = getPurchaseOutboundOrderDetails(form, skus, pagenation);
        return null;
    }

    private List<PurchaseOutboundDetail> getPurchaseOutboundOrderDetails(PurchaseOutboundItemForm form, String skus, Pagenation<PurchaseOutboundDetail> pagenation) {

        //是否条件查询的标记
        boolean flag = false;
        if(StringUtils.isNotBlank(form.getSkuCode())
                || StringUtils.isNotBlank(form.getSkuName())
                || StringUtils.isNotBlank(form.getBrandName())
                || StringUtils.isNotBlank(form.getBarCode()) ){
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
        for(SupplierBrand sb: supplierBrandList){
            categoryIds.add(sb.getCategoryId());
            brandIds.add(sb.getBrandId());
        }

        //查询品牌信息
        Example brandExample = new Example(Brand.class);
        Example.Criteria brandCriteria = brandExample.createCriteria();
        brandCriteria.andIn("id", brandIds);
        if(StringUtils.isNotBlank(form.getBrandName())){
            brandCriteria.andLike("name", "%" + form.getBrandName() + "%");
        }
        List<Brand> brandList = brandService.selectByExample(brandExample);
        AssertUtil.notEmpty(brandList, String.format("根据品牌ID[%s]批量查询品牌信息为空",
                CommonUtil.converCollectionToString(new ArrayList<>(brandIds))));
        List<String> brandIdList = new ArrayList<>();
        for(Brand brand: brandList){
            brandIdList.add(brand.getId().toString());
        }

        //查询退货仓库，退货类型对应的sku
        //查询仓库商品信息
        List<WarehouseItemInfo> warehouseItemInfoList = new ArrayList<>();
        Example warehouseItemExample = new Example(WarehouseItemInfo.class);
        Example.Criteria warehouseItemCriteria = warehouseItemExample.createCriteria();
        warehouseItemCriteria.andEqualTo("warehouseInfoId", form.getWarehouseInfoId());
        warehouseItemCriteria.andEqualTo("noticeStatus", NoticsWarehouseStateEnum.SUCCESS.getCode());
        warehouseItemCriteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());

        if(CollectionUtils.isEmpty(warehouseItemInfoList)){
            if(!flag){
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION,
                        "无数据，请确认【商品管理】中存在所选供应商的品牌的，且所选收货仓库在【仓库信息管理】中“通知仓库状态”为“通知成功”的启用商品！");
            }
            return new ArrayList<PurchaseOutboundDetail>();
        }


        //查询供应商相关商品
        Example itemExample = new Example(Items.class);
        Example.Criteria itemCriteria = itemExample.createCriteria();
        itemCriteria.andIn("categoryId", categoryIds);
        itemCriteria.andIn("brandId", brandIdList);
        itemCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Items> itemsList = itemsService.selectByExample(itemExample);
        if(CollectionUtils.isEmpty(itemsList)){
            log.error(String.format("根据分类ID[%s]、品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                    CommonUtil.converCollectionToString(new ArrayList<>(categoryIds)), CommonUtil.converCollectionToString(new ArrayList<>(brandIds)), ValidStateEnum.ENABLE.getName()));
            if(!flag){
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "无数据，请确认【商品管理】中存在所选供应商的品牌的，且状态为启用的自采商品！");
            }
            return new ArrayList<PurchaseOutboundDetail>();
        }

        //查询供应商相关SKU
        List<Long> itemIds = new ArrayList<>();
        for(Items items: itemsList){
            itemIds.add(items.getId());
        }
        Example skusExample = new Example(Skus.class);
        Example.Criteria skusCriteria = skusExample.createCriteria();
        skusCriteria.andIn("itemId", itemIds);
        skusCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        if(StringUtils.isNotBlank(form.getSkuCode())){
            skusCriteria.andIn("skuCode", Arrays.asList(form.getSkuCode().split(",")));
        }
        if(StringUtils.isNotBlank(form.getSkuName())){
            skusCriteria.andLike("skuName", "%" + form.getSkuName() + "%");
        }
        List<Skus> skusList = skusService.selectByExample(skusExample);
        if(CollectionUtils.isEmpty(skusList)){
            if(!flag){
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, "无数据，请确认【商品管理】中存在所选供应商的品牌的，且状态为启用的自采商品！");
            }
            return new ArrayList<PurchaseOutboundDetail>();
        }


        return null;
    }


    private void checkWarehouse(String warehouseId) {
        if (StringUtils.isNotBlank(warehouseId)) {
            WarehouseInfo warehouse = new WarehouseInfo();
            warehouse.setId(Long.valueOf(warehouseId));
            warehouse.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouse = warehouseInfoService.selectOne(warehouse);
            if (ZeroToNineEnum.ZERO.getCode().equals(warehouse.getIsValid())) {
                log.error("仓库:{}已被停用", warehouse.getWarehouseName());
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_EXCEPTION, warehouse.getWarehouseName() + "仓库已被停用");
            }
        }
    }

    private void validationRequestParam(PurchaseOutboundOrder form) {
        AssertUtil.notNull(form, "采购退货单数据不能为空");
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
        //form.setCreateOperator(aclUserAccreditInfo.getUserId());
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
        logInfoService.recordLog(form, form.getId().toString(), userId, LogOperationEnum.ADD.getMessage(), null, ZeroToNineEnum.ZERO.getCode());

        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), code)) {
            logInfoService.recordLog(form, form.getId().toString(), userId, AuditStatusEnum.COMMIT.getName(), null, ZeroToNineEnum.ZERO.getCode());
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
        AssertUtil.notNull(form.getReturnOrderType(), "退货类型不能为空");
        AssertUtil.notNull(form.getPickType(), "提货方式不能为空");
        AssertUtil.notNull(form.getReceiver(), "退货收货人不能为空");
        AssertUtil.notNull(form.getReceiverNumber(), "收货人手机号不能为空");
        AssertUtil.notNull(form.getReturnPolicy(), "退货说明不能为空");
        AssertUtil.notEmpty(form.getPurchaseOutboundDetailList(), "退货商品不能为空");

        for (PurchaseOutboundDetail purchaseOutboundDetail : form.getPurchaseOutboundDetailList()) {

            if (purchaseOutboundDetail.getOutboundQuantity() == null || purchaseOutboundDetail.getOutboundQuantity() < 1) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货数量不能小于0");
            }
            if (purchaseOutboundDetail.getTaxRate() == null
                    || purchaseOutboundDetail.getTaxRate().doubleValue() < 0
                    || purchaseOutboundDetail.getTaxRate().doubleValue() > 100) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货税率在0~100");
            }

            if (purchaseOutboundDetail.getPrice() == null || purchaseOutboundDetail.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货含税单价不能小于0");
            }

            //退货数量不能大于可退数量
            if (purchaseOutboundDetail.getOutboundQuantity() > purchaseOutboundDetail.getCanBackQuantity()) {
                throw new PurchaseOutboundOrderException(ExceptionEnum.PURCHASE_OUTBOUND_ORDER_PARAM_VALIDATION_EXCEPTION, "采购退货单退货数量不能大于当前可退数量");
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
        if (StringUtils.isNotBlank(form.getSupplierName())) {
            List<Supplier> suppliers = supplierService.selectSupplierByName(form.getSupplierName());
            if (CollectionUtils.isEmpty(suppliers)) {
                return null;
            }
            List<String> supplierCodes = suppliers.stream().map(Supplier::getSupplierCode).collect(Collectors.toList());
            criteria.andIn("supplierCode", supplierCodes);
        }

        //出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他
        if (StringUtils.isNotBlank(form.getOutboundStatus())) {
            criteria.andEqualTo("outboundStatus", form.getOutboundStatus());
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
        if (StringUtils.isNotBlank(form.getWarehouseName())) {
            criteria.andLike("warehouseName", "%" + form.getWarehouseName() + "%");
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
