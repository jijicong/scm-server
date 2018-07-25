package org.trc.biz.impl.purchase;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.SequenceEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.purchase.PurchaseOutboundOrderStatusEnum;
import org.trc.exception.PurchaseOrderException;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.service.purchase.IPurchaseOutboundOrderService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private ISupplierService supplierService;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

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
        }
        List<PurchaseOutboundOrder> purchaseOutboundOrderList = new ArrayList<>();
        page.setResult(purchaseOutboundOrderList);
        page.setTotalCount(0);
        return page;
    }

    /**
     * 采购退货单保存或
     *
     * @param form                采购退货单数据
     * @param code                保存类型
     * @param aclUserAccreditInfo
     */
    @Override
    //@PurchaseOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //public void savePurchaseOutboundOrder(PurchaseOutboundOrderDataForm form, String code, AclUserAccreditInfo property) {
    public void savePurchaseOutboundOrder(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo) {
        validationRequestParam(form);
        ParamsUtil.setBaseDO(form);

        //校验仓库是否停用
        checkWarehouse(form.getWarehouseId());

        //提交审核校验必填参数
        if (StringUtils.equals(PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), code)) {
            validationParam(form);
        }
        String seq = serialUtilService.generateCode(LENGTH, SequenceEnum.CGTH_PREFIX.getCode(), DateUtils.dateToCompactString(form.getCreateTime()));
        AssertUtil.notBlank(code, "获取编码失败");

        insertPurchaseOutboundOrderAndDetail(form, code, aclUserAccreditInfo, seq);


    }

    private void checkWarehouse(String warehouseId) {
        if (StringUtils.isNotBlank(warehouseId)) {
            WarehouseInfo warehouse = new WarehouseInfo();
            warehouse.setId(Long.valueOf(warehouseId));
            warehouse.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouse = warehouseInfoService.selectOne(warehouse);
            if (ZeroToNineEnum.ZERO.getCode().equals(warehouse.getIsValid())) {
                String msg = String.format("仓库%s已被停用，请重新选择！", warehouse.getWarehouseName());
                log.error(msg);
                //throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
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
                if (purchaseOutboundDetail.getPrice() == null || purchaseOutboundDetail.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    //TODO 异常
                }
            });
        }
    }

    /**
     * 插入采购退货单和退货详情
     *
     * @param form
     * @param code
     * @param aclUserAccreditInfo
     * @param seq
     */
    private void insertPurchaseOutboundOrderAndDetail(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo, String seq) {
        BigDecimal totalAmount = new BigDecimal(0);
        form.setChannelCode(aclUserAccreditInfo.getChannelCode());
        form.setPurchaseOutboundOrderCode(seq);
        form.setIsValid(ValidEnum.VALID.getCode());
        if (!CollectionUtils.isEmpty(form.getPurchaseOutboundDetailList())) {
            List<PurchaseOutboundDetail> purchaseOutboundDetailList = form.getPurchaseOutboundDetailList();
            for (PurchaseOutboundDetail purchaseOutboundDetail : purchaseOutboundDetailList) {
                totalAmount = totalAmount.add(purchaseOutboundDetail.getPrice());
            }
        }
        form.setTotalFee(totalAmount.setScale(3, RoundingMode.HALF_UP));
        int count = purchaseOutboundOrderService.insert(form);
        if (count < 1) {
            // TODO 抛出异常
        }


    }

    /**
     * TODO 提交审核校验必填参数
     *
     * @param form
     */
    private void validationParam(PurchaseOutboundOrder form) {
        AssertUtil.notNull(form.getReturnOrderType(), "退货类型不能为空");
        AssertUtil.notNull(form.getPickType(), "提货方式不能为空");
        AssertUtil.notNull(form.getReceiver(), "退货收货人不能为空");
        AssertUtil.notNull(form.getReceiverNumber(), "收货人手机号不能为空");
        AssertUtil.notNull(form.getReturnPolicy(), "退货说明不能为空");
        if (CollectionUtils.isEmpty(form.getPurchaseOutboundDetailList())) {
            // TODO 抛出异常
        }

        for (PurchaseOutboundDetail purchaseOutboundDetail : form.getPurchaseOutboundDetailList()) {
            if (purchaseOutboundDetail.getPrice() == null) {
                // TODO 抛出异常
            }
            if (purchaseOutboundDetail.getOutboundQuantity() == null || purchaseOutboundDetail.getOutboundQuantity() < 1) {

            }
            if (purchaseOutboundDetail.getTaxRate() == null || purchaseOutboundDetail.getTaxRate().compareTo(BigDecimal.ZERO) < 0) {

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
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION, msg);
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
