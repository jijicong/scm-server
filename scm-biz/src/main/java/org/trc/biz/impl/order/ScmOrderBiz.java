package org.trc.biz.impl.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.*;
import org.trc.enums.*;
import org.trc.exception.OrderException;
import org.trc.exception.ParamValidException;
import org.trc.form.*;
import org.trc.form.JDModel.*;
import org.trc.form.liangyou.LiangYouOrder;
import org.trc.form.liangyou.OutOrderGoods;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IJDService;
import org.trc.service.ITrcService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.order.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.ws.rs.container.ContainerRequestContext;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Service("scmOrderBiz")
public class ScmOrderBiz implements IScmOrderBiz {

    private Logger log = LoggerFactory.getLogger(ScmOrderBiz.class);

    //创建线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);

    //京东地址分隔符
    public final static String JING_DONG_ADDRESS_SPLIT = "/";

    public final static String FLAG_EXT = "-";
    //html换行符
    public final static String HTML_BR = "<br>";

    public final static String F = "F";

    public final static String DISTRICT = "区";

    //供应商下单接口调用失败重试次数
    public final static int SUBMIT_SUPPLIER_ORDER_FAILURE_TIMES = 3;

    @Value("${channel.orderMoneyCheck}")
    private String channelOrderMoneyCheck;

    @Autowired
    private IShopOrderService shopOrderService;
    @Autowired
    private IPlatformOrderService platformOrderService;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private IWarehouseOrderService warehouseOrderService;
    @Autowired
    private ISupplierOrderInfoService supplierOrderInfoService;
    @Autowired
    private IJDService ijdService;
    @Autowired
    private IExternalItemSkuService externalItemSkuService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private IOrderFlowService orderFlowService;
    @Autowired
    private ITrcService trcService;
    @Autowired
    private ISupplierOrderLogisticsService supplierOrderLogisticsService;
    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    @Autowired
    private ISkuRelationService skuRelationService;
    @Autowired
    private IRequestFlowBiz requestFlowBiz;
    @Autowired
    private ILogInfoService logInfoService;

    @Value("{trc.jd.logistic.url}")
    private String TRC_JD_LOGISTIC_URL;

    private String SP0 = "SP0";

    private String SP1 = "SP1";

    private String ONE = "1";

    private String ZERO = "0";

    //业务类型：交易
    public final static String BIZ_TYPE_DEAL = "DEAL";

    //京东下单价格不匹配错误代码
    public final static String JD_ORDER_SUBMIT_PRICE_ERROR = "3019";

