package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.domain.System.Warehouse;
import org.trc.domain.dict.Dict;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.*;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.domain.util.Area;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.exception.PurchaseOrderException;
import org.trc.exception.WarehouseNoticeException;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemsService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.purchase.*;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.text.ParseException;
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
    @Resource
    private IWarehouseNoticeService iWarehouseNoticeService;
    @Resource
    private ISerialUtilService iSerialUtilService;
    @Resource
    private IWarehouseNoticeDetailsService warehouseNoticeDetailsService;
    @Resource
    private ILogInfoService logInfoService;
    @Resource
    private ISupplierBrandService iSupplierBrandService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ISkusService skusService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private ILocationUtilService locationUtilService;



    private final static String  SERIALNAME = "CGD";

    private final static Integer LENGTH = 5;

    private final static String SUPPLIER_CODE = "supplierCode";

    private final static String WAREHOUSE_INFO_ID = "warehouseInfoId";

    private final static String SKU = "sku";

    private final static String CGRKTZ="CGRKTZ";

    private final static String DATE_EXT = " 23:59:59";

    @Resource
    private ISerialUtilService serialUtilService;

    private final static List<String> STATUS_LIST;

    static {
        List<String> list = new ArrayList<String>();
        list.add(ZeroToNineEnum.ZERO.getCode());
        list.add(ZeroToNineEnum.ONE.getCode());
        list.add(ZeroToNineEnum.TWO.getCode());
        list.add(ZeroToNineEnum.THREE.getCode());
        list.add(ZeroToNineEnum.SIX.getCode());
        list.add(ZeroToNineEnum.SEVEN.getCode());
        list.add(ZeroToNineEnum.EIGHT.getCode());
        STATUS_LIST = list;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Cacheable(key="#form.toString()+#page.pageNo+#page.pageSize+#channelCode",isList=true)
    public Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form, Pagenation<PurchaseOrder> page,String  channelCode)  {

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
            if(StringUtils.isNotBlank(purchaseOrder.getPurchaseGroupCode())){
                PurchaseGroup paramGroup = new PurchaseGroup();
                paramGroup.setCode(purchaseOrder.getPurchaseGroupCode());
                PurchaseGroup entityGroup = purchaseGroupService.selectOne(paramGroup);
                purchaseOrder.setPurchaseGroupName(entityGroup.getName());
            }
            //赋值采购人名称
            if(StringUtils.isNotBlank(purchaseOrder.getPurchasePersonId())){
                AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
                aclUserAccreditInfo.setUserId(purchaseOrder.getPurchasePersonId());
                AclUserAccreditInfo entityAclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
                purchaseOrder.setPurchasePerson(entityAclUserAccreditInfo.getName());
            }
            //赋值供应商名称
            if(StringUtils.isNotBlank(purchaseOrder.getSupplierCode())){
                Supplier supplier = new Supplier();
                supplier.setSupplierCode(purchaseOrder.getSupplierCode());
                Supplier entitySupplier = supplierService.selectOne(supplier);
                purchaseOrder.setSupplierName(entitySupplier.getSupplierName());
            }
            //赋值仓库名称
            if(StringUtils.isNotBlank(purchaseOrder.getWarehouseCode())){
                Warehouse warehouse = new Warehouse();
                warehouse.setCode(purchaseOrder.getWarehouseCode());
                Warehouse entityWarehouse = warehouseService.selectOne(warehouse);
                purchaseOrder.setWarehouseName(entityWarehouse.getName());
            }
        }

    }
    //为仓库名称赋值
    private void selectAssignmentWarehouseName(List<PurchaseOrder> purchaseOrderList) {

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
    private void selectAssignmentSupplierName(List<PurchaseOrder> purchaseOrderList) {
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
    private void selectAssignmentPurchaseName(List<PurchaseOrder> purchaseOrderList) {

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
    private void selectAssignmentPurchaseGroupName(List<PurchaseOrder> purchaseOrderList) {

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
    private Example setCondition(PurchaseOrderForm form,String channelCode)  {
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
            }else { //说明没有查到对应的采购人
                return null;
            }

        }

        if (!StringUtils.isBlank(form.getPurchaseType())) {
            criteria.andEqualTo("purchaseType", form.getPurchaseType());
        }

        if(!StringUtils.isBlank(form.getPurchaseStatus())){
            criteria.andEqualTo("status", form.getPurchaseStatus());
        }else{
            criteria.andIn("status", STATUS_LIST);
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
                LOGGER.error(msg);
                throw  new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_QUERY_EXCEPTION,msg);
            }
            date =DateUtils.addDays(date,1);
            form.setEndDate(sdf.format(date));
            criteria.andLessThan("updateTime", form.getEndDate());
        }
        criteria.andEqualTo("isDeleted","0");
        example.setOrderByClause("instr('0,1,3,2,8,6,7',`status`) ASC");
        //example.orderBy("status").asc();
        example.orderBy("updateTime").desc();
        return example;

    }

    @Override
    public List<Supplier> findSuppliersByUserId(String userId)  {
        //根据渠道用户查询对应的供应商
        AssertUtil.notBlank(userId ,"获取用户id失败");
        if (StringUtils.isBlank(userId)) {
            String msg = "根据userId查询供应商的参数userId为空";
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        List<Supplier> supplierList = purchaseOrderService.findSuppliersByUserId(userId);
        if(supplierList==null){
            supplierList = new ArrayList<Supplier>();
        }
        return supplierList;
    }

    @Override
    public Response findWarehousesByChannelCode(String channelCode) {
        //根据业务线查询对应的仓库
        AssertUtil.notBlank(channelCode ,"获取业务线失败");
        if (StringUtils.isBlank(channelCode)) {
            String msg = "根据业务线查询仓库的参数为空";
            LOGGER.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

        //获取已启用仓库信息
        Warehouse warehouse = new Warehouse();
        warehouse.setIsValid(ZeroToNineEnum.ONE.getCode());
        warehouse.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Warehouse> warehouseList = warehouseService.select(warehouse);

        if(warehouseList==null || warehouseList.size() < 1){
            String msg = "无数据，请确认【系统管理-仓库管理】中存在“启用”状态的仓库！";
            LOGGER.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

        //校验仓库是否已通知
        Map<String, String> map = new HashMap<String, String>();
        map.put("channelCode", channelCode);
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectWarehouseInfo(map);
        if(warehouseInfoList == null || warehouseInfoList.size() < 1){
            String msg = "无数据，请确认【仓储管理-仓库信息管理】中存在“货主仓库状态”为“通知成功”的仓库！";
            LOGGER.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        return ResultUtil.createSuccessResult("根据业务线查询对应的仓库", warehouseInfoList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagenation<PurchaseDetail> findPurchaseDetail(ItemForm form, Pagenation<PurchaseDetail> page, String skus) {
        String supplierCode = form.getSupplierCode();
        String warehouseInfoId = form.getWarehouseInfoId();
        //校验商品
        this.checkItems(supplierCode);
        AssertUtil.notBlank(supplierCode,"根据供应商编码查询的可采购商品失败,供应商编码为空");
        AssertUtil.notBlank(warehouseInfoId,"根据仓库信息查询的可采购商品失败,仓库信息主键为空");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(SUPPLIER_CODE, supplierCode);
        map.put(WAREHOUSE_INFO_ID, warehouseInfoId);
        List<PurchaseDetail>  purchaseDetailListCheck = purchaseOrderService.selectItemsBySupplierCodeCheck(map);
        if(purchaseDetailListCheck.size() == 0){
            String msg = "无数据，请确认【商品管理】中存在所选供应商的品牌的，且所选收货仓库在【仓库信息管理】中“通知仓库状态”为“通知成功”的启用商品！";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

        if(StringUtils.isBlank(skus)){
            map.put("skuTemp",null);
        } else {
            map.put("skuTemp",SKU);
            map.put("arrSkus", skus.split(","));
        }

        map.put("skuName", form.getSkuName());
        map.put("skuCode", form.getSkuCode());
        map.put("brandName", form.getBrandName());
        map.put("barCode", form.getBarCode());
        map.put("itemNo", form.getItemNo());
        int purchaseDetailListCount = purchaseOrderService.selectItemsBySupplierCodeCount(map);
        if(purchaseDetailListCount < 1){
            return new Pagenation<PurchaseDetail>();
        }
        //获取总数
        page.setTotalCount(purchaseDetailListCount);
        //获取分页数
        map.put("start", page.getStart());
        map.put("pageSize", page.getPageSize());
        List<PurchaseDetail>  purchaseDetailList = purchaseOrderService.selectItemsBySupplierCode(map);
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

        page.setResult(purchaseDetailList);

        return page;
    }

    private Pagenation<PurchaseDetail> getPage(String msg){
        Pagenation<PurchaseDetail> page = new Pagenation<PurchaseDetail>();
        List<PurchaseDetail> list = new ArrayList<>();
        PurchaseDetail detail = new PurchaseDetail();
        detail.setSkuName(msg);
        list.add(detail);
        page.setResult(list);
        return page;
    }

    //保存采购单
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void savePurchaseOrder(PurchaseOrderAddData purchaseOrder, String status,AclUserAccreditInfo aclUserAccreditInfo)  {
        AssertUtil.notNull(purchaseOrder,"采购单对象为空");
        String supplierCode = purchaseOrder.getSupplierCode();
        AssertUtil.notBlank(supplierCode, "供应商为空");
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
        //如果提交采购单的方式为提交审核--则校验必须的数据
        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){
            assertArgs(purchaseOrder);
        }
//        assertArgs(purchaseOrder);
        if(purchaseOrder.getTotalFeeD() != null){
            purchaseOrder.setTotalFee(purchaseOrder.getTotalFeeD().multiply(new BigDecimal(100)).longValue());//设置总价格*100
        }
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            BigDecimal bd = new BigDecimal("100");
            paymentProportion=paymentProportion.divide(bd);
            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
                String msg = "采购单保存,付款比例超出范围";
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(paymentProportion);
        }
        //格式化时间
        this.formatDate(purchaseOrder);

        count = purchaseOrderService.insert(purchaseOrder);
        if (count<1){
            String msg = "采购单保存,数据库操作失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        String purchaseOrderStrs = purchaseOrder.getGridValue();//采购商品详情的字符串

        Long orderId = purchaseOrder.getId();

        code = purchaseOrder.getPurchaseOrderCode();

        if(StringUtils.isNotBlank(purchaseOrderStrs) && !"[]".equals(purchaseOrderStrs)){
            BigDecimal totalPrice = savePurchaseDetail(purchaseOrderStrs,orderId,code,purchaseOrder.getCreateOperator(),status);//保存采购商品
            if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){//提交审核做金额校验
                if(totalPrice.compareTo(purchaseOrder.getTotalFeeD()) != 0){//比较实际采购价格与页面传输的价格是否相等
                    String msg = "采购单保存,采购商品的总价与页面的总价不相等";
                    LOGGER.error(msg);
                    throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
                }
            }
        }
        //保存操作日志
        String userId= aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.ADD.getMessage(),null,ZeroToNineEnum.ZERO.getCode());

        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){ //保存提交审核
            savePurchaseOrderAudit(purchaseOrder,aclUserAccreditInfo);
        }

    }

    private void formatDate(PurchaseOrder purchaseOrder){
        if(StringUtils.isNotEmpty(purchaseOrder.getRequriedReceiveDate()) && purchaseOrder.getRequriedReceiveDate().length() < 15){
            purchaseOrder.setRequriedReceiveDate(purchaseOrder.getRequriedReceiveDate() + DATE_EXT);
        }
        if(StringUtils.isNotEmpty(purchaseOrder.getEndReceiveDate()) && purchaseOrder.getEndReceiveDate().length() < 15){
            purchaseOrder.setEndReceiveDate(purchaseOrder.getEndReceiveDate() + DATE_EXT);
        }
    }

    /**
     * 校验采购单信息
     * @param purchaseOrder
     */
    private void assertArgs(PurchaseOrderAddData purchaseOrder){
        AssertUtil.notNull(purchaseOrder,"采购单的信息为空!");
        AssertUtil.notBlank(purchaseOrder.getPurchaseType(),"采购类型不能为空!");
        AssertUtil.notBlank(purchaseOrder.getPayType(),"付款类型不能为空!");
        AssertUtil.notBlank(purchaseOrder.getPurchaseGroupCode(),"采购组不能为空!");
        AssertUtil.notBlank(purchaseOrder.getCurrencyType(),"币值不能为空!");
        AssertUtil.notBlank(purchaseOrder.getPurchasePersonId(),"采购人不能为空!");
        AssertUtil.notBlank(purchaseOrder.getWarehouseInfoId(),"收货仓库不能为空!");
        AssertUtil.notBlank(purchaseOrder.getReceiver(),"收货人不能为空!");
        AssertUtil.notBlank(purchaseOrder.getReceiverNumber(),"收货人手机不能为空!");
        AssertUtil.notBlank(purchaseOrder.getTransportFeeDestId(),"运输费用承担方不能为空!");
        AssertUtil.notBlank(purchaseOrder.getRequriedReceiveDate(),"要求到货日期不能为空!");
        AssertUtil.notBlank(purchaseOrder.getEndReceiveDate(),"截止到货日期不能为空!");
        AssertUtil.notBlank(purchaseOrder.getHandlerPriority(),"处理优先级不能为空!");
        AssertUtil.notBlank(purchaseOrder.getGridValue(),"采购商品不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSupplierCode(),"供应商不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSender(),"发件人不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSenderProvince(),"发件方省份不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSenderCity(),"发件方城市不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSenderNumber(),"发件方手机不能为空!");
        AssertUtil.notBlank(purchaseOrder.getSenderAddress(),"发件方详细地址不能为空!");
        AssertUtil.notNull(purchaseOrder.getTotalFeeD(),"采购总金额不能为空!");
    }
    /**
     * 保存提交审核的采购信息
     */
    private void savePurchaseOrderAudit(PurchaseOrderAddData purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo){

        AssertUtil.notNull(purchaseOrder,"采购订单提交审核失败，采购订单信息为空");
        PurchaseOrderAudit purchaseOrderAudit = new PurchaseOrderAudit();
        purchaseOrderAudit.setPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode());
        purchaseOrderAudit.setPurchaseOrderId(purchaseOrder.getId());
        purchaseOrderAudit.setStatus(ZeroToNineEnum.ONE.getCode());  //采购单提交审核，审核表的默认状态，提交审核
        purchaseOrderAudit.setCreateOperator(purchaseOrder.getCreateOperator());
        //purchaseOrder
        ParamsUtil.setBaseDO(purchaseOrderAudit);
        int count = iPurchaseOrderAuditService.insert(purchaseOrderAudit);
        if (count == 0) {
            String msg = String.format("保存%s采购单审核操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

        //提交审核操作日志
        String userId= aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,AuditStatusEnum.COMMIT.getName(),null,ZeroToNineEnum.ZERO.getCode());

    }

    /**
     * 保存采购商品
     * @param purchaseOrderStrs 采购商品的json串
     * @param orderId 采购订单id
     * @param code  采购订单 编码
     * @param createOperator 创建人
     * @return int 商品的采购总价
     */
    private BigDecimal savePurchaseDetail(String purchaseOrderStrs,Long orderId,String code,String createOperator,String status) {

        if(StringUtils.isBlank(purchaseOrderStrs)){
            String msg = "保存采购商品的信息为空";
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        List<PurchaseDetail> purchaseDetailList = null;
        try {
            purchaseDetailList = JSONArray.parseArray(purchaseOrderStrs,PurchaseDetail.class);
        }catch (JSONException e){
            String msg = "采购商品保存,数据解析失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        AssertUtil.notEmpty(purchaseDetailList,"采购单保存失败,无采购商品");

        BigDecimal totalPrice = new BigDecimal(0);

        for (PurchaseDetail purchaseDetail : purchaseDetailList) {
            String skuCode = purchaseDetail.getSkuCode();
            if(skuCode == null){
                String msg = "采购商品保存,数据错误";
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }

            if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){
                this.assertDetailArgs(purchaseDetail);
            }

            Skus skus = new Skus();
            skus.setSkuCode(skuCode);
            skus.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            skus = skusService.selectOne(skus);
            if(ZeroToNineEnum.ZERO.getCode().equals(skus.getIsValid())){
                String msg = String.format("商品%s已被停用，请先删除！", skuCode);
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }

            if(purchaseDetail.getTotalPurchaseAmountD() != null){
                totalPrice = totalPrice.add(purchaseDetail.getTotalPurchaseAmountD());
                BigDecimal bd = purchaseDetail.getPurchasePriceD().multiply(new BigDecimal(100));
                purchaseDetail.setPurchasePrice(bd.longValue());//设置采购价格*100
            }else {
                purchaseDetail.setPurchasePrice(null);//设置采购价格*100
            }
            if(purchaseDetail.getTotalPurchaseAmountD()!=null){
                purchaseDetail.setTotalPurchaseAmount(purchaseDetail.getTotalPurchaseAmountD().multiply(new BigDecimal(100)).longValue());//设置单品的总采购价*100
            } else{
                purchaseDetail.setTotalPurchaseAmount(null);//设置单品的总采购价*100
            }
            //purchaseDetail.setTotalPurchaseAmount(purchaseDetail.getTotalPurchaseAmountD().multiply(new BigDecimal(100)).longValue());//设置单品的总采购价*100
            purchaseDetail.setPurchaseId(orderId);
            purchaseDetail.setPurchaseOrderCode(code);
            purchaseDetail.setCreateOperator(createOperator);
            this.checkPurchaseDetail(purchaseDetail);
            ParamsUtil.setBaseDO(purchaseDetail);
        }
        int count = 0;
        count = purchaseDetailService.insertList(purchaseDetailList);
        if (count<1){
            String msg = "采购商品保存,数据库操作失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        return totalPrice;
    }

    /**
     * 校验采购单详情信息
     */
    private void assertDetailArgs(PurchaseDetail purchaseDetail){
        AssertUtil.notNull(purchaseDetail.getPurchasePriceD(),"采购商品进价不能为空!");
        AssertUtil.notNull(purchaseDetail.getPurchasingQuantity(),"采购商品数量不能为空!");
        AssertUtil.notBlank(purchaseDetail.getBatchCode(),"采购商品批次号不能为空!");
    }

    private void checkPurchaseDetail(PurchaseDetail purchaseDetail){
        AssertUtil.notNull(purchaseDetail.getWarehouseItemInfoId(), "仓库商品ID不能为空");
        AssertUtil.notNull(purchaseDetail.getSpecNatureInfo(), "商品规格不能为空");
        AssertUtil.notNull(purchaseDetail.getItemNo(), "商品货号不能为空");
        AssertUtil.notNull(purchaseDetail.getBarCode(), "商品条形码不能为空");
        AssertUtil.notNull(purchaseDetail.getBrandName(), "商品品牌名称不能为空");
        AssertUtil.notNull(purchaseDetail.getAllCategoryName(), "商品分类不能为空");
        AssertUtil.notNull(purchaseDetail.getSkuCode(), "商品sku编码不能为空");
        AssertUtil.notNull(purchaseDetail.getSkuName(), "商品sku名称不能为空");
    }

    @Override
    public List<PurchaseDetail> findAllPurchaseDetailBysupplierCode(String supplierCode)  {
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
    public Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode, ItemForm form, Pagenation<PurchaseDetail> page, String skus)  {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        AssertUtil.notBlank(supplierCode,"根据供应商查询商品信息,供应商编码为空" );
        map.put(SUPPLIER_CODE,supplierCode);
        map.put("skuName", form.getSkuName());
        if(StringUtils.isBlank(skus)){
            map.put("skuTemp",null);
        } else {
            map.put("skuTemp",SKU);
            map.put("arrSkus", skus.split(","));
        }
        map.put("skuCode", form.getSkuCode());
        map.put("brandName", form.getBrandName());
        map.put("barCode", form.getBarCode());
        map.put("itemNo", form.getItemNo());

        List<PurchaseDetail>  purchaseDetailList = purchaseOrderService.selectItemsBySupplierCode(map);
        if(purchaseDetailList.size() == 0){
            String msg = "无数据，请确认所选收货仓库在【仓储管理-仓库信息管理】中存在“通知仓库状态”为“通知成功”的商品！";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
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

    private void checkItems(String supplierCode){
        Example example = new Example(Items.class);
        int count = itemsService.selectCountByExample(example);
        if(count < 1){
            String msg = "无数据，请确认【商品管理】中存在商品类型为”自采“的商品！";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

        example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        count = itemsService.selectCountByExample(example);
        if(count < 1){
            String msg = "无数据，请确认【商品管理】中存在“启用”状态的自采商品！";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

        Map<String, Object> map = new HashMap<>();
        AssertUtil.notBlank(supplierCode,"根据供应商查询商品信息,供应商编码为空" );
        map.put(SUPPLIER_CODE,supplierCode);
        int count2 = purchaseOrderService.selectCountItemsForSupplier(map);
        if(count2 < 1){
            String msg = "无数据，请确认【商品管理】中存在所选供应商的品牌的，且状态为启用的自采商品";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public String updatePurchaseOrderState(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo)  {

        AssertUtil.notNull(purchaseOrder,"采购订单状态修改失败，采购订单信息为空");
        String status = purchaseOrder.getStatus();

        if(PurchaseOrderStatusEnum.HOLD.getCode().equals(status)){ //暂存：的删除操作
            handleDeleted(purchaseOrder,aclUserAccreditInfo);
            return "删除成功!";
        }
        if(PurchaseOrderStatusEnum.REJECT.getCode().equals(status)){ //审核驳回：的删除操作
            handleDeleted(purchaseOrder,aclUserAccreditInfo);
            return "删除成功!";
        }
        if(PurchaseOrderStatusEnum.PASS.getCode().equals(status)){//审核通过：的作废操作
            handleCancel(purchaseOrder,aclUserAccreditInfo);
            return "作废成功!";
        }
        if(PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode().equals(status)){ //入库通知的（未通知仓储）：的作废操作
            handleCancel(purchaseOrder,aclUserAccreditInfo);
        }
        return "作废成功!";
    }
    //采购单作废操作
    private void handleCancel(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo) {

        PurchaseOrder tmp = new PurchaseOrder();
        tmp.setId(purchaseOrder.getId());
        tmp.setStatus(PurchaseOrderStatusEnum.CANCEL.getCode());
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = String.format("作废%s采购单操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }
        String userId= aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.CANCEL.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        //更改入库通知单的状态
        WarehouseNotice warehouseNotice = new WarehouseNotice();
        warehouseNotice.setPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode());
        warehouseNotice = iWarehouseNoticeService.selectOne(warehouseNotice);
        if(warehouseNotice != null && warehouseNotice.getStatus().equals(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode())){
            //更改入库通知单的状态--用自身的‘待发起入库通知状态’,作为判断是否执行作废的操作
            WarehouseNotice notice = new WarehouseNotice();
            notice.setStatus(WarehouseNoticeStatusEnum.CANCELLATION.getCode());
            notice.setUpdateTime(Calendar.getInstance().getTime());
            Example example = new Example(WarehouseNotice.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id",warehouseNotice.getId());
            criteria.andEqualTo("status",WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
            int num = iWarehouseNoticeService.updateByExampleSelective(notice,example);
            if (num == 0) {
                String msg = String.format("作废%s采购单操作失败,入库通知单已经被执行操作", JSON.toJSONString(warehouseNotice));
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
            }
            logInfoService.recordLog(warehouseNotice,warehouseNotice.getId().toString(),userId,LogOperationEnum.CANCEL.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        }
    }
    //采购单逻辑删除
    private void handleDeleted(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo){
        PurchaseOrder tmp = new PurchaseOrder();
        tmp.setId(purchaseOrder.getId());
        tmp.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        tmp.setUpdateTime(Calendar.getInstance().getTime());
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
        /*if (count == 0) {
            String msg = String.format("删除%s采购单对应的商品操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }*/

        String userId= aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.DELETE.getMessage(),null,ZeroToNineEnum.ZERO.getCode());

    }

    //type 1:查询详情 2:编辑
    @Override
    public PurchaseOrder findPurchaseOrderAddDataById(Long id)  {

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
        if(StringUtils.isNotBlank(purchaseOrder.getPurchaseGroupCode())){
            PurchaseGroup purchaseGroup = new PurchaseGroup();
            purchaseGroup.setCode(purchaseOrder.getPurchaseGroupCode());
            purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
            AssertUtil.notNull(supplier,"根据采购组编码查询采购组失败");
            purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());    //赋值采购组名称
        }
        if(StringUtils.isNotBlank(purchaseOrder.getPurchasePersonId())){
            AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
            aclUserAccreditInfo.setUserId(purchaseOrder.getPurchasePersonId());
            aclUserAccreditInfo = iAclUserAccreditInfoService.selectOne(aclUserAccreditInfo);
            AssertUtil.notNull(aclUserAccreditInfo,"根据用户的userId查询用户信息失败");
            purchaseOrder.setPurchasePerson(aclUserAccreditInfo.getName());         //赋值采购人的名称
        }
        dicts = configBiz.findDictsByTypeNo("currency");
        for (Dict dict:dicts) {
            if(dict.getValue().equals(purchaseOrder.getCurrencyType())){  //赋值币种的name
                purchaseOrder.setCurrencyTypeName(dict.getName());
            }
        }
        if(StringUtils.isNotBlank(purchaseOrder.getWarehouseCode())){
            Warehouse warehouse = new Warehouse();
            warehouse.setCode(purchaseOrder.getWarehouseCode());
            warehouse = warehouseService.selectOne(warehouse);
            AssertUtil.notNull(warehouse,"根据用户的仓库编码查询仓库信息失败");
            purchaseOrder.setWarehouseName(warehouse.getName());
        }
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

        this.setArea(purchaseOrder);
        return purchaseOrder;

    }

    private void setArea(PurchaseOrder purchaseOrder){
        String province = "";
        String city = "";
        Area area = new Area();
        if(StringUtils.isNotEmpty(purchaseOrder.getSenderProvince())){
            area.setCode(purchaseOrder.getSenderProvince());
            area = locationUtilService.selectOne(area);
            if(area != null){
                purchaseOrder.setSenderProvinceName(area.getProvince());
            }
        }
        if(StringUtils.isNotEmpty(purchaseOrder.getSenderCity())){
            area = new Area();
            area.setCode(purchaseOrder.getSenderCity());
            area = locationUtilService.selectOne(area);
            if(area != null){
                purchaseOrder.setSenderCityName(area.getCity());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PurchaseOrder findPurchaseOrderAddDataByCode(String purchaseCode) {

        AssertUtil.notBlank(purchaseCode,"采购单的编码为空!");
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderCode(purchaseCode);
        purchaseOrder = purchaseOrderService.selectOne(purchaseOrder);
        AssertUtil.notNull(purchaseOrder.getId(),"查询采购单信息失败!");
        //使用根据采购单id的方法直接查询采购单信息
        PurchaseOrder purchaseOrderSele = findPurchaseOrderAddDataById(purchaseOrder.getId());
        AssertUtil.notNull(purchaseOrderSele,"根据id查询采购单信息为空");
        return purchaseOrderSele;

    }

    @Override
    @CacheEvit
    public void updatePurchaseStateFreeze(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo)  {

        AssertUtil.notNull(purchaseOrder,"采购订单状态修改失败，采购订单信息为空");
        String status = purchaseOrder.getStatus();
        if(PurchaseOrderStatusEnum.PASS.getCode().equals(status)){ //需冻结
            PurchaseOrder tmp = new PurchaseOrder();
            tmp.setId(purchaseOrder.getId());
            tmp.setStatus(PurchaseOrderStatusEnum.FREEZE.getCode());
            tmp.setUpdateTime(Calendar.getInstance().getTime());
            int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
            if (count == 0) {
                String msg = String.format("冻结%s采购单操作失败", JSON.toJSONString(purchaseOrder));
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
            String userId= aclUserAccreditInfo.getUserId();
            PurchaseOrder purchaseOrderLog = new PurchaseOrder();
            purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
            logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.FREEZE.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
            return;
        }
        if(PurchaseOrderStatusEnum.FREEZE.getCode().equals(status)){ //需解冻
            PurchaseOrder tmp = new PurchaseOrder();
            tmp.setId(purchaseOrder.getId());
            tmp.setStatus(PurchaseOrderStatusEnum.PASS.getCode());
            int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
            tmp.setUpdateTime(Calendar.getInstance().getTime());
            if (count == 0) {
                String msg = String.format("解冻%s采购单操作失败", JSON.toJSONString(purchaseOrder));
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
            String userId= aclUserAccreditInfo.getUserId();
            PurchaseOrder purchaseOrderLog = new PurchaseOrder();
            purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
            logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.UN_FREEZE.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void updatePurchaseOrder(PurchaseOrderAddData purchaseOrderAddData,AclUserAccreditInfo aclUserAccreditInfo)  {

        AssertUtil.notNull(purchaseOrderAddData,"修改采购单失败,采购单为空");
        PurchaseOrder purchaseOrder = purchaseOrderAddData;//转型
        purchaseOrder.setTotalFee(purchaseOrder.getTotalFeeD().multiply(new BigDecimal(100)).longValue());//设置总价格*100
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
        if(paymentProportion!=null){
            BigDecimal bd = new BigDecimal("100");
            paymentProportion=paymentProportion.divide(bd);
            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
                String msg = "采购单修改,付款比例超出范围";
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(paymentProportion);
        }

        //如果提交采购单的方式为提交审核--则校验必须的数据
        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrder.getStatus())){
            assertArgs(purchaseOrderAddData);
        }

        //格式化时间
        this.formatDate(purchaseOrder);

        int count = purchaseOrderService.updateByPrimaryKeySelective(purchaseOrder);
        if (count == 0) {
            String msg = String.format("修改采购单%s数据库操作失败",JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }
        purchaseDetailService.deletePurchaseDetailByPurchaseOrderCode(purchaseOrderAddData.getPurchaseOrderCode());
        Object obj =aclUserAccreditInfo.getUserId();
        AssertUtil.notNull(obj,"采购单更新失败,获取授权信息失败");
        purchaseOrderAddData.setCreateOperator((String) obj);

        if(StringUtils.isNotBlank(purchaseOrderAddData.getGridValue()) && !"[]".equals(purchaseOrderAddData.getGridValue())){
            BigDecimal totalPrice = savePurchaseDetail(purchaseOrderAddData.getGridValue(),purchaseOrderAddData.getId(),purchaseOrderAddData.getPurchaseOrderCode(),purchaseOrderAddData.getCreateOperator(), purchaseOrder.getStatus());
            if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrder.getStatus())){
                if(totalPrice.compareTo(purchaseOrder.getTotalFeeD()) != 0){//比较实际采购价格与页面传输的价格是否相等
                    String msg = "采购单修改,采购商品的总价与页面的总价不相等";
                    LOGGER.error(msg);
                    throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
                }
            }
        }
        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrder.getStatus())){//后台检验提交审核：商品不能为空
            if(StringUtils.isBlank(purchaseOrderAddData.getGridValue()) && "[]".equals(purchaseOrderAddData.getGridValue())){
                String msg = "采购单修改,提交审核.采购商品不能为空!";
                LOGGER.error(msg);
                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
            }
        }

        //修改操作日志
        String userId= aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),null,ZeroToNineEnum.ZERO.getCode());

        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrder.getStatus())){ //修改提交审核
            updatePurchaseOrderAudit(purchaseOrderAddData,aclUserAccreditInfo);
        }

    }
    /**
     * 修改提交审核的采购信息
     */
    private void updatePurchaseOrderAudit(PurchaseOrderAddData purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo){

        AssertUtil.notNull(purchaseOrder,"采购订单提交审核失败，采购订单信息为空");
        AssertUtil.notNull(purchaseOrder.getPurchaseOrderCode(),"采购订单编码为空");

        PurchaseOrderAudit purchaseOrderAudit = new PurchaseOrderAudit();
        purchaseOrderAudit.setPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode());
        purchaseOrderAudit = iPurchaseOrderAuditService.selectOne(purchaseOrderAudit);
        if(purchaseOrderAudit == null){
            savePurchaseOrderAudit(purchaseOrder,aclUserAccreditInfo);
            return;
        }
        //AssertUtil.notNull(purchaseOrderAudit.getId(),"查询采购单审核失败!");
        PurchaseOrderAudit updatePurchaseOrderAudit = new PurchaseOrderAudit();
        updatePurchaseOrderAudit.setId(purchaseOrderAudit.getId());
        updatePurchaseOrderAudit.setStatus(ZeroToNineEnum.ONE.getCode());//待审核的状态
        updatePurchaseOrderAudit.setUpdateTime(Calendar.getInstance().getTime());
        int count = iPurchaseOrderAuditService.updateByPrimaryKeySelective(updatePurchaseOrderAudit);
        if (count == 0) {
            String msg = String.format("保存%s采购单审核操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

        //提交审核操作日志
        String userId=aclUserAccreditInfo.getUserId();
        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,AuditStatusEnum.COMMIT.getName(),null,ZeroToNineEnum.ZERO.getCode());

    }

    /***
     * 1.先根据采购订单编码查询，该采购单对应的skus编码
     * 2.比对前台提交的skuss
     *   传入的skus 有的删除， 剩下在原有的skus 也删除
     * @param
     */
    /*private void saveSkusAndDeleteSkus(String purchaseOrderStrs,PurchaseOrderAddData purchaseOrderAddData) {

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void warahouseAdvice(PurchaseOrder purchaseOrder, AclUserAccreditInfo aclUserAccreditInfo){

        AssertUtil.notNull(purchaseOrder,"采购单信息为空,保存入库通知单失败");
        AssertUtil.notNull(purchaseOrder.getId(),"采购单的主键为空,保存入库通知单失败");
        //根据采购单id,查询采购单的信息
        PurchaseOrder order = purchaseOrderService.selectByPrimaryKey(purchaseOrder.getId());
        AssertUtil.notNull(order,"根据主键查询该采购单为空");
        WarehouseNotice warehouseNotice = new WarehouseNotice();
        //这里没有继承commDao类，因此创建人要自己的代码处理
        Object obj = aclUserAccreditInfo.getUserId();
        AssertUtil.notNull(obj,"您的用户信息为空");
        warehouseNotice.setCreateOperator((String) obj);

        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setId(Long.parseLong(purchaseOrder.getWarehouseInfoId()));
        warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(purchaseOrder.getWarehouseId());
        warehouse  = warehouseService.selectOne(warehouse);

        assignmentWarehouseNotice(order,warehouseNotice, warehouseInfo, warehouse);
        int count = iWarehouseNoticeService.insert(warehouseNotice);
        if(count == 0){
            String msg = "保存入库通知单数据库操作失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }
         /* //查询该采购单对应的采购商品
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseId(purchaseOrder.getId());
        purchaseDetailService.select(purchaseDetail);

        //入库通知的商品表
        WarehouseNoticeDetails warehouseNoticeDetails = new WarehouseNoticeDetails();
        //warehouseNoticeDetails
        //warehouseNoticeDetailsService*/
        //更新采购单的状态
        PurchaseOrder _purchaseOrder = new PurchaseOrder();
        _purchaseOrder.setId(order.getId());
        _purchaseOrder.setStatus(PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
        _purchaseOrder.setEnterWarehouseNotice(WarehouseNoticeEnum.TO_BE_NOTIFIED.getCode());//待通知
        _purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        int sum = purchaseOrderService.updateByPrimaryKeySelective(_purchaseOrder);
        if(sum == 0){
            String msg = "更改采购单的状态,数据库操作失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }

        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseOrder,purchaseOrder.getId().toString(),userId,LogOperationEnum.WAREHOUSE_NOTICE.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        logInfoService.recordLog(warehouseNotice,warehouseNotice.getId().toString(),userId,LogOperationEnum.ADD.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        //生成入库通知商品明细
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseOrderCode(warehouseNotice.getPurchaseOrderCode());
        List<PurchaseDetail> purchaseDetails = purchaseDetailService.select(purchaseDetail);
        if(CollectionUtils.isEmpty(purchaseDetails)){
            String msg = String.format("采购单的编码[purchaseOrderCode=%s]的状态没有查到对应的采购商品,请核实该入库明细",warehouseNotice.getPurchaseOrderCode());
            LOGGER.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }

        insertWarehouseNoticeDetail(purchaseDetails,warehouseNotice.getWarehouseNoticeCode(), warehouseInfo.getChannelCode(),
                Long.parseLong(warehouseInfo.getWarehouseId()), warehouseInfo.getWarehouseOwnerId());

    }

    private void insertWarehouseNoticeDetail(List<PurchaseDetail> purchaseDetailList , String warehouseNoticeCode,
                                             String channelCode, Long warehouseId, String ownerCode){

        List<WarehouseNoticeDetails> warehouseNoticeDetails = new ArrayList<WarehouseNoticeDetails>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (PurchaseDetail purchaseDetail: purchaseDetailList) {
            SkuStock skuStock = new SkuStock();
            WarehouseNoticeDetails details = new WarehouseNoticeDetails();
            details.setWarehouseNoticeCode(warehouseNoticeCode);
            details.setBrandId(purchaseDetail.getBrandId());
            details.setCategoryId(purchaseDetail.getCategoryId());
            details.setSkuCode(purchaseDetail.getSkuCode());
            details.setSkuName(purchaseDetail.getSkuName());
            //details.setActualStorageQuantity(0L);//初始化0
            details.setPurchasingQuantity(purchaseDetail.getPurchasingQuantity());
            //details.setCreateTime(Calendar.getInstance().getTime());
            details.setPurchasePrice(purchaseDetail.getPurchasePrice());//采购价格
            //details.setStorageTime(details.getCreateTime());
            details.setBarCode(purchaseDetail.getBarCode());
            details.setSpecInfo(purchaseDetail.getSpecNatureInfo());
            details.setBatchNo(purchaseDetail.getBatchCode());
            details.setProductionCode(purchaseDetail.getProduceCode());
            try{
                details.setProductionDate(sdf.parse(purchaseDetail.getProductDate()));
                details.setExpiredDate(sdf.parse(purchaseDetail.getExpireDate()));
            }catch(Exception e){
                LOGGER.error("格式化时间错误", e);
            }
            details.setExpiredDay(purchaseDetail.getShelfLifeDays());

            skuStock.setChannelCode(channelCode);
            skuStock.setWarehouseId(warehouseId);
            skuStock.setSkuCode(purchaseDetail.getSkuCode());
            skuStock.setIsValid(ZeroToNineEnum.ONE.getCode());
            skuStock.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            skuStock = skuStockService.selectOne(skuStock);
            if(skuStock == null){
                String msg = String.format("仓库商品ID为%s的没有相应库存",purchaseDetail.getWarehouseItemId());
                LOGGER.error(msg);
                throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
            }
            details.setSkuStockId(skuStock.getId());
            details.setPurchaseAmount(purchaseDetail.getPurchasingQuantity() * purchaseDetail.getPurchasePrice());
            details.setStatus(Integer.parseInt(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode()));
            details.setOwnerCode(ownerCode);
            details.setItemId(purchaseDetail.getWarehouseItemId());
            details.setCreateTime(Calendar.getInstance().getTime());
            details.setNormalStorageQuantity(0L);
            details.setDefectiveStorageQuantity(0L);
            warehouseNoticeDetails.add(details);
        }
        int count = warehouseNoticeDetailsService.insertList(warehouseNoticeDetails);
        if(count < 1){
            String msg = String.format("入库通知的编码[warehouseNoticeCode=%s]保存入库通知明细初始化失败,无法进行入库通知的操作",warehouseNoticeCode);
            LOGGER.error(msg);
            throw new WarehouseNoticeException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION,msg);
        }

    }

    /**赋值入库通知单
     */
    private void assignmentWarehouseNotice(PurchaseOrder order, WarehouseNotice warehouseNotice, WarehouseInfo warehouseInfo, Warehouse warehouse){
        //'入库通知单编号',流水的长度为5,前缀为CGRKTZ,加时间
        String warehouseNoticeCode = iSerialUtilService.generateCode(5,CGRKTZ,DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        warehouseNotice.setWarehouseNoticeCode(warehouseNoticeCode);
        warehouseNotice.setPurchaseOrderCode(order.getPurchaseOrderCode());
        warehouseNotice.setContractCode(order.getContractCode());
        warehouseNotice.setPurchaseGroupCode(order.getPurchaseGroupCode());
        warehouseNotice.setWarehouseId(order.getWarehouseId());
        warehouseNotice.setWarehouseCode(order.getWarehouseCode());
        //'状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
        warehouseNotice.setStatus(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
        warehouseNotice.setSupplierId(order.getSupplierId());
        warehouseNotice.setSupplierCode(order.getSupplierCode());
        warehouseNotice.setPurchaseType(order.getPurchaseType());
        warehouseNotice.setPurchasePersonId(order.getPurchasePersonId());
        warehouseNotice.setTakeGoodsNo(order.getTakeGoodsNo());
        warehouseNotice.setRequriedReceiveDate(order.getRequriedReceiveDate());
        warehouseNotice.setEndReceiveDate(order.getEndReceiveDate());
        warehouseNotice.setRemark("新增入库通知单");
        warehouseNotice.setCreateTime(Calendar.getInstance().getTime());
        warehouseNotice.setUpdateTime(Calendar.getInstance().getTime());

        warehouseNotice.setFailureCause("无");
        warehouseNotice.setExceptionCause("无");

        warehouseNotice.setChannelCode(order.getChannelCode());
        warehouseNotice.setWarehouseInfoId(warehouseInfo.getId());
        warehouseNotice.setOwnerCode(warehouseInfo.getWarehouseOwnerId());
        warehouseNotice.setQimenWarehouseCode(warehouseInfo.getQimenWarehouseCode());
        warehouseNotice.setSender(order.getSender());
        warehouseNotice.setReceiverNumber(order.getReceiverNumber());
        warehouseNotice.setReceiver(order.getReceiver());
        Area area =new Area();
        area.setCode(order.getSenderProvince());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "发件人所在省为空");
        warehouseNotice.setSenderProvince(area.getProvince());
        area =new Area();
        area.setCode(order.getSenderCity());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "发件人所在城市为空");
        warehouseNotice.setSenderCity(area.getCity());
        warehouseNotice.setSenderAddress(order.getSenderAddress());
        warehouseNotice.setSenderNumber(order.getSenderNumber());
        area =new Area();
        area.setCode(warehouse.getProvince());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "收件人所在省为空");
        warehouseNotice.setReceiverProvince(area.getProvince());
        area =new Area();
        area.setCode(warehouse.getCity());
        area = locationUtilService.selectOne(area);
        AssertUtil.notNull(area, "收件人所在城市为空");
        warehouseNotice.setReceiverCity(area.getCity());
        warehouseNotice.setReceiverAddress(warehouse.getAddress());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void cancelWarahouseAdvice(PurchaseOrder purchaseOrder, AclUserAccreditInfo aclUserAccreditInfo) {

        AssertUtil.notNull(purchaseOrder,"采购单的信息为空");
        //更改采购单的状态
        PurchaseOrder tmp = new PurchaseOrder();
        tmp.setId(purchaseOrder.getId());
        tmp.setStatus(PurchaseOrderStatusEnum.CANCEL.getCode());
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        //是否已经发起入库通知，设为""
        tmp.setEnterWarehouseNotice("");
        int count = purchaseOrderService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = String.format("作废%s采购单操作失败", JSON.toJSONString(purchaseOrder));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_UPDATE_EXCEPTION, msg);
        }


        //更改入库通知单的状态;在修改状态之前。判断入库通知单的状态，是否为待发起入库通知/乐观锁
        WarehouseNotice warehouseNotice = new WarehouseNotice();
        warehouseNotice.setPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode());
        warehouseNotice = iWarehouseNoticeService.selectOne(warehouseNotice);
        if(!warehouseNotice.getStatus().equals(WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode())){
            //说明入库通知单已经被推送给仓储,取消失败
            // String msg = String.format("作废%s入库通知单操作失败", JSON.toJSONString(warehouseNotice));
            String msg = "入库通知单已经被推送给仓储,取消失败";
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }

        //更改入库通知单的状态--用自身的‘待发起入库通知状态’,作为判断是否执行作废的操作
        WarehouseNotice notice = new WarehouseNotice();
        notice.setStatus(WarehouseNoticeStatusEnum.DROPPED.getCode());
        notice.setUpdateTime(Calendar.getInstance().getTime());
        Example example = new Example(WarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",warehouseNotice.getId());
        criteria.andEqualTo("status",WarehouseNoticeStatusEnum.WAREHOUSE_NOTICE_RECEIVE.getCode());
        int num = iWarehouseNoticeService.updateByExampleSelective(notice,example);
        if (num == 0) {
            String msg = String.format("作废%s采购单操作失败,入库通知单已经被执行操作", JSON.toJSONString(warehouseNotice));
            LOGGER.error(msg);
            throw new PurchaseOrderException(ExceptionEnum.WAREHOUSE_NOTICE_UPDATE_EXCEPTION, msg);
        }
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseOrder,purchaseOrder.getId().toString(),userId,LogOperationEnum.CANCEL.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
        logInfoService.recordLog(warehouseNotice,warehouseNotice.getId().toString(),userId,LogOperationEnum.CANCEL.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
    }
    @Override
    public   List<String> associationSearch(String queryString) throws Exception{
        List<String> brandNameList = new ArrayList<>();
        return brandNameList;
    }

    @Override
    public List<SupplierBrandExt> findSupplierBrand(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode,"供应商的编码为空!");

        List<SupplierBrandExt> supplierBrandExts = iSupplierBrandService.selectSupplierBrandNames(supplierCode);

        return supplierBrandExts;
    }

    @Override
    @CacheEvit
    public void cacheEvitForPurchaseOrder() {}

}
