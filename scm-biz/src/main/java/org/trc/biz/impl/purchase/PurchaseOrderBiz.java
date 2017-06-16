package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.category.Category;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.*;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseOrderBiz")
public class PurchaseOrderBiz implements IPurchaseOrderBiz{

    private Logger  LOGGER = LoggerFactory.getLogger(PurchaseOrderBiz.class);
    @Resource
    private IPurchaseOrderService purchaseOrderService;
    @Resource
    private IPurchaseDetailService purchaseDetailService;
    @Resource
    private IUserAccreditInfoService userAccreditInfoService ;
    @Resource
    private ISupplierService supplierService;
    @Resource
    private IPurchaseGroupService purchaseGroupService;
    @Resource
    private IWarehouseService warehouseService;

    private final static String  SERIALNAME = "CGD";

    private final static Integer LENGTH = 5;

    @Resource
    private ISerialUtilService serialUtilService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form, Pagenation<PurchaseOrder> page) throws Exception {

        Example example = setCondition(form);
        Pagenation<PurchaseOrder> pagenation = purchaseOrderService.pagination(example,page,form);

        List<PurchaseOrder> purchaseOrderList = pagenation.getResult();
        if( purchaseOrderList==null  ){
            return pagenation;
        }
        if(purchaseOrderList.size()==0 ){
            return pagenation;
        }
        purchaseOrderList = selectAssignmentPurchaseGroupName(purchaseOrderList);
        purchaseOrderList = selectAssignmentPurchaseName(purchaseOrderList);
        purchaseOrderList = selectAssignmentSupplierName(purchaseOrderList);
        purchaseOrderList = selectAssignmentWarehouseName(purchaseOrderList);
        pagenation.setResult(purchaseOrderList);
        return pagenation;

    }
    //为仓库名称赋值
    private List<PurchaseOrder> selectAssignmentWarehouseName(List<PurchaseOrder> purchaseOrderList)throws Exception {
        String[] warehouseArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            warehouseArray[i] = purchaseOrderList.get(i).getWarehouseCode();
        }
        List<Warehouse> warehouseList = warehouseService.selectWarehouseNames(warehouseArray);
        for (Warehouse warehouse : warehouseList){
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(warehouse.getCode().equals(purchaseOrder.getWarehouseCode())){
                    purchaseOrder.setWarehouseName(warehouse.getName());
                }
            }
        }
        return purchaseOrderList;
    }

    //为供应商名称赋值
    private List<PurchaseOrder> selectAssignmentSupplierName(List<PurchaseOrder> purchaseOrderList)throws Exception {
        String[] supplierArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            supplierArray[i] = purchaseOrderList.get(i).getSupplierCode();
        }
        List<Supplier> supplierList = supplierService.selectSupplierNames(supplierArray);
        for (Supplier supplier : supplierList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(supplier.getSupplierCode().equals(purchaseOrder.getSupplierCode())){
                    purchaseOrder.setSupplierName(supplier.getSupplierName());
                }
            }
        }
        return purchaseOrderList;
    }
    //为归属采购人姓名赋值
    private List<PurchaseOrder> selectAssignmentPurchaseName(List<PurchaseOrder> purchaseOrderList) throws Exception{

        String[] userAccreditInfoArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){//purchase_person_id
            userAccreditInfoArray[i] = purchaseOrderList.get(i).getPurchasePersonId();
        }
        List<UserAccreditInfo> userAccreditInfoList = userAccreditInfoService.selectUserNames(userAccreditInfoArray);
        for (UserAccreditInfo userAccreditInfo : userAccreditInfoList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(userAccreditInfo.getUserId().equals(purchaseOrder.getPurchasePersonId())){
                    purchaseOrder.setPurchasePerson(userAccreditInfo.getName());
                }
            }
        }
        return purchaseOrderList;
    }

    //赋值采购组名称
    private List<PurchaseOrder> selectAssignmentPurchaseGroupName(List<PurchaseOrder> purchaseOrderList)throws Exception {

        String[] purchaseGroupArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            purchaseGroupArray[i] = purchaseOrderList.get(i).getPurchaseGroupCode();
        }

        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.selectPurchaseGroupNames(purchaseGroupArray);
        for (PurchaseGroup purchaseGroup : purchaseGroupList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(purchaseGroup.getCode().equals(purchaseOrder.getPurchaseGroupCode())){
                    purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());
                }
            }
        }


        return purchaseOrderList;
    }
    //赋值过滤条件
    private Example setCondition(PurchaseOrderForm form) throws Exception {
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (!StringUtils.isBlank(form.getPurchaseOrderCode())) {
            criteria.andEqualTo("purchaseOrderCode", form.getPurchaseOrderCode());
        }

        String supplierName = form.getSupplierName();//供应商名称--供应商编码
        if(!StringUtils.isBlank(supplierName)){
            Supplier supplier = new Supplier();
            supplier.setSupplierName(supplierName);
            supplier=supplierService.selectOne(supplier);
            if(supplier!=null){
                criteria.andEqualTo("supplierCode", supplier.getSupplierCode());
            }
        }

        String purchaseName = form.getPurchaseName();//采购人name
        if(!StringUtils.isBlank(purchaseName)){
            UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
            userAccreditInfo.setName(purchaseName);
            userAccreditInfo = userAccreditInfoService.selectOne(userAccreditInfo);
            if(userAccreditInfo!=null){
                criteria.andEqualTo("purchasePersonId", userAccreditInfo.getUserId());
            }
        }

        if (!StringUtils.isBlank(form.getPurchaseType())) {
            criteria.andEqualTo("purchaseType", form.getPurchaseType());
        }

        if(!StringUtils.isBlank(form.getPurchaseStatus())){
            criteria.andEqualTo("status", form.getPurchaseStatus());
        }

        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThan("updateTime", form.getStartDate());
        }
        if (!StringUtils.isBlank(form.getEndDate())) {
            criteria.andLessThan("updateTime", form.getEndDate());
        }
        example.orderBy("status").asc();
        example.orderBy("updateTime").desc();
        return example;
    }

    @Override
    public List<Supplier> findSuppliersByUserId(ContainerRequestContext requestContext) throws Exception {
        //有没有对应的渠道。渠道有没有对应的供应商
        //TODO userId加入
        String userId = (String)requestContext.getProperty("userId");
        userId = "E2E4BDAD80354EFAB6E70120C271968C";
        if (StringUtils.isBlank(userId)) {
            String msg = CommonUtil.joinStr("根据userId查询供应商的参数userId为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        List<Supplier> supplierList = purchaseOrderService.findSuppliersByUserId(userId);
        if(supplierList==null){
            supplierList = new ArrayList<Supplier>();
        }
        return supplierList;
    }

    //保存采购单--状态为暂存
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePurchaseOrder(PurchaseOrder purchaseOrder,String status) throws Exception {
        AssertUtil.notNull(purchaseOrder,"采购单对象为空");
        ParamsUtil.setBaseDO(purchaseOrder);
        int count = 0;
        //根据用户的id查询渠道
        UserAccreditInfo user = new UserAccreditInfo();
        //TODO 加入 userId
       // user.setUserId(purchaseOrder.getCreateOperator());
        user.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        user = userAccreditInfoService.selectOne(user);//查询用户对应的渠道
        purchaseOrder.setChannelCode(user.getChannelCode());
        purchaseOrder.setPurchaseOrderCode(serialUtilService.generateCode(LENGTH,SERIALNAME, DateUtils.dateToCompactString(purchaseOrder.getCreateTime())));
        purchaseOrder.setIsValid(ValidEnum.VALID.getCode());
        purchaseOrder.setStatus(status);//设置状态
        purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.TO_BE_NOTIFIED.getCode());//设置入库通知的状态
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            BigDecimal bd = new BigDecimal("100");
            paymentProportion=paymentProportion.divide(bd);
            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
                String msg = CommonUtil.joinStr("采购单保存,付款比例超出范围").toString();
                LOGGER.error(msg);
                throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(paymentProportion);
        }
        count = purchaseOrderService.insert(purchaseOrder);
        if (count<1){
            String msg = CommonUtil.joinStr("采购单保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        String purchaseOrderStrs = purchaseOrder.getGridValue();//采购商品详情的字符串

        Long orderId = purchaseOrder.getId();

        String code = purchaseOrder.getPurchaseOrderCode();

        savePurchaseDetail(purchaseOrderStrs,orderId,code);//保存采购商品

    }

    /**
     *保存采购商品
     */
    public void savePurchaseDetail(String purchaseOrderStrs,Long orderId,String code) throws Exception{

        if(StringUtils.isBlank(purchaseOrderStrs)){
            String msg = CommonUtil.joinStr("保存采购商品的信息为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

        List<PurchaseDetail> purchaseDetailList = JSONArray.parseArray(purchaseOrderStrs,PurchaseDetail.class);
        for (PurchaseDetail purchaseDetail : purchaseDetailList) {
            purchaseDetail.setPurchaseId(orderId);
            purchaseDetail.setPurchaseOrderCode(code);
            ParamsUtil.setBaseDO(purchaseDetail);
        }
        int count = 0;
        count = purchaseDetailService.insertList(purchaseDetailList);
        if (count<1){
            String msg = CommonUtil.joinStr("采购商品保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode, ItemForm form, Pagenation<PurchaseDetail> page, String skus) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierCode",supplierCode);
        map.put("name", form.getName());
        if(skus==null || "".equals(skus) || "null".equals(skus)){
            map.put("skuTemp",null);
        }else {
            map.put("skuTemp","TODO");
            map.put("arrSkus", skus.split(","));
        }
        map.put("skuCode", form.getSkuCode());
        map.put("brandName", form.getBrandName());

        List<PurchaseDetail>  purchaseDetailList = purchaseOrderService.selectItemsBySupplierCode(map);
        if(purchaseDetailList.size() == 0){
            page.setTotalCount(0);
            page.setResult(purchaseDetailList);
            return  page;
        }
        List<Long> categoryIds = new ArrayList<>();
        //获得所有分类的id 拼接，并且显示name的拼接--brand
        for (PurchaseDetail purchaseDetail: purchaseDetailList){
            categoryIds.add(purchaseDetail.getCategoryId());
        }
        List<PurchaseDetail> temp = purchaseOrderService.selectAllCategory(categoryIds);
        //categoryId    allCategoryName    allCategory >>>>>>分类全路径赋值
        for (PurchaseDetail purchaseDetailTmp: temp) {
            for (PurchaseDetail purchaseDetail:purchaseDetailList) {
                if(purchaseDetailTmp.getCategoryId().equals(purchaseDetail.getCategoryId())){
                    purchaseDetail.setAllCategory(purchaseDetailTmp.getAllCategory());
                    purchaseDetail.setAllCategoryName(purchaseDetailTmp.getAllCategoryName());
                }
            }
        }
        int count = purchaseOrderService.selectCountItems(map);
        page.setTotalCount(count);
        page.setResult(purchaseDetailList);

        return page;

    }

}