    @Override
    public Pagenation<ShopOrder> shopOrderPage(ShopOrderForm queryModel, Pagenation<ShopOrder> page) {
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getPlatformOrderCode())) {//平台订单编码
            criteria.andLike("platformOrderCode", "%" + queryModel.getPlatformOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopOrderCode())) {//店铺订单编码
            criteria.andLike("shopOrderCode", "%" + queryModel.getShopOrderCode() + "%");
        }
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(queryModel, ZeroToNineEnum.ZERO.getCode());
        if(null != platformOrderList){
            if(platformOrderList.size() > 0){
                List<String> platformOrderCodeList = new ArrayList<String>();
                for(PlatformOrder platformOrder: platformOrderList){
                    platformOrderCodeList.add(platformOrder.getPlatformOrderCode());
                }
                if(platformOrderCodeList.size() > 0){
                    criteria.andIn("platformOrderCode", platformOrderCodeList);
                }
            }else {
                return page;
            }
        }
        page = shopOrderService.pagination(example, page, new QueryModel());
        if(page.getResult().size() > 0){
            handlerOrderInfo(page, platformOrderList);
        }
        return page;
    }

    @Override
    public Pagenation<WarehouseOrder> warehouseOrderPage(WarehouseOrderForm form, Pagenation<WarehouseOrder> page) {
        AssertUtil.notNull(form, "查询供应商订单分页参数不能为空");
        Example example = new Example(WarehouseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(form.getOrderType())){//订单类型
            criteria.andEqualTo("orderType", form.getOrderType());
        }
        if(StringUtils.isNotBlank(form.getSupplierOrderStatus())){//状态
            criteria.andEqualTo("supplierOrderStatus", form.getSupplierOrderStatus());
        }
        if(StringUtils.isNotBlank(form.getWarehouseOrderCode())){//供应商订单编号
            criteria.andLike("warehouseOrderCode", "%" + form.getWarehouseOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getShopOrderCode())){//店铺订单号
            criteria.andLike("shopOrderCode", "%" + form.getShopOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getSupplierCode())){//供应商编码
            criteria.andEqualTo("supplierCode", form.getSupplierCode());
        }
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(form, ZeroToNineEnum.ONE.getCode());
        if(null != platformOrderList){
            if(platformOrderList.size() > 0){
                List<String> platformOrderCodeList = new ArrayList<String>();
                for(PlatformOrder platformOrder: platformOrderList){
                    platformOrderCodeList.add(platformOrder.getPlatformOrderCode());
                }
                if(platformOrderCodeList.size() > 0){
                    criteria.andIn("platformOrderCode", platformOrderCodeList);
                }
            }else {
                return page;
            }
        }
        example.orderBy("status").asc();
        page = warehouseOrderService.pagination(example, page, form);
        handlerWarehouseOrderInfo(page, platformOrderList);
        return page;
    }

    @Override
    public List<ShopOrder> queryShopOrders(ShopOrderForm form) {
        AssertUtil.notNull(form, "查询商铺订单列表参数不能为空");
        ShopOrder shopOrder = new ShopOrder();
        BeanUtils.copyProperties(form, shopOrder);
        List<ShopOrder> shopOrderList = shopOrderService.select(shopOrder);
        AssertUtil.notEmpty(shopOrderList, String.format("根据条件%s查询商铺订单为空", JSONObject.toJSON(form)));
        List<String> platformOrderCodeList = new ArrayList<String>();
        for(ShopOrder shopOrder2: shopOrderList){
            platformOrderCodeList.add(shopOrder2.getPlatformOrderCode());
        }
        List<PlatformOrder> platformOrderList = new ArrayList<PlatformOrder>();
        if(platformOrderCodeList.size() > 0){
            Example example = new Example(PlatformOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("platformOrderCode", platformOrderCodeList);
            platformOrderList = platformOrderService.selectByExample(example);
            AssertUtil.notEmpty(platformOrderList, String.format("根据平台订单编号[%s]查询平台订单为空",
                    CommonUtil.converCollectionToString(platformOrderCodeList)));
        }
        if(platformOrderList.size() > 0){
            for(ShopOrder shopOrder2: shopOrderList){
                for(PlatformOrder platformOrder: platformOrderList){
                    if(StringUtils.equals(shopOrder2.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                        setShopOrderItemsDetail(shopOrder2, platformOrder);
                    }
                }
            }
        }
        return shopOrderList;
    }

    @Override
    public WarehouseOrder queryWarehouseOrdersDetail(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "查询仓库订单明细参数仓库订单编码不能为空");
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("根据仓库订单编码[%s]查询仓库订单为空",warehouseOrderCode));
        //查询品台订单
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("根据平台订单编码[%s]查询平台订单为空", warehouseOrder.getPlatformOrderCode()));
        warehouseOrder.setPlatformOrder(platformOrder);
        //查询商品明细
        OrderItem orderItem = new OrderItem();
        orderItem.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        orderItem.setShopOrderCode(warehouseOrder.getShopOrderCode());
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据平台订单编号[%s]和商铺订单编号[%s]查询订单商品明细为空",
                warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode()));
        warehouseOrder.setOrderItemList(orderItemList);
        return warehouseOrder;
    }

    @Override
    public List<PlatformOrder> queryPlatformOrders(PlatformOrderForm form) {
        AssertUtil.notNull(form, "查询平台订单列表参数对象不能为空");
        PlatformOrder platformOrder = new PlatformOrder();
        BeanUtils.copyProperties(form, platformOrder);
        return platformOrderService.select(platformOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppResult submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单京东订单仓库订单编码不能为空");
        AssertUtil.notBlank(jdAddressCode, "提交订单京东订单四级地址编码不能为空");
        AssertUtil.notBlank(jdAddressName, "提交订单京东订单四级地址不能为空");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressCode, "提交订单京东订单四级地址编码格式错误");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressName, "提交订单京东订单四级地址格式错误");
        //获取京东四级地址
        String[] jdAddressCodes = jdAddressCode.split(JING_DONG_ADDRESS_SPLIT);
        String[] jdAddressNames = jdAddressName.split(JING_DONG_ADDRESS_SPLIT);
        AssertUtil.isTrue(jdAddressCodes.length == jdAddressNames.length, "京东四级地址编码与名称个数不匹配");
        //适配京东地址
        /*Map<String, String[]> addressMap =  adapterJingDongAddress(jdAddressCodes, jdAddressNames);
        jdAddressCodes = addressMap.get("jdAddressCodes");
        jdAddressNames = addressMap.get("jdAddressNames");*/
        AppResult appResult = null;
        //获取供应链订单数据
        Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
        PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
        WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
        List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
        //获取京东订单对象
        JingDongOrder jingDongOrder = getJingDongOrder(warehouseOrder, platformOrder, orderItemList, jdAddressCodes);
        //更新仓库订单供应商订单状态为已发送
        updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), SupplierOrderStatusEnum.SUBMIT.getCode());
        String supplierOrderStatus = SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode();
        //调用京东下单服务接口
        ReturnTypeDO returnTypeDO = invokeSubmitSuuplierOrder(jingDongOrder);
        //保存请求流水
        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(jingDongOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.JINGDONG, RequestFlowTypeEnum.JD_SUBMIT_ORDER, returnTypeDO, RequestFlowConstant.GYL);
        if(returnTypeDO.getSuccess()){
            log.info(String.format("调用京东下单接口提交订单%s成功", JSONObject.toJSON(jingDongOrder)));
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.SUBMIT_JINGDONG_ORDER.getMessage(), null,null);
        }else{
            if(StringUtils.isNotBlank(returnTypeDO.getResultCode()) && StringUtils.equals(JD_ORDER_SUBMIT_PRICE_ERROR, returnTypeDO.getResultCode())){
                log.info(String.format("调用京东下单商品价格不匹配,更新京东最新价格重新下单"));
                returnTypeDO = reSubmitJingDongOrder(jingDongOrder);
                if(!returnTypeDO.getSuccess()){
                    supplierOrderStatus = SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode();
                }
                //保存请求流水
                requestFlowBiz.saveRequestFlow(JSONObject.toJSON(jingDongOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.JINGDONG, RequestFlowTypeEnum.JD_SKU_PRICE_UPDATE_SUBMIT_ORDER, returnTypeDO, RequestFlowConstant.GYL);
                //记录操作日志
                logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), LogOperationEnum.SUBMIT_JINGDONG_ORDER.getMessage(), null,null);
            }else{
                supplierOrderStatus = SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode();
                log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), returnTypeDO.getResultMessage()));
            }
        }
        //根据仓库订单下单结果更新供应商订单状态
        updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderStatus);
        //保存京东订单信息
        saveSupplierOrderInfo(warehouseOrder, returnTypeDO, orderItemList, jdAddressCodes, jdAddressNames, ZeroToNineEnum.ZERO.getCode());
        if(returnTypeDO.getSuccess()){
            String msg = String.format("提交仓库级订单编码为[%s]的京东订单下单成功", warehouseOrderCode);
            log.info(msg);
            appResult = ResultUtil.createSucssAppResult(msg,"");
        }else{
            String msg = String.format("提交仓库级订单编码为[%s]的京东订单下单失败。京东下单接口返回错误信息:%s",
                    warehouseOrderCode, returnTypeDO.getResultMessage());
            log.error(msg);
            appResult = ResultUtil.createFailAppResult(msg);
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), LogOperationEnum.SUBMIT_ORDER_FIALURE.getMessage(), returnTypeDO.getResultMessage(),null);
        }
        //下单结果通知渠道
        try{
            notifyChannelSubmitOrderResult(warehouseOrder);
        }catch (Exception e){
            String msg = String.format("仓库级订单编码为[%s]的京东订单下单结果通知渠道异常,异常信息:%s",
                    warehouseOrderCode, appResult.getDatabuffer());
            log.error(msg, e);
            appResult = ResultUtil.createFailAppResult(msg);
        }
        return appResult;
    }

    /**
     * 重新提交京东订单
     * @param jingDongOrder
     * @return
     */
    private ReturnTypeDO reSubmitJingDongOrder(JingDongOrder jingDongOrder){
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        StringBuilder sku = new StringBuilder();
        for(OrderPriceSnap orderPriceSnap: jingDongOrder.getOrderPriceSnap()){
            sku.append(orderPriceSnap.getSkuId()).append(SupplyConstants.Symbol.COMMA);
        }
        String skus = "";
        if(sku.length() > 0)
            skus = sku.substring(0, sku.length()-1);
        else{
            String msg = String.format("京东订单提交数据中%s订单价格快照为空", JSON.toJSONString(jingDongOrder));
            log.error(msg);
            returnTypeDO.setResultMessage(msg);
            return returnTypeDO;
        }
        //调用京东sku价格查询接口
        ReturnTypeDO returnPrice = ijdService.getSellPrice(skus);
        if(!returnPrice.getSuccess()){
            String msg = String.format("调用京东sku价格查询服务失败,错误信息:%s", returnPrice.getResultMessage());
            log.error(msg);
            returnTypeDO.setResultMessage(msg);
            return returnTypeDO;
        }
        List<SupplyItemsUpdate> supplyItemsUpdates = JSONArray.parseArray(returnPrice.getResult().toString(), SupplyItemsUpdate.class);
        //更新京东订单数据中的订单价格快照里面的sku价格
        for(OrderPriceSnap orderPriceSnap: jingDongOrder.getOrderPriceSnap()){
            for(SupplyItemsUpdate supplyItemsUpdate: supplyItemsUpdates){
                if(StringUtils.equals(orderPriceSnap.getSkuId().toString(), supplyItemsUpdate.getSkuId())){
                    orderPriceSnap.setPrice(supplyItemsUpdate.getPrice());
                }
            }
        }
        //提交京东订单
        returnTypeDO = invokeSubmitSuuplierOrder(jingDongOrder);
        if(returnTypeDO.getSuccess()){
            log.info(String.format("调用京东下单接口提交订单%s成功", JSONObject.toJSON(jingDongOrder)));
        }else{
            log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), returnTypeDO.getResultMessage()));
        }
        return returnTypeDO;
    }


    /**
     * 获取供应链订单map
     * @param warehouseOrderCode
     * @return
     */
    private Map<String, Object> getScmOrderMap(String warehouseOrderCode){
        Map<String, Object> map = new HashMap<>();
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("根据仓库订单编码[%s]查询仓库订单为空", warehouseOrderCode));
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("根据平台订单编码[%s]查询平台订单为空", warehouseOrder.getPlatformOrderCode()));
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单编码[%s]查询订单商品明细信息为空", warehouseOrderCode));
        Set<String> skuCodeList = new HashSet<String>();
        for(OrderItem orderItem2: orderItemList){
            skuCodeList.add(orderItem2.getSkuCode());
        }
        //查询代发商品信息
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        List<String> skuCodeList2 = new ArrayList<String>();
        skuCodeList2.addAll(skuCodeList);
        AssertUtil.notEmpty(externalItemSkuList, String.format("根据多个代发商品sku编码[%s]查询代发商品信息为空", CommonUtil.converCollectionToString(skuCodeList2)));
        //将订单商品明细中的skuCode替换成供应商skuCode
        for(OrderItem orderItem2: orderItemList){
            for(ExternalItemSku externalItemSku: externalItemSkuList){
                if(StringUtils.equals(orderItem2.getSkuCode(), externalItemSku.getSkuCode())){
                    orderItem2.setSkuCode(externalItemSku.getSupplierSkuCode());
                }
            }
        }
        map.put("platformOrder", platformOrder);
        map.put("warehouseOrder", warehouseOrder);
        map.put("orderItemList", orderItemList);
        return map;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AppResult submitLiangYouOrder(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单粮油订单仓库订单编码不能为空");
        //获取供应链订单数据
        Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
        PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
        WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
        List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
        //获取粮油订单对象
        LiangYouOrder liangYouOrder = getLiangYouOrder(warehouseOrder, platformOrder, orderItemList);
        //更新仓库订单供应商订单状态为已发送
        updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), SupplierOrderStatusEnum.SUBMIT.getCode());
        String supplierOrderStatus = SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode();
        //调用粮油下单服务接口
        ReturnTypeDO returnTypeDO = invokeSubmitSuuplierOrder(liangYouOrder);
        //保存请求流水
        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(liangYouOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.LY, RequestFlowTypeEnum.LY_SUBMIT_ORDER, returnTypeDO, RequestFlowConstant.GYL);
        if(returnTypeDO.getSuccess()){
            log.info(returnTypeDO.getResultMessage());
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), LogOperationEnum.SUBMIT_ORDER.getMessage(), null,null);
        }else{
            supplierOrderStatus = SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode();
            log.error(returnTypeDO.getResultMessage());
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), LogOperationEnum.SUBMIT_ORDER_FIALURE.getMessage(), returnTypeDO.getResultMessage(),null);
        }
        if(returnTypeDO.getSuccess()){
            log.info(String.format("调用京东下单接口提交仓库订单%s成功", JSONObject.toJSON(warehouseOrder)));
        }else{
            supplierOrderStatus = SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode();
            log.error(String.format("调用京东下单接口提交仓库订单%s失败,错误信息:%s", JSONObject.toJSON(warehouseOrder), returnTypeDO.getResultMessage()));
        }
        //根据仓库订单下单结果更新供应商订单状态
        updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderStatus);
        //保存粮油订单信息
        saveSupplierOrderInfo(warehouseOrder, returnTypeDO, orderItemList, new String[0], new String[0], ZeroToNineEnum.ZERO.getCode());
        if(returnTypeDO.getSuccess()){
            return ResultUtil.createSucssAppResult(String.format("提交仓库级订单编码为[%s]的粮油订单下单成功", warehouseOrderCode),"");
        }else{
            String msg = String.format("提交仓库级订单编码为[%s]的粮油订单下单失败。粮油下单接口返回错误信息:%s",
                    warehouseOrderCode, returnTypeDO.getResultMessage());
            log.error(msg);
            return ResultUtil.createFailAppResult(msg);
        }
    }

    @Override
    public void saveChannelOrderRequestFlow(String orderInfo, AppResult appResult) {
        requestFlowBiz.saveRequestFlow(orderInfo, RequestFlowConstant.TRC, RequestFlowConstant.GYL, RequestFlowTypeEnum.RECEIVE_CHANNEL_ORDER, appResult, RequestFlowConstant.GYL);
    }

    /**
     * 获取京东订单信息
     * @param warehouseOrder
     * @param platformOrder
     * @param orderItemList
     * @param jdAddressCodes
     * @return
     */
    private JingDongOrder getJingDongOrder(WarehouseOrder warehouseOrder, PlatformOrder platformOrder, List<OrderItem> orderItemList, String[] jdAddressCodes){
        JingDongOrder jingDongOrder = new JingDongOrder();
        jingDongOrder.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        jingDongOrder.setThirdOrder(warehouseOrder.getWarehouseOrderCode());
        jingDongOrder.setName(platformOrder.getReceiverName());
        if(jdAddressCodes.length > 0){
            jingDongOrder.setProvince(jdAddressCodes[0]);
            jingDongOrder.setInvoiceProvice(Integer.parseInt(jdAddressCodes[0]));
        }
        if(jdAddressCodes.length > 1){
            jingDongOrder.setCity(jdAddressCodes[1]);
            jingDongOrder.setInvoiceCity(Integer.parseInt(jdAddressCodes[1]));
        }
        if(jdAddressCodes.length > 2){
            jingDongOrder.setCounty(jdAddressCodes[2]);
            jingDongOrder.setInvoiceCounty(Integer.parseInt(jdAddressCodes[2]));
        }
        if(jdAddressCodes.length > 3){
            jingDongOrder.setTown(jdAddressCodes[3]);
        }
        if(StringUtils.isBlank(jingDongOrder.getTown()))
            jingDongOrder.setTown(ZeroToNineEnum.ZERO.getCode());//没有四级地址传0
        jingDongOrder.setEmail(platformOrder.getReceiverEmail());
        jingDongOrder.setAddress(platformOrder.getReceiverAddress());
        jingDongOrder.setZip(platformOrder.getReceiverZip());
        jingDongOrder.setPhone(platformOrder.getReceiverPhone());
        jingDongOrder.setMobile(platformOrder.getReceiverMobile());
        jingDongOrder.setRemark("");// TODO 备注信息
        jingDongOrder.setInvoiceState(JdInvoiceStateEnum.FOCUS.getCode());//目前只能选择2-集中开票
        jingDongOrder.setInvoiceType(JdInvoiceTypeEnum.VALUE_ADDED_TAX.getCode());//目前只支持：2-增值税发票
        jingDongOrder.setSelectedInvoiceTitle(JdInvoiceTitleEnum.COMPANY.getCode());//目前只能选择5-单位
        jingDongOrder.setCompanyName(externalSupplierConfig.getCompanyName());//目前都填写成固定值
        jingDongOrder.setInvoiceContent(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));////目前选择1-明细
        jingDongOrder.setPaymentType(JdPaymentTypeEnum.ON_LINE.getCode());
        jingDongOrder.setIsUseBalance(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));
        jingDongOrder.setSubmitState(Integer.parseInt(ZeroToNineEnum.ZERO.getCode()));//预占库存
        jingDongOrder.setInvoiceName(platformOrder.getReceiverName());
        jingDongOrder.setInvoiceAddress(platformOrder.getReceiverAddress());
        jingDongOrder.setDoOrderPriceMode(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));//下单价格模式,1-必需验证客户端订单价格快照
        // TODO 大家电、中小件配送安装参数设置
        //设置京东订单SKU信息
        setJdOrderSkuInfo(jingDongOrder, orderItemList);
        return jingDongOrder;
    }


    /**
     * 获取粮油订单信息
     * @param warehouseOrder
     * @param platformOrder
     * @param orderItemList
     * @return
     */
    private LiangYouOrder getLiangYouOrder(WarehouseOrder warehouseOrder, PlatformOrder platformOrder, List<OrderItem> orderItemList){
        LiangYouOrder liangYouOrder = new LiangYouOrder();
        liangYouOrder.setShopOrderCode(warehouseOrder.getShopOrderCode());
        liangYouOrder.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        liangYouOrder.setConsignee(platformOrder.getReceiverName());
        liangYouOrder.setRealName(platformOrder.getReceiverName());
        liangYouOrder.setImId(platformOrder.getReceiverIdCard());
        liangYouOrder.setDisType(F);
        liangYouOrder.setPhoneMob(platformOrder.getReceiverMobile());
        liangYouOrder.setAddress(platformOrder.getReceiverAddress());
        liangYouOrder.setProvince(platformOrder.getReceiverProvince());
        liangYouOrder.setCity(platformOrder.getReceiverCity());
        liangYouOrder.setCounty(platformOrder.getReceiverDistrict());
        List<OutOrderGoods> outOrderGoodsList = new ArrayList<OutOrderGoods>();
        for(OrderItem orderItem: orderItemList){
            OutOrderGoods outOrderGoods = new OutOrderGoods();
            outOrderGoods.setGoodsName(orderItem.getItemName());
            outOrderGoods.setOnlySku(orderItem.getSupplierSkuCode());
            outOrderGoods.setQuantity(orderItem.getNum());
            outOrderGoodsList.add(outOrderGoods);
        }
        liangYouOrder.setOutOrderGoods(outOrderGoodsList);
        return liangYouOrder;
    }

    /**
     * 设置京东订单SKU信息
     * @param jingDongOrder
     * @param orderItemList
     */
    private void setJdOrderSkuInfo(JingDongOrder jingDongOrder, List<OrderItem> orderItemList){
        List<JdSku> jdSkuList = new ArrayList<JdSku>();
        List<OrderPriceSnap> orderPriceSnapList = new ArrayList<OrderPriceSnap>();
        for(OrderItem orderItem2: orderItemList){
            JdSku jdSku = new JdSku();
            jdSku.setSkuId(orderItem2.getSupplierSkuCode());
            jdSku.setNum(orderItem2.getNum());
            jdSku.setbNeedAnnex(true);
            jdSku.setbNeedGift(false);
            jdSkuList.add(jdSku);

            OrderPriceSnap orderPriceSnap = new OrderPriceSnap();
            orderPriceSnap.setSkuId(Long.parseLong(orderItem2.getSupplierSkuCode()));
            orderPriceSnap.setPrice(orderItem2.getTransactionPrice());
            orderPriceSnapList.add(orderPriceSnap);
        }
        jingDongOrder.setSku(jdSkuList);
        jingDongOrder.setOrderPriceSnap(orderPriceSnapList);
    }

    /**
     *保存供应商订单信息
     * @param warehouseOrder
     * @param returnTypeDO
     * @param jdAddressCodes
     * @param jdAddressNames
     * @param flag : 0-京东订单,1-粮油订单
     */
    private void saveSupplierOrderInfo(WarehouseOrder warehouseOrder,
        ReturnTypeDO returnTypeDO, List<OrderItem> orderItemList, String[] jdAddressCodes, String[] jdAddressNames, String flag){
        List<SupplierOrderInfo> supplierOrderInfoList = new ArrayList<SupplierOrderInfo>();
        if(returnTypeDO.getSuccess()){
            JSONObject orderObj = null;
            try {
                orderObj = JSONObject.parseObject(returnTypeDO.getResult().toString());
            } catch (ClassCastException e) {
                String msg = String.format("调用下单服务返回结果不是JSON格式");
                log.error(msg, e);
                throw new OrderException(ExceptionEnum.SUBMIT_JING_DONG_ORDER, msg);
            }
            AssertUtil.notNull(orderObj, "调用下单服务返回结为空");
            JSONArray orders = orderObj.getJSONArray("order");
            for(Object order: orders){
                JSONObject orderInfo = (JSONObject)order;
                SupplierOrderInfo supplierOrderInfo = getSupplierOrderInfo(warehouseOrder, orderInfo, jdAddressCodes, jdAddressNames, flag);
                supplierOrderInfoList.add(supplierOrderInfo);
            }
        }else{
            log.error(String.format("调用下单服务接口失败,错误信息: %s", returnTypeDO.getResultMessage()));
            SupplierOrderInfo supplierOrderInfo = getSupplierOrderFailureInfo(warehouseOrder, orderItemList);
            supplierOrderInfoList.add(supplierOrderInfo);
        }
        supplierOrderInfoService.insertList(supplierOrderInfoList);
    }

    /**
     * 获取供应商订单
     * @param warehouseOrder
     * @param orderInfo
     * @param jdAddressCodes
     * @param jdAddressNames
     * @param flag
     * @return
     */
    private SupplierOrderInfo getSupplierOrderInfo(WarehouseOrder warehouseOrder, JSONObject orderInfo, String[] jdAddressCodes, String[] jdAddressNames, String flag){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
        String supplyOrderCode = orderInfo.getString("supplyOrderCode");//供应商订单号
        String state = orderInfo.getString("state");//下单状态
        JSONArray skusArray = orderInfo.getJSONArray("skus");//订单相关sku
        supplierOrderInfo.setSupplierOrderCode(supplyOrderCode);
        supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());//未完成
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), state)){//供应商下单接口下单成功
            supplierOrderInfo.setStatus(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode());//待发货
        }else{
            supplierOrderInfo.setStatus(SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode());//下单失败
        }
        supplierOrderInfo.setSkus(skusArray.toJSONString());
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//京东订单
            if(jdAddressCodes.length > 0){
                supplierOrderInfo.setJdProvinceCode(jdAddressCodes[0]);
                supplierOrderInfo.setJdProvince(jdAddressNames[0]);
            }
            if(jdAddressCodes.length > 1){
                supplierOrderInfo.setJdCityCode(jdAddressCodes[1]);
                supplierOrderInfo.setJdCity(jdAddressNames[1]);
            }
            if(jdAddressCodes.length > 2){
                supplierOrderInfo.setJdDistrictCode(jdAddressCodes[2]);
                supplierOrderInfo.setJdDistrict(jdAddressNames[2]);
            }
            if(jdAddressCodes.length > 3){
                supplierOrderInfo.setJdTownCode(jdAddressCodes[3]);
                supplierOrderInfo.setJdTown(jdAddressNames[3]);
            }
        }
        ParamsUtil.setBaseDO(supplierOrderInfo);
        return supplierOrderInfo;
    }

    /**
     * 获取失败的供应商订单信息
     * @param warehouseOrder
     * @param orderItemList
     * @return
     */
    private SupplierOrderInfo getSupplierOrderFailureInfo(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
        supplierOrderInfo.setStatus(SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode());//下单失败
        supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());//未完成
        List<SkuInfo> skuInfoList = new ArrayList<SkuInfo>();
        for(OrderItem orderItem: orderItemList){
            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setSkuCode(orderItem.getSkuCode());
            skuInfo.setSkuName(orderItem.getItemName());
            skuInfo.setNum(orderItem.getNum());
            skuInfoList.add(skuInfo);
        }
        supplierOrderInfo.setSkus(JSON.toJSONString(skuInfoList));
        ParamsUtil.setBaseDO(supplierOrderInfo);
        return supplierOrderInfo;
    }


    /**
     * 调用京东下单服务接口
     * @param orderInfo
     * @return
     */
    private ReturnTypeDO invokeSubmitSuuplierOrder(Object orderInfo){
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        if(orderInfo instanceof JingDongOrder){
            returnTypeDO = ijdService.submitJingDongOrder((JingDongOrder)orderInfo);
        }else if(orderInfo instanceof LiangYouOrder){
            returnTypeDO = ijdService.submitLiangYouOrder((LiangYouOrder)orderInfo);
        }else{
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "提交订单到供应商商接口参数类型错误");
        }
        return returnTypeDO;
    }

    /**
     * 获取平台订单编码列表条件
     * @param queryModel
     * @param flag 0-店铺订单分页查询,1-仓库订单分页查询
     * @return
     */
    private List<PlatformOrder> getPlatformOrdersConditon(QueryModel queryModel, String flag){
        Example example = new Example(PlatformOrder.class);
        Example.Criteria criteria = example.createCriteria();
        boolean isQuery = false;
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(queryModel.getStartDate()));
            isQuery = true;
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
            isQuery = true;
        }
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//店铺订单分页查询
            ShopOrderForm shopOrderForm = (ShopOrderForm)queryModel;
            if (StringUtil.isNotEmpty(shopOrderForm.getStatus())) {//订单状态
                criteria.andGreaterThanOrEqualTo("status", shopOrderForm.getStatus());
                isQuery = true;
            }
            if (StringUtil.isNotEmpty(shopOrderForm.getType())) {//
                criteria.andEqualTo("type", shopOrderForm.getType());
                isQuery = true;
            }
            if (StringUtil.isNotEmpty(shopOrderForm.getReceiverName())) {//收货人姓名
                criteria.andLike("receiverName", "%" + shopOrderForm.getReceiverName() + "%");
                isQuery = true;
            }
        }
        if(isQuery){
            return platformOrderService.selectByExample(example);
        }else {
            return null;
        }

    }

    private void handlerOrderInfo(Pagenation<ShopOrder> page, List<PlatformOrder> platformOrderList){
        List<PlatformOrder> platformOrders = new ArrayList<PlatformOrder>();
        if(null == platformOrderList || platformOrderList.size() == 0){
            List<String> platformOrdersCodes = new ArrayList<String>();
            for(ShopOrder shopOrder: page.getResult()){
                platformOrdersCodes.add(shopOrder.getPlatformOrderCode());
            }
            Example example = new Example(PlatformOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("platformOrderCode", platformOrdersCodes);
            platformOrders = platformOrderService.selectByExample(example);
        }else {
            platformOrders = platformOrderList;
        }
        //设置商品订单扩展信息
        for(ShopOrder shopOrder: page.getResult()){
            for(PlatformOrder platformOrder: platformOrders){
                if(StringUtils.equals(shopOrder.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                    BeanUtils.copyProperties(platformOrder, (OrderBase)shopOrder);
                    setShopOrderItemsDetail(shopOrder, platformOrder);
                }
            }
        }
        //按付款时间将序排序
        Collections.sort(page.getResult(), new Comparator<ShopOrder>() {
            @Override
            public int compare(ShopOrder o1, ShopOrder o2) {
                if(null == o1.getPayTime() || null == o2.getPayTime()){
                    return 0;
                }
                return o1.getPayTime().compareTo(o2.getPayTime());
            }
        });
    }

    private void setShopOrderItemsDetail(ShopOrder shopOrder, PlatformOrder platformOrder){
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("shopOrderCode", shopOrder.getShopOrderCode());
        criteria.andEqualTo("platformOrderCode", shopOrder.getPlatformOrderCode());
        List<OrderItem> orderItemList = orderItemService.selectByExample(example);
        AssertUtil.notEmpty(orderItemList, String.format("根据平台订单编号[%s]和商铺订单编号[%s]查询订单商品明细为空",
                shopOrder.getPlatformOrderCode(), shopOrder.getShopOrderCode()));
        OrderExt orderExt = new OrderExt();
        BeanUtils.copyProperties(platformOrder, (OrderBase)shopOrder);
        BeanUtils.copyProperties(platformOrder, (OrderBase)orderExt);
        orderExt.setPayment(shopOrder.getPayment());
        orderExt.setPostageFee(shopOrder.getPostageFee());
        orderExt.setTotalTax(shopOrder.getTotalTax());
        orderExt.setOrderItemList(orderItemList);
        List<OrderExt> orderExts = new ArrayList<OrderExt>();
        orderExts.add(orderExt);
        shopOrder.setRecords(orderExts);
    }

    private void handlerWarehouseOrderInfo(Pagenation<WarehouseOrder> page, List<PlatformOrder> platformOrderList){
        List<PlatformOrder> platformOrders = new ArrayList<PlatformOrder>();
        if(null == platformOrderList || platformOrderList.size() == 0){
            List<String> platformOrdersCodes = new ArrayList<String>();
            for(WarehouseOrder warehouseOrder: page.getResult()){
                platformOrdersCodes.add(warehouseOrder.getPlatformOrderCode());
            }
            if(platformOrdersCodes.size() > 0){
                Example example = new Example(PlatformOrder.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("platformOrderCode", platformOrdersCodes);
                platformOrders = platformOrderService.selectByExample(example);
            }
        }else{
            if(platformOrderList.size() > 0){
                platformOrders = platformOrderList;
            }
        }
        //设置仓库订单支付时间及物流信息
        for(WarehouseOrder warehouseOrder: page.getResult()){
            for(PlatformOrder platformOrder: platformOrders){
                if(StringUtils.equals(warehouseOrder.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                    warehouseOrder.setPayTime(platformOrder.getPayTime());
                }
            }
            setLogisticsInfo(warehouseOrder);
        }
        //按付款时间将序排序
        Collections.sort(page.getResult(), new Comparator<WarehouseOrder>() {
            @Override
            public int compare(WarehouseOrder o1, WarehouseOrder o2) {
                if(null == o1.getPayTime() || null == o2.getPayTime()){
                    return 0;
                }
                return o1.getPayTime().compareTo(o2.getPayTime());
            }
        });
    }

    /**
     * 设置仓库订单物流新信息
     * @param warehouseOrder
     */
    private void setLogisticsInfo(WarehouseOrder warehouseOrder){
        SupplierOrderLogistics supplierOrderLogistics = new SupplierOrderLogistics();
        supplierOrderLogistics.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<SupplierOrderLogistics> supplierOrderLogisticsList = supplierOrderLogisticsService.select(supplierOrderLogistics);
        StringBuilder sb = new StringBuilder();
        for(SupplierOrderLogistics supplierOrderLogistics2: supplierOrderLogisticsList){
            if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), supplierOrderLogistics2.getType())){//物流信息
                sb.append(supplierOrderLogistics2.getLogisticsCorporation()).append(FLAG_EXT).append(supplierOrderLogistics2.getWaybillNumber()).append(HTML_BR);
            }
        }
        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, warehouseOrder.getSupplierCode())){
            warehouseOrder.setLogisticsInfo(SupplyConstants.Order.SUPPLIER_JD_LOGISTICS_COMPANY);
        }else{
            warehouseOrder.setLogisticsInfo(sb.toString());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppResult<String> reciveChannelOrder(String orderInfo) {
        AssertUtil.notBlank(orderInfo, "渠道同步订单给供应链订单信息参数不能为空");
        JSONObject orderObj = getChannelOrder(orderInfo);
        //获取平台订单信息
        PlatformOrder platformOrder = getPlatformOrder(orderObj);
        JSONArray shopOrderArray = getShopOrdersArray(orderObj);
        //获取店铺订单
        List<ShopOrder> shopOrderList = getShopOrderList(shopOrderArray);
        //保存幂等流水
        saveIdempotentFlow(shopOrderList);
        //拆分仓库订单
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (ShopOrder shopOrder : shopOrderList) {
            warehouseOrderList.addAll(dealShopOrder(shopOrder));
        }
        //订单商品明细
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (WarehouseOrder warehouseOrder : warehouseOrderList) {
            orderItemList.addAll(warehouseOrder.getOrderItemList());
        }
        //校验订单金额
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), channelOrderMoneyCheck)){
            orderMoneyCheck(platformOrder, shopOrderList, orderItemList);
        }
        //校验商品是否不是添加过的供应商商品
        checkItemsSource(orderItemList, platformOrder.getChannelCode());
        //保存商品明细
        orderItemService.insertList(orderItemList);
        //保存仓库订单
        warehouseOrderService.insertList(warehouseOrderList);
        //保存商铺订单
        shopOrderService.insertList(shopOrderList);
        //保存平台订单
        platformOrderService.insert(platformOrder);
        //创建订单日志
        createOrderLog(warehouseOrderList);
        //提交供应商订单
        try{
            submitSupplierOrder(warehouseOrderList);
        }catch (Exception e){
            log.error(String.format("多线程提交供应商订单异常,%s", e));
        }
        return ResultUtil.createSucssAppResult("接收订单成功", "");
    }



    /**
     * 订单金额校验
     * @param platformOrder
     * @param shopOrders
     * @param orderItems
     */
    private void orderMoneyCheck(PlatformOrder platformOrder, List<ShopOrder> shopOrders, List<OrderItem> orderItems){
        platformOrderParamCheck(platformOrder);
        int itemsNum = 0;//商品数量
        BigDecimal payment = new BigDecimal(0);//实付金额
        BigDecimal totalFee = new BigDecimal(0);//应付总金额
        BigDecimal postFee = new BigDecimal(0);//邮费金额
        BigDecimal totalTax = new BigDecimal(0);//税费金额
        for(ShopOrder shopOrder: shopOrders){
            shopOrderParamCheck(shopOrder);
            int itemsNum2 = 0;//商品数量
            BigDecimal payment2 = new BigDecimal(0);//实付金额
            BigDecimal totalFee2 = new BigDecimal(0);//应付总金额
            BigDecimal postFee2 = new BigDecimal(0);//邮费金额
            BigDecimal totalTax2 = new BigDecimal(0);//税费金额
            for(OrderItem orderItem: orderItems){
                if(StringUtils.equals(orderItem.getPlatformOrderCode(), shopOrder.getPlatformOrderCode()) &&
                        StringUtils.equals(orderItem.getShopOrderCode(), shopOrder.getShopOrderCode())){
                    orderItemsParamCheck(orderItem);
                    itemsNum2 = itemsNum2 + orderItem.getNum();
                    payment2 = payment2.add(orderItem.getPayment());
                    totalFee2 = totalFee2.add(orderItem.getTotalFee());
                    postFee2 = postFee2.add(orderItem.getPostDiscount());
                    totalTax2 = totalTax2.add(orderItem.getPriceTax());
                }
            }
            itemsNum = itemsNum + shopOrder.getItemNum();
            payment = payment.add(shopOrder.getPayment());
            totalFee = totalFee.add(shopOrder.getTotalFee());
            postFee = postFee.add(shopOrder.getPostageFee());
            totalTax = totalTax.add(shopOrder.getTotalTax());
            AssertUtil.isTrue(shopOrder.getItemNum().compareTo(itemsNum2) == 0, "店铺订单商品数量与该店铺所有商品总数量不相等");
            AssertUtil.isTrue(shopOrder.getPayment().compareTo(payment2) == 0, "店铺订单实付金额与该店铺所有商品实付总金额不相等");
            AssertUtil.isTrue(shopOrder.getTotalFee().compareTo(totalFee2) == 0, "店铺订单应付总金额与该店铺所有商品应付总金额不相等");
            AssertUtil.isTrue(shopOrder.getPostageFee().compareTo(postFee2) == 0, "店铺订单邮费金额与该店铺所有商品邮费总金额不相等");
            AssertUtil.isTrue(shopOrder.getTotalTax().compareTo(totalTax2) == 0, "店铺订单税费金额与该店铺所有商品税费总金额不相等");
        }
        AssertUtil.isTrue(platformOrder.getItemNum().compareTo(itemsNum) == 0, "平台订单商品数量与所有店铺商品总数量不相等");
        AssertUtil.isTrue(platformOrder.getPayment().compareTo(payment) == 0, "平台订单实付金额与所有店铺实付总金额不相等");
        AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(totalFee) == 0, "平台订单应付总金额与所有店铺应付总金额不相等");
        AssertUtil.isTrue(platformOrder.getPostageFee().compareTo(postFee) == 0, "平台订单邮费金额与所有店铺邮费总金额不相等");
        AssertUtil.isTrue(platformOrder.getTotalTax().compareTo(totalTax) == 0, "平台订单税费金额与所有店铺税费总金额不相等");
    }

    /**
     * 创建订单日志
     * @param warehouseOrderList
     */
    private void createOrderLog(List<WarehouseOrder> warehouseOrderList ){
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), LogInfoBiz.ADMIN, LogOperationEnum.ADD.getMessage(), null,null);
        }
    }

    /**
     * @param warehouseOrderList
     */
    private void submitSupplierOrder(List<WarehouseOrder> warehouseOrderList){
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            if(org.apache.commons.lang.StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, warehouseOrder.getSupplierCode())){//粮油订单
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //提交订单
                            AppResult appResult = submitLiangYouOrder(warehouseOrder.getWarehouseOrderCode());
                            if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), appResult.getAppcode())){
                                log.info(appResult.getDatabuffer());
                            }else{
                                log.error(appResult.getDatabuffer());
                            }
                            //下单结果通知渠道
                            notifyChannelSubmitOrderResult(warehouseOrder);
                        }catch (Exception e){
                            String msg = String.format("调用代发商品供应商%s下单接口提交订单%s异常,%s",warehouseOrder.getSupplierName(),
                                    JSONObject.toJSON(warehouseOrder), e.getMessage());
                            log.error(msg, e);
                        }
                    }
                });
            }
        }
    }

    /**
     * 通知渠道订单下单结果
     * @param warehouseOrder
     * @return
     */
    private void notifyChannelSubmitOrderResult(WarehouseOrder warehouseOrder){
        ChannelOrderResponse channelOrderResponse = new ChannelOrderResponse();
        channelOrderResponse.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        channelOrderResponse.setShopOrderCode(warehouseOrder.getShopOrderCode());
        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, warehouseOrder.getSupplierCode()))
            channelOrderResponse.setOrderType(SupplierOrderTypeEnum.JD.getCode());//京东订单
        else if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, warehouseOrder.getSupplierCode()))
            channelOrderResponse.setOrderType(SupplierOrderTypeEnum.LY.getCode());//粮油订单
        else
            throw new OrderException(ExceptionEnum.SUPPLIER_ORDER_NOTIFY_EXCEPTION, String.format("仓库订单%s不是一件代发订单,订单下单结果不需要通知渠道", JSON.toJSONString(warehouseOrder)));
        //供应商订单信息
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        AssertUtil.notEmpty(supplierOrderInfoList, String.format("根据仓库订单编码[%s]查询供应商订单信息为空", warehouseOrder.getWarehouseOrderCode()));
        //仓库订单明细信息
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(supplierOrderInfoList, String.format("根据仓库订单编码[%s]查询订单商品明细信息为空", warehouseOrder.getWarehouseOrderCode()));
        //设置订单提交返回信息
        List<SupplierOrderReturn> supplierOrderReturnList = new ArrayList<SupplierOrderReturn>();
        for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            SupplierOrderReturn supplierOrderReturn = new SupplierOrderReturn();
            supplierOrderReturn.setSupplyOrderCode(supplierOrderInfo2.getSupplierOrderCode());
            if(StringUtils.equals(SupplierOrderStatusEnum.SUBMIT_FAILURE.getCode(), supplierOrderInfo2.getStatus()))
                supplierOrderReturn.setState(SuccessFailureEnum.FAILURE.getCode());//失败
            else if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), supplierOrderInfo2.getStatus())||
                    StringUtils.equals(SupplierOrderStatusEnum.DELIVER.getCode(), supplierOrderInfo2.getStatus()))
                supplierOrderReturn.setState(SuccessFailureEnum.SUCCESS.getCode());//成功
            supplierOrderReturn.setSkus(getSupplierOrderReturnSkuInfo(supplierOrderInfo2, orderItemList));
            supplierOrderReturnList.add(supplierOrderReturn);
        }
        channelOrderResponse.setOrder(supplierOrderReturnList);
        ToGlyResultDO toGlyResultDO = trcService.sendOrderSubmitResultNotice(channelOrderResponse);
        //保存请求流水
        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(channelOrderResponse).toString(), RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT, toGlyResultDO, RequestFlowConstant.GYL);
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("仓库级订单订单%s提交结果通知渠道成功", JSON.toJSONString(warehouseOrder)));
        }else {
            log.error(String.format("仓库级订单订单%s提交结果通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(warehouseOrder), toGlyResultDO.getMsg()));
        }
    }

    /**
     * 获取供应商订单提交返回结果SKU信息
     * @param supplierOrderInfo
     * @param orderItemList
     * @return
     */
    private List<SkuInfo> getSupplierOrderReturnSkuInfo(SupplierOrderInfo supplierOrderInfo, List<OrderItem> orderItemList){
        JSONArray skuArray = JSONArray.parseArray(supplierOrderInfo.getSkus());
        List<SkuInfo> skuInfoList = skuArray.toJavaList(SkuInfo.class);
        for(SkuInfo skuInfo: skuInfoList){
            for(OrderItem orderItem: orderItemList){
                if(StringUtils.equals(skuInfo.getSkuCode(), orderItem.getSupplierSkuCode())){
                    skuInfo.setSkuCode(orderItem.getSkuCode());
                }
            }
        }
        return skuInfoList;
    }

    @Override
    public AppResult<LogisticNoticeForm> getJDLogistics(String shopOrderCode)throws Exception {
        AppResult<LogisticNoticeForm> appResult = null;
        try{
            AssertUtil.notBlank(shopOrderCode, "查询京东物流信息店铺订单号shopOrderCode不能为空");
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setShopOrderCode(shopOrderCode);
            warehouseOrder.setSupplierCode(SupplyConstants.Order.SUPPLIER_JD_CODE);
            //一个店铺订单下只有一个京东仓库订单
            warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
            if(null == warehouseOrder){
                return ResultUtil.createFailAppResult(String.format("不存在店铺订单编码为[%s]的京东订单信息", shopOrderCode));
            }
            LogisticForm logisticForm = invokeGetLogisticsInfo(warehouseOrder.getWarehouseOrderCode(), SupplierLogisticsEnum.JD.getCode());
            LogisticNoticeForm logisticNoticeForm = new LogisticNoticeForm();
            logisticNoticeForm.setShopOrderCode(shopOrderCode);
            if(null != logisticForm) {
                BeanUtils.copyProperties(logisticForm, logisticNoticeForm);
                SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
                supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());//未完成
                supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                supplierOrderInfo = supplierOrderInfoService.selectOne(supplierOrderInfo);
                if(null != supplierOrderInfo){
                    //更新供应商订单物流信息
                    updateSupplierOrderLogistics(supplierOrderInfo, logisticForm);
                }
            }
            appResult = ResultUtil.createSucssAppResult("查询订单配送信息成功", logisticNoticeForm);
        }catch (Exception e){
            String msg = String.format("根据店铺订单号[%s]查询订单物流信息异常,%s", shopOrderCode, e.getMessage());
            log.error(msg,e);
            appResult = ResultUtil.createFailAppResult(msg);
        }
        //保存请求流水
        requestFlowBiz.saveRequestFlow(String.format("{shopOrderCode:%s}", shopOrderCode), RequestFlowConstant.TRC, RequestFlowConstant.GYL, RequestFlowTypeEnum.LY_LOGISTIC_INFO_QUERY, appResult, RequestFlowConstant.GYL);
        return appResult;
    }


    @Override
    public void fetchLogisticsInfo() {
        Example example = new Example(SupplierOrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        String[] statusArray = new String[]{SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), SupplierOrderStatusEnum.DELIVER.getCode()};
        criteria.andIn("status", Arrays.asList(statusArray));
        criteria.andEqualTo("logisticsStatus", WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.selectByExample(example);
        for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            try{
                String flag = "";
                if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, supplierOrderInfo2.getSupplierCode()))
                    flag = SupplierLogisticsEnum.JD.getCode();
                else if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, supplierOrderInfo2.getSupplierCode()))
                    flag = SupplierLogisticsEnum.LY.getCode();
                if(StringUtils.isNotBlank(flag)){
                    LogisticForm logisticForm = invokeGetLogisticsInfo(supplierOrderInfo2.getWarehouseOrderCode(), flag);
                    if(null != logisticForm){
                        //更新供应商订单物流信息
                        updateSupplierOrderLogistics(supplierOrderInfo2, logisticForm);
                        //物流信息同步给渠道
                        logisticsInfoNoticeChannel(logisticForm);
                    }
                }
            }catch (Exception e){
                log.error(String.format("更新供应商订单%s物流信息异常,%s", JSONObject.toJSON(supplierOrderInfo2), e.getMessage()));
            }
        }
    }

    /**
     * 物流信息通知渠道
     * @param logisticForm
     */
    private void logisticsInfoNoticeChannel(LogisticForm logisticForm){
        try{
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setWarehouseOrderCode(logisticForm.getWarehouseOrderCode());
            warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
            AssertUtil.notNull(warehouseOrder, String.format("根据仓库订单编码[%s]查询仓库订单信息为空", logisticForm.getWarehouseOrderCode()));
            OrderItem orderItem = new OrderItem();
            orderItem.setWarehouseOrderCode(logisticForm.getWarehouseOrderCode());
            List<OrderItem> orderItemList = orderItemService.select(orderItem);
            AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单编码[%s]查询仓库订单商品明细信息为空", logisticForm.getWarehouseOrderCode()));
            LogisticNoticeForm logisticNoticeForm = new LogisticNoticeForm();
            BeanUtils.copyProperties(logisticForm, logisticNoticeForm);
            logisticNoticeForm.setShopOrderCode(warehouseOrder.getShopOrderCode());
            //将LogisticNoticeForm里面的供应商sku编码换成供应链sku编码
            for(Logistic logistic: logisticNoticeForm.getLogistics()){
                for(SkuInfo skuInfo: logistic.getSkus()){
                    for(OrderItem orderItem2: orderItemList){
                        if(StringUtils.equals(skuInfo.getSkuCode(), orderItem2.getSupplierSkuCode())){
                            skuInfo.setSkuCode(orderItem2.getSkuCode());
                        }
                    }
                }
            }
            //物流信息同步给渠道
            ToGlyResultDO toGlyResultDO = trcService.sendLogisticInfoNotice(logisticNoticeForm);
            if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
                log.error(String.format("物流信息%s同步给渠道失败,%s", JSON.toJSONString(logisticNoticeForm), toGlyResultDO.getMsg()));
            }
        }catch (Exception e){
            log.error(String.format("同步物流信息%s给渠道异常,%s", JSON.toJSONString(logisticForm), e.getMessage()), e);
        }


    }



    /**
     * 更新供应商订单物流信息
     * @param supplierOrderInfo
     * @param logisticForm
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void updateSupplierOrderLogistics(SupplierOrderInfo supplierOrderInfo, LogisticForm logisticForm){
        List<SupplierOrderLogistics> supplierOrderLogisticsList = new ArrayList<SupplierOrderLogistics>();
        //保存物流信息
        for(Logistic logistic: logisticForm.getLogistics()){
            SupplierOrderLogistics supplierOrderLogistics = null;
            try{
                supplierOrderLogistics = getSupplierOrderLogistics(supplierOrderInfo, logistic, logisticForm.getType());
                saveSupplierOrderLogistics(supplierOrderLogistics);
                supplierOrderLogisticsList.add(supplierOrderLogistics);
            }catch (Exception e){
                log.error(String.format("保存供应商物流信息%s异常,%s", JSONObject.toJSON(supplierOrderLogistics), e.getMessage()), e);
            }
        }
        if(supplierOrderLogisticsList.size() > 0){
            //更新供应商订单状态
            updateSupplierOrderStatus(supplierOrderInfo, supplierOrderLogisticsList);
            //更新仓库供应商订单状态
            updateWarehouseOrderSupplierOrderStatus(supplierOrderInfo.getWarehouseOrderCode(), SupplierOrderStatusEnum.DELIVER.getCode());
        }
    }



    /**
     * 保存供应商订单信息
     * @param supplierOrderLogistics
     */
    private void saveSupplierOrderLogistics(SupplierOrderLogistics supplierOrderLogistics){
        SupplierOrderLogistics supplierOrderLogistics2 = new SupplierOrderLogistics();
        supplierOrderLogistics2.setWarehouseOrderCode(supplierOrderLogistics.getWarehouseOrderCode());
        supplierOrderLogistics2.setSupplierOrderCode(supplierOrderLogistics.getSupplierOrderCode());
        supplierOrderLogistics2 = supplierOrderLogisticsService.selectOne(supplierOrderLogistics2);
        if(null == supplierOrderLogistics2){
            supplierOrderLogisticsService.insert(supplierOrderLogistics);
        }else{
            supplierOrderLogistics.setId(supplierOrderLogistics2.getId());
            supplierOrderLogistics.setUpdateTime(Calendar.getInstance().getTime());
            int count = supplierOrderLogisticsService.updateByPrimaryKeySelective(supplierOrderLogistics);
            if(count == 0){
                String msg = String.format("根据主键更新供应商订单物流信息%s失败", JSONObject.toJSON(supplierOrderLogistics));
                log.error(msg);
                throw new OrderException(ExceptionEnum.SUPPLIER_LOGISTICS_UPDATE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 更新供应商订单物流状态
     * @param supplierOrderInfo
     * @param supplierOrderLogisticsList
     */
    private void updateSupplierOrderStatus(SupplierOrderInfo supplierOrderInfo, List<SupplierOrderLogistics> supplierOrderLogisticsList){
        Boolean flag = true;
        for(SupplierOrderLogistics supplierOrderLogistics: supplierOrderLogisticsList){
            if(StringUtils.equals(SupplierOrderLogisticsStatusEnum.CREATE.getCode(), supplierOrderLogistics.getLogisticsStatus()) ||//新建
                    StringUtils.equals(SupplierOrderLogisticsStatusEnum.REJECT.getCode(), supplierOrderLogistics.getLogisticsStatus())){//拒收
                flag = false;
                break;
            }
        }
        if(flag){
            supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.COMPLETE.getCode());//未完成);//已完成
        }
        if(supplierOrderLogisticsList.size() > 0){
            supplierOrderInfo.setStatus(SupplierOrderStatusEnum.DELIVER.getCode());//已发货
        }
        if(StringUtils.isNotBlank(supplierOrderInfo.getStatus()) || StringUtils.isNotBlank(supplierOrderInfo.getLogisticsStatus())){
            supplierOrderInfo.setUpdateTime(Calendar.getInstance().getTime());
            int count = supplierOrderInfoService.updateByPrimaryKeySelective(supplierOrderInfo);
            if(count == 0){
                String msg = String.format("根据主键更新供应商订单信息%s失败", JSONObject.toJSON(supplierOrderInfo));
                log.error(msg);
                throw new OrderException(ExceptionEnum.SUPPLIER_LOGISTICS_UPDATE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 获取供应商订单物流信息
     * @param supplierOrderInfo
     * @param logistic
     * @param type
     * @return
     */
    private SupplierOrderLogistics getSupplierOrderLogistics(SupplierOrderInfo supplierOrderInfo, Logistic logistic, String type){
        SupplierOrderLogistics supplierOrderLogistics = new SupplierOrderLogistics();
        supplierOrderLogistics.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        supplierOrderLogistics.setSupplierCode(supplierOrderInfo.getSupplierCode());
        if(StringUtils.isNotBlank(supplierOrderInfo.getSupplierOrderCode()))
            supplierOrderLogistics.setSupplierParentOrderCode(supplierOrderInfo.getSupplierOrderCode());
        supplierOrderLogistics.setSupplierOrderCode(logistic.getSupplierOrderCode());
        supplierOrderLogistics.setLogisticsStatus(logistic.getLogisticsStatus());
        supplierOrderLogistics.setLogisticsInfo(JSONArray.toJSONString(logistic.getSkus()));
        supplierOrderLogistics.setType(type);
        if(StringUtils.equals(type, LogsticsTypeEnum.WAYBILL_NUMBER.getCode())){//物流单号
            supplierOrderLogistics.setWaybillNumber(logistic.getWaybillNumber());
            supplierOrderLogistics.setLogisticsCorporation(logistic.getLogisticsCorporation());
        }else if(StringUtils.equals(type, LogsticsTypeEnum.LOGSTICS.getCode())){//配送信息
            supplierOrderLogistics.setLogisticsInfo(JSONArray.toJSONString(logistic.getLogisticInfo()));
        }
        supplierOrderLogistics.setLogisticsStatus(logistic.getLogisticsStatus());
        ParamsUtil.setBaseDO(supplierOrderLogistics);
        return supplierOrderLogistics;
    }

    /**
     * 调用查询物流服务接口
     * @param warehouseOrderCode
     * @param flag 0-京东,1-粮油
     * @return
     */
    private LogisticForm invokeGetLogisticsInfo(String warehouseOrderCode, String flag){
        ReturnTypeDO returnTypeDO = ijdService.getLogisticsInfo(warehouseOrderCode, flag);
        RequestFlowTypeEnum requestFlowTypeEnum = RequestFlowTypeEnum.JD_LOGISTIC_INFO_QUERY;
        if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode()))
            requestFlowTypeEnum = RequestFlowTypeEnum.LY_LOGISTIC_INFO_QUERY;
        //保存请求流水
        requestFlowBiz.saveRequestFlow(String.format("{warehouseOrderCode:%s,flag:%s}", warehouseOrderCode, flag), RequestFlowConstant.GYL, RequestFlowConstant.JINGDONG, requestFlowTypeEnum, returnTypeDO, RequestFlowConstant.GYL);
        if(!returnTypeDO.getSuccess()){
            String msg = String.format("调用物流查询服务查询仓库订单编码为[%s]的仓库订单物流信息失败,错误信息:%s",
                    warehouseOrderCode, returnTypeDO.getResultMessage());
            log.error(msg);
            return null;
        }
        if(null == returnTypeDO.getResult()){
            log.warn(String.format("调用物流查询服务查询仓库订单编码为[%s]的仓库订单物流信息返回结为空", warehouseOrderCode));
            return null;
        }
        JSONObject orderObj = null;
        try {
            orderObj = JSONObject.parseObject(returnTypeDO.getResult().toString());
        } catch (ClassCastException e) {
            String msg = String.format("调用物流查询服务返回结果%s不是JSON格式", returnTypeDO.getResult());
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.SUPPLIER_LOGISTICS_QUERY_EXCEPTION, msg);
        }
        if(null == orderObj){
            log.warn(String.format("调用物流查询服务查询仓库订单编码为[%s]的仓库订单物流信息返回结为空", warehouseOrderCode));
            return null;
        }
        LogisticForm logisticForm = orderObj.toJavaObject(LogisticForm.class);
        JSONArray logisticsArray = orderObj.getJSONArray("logistics");
        List<Logistic> logistics = new ArrayList<>();
        for(Object obj: logisticsArray){
            JSONObject jbo = (JSONObject)obj;
            Logistic logistic = jbo.toJavaObject(Logistic.class);
            JSONArray skusArray = jbo.getJSONArray("skus");
            List<SkuInfo> skuInfoList = new ArrayList<SkuInfo>();
            for(Object skuObj: skusArray){
                JSONObject jbo2 = (JSONObject)skuObj;
                SkuInfo skuInfo = jbo2.toJavaObject(SkuInfo.class);
                skuInfoList.add(skuInfo);
            }
            logistic.setSkus(skuInfoList);
            JSONArray logisticInfoArray = jbo.getJSONArray("logisticInfo");
            List<LogisticInfo> logisticInfoList = new ArrayList<LogisticInfo>();
            for(Object logisticInfoObj: logisticInfoArray){
                JSONObject jbo2 = (JSONObject)logisticInfoObj;
                LogisticInfo logisticInfo = jbo2.toJavaObject(LogisticInfo.class);
                logisticInfoList.add(logisticInfo);
            }
            logistic.setLogisticInfo(logisticInfoList);
            logistics.add(logistic);
        }
        logisticForm.setLogistics(logistics);
        return logisticForm;
    }

    private JSONObject getChannelOrder(String orderInfo){
        JSONObject orderObj = null;
        try {
            orderObj = JSONObject.parseObject(orderInfo);
        } catch (ClassCastException e) {
            String msg = String.format("渠道同步订单参数不是JSON格式");
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        AssertUtil.notNull(orderObj, "接收渠道订单参数中平台订单信息为空");
        return orderObj;
    }

    /**
     * 适配地址,主要是对直辖市处理
     * 将渠道过来的平台订单里面市、区进行匹配处理重新赋值
     * @param platformOrder
     */
    private void adapterAddress(PlatformOrder platformOrder){
        String[] directCitys = SupplyConstants.DirectGovernedCity.DIRECT_CITY;
        for(String city: directCitys){
            if(platformOrder.getReceiverProvince().contains(city)){//当前发货地址是直辖市
                if(platformOrder.getReceiverCity().endsWith(DISTRICT)){//市地址放的是区信息
                    String districtInfo = platformOrder.getReceiverCity();
                    platformOrder.setReceiverDistrict(districtInfo);
                    platformOrder.setReceiverCity(platformOrder.getReceiverProvince());
                }
                break;
            }
        }
    }

    /**
     * 适配京东地址
     * @param jdAddressCodes
     * @param jdAddressNames
     * @return
     */
    private Map<String, String[]> adapterJingDongAddress(String[] jdAddressCodes, String[] jdAddressNames){
        String[] directCitys = SupplyConstants.DirectGovernedCity.DIRECT_CITY;
        Map<String, String[]> map = new HashMap<String, String[]>();
        boolean flag = false;
        List<String> jdAddressCodeList = new ArrayList<String>();
        List<String> jdAddressNameList = new ArrayList<String>();
        if(jdAddressCodes.length > 0){
            for(String city: directCitys){
                if(jdAddressNames[0].contains(city)){//当前发货地址是直辖市
                    flag = true;
                    break;
                }
            }
        }
        if(flag){
            jdAddressCodeList.add(jdAddressCodes[0]);
            jdAddressCodeList.add(jdAddressCodes[0]);
            jdAddressNameList.add(jdAddressNames[0]);
            jdAddressNameList.add(jdAddressNames[0]);
            if(jdAddressCodes.length > 1){
                if(jdAddressNames[1].endsWith(DISTRICT)){//市地址放的是区信息
                    jdAddressCodeList.add(jdAddressCodes[1]);
                    jdAddressNameList.add(jdAddressNames[1]);
                }
            }
            if(jdAddressCodes.length > 2){
                jdAddressCodeList.add(jdAddressCodes[2]);
                jdAddressNameList.add(jdAddressNames[2]);
            }
            map.put("jdAddressCodes", jdAddressCodeList.toArray(new String[jdAddressCodeList.size()]));
            map.put("jdAddressNames", jdAddressNameList.toArray(new String[jdAddressNameList.size()]));
        }else{
            map.put("jdAddressCodes", jdAddressCodes);
            map.put("jdAddressNames", jdAddressNames);
        }
        return map;
    }

    /**
     * 获取平台订单
     * @param orderObj
     * @return
     */
    private PlatformOrder getPlatformOrder(JSONObject orderObj){
        JSONObject platformObj = null;
        try{
            platformObj = orderObj.getJSONObject("platformOrder");
        }catch (ClassCastException e){
            String msg = String.format("平台订单信息转JSON错误");
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        AssertUtil.notNull(platformObj, "接收渠道订单参数中平台订单信息为空");
        PlatformOrder platformOrder = platformObj.toJavaObject(PlatformOrder.class);
        platformOrder.setPayment(platformObj.getBigDecimal("payment"));//实付金额
        platformOrder.setPostageFee(platformObj.getBigDecimal("postageFee"));//积分抵扣金额
        platformOrder.setTotalFee(platformObj.getBigDecimal("totalFee"));//订单总金额
        platformOrder.setAdjustFee(platformObj.getBigDecimal("adjustFee"));//卖家手工调整金额
        platformOrder.setPointsFee(platformObj.getBigDecimal("pointsFee"));//邮费
        platformOrder.setTotalTax(platformObj.getBigDecimal("totalTax"));//总税费
        platformOrder.setStepPaidFee(platformObj.getBigDecimal("stepPaidFee"));//分阶段已付金额
        platformOrder.setDiscountPromotion(platformObj.getBigDecimal("discountPromotion"));//促销优惠总金额
        platformOrder.setDiscountCouponShop(platformObj.getBigDecimal("discountCouponShop"));//店铺优惠卷优惠金额
        platformOrder.setDiscountCouponPlatform(platformObj.getBigDecimal("discountCouponPlatform"));//平台优惠卷优惠金额
        platformOrder.setDiscountFee(platformObj.getBigDecimal("discountFee"));//订单优惠总金额

        platformOrder.setCreateTime(DateUtils.timestampToDate(platformObj.getLong("createTime")));//创建时间
        platformOrder.setPayTime(DateUtils.timestampToDate(platformObj.getLong("payTime")));//支付时间
        platformOrder.setConsignTime(DateUtils.timestampToDate(platformObj.getLong("consignTime")));//发货时间
        platformOrder.setReceiveTime(DateUtils.timestampToDate(platformObj.getLong("receiveTime")));//确认收货时间
        platformOrder.setUpdateTime(DateUtils.timestampToDate(platformObj.getLong("updateTime")));//修改时间
        platformOrder.setTimeoutActionTime(DateUtils.timestampToDate(platformObj.getLong("timeoutActionTime")));//超时确认时间
        platformOrder.setEndTime(DateUtils.timestampToDate(platformObj.getLong("endTime")));//订单结束时间

        //适配地址,主要是对直辖市处理
        adapterAddress(platformOrder);

        return platformOrder;
    }

    /**
     * 获取店铺订单信息JSON数据
     * @param orderObj
     * @return
     */
    private JSONArray getShopOrdersArray(JSONObject orderObj){
        JSONArray shopOrderArray = null;
        try{
            shopOrderArray = orderObj.getJSONArray("shopOrders");
        }catch (ClassCastException e){
            String msg = String.format("店铺订单信息转JSON错误");
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        if(shopOrderArray.size() == 0){
            String msg = String.format("店铺订单信息为空");
            log.error(msg);
            throw new OrderException(ExceptionEnum.CHANNEL_ORDER_DATA_NOT_JSON_EXCEPTION, msg);
        }
        return shopOrderArray;
    }


    private void updateWarehouseOrderSupplierOrderStatus(String warehouseOrderCode, String supplierOrderStatus){
        AssertUtil.notBlank(warehouseOrderCode, "更新仓库订单供应商订单状态仓库订单编码参数warehouseOrderCode不能为空");
        AssertUtil.notBlank(supplierOrderStatus, "更新仓库订单供应商订单状态仓库订单状态参数supplierOrderStatus不能为空");
        WarehouseOrder warehouseOrder2 = new WarehouseOrder();
        warehouseOrder2.setSupplierOrderStatus(supplierOrderStatus);
        warehouseOrder2.setUpdateTime(Calendar.getInstance().getTime());
        Example example = new Example(WarehouseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("warehouseOrderCode", warehouseOrderCode);
        int count = warehouseOrderService.updateByExampleSelective(warehouseOrder2, example);
        if(count == 0){
            String msg = String.format("更新仓库订单编码为[%s]的仓库订单供应商订单状态为%s失败", warehouseOrderCode, supplierOrderStatus);
            log.error(msg);
            throw new OrderException(ExceptionEnum.ORDER_STATUS_UPDATE_EXCEPTION, msg);
        }
        if(StringUtils.equals(SupplierOrderStatusEnum.DELIVER.getCode(), supplierOrderStatus)){
            WarehouseOrder warehouseOrder3 = new WarehouseOrder();
            warehouseOrder3.setWarehouseOrderCode(warehouseOrderCode);
            warehouseOrder3 = warehouseOrderService.selectOne(warehouseOrder3);
            AssertUtil.notNull(warehouseOrder3, String.format("根据仓库订单编码[%s]查询仓库订单信息为空", warehouseOrderCode));
            //记录操作日志
            logInfoService.recordLog(warehouseOrder3,warehouseOrder3.getId().toString(), warehouseOrder3.getSupplierName(), LogOperationEnum.DELIVER.getMessage(), null,null);
        }
    }


    /**
     * 保存幂等流水
     *
     * @param shopOrderList
     */
    private void saveIdempotentFlow(List<ShopOrder> shopOrderList) {
        try {
            for (ShopOrder shopOrder : shopOrderList) {
                OrderFlow orderFlow = new OrderFlow();
                orderFlow.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
                orderFlow.setShopOrderCode(shopOrder.getShopOrderCode());
                orderFlow.setType(shopOrder.getChannelCode());
                int count = orderFlowService.insert(orderFlow);
                if (count == 0) {
                    String msg = String.format("保存订单同步幂等流水%s失败", JSONObject.toJSON(orderFlow));
                    log.error(msg);
                    throw new OrderException(ExceptionEnum.ORDER_IDEMPOTENT_SAVE_EXCEPTION, msg);
                }
            }
        } catch (DuplicateKeyException e) {
            log.error("重复提交订单: ",e);
            throw new OrderException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, "重复提交订单");
        }


    }

    /**
     * 获取店铺订单
     *
     * @param shopOrderArray
     * @return
     */
    private List<ShopOrder> getShopOrderList(JSONArray shopOrderArray) {
        List<ShopOrder> shopOrderList = new ArrayList<ShopOrder>();
        BigDecimal totalShop = new BigDecimal(0);
        for (Object obj : shopOrderArray) {
            JSONObject tmpObj = (JSONObject) obj;
            JSONObject shopOrderObj = tmpObj.getJSONObject("shopOrder");
            AssertUtil.notNull(shopOrderObj, "接收渠道订单参数中平店铺订单信息为空");
            ShopOrder shopOrder = shopOrderObj.toJavaObject(ShopOrder.class);
            shopOrder.setCreateTime(DateUtils.timestampToDate(shopOrderObj.getLong("createTime")));//创建时间
            shopOrder.setPayTime(DateUtils.timestampToDate(shopOrderObj.getLong("payTime")));//支付时间
            shopOrder.setConsignTime(DateUtils.timestampToDate(shopOrderObj.getLong("consignTime")));//发货时间
            shopOrder.setUpdateTime(DateUtils.timestampToDate(shopOrderObj.getLong("updateTime")));//修改时间
            //设置店铺金额
            setShopOrderFee(shopOrder, shopOrderObj);
            JSONArray orderItemArray = tmpObj.getJSONArray("orderItems");
            AssertUtil.notEmpty(orderItemArray, String.format("接收渠道订单参数中平店铺订单%s相关商品订单明细信息为空为空", shopOrderObj));
            //获取订单商品明细
            List<OrderItem> orderItemList = getOrderItem(orderItemArray);
            totalShop = totalShop.add(shopOrder.getPayment());
            shopOrder.setOrderItems(orderItemList);
            shopOrderList.add(shopOrder);
        }
        return shopOrderList;
    }

    /**
     * 设置店铺金额
     * @param shopOrder
     * @param shopOrderObj
     */
    private void setShopOrderFee(ShopOrder shopOrder, JSONObject shopOrderObj){
        shopOrder.setPayment(shopOrderObj.getBigDecimal("payment"));//实付金额
        shopOrder.setPostageFee(shopOrderObj.getBigDecimal("postageFee"));//积分抵扣金额
        shopOrder.setTotalFee(shopOrderObj.getBigDecimal("totalFee"));//订单总金额
        shopOrder.setAdjustFee(shopOrderObj.getBigDecimal("adjustFee"));//卖家手工调整金额
        shopOrder.setTotalTax(shopOrderObj.getBigDecimal("totalTax"));//总税费
        shopOrder.setDiscountPromotion(shopOrderObj.getBigDecimal("discountPromotion"));//促销优惠总金额
        shopOrder.setDiscountCouponShop(shopOrderObj.getBigDecimal("discountCouponShop"));//店铺优惠卷优惠金额
        shopOrder.setDiscountCouponPlatform(shopOrderObj.getBigDecimal("discountCouponPlatform"));//平台优惠卷优惠金额
        shopOrder.setDiscountFee(shopOrderObj.getBigDecimal("discountFee"));//订单优惠总金额
    }

    /**
     * 获取订单商品明细
     * @param orderItemArray
     * @return
     */
    private List<OrderItem> getOrderItem(JSONArray orderItemArray){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(Object obj: orderItemArray){
            JSONObject orderItemObj = (JSONObject)obj;
            OrderItem orderItem = orderItemObj.toJavaObject(OrderItem.class);
            orderItem.setPayment(orderItemObj.getBigDecimal("payment"));//实付金额
            orderItem.setTotalFee(orderItemObj.getBigDecimal("totalFee"));//订单总金额
            orderItem.setAdjustFee(orderItemObj.getBigDecimal("adjustFee"));//卖家手工调整金额
            orderItem.setDiscountPromotion(orderItemObj.getBigDecimal("discountPromotion"));//促销优惠总金额
            orderItem.setDiscountCouponShop(orderItemObj.getBigDecimal("discountCouponShop"));//店铺优惠卷优惠金额
            orderItem.setDiscountCouponPlatform(orderItemObj.getBigDecimal("discountCouponPlatform"));//平台优惠卷优惠金额
            orderItem.setDiscountFee(orderItemObj.getBigDecimal("discountFee"));//订单优惠总金额
            orderItem.setPostDiscount(orderItemObj.getBigDecimal("postDiscount"));//运费分摊
            orderItem.setRefundFee(orderItemObj.getBigDecimal("refundFee"));//退款金额
            orderItem.setPriceTax(orderItemObj.getBigDecimal("priceTax"));//商品税费
            orderItem.setPrice(orderItemObj.getBigDecimal("price"));//商品价格
            orderItem.setMarketPrice(orderItemObj.getBigDecimal("marketPrice"));//市场价
            orderItem.setPromotionPrice(orderItemObj.getBigDecimal("promotionPrice"));//促销价
            orderItem.setCustomsPrice(orderItemObj.getBigDecimal("customsPrice"));//报关单价
            orderItem.setTransactionPrice(orderItemObj.getBigDecimal("transactionPrice"));//成交单价
            orderItem.setTotalWeight(orderItemObj.getBigDecimal("totalWeight"));//商品重量

            orderItem.setCreateTime(DateUtils.timestampToDate(orderItemObj.getLong("createTime")));//创建时间
            orderItem.setPayTime(DateUtils.timestampToDate(orderItemObj.getLong("payTime")));//支付时间
            orderItem.setConsignTime(DateUtils.timestampToDate(orderItemObj.getLong("consignTime")));//发货时间
            orderItem.setUpdateTime(DateUtils.timestampToDate(orderItemObj.getLong("updateTime")));//修改时间
            orderItem.setTimeoutActionTime(DateUtils.timestampToDate(orderItemObj.getLong("timeoutActionTime")));//超时确认时间
            orderItem.setEndTime(DateUtils.timestampToDate(orderItemObj.getLong("endTime")));//结束时间

            orderItemList.add(orderItem);
        }
        return orderItemList;
    }


    /**
     * 检查商品来源
     * @param orderItemList
     * @param channelCode 渠道编码
     */
    private void checkItemsSource(List<OrderItem> orderItemList, String channelCode){
        Set<String> skuCodes = new HashSet<String>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        //查询自采商品
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("channelCode", channelCode);
        List<SkuRelation> skuRelations = skuRelationService.selectByExample(example);
        AssertUtil.notEmpty(skuRelations, String.format("根据多个skuCode[%s]查询skuRelation列表为空", CommonUtil.converCollectionToString(Arrays.asList(skuCodes.toArray()))));
        StringBuilder sb = new StringBuilder();
        for(OrderItem orderItem: orderItemList){
            Boolean flag = false;
            for(SkuRelation skuRelation: skuRelations){
                if(StringUtils.equals(orderItem.getSkuCode(), skuRelation.getSkuCode())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                sb.append(String.format("{skuCode:%s, skuName:%s}", orderItem.getSkuCode(), orderItem.getItemName())).append(",");
            }
        }
        if(sb.length() > 0){
            String msg = "商品[" + sb.toString()+"]在供应链系统无法识别";
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
    }

    /**
     * 平台订单校验
     *
     * @param platformOrder
     */
    private void platformOrderParamCheck(PlatformOrder platformOrder) {
        AssertUtil.notBlank(platformOrder.getChannelCode(), "渠道编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(platformOrder.getUserId(), "平台订单会员id不能为空");
        AssertUtil.notBlank(platformOrder.getUserName(), "平台订单会员名称不能为空");

        AssertUtil.notBlank(platformOrder.getPayType(), "平台订单单支付类型不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverProvince(), "平台订单收货人所在省不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverCity(), "平台订单收货人所在城市不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverDistrict(), "平台订单收货人所在地区不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverAddress(), "平台订单收货人详细地址不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverName(), "平台订单收货人姓名不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverMobile(), "平台订单收货人手机号码不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverEmail(), "平台订单收货人电子邮箱不能为空");
        AssertUtil.notBlank(platformOrder.getStatus(), "平台订单订单状态不能为空");
        AssertUtil.notBlank(platformOrder.getType(), "平台订单订单类型不能为空");
        AssertUtil.notNull(platformOrder.getCreateTime(), "平台订单创建时间不能为空");
        AssertUtil.notNull(platformOrder.getPayTime(), "平台订单支付时间不能为空");

        AssertUtil.isTrue(platformOrder.getItemNum() > 0, "买家购买的商品总数不能为空");
        AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(new BigDecimal(0))==0 || platformOrder.getTotalFee().compareTo(new BigDecimal(0))==1, "平台订单总金额应大于等于0");
        AssertUtil.isTrue(platformOrder.getPayment().compareTo(new BigDecimal(0))==0 || platformOrder.getPayment().compareTo(new BigDecimal(0))==1 ,"平台订单实付金额应大于等于0");

    }

    /**
     * &
     * 店铺订单校验
     *
     * @param shopOrder
     */
    private void shopOrderParamCheck(ShopOrder shopOrder) {
        AssertUtil.notBlank(shopOrder.getChannelCode(), "店铺订单渠道编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformCode(), "店铺订单来源平台编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformOrderCode(), "店铺订单平台订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformType(), "店铺订单订单来源类型不能为空");
        AssertUtil.notNull(shopOrder.getShopId(), "店铺订单订单所属的店铺id不能为空");
        AssertUtil.notBlank(shopOrder.getShopName(), "店铺订单店铺名称不能为空");
        AssertUtil.notBlank(shopOrder.getUserId(), "店铺订单会员id不能为空");
        AssertUtil.notBlank(shopOrder.getStatus(), "店铺订单订单状态不能为空");
        AssertUtil.notNull(shopOrder.getCreateTime(), "平台订单创建时间不能为空");
        AssertUtil.isTrue(shopOrder.getItemNum() > 0, "店铺订单商品总数不能为空");

        AssertUtil.isTrue(shopOrder.getItemNum() > 0, "店铺订单购买的商品总数不能为空");
        AssertUtil.isTrue(shopOrder.getTotalFee().compareTo(new BigDecimal(0))==0 || shopOrder.getTotalFee().compareTo(new BigDecimal(0))==1, "店铺订单总金额应大于等于0");
        AssertUtil.isTrue(shopOrder.getPayment().compareTo(new BigDecimal(0))==0 || shopOrder.getPayment().compareTo(new BigDecimal(0))==1 ,"店铺订单实付金额应大于等于0");

    }

    /**
     * 商品参数校验
     *
     * @param orderItem
     */
    private void orderItemsParamCheck(OrderItem orderItem) {
        AssertUtil.notBlank(orderItem.getChannelCode(), "订单商品渠道编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformCode(), "订单商品来源平台编码不能为空");
        AssertUtil.notBlank(orderItem.getPlatformOrderCode(), "订单商品平台订单编码不能为空");
        AssertUtil.notBlank(orderItem.getShopOrderCode(), "订单商品店铺订单编码不能为空");

        AssertUtil.notBlank(orderItem.getSkuCode(), "订单商品商品sku编码不能为空");
        AssertUtil.notNull(orderItem.getShopId(), "订单商品订单所属的店铺id不能为空");
        AssertUtil.notBlank(orderItem.getShopName(), "订单商品店铺名称不能为空");
        AssertUtil.notBlank(orderItem.getUserId(), "订单商品会员id不能为空");
        AssertUtil.notBlank(orderItem.getItemNo(), "订单商品货号不能为空");
        AssertUtil.notBlank(orderItem.getItemName(), "订单商品名称不能为空");
        AssertUtil.notBlank(orderItem.getStatus(), "订单商品订单状态不能为空");
        AssertUtil.notNull(orderItem.getCreateTime(), "平台订单创建时间不能为空");

        AssertUtil.isTrue(orderItem.getNum() > 0, "订单商品购买数量不能为空");
        AssertUtil.isTrue(orderItem.getPrice().compareTo(new BigDecimal(0))==1, "订单商品价格应大于0");
        AssertUtil.isTrue(orderItem.getTotalFee().compareTo(new BigDecimal(0))==0 || orderItem.getTotalFee().compareTo(new BigDecimal(0))==1, "订单商品总金额应大于等于0");
        AssertUtil.isTrue(orderItem.getPayment().compareTo(new BigDecimal(0))==0 || orderItem.getPayment().compareTo(new BigDecimal(0))==1 ,"订单商品实付金额应大于等于0");
        BigDecimal totalFee = orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum()));
        AssertUtil.isTrue(totalFee.compareTo(orderItem.getTotalFee())==0, "订单商品价格*商品数量应等于订单应付金额totalFee");
    }


    /**
     * 拆分店铺级订单
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealShopOrder(ShopOrder shopOrder) {
        List<OrderItem> orderItemList = shopOrder.getOrderItems();
        //分离一件代发和自采商品
        List<OrderItem> orderItemList1 = new ArrayList<>();//自采商品
        List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith(SP0)) {
                orderItemList1.add(orderItem);
            }
            if (orderItem.getSkuCode().startsWith(SP1)) {
                orderItemList2.add(orderItem);
            }
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        if(orderItemList1.size() > 0){
            // TODO 自采的拆单暂时不做
        }
        if(orderItemList2.size() > 0){
            warehouseOrderList = dealSupplier(orderItemList2, shopOrder);
        }
        return warehouseOrderList;
    }



    public List<WarehouseOrder> dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder) {
        //新建仓库级订单
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        AssertUtil.notEmpty(externalItemSkuList, String.format("根据sku编码列表[%s]查询一件代发商品为空", CommonUtil.converCollectionToString(skuCodeList)));
        Set<String> supplierCodes = new HashSet<>();
        for (ExternalItemSku externalItemSku : externalItemSkuList) {
            supplierCodes.add(externalItemSku.getSupplierCode());
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (String supplierCode : supplierCodes) {
            List<OrderItem> orderItemList2 = new ArrayList<OrderItem>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setSupplierCode(supplierCode);
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setSupplierCode(supplierCode);
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
            warehouseOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode());
            //流水号
            String code = serialUtilService.generateRandomCode(Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()), SupplyConstants.Serial.WAREHOUSE_ORDER, supplierCode, ZeroToNineEnum.ONE.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            warehouseOrder.setWarehouseOrderCode(code);
            warehouseOrder.setOrderType(ZeroToNineEnum.ONE.getCode());
            Boolean flag = false;
            for (ExternalItemSku externalItemSku : externalItemSkuList) {
                if(org.apache.commons.lang.StringUtils.equals(supplierCode, externalItemSku.getSupplierCode())){
                    OrderItem orderItem = getWarehouseOrderItems(warehouseOrder,externalItemSku, orderItems);
                    if(!flag){
                        warehouseOrder.setSupplierName(orderItem.getSupplierName());
                        flag = true;
                    }
                    orderItemList2.add(orderItem);
                }
            }
            setWarehouseOrderFee(warehouseOrder, orderItemList2);
            warehouseOrder.setOrderItemList(orderItemList2);
            warehouseOrderList.add(warehouseOrder);
        }
        return warehouseOrderList;
    }

    /**
     * 设置仓库订单金额
     * @param warehouseOrder
     * @param orderItemList
     */
    private void setWarehouseOrderFee(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        Integer itemsNum = 0;//商品总数量
        BigDecimal totalFee = new BigDecimal(0);//总金额
        BigDecimal payment = new BigDecimal(0);//实付金额
        BigDecimal adjustFee = new BigDecimal(0);//卖家手工调整金额
        BigDecimal postageFee = new BigDecimal(0);//邮费分摊金额
        BigDecimal discountPromotion = new BigDecimal(0);//促销优惠总金额
        BigDecimal discountCouponShop = new BigDecimal(0);//店铺优惠卷分摊总金额
        BigDecimal discountCouponPlatform = new BigDecimal(0);//平台优惠卷分摊总金额
        BigDecimal discountFee = new BigDecimal(0);//促销优惠金额
        for(OrderItem orderItem: orderItemList){
            itemsNum += orderItem.getNum();
            totalFee = totalFee.add(orderItem.getTotalFee());
            payment = payment.add(orderItem.getPayment());
            adjustFee = adjustFee.add(orderItem.getAdjustFee());
            postageFee = postageFee.add(orderItem.getPostDiscount());
            discountPromotion = discountPromotion.add(orderItem.getDiscountPromotion());
            discountCouponShop = discountCouponShop.add(orderItem.getDiscountCouponShop());
            discountCouponPlatform = discountCouponPlatform.add(orderItem.getDiscountCouponPlatform());
            discountFee = discountFee.add(orderItem.getDiscountFee());
        }
        warehouseOrder.setItemsNum(itemsNum);
        warehouseOrder.setTotalFee(totalFee);
        warehouseOrder.setPayment(payment);
        warehouseOrder.setAdjustFee(adjustFee);
        warehouseOrder.setPostageFee(postageFee);
        warehouseOrder.setDiscountPromotion(discountPromotion);
        warehouseOrder.setDiscountCouponShop(discountCouponShop);
        warehouseOrder.setDiscountCouponPlatform(discountCouponPlatform);
        warehouseOrder.setDiscountFee(discountFee);
    }


    /**
     * 获取仓库商品明细
     * @param warehouseOrder
     * @param externalItemSku
     * @param orderItemList
     * @return
     */
    private OrderItem getWarehouseOrderItems(WarehouseOrder warehouseOrder, ExternalItemSku externalItemSku, List<OrderItem> orderItemList){
        for(OrderItem orderItem: orderItemList){
            if(org.apache.commons.lang.StringUtils.equals(externalItemSku.getSkuCode(),orderItem.getSkuCode())){
                orderItem.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
                orderItem.setShopOrderCode(warehouseOrder.getShopOrderCode());
                orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                orderItem.setSupplierName(externalItemSku.getSupplierName());
                return orderItem;
            }
        }
        return null;
    }

}
