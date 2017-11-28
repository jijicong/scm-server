package org.trc.biz.impl.warehouseNotice;


import com.alibaba.fastjson.JSON;
import com.qimen.api.request.EntryorderConfirmRequest;
import com.qiniu.util.Json;
import net.sf.json.xml.XMLSerializer;
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
import org.trc.service.System.IWarehouseService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
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



    /**
     * 入库通知单分页查询
     * @param form form表单查询条件
     * @param page 分页查询的条件
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
        //采购类型
        if (!StringUtils.isBlank(form.getPurchaseType())) {
            criteria.andEqualTo("purchaseType", form.getPurchaseType());
        }
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", String.valueOf(form.getStatus()));
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
            criteria.andLike("supplierName", "%" + form.getSupplierName() + "%");
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

        return page;
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
     * @param requestText
     */
    @Override
    public void updateInStock(String requestText) {
        AssertUtil.notBlank(requestText,"获取奇门返回信息为空!");
        EntryorderConfirmRequest confirmRequest = null;
        try {
            //创建 XMLSerializer对象
            XMLSerializer xmlSerializer = new XMLSerializer();
            //将xml转为json（注：如果是元素的属性，会在json里的key前加一个@标识）
            String result = xmlSerializer.read(requestText).toString();
            confirmRequest = JSON.parseObject(result,EntryorderConfirmRequest.class);
        }catch (Exception e){
            logger.error("Bean转换异常!");
        }
        if (null!=confirmRequest){
            //获取entryOrder 入库单信息
            //获取senderInfo 发件人信息
            //获取orderLines 入库单详情
            List<EntryorderConfirmRequest.OrderLine> orderLineList=confirmRequest.getOrderLines();
            //修改库存
            if (!AssertUtil.collectionIsEmpty(orderLineList)){
                for (EntryorderConfirmRequest.OrderLine orderLine:orderLineList ) {
                    //入库通知单详情表
                    WarehouseNoticeDetails warehouseNoticeDetails = new WarehouseNoticeDetails();
                    //获取业务线,库存表
                    SkuStock skuStock = new SkuStock();
                    //货主ID
                    skuStock.setChannelCode(orderLine.getOwnerCode());
                    warehouseNoticeDetails.setOwnerCode(orderLine.getOwnerCode());
                    //获取商品SKU
                    skuStock.setSkuCode(orderLine.getItemCode());
                    warehouseNoticeDetails.setSkuCode(orderLine.getItemCode());
                    //获取仓储系统商品ID
                    skuStock.setWarehouseItemId(orderLine.getItemId());
                    warehouseNoticeDetails.setItemId(orderLine.getItemId());
                    //应收商品数量(采购数量)
                    warehouseNoticeDetails.setPurchasingQuantity(orderLine.getPlanQty());
                    //“ZP”，正品,“ZP”，正品

                    //商品生产日期
                    Date productionDate = DateUtils.parseDate(orderLine.getProductDate());
                    warehouseNoticeDetails.setProductionDate(productionDate);
                    //截止保质日期
                    Date expiredDate = DateUtils.parseDate(orderLine.getExpireDate());
                    warehouseNoticeDetails.setExpiredDate(expiredDate);
                    //计算理论保质期
                    warehouseNoticeDetails.setExpiredDay(DateUtils.differentDays(expiredDate,productionDate));
                    //produceCode 生产批号,生产编码
                    warehouseNoticeDetails.setProductionCode(orderLine.getProduceCode());
                    //批次号
                    warehouseNoticeDetails.setBatchNo(orderLine.getBatchCode());

                    //计算库存
                    //获取数据库中当前库存
                    SkuStock nowStock = new SkuStock();
                    nowStock.setSkuCode(skuStock.getSkuCode());
                    nowStock.setChannelCode(skuStock.getChannelCode());
                    nowStock.setWarehouseCode(getItemId(orderLine));
                    nowStock = skuStockService.selectOne(nowStock);
                    if (null!=nowStock){
                        //计算库存
                        //采购数量
                        Long purchaserCount=orderLine.getPlanQty()==null?0:orderLine.getPlanQty();
                        //实收=正品入库数量
                        Long actualQtyCount = orderLine.getActualQty()==null?0:orderLine.getActualQty();
                        //本期只考虑正品数量
                    }
                }
            }
        }
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
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.selectByExample(example);
        if (CollectionUtils.isEmpty(purchaseOrders)) {
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, "查询采购单失败！");
        }
        int count = purchaseOrderService.updateByExampleSelective(purchaseOrder, example);

        if (count != 1) {
            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态已作废,无法进行入库通知的操作", warehouseNotice.getPurchaseOrderCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }

        Example warehouseNoticeExample = new Example(WarehouseNotice.class);
        Example.Criteria warehouseNoticeCriteria = warehouseNoticeExample.createCriteria();
        warehouseNoticeCriteria.andEqualTo("warehouseNoticeCode", warehouseNotice.getWarehouseNoticeCode());
        warehouseNoticeCriteria.andEqualTo("status", WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
        WarehouseNotice warehouseNotice1 = new WarehouseNotice();
        warehouseNotice1.setStatus(WarehouseNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode());
        warehouseNotice1.setUpdateTime(Calendar.getInstance().getTime());
        int num = warehouseNoticeService.updateByExampleSelective(warehouseNotice1, warehouseNoticeExample);
        if (num != 1) {
            String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]的状态已不符合修改条件,无法进行入库通知的操作", warehouseNotice.getWarehouseNoticeCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(warehouseNotice, warehouseNotice.getId().toString(), userId, LogOperationEnum.NOTICE_RECEIVE.getMessage(), null, null);
        logInfoService.recordLog(purchaseOrder, purchaseOrders.get(0).getId().toString(), userId, LogOperationEnum.NOTICE_RECEIVE.getMessage(), null, null);
        purchaseOrderBiz.cacheEvitForPurchaseOrder();

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
            //价格转化成元
            warehouseNoticeDetails.setPurchasePriceT(new BigDecimal(warehouseNoticeDetails.getPurchasePrice()).divide(new BigDecimal(100)));

        }

    }

    private String getItemId(EntryorderConfirmRequest.OrderLine orderLine){
        //ownerCode 货主编码,itemId 仓储系统商品ID 需要转换成本地对应的YWX,itemId
       String itemId = orderLine.getItemId();
       if (!StringUtils.isBlank(itemId)){
            Warehouse warehouse  = new Warehouse();
            warehouse.setQimenWarehouseCode(itemId);
            warehouse = warehouseService.selectOne(warehouse);
           if (null!=warehouse){
               return warehouse.getCode();
           }
       }
        return "";
    }
}
