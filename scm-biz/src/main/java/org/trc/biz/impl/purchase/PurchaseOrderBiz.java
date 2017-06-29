package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.*;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.exception.PurchaseOrderException;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderAuditService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import sun.reflect.generics.tree.FormalTypeParameter;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private IAclUserAccreditInfoService userAccreditInfoService ;
    @Resource
    private ISupplierService supplierService;
    @Resource
    private IPurchaseGroupService purchaseGroupService;
    @Resource
    private IWarehouseService warehouseService;
    @Resource
    private IPurchaseOrderAuditService iPurchaseOrderAuditService;
    @Resource
    private ISupplierService iSupplierService;
    @Resource
    private IConfigBiz configBiz;
    @Resource
    private IAclUserAccreditInfoService iAclUserAccreditInfoService;

    private final static String  SERIALNAME = "CGD";

    private final static Integer LENGTH = 5;

    private final static String SUPPLIER_CODE = "supplierCode";

    private final static String SKU = "sku";

    @Resource
    private ISerialUtilService serialUtilService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form, Pagenation<PurchaseOrder> page,ContainerRequestContext requestContext) throws Exception {

        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj,"查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo=(AclUserAccreditInfo)obj;
        String  channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        AssertUtil.notBlank(channelCode,"未获得授权");
        Example example = setCondition(form,channelCode);
        if(example!=null){
            Pagenation<PurchaseOrder> pagenation = purchaseOrderService.pagination(example,page,form);
            List<PurchaseOrder> purchaseOrderList = pagenation.getResult();
            if( CollectionUtils.isEmpty(purchaseOrderList) ){
                return pagenation;
            }
            //selectAssignmentPurchaseGroupName(purchaseOrderList);
            _renderPurchaseOrders(purchaseOrderList);
            //selectAssignmentPurchaseName(purchaseOrderList);
            //selectAssignmentSupplierName(purchaseOrderList);
            //selectAssignmentWarehouseName(purchaseOrderList);
            return pagenation;
        }
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
        page.setResult(purchaseOrderList);
        page.setTotalCount(0);
        return page;

    }

    private void  _renderPurchaseOrders(List<PurchaseOrder> purchaseOrderList){ //List<PurchaseOrder>

        for(PurchaseOrder purchaseOrder : purchaseOrderList){
            //赋值采购组名称
            PurchaseGroup paramGroup = new PurchaseGroup();
            paramGroup.setCode(purchaseOrder.getPurchaseGroupCode());
            PurchaseGroup entityGroup = purchaseGroupService.selectOne(paramGroup);
            purchaseOrder.setPurchaseGroupName(entityGroup.getName());
            //赋值采购人名称
            AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
            aclUserAccreditInfo.setUserId(purchaseOrder.getPurchasePersonId());
            AclUserAccreditInfo entityAclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
            purchaseOrder.setPurchasePerson(entityAclUserAccreditInfo.getName());
            //赋值供应商名称
            Supplier supplier = new Supplier();
            supplier.setSupplierCode(purchaseOrder.getSupplierCode());
            Supplier entitySupplier = supplierService.selectOne(supplier);
            purchaseOrder.setSupplierName(entitySupplier.getSupplierName());
            //赋值仓库名称
            Warehouse warehouse = new Warehouse();
            warehouse.setCode(purchaseOrder.getWarehouseCode());
            Warehouse entityWarehouse = warehouseService.selectOne(warehouse);
            purchaseOrder.setWarehouseName(entityWarehouse.getName());

        }

    }
    //为仓库名称赋值
    private void selectAssignmentWarehouseName(List<PurchaseOrder> purchaseOrderList)throws Exception {

        String[] warehouseArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            warehouseArray[i] = purchaseOrderList.get(i).getWarehouseCode();
        }
        List<Warehouse> warehouseList = warehouseService.selectWarehouseNames(warehouseArray);
        if(CollectionUtils.isEmpty(warehouseList)){
            String msg = "根据仓库编码,查询仓库失败";
            LOGGER.error(msg);
            throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION, msg);
        }
        for (Warehouse warehouse : warehouseList){
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(warehouse.getCode().equals(purchaseOrder.getWarehouseCode())){
                    purchaseOrder.setWarehouseName(warehouse.getName());
                }
            }
        }

    }

    //为供应商名称赋值
    private void selectAssignmentSupplierName(List<PurchaseOrder> purchaseOrderList)throws Exception {
        String[] supplierArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            supplierArray[i] = purchaseOrderList.get(i).getSupplierCode();
        }
        List<Supplier> supplierList = supplierService.selectSupplierNames(supplierArray);
        if(CollectionUtils.isEmpty(supplierList)){
            String msg = "根据供应商编码,查询供应商失败";
            LOGGER.error(msg);
            throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION, msg);
        }
        for (Supplier supplier : supplierList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(supplier.getSupplierCode().equals(purchaseOrder.getSupplierCode())){
                    purchaseOrder.setSupplierName(supplier.getSupplierName());
                }
            }
        }

    }
    //为归属采购人姓名赋值
    private void selectAssignmentPurchaseName(List<PurchaseOrder> purchaseOrderList) throws Exception{

        String[] userAccreditInfoArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            userAccreditInfoArray[i] = purchaseOrderList.get(i).getPurchasePersonId();
        }
        List<AclUserAccreditInfo> aclUserAccreditInfoList = userAccreditInfoService.selectUserNames(userAccreditInfoArray);
        if(CollectionUtils.isEmpty(aclUserAccreditInfoList)){
            String msg = "根据用户的ID,查询用户失败";
            LOGGER.error(msg);
            throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION, msg);
        }
        for (AclUserAccreditInfo aclUserAccreditInfo : aclUserAccreditInfoList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(aclUserAccreditInfo.getUserId().equals(purchaseOrder.getPurchasePersonId())){
                    purchaseOrder.setPurchasePerson(aclUserAccreditInfo.getName());
                }
            }
        }

    }

    //赋值采购组名称
    private void selectAssignmentPurchaseGroupName(List<PurchaseOrder> purchaseOrderList)throws Exception {

        String[] purchaseGroupArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            purchaseGroupArray[i] = purchaseOrderList.get(i).getPurchaseGroupCode();
        }

        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.selectPurchaseGroupNames(purchaseGroupArray);

        if(CollectionUtils.isEmpty(purchaseGroupList)){
            String msg = "根据采购组编码,查询采购组失败";
            LOGGER.error(msg);
            throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION, msg);
        }

        for (PurchaseGroup purchaseGroup : purchaseGroupList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(purchaseGroup.getCode().equals(purchaseOrder.getPurchaseGroupCode())){
                    purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());
                }
            }
        }

    }
    //赋值过滤条件
    private Example setCondition(PurchaseOrderForm form,String channelCode) throws Exception {
        Example example = new Example(PurchaseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getPurchaseOrderCode())) {
            criteria.andLike("purchaseOrderCode","%"+ form.getPurchaseOrderCode()+"%");
        }
        if (!StringUtils.isBlank(channelCode)) { //用于过滤该渠道用户的数据
            criteria.andEqualTo("channelCode",channelCode);
        }
        String supplierName = form.getSupplierName();//供应商名称--供应商编码
        if(!StringUtils.isBlank(supplierName)){
            /**
             * 供应商的名称为3 ，则供应商 为null ，则对供应商上就没有做限制
             * 实际上是若供应商 为null，若没有查到供应商，那么直接返回 ，不用再继续查
             * 1.根据名称 模糊查询 所有的供应商编码
             */
            List<Supplier> supplierList = supplierService.selectSupplierByName(supplierName);
            if(supplierList!=null && supplierList.size() >0){
                List<String> supplierCodes = new ArrayList<>();
                for(Supplier supplier:supplierList){
                    supplierCodes.add(supplier.getSupplierCode());
                }
                criteria.andIn(SUPPLIER_CODE,supplierCodes);
            }else { //说明没有查到对应的供应商
                return null;
            }

        }

        String purchaseName = form.getPurchaseName();//采购人name 的处理逻辑同供应商
        if(!StringUtils.isBlank(purchaseName)){

            List<AclUserAccreditInfo> aclUserAccreditInfos = userAccreditInfoService.selectUserByName(purchaseName);
            if(aclUserAccreditInfos!=null && aclUserAccreditInfos.size() >0){
                List<String> userIds = new ArrayList<>();
                for(AclUserAccreditInfo aclUserAccreditInfo:aclUserAccreditInfos){
                    userIds.add(aclUserAccreditInfo.getUserId());
                }
                criteria.andIn("purchasePersonId",userIds);
            }else { //说明没有查到对应的供应商
                return null;
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
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
            Date date = sdf.parse(form.getEndDate());
            date =DateUtils.addDays(date,2);
            form.setEndDate(sdf.format(date));
            criteria.andLessThan("updateTime", form.getEndDate());
        }
        criteria.andEqualTo("isDeleted","0");
        example.orderBy("status").asc();
        example.orderBy("updateTime").desc();
        return example;

    }

    @Override
    public List<Supplier> findSuppliersByUserId(ContainerRequestContext requestContext) throws Exception {
        //根据渠道用户查询对应的供应商
        String userId = (String)requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
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

    //保存采购单
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePurchaseOrder(PurchaseOrderAddData purchaseOrder, String status) throws Exception {
        AssertUtil.notNull(purchaseOrder,"采购单对象为空");
        ParamsUtil.setBaseDO(purchaseOrder);
        int count = 0;
        //根据用户的id查询渠道
        AclUserAccreditInfo user = new AclUserAccreditInfo();
        user.setUserId(purchaseOrder.getCreateOperator());
        user = userAccreditInfoService.selectOne(user);//查询用户对应的渠道
        purchaseOrder.setChannelCode(user.getChannelCode());
        String code = serialUtilService.generateCode(LENGTH,SERIALNAME, DateUtils.dateToCompactString(purchaseOrder.getCreateTime()));
        AssertUtil.notBlank(code,"获取编码失败");
        purchaseOrder.setPurchaseOrderCode(code);
        purchaseOrder.setIsValid(ValidEnum.VALID.getCode());
        purchaseOrder.setStatus(status);//设置状态
        purchaseOrder.setTotalFee(purchaseOrder.getTotalFeeD().multiply(new BigDecimal(100)).longValue());//设置总价格*100
        purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.TO_BE_NOTIFIED.getCode());//设置入库通知的状态
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            BigDecimal bd = new BigDecimal("100");
            paymentProportion=paymentProportion.divide(bd);
            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
                String msg = CommonUtil.joinStr("采购单保存,付款比例超出范围").toString();
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(paymentProportion);
        }
        count = purchaseOrderService.insert(purchaseOrder);
        if (count<1){
            String msg = CommonUtil.joinStr("采购单保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        String purchaseOrderStrs = purchaseOrder.getGridValue();//采购商品详情的字符串

        Long orderId = purchaseOrder.getId();

        code = purchaseOrder.getPurchaseOrderCode();

        savePurchaseDetail(purchaseOrderStrs,orderId,code,purchaseOrder.getCreateOperator());//保存采购商品
        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){ //保存提交审核
            savePurchaseOrderAudit(purchaseOrder);
        }
    }
    /**
     * 保存提交审核的采购信息
     */
    private void savePurchaseOrderAudit(PurchaseOrderAddData purchaseOrder){

        AssertUtil.notNull(purchaseOrder,"采购订单提交审核失败，采购订单信息为空");
        PurchaseOrderAudit purchaseOrderAudit = new PurchaseOrderAudit();
        purchaseOrderAudit.setPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode());
        purchaseOrderAudit.setPurchaseOrderId(purchaseOrder.getId());
        purchaseOrderAudit.setStatus(PurchaseOrderAuditEnum.AUDIT.getCode());  //采购单提交审核，审核表的默认状态，提交审核
        purchaseOrderAudit.setCreateOperator(purchaseOrder.getCreateOperator());
        //purchaseOrder
        ParamsUtil.setBaseDO(purchaseOrderAudit);
        int count = iPurchaseOrderAuditService.insert(purchaseOrderAudit);
        if (count == 0) {
            String msg = String.format("保存%s采购单审核操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

    }

    /**
     * 保存采购商品
     * @param purchaseOrderStrs 采购商品的json串
     * @param orderId 采购订单id
     * @param code  采购订单 编码
     * @param createOperator 创建人
     * @throws Exception
     */
    public void savePurchaseDetail(String purchaseOrderStrs,Long orderId,String code,String createOperator) throws Exception{

        if(StringUtils.isBlank(purchaseOrderStrs)){
            String msg = CommonUtil.joinStr("保存采购商品的信息为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

        List<PurchaseDetail> purchaseDetailList = null;
        try {
            purchaseDetailList = JSONArray.parseArray(purchaseOrderStrs,PurchaseDetail.class);
        }catch (JSONException e){
            String msg = CommonUtil.joinStr("采购商品保存,数据解析失败").toString();
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        AssertUtil.notEmpty(purchaseDetailList,"采购单保存失败,无采购商品");
        for (PurchaseDetail purchaseDetail : purchaseDetailList) {
            purchaseDetail.setPurchasePrice(purchaseDetail.getPurchasePriceD().multiply(new BigDecimal(100)).longValue());//设置采购价格*100
            purchaseDetail.setTotalPurchaseAmount(purchaseDetail.getTotalPurchaseAmountD().multiply(new BigDecimal(100)).longValue());//设置单品的总采购价*100
            purchaseDetail.setPurchaseId(orderId);
            purchaseDetail.setPurchaseOrderCode(code);
            purchaseDetail.setCreateOperator(createOperator);
            ParamsUtil.setBaseDO(purchaseDetail);
        }
        int count = 0;
        count = purchaseDetailService.insertList(purchaseDetailList);
        if (count<1){
            String msg = CommonUtil.joinStr("采购商品保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

    }

    @Override
    public List<PurchaseDetail> findAllPurchaseDetailBysupplierCode(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode,"根据供应商编码查询所有的可采购商品失败,供应商编码为空");
        Map<String, Object> map = new HashMap<>();
        map.put(SUPPLIER_CODE,supplierCode);
        List<PurchaseDetail>  purchaseDetailList = purchaseOrderService.selectItemsBySupplierCode(map);
        if(purchaseDetailList == null || purchaseDetailList.size()==0){
            purchaseDetailList = new ArrayList<>();  //如果没有查到，有效的sku商品
        }
        return purchaseDetailList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode, ItemForm form, Pagenation<PurchaseDetail> page, String skus) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        AssertUtil.notBlank(supplierCode,"根据供应商查询商品信息,供应商编码为空" );
        map.put(SUPPLIER_CODE,supplierCode);
        map.put("name", form.getName());
        if(StringUtils.isBlank(skus)){
            map.put("skuTemp",null);
        } else {
            map.put("skuTemp",SKU);
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePurchaseOrderState(PurchaseOrder purchaseOrder) throws Exception {

        AssertUtil.notNull(purchaseOrder,"采购订单状态修改失败，采购订单信息为空");
        String status = purchaseOrder.getStatus();

        if(PurchaseOrderStatusEnum.HOLD.getCode().equals(status)){ //暂存：的删除操作
            handleDeleted(purchaseOrder);
            return;
        }
        if(PurchaseOrderStatusEnum.REJECT.getCode().equals(status)){ //审核驳回：的删除操作
            handleDeleted(purchaseOrder);
            return;
        }
        if(PurchaseOrderStatusEnum.PASS.getCode().equals(status)){//审核通过：的作废操作
            handleCancel(purchaseOrder);
            return;
        }
        if(PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode().equals(status)){ //入库通知的（未通知仓储）：的作废操作
            handleCancel(purchaseOrder);
            return;
        }
    }
    //采购单作废操作
    private void handleCancel(PurchaseOrder purchaseOrder)throws Exception {

        PurchaseOrder tmp = new PurchaseOrder();
        tmp.setId(purchaseOrder.getId());
        tmp.setStatus(PurchaseOrderStatusEnum.CANCEL.getCode());
        int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = String.format("作废%s采购单操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }

    }
    //采购单逻辑删除

    private void handleDeleted(PurchaseOrder purchaseOrder)throws Exception{
        PurchaseOrder tmp = new PurchaseOrder();
        tmp.setId(purchaseOrder.getId());
        tmp.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = String.format("删除%s采购单操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }
        //根据采购编码的id，逻辑删除，该供应商对应的采购商品
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        Example example = new Example(PurchaseDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseId",purchaseOrder.getId());
        count = purchaseDetailService.updateByExampleSelective(purchaseDetail,example);
        if (count == 0) {
            String msg = String.format("删除%s采购单对应的商品操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    public PurchaseOrder findPurchaseOrderAddDataById(Long id) throws Exception {

        AssertUtil.notNull(id,"根据采购订单id查询采购单失败，采购订单id为空");
        //查询采购单
        PurchaseOrder purchaseOrder = purchaseOrderService.selectByPrimaryKey(id);
        AssertUtil.notNull(purchaseOrder,"采购单根据主键id查询，数据库查询失败");
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(purchaseOrder.getSupplierCode());
        supplier = iSupplierService.selectOne(supplier);
        AssertUtil.notNull(supplier,"根据供应商编码查询供应商失败");
        purchaseOrder.setSupplierName(supplier.getSupplierName()); //赋值供应商名称
        List<Dict> dicts = configBiz.findDictsByTypeNo("purchaseType");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getPurchaseType())){  //赋值采购类型的name
                purchaseOrder.setPurchaseTypeName(dict.getName());
            }
        }
        dicts = configBiz.findDictsByTypeNo("payType");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getPayType())){  //赋值付款方式的name
                purchaseOrder.setPayTypeName(dict.getName());
            }
        }
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            purchaseOrder.setPaymentProportion(paymentProportion.multiply(new BigDecimal("100")));
        }
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setCode(purchaseOrder.getPurchaseGroupCode());
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        AssertUtil.notNull(supplier,"根据采购组编码查询采购组失败");
        purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());    //赋值采购组名称

        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setUserId(purchaseOrder.getPurchasePersonId());
        aclUserAccreditInfo = iAclUserAccreditInfoService.selectOne(aclUserAccreditInfo);
        AssertUtil.notNull(aclUserAccreditInfo,"根据用户的userId查询用户信息失败");
        purchaseOrder.setPurchasePerson(aclUserAccreditInfo.getName());         //赋值采购人的名称

        dicts = configBiz.findDictsByTypeNo("currency");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getCurrencyType())){  //赋值币种的name
                purchaseOrder.setCurrencyTypeName(dict.getName());
            }
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(purchaseOrder.getWarehouseCode());
        warehouse = warehouseService.selectOne(warehouse);
        AssertUtil.notNull(warehouse,"根据用户的仓库编码查询仓库信息失败");
        purchaseOrder.setWarehouseName(warehouse.getName());

        dicts = configBiz.findDictsByTypeNo("transportCostsTake");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getTransportFeeDestId())){
                purchaseOrder.setTransportFeeDestIdName(dict.getName());
            }
        }

        dicts = configBiz.findDictsByTypeNo("handlerPriority");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getHandlerPriority())){
                purchaseOrder.setHandlerPriorityName(dict.getName());
            }
        }
        return purchaseOrder;

    }

    @Override
    public void updatePurchaseStateFreeze(PurchaseOrder purchaseOrder) throws Exception {

        AssertUtil.notNull(purchaseOrder,"采购订单状态修改失败，采购订单信息为空");
        String status = purchaseOrder.getStatus();
        if(PurchaseOrderStatusEnum.PASS.getCode().equals(status)){ //需冻结
            PurchaseOrder tmp = new PurchaseOrder();
            tmp.setId(purchaseOrder.getId());
            tmp.setStatus(PurchaseOrderStatusEnum.FREEZE.getCode());
            int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
            if (count == 0) {
                String msg = String.format("冻结%s采购单操作失败", JSON.toJSONString(purchaseOrder));
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
            return;
        }
        if(PurchaseOrderStatusEnum.FREEZE.getCode().equals(status)){ //需解冻
            PurchaseOrder tmp = new PurchaseOrder();
            tmp.setId(purchaseOrder.getId());
            tmp.setStatus(PurchaseOrderStatusEnum.PASS.getCode());
            int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
            if (count == 0) {
                String msg = String.format("解冻%s采购单操作失败", JSON.toJSONString(purchaseOrder));
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePurchaseOrder(PurchaseOrderAddData purchaseOrderAddData,ContainerRequestContext requestContext) throws Exception {

        AssertUtil.notNull(purchaseOrderAddData,"修改采购单失败,采购单为空");
        PurchaseOrder purchaseOrder = purchaseOrderAddData;//转型
        System.out.println(purchaseOrderAddData.getTotalFeeD().toString());
        purchaseOrder.setTotalFee(purchaseOrder.getTotalFeeD().multiply(new BigDecimal(100)).longValue());//设置总价格*100
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            BigDecimal bd = new BigDecimal("100");
            paymentProportion=paymentProportion.divide(bd);
            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
                String msg = CommonUtil.joinStr("采购单修改,付款比例超出范围").toString();
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(paymentProportion);
        }
        int count = purchaseOrderService.updateByPrimaryKeySelective(purchaseOrder);
        if (count == 0) {
            String msg = String.format("修改采购单%s数据库操作失败",JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }
        purchaseDetailService.deletePurchaseDetailByPurchaseOrderCode(purchaseOrderAddData.getPurchaseOrderCode());
        Object obj =requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AssertUtil.notNull(obj,"采购单更新失败,获取授权信息失败");
        purchaseOrderAddData.setCreateOperator((String) obj);
        savePurchaseDetail(purchaseOrderAddData.getGridValue(),purchaseOrderAddData.getId(),purchaseOrderAddData.getPurchaseOrderCode(),purchaseOrderAddData.getCreateOperator());
        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrder.getStatus())){ //保存提交审核
            savePurchaseOrderAudit(purchaseOrderAddData);
        }

    }

    /***
     * 1.先根据采购订单编码查询，该采购单对应的skus编码
     * 2.比对前台提交的skuss
     *   传入的skus 有的删除， 剩下在原有的skus 也删除
     * @param purchaseOrderStrs
     * @throws Exception
     */
    /*private void saveSkusAndDeleteSkus(String purchaseOrderStrs,PurchaseOrderAddData purchaseOrderAddData) throws Exception{

        List<PurchaseDetail> purchaseDetailList = null;
        try {
            //拿到现有的skus
            purchaseDetailList = JSONArray.parseArray(purchaseOrderStrs,PurchaseDetail.class);
        }catch (JSONException e){
            String msg = CommonUtil.joinStr("采购商品保存,数据解析失败").toString();
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        AssertUtil.notEmpty(purchaseDetailList,"采购单修改失败,无采购商品");
        //根据采购订单编码，查询以前拥有的采购skus
        Example example = new Example(PurchaseDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("purchaseId",purchaseOrderAddData.getId());
        List<PurchaseDetail> purchases = purchaseDetailService.selectByExample(example);
        if(CollectionUtils.isEmpty(purchases)){//若查询不出，则直接做插入操作
            savePurchaseDetail(purchaseOrderAddData.getGridValue(),purchaseOrderAddData.getId(),purchaseOrderAddData.getPurchaseOrderCode(),purchaseOrderAddData.getCreateOperator());
            return;
        }
        //查询出：做对比去重
        String [] strs = new String[purchases.size()];
        for (int i =0 ; i<purchases.size() ; i++) {
            strs[i] = purchases.get(i).getSkuCode();
        }//PurchaseDetail purchaseDetail : purchaseDetailList
        Iterator<PurchaseDetail> sListIterator = purchaseDetailList.iterator();
        List<String> skuList = new ArrayList();
        while(sListIterator.hasNext()){
            PurchaseDetail e = sListIterator.next();
            for (int i = 0 ; i<strs.length ; i++){
                if(strs[i].equals(e.getSkuCode())){
                    sListIterator.remove();
                    skuList.add(strs[i]);
                }
            }
        }
        //不需要删除的对价格的更改
        //根据（skus）删除剩余的数组元素，重复的不删，没有的删除
        System.out.println(strs.length+"---------------------------====--------------------------1");
        if(skuList.size() != 0){
            Example example1 = new Example(PurchaseDetail.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria.andNotIn("skuCode",skuList);
            purchaseDetailService.deleteByExample(example);
        }else {//没有相同，则全部删除
            Example example1 = new Example(PurchaseDetail.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria.andEqualTo("purchaseId",purchaseOrderAddData.getId());
        }
        System.out.println(purchaseDetailList.size()+"-----------------------------------------------------------1");
        //插入剩余的（purchaseDetails）
        if (purchaseDetailList.size()!=0){
            String jsonList = JSON.toJSONString(purchaseDetailList);
            savePurchaseDetail(jsonList,purchaseOrderAddData.getId(),purchaseOrderAddData.getPurchaseOrderCode(),purchaseOrderAddData.getCreateOperator());
        }

    }*/

}
