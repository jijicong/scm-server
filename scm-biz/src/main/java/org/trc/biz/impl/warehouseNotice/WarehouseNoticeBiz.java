package org.trc.biz.impl.warehouseNotice;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.impl.purchase.PurchaseOrderAuditBiz;
import org.trc.biz.purchase.IPurchaseDetailBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.util.CommonDO;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.*;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, Pagenation<WarehouseNotice> page,ContainerRequestContext requestContext) {

        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj,"查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo=(AclUserAccreditInfo)obj;
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
    /*private Example setCriterias(WarehouseNoticeForm form){
        Example example  = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(form.getWarehouseNoticeCode())){
            criteria.andLike("warehouseNoticeCode",form.getWarehouseNoticeCode());
        }
        if(StringUtils.isNotBlank(form.getPurchaseOrderCode())){
            criteria.andLike("purchaseOrderCode",form.getPurchaseOrderCode());
        }
        if(StringUtils.isNotBlank(form.getPurchaseType())){
            criteria.andEqualTo("purchaseType",form.getPurchaseType());
        }
        if(StringUtils.isNotBlank(form.getWarehouseNoticeStatus())){
            criteria.andEqualTo("status",form.getWarehouseNoticeStatus());
        }
        if(StringUtils.isNotBlank(form.getSupplierName())){
            Example example2  = new Example(WarehouseNotice.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andLike("supplierName",form.getSupplierName());
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
        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThan("updateTime", form.getStartDate());
        }
        if (!StringUtils.isBlank(form.getEndDate())) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
            Date date = null;
            try {
                date = sdf.parse(form.getEndDate());
            }catch (ParseException e){
                String msg = "采购订单列表查询,截止日期的格式不正确";
                logger.error(msg);
                throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION,msg);
            }
            date =DateUtils.addDays(date,2);
            form.setEndDate(sdf.format(date));
            criteria.andLessThan("updateTime", form.getEndDate());
        }
        return example;
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiptAdvice(WarehouseNotice warehouseNotice,ContainerRequestContext requestContext) {
        /*
        执行通知收货，关联的操作
            1.更改采购单的是否通知入库 为已通知（1）
           2.更改入库通知单的状态 为待仓库反馈（1）
           3.调用仓储的入库通知的接口，给仓库发入库通知单
                {
                此时，生成入库通知明细
                该入库通知明细，只所以没有在生成scm的入库通知单的时候，生成。
                是因为，如果该入库通知，在没有执行入库通知入库的条件下，是可以被采购单管理页面给（作废掉），这种情况下，生成的入库通知明细是（无用的）
                }
          */
        AssertUtil.notNull(warehouseNotice,"入库通知的信息为空");
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.HAVE_NOTIFIED.getCode());
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
        WarehouseNotice warehouseNotice1 = new WarehouseNotice();
        warehouseNotice1.setStatus(WarehouseNoticeStatusEnum.ON_WAREHOUSE_TICKLING.getCode());
        int num = warehouseNoticeService.updateByExampleSelective(warehouseNotice1,warehouseNoticeExample);
        if(num != 1){
            String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]的状态已不符合修改条件,无法进行入库通知的操作",warehouseNotice.getWarehouseNoticeCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseOrderCode(warehouseNotice.getPurchaseOrderCode());
        List<PurchaseDetail> purchaseDetails = purchaseDetailService.select(purchaseDetail);
        if(CollectionUtils.isEmpty(purchaseDetails)){
            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态没有查到对应的采购商品,请核实该入库明细",warehouseNotice.getPurchaseOrderCode());
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }
        insertWarehouseNoticeDetail(purchaseDetails,warehouseNotice.getWarehouseNoticeCode());
        //todo
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        logInfoService.recordLog(warehouseNotice,warehouseNotice.getId().toString(),userId, LogOperationEnum.NOTICE_RECEIVE.getMessage(),null,null);
        logInfoService.recordLog(purchaseOrder,purchaseOrders.get(0).getId().toString(),userId, LogOperationEnum.NOTICE_RECEIVE.getMessage(),null,null);

    }

    private void insertWarehouseNoticeDetail(List<PurchaseDetail> purchaseDetailList , String warehouseNoticeCode){

        List<WarehouseNoticeDetails> warehouseNoticeDetails = new ArrayList<WarehouseNoticeDetails>();

        for (PurchaseDetail purchaseDetail: purchaseDetailList) {
            WarehouseNoticeDetails details = new WarehouseNoticeDetails();
            details.setWarehouseNoticeCode(warehouseNoticeCode);
            details.setBrandId(purchaseDetail.getBrandId());
            details.setCategoryId(purchaseDetail.getCategoryId());
            details.setSkuCode(purchaseDetail.getSkuCode());
            details.setSkuName(purchaseDetail.getItemName());
            details.setActualStorageQuantity(0L);//初始化0
            details.setPurchasingQuantity(purchaseDetail.getPurchasingQuantity());
            details.setCreateTime(Calendar.getInstance().getTime());
            details.setPurchasePrice(purchaseDetail.getPurchasePrice());
            details.setStorageTime(details.getCreateTime());
            warehouseNoticeDetails.add(details);
        }
        int count = warehouseNoticeDetailsService.insertList(warehouseNoticeDetails);
        if(count < 1){
            String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]保存入库通知明细初始化失败,无法进行入库通知的操作",warehouseNoticeCode);
            logger.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
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

        return null;
    }
}
