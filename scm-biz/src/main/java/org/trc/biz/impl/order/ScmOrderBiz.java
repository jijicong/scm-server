package org.trc.biz.impl.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.request.DeliveryorderBatchcreateRequest;
import com.qimen.api.response.DeliveryorderBatchcreateResponse;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.biz.order.IOrderExtBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.common.RequsetUpdateStock;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.System.SellChannel;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.config.SystemConfig;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.*;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseInfo.WarehousePriority;
import org.trc.enums.*;
import org.trc.exception.OrderException;
import org.trc.exception.ParamValidException;
import org.trc.exception.SignException;
import org.trc.form.*;
import org.trc.form.JDModel.*;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.form.liangyou.OutOrderGoods;
import org.trc.form.order.*;
import org.trc.form.warehouse.*;
import org.trc.form.warehouseInfo.WarehouseItemInfoExceptionResult;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IJDService;
import org.trc.service.IQimenService;
import org.trc.service.ITrcService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.config.ISystemConfigService;
import org.trc.service.goods.*;
import org.trc.service.order.*;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.IRealIpService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.service.warehouseInfo.IWarehousePriorityService;
import org.trc.util.*;
import org.trc.util.cache.OutboundOrderCacheEvict;
import org.trc.util.cache.SupplierOrderCacheEvict;
import org.trc.util.excel.ExcelFieldInfo;
import org.trc.util.excel.ExcelServiceNew;
import org.trc.util.lock.RedisLock;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Service("scmOrderBiz")
public class ScmOrderBiz extends ExcelServiceNew implements IScmOrderBiz {

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
    //订单接收时间间隔
    public final static String ORDER_RECEIVE_INTERVAL = "orderReceiveInterval";
    //供应商下单接口调用失败重试次数
    public final static int SUBMIT_SUPPLIER_ORDER_FAILURE_TIMES = 3;
    //供应商平台取消订单说明
    public final static String SUPPLIER_PLATFORM_CANCEL_ORDER = "供应商平台已取消订单";
    //系统操作员
    public final static String SYSTEM = "系统";
    public final static String BAR = "-";
    public final static String EXCEL = ".xls";

    //业务类型：交易
    public final static String BIZ_TYPE_DEAL = "DEAL";
    //京东下单价格不匹配错误代码
    public final static String JD_ORDER_SUBMIT_PRICE_ERROR = "3019";
    //京东下单商品库存不足
    public final static String JD_ORDER_SUBMIT_STOCK_LESS = "3008";
    //京东下单余额不足错误代码
    public final static String JD_BALANCE_NOT_ENOUGH = "3017";
    //金额为0的常量
    public final static String ZERO_MONEY_STR = "0.000";
    //下单成功日志信息
    public final static String ORDER_SUCCESS_INFO = "下单成功";
    //下单成功日志信息
    public final static String ORDER_PART_INFO = "部分发货";
    //下单成功日志信息
    public final static String ORDER_ALL_INFO = "全部发货";
    //下单失败日志信息
    public final static String ORDER_FAILURE_INFO = "下单失败";
    //下单成功日志信息
    public final static String ORDER_CANCEL_INFO = "已取消";
    //自采商品异常订单的状态
    public final static String SELF_PURCHARES_ORDER_STATUS = "5555";
    //企业购业务线编码
    public final static String BUSINESS_PURCHASE_CHANNEL_CODE = "businessPurchaseChannelCode";


    //渠道订单金额校验:1-是,0-否
    @Value("${channel.orderMoneyCheck}")
    private String channelOrderMoneyCheck;
    //是否正式提交订单:1-是,0-否
    @Value("${submit.order.status}")
    private String submitOrderStatus;
    //京东下单方式 0--预占库存方式 1--不是预占库存
    @Value("${jd.submit.state}")
    private String jdSubmitState;
    //订单sku默认图片
    @Value("${order.sku.pictrue}")
    private String orderSkuPictrue;
    @Value("${domain.of.bucket}")
    private String quniuPicDomain;
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
    @Autowired
    private IRequestFlowService requestFlowService;
    @Autowired
    private TrcConfig trcConfig;
    @Autowired
    private ISystemConfigService systemConfigService;
    @Autowired
    private IRealIpService iRealIpService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private IExceptionOrderService exceptionOrderService;
    @Autowired
    private IExceptionOrderItemService exceptionOrderItemService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IQimenService qimenService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IOutboundDetailService outboundDetailService;
    @Autowired
    private IOutboundDetailLogisticsService outboundDetailLogisticsService;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private IItemsService iItemsService;
    @Autowired
    private ISkusService skusService;
    @Autowired
    private IOrderExtBiz orderExtBiz;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private IWarehouseExtService warehouseExtService;
    @Autowired
    private IWarehousePriorityService warehousePriorityService;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IImportOrderInfoService importOrderInfoService;
    @Autowired
    private IOrderIdempotentService orderIdempotentService;


    @Value("{trc.jd.logistic.url}")
    private String TRC_JD_LOGISTIC_URL;
    private String SP0 = "SP0";
    private String SP1 = "SP1";
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    //导入订单平台订单号生成前缀
    private final static String PLATFORM_ORDER_CORD_PREFIX = "PTDD";


    @Override
    @Cacheable(value = SupplyConstants.Cache.SHOP_ORDER)
    public Pagenation<ShopOrder> shopOrderPage(ShopOrderForm queryModel, Pagenation<ShopOrder> page, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }
        if(StringUtils.isNotBlank(queryModel.getSellCode())){
            criteria.andEqualTo("sellCode", queryModel.getSellCode());
        }
        if (StringUtil.isNotEmpty(queryModel.getPlatformOrderCode())) {//平台订单编码
            criteria.andLike("platformOrderCode", "%" + queryModel.getPlatformOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopOrderCode())) {//店铺订单编码
            criteria.andLike("shopOrderCode", "%" + queryModel.getShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getScmShopOrderCode())) {//系统订单号
            criteria.andLike("scmShopOrderCode", "%" + queryModel.getScmShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopName())) {//店铺名称
            criteria.andLike("shopName", "%" + queryModel.getShopName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSupplierOrderStatus())) {//发货状态
            criteria.andEqualTo("supplierOrderStatus", queryModel.getSupplierOrderStatus());
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
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
        example.orderBy("createTime").desc();
        page = shopOrderService.pagination(example, page, new QueryModel());
        if(page.getResult().size() > 0){
            handlerOrderInfo(page, platformOrderList);
            orderExtBiz.setOrderSellName(page);
        }
        return page;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.SUPPLIER_ORDER)
    public Pagenation<WarehouseOrder> warehouseOrderPage(WarehouseOrderForm form, Pagenation<WarehouseOrder> page, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        AssertUtil.notNull(form, "查询供应商订单分页参数不能为空");
        Example example = new Example(WarehouseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }
        if(StringUtils.isNotBlank(form.getSellCode())){
            criteria.andEqualTo("sellCode", form.getSellCode());
        }
        if(StringUtils.isNotBlank(form.getOrderType())){//订单类型
            criteria.andEqualTo("orderType", form.getOrderType());
        }
        if(StringUtils.isNotBlank(form.getSupplierOrderStatus())){//状态
            criteria.andEqualTo("supplierOrderStatus", form.getSupplierOrderStatus());
        }
        if(StringUtils.isNotBlank(form.getPlatformOrderCode())){//销售渠道平台订单号
            criteria.andLike("platformOrderCode", "%" + form.getPlatformOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getWarehouseOrderCode())){//供应商订单编号
            criteria.andLike("warehouseOrderCode", "%" + form.getWarehouseOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getShopOrderCode())){//店铺订单号
            criteria.andLike("shopOrderCode", "%" + form.getShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(form.getScmShopOrderCode())) {//系统订单号
            criteria.andLike("scmShopOrderCode", "%" + form.getScmShopOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getSupplierCode())){//供应商编码
            criteria.andEqualTo("supplierCode", form.getSupplierCode());
        }
        if (StringUtil.isNotEmpty(form.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(form.getStartDate()));
        }
        if (StringUtil.isNotEmpty(form.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(form.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
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
        example.setOrderByClause("instr('2,5,1',`supplier_order_status`) DESC");
        example.orderBy("payTime").desc();
        example.orderBy("updateTime").desc();
        page = warehouseOrderService.pagination(example, page, form);
        handlerWarehouseOrderInfo(page, platformOrderList);
        orderExtBiz.setOrderSellName(page);
        return page;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.SHOP_ORDER)
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
                        setShopOrderItemsDetail(shopOrder2, platformOrder, ZeroToNineEnum.ONE.getCode());
                    }
                }
            }
        }
        return shopOrderList;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.SUPPLIER_ORDER)
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
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据平台订单编号[%s]和商铺订单编号[%s]查询订单商品明细为空",
                warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode()));
        warehouseOrder.setOrderItemList(orderItemList);
        //设置商品详情信息
        setOrderItemDetail(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode(), orderItemList);
        //查询京东四级地址
        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, warehouseOrder.getSupplierCode())){
            SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
            supplierOrderInfo.setWarehouseOrderCode(warehouseOrderCode);
            supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
            List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
            if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
                supplierOrderInfo = supplierOrderInfoList.get(0);
                StringBuilder sb = new StringBuilder();
                if(StringUtils.isNotBlank(supplierOrderInfo.getJdProvince()))
                    sb.append(supplierOrderInfo.getJdProvince());
                if(StringUtils.isNotBlank(supplierOrderInfo.getJdCity()))
                    sb.append(SupplyConstants.Symbol.FILE_NAME_SPLIT).append(supplierOrderInfo.getJdCity());
                if(StringUtils.isNotBlank(supplierOrderInfo.getJdDistrict()))
                    sb.append(SupplyConstants.Symbol.FILE_NAME_SPLIT).append(supplierOrderInfo.getJdDistrict());
                if(StringUtils.isNotBlank(supplierOrderInfo.getJdTown()))
                    sb.append(SupplyConstants.Symbol.FILE_NAME_SPLIT).append(supplierOrderInfo.getJdTown());
                if(sb.length() > 0)
                    warehouseOrder.setJdAddress(sb.toString());
            }
        }
        return warehouseOrder;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.SHOP_ORDER)
    public List<PlatformOrder> queryPlatformOrders(PlatformOrderForm form) {
        AssertUtil.notNull(form, "查询平台订单列表参数对象不能为空");
        PlatformOrder platformOrder = new PlatformOrder();
        BeanUtils.copyProperties(form, platformOrder);
        return platformOrderService.select(platformOrder);
    }

    @Override
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单京东订单仓库订单编码不能为空");
        AssertUtil.notBlank(jdAddressCode, "提交订单京东订单四级地址编码不能为空");
        AssertUtil.notBlank(jdAddressName, "提交订单京东订单四级地址不能为空");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressCode, "提交订单京东订单四级地址编码格式错误");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressName, "提交订单京东订单四级地址格式错误");
        //添加锁
        String identifier = redisLock.Lock(DistributeLockEnum.SUBMIT_JINGDONG_ORDERßßß.getCode() + warehouseOrderCode, 10000, 600000);
        if(StringUtils.isBlank(identifier)){
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "操作成功", "");
        }
        try{
            //获取京东四级地址
            String[] jdAddressCodes = jdAddressCode.split(JING_DONG_ADDRESS_SPLIT);
            String[] jdAddressNames = jdAddressName.split(JING_DONG_ADDRESS_SPLIT);
            AssertUtil.isTrue(jdAddressCodes.length == jdAddressNames.length, "京东四级地址编码与名称个数不匹配");
            //检查供应商订单状态
            boolean orderStatus = checkSupplierOrderStatus(warehouseOrderCode);
            if(orderStatus){
                throw new OrderException(ExceptionEnum.ORDER_REPAT_SUBMIT, "订单已经下单成功,请刷新页面查看最新订单信息!");
            }
            ResponseAck responseAck = null;
            //获取供应链订单数据
            Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
            PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
            WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
            List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
            //获取京东订单对象
            JingDongSupplierOrder jingDongOrder = getJingDongOrder(warehouseOrder, platformOrder, orderItemList, jdAddressCodes);
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.SUBMIT_JINGDONG_ORDER.getMessage(), null,null);
            //调用京东下单服务接口
            responseAck = invokeSubmitSuuplierOrder(jingDongOrder);
            ResponseAck responseAck2 = null;
            //保存请求流水
            requestFlowBiz.saveRequestFlow(JSONObject.toJSON(jingDongOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.JINGDONG, RequestFlowTypeEnum.JD_SUBMIT_ORDER, responseAck, RequestFlowConstant.GYL);
            if(StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)){
                AssertUtil.notNull(responseAck.getData(), "调用京东下单服务返回下单结果信息为空");
                AssertUtil.notBlank(responseAck.getData().toString(), "调用京东下单服务返回下单结果信息为空");
                OrderSubmitResult orderSubmitResult = getOrderSubmitResult(responseAck.getData().toString());
                List<SupplierOrderReturn> supplierOrderReturnList = orderSubmitResult.getOrder();
                AssertUtil.notEmpty(supplierOrderReturnList, "调用京东下单服务返回下单明细信息为空");
                SupplierOrderReturn supplierOrderReturn = supplierOrderReturnList.get(0);//每次京东下单只会有一个订单
                RequestFlowTypeEnum requestFlowTypeEnum = RequestFlowTypeEnum.JD_SUBMIT_ORDER;
                if(!StringUtils.equals(ResponseAck.SUCCESS_CODE, supplierOrderReturn.getState())){
                    if(StringUtils.equals(JD_ORDER_SUBMIT_PRICE_ERROR, supplierOrderReturn.getState())){//京东商品价格不匹配
                        log.info(String.format("调用京东下单商品价格不匹配,更新京东最新价格重新下单"));
                        responseAck = reSubmitJingDongOrder(jingDongOrder);
                        AssertUtil.notNull(responseAck.getData(), "调用京东下单服务返回下单结果为空");
                        OrderSubmitResult orderSubmitResult2 = getOrderSubmitResult(responseAck.getData().toString());
                        if(StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)){
                            List<SupplierOrderReturn> supplierOrderReturnList2 = orderSubmitResult2.getOrder();
                            AssertUtil.notEmpty(supplierOrderReturnList2, "调用京东下单服务返回下单明细信息为空");
                            SupplierOrderReturn supplierOrderReturn2 = supplierOrderReturnList2.get(0);//每次京东下单只会有一个订单
                            if(!StringUtils.equals(ResponseAck.SUCCESS_CODE, supplierOrderReturn2.getState())){
                                if(StringUtils.equals(JD_BALANCE_NOT_ENOUGH, supplierOrderReturn2.getState())){//京东下单余额不足
                                    //TODO 这里以后可能会发起重新下单逻辑
                                    requestFlowTypeEnum = RequestFlowTypeEnum.JD_BALANCE_NOT_ENOUGH;
                                    responseAck2 = new ResponseAck(supplierOrderReturn2.getState(), supplierOrderReturn2.getMessage(), "");
                                    log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), supplierOrderReturn2.getMessage()));
                                }else if(StringUtils.equals(JD_BALANCE_NOT_ENOUGH, supplierOrderReturn2.getState())){//京东下单库存不足
                                    //TODO 这里以后可能会发起重新下单逻辑
                                    responseAck2 = new ResponseAck(supplierOrderReturn2.getState(), supplierOrderReturn2.getMessage(), "");
                                    log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), supplierOrderReturn2.getMessage()));
                                }else {
                                    responseAck2 = new ResponseAck(supplierOrderReturn2.getState(), supplierOrderReturn2.getMessage(), "");
                                    log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), supplierOrderReturn2.getMessage()));
                                }
                            }
                        }else {
                            responseAck2 = responseAck;
                        }
                        requestFlowTypeEnum = RequestFlowTypeEnum.JD_SKU_PRICE_UPDATE_SUBMIT_ORDER;
                        //保存请求流水
                        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(jingDongOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.JINGDONG, requestFlowTypeEnum, responseAck, RequestFlowConstant.GYL);
                    }else if(StringUtils.equals(JD_BALANCE_NOT_ENOUGH, supplierOrderReturn.getState())){//京东下单余额不足
                        //TODO 这里以后可能会发起重新下单逻辑
                        requestFlowTypeEnum = RequestFlowTypeEnum.JD_BALANCE_NOT_ENOUGH;
                        responseAck2 = new ResponseAck(supplierOrderReturn.getState(), supplierOrderReturn.getMessage(), "");
                        log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), supplierOrderReturn.getMessage()));
                    }else if(StringUtils.equals(JD_BALANCE_NOT_ENOUGH, supplierOrderReturn.getState())){//京东下单库存不足
                        //TODO 这里以后可能会发起重新下单逻辑
                        responseAck2 = new ResponseAck(supplierOrderReturn.getState(), supplierOrderReturn.getMessage(), "");
                        log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), supplierOrderReturn.getMessage()));
                    }
                }else{
                    log.info(String.format("调用京东下单接口提交订单%s成功", JSONObject.toJSON(jingDongOrder)));
                }
            }else{
                responseAck2 = responseAck;
                log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), responseAck.getMessage()));
            }
            //保存京东订单信息
            List<SupplierOrderInfo> supplierOrderInfoList = saveSupplierOrderInfo(warehouseOrder, responseAck, orderItemList, jdAddressCodes, jdAddressNames, ZeroToNineEnum.ZERO.getCode());
            //更新订单商品供应商订单状态
            updateOrderItemSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderInfoList);
            //更新仓库订单供应商订单状态
            warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), true);
            //更新店铺订单供应商订单状态
            updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
            if(StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)){
                String msg = String.format("提交仓库级订单编码为[%s]的京东订单下单成功", warehouseOrderCode);
                log.info(msg);
            }else{
                log.error(String.format("调用京东下单接口提交仓库订单%s失败,错误信息:%s", JSONObject.toJSON(warehouseOrder), responseAck.getMessage()));
            }
            //下单结果通知渠道
            notifyChannelSubmitOrderResult(warehouseOrder);
            if(null != responseAck2){
                return responseAck2;
            }
            return responseAck;
        }catch (Exception e){
            String msg = String.format("仓库级订单编码为[%s]的京东订单下单异常,异常信息:%s",
                    warehouseOrderCode, e.getMessage());
            log.error(msg, e);
            return new ResponseAck(ExceptionEnum.SUBMIT_JING_DONG_ORDER.getCode(), e.getMessage(), "");
        }finally {
            boolean flag = redisLock.releaseLock(DistributeLockEnum.SUBMIT_JINGDONG_ORDERßßß.getCode() + warehouseOrderCode, identifier);
            if(!flag){
                log.error(String.format("仓库级订单编码为[%s]的京东订单下单释放redis锁失败", warehouseOrderCode));
            }
        }
    }

    /**
     * 检查供应商订单状态
     * @param warehouseOrderCode
     */
    private boolean checkSupplierOrderStatus(String warehouseOrderCode){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrderCode);
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            if(StringUtils.equals(ResponseAck.SUCCESS_CODE, supplierOrderInfo2.getStatus())){
                return true;
            }
        }
        return false;
    }


    /**
     * 获取异常订单错误信息
     * @param supplierOrderInfoList
     * @return
     */
    private String getOrderExceptionMessage(List<SupplierOrderInfo> supplierOrderInfoList){
        StringBuilder sb = new StringBuilder();
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
            if (StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())) {
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_SUCCESS_INFO).append(HTML_BR);
                }
            } else if (StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())) {
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_PART_INFO).append(HTML_BR);
                }
            } else if (StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())) {
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_ALL_INFO).append(HTML_BR);
                }
            } else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_FAILURE_INFO)
                    	.append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append(HTML_BR);
                }
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_CANCEL_INFO).append(SupplyConstants.Symbol.COMMA).append(HTML_BR);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取异常订单错误信息,给供应商订单分页查询页面用
     * @param supplierOrderInfoList
     * @return
     */
    private String getOrderExceptionMessage2(List<SupplierOrderInfo> supplierOrderInfoList){
        StringBuilder sb = new StringBuilder();
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
            if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_SUCCESS_INFO).append(HTML_BR);
                }
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append("<font color=\"red\">").append(skuInfo.getSkuCode()).append(":").append(ORDER_FAILURE_INFO).append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append("</font>").append(HTML_BR);
                }
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    //sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_CANCEL_INFO).append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append(HTML_BR);
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_CANCEL_INFO).append(HTML_BR);
                }
            }
        }
        return sb.toString();
    }



    /**
     * 重新提交京东订单
     * @param jingDongOrder
     * @return
     */
    private ResponseAck reSubmitJingDongOrder(JingDongSupplierOrder jingDongOrder){
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
            return new ResponseAck(ExceptionEnum.ORDER_PARAM_DATA_ERROR, "");
        }
        //调用京东sku价格查询接口
        ReturnTypeDO returnPrice = ijdService.getSellPrice(skus);
        if(!returnPrice.getSuccess()){
            String msg = String.format("调用京东sku价格查询服务失败,错误信息:%s", returnPrice.getResultMessage());
            log.error(msg);
            return new ResponseAck(ExceptionEnum.INVOKE_JD_QUERY_INTERFACE_FAIL, "");
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
        ResponseAck responseAck = invokeSubmitSuuplierOrder(jingDongOrder);
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            log.info(String.format("调用京东下单接口提交订单%s成功", JSONObject.toJSON(jingDongOrder)));
        }else{
            log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), responseAck.getMessage()));
        }
        return responseAck;
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
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck submitLiangYouOrder(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单粮油订单仓库订单编码不能为空");
        //获取供应链订单数据
        Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
        PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
        WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
        List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
        //设置订单提交时间
        setOrderSubmitTime(orderItemList);
        //获取粮油订单对象
        LiangYouSupplierOrder liangYouOrder = getLiangYouOrder(warehouseOrder, platformOrder, orderItemList);
        //调用粮油下单服务接口
        ResponseAck responseAck = invokeSubmitSuuplierOrder(liangYouOrder);
        /**
         * 记录发送日志 
         ***/
//        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            log.info(responseAck.getMessage());
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), SYSTEM, LogOperationEnum.SUBMIT_ORDER.getMessage(), null,null);
//        }
        
        //保存请求流水
        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(liangYouOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.LY, RequestFlowTypeEnum.LY_SUBMIT_ORDER, responseAck, RequestFlowConstant.GYL);
        //保存粮油订单信息
        List<SupplierOrderInfo> supplierOrderInfoList = saveSupplierOrderInfo(warehouseOrder, responseAck, orderItemList, new String[0], new String[0], ZeroToNineEnum.ZERO.getCode());
        //更新订单商品供应商订单状态
        updateOrderItemSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderInfoList);
        //更新仓库订单供应商订单状态
        warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), true);
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());

        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            log.info(String.format("调用粮油下单接口提交仓库订单%s成功", JSONObject.toJSON(warehouseOrder)));
        }else{
            log.error(String.format("调用粮油下单接口提交仓库订单%s失败,错误信息:%s", JSONObject.toJSON(warehouseOrder), responseAck.getMessage()));
        }
        if(!StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            String msg = String.format("提交仓库级订单编码为[%s]的粮油订单下单失败。粮油下单接口返回错误信息:%s",
                    warehouseOrderCode, responseAck.getMessage());
            log.error(msg);
        }
        return responseAck;
    }

    /**
     * 设置订单提交时间
     * @param orderItemList
     */
    private void setOrderSubmitTime(List<OrderItem> orderItemList){
        Date currentTime = Calendar.getInstance().getTime();
        List<Long> ids = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            ids.add(orderItem.getId());
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setSubmitTime(currentTime);
        orderItem.setUpdateTime(currentTime);
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        orderItemService.updateByExampleSelective(orderItem, example);
    }

    /**
     * 更新订单商品供应商订单状态
     * @param warehouseOrderCode
     * @param supplierOrderInfoList
     */
    private void updateOrderItemSupplierOrderStatus(String warehouseOrderCode, List<SupplierOrderInfo> supplierOrderInfoList){
        if(CollectionUtils.isEmpty(supplierOrderInfoList))
            return;
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新订单商品供应商订单状态,根据仓库订单号[%s]查询相应的商品明细为空", warehouseOrderCode));
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            List<SkuInfo> skuInfos = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
            for(SkuInfo skuInfo : skuInfos){
                for(OrderItem orderItem2: orderItemList){
                    if(StringUtils.equals(orderItem2.getSupplierSkuCode(), skuInfo.getSkuCode())){
                        orderItem2.setSupplierOrderStatus(supplierOrderInfo.getSupplierOrderStatus());
                        /**
                         * 说明：SupplierOrderInfo级别供应商下单异常状态(同时存在供应商下单失败和等待供应商发货的商品，
                         * 或同时存在供应商下单失败或已取消的商品)是不会出现的，要么下单全部成功，要么下单全部失败
                         */
                        if(StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus()) ||
                                StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())
                                ){
                            if(orderItem2.getNum() == skuInfo.getNum()){
                                orderItem2.setSupplierOrderStatus(SupplierOrderStatusEnum.ALL_DELIVER.getCode());
                            }else if(orderItem2.getNum() > skuInfo.getNum()){
                                orderItem2.setSupplierOrderStatus(SupplierOrderStatusEnum.PARTS_DELIVER.getCode());
                            }{
                                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("仓库订单编码为%s的订单中商品sku为%s的商品数量为%s,而供应商下单接口返回的商品数量为%s",
                                        warehouseOrderCode, orderItem2.getSupplierSkuCode(), orderItem2.getNum(), skuInfo.getNum()));
                            }
                        }
                        orderItemService.updateByPrimaryKey(orderItem2);
                    }
                }
            }
        }
    }

    /**
     * 更新仓库订单供应商订单状态
     * @param warehouseOrderCode
     */
    private WarehouseOrder updateWarehouseOrderSupplierOrderStatus(String warehouseOrderCode, boolean flg){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新仓库订单供应商订单状态,根据仓库订单号[%s]查询相应的商品明细为空", warehouseOrderCode));
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("更新仓库订单供应商订单状态,根据仓库订单编码%s查询仓库订单信息为空", warehouseOrder));
        if(StringUtils.equals(OrderTypeEnum.SUPPLIER.getCode(), warehouseOrder.getOrderType())){
            //更新代发订单供应商订单状态
            updateWarehouseOrderSupplierOrderStatus_supplier(warehouseOrder, orderItemList, flg);
        }else if(StringUtils.equals(OrderTypeEnum.SELF_PURCHARSE.getCode(), warehouseOrder.getOrderType())){
            //更新自采仓库订单供应商订单状态
            updateWarehouseOrderSupplierOrderStatus_selfPurchase(warehouseOrder, orderItemList);
        }
        return warehouseOrder;
    }


    /**
     * 更新代发仓库订单供应商订单状态
     * @param warehouseOrder
     * @param orderItemList
     * @param isStatus
     */
    private void updateWarehouseOrderSupplierOrderStatus_supplier(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList,
    		boolean isStatus){
        String supplierOrderStatus = getSupplierOrderStatusByItems(orderItemList, ZeroToNineEnum.ZERO.getCode());
        if(StringUtils.isBlank(supplierOrderStatus)){
            return;
        }
        warehouseOrder.setSupplierOrderStatus(supplierOrderStatus);
        warehouseOrder.setUpdateTime(Calendar.getInstance().getTime());
        warehouseOrderService.updateByPrimaryKey(warehouseOrder);
        LogOperationEnum logOperationEnum = null;
        // 状态订单，用于判断下单成功 异常 失败
        if (isStatus) {
        	if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode(), warehouseOrder.getSupplierOrderStatus())){
        		logOperationEnum = LogOperationEnum.ORDER_EXCEPTION;
        	}else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), warehouseOrder.getSupplierOrderStatus())){
        		logOperationEnum = LogOperationEnum.ORDER_FAILURE;
        	}else if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
//        		logOperationEnum = LogOperationEnum.WAIT_FOR_DELIVER;
        		//下单成功
        		logOperationEnum = LogOperationEnum.ORDER_SUCCESS;
        	}else if(StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
//        		logOperationEnum = LogOperationEnum.PARTS_DELIVER;
        		logOperationEnum = LogOperationEnum.SEND;
        	}else if(StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
//        		logOperationEnum = LogOperationEnum.ALL_DELIVER;
        		logOperationEnum = LogOperationEnum.SEND;
        	}
        }
        // 是否需要记日志
        if (null != logOperationEnum) {
            String remark = "";
            if(!StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(),
            		warehouseOrder.getSupplierOrderStatus())){ // 非下单成功的状态，下单成功不需要备注信息
                SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
                supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
                if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
                    remark = getOrderExceptionMessage(supplierOrderInfoList);
                }
            }
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), 
            		warehouseOrder.getSupplierName(), logOperationEnum.getMessage(), remark,null);
        }
    }

    /**
     * 更新自采仓库订单供应商订单状态
     * @param warehouseOrder
     * @param orderItemList
     */
    private void updateWarehouseOrderSupplierOrderStatus_selfPurchase(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        warehouseOrder.setSupplierOrderStatus(getSupplierOrderStatusByItems(orderItemList, ZeroToNineEnum.ZERO.getCode()));
        warehouseOrder.setUpdateTime(Calendar.getInstance().getTime());
        warehouseOrderService.updateByPrimaryKey(warehouseOrder);
    }




    /**
     *根据订单商品状态获取订单状态(代发)
     * @param orderItemList
     * @param flag：0-仓库级订单,1-店铺级订单
     * @return
     */
    /*private String getSupplierOrderStatusByItems(List<OrderItem> orderItemList, String flag){
        int failureNum = 0;//供应商下单失败数
        int waitDeliverNum = 0;//等待供应商发货数
        int allDeliverNum = 0;//全部发货数
        int partsDeliverNum = 0;//部分发货数
        int cancelNum = 0;//已取消数
        for(OrderItem orderItem: orderItemList){
            if(StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus()))
                failureNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.WAIT_FOR_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                waitDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                allDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.PARTS_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                partsDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem.getSupplierOrderStatus()))
                cancelNum++;
        }
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//仓库级订单
            //供应商下单失败：所有商品的发货状态均为“供应商下单失败”时，供应商订单的状态就为“供应商下单失败”
            if(failureNum == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_FAILURE.getCode();
            //等待供应商发货：所有商品的发货状态均为“等待供应商发货”时，供应商订单的状态就为“等待供应商发货”
            if(waitDeliverNum == orderItemList.size())
                return SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode();
            //部分发货：存在发货状态为“部分发货”的商品或者同时存在待发货和已发货(部分发货或全部发货)的商品，供应商订单的状态就为“部分发货”
            if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0)))
                return SupplierOrderStatusEnum.PARTS_DELIVER.getCode();
            //全部发货：所有商品的发货状态均更新为“全部发货”时，供应商订单的状态就更新为“全部发货”
            if(allDeliverNum + cancelNum == orderItemList.size())
                return SupplierOrderStatusEnum.ALL_DELIVER.getCode();
            //已取消：所有商品的发货状态均更新为“已取消”时，供应商订单的状态就更新为“已取消”；
            if(cancelNum == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_CANCEL.getCode();
            //供应商下单异常: 同时存在供应商下单失败和等待供应商发货的商品，或同时存在供应商下单失败或已取消的商品），供应商订单的状态就为“供应商下单异常”
            if(failureNum > 0 &&  failureNum < orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode();
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//店铺级订单
            //已取消：订单中全部商品的发货状态均为“已取消”时，订单就更新为“已取消”
            if(cancelNum == orderItemList.size())
                return OrderDeliverStatusEnum.ORDER_CANCEL.getCode();
            //部分发货：订单中存在发货状态为“部分发货”的商品或者同时存在待发货和已发货(部分发货或全部发货)的商品，订单的状态就为“部分发货”；
            if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0)))
                return OrderDeliverStatusEnum.PARTS_DELIVER.getCode();
            //全部发货：订单中除去发货状态为“已取消”的商品外，其他商品的发货状态均为“全部发货”时，订单就更新为“全部发货”；
            if(allDeliverNum == orderItemList.size() || (allDeliverNum+cancelNum) == orderItemList.size())
                return OrderDeliverStatusEnum.ALL_DELIVER.getCode();
        }
        return null;
    }*/


    /**
     * 根据订单商品状态获取订单状态(自采)
     * @param orderItemList
     * @param flag：0-仓库级订单,1-店铺级订单
     * @return
     */
    private String getSupplierOrderStatusByItems(List<OrderItem> orderItemList, String flag){
        int waitHandlerNum = 0;//待了结数
        int handlerNum = 0;//已了结数
        int sendSupplierFialure = 0;//发送供应商失败数
        int sendWarehouseFialure = 0;//仓库接收失败数
        int waitDeliverNum = 0;//等待供应商发货数
        int waitWarehouseDeliverNum = 0;//等待仓库发货数
        int warehouseSendProcessrNum = 0;//仓库告知的过程中状态数
        int allDeliverNum = 0;//代发全部发货数
        int partsDeliverNum = 0;//代发部分发货数
        int cancelNum = 0;//已取消数
        int cancellingNum = 0;//取消中
        int offLineDeliverNum = 0;//线下发货s数量
        for(OrderItem orderItem: orderItemList){
            if(StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus()))
                sendSupplierFialure++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.WAIT_FOR_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                waitDeliverNum++;
            if(StringUtils.equals(OrderItemDeliverStatusEnum.WAREHOUSE_RECIVE_FAILURE.getCode(), orderItem.getSupplierOrderStatus()))
                sendWarehouseFialure++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                waitWarehouseDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.WAREHOUSE_MIDDEL_STATUS.getCode(), orderItem.getSupplierOrderStatus()))
                warehouseSendProcessrNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                allDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.PARTS_DELIVER.getCode(), orderItem.getSupplierOrderStatus()))
                partsDeliverNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem.getSupplierOrderStatus()))
                cancelNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.WAIT_HANDLER.getCode().toString(), orderItem.getSupplierOrderStatus()))
                waitHandlerNum++;
            else if(StringUtils.equals(ExceptionOrderHandlerEnum.HANDLERED.getCode().toString(), orderItem.getSupplierOrderStatus()))
                handlerNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCELING.getCode().toString(), orderItem.getSupplierOrderStatus()))
            	cancellingNum++;
            else if(StringUtils.equals(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode().toString(), orderItem.getSupplierOrderStatus()))
                offLineDeliverNum++;
        }
        
        /**  取消中数量等价于等待仓库发货数   **/
        waitWarehouseDeliverNum = waitWarehouseDeliverNum + cancellingNum;
        /**  线下发货数量等价于全部发货数   **/
        allDeliverNum = allDeliverNum + offLineDeliverNum;

        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//仓库级订单
            //下单失败：(发送供应商失败数 + 仓库接收失败数) > 0 && (发送供应商失败数 + 仓库接收失败数 + 已了结数 + 已取消) = 商品应发数量
            if((sendSupplierFialure + sendWarehouseFialure + handlerNum) > 0 && (sendSupplierFialure + sendWarehouseFialure + handlerNum + cancelNum) == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_FAILURE.getCode();
            //等待供应商发货：（等待供应商发货数 + 等待仓库发货数）> 0 
            	//&& (等待供应商发货数 + 等待仓库发货数 + 已了结数 + 已取消) = 商品应发数量
            if((waitDeliverNum + waitWarehouseDeliverNum) > 0 
            		&& (waitDeliverNum + waitWarehouseDeliverNum + handlerNum + cancelNum) == orderItemList.size())
                return SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode();
            //供应商下单异常: (发送供应商失败数 + 仓库接收失败数) > 0 
            	//&& (等待供应商发货数 + 等待仓库发货数 + 全部发货数 + 已取消  + 部分发货) > 0
            if((sendWarehouseFialure + sendSupplierFialure) > 0 
            		&& (waitDeliverNum + waitWarehouseDeliverNum + allDeliverNum + cancelNum + partsDeliverNum) > 0)
              return SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode();
            //部分发货：部分发货数 > 0 || ((等待供应商发货数 + 等待仓库发货数) > 0 && 全部发货数 > 0)
            if(partsDeliverNum > 0 || ((waitDeliverNum + waitWarehouseDeliverNum) > 0 && allDeliverNum > 0))
                return SupplierOrderStatusEnum.PARTS_DELIVER.getCode();
            //全部发货：全部发货数 > 0 && (全部发货数 + 已了结数 + 已取消数) = 商品应发数量
            if(allDeliverNum > 0 && (allDeliverNum + handlerNum + cancelNum) == orderItemList.size())
                return SupplierOrderStatusEnum.ALL_DELIVER.getCode();
            //已取消：已取消数 > 0 && (已了结数 + 已取消数) = 商品应发数量
            if(cancelNum > 0 && (handlerNum + cancelNum) == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_CANCEL.getCode();
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//店铺级订单
            //待发货：(等待供应商发货数 + 等待仓库发货数) = 商品应发数量
            if((waitDeliverNum + waitWarehouseDeliverNum) == orderItemList.size())
                return OrderDeliverStatusEnum.WAIT_FOR_DELIVER.getCode();
            //已取消：已取消数 = 商品应发数量
            if(cancelNum == orderItemList.size())
                return OrderDeliverStatusEnum.ORDER_CANCEL.getCode();
            //发货异常: 供应商下单失败数 > 0 || 仓库接收失败数 > 0 || 缺货数  > 0
            if(sendSupplierFialure > 0 || sendWarehouseFialure > 0 || handlerNum > 0)
              return OrderDeliverStatusEnum.DELIVER_EXCEPTION.getCode();
            //全部发货：全部发货数 > 0 && (全部发货数 + 已取消数) = 商品应发数量
            if(allDeliverNum > 0 && (allDeliverNum + cancelNum) == orderItemList.size())
                return OrderDeliverStatusEnum.ALL_DELIVER.getCode();
            //部分发货：部分发货数 > 0 || ((等待供应商发货数 + 等待仓库发货数) > 0 && 全部发货数 > 0)
            if(partsDeliverNum > 0 || ((waitDeliverNum + waitWarehouseDeliverNum) > 0 && allDeliverNum > 0))
                return OrderDeliverStatusEnum.PARTS_DELIVER.getCode();
        }
        return null;
    }

    /**
     * 更新店铺订单供应商订单状态
     * @param scmShopOrderCode
     * @param shopOrderCode
     */
    /*private void updateShopOrderSupplierOrderStatus(String platformOrderCode, String shopOrderCode){
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setPlatformOrderCode(platformOrderCode);
        shopOrder.setShopOrderCode(shopOrderCode);
        shopOrder = shopOrderService.selectOne(shopOrder);
        AssertUtil.notNull(shopOrder, String.format("更新店铺订单供应商订单状态,根据平台订单编码%s和店铺订单编码%s查询店铺订单信息为空", platformOrderCode, shopOrderCode));
        int waitSendNum = 0;//待发送供应商数
        int exceptionNum = 0;//供应商下单异常数
        int failureNum = 0;//供应商下单失败数
        int waitDeliverNum = 0;//等待供应商发货数
        int allDeliverNum = 0;//全部发货数
        int partsDeliverNum = 0;//部分发货数
        int cancelNum = 0;//已取消数
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setPlatformOrderCode(platformOrderCode);
        warehouseOrder.setShopOrderCode(shopOrderCode);
        List<WarehouseOrder> warehouseOrderList = warehouseOrderService.select(warehouseOrder);
        for(WarehouseOrder warehouseOrder2: warehouseOrderList){
            if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                waitSendNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                exceptionNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                failureNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                waitDeliverNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                allDeliverNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                waitSendNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                partsDeliverNum++;
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), warehouseOrder2.getSupplierOrderStatus())){
                cancelNum++;
            }
        }
        //已取消：
        if(cancelNum == warehouseOrderList.size())
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.ORDER_CANCEL.getCode());
        //待发货：
        if(waitSendNum > 0 && (waitSendNum + cancelNum) == warehouseOrderList.size())
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.WAIT_FOR_DELIVER.getCode());
        //部分发货:
        if(partsDeliverNum > 0)
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.PARTS_DELIVER.getCode());
        //全部发货：
        if(allDeliverNum > 0 && (allDeliverNum + cancelNum) == warehouseOrderList.size())
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.ALL_DELIVER.getCode());
        shopOrder.setUpdateTime(Calendar.getInstance().getTime());
        shopOrderService.updateByPrimaryKey(shopOrder);
    }*/

    private void updateShopOrderSupplierOrderStatus(String scmShopOrderCode, String shopOrderCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setScmShopOrderCode(scmShopOrderCode);
        orderItem.setShopOrderCode(shopOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新店铺订单供应商订单状态,根据店铺订单号[%s]查询相应的商品明细为空", shopOrderCode));
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setScmShopOrderCode(scmShopOrderCode);
        shopOrder.setShopOrderCode(shopOrderCode);
        shopOrder = shopOrderService.selectOne(shopOrder);
        AssertUtil.notNull(shopOrder, String.format("更新店铺订单供应商订单状态,根据系统订单编码%s和店铺订单编码%s查询店铺订单信息为空", scmShopOrderCode, shopOrderCode));
        String supplierOrderStatus = getSupplierOrderStatusByItems(orderItemList, ZeroToNineEnum.ONE.getCode());
        if(StringUtils.isNotBlank(supplierOrderStatus)){
            shopOrder.setSupplierOrderStatus(supplierOrderStatus);
            shopOrder.setUpdateTime(Calendar.getInstance().getTime());
            shopOrderService.updateByPrimaryKey(shopOrder);
        }
    }


    @Override
    @SupplierOrderCacheEvict
    public void submitLiangYouOrders(List<WarehouseOrder> warehouseOrders) {
        for(WarehouseOrder warehouseOrder: warehouseOrders){
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        //提交订单
                        ResponseAck responseAck = submitLiangYouOrder(warehouseOrder.getWarehouseOrderCode());
                        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
                            log.info(responseAck.getMessage());
                        }else{
                            log.error(String.format("仓库订单号为[%s]的粮油订单下单失败,错误代码[%s],错误信息：%s;", warehouseOrder.getWarehouseOrderCode(), responseAck.getCode(), responseAck.getMessage()));
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

    @Override
    public void saveChannelOrderRequestFlow(String orderInfo, ResponseAck responseAck) {
        requestFlowBiz.saveRequestFlow(orderInfo, RequestFlowConstant.TRC, RequestFlowConstant.GYL, RequestFlowTypeEnum.RECEIVE_CHANNEL_ORDER, responseAck, RequestFlowConstant.GYL);
    }

    /**
     * 获取京东订单信息
     * @param warehouseOrder
     * @param platformOrder
     * @param orderItemList
     * @param jdAddressCodes
     * @return
     */
    private JingDongSupplierOrder getJingDongOrder(WarehouseOrder warehouseOrder, PlatformOrder platformOrder, List<OrderItem> orderItemList, String[] jdAddressCodes){
        JingDongSupplierOrder jingDongOrder = new JingDongSupplierOrder();
        jingDongOrder.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
        jingDongOrder.setShopOrderCode(warehouseOrder.getShopOrderCode());
        jingDongOrder.setOrderSubmitTime(platformOrder.getCreateTime());
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
        jingDongOrder.setSubmitState(Integer.parseInt(jdSubmitState));//预占库存
        jingDongOrder.setInvoiceName(platformOrder.getReceiverName());
        jingDongOrder.setInvoiceAddress(platformOrder.getReceiverAddress());
        jingDongOrder.setDoOrderPriceMode(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));//下单价格模式,1-必需验证客户端订单价格快照
        // TODO 大家电、中小件配送安装参数设置
        //设置京东订单SKU信息
        setJdOrderSkuInfo(jingDongOrder, orderItemList);
        jingDongOrder.setSubmitOrderStatus(submitOrderStatus);
        return jingDongOrder;
    }


    /**
     * 获取粮油订单信息
     * @param warehouseOrder
     * @param platformOrder
     * @param orderItemList
     * @return
     */
    private LiangYouSupplierOrder getLiangYouOrder(WarehouseOrder warehouseOrder, PlatformOrder platformOrder, List<OrderItem> orderItemList){
        LiangYouSupplierOrder liangYouOrder = new LiangYouSupplierOrder();
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
        liangYouOrder.setSubmitOrderStatus(submitOrderStatus);
        return liangYouOrder;
    }

    /**
     * 设置京东订单SKU信息
     * @param jingDongOrder
     * @param orderItemList
     */
    private void setJdOrderSkuInfo(JingDongSupplierOrder jingDongOrder, List<OrderItem> orderItemList){
        List<JdSku> jdSkuList = new ArrayList<JdSku>();
        List<OrderPriceSnap> orderPriceSnapList = new ArrayList<OrderPriceSnap>();
        for(OrderItem orderItem2: orderItemList){
            JdSku jdSku = new JdSku();
            jdSku.setSkuId(orderItem2.getSupplierSkuCode());
            jdSku.setNum(orderItem2.getNum());
            jdSku.setPayment(orderItem2.getPayment());
            jdSku.setbNeedAnnex(true);
            jdSku.setbNeedGift(false);
            jdSku.setOrderItemCode(orderItem2.getOrderItemCode());
            jdSkuList.add(jdSku);

            OrderPriceSnap orderPriceSnap = new OrderPriceSnap();
            orderPriceSnap.setSkuId(Long.parseLong(orderItem2.getSupplierSkuCode()));
            orderPriceSnap.setPrice(orderItem2.getSupplyPrice());
            orderPriceSnapList.add(orderPriceSnap);
        }
        jingDongOrder.setSku(jdSkuList);
        jingDongOrder.setOrderPriceSnap(orderPriceSnapList);
    }

    /**
     *保存供应商订单信息
     * @param warehouseOrder
     * @param responseAck
     * @param jdAddressCodes
     * @param jdAddressNames
     * @param flag : 0-京东订单,1-粮油订单
     */
    private List<SupplierOrderInfo> saveSupplierOrderInfo(WarehouseOrder warehouseOrder,
                                                          ResponseAck responseAck, List<OrderItem> orderItemList, String[] jdAddressCodes, String[] jdAddressNames, String flag){
        SupplierOrderInfo _supplierOrderInfo = new SupplierOrderInfo();
        _supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(_supplierOrderInfo);
        //删除之前失败的下单记录
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            if(StringUtils.isBlank(supplierOrderInfo.getSupplierOrderCode())){
                supplierOrderInfoService.deleteByPrimaryKey(supplierOrderInfo.getId());
            }
        }
        List<SupplierOrderInfo> newSupplierOrderInfoList = new ArrayList<SupplierOrderInfo>();
        if(StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)){
            AssertUtil.notNull(responseAck.getData(), "供应商订单下单成功返回结果为空");
            OrderSubmitResult orderSubmitResult = getOrderSubmitResult(responseAck.getData().toString());
            List<SupplierOrderReturn> orders = orderSubmitResult.getOrder();
            for(SupplierOrderReturn order: orders){
                SupplierOrderInfo supplierOrderInfo = getSupplierOrderInfo(warehouseOrder, order, jdAddressCodes, jdAddressNames, flag);
                newSupplierOrderInfoList.add(supplierOrderInfo);
            }
        }else{
            log.error(String.format("调用下单服务接口失败,错误信息: %s", responseAck.getMessage()));
            SupplierOrderInfo supplierOrderInfo = getSupplierOrderFailureInfo(warehouseOrder, orderItemList, jdAddressCodes, jdAddressNames, responseAck);
            newSupplierOrderInfoList.add(supplierOrderInfo);
        }
        if(newSupplierOrderInfoList.size() > 0)
            supplierOrderInfoService.insertList(newSupplierOrderInfoList);
        return newSupplierOrderInfoList;
    }

    /**
     * 获取供应商下单结果
     * @param orderResult
     * @return
     */
    private OrderSubmitResult getOrderSubmitResult(String orderResult){
        OrderSubmitResult orderSubmitResult = null;
        try {
            JSONObject orderObj = JSONObject.parseObject(orderResult);
            orderSubmitResult = orderObj.toJavaObject(OrderSubmitResult.class);
        } catch (JSONException e) {
            String msg = String.format("供应商订单下单返回结果不是json格式");
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.ORDER_PARAM_DATA_ERROR, msg);
        } catch (ClassCastException e) {
            String msg = String.format("供应商订单下单返回结果格式错误");
            log.error(msg, e);
            throw new OrderException(ExceptionEnum.ORDER_PARAM_DATA_ERROR, msg);
        }
        return orderSubmitResult;
    }

    /**
     * 获取供应商订单
     * @param warehouseOrder
     * @param supplierOrderReturn
     * @param jdAddressCodes
     * @param jdAddressNames
     * @param flag
     * @return
     */
    private SupplierOrderInfo getSupplierOrderInfo(WarehouseOrder warehouseOrder, SupplierOrderReturn  supplierOrderReturn, String[] jdAddressCodes, String[] jdAddressNames, String flag){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
        String supplyOrderCode = supplierOrderReturn.getSupplyOrderCode();//供应商订单号
        String state = supplierOrderReturn.getState();//下单状态
        List<SkuInfo> skuInfos = supplierOrderReturn.getSkus();//订单相关sku
        supplierOrderInfo.setSupplierOrderCode(supplyOrderCode);
        supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());//未完成
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, state)){//供应商下单接口下单成功
            supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode());//待发货
        }else{
            supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.ORDER_FAILURE.getCode());//下单失败
        }
        supplierOrderInfo.setStatus(state);
        supplierOrderInfo.setMessage(supplierOrderReturn.getMessage());
        supplierOrderInfo.setSkus(JSON.toJSONString(skuInfos));
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//京东订单
            setSupplierOrderInfoJdAddress(supplierOrderInfo, jdAddressCodes, jdAddressNames);
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
    private SupplierOrderInfo getSupplierOrderFailureInfo(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList, String[] jdAddressCodes, String[] jdAddressNames, ResponseAck responseAck){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
        supplierOrderInfo.setStatus(responseAck.getCode());
        supplierOrderInfo.setMessage(responseAck.getMessage());
        supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.ORDER_FAILURE.getCode());//下单失败
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
        setSupplierOrderInfoJdAddress(supplierOrderInfo, jdAddressCodes, jdAddressNames);
        ParamsUtil.setBaseDO(supplierOrderInfo);
        return supplierOrderInfo;
    }

    /**
     * 设置供应商订单京东地址
     * @param supplierOrderInfo
     * @param jdAddressCodes
     * @param jdAddressNames
     */
    private void setSupplierOrderInfoJdAddress(SupplierOrderInfo supplierOrderInfo, String[] jdAddressCodes, String[] jdAddressNames){
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


    /**
     * 调用京东下单服务接口
     * @param orderInfo
     * @return
     */
    private ResponseAck invokeSubmitSuuplierOrder(Object orderInfo){
        ResponseAck responseAck = null;
        if(orderInfo instanceof JingDongSupplierOrder){
            responseAck = ijdService.submitJingDongOrder((JingDongSupplierOrder)orderInfo);
        }else if(orderInfo instanceof LiangYouSupplierOrder){
            responseAck = ijdService.submitLiangYouOrder((LiangYouSupplierOrder)orderInfo);
        }
        JSONObject jsonObject = (JSONObject)JSON.toJSON(orderInfo);
        //检查供应商订单状态
        boolean orderStatus = checkSupplierOrderStatus(jsonObject.getString("warehouseOrderCode"));
        if(orderStatus){
            throw new OrderException(ExceptionEnum.ORDER_REPAT_SUBMIT, "订单已经下单成功,请刷新页面查看最订单信息!");
        }
        return responseAck;
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
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//店铺订单分页查询
            ShopOrderForm shopOrderForm = (ShopOrderForm)queryModel;
            if (StringUtil.isNotEmpty(shopOrderForm.getType())) {//订单类型
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
                    setShopOrderItemsDetail(shopOrder, platformOrder, ZeroToNineEnum.ZERO.getCode());
                }
            }
        }
    }

    /**
     *
     * @param shopOrder
     * @param platformOrder
     * @param flag 0-店铺订单分页查询,1-订单列表查询
     */
    private void setShopOrderItemsDetail(ShopOrder shopOrder, PlatformOrder platformOrder, String flag){
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("shopOrderCode", shopOrder.getShopOrderCode());
        criteria.andEqualTo("platformOrderCode", shopOrder.getPlatformOrderCode());
        List<OrderItem> orderItemList = orderItemService.selectByExample(example);
        AssertUtil.notEmpty(orderItemList, String.format("根据平台订单编号[%s]和商铺订单编号[%s]查询订单商品明细为空",
                shopOrder.getPlatformOrderCode(), shopOrder.getShopOrderCode()));
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){
            //设置商品明细信息
            setOrderItemDetail(shopOrder.getPlatformOrderCode(), shopOrder.getShopOrderCode(), orderItemList);
        }
        for(OrderItem orderItem: orderItemList){
            orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
        }
        //设置商品扩展信息
        OrderBase orderBase = new OrderBase();
        BeanUtils.copyProperties(platformOrder, orderBase);
        BeanUtils.copyProperties(orderBase, shopOrder, "buyerMessage", "shopMemo", "sellCode");
        orderBase.setBuyerMessage(shopOrder.getBuyerMessage());
        orderBase.setShopMemo(shopOrder.getShopMemo());
        OrderExt orderExt = new OrderExt();
        BeanUtils.copyProperties(orderBase, orderExt);
        orderExt.setPayment(shopOrder.getPayment());
        orderExt.setPostageFee(shopOrder.getPostageFee());
        orderExt.setTotalTax(shopOrder.getTotalTax());
        orderExt.setOrderItemList(orderItemList);
        List<OrderExt> orderExts = new ArrayList<OrderExt>();
        orderExts.add(orderExt);
        shopOrder.setRecords(orderExts);
    }

    /**
     *设置订单商品明细信息
     * @param platformOrderCode
     * @param shopOrderCode
     * @param orderItemList
     */
    private void setOrderItemDetail(String platformOrderCode, String shopOrderCode, List<OrderItem> orderItemList){
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setPlatformOrderCode(platformOrderCode);
        warehouseOrder.setShopOrderCode(shopOrderCode);
        List<WarehouseOrder> warehouseOrderList = warehouseOrderService.select(warehouseOrder);
        if(CollectionUtils.isEmpty(warehouseOrderList)){
            log.error(String.format("根据平台订单号%s和店铺订单号%s查询仓库订单信息为空", platformOrderCode, shopOrderCode));
            return;
        }
        //设置商品类型:0-自采,1-代发，和发货仓库/发货供应商
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuCode().startsWith(SP0)) {
                orderItem.setItemType(ZeroToNineEnum.ZERO.getCode());
            }else if (orderItem.getSkuCode().startsWith(SP1)) {
                orderItem.setItemType(ZeroToNineEnum.ONE.getCode());
            }
            for(WarehouseOrder warehouseOrder2: warehouseOrderList){
                if(StringUtils.equals(orderItem.getWarehouseOrderCode(), warehouseOrder2.getWarehouseOrderCode())){
                    if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderItem.getItemType())){
                        //自采发货仓库名称 TODO
                        orderItem.setWarehouseName(warehouseOrder2.getWarehouseName());

                    }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderItem.getItemType())){
                        orderItem.setWarehouseName(warehouseOrder2.getSupplierName());
                    }
                    break;
                }
            }
        }

        Set<String> warehouseOrderCodes = new HashSet<>();
        for(WarehouseOrder warehouseOrder2: warehouseOrderList){
            warehouseOrderCodes.add(warehouseOrder2.getWarehouseOrderCode());
        }
        //设置供应商订单信息
        querySupplierOrderLogistics(orderItemList, warehouseOrderCodes);
        //设置自营订单信息
        queryOutOrderLogistics(orderItemList, warehouseOrderCodes);

    }

    private void queryOutOrderLogistics(List<OrderItem> orderItemList, Set<String> warehouseOrderCodes) {
        //通过warehouseOrderCode查询发货通知单
        Example example2 = new Example(OutboundOrder.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<OutboundOrder> outboundOrderList = outBoundOrderService.selectByExample(example2);
        if(CollectionUtils.isEmpty(outboundOrderList)){
            return;
        }
        int storeItemCount = 0;
        //设置企业购商品发货数量
        for(OutboundOrder outboundOrder: outboundOrderList){
            if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == outboundOrder.getIsStoreOrder().intValue()){//门店订单
                for(OrderItem orderItem: orderItemList){
                    if(StringUtils.equals(outboundOrder.getShopOrderCode(), orderItem.getShopOrderCode())){
                        orderItem.setDeliverNum(orderItem.getNum());
                        storeItemCount++;
                    }
                }
            }
        }
        if(storeItemCount == orderItemList.size()){//全部商品都是门店的
            return;
        }

        Map<String, OutboundOrder> outboundOrderMap = new HashMap<>();
        Set<String> outboundOrderCodeSet = new HashSet<>();
        for (OutboundOrder outboundOrder : outboundOrderList) {
            outboundOrderCodeSet.add(outboundOrder.getOutboundOrderCode());
            outboundOrderMap.put(outboundOrder.getWarehouseOrderCode(), outboundOrder);
        }
        //通过outboundOrderCode查询发货通知单详情
        Example example3 = new Example(OutboundDetail.class);
        Example.Criteria criteria3 = example3.createCriteria();
        criteria3.andIn("outboundOrderCode", outboundOrderCodeSet);
        List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example3);
        if (!AssertUtil.collectionIsEmpty(outboundDetailList)) {
            Map<String, List<OutboundDetail>> outboundDetailMap = new HashMap<>();
            Set<Long> outboundDetailIds = new HashSet<>();
            for(String outboundOrderCode: outboundOrderCodeSet){
                List<OutboundDetail> outboundDetails = new ArrayList<>();
                for (OutboundDetail outboundDetail : outboundDetailList) {
                    if(StringUtils.equals(outboundOrderCode, outboundDetail.getOutboundOrderCode())){
                        outboundDetails.add(outboundDetail);
                    }
                    outboundDetailIds.add(outboundDetail.getId());
                }
                outboundDetailMap.put(outboundOrderCode, outboundDetails);
            }
            //查询发货通知单物流信息
            Example example4 = new Example(OutboundDetailLogistics.class);
            Example.Criteria criteria4 = example4.createCriteria();
            criteria4.andIn("outboundDetailId", outboundDetailIds);
            List<OutboundDetailLogistics> outboundDetailLogisticsList = outboundDetailLogisticsService.selectByExample(example4);
            Map<Long, List<OutboundDetailLogistics>> outboundDetailLogisticsMap = new HashMap<>();
            if (!AssertUtil.collectionIsEmpty(outboundDetailLogisticsList)) {
                for (Long detailId : outboundDetailIds) {
                    List<OutboundDetailLogistics> detailLogisticsList = new ArrayList<>();
                    for (OutboundDetailLogistics outboundDetailLogistics : outboundDetailLogisticsList) {
                        if (detailId.equals(outboundDetailLogistics.getOutboundDetailId())) {
                            detailLogisticsList.add(outboundDetailLogistics);
                        }
                    }
                    outboundDetailLogisticsMap.put(detailId, detailLogisticsList);
                }
                //组装物流信息, //设置自采订单、物流信息
                for (OrderItem orderItem : orderItemList) {
                    if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderItem.getItemType())) {
                        if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == orderItem.getIsStoreOrder().intValue()){//门店订单
                            orderItem.setSendNum(orderItem.getNum());
                            continue;
                        }
                        //实发商品数量
                        OutboundOrder outboundOrder = outboundOrderMap.get(orderItem.getWarehouseOrderCode());
                        if (null != outboundOrder && null != outboundOrder.getOutboundOrderCode()) {
                            List<OutboundDetail> outboundDetails = outboundDetailMap.get(outboundOrder.getOutboundOrderCode());
                            OutboundDetail targetOutboundDetail = null;
                            if(!CollectionUtils.isEmpty(outboundDetails)){
                                for(OutboundDetail detail: outboundDetails){
                                    if(StringUtils.equals(orderItem.getSkuCode(), detail.getSkuCode()) && null != detail.getRealSentItemNum()){
                                        orderItem.setDeliverNum(Integer.parseInt(String.valueOf(detail.getRealSentItemNum())));
                                        targetOutboundDetail = detail;
                                        break;
                                    }
                                }
                            }
                            if(null != targetOutboundDetail){
                                //物流信息
                                List<OutboundDetailLogistics> logisticsList = outboundDetailLogisticsMap.get(targetOutboundDetail.getId());
                                //物流详细
                                List<DeliverPackageForm> deliverPackageFormList = new ArrayList<>();
                                if (!AssertUtil.collectionIsEmpty(logisticsList)) {
                                    for (OutboundDetailLogistics outboundDetailLogistics : logisticsList) {
                                        //物流信息
                                        DeliverPackageForm deliverPackageForm = new DeliverPackageForm();
                                        deliverPackageForm.setLogisticsCorporation(outboundDetailLogistics.getLogisticsCorporation());
                                        deliverPackageForm.setWaybillNumber(outboundDetailLogistics.getWaybillNumber());
                                        if (outboundDetailLogistics.getItemNum() != null) {
                                            deliverPackageForm.setSkuNum(Integer.parseInt(String.valueOf(outboundDetailLogistics.getItemNum())));
                                        }
                                        deliverPackageFormList.add(deliverPackageForm);
                                    }
                                    orderItem.setDeliverPackageFormList(deliverPackageFormList);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void querySupplierOrderLogistics(List<OrderItem> orderItemList, Set<String> warehouseOrderCodes) {
        Example example2 = new Example(SupplierOrderLogistics.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<SupplierOrderLogistics> supplierOrderLogisticsList = supplierOrderLogisticsService.selectByExample(example2);
        if(!CollectionUtils.isEmpty(supplierOrderLogisticsList)){
            //设置商品供应商订单、物流信息
            for (OrderItem orderItem : orderItemList) {
                if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderItem.getItemType())) {
                    StringBuilder sb = new StringBuilder();//供应商订单编码
                    int deliverNum = 0;//实发商品数量
                    List<DeliverPackageForm> deliverPackageFormList = new ArrayList<>();
                    Set<String> supplierSkus = new HashSet<>();
                    for (SupplierOrderLogistics supplierOrderLogistics2 : supplierOrderLogisticsList) {
                        List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderLogistics2.getSkus(), SkuInfo.class);
                        if (!CollectionUtils.isEmpty(skuInfoList)) {
                            for (SkuInfo skuInfo : skuInfoList) {
                                if (StringUtils.equals(orderItem.getSupplierSkuCode(), skuInfo.getSkuCode())) {
                                    deliverNum += skuInfo.getNum();
                                    if (StringUtils.isBlank(orderItem.getSupplierOrderCode())) {
                                        sb.append(supplierOrderLogistics2.getSupplierOrderCode()).append(SupplyConstants.Symbol.COMMA);
                                    }
                                    supplierSkus.add(skuInfo.getSkuCode());
                                    DeliverPackageForm deliverPackageForm = new DeliverPackageForm();
                                    deliverPackageForm.setSkuCode(skuInfo.getSkuCode());
                                    deliverPackageForm.setSkuNum(skuInfo.getNum());
                                    deliverPackageForm.setLogisticsCorporation(supplierOrderLogistics2.getLogisticsCorporation());
                                    deliverPackageForm.setWaybillNumber(supplierOrderLogistics2.getWaybillNumber());
                                    deliverPackageFormList.add(deliverPackageForm);
                                }
                            }
                        }
                    }
                    if (sb.length() > 0) {
                        orderItem.setSupplierOrderCode(sb.substring(0, sb.length() - 1));
                    }
                    if (StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode(), orderItem.getSupplierOrderStatus()) ||
                            StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus()) ||
                            StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), orderItem.getSupplierOrderStatus())) {
                        orderItem.setDeliverNum(null);
                    } else {
                        orderItem.setDeliverNum(deliverNum);
                    }
                    if (supplierSkus.size() > 0) {
                        Example example3 = new Example(ExternalItemSku.class);
                        Example.Criteria criteria3 = example3.createCriteria();
                        criteria3.andIn("supplierSkuCode", supplierSkus);
                        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example3);
                        for (DeliverPackageForm deliverPackageForm : deliverPackageFormList) {
                            for (ExternalItemSku externalItemSku : externalItemSkuList) {
                                if (StringUtils.equals(deliverPackageForm.getSkuCode(), externalItemSku.getSupplierSkuCode())) {
                                    deliverPackageForm.setSkuCode(externalItemSku.getSkuCode());
                                }
                            }
                        }
                    }
                    orderItem.setDeliverPackageFormList(deliverPackageFormList);
                }
            }
        }
        Example example = new Example(SupplierOrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.selectByExample(example);
        if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
            for (OrderItem orderItem : orderItemList) {
                if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderItem.getItemType())) {
                    StringBuilder sb = new StringBuilder();//供应商订单编码
                    for (SupplierOrderInfo SupplierOrderInfo : supplierOrderInfoList) {
                        List<SkuInfo> skuInfoList = JSONArray.parseArray(SupplierOrderInfo.getSkus(), SkuInfo.class);
                        for (SkuInfo skuInfo : skuInfoList) {
                            if (StringUtils.equals(orderItem.getSupplierSkuCode(), skuInfo.getSkuCode())) {
                                if (StringUtils.isBlank(orderItem.getSupplierOrderCode())) {
                                    if (!StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus())) {
                                        sb.append(SupplierOrderInfo.getSupplierOrderCode()).append(SupplyConstants.Symbol.COMMA);
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus())) {
                        orderItem.setSupplierOrderCode(null);
                    } else {
                        if (sb.length() > 0) {
                            orderItem.setSupplierOrderCode(sb.substring(0, sb.length() - 1));
                        }
                    }
                }
            }
        }
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
        //设置仓库订单支付时间、物流信息、下单失败原因
        for(WarehouseOrder warehouseOrder: page.getResult()){
            //计算是否显示"取消关闭"操作
            computeShowCancel(warehouseOrder);

            for(PlatformOrder platformOrder: platformOrders){
                if(StringUtils.equals(warehouseOrder.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                    warehouseOrder.setPayTime(platformOrder.getPayTime());
                }
            }
            setLogisticsInfo(warehouseOrder);
            setOrderSubmitFialureMsg(warehouseOrder);
        }
    }

    /**
     * 计算是否显示"取消关闭"操作
     * @param warehouseOrder
     */
    private void computeShowCancel(WarehouseOrder warehouseOrder){
        if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), warehouseOrder.getIsCancel())){//手工取消订单
            if(null != warehouseOrder.getHandCancelTime()){
                int days = DateUtils.getDaysBetween(warehouseOrder.getHandCancelTime(), Calendar.getInstance().getTime());
                if(days <= 7){
                    warehouseOrder.setShowCancel(ZeroToNineEnum.ONE.getCode());
                }else {
                    warehouseOrder.setShowCancel(ZeroToNineEnum.ZERO.getCode());
                }
            }else{
                if(null == warehouseOrder.getPayTime()){
                    warehouseOrder.setShowCancel(ZeroToNineEnum.ZERO.getCode());
                }else{
                    int days = DateUtils.getDaysBetween(warehouseOrder.getPayTime(), Calendar.getInstance().getTime());
                    if(days <= 7){
                        warehouseOrder.setShowCancel(ZeroToNineEnum.ONE.getCode());
                    }else {
                        warehouseOrder.setShowCancel(ZeroToNineEnum.ZERO.getCode());
                    }
                }
            }
        }else{
            warehouseOrder.setShowCancel(ZeroToNineEnum.ZERO.getCode());
        }
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
        warehouseOrder.setLogisticsInfo(sb.toString());
    }

    /**
     * 设置供应商订单下单失败原因
     * @param warehouseOrder
     */
    private void setOrderSubmitFialureMsg(WarehouseOrder warehouseOrder){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), warehouseOrder.getSupplierOrderStatus())){
            StringBuilder sb = new StringBuilder();
            for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            	List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderInfo2.getSkus(), SkuInfo.class);
                
                if(!StringUtils.equals(ResponseAck.SUCCESS_CODE, supplierOrderInfo2.getStatus())){
                    if(StringUtils.isNotBlank(supplierOrderInfo2.getMessage())){
                        if (skuInfoList != null && !skuInfoList.isEmpty()) {
                        	// eg: 001:供应商平台已取消订单
                        	for (SkuInfo sku : skuInfoList) {
//                        		sb.append(sku.getSkuCode()).append(":").append(SUPPLIER_PLATFORM_CANCEL_ORDER)
//                        			.append(HTML_BR);
                        		sb.append(sku.getSkuCode()).append(":")
                        			.append(supplierOrderInfo2.getMessage()).append(SupplyConstants.Symbol.SEMICOLON).append(HTML_BR);
                        	}
                        }
                    }
                }else {
                    if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), supplierOrderInfo2.getSupplierOrderStatus())){
                        if (skuInfoList != null && !skuInfoList.isEmpty()) {
                        	// eg: 001:供应商平台已取消订单
                        	for (SkuInfo sku : skuInfoList) {
//                        		sb.append(sku.getSkuCode()).append(":").append(SUPPLIER_PLATFORM_CANCEL_ORDER)
//                        			.append(HTML_BR);
                        		sb.append(sku.getSkuCode()).append(":")
                        			.append(SupplierOrderStatusEnum.ORDER_CANCEL.getName()).append(SupplyConstants.Symbol.SEMICOLON).append(HTML_BR);
                        	}
                        }
                    }
                }
            }
            if(sb.length() > 0)
                warehouseOrder.setMessage(sb.toString());
            else{
                warehouseOrder.setMessage(StringUtils.EMPTY);
            }
        }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode(), warehouseOrder.getSupplierOrderStatus())){
            warehouseOrder.setMessage(getOrderExceptionMessage2(supplierOrderInfoList));
        }
    }


    @Override
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck<Map<String, Object>> reciveChannelOrder(String orderInfo) throws Exception {
        AssertUtil.notBlank(orderInfo, "渠道同步订单给供应链订单信息参数不能为空");
        JSONObject orderObj = getChannelOrder(orderInfo);
        //订单检查
        orderCheck(orderObj);
        //获取平台订单信息
        PlatformOrder platformOrder = getPlatformOrder(orderObj);
        JSONArray shopOrderArray = getShopOrdersArray(orderObj);
        //获取店铺订单
        List<ShopOrder> shopOrderList = getShopOrderList(shopOrderArray, platformOrder.getPlatformType(), platformOrder.getPayTime());
        Map<String, Object> map = processOrder(platformOrder, shopOrderList, ZeroToNineEnum.ZERO.getCode(), null,"");
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "接收订单成功", map);
    }

    /**
     * 订单处理
     * @param platformOrder
     * @param shopOrderList
     * @param orderType 订单类型:0-接收,1-导入
     * @param operator 操作人
     * @return
     * @throws Exception
     */
    private Map<String, Object> processOrder(PlatformOrder platformOrder, List<ShopOrder> shopOrderList, String orderType,
                                             List<ImportOrderInfo> importOrderInfoList, String operator) throws Exception {
        //设置企业购商品状态
        boolean businessPurchaseFlag = setBusinessPurchaseItemStatus(shopOrderList);
        //拆分自采和代发商品
        List<OrderItem> tmpOrderItemList = new ArrayList<>();//全部商品
        for(ShopOrder shopOrder: shopOrderList){
            for (OrderItem orderItem : shopOrder.getOrderItems()) {
                tmpOrderItemList.add(orderItem);
            }
        }
        List<SellChannel> sellChannelList = null;
        //校验订单金额
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), channelOrderMoneyCheck)){
            sellChannelList = orderMoneyCheck(platformOrder, shopOrderList, tmpOrderItemList);
        }
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderType)) {//接收订单
            //保存幂等流水
            saveIdempotentFlow(shopOrderList, importOrderInfoList, orderType);
        }
        //校验商品是否从供应链新增
        //isScmItems(tmpOrderItemList);
        //设置门店订单状态
        List<WarehouseInfo> storeWarehouseInfoList = setStoreOrderStatus(shopOrderList, sellChannelList, importOrderInfoList, orderType);
        if(shopOrderList.size() == 0){
            return getEmptyOrderReturnMap(new HashMap<>());
        }
        List<OrderItem> allSelfPurcharseOrderItemList = new ArrayList<>();//所有自采商品
        List<OrderItem> selfPurcharseOrderItemList = new ArrayList<>();//自采商品
        List<String> skuCodes = new ArrayList<>();
        List<String> _skuCodes = new ArrayList<>();
        List<OrderItem> supplierOrderItemList = new ArrayList<>();//一件代发
        for(ShopOrder shopOrder: shopOrderList){
            for (OrderItem orderItem : shopOrder.getOrderItems()) {
                tmpOrderItemList.add(orderItem);
                if (orderItem.getSkuCode().startsWith(SP0)) {
                    allSelfPurcharseOrderItemList.add(orderItem);
                    if(!StringUtils.equals(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode(), orderItem.getSupplierOrderStatus())){
                        selfPurcharseOrderItemList.add(orderItem);
                        skuCodes.add(orderItem.getSkuCode());
                        if(IsStoreOrderEnum.NOT_STORE_ORDER.getCode().intValue() == orderItem.getIsStoreOrder().intValue()){//非门店订单
                            _skuCodes.add(orderItem.getSkuCode());
                        }
                    }
                }
                if (orderItem.getSkuCode().startsWith(SP1)) {
                    supplierOrderItemList.add(orderItem);
                }
            }
        }
        List<ExternalItemSku> externalItemSkuList = null;
        if(supplierOrderItemList.size() > 0){
            //校验代发商品
            externalItemSkuList = checkSupplierItems(supplierOrderItemList);
        }
        //自采商品处理
        List<SkuStock> skuStockList = new ArrayList<>();
        Map<String, List<SkuWarehouseDO>> skuWarehouseMap = null;//sku和仓库可用库存关系,一个sku对应多个仓库可用库存
        //校验失败的商品
        List<ExceptionOrderItem> exceptionOrderItemList = new ArrayList<>();
        if(allSelfPurcharseOrderItemList.size() > 0){
            //设置自采商品spu编码
            setSelfPurcharesSpuInfo(shopOrderList, allSelfPurcharseOrderItemList, selfPurcharseOrderItemList);
        }
        if(selfPurcharseOrderItemList.size() > 0){
            //获取自采商品仓库库存
            List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = new ArrayList<>();
            List<WarehouseInfo> warehouseInfoList = warehouseExtService.getWarehouseInfo();
            List<String> warehouseInfoIds = new ArrayList<>();
            for(WarehouseInfo warehouseInfo2: warehouseInfoList){
                warehouseInfoIds.add(warehouseInfo2.getId().toString());
            }
            List<WarehouseItemInfo> warehouseItemInfoList = warehouseExtService.getWarehouseItemInfo(skuCodes, warehouseInfoIds);
            //校验订单产品仓库绑定信息
            checkStoreItemsWarehouseInfo(shopOrderList, importOrderInfoList, warehouseItemInfoList, storeWarehouseInfoList, exceptionOrderItemList, sellChannelList, orderType);
            if(shopOrderList.size() == 0){
                return getEmptyOrderReturnMap(new HashMap<>());
            }
            if(_skuCodes.size() > 0){
                //查询仓库库存
                if(!CollectionUtils.isEmpty(warehouseItemInfoList)){
                    List<WarehouseItemInfo> _warehouseItemInfoList = new ArrayList<>();
                    for(String skuCode: _skuCodes){
                        for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                            if(StringUtils.equals(skuCode, warehouseItemInfo.getSkuCode())){
                                _warehouseItemInfoList.add(warehouseItemInfo);
                            }
                        }
                    }
                    scmInventoryQueryResponseList = warehouseExtService.getWarehouseInventory(warehouseInfoList, _warehouseItemInfoList, JingdongInventoryTypeEnum.SALE.getCode());
                }
            }
            //获取自采商品本地库存
            skuStockList = getSelfItemsLocalStock(selfPurcharseOrderItemList);
            //查询仓库匹配优先级
            List<WarehousePriority> warehousePriorityList = getWarehousePriority();
            //校验自采商品的可用库存
            Map<String, Object> map = checkSelfItemAvailableInventory(selfPurcharseOrderItemList, skuStockList, scmInventoryQueryResponseList, warehousePriorityList, storeWarehouseInfoList);
            List<OrderItem> checkFailureSelfPurcharseItems = (List<OrderItem>)map.get("checkFailureItems");
            skuWarehouseMap = (Map<String, List<SkuWarehouseDO>>)map.get("warehouseSkuMap");
            if(!CollectionUtils.isEmpty(checkFailureSelfPurcharseItems)){
                for(OrderItem orderItem : checkFailureSelfPurcharseItems) {
                    ExceptionOrderItem exceptionOrderItem = getExceptionOrderItem(orderItem, SupplyConstants.ExceptionOrder.ALL_WAREHOUSE_STOCK_LESS, exceptionOrderItemList);
                    //设置订单商品状态跟异常单状态一致
                    orderItem.setSupplierOrderStatus(exceptionOrderItem.getStatus().toString());
                }
            }
        }
        boolean selfSkuAllException = false;//自采sku是否全部异常
        if(selfPurcharseOrderItemList.size() > 0 && selfPurcharseOrderItemList.size() == exceptionOrderItemList.size()){
            selfSkuAllException = true;
        }
        //过滤库存校验失败的导入订单
        filterLessStockOrder(orderType, shopOrderList, importOrderInfoList);
        //保存幂等流水
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)) {//导入订单
            saveIdempotentFlow(shopOrderList, importOrderInfoList, orderType);
        }
        if(shopOrderList.size() == 0){
            return getEmptyOrderReturnMap(new HashMap<>());
        }

        //拆分仓库订单
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (ShopOrder shopOrder : shopOrderList) {
            //分离一件代发和自采商品
            List<OrderItem> orderItemList1 = new ArrayList<>();//自采商品
            List<OrderItem> busiPurchaseOrderItemList = new ArrayList<>();//企业购自采商品
            for(OrderItem _orderItem: shopOrder.getOrderItems()){
                if(StringUtils.equals(_orderItem.getSupplierOrderStatus(), OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode())){
                    busiPurchaseOrderItemList.add(_orderItem);
                }else {
                    for(OrderItem orderItem: selfPurcharseOrderItemList){
                        if(StringUtils.equals(_orderItem.getScmShopOrderCode(), orderItem.getScmShopOrderCode()) &&
                                StringUtils.equals(_orderItem.getSkuCode(), orderItem.getSkuCode())){
                            orderItemList1.add(_orderItem);
                        }
                    }
                }
            }
            List<OrderItem> orderItemList2 = new ArrayList<>();//一件代发
            for(OrderItem _orderItem: shopOrder.getOrderItems()){
                for(OrderItem orderItem: supplierOrderItemList){
                    if(StringUtils.equals(_orderItem.getScmShopOrderCode(), orderItem.getScmShopOrderCode()) &&
                            StringUtils.equals(_orderItem.getSkuCode(), orderItem.getSkuCode())){
                        orderItemList2.add(_orderItem);
                    }
                }
            }

            if(orderItemList1.size() > 0){
                warehouseOrderList.addAll(dealSelfPurcharseOrder(orderItemList1, shopOrder, skuStockList, skuWarehouseMap, storeWarehouseInfoList));
            }
            if(orderItemList2.size() > 0){
                warehouseOrderList.addAll(dealSupplierOrder(orderItemList2, shopOrder));
            }
            List<OrderItem> _orderItemList = new ArrayList<>(orderItemList1);
            _orderItemList.addAll(busiPurchaseOrderItemList);
            _orderItemList.addAll(orderItemList2);
            shopOrder.setOrderItems(_orderItemList);
        }

        //订单商品明细
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (WarehouseOrder warehouseOrder : warehouseOrderList) {
            orderItemList.addAll(warehouseOrder.getOrderItemList());
        }
        //保存异常单信息
        if(exceptionOrderItemList.size() > 0){
            saveExceptionOrder(platformOrder, shopOrderList, exceptionOrderItemList);
        }
        //保存商品明细
        List<OrderItem> itemList = new ArrayList<>();//一件代发
        for(ShopOrder shopOrder: shopOrderList){
            itemList.addAll(shopOrder.getOrderItems());
        }
        //设置代发商品供货价
        setOrderItemSupplyPrice(itemList, externalItemSkuList);

        orderItemService.insertList(itemList);
        //保存仓库订单
        if(warehouseOrderList.size() > 0){
            warehouseOrderService.insertList(warehouseOrderList);
        }
        //保存商铺订单
        shopOrderService.insertList(shopOrderList);
        //保存平台订单
        platformOrderService.insert(platformOrder);
        //如果存在企业购的，那么需要更新订单状态
        if(businessPurchaseFlag){
            for(ShopOrder shopOrder: shopOrderList){
                boolean ls = false;
                for(OrderItem orderItem: shopOrder.getOrderItems()){
                    if(StringUtils.equals(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode(), orderItem.getSupplierOrderStatus())){
                        ls = true;
                        break;
                    }
                }
                if(ls){
                    //更新店铺订单供应商订单状态
                    updateShopOrderSupplierOrderStatus(shopOrder.getScmShopOrderCode(), shopOrder.getShopOrderCode());
                }
            }
            //企业购订单下单结果通知渠道
            purchaseOrderNotifyChannelSubmitOrderResult(shopOrderList);
        }
        //创建订单日志
        createOrderLog(warehouseOrderList);
        //创建店铺订单日志
        createShopOrderLog(shopOrderList, orderType, operator);

        if(!CollectionUtils.isEmpty(skuWarehouseMap)){
            //更新订单商品占用库存
            frozenOrderInventory(skuWarehouseMap);
        }
        /**
         * 自采商品存在异常订单的时候，这里需要更新店铺状态信息
         **/
        if (exceptionOrderItemList.size() > 0) {
            for (ShopOrder shopOrder: shopOrderList) {
                List<OrderItem> shopItemList = shopOrder.getOrderItems();
                for (OrderItem shopItem : shopItemList) {
                    boolean flag = false;
                    for (ExceptionOrderItem exItem : exceptionOrderItemList) {
                        if (StringUtils.equals(exItem.getSkuCode(), shopItem.getSkuCode()) &&
                                StringUtils.equals(exItem.getScmShopOrderCode(), shopItem.getScmShopOrderCode())) {
                            updateShopOrderSupplierOrderStatus(shopOrder.getScmShopOrderCode(), shopOrder.getShopOrderCode());
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        break;
                    }
                }
            }
            Iterator<WarehouseOrder> it = warehouseOrderList.iterator();
            while (it.hasNext()){
                WarehouseOrder warehouseOrder = it.next();
                for (OrderItem shopItem : warehouseOrder.getOrderItemList()) {
                    boolean flag = false;
                    for (ExceptionOrderItem exItem : exceptionOrderItemList) {
                        if (StringUtils.equals(exItem.getSkuCode(), shopItem.getSkuCode()) &&
                                StringUtils.equals(exItem.getScmShopOrderCode(), shopItem.getScmShopOrderCode())) {
                            updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), false);
                            it.remove();
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        break;
                    }
                }
            }
        }
        //如果自采sku全部异常，那么直接通知渠道自采sku下单失败
        if(selfSkuAllException){
            ExceptionOrder exceptionOrder = new ExceptionOrder();
            exceptionOrder.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
            List<ExceptionOrder> exceptionOrderList = exceptionOrderService.select(exceptionOrder);
            for(ExceptionOrder exceptionOrder2: exceptionOrderList){
                notifyChannelSubmitOrderResult(exceptionOrder2);
            }
        }
        Map<String, Object> map = new HashedMap();
        map.put("warehouseOrderList", warehouseOrderList);
        map.put("skuWarehouseMap", skuWarehouseMap);
        return map;
    }

    /**
     * 获取空的订单返回结果
     * @param skuWarehouseMap
     * @return
     */
    private Map<String, Object> getEmptyOrderReturnMap(Map<String, List<SkuWarehouseDO>> skuWarehouseMap){
        Map<String, Object> map = new HashedMap();
        map.put("warehouseOrderList", new ArrayList());
        map.put("skuWarehouseMap", skuWarehouseMap);
        return map;
    }


    /**
     * 校验门店订单产品仓库绑定信息
     * @param shopOrderList
     * @param importOrderInfoList
     * @param warehouseItemInfoList
     * @param storeWarehouseInfoList
     * @param exceptionOrderItemList
     * @param orderType
     */
    private void checkStoreItemsWarehouseInfo(List<ShopOrder> shopOrderList, List<ImportOrderInfo> importOrderInfoList, List<WarehouseItemInfo> warehouseItemInfoList,
                                           List<WarehouseInfo> storeWarehouseInfoList, List<ExceptionOrderItem> exceptionOrderItemList, List<SellChannel> sellChannelList, String orderType){
        List<ShopOrder> failShopOrderList = new ArrayList<>();
        List<OrderItem> failOrderItems = new ArrayList<>();
        Iterator<ShopOrder> it = shopOrderList.iterator();
        Map<String, WarehouseInfo> sellChannelWarehouseMap = new HashedMap();//销售渠道对应的仓库
        while(it.hasNext()){
            ShopOrder shopOrder = it.next();
            boolean _flag = true;
            Iterator<OrderItem> orderItems = shopOrder.getOrderItems().iterator();
            while (orderItems.hasNext()){
                OrderItem orderItem = orderItems.next();
                if(!orderItem.getSkuCode().startsWith(SP0)){
                    continue;
                }
                boolean flag = false;
                if(shopOrder.getIsStoreOrder()) {//门店订单
                    for(WarehouseInfo warehouseInfo: storeWarehouseInfoList){
                        if(StringUtils.equals(orderItem.getSellCode(), warehouseInfo.getStoreCorrespondChannel())){
                            if(!sellChannelWarehouseMap.containsKey(shopOrder.getSellCode())){
                                sellChannelWarehouseMap.put(shopOrder.getSellCode(), warehouseInfo);
                            }
                            for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                                if(StringUtils.equals(warehouseInfo.getCode(), warehouseItemInfo.getWarehouseCode()) &&
                                        StringUtils.equals(orderItem.getSkuCode(), warehouseItemInfo.getSkuCode())){
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }else {//非门店订单
                    for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                        if(StringUtils.equals(orderItem.getSkuCode(), warehouseItemInfo.getSkuCode())){
                            flag = true;
                            break;
                        }
                    }
                }

                if(!flag){
                    if(_flag){
                        _flag = false;
                    }
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.HANDLERED.getCode());//已了结
                    failShopOrderList.add(shopOrder);
                    failOrderItems.add(orderItem);
                }
            }
            if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)) {//导入订单
                if(!_flag){
                    if(shopOrder.getIsStoreOrder()){
                        it.remove();
                    }
                }
            }
        }

        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderType)){//线上订单
            if(failOrderItems.size() > 0){
                for(OrderItem orderItem: failOrderItems){
                    if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == orderItem.getIsStoreOrder()){
                        SellChannel sellChannel = null;
                        for(SellChannel _sellChannel: sellChannelList){
                            if(StringUtils.equals(orderItem.getSellCode(), _sellChannel.getSellCode())){
                                sellChannel = _sellChannel;
                                break;
                            }
                        }
                        WarehouseInfo warehouseInfo = sellChannelWarehouseMap.get(orderItem.getSellCode());
                        getExceptionOrderItem(orderItem,
                                String.format("门店%s商品%s未在门店仓库%s绑定", sellChannel.getSellName(), orderItem.getSkuCode(), warehouseInfo.getWarehouseName()), exceptionOrderItemList);
                    }
                }
            }
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)){//导入订单
            if(failOrderItems.size() > 0){
                for(ShopOrder fialShopOrder: failShopOrderList){
                    StringBuilder sb = new StringBuilder();
                    List<OrderItem> failShopOrderItems = new ArrayList<>();
                    for(OrderItem orderItem: failOrderItems){
                        if(StringUtils.equals(fialShopOrder.getScmShopOrderCode(), orderItem.getScmShopOrderCode()) &&
                                StringUtils.equals(fialShopOrder.getShopOrderCode(), orderItem.getShopOrderCode())){
                            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                                if(StringUtils.equals(orderItem.getChannelCode(), importOrderInfo.getChannelCode()) &&
                                        StringUtils.equals(orderItem.getSellCode(), importOrderInfo.getSellCode()) &&
                                        StringUtils.equals(orderItem.getShopOrderCode(), importOrderInfo.getShopOrderCode()) &&
                                        StringUtils.equals(orderItem.getSkuCode(), importOrderInfo.getSkuCode())){
                                    importOrderInfo.setFlag(false);
                                    String errorMsg = "";
                                    if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == orderItem.getIsStoreOrder()){//门店订单
                                        SellChannel sellChannel = null;
                                        for(SellChannel _sellChannel: sellChannelList){
                                            if(StringUtils.equals(orderItem.getSellCode(), _sellChannel.getSellCode())){
                                                sellChannel = _sellChannel;
                                                break;
                                            }
                                        }
                                        WarehouseInfo warehouseInfo = sellChannelWarehouseMap.get(orderItem.getSellCode());
                                        errorMsg = String.format("门店%s商品%s未在门店仓库%s绑定", sellChannel.getSellName(), orderItem.getSkuCode(), warehouseInfo.getWarehouseName());
                                    }else{
                                        errorMsg = String.format("商品%s未绑定仓库", importOrderInfo.getSkuCode());
                                    }
                                    setImportOrderErrorMsg(importOrderInfo, errorMsg);
                                    sb.append(importOrderInfo.getSkuCode()).append(SupplyConstants.Symbol.COMMA);
                                    failShopOrderItems.add(orderItem);
                                }
                            }
                        }
                    }
                    for(OrderItem orderItem: fialShopOrder.getOrderItems()){
                        boolean flag = false;
                        for(OrderItem failOrderItem: failShopOrderItems){
                            if(StringUtils.equals(orderItem.getSkuCode(), failOrderItem.getSkuCode())){
                                flag = true;
                                break;
                            }
                        }
                        if(!flag){
                            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                                if(StringUtils.equals(orderItem.getChannelCode(), importOrderInfo.getChannelCode()) &&
                                        StringUtils.equals(orderItem.getSellCode(), importOrderInfo.getSellCode()) &&
                                        StringUtils.equals(orderItem.getShopOrderCode(), importOrderInfo.getShopOrderCode()) &&
                                        StringUtils.equals(orderItem.getSkuCode(), importOrderInfo.getSkuCode())){
                                    importOrderInfo.setFlag(false);
                                    setImportOrderErrorMsg(importOrderInfo, String.format("同一订单中的商品%s未绑定仓库", sb.substring(0, sb.length()-1)));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 处理库存校验失败的订单
     * @param orderType
     * @param shopOrders
     * @param importOrderInfoList
     */
    private void filterLessStockOrder(String orderType, List<ShopOrder> shopOrders, List<ImportOrderInfo> importOrderInfoList){
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderType)){//接收订单
            return;
        }

        Iterator<ShopOrder> it = shopOrders.iterator();
        while (it.hasNext()){
            ShopOrder shopOrder = it.next();
            boolean flag = false;
            for(OrderItem item: shopOrder.getOrderItems()){
                if(StringUtils.equals(OrderItemDeliverStatusEnum.HANDLERED.getCode(), item.getSupplierOrderStatus())){//已了结(库存不足)
                    flag = true;
                    break;
                }
            }
            StringBuilder sb = new StringBuilder();
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                for(OrderItem item: shopOrder.getOrderItems()){
                    if(StringUtils.equals(importOrderInfo.getChannelCode(), item.getChannelCode()) &&
                            StringUtils.equals(importOrderInfo.getSellCode(), item.getSellCode()) &&
                            StringUtils.equals(importOrderInfo.getShopOrderCode(), item.getShopOrderCode()) &&
                            StringUtils.equals(importOrderInfo.getSkuCode(), item.getSkuCode()) &&
                            StringUtils.equals(OrderItemDeliverStatusEnum.HANDLERED.getCode(), item.getSupplierOrderStatus())){
                        importOrderInfo.setFlag(false);
                        setImportOrderErrorMsg(importOrderInfo, "库存不足");
                        sb.append(importOrderInfo.getSkuCode()).append(SupplyConstants.Symbol.COMMA);
                        break;
                    }
                }
            }
            if(flag){
                for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                    for(OrderItem item: shopOrder.getOrderItems()){
                        if(StringUtils.equals(importOrderInfo.getChannelCode(), item.getChannelCode()) &&
                                StringUtils.equals(importOrderInfo.getSellCode(), item.getSellCode()) &&
                                StringUtils.equals(importOrderInfo.getShopOrderCode(), item.getShopOrderCode()) &&
                                StringUtils.equals(importOrderInfo.getSkuCode(), item.getSkuCode()) &&
                                importOrderInfo.getFlag()){
                            importOrderInfo.setFlag(false);
                            setImportOrderErrorMsg(importOrderInfo, String.format("同一订单中的商品%s库存不足", sb.substring(0, sb.length() - 1)));
                            sb.append(importOrderInfo.getSkuCode());
                            break;
                        }
                    }
                }
                it.remove();
            }

        }

    }

    private void createShopOrderLog(List<ShopOrder> shopOrderList, String orderType, String operator){
        String _operator = "";
        LogOperationEnum logOperationEnum= null;
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderType)){//接收订单
            _operator = SYSTEM;
            logOperationEnum = LogOperationEnum.RECEIVE_ORDER;
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)){//导入订单
            _operator = operator;
            logOperationEnum = LogOperationEnum.IMPORT_ORDER;
        }
        for(ShopOrder shopOrder: shopOrderList){
            logInfoService.recordLog(shopOrder,shopOrder.getId().toString(), _operator, logOperationEnum.getMessage(), null,null);
        }
    }

    /**
     * 获取仓库匹配优先级
     * @return
     */
    private List<WarehousePriority> getWarehousePriority(){
        Example example = new Example(WarehousePriority.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        example.orderBy("priority").asc();
        List<WarehousePriority> warehousePriorityList = warehousePriorityService.selectByExample(example);
        if(!CollectionUtils.isEmpty(warehousePriorityList)){
            List<String> warehouseCodes = new ArrayList<>();
            for(WarehousePriority priority: warehousePriorityList){
                warehouseCodes.add(priority.getWarehouseCode());
            }
            Example example2 = new Example(WarehouseInfo.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("code", warehouseCodes);
            List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example2);
            if(!CollectionUtils.isEmpty(warehouseInfoList)){
                for(WarehousePriority priority: warehousePriorityList){
                    for(WarehouseInfo warehouseInfo: warehouseInfoList){
                        if(StringUtils.equals(priority.getWarehouseCode(), warehouseInfo.getCode())){
                            priority.setWarehouseName(warehouseInfo.getWarehouseName());
                            priority.setWmsWarehouseCode(warehouseInfo.getWmsWarehouseCode());
                            break;
                        }
                    }
                }
            }
        }
        return warehousePriorityList;
    }

    /**
     * 设置企业购商品状态
     * @param shopOrderList
     */
    private boolean setBusinessPurchaseItemStatus(List<ShopOrder> shopOrderList){
        boolean flag = false;
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode(BUSINESS_PURCHASE_CHANNEL_CODE);
        systemConfig = systemConfigService.selectOne(systemConfig);
        AssertUtil.notNull(systemConfig, String.format("系统配置表system_config里没有配置企业购业务线编码"));
        for(ShopOrder shopOrder: shopOrderList){
            for (OrderItem orderItem : shopOrder.getOrderItems()) {
                if(StringUtils.equals(systemConfig.getContent(), orderItem.getChannelCode()) && orderItem.getSkuCode().startsWith(SP0)){
                    if(!flag){
                        flag = true;
                    }
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode());
                }
            }
        }
        return flag;
    }

    /***
     * 设置门店订单状态:
     * 若发货通知单“销售渠道”的“销售渠道类型”为“门店”，
     * 则“发货状态”自动置为“全部发货”，“物流信息”自动置为“已自提”
     * @param shopOrderList
     * @param sellChannelList
     */
    private List<WarehouseInfo> setStoreOrderStatus(List<ShopOrder> shopOrderList, List<SellChannel> sellChannelList,
                                                    List<ImportOrderInfo> importOrderInfoList, String orderType){
        if(CollectionUtils.isEmpty(shopOrderList) || CollectionUtils.isEmpty(sellChannelList)){
            return null;
        }
        List<String> storeSellCodes = new ArrayList<>();
        for(ShopOrder shopOrder: shopOrderList){
            for(SellChannel sellChannel: sellChannelList){
                if(StringUtils.equals(shopOrder.getSellCode(), sellChannel.getSellCode())){
                    if(StringUtils.equals(String.valueOf(SellChannelTypeEnum.STORE.getCode()), sellChannel.getSellType())){
                        storeSellCodes.add(sellChannel.getSellCode());
                        boolean flag = false;
                        for(OrderItem orderItem: shopOrder.getOrderItems()){
                            if(orderItem.getSkuCode().startsWith(SP0)){
                                orderItem.setIsStoreOrder(IsStoreOrderEnum.STORE_ORDER.getCode());
                                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode());
                            }else {
                                flag = true;
                            }
                        }
                        if(!flag){
                            shopOrder.setIsStoreOrder(true);
                            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.ALL_DELIVER.getCode());
                        }
                        break;
                    }
                }
            }
        }
        if(storeSellCodes.size() == 0){
            return null;
        }
        //查询门店类型销售渠道对应的仓库
        List<WarehouseInfo> warehouseInfoList = null;
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        String[] operationalTypes = new String[]{OperationalTypeEnum.NORMAL_STORE.getCode(), OperationalTypeEnum.UNMANNED_STORE.getCode()};
        criteria.andIn("operationalType", Arrays.asList(operationalTypes));
        criteria.andIn("storeCorrespondChannel", storeSellCodes);
        warehouseInfoList = warehouseInfoService.selectByExample(example);
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)){//导入订单
            for(String storeSellCode: storeSellCodes){
                boolean flag = false;
                for(WarehouseInfo warehouseInfo: warehouseInfoList){
                    if(StringUtils.equals(storeSellCode, warehouseInfo.getStoreCorrespondChannel())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                        if(StringUtils.equals(storeSellCode, importOrderInfo.getSellCode())){
                            importOrderInfo.setFlag(false);
                            setImportOrderErrorMsg(importOrderInfo, String.format("门店销售渠道%s没有绑定到对应的门店仓库", storeSellCode));
                        }
                    }
                    Iterator<ShopOrder> it = shopOrderList.iterator();
                    while(it.hasNext()){
                        ShopOrder shopOrder = it.next();
                        if(StringUtils.equals(storeSellCode, shopOrder.getSellCode())){
                            it.remove();
                        }
                    }
                }
            }
        }
        return warehouseInfoList;
    }


    /**
     * 设置代发商品供货价
     * @param itemList
     * @param externalItemSkuList
     */
    private void setOrderItemSupplyPrice(List<OrderItem> itemList, List<ExternalItemSku> externalItemSkuList){
        if(!CollectionUtils.isEmpty(externalItemSkuList)){
            for(ExternalItemSku externalItemSku: externalItemSkuList){
                for(OrderItem orderItem: itemList){
                    if(StringUtils.equals(orderItem.getSkuCode(), externalItemSku.getSkuCode())){
                        /**
                         * 设置供货价：
                         * 1、如果代发商品有供货价，那么就用代发商品供货价
                         * 2、如果代发商品没有有供货价，并且代发商品有供应商售价，那么就用代发商品有供应商售价
                         * 3、如果代发商品没有有供货价和供应商售价，那么就用代市场参考价
                         */
                        if(null != externalItemSku.getSupplyPrice() && externalItemSku.getSupplyPrice().longValue() > 0){
                            orderItem.setSupplyPrice(CommonUtil.fenToYuan(externalItemSku.getSupplyPrice()));
                        }else if(null != externalItemSku.getSupplierPrice() && externalItemSku.getSupplierPrice().longValue() > 0){
                            orderItem.setSupplyPrice(CommonUtil.fenToYuan(externalItemSku.getSupplierPrice()));
                        }else if(null != externalItemSku.getMarketReferencePrice() && externalItemSku.getMarketReferencePrice().longValue() > 0){
                            orderItem.setSupplyPrice(CommonUtil.fenToYuan(externalItemSku.getMarketReferencePrice()));
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 设置自采商品spu编码
     * @param selfPurcharseOrderItemList
     */
    private void setSelfPurcharesSpuInfo(List<ShopOrder> shopOrderList, List<OrderItem> allSelfPurcharseOrderItemList, List<OrderItem> selfPurcharseOrderItemList){
        List<String> skuCodes = new ArrayList<>();
        for(OrderItem orderItem: allSelfPurcharseOrderItemList){
            if(orderItem.getSkuCode().startsWith(SP0)){
                skuCodes.add(orderItem.getSkuCode());
            }
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        List<Skus> skusList = skusService.selectByExample(example);
        if(CollectionUtils.isEmpty(skusList)){
            return;
        }
        Set<String> spuCodes = new HashSet<>();
        for(Skus skus: skusList){
            spuCodes.add(skus.getSpuCode());
        }
        Example example2 = new Example(Items.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("spuCode", spuCodes);
        List<Items> itemsList = iItemsService.selectByExample(example2);
        for(OrderItem orderItem: selfPurcharseOrderItemList){
            for(Skus skus: skusList){
                if(StringUtils.equals(orderItem.getSkuCode(), skus.getSkuCode())){
                    orderItem.setSpuCode(skus.getSpuCode());
                    orderItem.setItemName(skus.getSkuName());
                    orderItem.setSpecNatureInfo(skus.getSpecInfo());
                    break;
                }
            }
            for(Items items: itemsList){
                if(StringUtils.equals(orderItem.getSpuCode(), items.getSpuCode())){
                    if(StringUtils.isBlank(orderItem.getPicPath())){
                        if(StringUtils.isNotBlank(items.getMainPicture())){
                            orderItem.setPicPath(quniuPicDomain+"/"+items.getMainPicture());
                        }else{
                            orderItem.setPicPath(orderSkuPictrue);
                        }
                    }
                }
            }
        }
        for(ShopOrder shopOrder: shopOrderList){
            for(OrderItem _orderItem: shopOrder.getOrderItems()){
                for(Skus skus: skusList){
                    if(StringUtils.equals(_orderItem.getSkuCode(), skus.getSkuCode())){
                        _orderItem.setItemName(skus.getSkuName());
                        _orderItem.setSpuCode(skus.getSpuCode());
                        _orderItem.setSpecNatureInfo(skus.getSpecInfo());
                        break;
                    }
                }
                for(Items items: itemsList){
                    if(StringUtils.equals(_orderItem.getSpuCode(), items.getSpuCode())){
                        if(StringUtils.isBlank(_orderItem.getPicPath())){
                            if(StringUtils.isNotBlank(items.getMainPicture())){
                                _orderItem.setPicPath(quniuPicDomain+"/"+items.getMainPicture());
                            }else{
                                _orderItem.setPicPath(orderSkuPictrue);
                            }
                        }
                    }
                }
            }

        }
    }


    /**
     * 获取并校验业务线相关仓储信息
     * @param channelCode
     * @return
     */
    /*private List<WarehouseInfo> getChannelAndCheckWarehouseInfo(String channelCode){
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setChannelCode(channelCode);
        warehouseInfo.setOwnerWarehouseState(OwnerWarehouseStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        AssertUtil.notEmpty(warehouseInfoList, String.format("业务线%s还没有绑定仓库", channelCode));
        return warehouseInfoList;
    }*/



    /**
     * 订单检查
     * @param orderObj
     */
    private void orderCheck(JSONObject orderObj){
        Long operateTime = orderObj.getLong("operateTime");
        String sign = orderObj.getString("sign");
        StringBuilder sb = new StringBuilder();
        sb.append(orderObj.getString("noticeNum")).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
        sb.append(operateTime).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
        PlatformOrder platformOrder = orderObj.getJSONObject("platformOrder").toJavaObject(PlatformOrder.class);
        //sb.append(platformOrder.getChannelCode()).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
        sb.append(platformOrder.getPlatformOrderCode()).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
        JSONArray shopOrders = orderObj.getJSONArray("shopOrders");
        for(Object obj: shopOrders){
            ShopOrder shopOrder = ((JSONObject)obj).getJSONObject("shopOrder").toJavaObject(ShopOrder.class);
            sb.append(shopOrder.getShopOrderCode()).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
        }
        sb.append(StringUtils.isNotBlank(platformOrder.getUserId())? platformOrder.getUserId():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//用户id
        sb.append(StringUtils.isNotBlank(platformOrder.getUserName())? platformOrder.getUserName():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//会员名称
        sb.append(null != platformOrder.getItemNum()? platformOrder.getItemNum():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//商品总数
        sb.append(null != platformOrder.getPayment()? platformOrder.getPayment():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//实付金额
        sb.append(null != platformOrder.getTotalFee()? platformOrder.getTotalFee():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//订单总金额
        sb.append(null != platformOrder.getPostageFee()? platformOrder.getPostageFee():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//邮费
        sb.append(null != platformOrder.getTotalTax()? platformOrder.getTotalTax():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//总税费
        sb.append(StringUtils.isNotBlank(platformOrder.getStatus())? platformOrder.getStatus():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//订单状态
        //sb.append(StringUtils.isNotBlank(platformOrder.getReceiverName())? platformOrder.getReceiverName():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//收货人姓名
        sb.append(StringUtils.isNotBlank(platformOrder.getReceiverIdCard())? platformOrder.getReceiverIdCard():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//收货人身份证
        sb.append(StringUtils.isNotBlank(platformOrder.getReceiverMobile())? platformOrder.getReceiverMobile():StringUtils.EMPTY).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);//收货人手机号码

        String encryptStr = sb.toString();
        if(encryptStr.endsWith(SupplyConstants.Symbol.FULL_PATH_SPLIT)){
            encryptStr = encryptStr.substring(0, encryptStr.length()-1);
        }
        String _sign = SHAEncrypt.SHA256(encryptStr);
        if(!StringUtils.equals(sign, _sign)){
            throw new SignException(ExceptionEnum.SIGN_ERROR, "签名错误");
        }
        Date operateDate = DateUtils.timestampToDate(operateTime);
        Long currentTime = System.currentTimeMillis();
        Long secondDiff = (currentTime - operateDate.getTime())/1000;
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode(ORDER_RECEIVE_INTERVAL);
        systemConfig = systemConfigService.selectOne(systemConfig);
        AssertUtil.notNull(systemConfig, "订单接收时间间隔参数未配置");
        Long orderReceiveInterval = Long.parseLong(systemConfig.getContent());
        if(secondDiff.longValue() >= orderReceiveInterval){
            throw new OrderException(ExceptionEnum.ORDER_NOTIFY_TIME_OUT, String.format("渠道发送订单到供应链超过%s秒,不予接收", orderReceiveInterval));
        }

    }

    /**
     * 订单金额校验
     * @param platformOrder
     * @param shopOrders
     * @param orderItems
     */
    private List<SellChannel> orderMoneyCheck(PlatformOrder platformOrder, List<ShopOrder> shopOrders, List<OrderItem> orderItems){
        //店铺订单业务线及销售渠道校验
        Set<String> channelCodes = new HashSet<>();
        Set<String> sellCodes = new HashSet<>();
        for(ShopOrder shopOrder: shopOrders){
            channelCodes.add(shopOrder.getChannelCode());
            sellCodes.add(shopOrder.getSellCode());
        }
        List<SellChannel> sellChannelList = checkOrderChannel(new ArrayList<String>(channelCodes), new ArrayList<String>(sellCodes));
        //平台订单校验
        platformOrderParamCheck(platformOrder, sellChannelList, orderItems);
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
                    if(null != orderItem.getNum())
                        itemsNum2 = itemsNum2 + orderItem.getNum();
                    if(null != orderItem.getPayment())
                        payment2 = payment2.add(orderItem.getPayment());
                    if(null != orderItem.getTotalFee())
                        totalFee2 = totalFee2.add(orderItem.getTotalFee());
                    if(null != orderItem.getPostDiscount())
                        postFee2 = postFee2.add(orderItem.getPostDiscount());
                    if(null != orderItem.getPriceTax())
                        totalTax2 = totalTax2.add(orderItem.getPriceTax());
                }
            }
            if(null != shopOrder.getItemNum())
                itemsNum = itemsNum + shopOrder.getItemNum();
            if(null != shopOrder.getPayment())
                payment = payment.add(shopOrder.getPayment());
            if(null != shopOrder.getTotalFee())
                totalFee = totalFee.add(shopOrder.getTotalFee());
            if(null != shopOrder.getPostageFee())
                postFee = postFee.add(shopOrder.getPostageFee());
            if(null != shopOrder.getTotalTax())
                totalTax = totalTax.add(shopOrder.getTotalTax());
            if(null != shopOrder.getItemNum())
                AssertUtil.isTrue(shopOrder.getItemNum().compareTo(itemsNum2) == 0, "店铺订单商品数量与该店铺所有商品总数量不相等");
            AssertUtil.isTrue(shopOrder.getPayment().compareTo(payment2) == 0, "店铺订单实付金额与该店铺所有商品实付总金额不相等");
            //AssertUtil.isTrue(shopOrder.getTotalFee().compareTo(totalFee2) == 0, "店铺订单应付总金额与该店铺所有商品应付总金额不相等");
            if(null != shopOrder.getPostageFee())
                AssertUtil.isTrue(shopOrder.getPostageFee().compareTo(postFee2) == 0, "店铺订单邮费金额与该店铺所有商品邮费总金额不相等");
            if(null != shopOrder.getTotalTax())
                AssertUtil.isTrue(shopOrder.getTotalTax().compareTo(totalTax2) == 0, "店铺订单税费金额与该店铺所有商品税费总金额不相等");
        }
        if(null != platformOrder.getItemNum())
            AssertUtil.isTrue(platformOrder.getItemNum().compareTo(itemsNum) == 0, "平台订单商品数量与所有店铺商品总数量不相等");
        AssertUtil.isTrue(platformOrder.getPayment().compareTo(payment) == 0, "平台订单实付金额与所有店铺实付总金额不相等");
        //AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(totalFee) == 0, "平台订单应付总金额与所有店铺应付总金额不相等");
        if(null != platformOrder.getPostageFee())
            AssertUtil.isTrue(platformOrder.getPostageFee().compareTo(postFee) == 0, "平台订单邮费金额与所有店铺邮费总金额不相等");
        if(null != platformOrder.getTotalTax())
            AssertUtil.isTrue(platformOrder.getTotalTax().compareTo(totalTax) == 0, "平台订单税费金额与所有店铺税费总金额不相等");
        return sellChannelList;
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
            supplierOrderReturn.setState(supplierOrderInfo2.getStatus());
            supplierOrderReturn.setMessage(supplierOrderInfo2.getMessage());
            supplierOrderReturn.setSkus(getSupplierOrderReturnSkuInfo(supplierOrderInfo2, orderItemList));
            supplierOrderReturnList.add(supplierOrderReturn);
        }
        channelOrderResponse.setOrder(supplierOrderReturnList);
        //通知渠道订单结果
        noticeChannelOrderResult(channelOrderResponse);
    }

    /**
     * 通知渠道订单下单结果
     * @param exceptionOrder
     * @return
     */
    private void notifyChannelSubmitOrderResult(ExceptionOrder exceptionOrder){
        ChannelOrderResponse channelOrderResponse = new ChannelOrderResponse();
        channelOrderResponse.setPlatformOrderCode(exceptionOrder.getPlatformOrderCode());
        channelOrderResponse.setShopOrderCode(exceptionOrder.getShopOrderCode());
        channelOrderResponse.setOrderType(SupplierOrderTypeEnum.ZC.getCode());//自采订单
        //异常订单明细
        ExceptionOrderItem exceptionOrderItem = new ExceptionOrderItem();
        exceptionOrderItem.setExceptionOrderCode(exceptionOrder.getExceptionOrderCode());
        List<ExceptionOrderItem> exceptionOrderItemList = exceptionOrderItemService.select(exceptionOrderItem);
        AssertUtil.notEmpty(exceptionOrderItemList, String.format("根据异常订单号%s查询异常单明细信息为空", exceptionOrder.getExceptionOrderCode()));
        //设置订单提交返回信息
        List<SupplierOrderReturn> supplierOrderReturnList = new ArrayList<SupplierOrderReturn>();
        SupplierOrderReturn supplierOrderReturn = new SupplierOrderReturn();
        supplierOrderReturn.setState(SELF_PURCHARES_ORDER_STATUS);
        StringBuilder message = new StringBuilder();//异常信息
        List<SkuInfo> skuInfoList = new ArrayList<>();
        for(ExceptionOrderItem orderItem: exceptionOrderItemList){
            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setSkuCode(orderItem.getSkuCode());
            skuInfo.setSkuName(orderItem.getItemName());
            skuInfo.setNum(orderItem.getItemNum());
            skuInfoList.add(skuInfo);
            message.append(orderItem.getSkuCode()).append(":").append(orderItem.getExceptionReason()).append(SupplyConstants.Symbol.COMMA);
        }
        supplierOrderReturn.setSkus(skuInfoList);
        if(message.length() > 0){
            supplierOrderReturn.setMessage(message.substring(0, message.length()-1));
        }
        supplierOrderReturn.setSupplyOrderCode("");
        supplierOrderReturnList.add(supplierOrderReturn);
        channelOrderResponse.setOrder(supplierOrderReturnList);
        //通知渠道订单结果
        noticeChannelOrderResult(channelOrderResponse);
    }


    /**
     * 企业购订单下单结果通知渠道
     * @param shopOrderList
     */
    private void purchaseOrderNotifyChannelSubmitOrderResult(List<ShopOrder> shopOrderList){
        List<ShopOrder> tmpshopOrderList = new ArrayList<>();
        for(ShopOrder shopOrder: shopOrderList){
            List<OrderItem> orderItems = new ArrayList<>();
            for(OrderItem orderItem: shopOrder.getOrderItems()){
                if(StringUtils.equals(orderItem.getSupplierOrderStatus(), OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode())){
                    orderItems.add(orderItem);
                }
            }
            if(orderItems.size() > 0){
                ShopOrder _shopOrder = shopOrder;
                _shopOrder.setOrderItems(orderItems);
                tmpshopOrderList.add(_shopOrder);
            }
        }
        if(tmpshopOrderList.size() == 0){
            return;
        }
        ShopOrder shopOrder = tmpshopOrderList.get(0);
        ChannelOrderResponse orderRes = new ChannelOrderResponse();
        orderRes.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
        orderRes.setShopOrderCode(shopOrder.getShopOrderCode());
        orderRes.setOrderType(SupplierOrderTypeEnum.ZC.getCode());//自采订单
        List<SupplierOrderReturn> orderList = new ArrayList<>();
        for (ShopOrder _shopOrder : tmpshopOrderList) {
            SupplierOrderReturn returnOrder = new SupplierOrderReturn();
            returnOrder.setSupplyOrderCode(_shopOrder.getScmShopOrderCode());
            returnOrder.setState(ResponseAck.SUCCESS_CODE);
            List<SkuInfo> skuInfoList = new ArrayList<>();
            for(OrderItem orderItem: _shopOrder.getOrderItems()){
                SkuInfo skuInfo = new SkuInfo();
                skuInfo.setSkuCode(orderItem.getSkuCode());
                skuInfo.setNum(orderItem.getNum());
                skuInfo.setSkuName(orderItem.getItemName());
                skuInfoList.add(skuInfo);
            }
            returnOrder.setSkus(skuInfoList);
            orderList.add(returnOrder);
        }
        orderRes.setOrder(orderList);
        //通知渠道订单结果
        noticeChannelOrderResult(orderRes);

    }

    /**
     * 通知渠道订单结果
     * @param channelOrderResponse
     */
    private void noticeChannelOrderResult(ChannelOrderResponse channelOrderResponse){
        //设置请求渠道的签名
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), TrcActionTypeEnum.SUBMIT_ORDER_NOTICE);
        BeanUtils.copyProperties(trcParam, channelOrderResponse);
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.CHANNEL_RECEIVE_ORDER_SUBMIT_RESULT.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(JSONObject.toJSONString(channelOrderResponse));
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendOrderSubmitResultNotice(channelOrderResponse);
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("店铺订单号为%s的订单提交结果通知渠道失败,渠道返回错误信息:%s", channelOrderResponse.getShopOrderCode(), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("店铺订单号为%s的订单提交结果通知渠道超时,渠道返回错误信息:%s", channelOrderResponse.getShopOrderCode(), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("店铺订单号为%s的订单提交结果通知渠道成功", channelOrderResponse.getShopOrderCode()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("店铺订单号为%s的订单提交结果通知渠道错误,渠道返回错误信息:%s", channelOrderResponse.getShopOrderCode(), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            log.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
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
    public ResponseAck<LogisticNoticeForm2> getJDLogistics(String channelCode, String shopOrderCode)throws Exception {
        ResponseAck<LogisticNoticeForm2> responseAck = null;
        try{
            AssertUtil.notBlank(channelCode, "查询京东物流信息渠道编码channelCode不能为空");
            AssertUtil.notBlank(shopOrderCode, "查询京东物流信息店铺订单号shopOrderCode不能为空");
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setChannelCode(channelCode);
            warehouseOrder.setShopOrderCode(shopOrderCode);
            warehouseOrder.setSupplierCode(SupplyConstants.Order.SUPPLIER_JD_CODE);
            //一个店铺订单下只有一个京东仓库订单
            warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
            if(null == warehouseOrder){
                throw new OrderException(ExceptionEnum.ORDER_QUERY_EXCEPTION, String.format("不存在店铺订单编码为[%s]的京东订单信息", shopOrderCode));
            }
            LogisticForm logisticForm = invokeGetLogisticsInfo(warehouseOrder.getWarehouseOrderCode(), warehouseOrder.getChannelCode(), SupplierLogisticsEnum.JD.getCode());
            LogisticNoticeForm2 logisticNoticeForm = new LogisticNoticeForm2();
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
            responseAck = new ResponseAck(ResponseAck.SUCCESS_CODE, "查询订单配送信息成功", logisticNoticeForm);
        }catch (Exception e){
            String msg = String.format("根据店铺订单号[%s]查询订单物流信息异常,%s", shopOrderCode, e.getMessage());
            log.error(msg,e);
            String code = ExceptionUtil.getErrorInfo(e);
            responseAck = new ResponseAck(code, msg, "");
        }
        //保存请求流水
        requestFlowBiz.saveRequestFlow(String.format("{shopOrderCode:%s}", shopOrderCode), RequestFlowConstant.TRC, RequestFlowConstant.GYL, RequestFlowTypeEnum.LY_LOGISTIC_INFO_QUERY, responseAck, RequestFlowConstant.GYL);
        return responseAck;
    }


    @Override
    public void fetchLogisticsInfo() {
        if (!iRealIpService.isRealTimerService()) return;
        Example example = new Example(SupplierOrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("logisticsStatus", WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());
        criteria.andEqualTo("status", ResponseAck.SUCCESS_CODE);
        List<String> supplierOrderStatusList = new ArrayList<String>();
        supplierOrderStatusList.add(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode());
        supplierOrderStatusList.add(SupplierOrderStatusEnum.PARTS_DELIVER.getCode());
        criteria.andIn("supplierOrderStatus", supplierOrderStatusList);
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.selectByExample(example);
        //处理订单物流信息
        if(CollectionUtils.isEmpty(supplierOrderInfoList)){
            return;
        }
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            try{
                handlerOrderLogisticsInfo(supplierOrderInfo);
            }catch (Exception e){
                log.error(String.format("更新供应商订单%s物流信息异常,%s", JSONObject.toJSON(supplierOrderInfo), e.getMessage()), e);
            }
        }
    }

    /**
     * 处理订单物流信息
     * @param supplierOrderInfo
     */
    public void handlerOrderLogisticsInfo(SupplierOrderInfo supplierOrderInfo){
        supplierOrderInfo = supplierOrderInfoService.selectByPrimaryKey(supplierOrderInfo.getId());
        AssertUtil.notNull(supplierOrderInfo, String.format("定时查询物流信息根据主键ID[%s]查询供应商订单信息为空", supplierOrderInfo.getId()));
        if(StringUtils.equals(SupplierOrderDeliverStatusEnum.COMPLETE.getCode(), supplierOrderInfo.getLogisticsStatus())){//供应商订单已经全部发货
            return;
        }
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("定时查询物流信息根据仓库订单编码[%s]查询仓库订单为空", supplierOrderInfo.getWarehouseOrderCode()));
        String flag = "";
        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, supplierOrderInfo.getSupplierCode()))
            flag = SupplierLogisticsEnum.JD.getCode();
        else if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, supplierOrderInfo.getSupplierCode()))
            flag = SupplierLogisticsEnum.LY.getCode();
        if(StringUtils.isNotBlank(flag)){
            //获取仓库订单编码的物流信息  -----------  一个仓库订单可能会产生多个包裹 -supplier_order_info
            LogisticForm logisticForm = invokeGetLogisticsInfo(supplierOrderInfo.getWarehouseOrderCode(), warehouseOrder.getChannelCode(), flag);
            if(null != logisticForm){
                //更新供应商订单物流信息
                updateSupplierOrderLogistics(supplierOrderInfo, logisticForm);
                if(logisticForm.getLogistics().size() > 0){
                    //清空配送信息
                    for(Logistic logistic: logisticForm.getLogistics()){
                        logistic.setLogisticInfo(new ArrayList<LogisticInfo>());
                    }
                    //物流信息同步给渠道
                    logisticsInfoNoticeChannel(logisticForm);
                }
            }
        }
    }

    @Override
    public AppResult<List<ScmDeliveryOrderCreateResponse>> deliveryOrderCreate(Map<String, OutboundForm> outboundMap, boolean isReCreate) {
        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        //获取采购单相关仓库
        List<WarehouseInfo> warehouseInfoList = getOutboundWarehouseInfo(entries);
        //获取采购单相关商品的仓库对接信息
        List<WarehouseItemInfo> warehouseItemInfoList = getWarehouseItemInfos(entries);
        //京东仓库下单参数
        ScmDeliveryOrderCreateRequest requestJD = new ScmDeliveryOrderCreateRequest();
        requestJD.setWarehouseType(WarehouseTypeEnum.Jingdong.getCode());
        List<ScmDeliveryOrderDO> scmDeliveryOrderDOListJD = new ArrayList<>();
        //自营仓库下单 参数
        ScmDeliveryOrderCreateRequest requestZY = new ScmDeliveryOrderCreateRequest();
        requestZY.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
        List<ScmDeliveryOrderDO> scmDeliveryOrderDOListZY = new ArrayList<>();
        Set<String> channelCodes = new HashSet<>();
        Set<String> sellCodes = new HashSet<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundForm outboundForm = entry.getValue();
            OutboundOrder outboundOrder = outboundForm.getOutboundOrder();
            List<OutboundDetail> outboundDetailList = outboundForm.getOutboundDetailList();
            channelCodes.add(outboundOrder.getChannelCode());
            sellCodes.add(outboundOrder.getSellCode());
            ScmDeliveryOrderDO scmDeliveryOrderDO = getScmDeliveryOrderDO(outboundOrder, warehouseItemInfoList);
            ScmOrderTypeEnum scmOrderType = ScmOrderTypeEnum.NOT_STORE_ORDRE;
            String outboundOrderCode = outboundOrder.getOutboundOrderCode();
            for(WarehouseInfo warehouseInfo: warehouseInfoList){
                if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouseInfo.getCode())){
                    if(StringUtils.equals(WarehouseOperateNatureEnum.OUTER_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//第三方仓库
                        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(),warehouseInfo.getIsThroughWms().toString())){//京东仓储
                            String code = outboundOrderCode;
                            if(isReCreate){
                                code = outboundOrderCode + "_"  + outboundOrder.getNewCode();
                            }
                            scmDeliveryOrderDO.setDeliveryOrderCode(code);//发货单编码
                            scmDeliveryOrderDOListJD.add(scmDeliveryOrderDO);
                        }
                    }else if(StringUtils.equals(WarehouseOperateNatureEnum.SELF_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//自营仓库
                        scmDeliveryOrderDO.setDeliveryOrderCode(outboundOrderCode);//发货单编码
                        scmDeliveryOrderDOListZY.add(scmDeliveryOrderDO);
                    }
                    if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == outboundOrder.getIsStoreOrder().intValue()){//门店订单
                        scmOrderType = ScmOrderTypeEnum.STORE_ORDRE;
                    }
                    break;
                }
            }
            scmDeliveryOrderDO.setScmOrderType(scmOrderType.getCode());
            List<ScmDeliveryOrderItem> scmDeliveryOrderItemList = new ArrayList<>();
            for(OutboundDetail detail: outboundDetailList){
                scmDeliveryOrderItemList.add(getScmDeliveryOrderItem(outboundOrder, detail, warehouseItemInfoList));
            }
            scmDeliveryOrderDO.setScmDeleveryOrderItemList(scmDeliveryOrderItemList);
        }
        //查询采发货单相关业务线
        List<Channel> channelList = getOutboundChannels(channelCodes);
        //查询采发货单相关销售渠道
        List<SellChannel> sellChannelList = getOutboundSellChannes(sellCodes);
        List<ScmDeliveryOrderCreateResponse> responseList = new ArrayList<>();

        if(scmDeliveryOrderDOListJD.size()> 0){
            requestJD.setScmDeleveryOrderDOList(scmDeliveryOrderDOListJD);
            //设置发货单创建参数渠道名称
            setScmDeliveryOrderCreateParamChannelNames(requestJD, channelList, sellChannelList);
            AppResult<List<ScmDeliveryOrderCreateResponse>> appResult = warehouseApiService.deliveryOrderCreate(requestJD);
            this.saveWaybill(appResult);
            responseList.addAll(getDeliveryOrderCreateResult(scmDeliveryOrderDOListJD, appResult));
        }
        if(scmDeliveryOrderDOListZY.size()> 0){
            requestZY.setScmDeleveryOrderDOList(scmDeliveryOrderDOListZY);
            //设置发货单创建参数渠道名称
            setScmDeliveryOrderCreateParamChannelNames(requestZY, channelList, sellChannelList);
            AppResult<List<ScmDeliveryOrderCreateResponse>> appResult = warehouseApiService.deliveryOrderCreate(requestZY);
            responseList.addAll(getDeliveryOrderCreateResult(scmDeliveryOrderDOListZY, appResult));
        }
        return new AppResult<>(ResponseAck.SUCCESS_CODE, "", responseList);
    }

    //保存京东运单号
    private void saveWaybill(AppResult<List<ScmDeliveryOrderCreateResponse>> appResult){
        new Thread(() -> {
            if(StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
                List<ScmDeliveryOrderCreateResponse> result =
                        (List<ScmDeliveryOrderCreateResponse>)appResult.getResult();

                //组装信息
                List<ScmDeliveryOrderDetailRequest> requests = new ArrayList<>();
                for(ScmDeliveryOrderCreateResponse response : result){
                    if(StringUtils.equals(ResponseAck.SUCCESS_CODE, response.getCode())){
                        //组装请求信息
                        ScmDeliveryOrderDetailRequest request = new ScmDeliveryOrderDetailRequest();
                        request.setOrderId(response.getWmsOrderCode());
                        requests.add(request);
                    }
                }

                //请求详情,并回写数据
                this.scmOrderDeliveryOrderDetail(requests);
            }
        }).start();
    }

    //调用获取商品详情接口
    private void scmOrderDeliveryOrderDetail (List<ScmDeliveryOrderDetailRequest> requests){
        try{
            for (ScmDeliveryOrderDetailRequest request : requests) {
                new Thread(() -> {
                    //调用接口
                    AppResult<ScmDeliveryOrderDetailResponse> responseAppResult =
                            warehouseApiService.deliveryOrderDetail(request);

                    //回写数据
                    try {
                        this.updateWaybill(responseAppResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("发货单号:{},物流信息获取异常{}", request.getOrderCode(),e.getMessage());
                    }
                }).start();
            }
        }catch(Exception e){
            log.error("获取商品详情失败", e);
        }
    }

    private void updateWaybill(AppResult<ScmDeliveryOrderDetailResponse> responseAppResult){
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAppResult.getAppcode())){
            ScmDeliveryOrderDetailResponse response = (ScmDeliveryOrderDetailResponse) responseAppResult.getResult();

            String outboundOrderCode = response.getDeliveryOrderCode().substring(0, 19);

            //获取发货单
            OutboundOrder outboundOrder = new OutboundOrder();
            outboundOrder.setWaybillNumber(response.getExpressCode());
            Example example = new Example(OutboundOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("outboundOrderCode", outboundOrderCode);
            outBoundOrderService.updateByExampleSelective(outboundOrder, example);
        }
    }

    /**
     * 查询采发货单相关业务线
     * @param channelCodes
     * @return
     */
    private List<Channel> getOutboundChannels(Set<String> channelCodes){
        Example example = new Example(Channel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", channelCodes);
        return channelService.selectByExample(example);
    }

    /**
     * 查询采发货单相关销售渠道
     * @param sellCodes
     * @return
     */
    private List<SellChannel> getOutboundSellChannes(Set<String> sellCodes){
        Example example = new Example(SellChannel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("sellCode", sellCodes);
        return sellChannelService.selectByExample(example);
    }

    /**
     * 设置发货单创建参数渠道名称
     * @param request
     * @param channelList
     * @param sellChannelList
     */
    private void setScmDeliveryOrderCreateParamChannelNames(ScmDeliveryOrderCreateRequest request, List<Channel> channelList, List<SellChannel> sellChannelList){
        for(ScmDeliveryOrderDO scmDeliveryOrderDO: request.getScmDeleveryOrderDOList()){
            for(Channel channel: channelList){
                if(StringUtils.equals(scmDeliveryOrderDO.getChannelCode(), channel.getCode())){
                    scmDeliveryOrderDO.setChannelName(channel.getName());
                    break;
                }
            }
            for(SellChannel channel: sellChannelList){
                if(StringUtils.equals(scmDeliveryOrderDO.getSellChannelCode(), channel.getSellCode())){
                    scmDeliveryOrderDO.setSellChannelName(channel.getSellName());
                    break;
                }
            }
        }
    }

    private List<ScmDeliveryOrderCreateResponse> getDeliveryOrderCreateResult(List<ScmDeliveryOrderDO> scmDeliveryOrderDOList, AppResult<List<ScmDeliveryOrderCreateResponse>> appResult){
        if(StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            return (List<ScmDeliveryOrderCreateResponse>)appResult.getResult();
        }else{
            List<ScmDeliveryOrderCreateResponse> failDeliveryOrderCreateResponseList = new ArrayList<>();
            for(ScmDeliveryOrderDO scmDeliveryOrderDO: scmDeliveryOrderDOList){
                ScmDeliveryOrderCreateResponse response = new ScmDeliveryOrderCreateResponse();
                response.setCode(appResult.getAppcode());
                response.setDeliveryOrderCode(scmDeliveryOrderDO.getDeliveryOrderCode());
                response.setMessage(appResult.getDatabuffer());
                failDeliveryOrderCreateResponseList.add(response);
            }
            return failDeliveryOrderCreateResponseList;
        }
    }

    /**
     * 获取采购单相关仓库
     * @param entries
     * @return
     */
    private List<WarehouseInfo> getOutboundWarehouseInfo(Set<Map.Entry<String, OutboundForm>> entries){
        Set<String> warehouserIds = new HashSet<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            warehouserIds.add(entry.getValue().getOutboundOrder().getWarehouseCode());
        }
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouserIds);
        return warehouseInfoService.selectByExample(example);
    }

    @Override
    public void outboundOrderSubmitResultNoticeChannel(String shopOrderCode) {
        AssertUtil.notBlank(shopOrderCode, "发货通知单下单结果通知渠道方法参数不能为空");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setShopOrderCode(shopOrderCode);
        List<OutboundOrder> outboundOrderList = outBoundOrderService.select(outboundOrder);
        AssertUtil.notEmpty(outboundOrderList, String.format("根据店铺订单号%s查询发货通知单信息为空", shopOrderCode));
        List<String> outboundOrderCodes = new ArrayList<>();
        for(OutboundOrder _outboundOrder: outboundOrderList){
            outboundOrderCodes.add(_outboundOrder.getOutboundOrderCode());
        }
        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("outboundOrderCode", outboundOrderCodes);
        List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example);
        AssertUtil.notEmpty(outboundDetailList, String.format("根据发货单编码[%s]批量查询发货通知单明细信息为空", StringUtils.join(outboundOrderCodes, SupplyConstants.Symbol.COMMA)));
        //组装通知参数
        ChannelOrderResponse orderRes = new ChannelOrderResponse();
        orderRes.setPlatformOrderCode(outboundOrderList.get(0).getPlatformOrderCode());
        orderRes.setShopOrderCode(shopOrderCode);
        orderRes.setOrderType(SupplierOrderTypeEnum.ZC.getCode());//自采订单
        List<SupplierOrderReturn> orderList = new ArrayList<>();
        for (OutboundOrder order : outboundOrderList) {
            SupplierOrderReturn returnOrder = new SupplierOrderReturn();
            returnOrder.setSupplyOrderCode(order.getOutboundOrderCode());
            returnOrder.setState(getNoticeOrderStatusByOutboundStatus(order.getStatus()));
            List<OutboundDetail> _outboundDetailList = new ArrayList<>();
            for(OutboundDetail detail: outboundDetailList){
                if(StringUtils.equals(detail.getOutboundOrderCode(), order.getOutboundOrderCode())){
                    _outboundDetailList.add(detail);
                }
            }
            returnOrder.setSkus(getSkuInfo(_outboundDetailList));
            orderList.add(returnOrder);
        }
        orderRes.setOrder(orderList);
        //通知渠道订单结果
        noticeChannelOrderResult(orderRes);
    }

    private List<SkuInfo> getSkuInfo(List<OutboundDetail> outboundDetailList){
        List<SkuInfo> skuInfoList = new ArrayList<>();
        for(OutboundDetail detail: outboundDetailList){
            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setSkuCode(detail.getSkuCode());
            skuInfo.setNum(detail.getShouldSentItemNum().intValue());
            skuInfo.setSkuName(detail.getSkuName());
            skuInfoList.add(skuInfo);
        }
        return skuInfoList;
    }

    /**
     * 根据发货单状态获取通知渠道的订单状态
     * @param outboundStatus
     * @return
     */
    private String getNoticeOrderStatusByOutboundStatus(String outboundStatus){
        if(outboundStatus.equals(OutboundOrderStatusEnum.WAITING.getCode()) || outboundStatus.equals(OutboundOrderStatusEnum.ALL_GOODS.getCode())){
            return NoticeChannelStatusEnum.SUCCESS.getCode();
        }else if(outboundStatus.equals(OutboundOrderStatusEnum.CANCELED.getCode()) || outboundStatus.equals(OutboundOrderStatusEnum.ON_CANCELED.getCode())){
            return NoticeChannelStatusEnum.CANCEL.getCode();
        }else{
            return outboundStatus;
        }
    }

    /**
     * 获取采购单相关商品的仓库对接信息
     * @param entries
     * @return
     */
    private List<WarehouseItemInfo> getWarehouseItemInfos(Set<Map.Entry<String, OutboundForm>> entries){
        Set<String> warehoseCodes = new HashSet<>();
        Set<String> skuCodes = new HashSet<>();
        List<String> outboudOrderCodes = new ArrayList<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundForm outboundForm = entry.getValue();
            OutboundOrder outboundOrder = outboundForm.getOutboundOrder();
            warehoseCodes.add(outboundOrder.getWarehouseCode());
            outboudOrderCodes.add(outboundOrder.getOutboundOrderCode());
            List<OutboundDetail> outboundDetailList = outboundForm.getOutboundDetailList();
            for(OutboundDetail detail: outboundDetailList){
                skuCodes.add(detail.getSkuCode());
            }
        }
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseCode", warehoseCodes);
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("isDelete", "0");
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
        AssertUtil.notEmpty(warehouseItemInfoList, String.format("发货单[%s]的相关商品全部不可用", CommonUtil.converCollectionToString(outboudOrderCodes)));
        return warehouseItemInfoList;
    }

    private ScmDeliveryOrderDO getScmDeliveryOrderDO(OutboundOrder outboundOrder, List<WarehouseItemInfo> warehouseItemInfoList){
        ScmDeliveryOrderDO scmDeliveryOrderDO = new ScmDeliveryOrderDO();
        for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouseItemInfo.getWarehouseCode())){
                scmDeliveryOrderDO.setWarehouseCode(warehouseItemInfo.getWmsWarehouseCode());
                break;
            }
        }
        scmDeliveryOrderDO.setOrderType(JdDeliverOrderTypeEnum.B2C.getCode());
        scmDeliveryOrderDO.setOwnerCode(jDWmsConstantConfig.getDeptNo());//货主编码(事业部编码)
        scmDeliveryOrderDO.setShopNo(jDWmsConstantConfig.getShopNo());
        scmDeliveryOrderDO.setShopNick(outboundOrder.getShopName());
        scmDeliveryOrderDO.setShipperNo(jDWmsConstantConfig.getShipperNo());
        scmDeliveryOrderDO.setReciverProvince(outboundOrder.getReceiverProvince());
        scmDeliveryOrderDO.setReciverCity(outboundOrder.getReceiverCity());
        scmDeliveryOrderDO.setReciverCountry(outboundOrder.getReceiverDistrict());
        //scmDeliveryOrderDO.setReciverTown("");//收货人所在镇
        scmDeliveryOrderDO.setReciverDetailAddress(outboundOrder.getReceiverAddress());
        scmDeliveryOrderDO.setReciverName(outboundOrder.getReceiverName());
        scmDeliveryOrderDO.setReciverMobile(outboundOrder.getReceiverPhone());
        //scmDeliveryOrderDO.setInvoiceFlag("");//是否需要发票
        //scmDeliveryOrderDO.setInvoiceType("");//发票类型
        //scmDeliveryOrderDO.setInvoiceTitle("");//发票抬头
        //scmDeliveryOrderDO.setInvoiceAmount(null);//发票金额
        //scmDeliveryOrderDO.setInvoiceState("");//发票标识
        //scmDeliveryOrderDO.setInvoiceContent("");//发票内容
        //scmDeliveryOrderDO.setInvoiceTax("");//购方税号(税务识别号) 
        scmDeliveryOrderDO.setBuyerMessage(outboundOrder.getBuyerMessage());
        //scmDeliveryOrderDO.setSellerMessage("");//
        scmDeliveryOrderDO.setPlaceOrderTime(outboundOrder.getCreateTime());//下单时间
        scmDeliveryOrderDO.setPayTime(outboundOrder.getPayTime());
        scmDeliveryOrderDO.setShopOrderCode(outboundOrder.getShopOrderCode());
        scmDeliveryOrderDO.setChannelCode(outboundOrder.getChannelCode());
        scmDeliveryOrderDO.setSellChannelCode(outboundOrder.getSellCode());

        /**
         * 京东专有参数
         */
        scmDeliveryOrderDO.setIsvSource(jDWmsConstantConfig.getIsvSource());//ISV来源编号
        scmDeliveryOrderDO.setSalePlatformSource(jDWmsConstantConfig.getSalePlatformSource());//销售平台来源
        scmDeliveryOrderDO.setOrderMark(jDWmsConstantConfig.getOrderMark());//订单标记位

        return scmDeliveryOrderDO;
    }

    private ScmDeliveryOrderItem getScmDeliveryOrderItem(OutboundOrder outboundOrder, OutboundDetail outboundDetail, List<WarehouseItemInfo> warehouseItemInfoList){
        ScmDeliveryOrderItem scmDeliveryOrderItem = new ScmDeliveryOrderItem();
        scmDeliveryOrderItem.setItemCode(outboundDetail.getSkuCode());
        scmDeliveryOrderItem.setSkuName(outboundDetail.getSkuName());
        scmDeliveryOrderItem.setSpecInfo(outboundDetail.getSpecNatureInfo());
        String warehouseOwnerId = "";
        for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouseItemInfo.getWarehouseCode()) &&
                    StringUtils.equals(outboundDetail.getSkuCode(), warehouseItemInfo.getSkuCode())){
                scmDeliveryOrderItem.setItemId(warehouseItemInfo.getWarehouseItemId());
                scmDeliveryOrderItem.setBarCode(warehouseItemInfo.getBarCode());
                warehouseOwnerId = warehouseItemInfo.getWarehouseOwnerId();
                break;
            }
        }
        scmDeliveryOrderItem.setOwnerCode(warehouseOwnerId);
        scmDeliveryOrderItem.setPlanQty(outboundDetail.getShouldSentItemNum());
        scmDeliveryOrderItem.setActualPrice(CommonUtil.fenToYuan(outboundDetail.getActualAmount()));
        return scmDeliveryOrderItem;
    }

    @Override
    public Response exportOrder(ShopOrderForm queryModel, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }
        if(StringUtils.isNotBlank(queryModel.getSellCode())){
            criteria.andEqualTo("sellCode", queryModel.getSellCode());
        }
        if (StringUtil.isNotEmpty(queryModel.getShopOrderCode())) {//店铺订单编码
            criteria.andLike("shopOrderCode", "%" + queryModel.getShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getScmShopOrderCode())) {//系统订单号
            criteria.andLike("scmShopOrderCode", "%" + queryModel.getScmShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopName())) {//店铺名称
            criteria.andLike("shopName", "%" + queryModel.getShopName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSupplierOrderStatus())) {//发货状态
            criteria.andEqualTo("supplierOrderStatus", queryModel.getSupplierOrderStatus());
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
        }
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(queryModel, ZeroToNineEnum.ZERO.getCode());
        boolean flag = true;
        if(null != platformOrderList ){
            if(platformOrderList.size() > 0){
                List<String> platformOrderCodeList = new ArrayList<String>();
                for(PlatformOrder platformOrder: platformOrderList){
                    platformOrderCodeList.add(platformOrder.getPlatformOrderCode());
                }
                if(platformOrderCodeList.size() > 0){
                    criteria.andIn("platformOrderCode", platformOrderCodeList);
                }
            }else {
                flag = false;
            }
        }
        example.orderBy("createTime").desc();
        List<ExportOrderDO> exportOrderDOList = new ArrayList<>();
        if(flag){
            Pagenation<ShopOrder> page = new Pagenation<>(100);
            getExportOrderData(page, example, exportOrderDOList);
            if(!CollectionUtils.isEmpty(exportOrderDOList)){
                //设置导出订单业务线和销售渠道名称
                setExportOrderChannelName(exportOrderDOList);
            }
        }
        Map<String, ExcelFieldInfo> fieldMap = new LinkedHashMap<>();
        fieldMap.put("scmShopOrderCode", new ExcelFieldInfo("系统订单号", null));
        fieldMap.put("shopOrderCode", new ExcelFieldInfo("销售渠道订单号", null));
        fieldMap.put("channelName", new ExcelFieldInfo("业务线", null));
        fieldMap.put("sellName", new ExcelFieldInfo("销售渠道", null));
        fieldMap.put("receiverName", new ExcelFieldInfo("收货人", null));
        fieldMap.put("receiverMobile", new ExcelFieldInfo("收货人手机号", null));
        fieldMap.put("receiverArea", new ExcelFieldInfo("收货省市区", null));
        fieldMap.put("receiverAddress", new ExcelFieldInfo("收货详细地址", null));
        fieldMap.put("skuName", new ExcelFieldInfo("SKU名称", null));
        fieldMap.put("skuCode", new ExcelFieldInfo("SKU编号", null));
        fieldMap.put("barCode", new ExcelFieldInfo("条形码", null));
        fieldMap.put("itemNum", new ExcelFieldInfo("交易数量", null));
        fieldMap.put("sendNum", new ExcelFieldInfo("发货数量", null));
        fieldMap.put("logisticsCompany", new ExcelFieldInfo("物流公司", null));
        fieldMap.put("waybill", new ExcelFieldInfo("运单编号", null));
        return exportData(fieldMap, exportOrderDOList, "订单导出_"+ DateUtils.dateToNormalFullString(new Date()));
    }

    /**
     * 设置导出订单业务线和销售渠道名称
     * @param exportOrderDOList
     */
    private void setExportOrderChannelName(List<ExportOrderDO> exportOrderDOList){
        Set<String> channelCodes = new HashSet<>();
        Set<String> sellCodes = new HashSet<>();
        for(ExportOrderDO exportOrderDO: exportOrderDOList){
            channelCodes.add(exportOrderDO.getChannelCode());
            sellCodes.add(exportOrderDO.getSellCode());
        }
        Example channelExample = new Example(Channel.class);
        Example.Criteria channelCriteria = channelExample.createCriteria();
        channelCriteria.andIn("code", channelCodes);
        List<Channel> channelList = channelService.selectByExample(channelExample);
        for(ExportOrderDO exportOrderDO: exportOrderDOList){
            for(Channel channel: channelList){
                if(StringUtils.equals(exportOrderDO.getChannelCode(), channel.getCode())){
                    exportOrderDO.setChannelName(channel.getName());
                    break;
                }
            }
        }
        Example sellChannelExample = new Example(SellChannel.class);
        Example.Criteria sellChannelCriteria = sellChannelExample.createCriteria();
        sellChannelCriteria.andIn("sellCode", sellCodes);
        List<SellChannel> sellChannelList = sellChannelService.selectByExample(sellChannelExample);
        for(ExportOrderDO exportOrderDO: exportOrderDOList){
            for(SellChannel channel: sellChannelList){
                if(StringUtils.equals(exportOrderDO.getSellCode(), channel.getSellCode())){
                    exportOrderDO.setSellName(channel.getSellName());
                    break;
                }
            }
        }
    }


    /**
     * 获取导出订单数据
     * @param page
     * @param example
     * @param exportOrderDOList
     */
    private void getExportOrderData(Pagenation<ShopOrder> page, Example example,List<ExportOrderDO> exportOrderDOList){
        page = shopOrderService.pagination(example, page, new QueryModel());
        //处理导出订单数据
        handerExportOrderData(page.getResult(), exportOrderDOList);
        if(page.getTotalCount() == page.getResult().size()){//只有一页
            return;
        }else{
            int currentCount = (page.getPageNo()-1)*page.getPageSize() + page.getResult().size();
            if(page.getTotalCount() > currentCount){
                page.setPageNo(page.getPageNo()+1);
                page.setStart(currentCount);
                getExportOrderData(page, example, exportOrderDOList);
            }
        }
    }


    /**
     * 处理导出订单数据
     * @param shopOrders
     * @param exportOrderDOList
     */
    private void handerExportOrderData(List<ShopOrder> shopOrders, List<ExportOrderDO> exportOrderDOList){
        List<String> platformOrderCodes = new ArrayList<>();
        List<String> scmShopOrderCodes = new ArrayList<>();
        for(ShopOrder shopOrder: shopOrders){
            platformOrderCodes.add(shopOrder.getPlatformOrderCode());
            scmShopOrderCodes.add(shopOrder.getScmShopOrderCode());
        }
        Example pExample = new Example(PlatformOrder.class);
        Example.Criteria pCriteria = pExample.createCriteria();
        pCriteria.andIn("platformOrderCode", platformOrderCodes);
        List<PlatformOrder> platformOrderList = platformOrderService.selectByExample(pExample);

        Example wExample = new Example(WarehouseOrder.class);
        Example.Criteria wCriteria = wExample.createCriteria();
        wCriteria.andIn("scmShopOrderCode", scmShopOrderCodes);
        List<WarehouseOrder> warehouseOrderList = warehouseOrderService.selectByExample(wExample);

        Example itemExample = new Example(OrderItem.class);
        Example.Criteria itemCriteria = itemExample.createCriteria();
        itemCriteria.andIn("scmShopOrderCode", scmShopOrderCodes);
        List<OrderItem> orderItemList = orderItemService.selectByExample(itemExample);

        Set<String> warehouseOrderCodes = new HashSet<>();
        List<OrderItem> selfItems = new ArrayList<>();//自采商品sku编码
        List<OrderItem> outerItems = new ArrayList<>();//代发商品sku编码
        for(OrderItem orderItem: orderItemList){
            warehouseOrderCodes.add(orderItem.getWarehouseOrderCode());
            if(orderItem.getSkuCode().startsWith(SP0)){
                selfItems.add(orderItem);
            }else if(orderItem.getSkuCode().startsWith(SP1)){
                outerItems.add(orderItem);
            }
        }

        List<SupplierOrderLogistics> supplierOrderLogisticsList = null;
        if(outerItems.size() > 0){
            Example supplierLogisticExample = new Example(SupplierOrderLogistics.class);
            Example.Criteria supplierLogisticCriteria = supplierLogisticExample.createCriteria();
            supplierLogisticCriteria.andIn("warehouseOrderCode", warehouseOrderCodes);
            supplierOrderLogisticsList = supplierOrderLogisticsService.selectByExample(supplierLogisticExample);
        }

        List<OutboundOrder> outboundOrderList = null;
        List<OutboundDetail> outboundDetailList = null;
        List<OutboundDetailLogistics> outboundDetailLogisticsList = null;
        if(selfItems.size() > 0){
            Example outboundExample = new Example(OutboundOrder.class);
            Example.Criteria outboundCriteria = outboundExample.createCriteria();
            outboundCriteria.andIn("scmShopOrderCode", scmShopOrderCodes);
            outboundOrderList = outBoundOrderService.selectByExample(outboundExample);
            List<String> outboundOrderCodes = new ArrayList<>();
            for(OutboundOrder outboundOrder: outboundOrderList){
                outboundOrderCodes.add(outboundOrder.getOutboundOrderCode());
            }
            if(outboundOrderCodes.size() > 0){
                Example outboundDetailExample = new Example(OutboundDetail.class);
                Example.Criteria outboundDetailCriteria = outboundDetailExample.createCriteria();
                outboundDetailCriteria.andIn("outboundOrderCode", outboundOrderCodes);
                outboundDetailList = outboundDetailService.selectByExample(outboundDetailExample);
                List<Long> outboundDetailIds = new ArrayList<>();
                for(OutboundDetail detail: outboundDetailList){
                    outboundDetailIds.add(detail.getId());
                }
                if(outboundDetailIds.size() > 0){
                    Example outboundLogisticsExample = new Example(OutboundDetailLogistics.class);
                    Example.Criteria outboundLogisticsCriteria = outboundLogisticsExample.createCriteria();
                    outboundLogisticsCriteria.andIn("outboundDetailId", outboundDetailIds);
                    outboundDetailLogisticsList = outboundDetailLogisticsService.selectByExample(outboundLogisticsExample);
                }
            }
        }

        for(ShopOrder shopOrder: shopOrders){
            exportOrderDOList.addAll(getExportOrderInfo(shopOrder, platformOrderList, warehouseOrderList, orderItemList,
                    outboundOrderList, outboundDetailList, outboundDetailLogisticsList, supplierOrderLogisticsList));
        }
    }

    private List<ExportOrderDO> getExportOrderInfo(ShopOrder shopOrder, List<PlatformOrder> platformOrderList, List<WarehouseOrder> warehouseOrderList, List<OrderItem> orderItemList, List<OutboundOrder> outboundOrderList,
                                                   List<OutboundDetail> outboundDetailList, List<OutboundDetailLogistics> outboundDetailLogisticsList, List<SupplierOrderLogistics> supplierOrderLogisticsList){
        List<ExportOrderDO> exportOrderList = new ArrayList<>();
        PlatformOrder platformOrder = null;
        for(PlatformOrder platformOrder1: platformOrderList){
            if(StringUtils.equals(shopOrder.getPlatformOrderCode(), platformOrder1.getPlatformOrderCode())){
                platformOrder = platformOrder1;
                break;
            }
        }
        if(null == platformOrder){
            log.error(String.format("店铺订单[%s]对应的品台订单信息为", shopOrder.getScmShopOrderCode()));
            return exportOrderList;
        }
        List<WarehouseOrder> _warehouseOrderList = new ArrayList<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            _warehouseOrderList.add(warehouseOrder);
        }
        List<OrderItem> selfItems = new ArrayList<>();//自采商品sku编码
        List<OrderItem> outerItems = new ArrayList<>();//代发商品sku编码
        for(OrderItem orderItem: orderItemList){
            if(StringUtils.equals(orderItem.getScmShopOrderCode(), shopOrder.getScmShopOrderCode())){
                if(orderItem.getSkuCode().startsWith(SP0)){
                    selfItems.add(orderItem);
                }else if(orderItem.getSkuCode().startsWith(SP1)){
                    outerItems.add(orderItem);
                }
            }
        }

        if(selfItems.size() > 0){
            List<ExportOrderDO> selfExportOrderList = getExportOrderDOList(platformOrder, shopOrder, selfItems);
            //设置导出的门店订单
            setExportOrderStoreOrder(shopOrder, outboundOrderList, outboundDetailList, selfExportOrderList);

            List<ExportOrderDO> finishExportOrderList = new ArrayList<>();//已完成的导出列表
            List<ExportOrderDO> unFinishExportOrderList = new ArrayList<>();//未完成的导出列表
            for(ExportOrderDO orderDO: selfExportOrderList){
                if(orderDO.getIsFinish()){
                    finishExportOrderList.add(orderDO);
                }else{
                    unFinishExportOrderList.add(orderDO);
                }
            }
            if(unFinishExportOrderList.size() > 0){
                //设置自采导出订单
                setSelfExportOrder(shopOrder, unFinishExportOrderList, outboundOrderList, outboundDetailList, outboundDetailLogisticsList);
            }
            exportOrderList.addAll(finishExportOrderList);
            exportOrderList.addAll(unFinishExportOrderList);
        }

        if(outerItems.size() > 0){
            List<ExportOrderDO> outerExportOrderList = getExportOrderDOList(platformOrder, shopOrder, outerItems);
            //设置代发导出订单
            setOuterExportOrder(shopOrder, outerExportOrderList, warehouseOrderList, supplierOrderLogisticsList);
            exportOrderList.addAll(outerExportOrderList);
        }
        return exportOrderList;
    }

    private List<ExportOrderDO> getExportOrderDOList(PlatformOrder platformOrder, ShopOrder shopOrder, List<OrderItem> orderItems){
        List<ExportOrderDO> exportOrderDOList = new ArrayList<>();
        for(OrderItem orderItem: orderItems){
            ExportOrderDO exportOrderDO = new ExportOrderDO();
            exportOrderDO.setItemNum(orderItem.getNum());
            exportOrderDO.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
            exportOrderDO.setShopOrderCode(shopOrder.getShopOrderCode());
            exportOrderDO.setWarehouseOrderCode(orderItem.getWarehouseOrderCode());
            exportOrderDO.setChannelCode(shopOrder.getChannelCode());
            exportOrderDO.setSellCode(shopOrder.getSellCode());
            exportOrderDO.setSkuCode(orderItem.getSkuCode());
            exportOrderDO.setSupplierSkuCode(orderItem.getSupplierSkuCode());
            exportOrderDO.setSkuName(orderItem.getItemName());
            exportOrderDO.setStatus(orderItem.getSupplierOrderStatus());
            exportOrderDO.setBarCode(orderItem.getBarCode());
            exportOrderDO.setReceiverName(platformOrder.getReceiverName());
            exportOrderDO.setReceiverMobile(platformOrder.getReceiverMobile());
            exportOrderDO.setReceiverAddress(platformOrder.getReceiverAddress());
            exportOrderDO.setReceiverArea(platformOrder.getReceiverProvince()+platformOrder.getReceiverCity()+platformOrder.getReceiverDistrict());
            if(StringUtils.equals(OrderItemDeliverStatusEnum.HANDLERED.getCode(), orderItem.getSupplierOrderStatus()) ||
                    StringUtils.equals(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode(), orderItem.getSupplierOrderStatus()) ||
                    StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem.getSupplierOrderStatus())){
                exportOrderDO.setIsFinish(Boolean.TRUE);
            }
            if(StringUtils.equals(OrderItemDeliverStatusEnum.OFF_LINE_DELIVER.getCode(), orderItem.getSupplierOrderStatus())){
                exportOrderDO.setSendNum(exportOrderDO.getItemNum());
                exportOrderDO.setWaybill(SupplyConstants.Symbol.MINUS);
                exportOrderDO.setLogisticsCompany(SupplyConstants.Symbol.MINUS);
            }
            exportOrderDOList.add(exportOrderDO);
        }
        return exportOrderDOList;
    }

    /**
     * 设置导出的门店订单
     * @param shopOrder
     * @param outboundOrderList
     * @param outboundDetailList
     * @param selfExportOrderList
     */
    private void setExportOrderStoreOrder(ShopOrder shopOrder, List<OutboundOrder> outboundOrderList, List<OutboundDetail> outboundDetailList, List<ExportOrderDO> selfExportOrderList){
        for(OutboundOrder outboundOrder: outboundOrderList){
            if(StringUtils.equals(shopOrder.getScmShopOrderCode(), outboundOrder.getScmShopOrderCode())){
                if(Integer.parseInt(ZeroToNineEnum.TWO.getCode()) == outboundOrder.getIsStoreOrder().intValue()){//门店订单
                    for(OutboundDetail detail: outboundDetailList){
                        if(StringUtils.equals(outboundOrder.getOutboundOrderCode(), detail.getOutboundOrderCode())){
                            for(ExportOrderDO exportOrderDO: selfExportOrderList){
                                if(StringUtils.equals(detail.getSkuCode(), exportOrderDO.getSkuCode())){
                                    exportOrderDO.setWaybill(SupplyConstants.Symbol.MINUS);
                                    exportOrderDO.setLogisticsCompany(SupplyConstants.Symbol.MINUS);
                                    exportOrderDO.setSendNum(exportOrderDO.getItemNum());
                                    exportOrderDO.setIsFinish(Boolean.TRUE);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 设置代发导出订单
     * @param shopOrder
     * @param exportOrderList
     * @param warehouseOrderList
     * @param supplierOrderLogisticsList
     */
    private void setOuterExportOrder(ShopOrder shopOrder, List<ExportOrderDO> exportOrderList, List<WarehouseOrder> warehouseOrderList, List<SupplierOrderLogistics> supplierOrderLogisticsList){
        if(CollectionUtils.isEmpty(exportOrderList)){
            return;
        }
        List<WarehouseOrder> _warehouseOrderList = new ArrayList<>();
        List<SupplierOrderLogistics> _supplierOrderLogistics = new ArrayList<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            if(StringUtils.equals(shopOrder.getScmShopOrderCode(), warehouseOrder.getScmShopOrderCode()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
                _warehouseOrderList.add(warehouseOrder);
                for(SupplierOrderLogistics logistics: supplierOrderLogisticsList){
                    if(StringUtils.equals(warehouseOrder.getWarehouseOrderCode(), logistics.getWarehouseOrderCode())){
                        _supplierOrderLogistics.add(logistics);
                    }
                }
            }
        }
        if(_supplierOrderLogistics.size() == 0){
            return;
        }
        Map<String, List<SkuLogisticsInfo>> map = new HashedMap();//sku对应的物流映射
        for(SupplierOrderLogistics logistics: _supplierOrderLogistics){
            List<SkuInfo> skuInfoList = JSONArray.parseArray(logistics.getSkus(), SkuInfo.class);
            for(SkuInfo skuInfo: skuInfoList){
                if(map.containsKey(skuInfo.getSkuCode())){
                    map.get(skuInfo.getSkuCode()).add(new SkuLogisticsInfo(logistics, skuInfo.getNum()));
                }else{
                    List<SkuLogisticsInfo> skuLogisticsInfos = new ArrayList<>();
                    skuLogisticsInfos.add(new SkuLogisticsInfo(logistics, skuInfo.getNum()));
                    map.put(skuInfo.getSkuCode(), skuLogisticsInfos);
                }
            }
        }

        boolean flag = false;
        List<ExportOrderDO> exportOrderDOS = new ArrayList<>();
        for(ExportOrderDO exportOrderDO: exportOrderList){
            List<SkuLogisticsInfo> _tempLogistics = new ArrayList<>();
            for(Map.Entry<String, List<SkuLogisticsInfo>> entry: map.entrySet()){
                if(StringUtils.equals(exportOrderDO.getSupplierSkuCode(), entry.getKey())){
                    for(SkuLogisticsInfo logistics: entry.getValue()){
                        if(StringUtils.equals(exportOrderDO.getWarehouseOrderCode(), logistics.getSupplierOrderLogistics().getWarehouseOrderCode())){
                            _tempLogistics.add(logistics);
                        }
                    }
                }
            }
            if(_tempLogistics.size() > 1){
                flag = true;
                for(int i=1; i<_tempLogistics.size(); i++){
                    ExportOrderDO _exportOrderDO = new ExportOrderDO();
                    BeanUtils.copyProperties(exportOrderDO, _exportOrderDO);
                    _exportOrderDO.setSendNum(_tempLogistics.get(i).getSendNum());
                    _exportOrderDO.setLogisticsCompany(_tempLogistics.get(i).getSupplierOrderLogistics().getLogisticsCorporation());
                    _exportOrderDO.setWaybill(_tempLogistics.get(i).getSupplierOrderLogistics().getWaybillNumber());
                    exportOrderDOS.add(_exportOrderDO);
                }
            }
            if(_tempLogistics.size() > 0){
                exportOrderDO.setSendNum(_tempLogistics.get(0).getSendNum().intValue());
                exportOrderDO.setLogisticsCompany(_tempLogistics.get(0).getSupplierOrderLogistics().getLogisticsCorporation());
                exportOrderDO.setWaybill(_tempLogistics.get(0).getSupplierOrderLogistics().getWaybillNumber());
            }
        }
        if(exportOrderDOS.size() > 0){
            exportOrderList.addAll(exportOrderDOS);
        }
        if(flag){//一个sku多次物流发货，那么需要按照sku排序，将相同sku的数据放一起
            Collections.sort(exportOrderList, new Comparator<ExportOrderDO>() {
                @Override
                public int compare(ExportOrderDO o1, ExportOrderDO o2) {
                    return o1.getSkuCode().equals(o2.getSkuCode())? 0: 1;
                }
            });
        }
    }

    /**
     * 设置自采导出订单
     * @param shopOrder
     * @param exportOrderList
     * @param outboundOrderList
     * @param outboundDetailList
     * @param outboundDetailLogisticsList
     */
    private void setSelfExportOrder(ShopOrder shopOrder, List<ExportOrderDO> exportOrderList, List<OutboundOrder> outboundOrderList, List<OutboundDetail> outboundDetailList, List<OutboundDetailLogistics> outboundDetailLogisticsList){
        if(CollectionUtils.isEmpty(exportOrderList)){
            return;
        }
        List<OutboundOrder> _outboundOrderList = new ArrayList<>();
        List<OutboundDetail> _outboundDetailList2 = new ArrayList<>();
        for(OutboundOrder outboundOrder: outboundOrderList){
            if(StringUtils.equals(shopOrder.getScmShopOrderCode(), outboundOrder.getScmShopOrderCode()) &&
                    (StringUtils.equals(OutboundOrderStatusEnum.PART_OF_SHIPMENT.getCode(), outboundOrder.getStatus()) ||
                    StringUtils.equals(OutboundOrderStatusEnum.ALL_GOODS.getCode(), outboundOrder.getStatus()))){
                List<OutboundDetail> _outboundDetailList = new ArrayList<>();
                for(OutboundDetail detail: outboundDetailList){
                    if(StringUtils.equals(outboundOrder.getOutboundOrderCode(), detail.getOutboundOrderCode())){
                        _outboundDetailList.add(detail);
                    }
                }
                outboundOrder.setOutboundDetailList(_outboundDetailList);
                _outboundDetailList2.addAll(_outboundDetailList);
                _outboundOrderList.add(outboundOrder);
            }
        }
        if(_outboundOrderList.size() == 0){
            return;
        }

        Map<Long, List<OutboundDetailLogistics>> map = new HashedMap();
        for(OutboundDetail detail: _outboundDetailList2){
            List<OutboundDetailLogistics> outboundDetailLogistics = new ArrayList<>();
            for(OutboundDetailLogistics logistics: outboundDetailLogisticsList){
                if(detail.getId().longValue() == logistics.getOutboundDetailId().longValue()){
                    outboundDetailLogistics.add(logistics);
                }
            }
            if(outboundDetailLogistics.size() > 0){
                map.put(detail.getId(), outboundDetailLogistics);
            }
        }

        boolean flag = false;
        for(OutboundOrder outboundOrder: _outboundOrderList){
            for(OutboundDetail detail: outboundOrder.getOutboundDetailList()){
                for(Map.Entry<Long, List<OutboundDetailLogistics>> entry: map.entrySet()){
                    List<OutboundDetailLogistics> outboundLogisticsList = entry.getValue();
                    if(detail.getId().longValue() == entry.getKey().longValue()){
                        List<ExportOrderDO> exportOrderDOS = new ArrayList<>();
                        for(ExportOrderDO exportOrderDO: exportOrderList){
                            if(StringUtils.equals(outboundOrder.getWarehouseOrderCode(), exportOrderDO.getWarehouseOrderCode()) &&
                                    StringUtils.equals(detail.getSkuCode(), exportOrderDO.getSkuCode())){
                                if(outboundLogisticsList.size() > 1){
                                    flag = true;
                                    for(int i=1; i<outboundLogisticsList.size(); i++){
                                        ExportOrderDO _exportOrderDO = new ExportOrderDO();
                                        BeanUtils.copyProperties(exportOrderDO, _exportOrderDO);
                                        _exportOrderDO.setSendNum(outboundLogisticsList.get(i).getItemNum().intValue());
                                        _exportOrderDO.setLogisticsCompany(outboundLogisticsList.get(i).getLogisticsCorporation());
                                        _exportOrderDO.setWaybill(outboundLogisticsList.get(i).getWaybillNumber());
                                        exportOrderDOS.add(_exportOrderDO);
                                    }
                                }
                                if(outboundLogisticsList.size() > 0){
                                    exportOrderDO.setSendNum(outboundLogisticsList.get(0).getItemNum().intValue());
                                    exportOrderDO.setLogisticsCompany(outboundLogisticsList.get(0).getLogisticsCorporation());
                                    exportOrderDO.setWaybill(outboundLogisticsList.get(0).getWaybillNumber());
                                }
                            }
                        }
                        exportOrderList.addAll(exportOrderDOS);
                    }
                }
            }
        }
        if(flag){//一个sku多次物流发货，那么需要按照sku排序，将相同sku的数据放一起
            Collections.sort(exportOrderList, new Comparator<ExportOrderDO>() {
                @Override
                public int compare(ExportOrderDO o1, ExportOrderDO o2) {
                    return o1.getSkuCode().equals(o2.getSkuCode())? 0: 1;
                }
            });
        }
    }


    @Override
    public javax.ws.rs.core.Response exportSupplierOrder(WarehouseOrderForm queryModel, AclUserAccreditInfo aclUserAccreditInfo) {
//        WarehouseOrderForm queryModel = new WarehouseOrderForm();
        try {
            List<WarehouseOrder> warehouseOrderList =new ArrayList<>();
            queryModel.setOrderType(ZeroToNineEnum.ONE.getCode());
            Pagenation<WarehouseOrder> page =new Pagenation<>();
            page.setPageSize(300);
            page=warehouseOrderPage(queryModel, page, aclUserAccreditInfo);
            warehouseOrderList.addAll(page.getResult());
            if (page.getTotalPages()>1){
                for (int i = 2; i <= page.getTotalPages(); i++) {
                    page.setPageNo(i);
                    page=warehouseOrderPage(queryModel, page, aclUserAccreditInfo);
                    warehouseOrderList.addAll(page.getResult());
                }
            }
            for (WarehouseOrder warehouseOrder:warehouseOrderList) {
                String code = warehouseOrder.getSupplierOrderStatus();
                String name = SupplierOrderStatusEnum.getSupplierOrderStatusEnumByCode(code).getName();
                warehouseOrder.setSupplierOrderStatus(name);
                if (warehouseOrder.getLogisticsInfo().indexOf(HTML_BR)!=-1){
                    String logisticsInfo = warehouseOrder.getLogisticsInfo();
                    logisticsInfo=  logisticsInfo.replaceAll(HTML_BR,"  ");
                    warehouseOrder.setLogisticsInfo(logisticsInfo);
                }
            }
            CellDefinition warehouseOrderCode = new CellDefinition("warehouseOrderCode", "供应商订单编号", CellDefinition.TEXT, null, 8000);
            CellDefinition supplierName = new CellDefinition("supplierName", "供应商名称", CellDefinition.TEXT, null,8000);
            CellDefinition shopOrderCode = new CellDefinition("shopOrderCode", "店铺订单号", CellDefinition.TEXT, null,8000);
            CellDefinition itemsNum = new CellDefinition("itemsNum", "商品总数量", CellDefinition.NUM_0, null,8000);
            CellDefinition payment = new CellDefinition("payment", "商品总金额(元)", CellDefinition.NUM_0_00, null,8000);
            CellDefinition payTime = new CellDefinition("payTime", "付款时间", CellDefinition.DATE_TIME, null,8000);
            CellDefinition supplierOrderStatus = new CellDefinition("supplierOrderStatus", "状态", CellDefinition.TEXT, null,8000);
            CellDefinition logisticsInfo = new CellDefinition("logisticsInfo", "反馈物流公司名称-反馈运单号", CellDefinition.TEXT, null,16000);

            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(warehouseOrderCode);
            cellDefinitionList.add(supplierName);
            cellDefinitionList.add(shopOrderCode);
            cellDefinitionList.add(itemsNum);
            cellDefinitionList.add(payment);
            cellDefinitionList.add(payTime);
            cellDefinitionList.add(supplierOrderStatus);
            cellDefinitionList.add(logisticsInfo);

            String sheetName = "供应商订单";
            String fileName = "供应商订单" + (queryModel.getStartDate()==null?"":queryModel.getStartDate()+BAR) + (queryModel.getEndDate()==null?"":queryModel.getEndDate()) + EXCEL;


            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                log.error("文件导出错误",e1);
            }
            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(warehouseOrderList, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);
            return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            log.error("供应商订单导出异常" + e.getMessage(), e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.SUPPLIER_ORDER_EXPORT_EXCEPTION.getCode()), ExceptionEnum.SUPPLIER_ORDER_EXPORT_EXCEPTION.getMessage());
        }
    }

    @Override
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String cancelHandler(SupplierOrderCancelForm form, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户信息不能为空");
        AssertUtil.notBlank(form.getIsCancel(), "供应商订单取消操作是否取消参数isCancel不能为空");
        if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), form.getIsCancel())){//取消操作
            AssertUtil.notBlank(form.getCancelReason(), "供应商订单取消操作时取消原因参数cancelReason不能为空");
        }
        //更新仓库订单状态
        WarehouseOrder warehouseOrder = updateWarehouseOrderByCancel(form);
        //根据取消操作更新供应商订单状态
        updateSupplierOrderInfoByCancel(warehouseOrder);
        //更新订单相关商品供应商订单状态
        updateOrderItemByCancel(warehouseOrder);
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
        //记录操作日志
        String logOperation = LogOperationEnum.ORDER_CLOSE.getMessage();
        if(StringUtils.equals(CancelStatusEnum.CLOASE_CANCEL.getCode(), warehouseOrder.getIsCancel())){//关闭取消操作
            logOperation = LogOperationEnum.ORDER_REOPEN.getMessage();
        }
        logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), aclUserAccreditInfo.getUserId(), logOperation, form.getCancelReason(),null);
        String msg = "取消关闭成功";
        if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), form.getIsCancel())){//取消操作
            msg = "关闭成功";
        }
        return msg;
    }

    @Override
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck<String> supplierCancelOrder(String orderInfo) {
        AssertUtil.notBlank(orderInfo, "供应商取消订单通知接口输入参数不能为空");
        SupplierOrderCancelNotify supplierOrderCancelNotify = JSON.parseObject(orderInfo, SupplierOrderCancelNotify.class);
        List<SupplierOrderInfo> supplierOrderInfoList = new ArrayList<SupplierOrderInfo>();
        for(SupplierOrderCancelInfo supplierOrderCancelInfo: supplierOrderCancelNotify.getOrder()){
            SupplierOrderInfo supplierOrderInfo = null;
            if(StringUtils.equals(supplierOrderCancelNotify.getOrderType(), ZeroToNineEnum.ZERO.getCode())){//京东订单
                supplierOrderInfo = cancelSupplierOrderByNotify(supplierOrderCancelInfo, 1);
            }else if(StringUtils.equals(supplierOrderCancelNotify.getOrderType(), ZeroToNineEnum.ONE.getCode())){//粮油订单
                supplierOrderInfo = cancelSupplierOrderByNotify(supplierOrderCancelInfo, 0);
            }else {
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "供应商渠道订单同步供应链参数订单类型orderType的值错误");
            }
            if(null != supplierOrderInfo){
                supplierOrderInfoList.add(supplierOrderInfo);
            }
        }
        if(supplierOrderInfoList.size() > 0){
            //根据供应商订单取消通知更新订单商品状态
            for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
                List<OrderItem> orderItemList = cancelOrderItemByNotify(supplierOrderInfo);
                //更新仓库订单供应商订单状态
                WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(supplierOrderInfo.getWarehouseOrderCode(), false);
                //更新店铺订单供应商订单状态
                updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
                //记录操作日志
                List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
                StringBuilder sb_remark = new StringBuilder();
                if (skuInfoList != null && !skuInfoList.isEmpty()) {
                	// eg: 001:供应商平台已取消订单
                	for (SkuInfo sku : skuInfoList) {
                		sb_remark.append(sku.getSkuCode()).append(":").append(SUPPLIER_PLATFORM_CANCEL_ORDER)
                			.append(HTML_BR);
                	}
                }
                logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(),
                		LogOperationEnum.ORDER_CANCEL.getMessage(), sb_remark.toString(), null);
                //通知泰然城
                supplierOrderCancelNotifyChannel(warehouseOrder, supplierOrderInfo, orderItemList);
            }
        }
        return new ResponseAck<String>(ResponseAck.SUCCESS_CODE, "订单取消通知接收成功", "");
    }

    /**
     * 供应商取消订单通知渠道
     * @param warehouseOrder
     * @param supplierOrderInfo
     * @param orderItemList
     */
    private void supplierOrderCancelNotifyChannel(WarehouseOrder warehouseOrder, SupplierOrderInfo supplierOrderInfo, List<OrderItem> orderItemList){
        ChannelOrderResponse channelOrderResponse = new ChannelOrderResponse();
        channelOrderResponse.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        channelOrderResponse.setShopOrderCode(warehouseOrder.getShopOrderCode());
        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, warehouseOrder.getSupplierCode()))
            channelOrderResponse.setOrderType(SupplierOrderTypeEnum.JD.getCode());//京东订单
        else if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, warehouseOrder.getSupplierCode()))
            channelOrderResponse.setOrderType(SupplierOrderTypeEnum.LY.getCode());//粮油订单
        else
            throw new OrderException(ExceptionEnum.SUPPLIER_ORDER_NOTIFY_EXCEPTION, String.format("仓库订单%s不是一件代发订单,订单下单结果不需要通知渠道", JSON.toJSONString(warehouseOrder)));
        //设置订单提交返回信息
        List<SupplierOrderReturn> supplierOrderReturnList = new ArrayList<SupplierOrderReturn>();
        SupplierOrderReturn supplierOrderReturn = new SupplierOrderReturn();
        supplierOrderReturn.setSupplyOrderCode(supplierOrderInfo.getSupplierOrderCode());
        supplierOrderReturn.setState(NoticeChannelStatusEnum.CANCEL.getCode());//订单取消
        supplierOrderReturn.setMessage(SUPPLIER_PLATFORM_CANCEL_ORDER);
        supplierOrderReturn.setSkus(getSupplierOrderReturnSkuInfo(supplierOrderInfo, orderItemList));
        supplierOrderReturnList.add(supplierOrderReturn);
        channelOrderResponse.setOrder(supplierOrderReturnList);
        //通知渠道订单结果
        noticeChannelOrderResult(channelOrderResponse);
    }

    /**
     * 根据供应商订单取消通知取消订单
     * @param supplierOrderCancelInfo
     * @param flag 0-粮油订单,1-京东订单
     */
    private SupplierOrderInfo cancelSupplierOrderByNotify(SupplierOrderCancelInfo supplierOrderCancelInfo, int flag){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(supplierOrderCancelInfo.getWarehouseOrderCode());
        String supplierOrderCode = "";
        if(flag == 0){
            supplierOrderCode = supplierOrderCancelInfo.getSupplyOrderCode();
        }else{
            supplierOrderCode = supplierOrderCancelInfo.getSupplierParentOrderCode();
        }
        supplierOrderInfo.setSupplierOrderCode(supplierOrderCode);
        supplierOrderInfo = supplierOrderInfoService.selectOne(supplierOrderInfo);
        AssertUtil.notNull(supplierOrderInfo, String.format("供应商订单号%s不存在", supplierOrderCode));
        if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), supplierOrderInfo.getSupplierOrderStatus()))
            return null;
        supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.ORDER_CANCEL.getCode());
        supplierOrderInfo.setUpdateTime(Calendar.getInstance().getTime());
        supplierOrderInfoService.updateByPrimaryKey(supplierOrderInfo);
        return supplierOrderInfo;
    }

    /**
     * 根据供应商订单取消通知更新订单商品状态
     * @param supplierOrderInfo
     */
    private List<OrderItem> cancelOrderItemByNotify(SupplierOrderInfo supplierOrderInfo){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单号%s查询订单商品明细为空", supplierOrderInfo.getWarehouseOrderCode()));
        List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
        List<OrderItem> updateOrderItemList = new ArrayList<OrderItem>();
        for(SkuInfo skuInfo: skuInfoList){
            for(OrderItem orderItem2: orderItemList){
                if(StringUtils.equals(skuInfo.getSkuCode(), orderItem2.getSupplierSkuCode()) &&
                        !StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem2.getSupplierOrderStatus())){
                    orderItem2.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode());
                    orderItem2.setUpdateTime(Calendar.getInstance().getTime());
                    orderItemService.updateByPrimaryKey(orderItem2);
                    updateOrderItemList.add(orderItem2);
                }
            }
        }
        if(updateOrderItemList.size() > 0){
            return updateOrderItemList;
        }
        return null;
    }

    /**
     * 根据取消操作更新仓库订单状态
     * @param form
     */
    private WarehouseOrder updateWarehouseOrderByCancel(SupplierOrderCancelForm form){
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(form.getWarehouseOrderCode());
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("供应链订单取消操作根据仓库订单编码%s查询仓库订单信息为空", form.getWarehouseOrderCode()));
        if(StringUtils.equals(form.getIsCancel(), warehouseOrder.getIsCancel())){
            if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), warehouseOrder.getIsCancel())){//取消操作
                throw new OrderException(ExceptionEnum.ORDER_IS_CANCEL, "订单已经是取消状态，不能进行取消操作");
            }else if(StringUtils.equals(CancelStatusEnum.CLOASE_CANCEL.getCode(), warehouseOrder.getIsCancel())){//取消操作
                throw new OrderException(ExceptionEnum.ORDER_IS_CLOSE_CANCEL, "订单不是取消状态，不能进行关闭取消操作");
            }
        }else{
            if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), form.getIsCancel()) &&
                    StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), warehouseOrder.getSupplierOrderStatus())){//取消操作
                throw new OrderException(ExceptionEnum.ORDER_IS_CANCEL, "订单已被供应商取消，不能进行取消操作");
            }
            warehouseOrder.setIsCancel(form.getIsCancel());
            if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), warehouseOrder.getIsCancel())){//取消操作
                warehouseOrder.setOldSupplierOrderStatus(warehouseOrder.getSupplierOrderStatus());
                warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.ORDER_CANCEL.getCode());
            }else if(StringUtils.equals(CancelStatusEnum.CLOASE_CANCEL.getCode(), warehouseOrder.getIsCancel())){//关闭取消操作
                warehouseOrder.setSupplierOrderStatus(warehouseOrder.getOldSupplierOrderStatus());
            }
            Date currentDate = Calendar.getInstance().getTime();
            warehouseOrder.setUpdateTime(currentDate);
            warehouseOrder.setHandCancelTime(currentDate);
            warehouseOrderService.updateByPrimaryKey(warehouseOrder);
        }
        return warehouseOrder;
    }

    /**
     * 根据取消操作更新供应商订单状态
     * @param warehouseOrder
     */
    private void updateSupplierOrderInfoByCancel(WarehouseOrder warehouseOrder){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        if(CollectionUtils.isEmpty(supplierOrderInfoList))
            return;
        for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), warehouseOrder.getIsCancel())){//取消操作
                supplierOrderInfo2.setOldSupplierOrderStatus(supplierOrderInfo2.getSupplierOrderStatus());
                supplierOrderInfo2.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode());
            }else if(StringUtils.equals(CancelStatusEnum.CLOASE_CANCEL.getCode(), warehouseOrder.getIsCancel())){//关闭取消操作
                supplierOrderInfo2.setSupplierOrderStatus(supplierOrderInfo2.getOldSupplierOrderStatus());
            }
            supplierOrderInfo2.setUpdateTime(Calendar.getInstance().getTime());
            supplierOrderInfoService.updateByPrimaryKey(supplierOrderInfo2);
        }
    }

    /**
     * 根据取消操作更新商品状态
     * @param warehouseOrder
     */
    private void updateOrderItemByCancel(WarehouseOrder warehouseOrder){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单号%s查询订单相关商品明细信息为空", warehouseOrder.getWarehouseOrderCode()));
        for(OrderItem orderItem2: orderItemList){
            if(StringUtils.equals(CancelStatusEnum.CANCEL.getCode(), warehouseOrder.getIsCancel())){//取消操作
                orderItem2.setOldSupplierOrderStatus(orderItem2.getSupplierOrderStatus());
                orderItem2.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode());
            }else if(StringUtils.equals(CancelStatusEnum.CLOASE_CANCEL.getCode(), warehouseOrder.getIsCancel())){//关闭取消操作
                orderItem2.setSupplierOrderStatus(orderItem2.getOldSupplierOrderStatus());
            }
            orderItem2.setUpdateTime(Calendar.getInstance().getTime());
            orderItemService.updateByPrimaryKey(orderItem2);
        }
    }



    /**
     * 物流信息通知渠道
     * @param logisticForm
     */
    private void logisticsInfoNoticeChannel(LogisticForm logisticForm){
        logisticForm.setType(LogsticsTypeEnum.WAYBILL_NUMBER.getCode());//信息类型:0-物流单号
        RequestFlow requestFlowUpdate = new RequestFlow();
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
            RequestFlow requestFlow = new RequestFlow();
            requestFlow.setRequester(RequestFlowConstant.GYL);
            requestFlow.setResponder(RequestFlowConstant.TRC);
            requestFlow.setType(RequestFlowTypeEnum.SEND_LOGISTICS_INFO_TO_CHANNEL.getCode());
            requestFlow.setRequestTime(Calendar.getInstance().getTime());
            String requestNum = GuidUtil.getNextUid(RequestFlowConstant.TRC);
            requestFlow.setRequestNum(requestNum);
            requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
            //设置请求渠道的签名
            TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), TrcActionTypeEnum.SEND_LOGISTIC);
            BeanUtils.copyProperties(trcParam, logisticNoticeForm);
            requestFlow.setRequestParam(JSONObject.toJSONString(logisticNoticeForm));
            requestFlowService.insert(requestFlow);
            requestFlowUpdate.setRequestNum(requestNum);
            //物流信息同步给渠道
            ToGlyResultDO toGlyResultDO = trcService.sendLogisticInfoNotice(logisticNoticeForm);
            requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
            if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
                log.error(String.format("物流信息%s同步给渠道失败,%s", JSON.toJSONString(logisticNoticeForm), toGlyResultDO.getMsg()));
                requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
            }
            if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
                log.error(String.format("物流信息%s同步给渠道超时,%s", JSON.toJSONString(logisticNoticeForm), toGlyResultDO.getMsg()));
                requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
            }
            if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
                log.error(String.format("物流信息%s同步给渠道成功,%s", JSON.toJSONString(logisticNoticeForm), toGlyResultDO.getMsg()));
                requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
            }
            if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
                log.error(String.format("物流信息%s同步给渠道异常,%s", JSON.toJSONString(logisticNoticeForm), toGlyResultDO.getMsg()));
                requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
            }
            int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
            if (count<=0){
                log.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
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
    void updateSupplierOrderLogistics(SupplierOrderInfo supplierOrderInfo, LogisticForm logisticForm){
        SupplierOrderLogistics _supplierOrderLogistics = new SupplierOrderLogistics();
        _supplierOrderLogistics.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        List<SupplierOrderLogistics> oldSupplierOrderLogisticsList = supplierOrderLogisticsService.select(_supplierOrderLogistics);
        if(!CollectionUtils.isEmpty(oldSupplierOrderLogisticsList)){
            //删除不存在的子订单物流信息(目前主要针对京东子订单二次拆单的情况)
            List<SupplierOrderLogistics> delSupplierOrderLogisticsList = new ArrayList<>();
            for(SupplierOrderLogistics supplierOrderLogistics: oldSupplierOrderLogisticsList){
                boolean flag = false;
                for(Logistic logistic: logisticForm.getLogistics()){
                    if(StringUtils.equals(supplierOrderLogistics.getSupplierOrderCode(), logistic.getSupplierOrderCode())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    delSupplierOrderLogisticsList.add(supplierOrderLogistics);
                }
            }
            if(delSupplierOrderLogisticsList.size() > 0){
                for(SupplierOrderLogistics supplierOrderLogistics: delSupplierOrderLogisticsList){
                    supplierOrderLogisticsService.deleteByPrimaryKey(supplierOrderLogistics.getId());
                }
            }
        }

        //在这里剔除已经发货完成并通知了渠道的物流信息
        List<Logistic> logistics = logisticForm.getLogistics();
        for (Iterator<Logistic> it = logistics.iterator(); it.hasNext();) {
            Logistic logistic = it.next();
            if (StringUtils.equals(supplierOrderInfo.getSupplierOrderCode(),logistic.getSupplierOrderCode()) &&
                    StringUtils.equals(SupplierOrderDeliverStatusEnum.COMPLETE.getCode(),supplierOrderInfo.getLogisticsStatus())) {
                it.remove();
            }
        }


        List<SupplierOrderLogistics> supplierOrderLogisticsList = new ArrayList<SupplierOrderLogistics>();
        boolean flag = false;//用来区分京东订单是否拆成子订单的标记
        //保存物流信息
        for(Logistic logistic: logisticForm.getLogistics()){
            if(StringUtils.equals(supplierOrderInfo.getSupplierOrderCode(), logistic.getSupplierOrderCode())){
                flag = true;
                SupplierOrderLogistics supplierOrderLogistics = null;
                try{
                    //获取供应商订单物流信息
                    supplierOrderLogistics = getSupplierOrderLogistics(supplierOrderInfo, logistic);
                    //保存的物流信息 or 更新物流信息
                    saveSupplierOrderLogistics(supplierOrderLogistics);
                    supplierOrderLogisticsList.add(supplierOrderLogistics);
                }catch (Exception e){
                    log.error(String.format("保存供应商物流信息%s异常,%s", JSONObject.toJSON(supplierOrderLogistics), e.getMessage()), e);
                }
            }
        }
        if(!flag){
            for(Logistic logistic: logisticForm.getLogistics()){
                if(StringUtils.equals(supplierOrderInfo.getSupplierCode(), SupplyConstants.Order.SUPPLIER_LY_CODE)){
                    if(!StringUtils.equals(supplierOrderInfo.getSupplierOrderCode(), logistic.getSupplierOrderCode())){
                        continue;
                    }
                }
                SupplierOrderLogistics supplierOrderLogistics = null;
                try{
                    //获取供应商订单物流信息
                    supplierOrderLogistics = getSupplierOrderLogistics(supplierOrderInfo, logistic);
                    //保存的物流信息 or 更新物流信息
                    saveSupplierOrderLogistics(supplierOrderLogistics);
                    supplierOrderLogisticsList.add(supplierOrderLogistics);
                }catch (Exception e){
                    log.error(String.format("保存供应商物流信息%s异常,%s", JSONObject.toJSON(supplierOrderLogistics), e.getMessage()), e);
                }
            }
        }
        if(supplierOrderLogisticsList.size() > 0){
            //根据物流信息更新订单商品供应商订单状态
            updateOrderItemSupplierOrderStatusByLogistics(supplierOrderInfo.getWarehouseOrderCode());
            //更新供应商订单状态
            updateSupplierOrderStatus(supplierOrderInfo);
            //更新仓库订单供应商订单状态
            WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(supplierOrderInfo.getWarehouseOrderCode(), false);
            //记录发货日志
            recordSendLog(supplierOrderLogisticsList, supplierOrderInfo.getWarehouseOrderCode());
            //更新店铺订单供应商订单状态
            updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
            //清除订单缓存
            orderExtBiz.cleanOrderCache();
        }
    }

    private void recordSendLog (List<SupplierOrderLogistics> supplierOrderLogistics, String warehouseOrderCode) {
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
//        List<Logistic> logistics = logisticForm.getLogistics();
        AssertUtil.notEmpty(orderItemList, String.format("记录发货日志时,根据仓库订单号[%s]查询相应的商品明细为空", warehouseOrderCode));
//        AssertUtil.notEmpty(logistics, "记录发货日志时,物流信息为空");

        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("记录发货日志时,根据仓库订单编码[%s]查询仓库订单信息为空", warehouseOrderCode));
        StringBuilder sb = new StringBuilder();

    	for (OrderItem item : orderItemList) {
    		for (SupplierOrderLogistics logic : supplierOrderLogistics) {
    			List<SkuInfo> skus = JSONArray.parseArray(logic.getSkus(), SkuInfo.class);
    			AssertUtil.notEmpty(skus, "记录发货日志时,sku信息为空");
    			for (SkuInfo sku : skus) {
    				if (StringUtils.equals(item.getSupplierSkuCode(), sku.getSkuCode())) {
    					if (StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), item.getSupplierOrderStatus())) {
    						sb.append(sku.getSkuCode()).append(":").append(ORDER_ALL_INFO).append(HTML_BR);
    					} else if (StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), item.getSupplierOrderStatus())) {
    						sb.append(sku.getSkuCode()).append(":").append(ORDER_PART_INFO).append(HTML_BR);
    					}
    				}
    			}
    		}
    	}
        //记录操作日志
        logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(),
        		warehouseOrder.getSupplierName(), LogOperationEnum.SEND.getMessage(), sb.toString(), null);
    }

    /**
     * 根据物流信息更新订单商品供应商订单状态
     * @param warehouseOrderCode
     */
    private void updateOrderItemSupplierOrderStatusByLogistics(String warehouseOrderCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("获取物流信息后更新订单商品供应商订单状态,根据仓库订单号[%s]查询相应的商品明细为空", warehouseOrderCode));
        SupplierOrderLogistics supplierOrderLogistics = new SupplierOrderLogistics();
        supplierOrderLogistics.setWarehouseOrderCode(warehouseOrderCode);
        List<SupplierOrderLogistics> supplierOrderLogisticsList = supplierOrderLogisticsService.select(supplierOrderLogistics);
        for(OrderItem orderItem2: orderItemList){
            if(!StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_FAILURE.getCode(), orderItem2.getSupplierOrderStatus()) &&
                    !StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem2.getSupplierOrderStatus())&& !StringUtils.equals(OrderItemDeliverStatusEnum.ORDER_CANCEL.getCode(), orderItem2.getSupplierOrderStatus())){
                int deliverNum = 0;
                for(SupplierOrderLogistics supplierOrderLogistics2: supplierOrderLogisticsList){
                    List<SkuInfo> skuInfos = JSONArray.parseArray(supplierOrderLogistics2.getSkus(), SkuInfo.class);
                    for(SkuInfo skuInfo : skuInfos){
                        if(StringUtils.equals(orderItem2.getSupplierSkuCode(), skuInfo.getSkuCode())){
                            deliverNum += skuInfo.getNum();
                        }
                    }
                }
                if(deliverNum == orderItem2.getNum()){
                    orderItem2.setSupplierOrderStatus(SupplierOrderStatusEnum.ALL_DELIVER.getCode());
                    orderItemService.updateByPrimaryKey(orderItem2);
                }else{
                    if(deliverNum > 0 && deliverNum < orderItem2.getNum()){
                        orderItem2.setSupplierOrderStatus(SupplierOrderStatusEnum.PARTS_DELIVER.getCode());
                        orderItemService.updateByPrimaryKey(orderItem2);
                    }
                }
            }
        }
    }

    /**
     * 获取商品已经发货数量
     * @param supplierOrderCode
     * @param supplierSkuCode
     * @param supplierOrderLogisticsList
     * @return
     */
    private int getOrderItemDeliveredNum(String supplierOrderCode, String supplierSkuCode, List<SupplierOrderLogistics> supplierOrderLogisticsList){
        for(SupplierOrderLogistics supplierOrderLogistics2: supplierOrderLogisticsList){
            if(StringUtils.equals(supplierOrderCode, supplierOrderLogistics2.getSupplierOrderCode())){
                List<SkuInfo> skuInfos = JSONArray.parseArray(supplierOrderLogistics2.getSkus(), SkuInfo.class);
                for(SkuInfo skuInfo : skuInfos){
                    if(StringUtils.equals(supplierSkuCode, skuInfo.getSkuCode())){
                        return skuInfo.getNum();
                    }
                }
            }
        }
        return 0;
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
     */
    private void updateSupplierOrderStatus(SupplierOrderInfo supplierOrderInfo){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新供应商订单供应商订单状态,根据仓库订单号[%s]查询相应的商品明细为空", supplierOrderInfo.getWarehouseOrderCode()));
        List<SkuInfo> skuInfos = JSONArray.parseArray(supplierOrderInfo.getSkus(), SkuInfo.class);
        List<OrderItem> tmpOrderItemList = new ArrayList<OrderItem>();
        for(SkuInfo skuInfo: skuInfos){
            for(OrderItem orderItem2: orderItemList){
                if(StringUtils.equals(skuInfo.getSkuCode(), orderItem2.getSupplierSkuCode())){
                    tmpOrderItemList.add(orderItem2);
                }
            }
        }
        if(tmpOrderItemList.size() == 0)
            return;
        String logisticsStatus = SupplierOrderDeliverStatusEnum.UM_COMPLETE.getCode();//未完成
        String supplierOrderStatus = getSupplierOrderStatusByItems(tmpOrderItemList, ZeroToNineEnum.ZERO.getCode());
        if(StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), supplierOrderStatus)){//全部发货
            logisticsStatus = SupplierOrderDeliverStatusEnum.COMPLETE.getCode();//完成
        }
        supplierOrderInfo.setLogisticsStatus(logisticsStatus);
        supplierOrderInfo.setSupplierOrderStatus(supplierOrderStatus);
        supplierOrderInfo.setUpdateTime(Calendar.getInstance().getTime());
        supplierOrderInfoService.updateByPrimaryKeySelective(supplierOrderInfo);
    }

    /**
     * 获取供应商订单物流信息
     * @param supplierOrderInfo
     * @param logistic
     * @return
     */
    private SupplierOrderLogistics getSupplierOrderLogistics(SupplierOrderInfo supplierOrderInfo, Logistic logistic){
        SupplierOrderLogistics supplierOrderLogistics = new SupplierOrderLogistics();
        supplierOrderLogistics.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
        supplierOrderLogistics.setSupplierCode(supplierOrderInfo.getSupplierCode());
        if(StringUtils.isNotBlank(supplierOrderInfo.getSupplierOrderCode()))
            supplierOrderLogistics.setSupplierParentOrderCode(supplierOrderInfo.getSupplierOrderCode());
        supplierOrderLogistics.setSupplierOrderCode(logistic.getSupplierOrderCode());
        supplierOrderLogistics.setType(LogsticsTypeEnum.WAYBILL_NUMBER.getCode());
        supplierOrderLogistics.setWaybillNumber(logistic.getWaybillNumber());
        supplierOrderLogistics.setLogisticsCorporation(logistic.getLogisticsCorporation());
        if(null != logistic.getSkus())
            supplierOrderLogistics.setSkus(JSONArray.toJSONString(logistic.getSkus()));
        if(null != logistic.getLogisticInfo())
            supplierOrderLogistics.setLogisticsInfo(JSONArray.toJSONString(logistic.getLogisticInfo()));
        supplierOrderLogistics.setLogisticsStatus(logistic.getLogisticsStatus());
        ParamsUtil.setBaseDO(supplierOrderLogistics);
        return supplierOrderLogistics;
    }

    /**
     * 调用查询物流服务接口
     * @param warehouseOrderCode
     * @param channelCode 渠道编码
     * @param flag 0-京东,1-粮油
     * @return
     */
    private LogisticForm invokeGetLogisticsInfo(String warehouseOrderCode, String channelCode, String flag){
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
            if(StringUtils.equals(LogsticsTypeEnum.LOGSTICS.getCode(), logisticForm.getType())){//京东配送信息
                logistic.setWaybillNumber(logistic.getSupplierOrderCode());
                logistic.setLogisticsCorporation(SupplyConstants.Order.SUPPLIER_JD_LOGISTICS_COMPANY2);
            }
            LogisticsCompany logisticsCompany = orderExtBiz.getLogisticsCompanyByName(LogisticsTypeEnum.TRC, logistic.getLogisticsCorporation());
            logistic.setLogisticsCorporationCode(logisticsCompany.getCompanyCode());
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
        PlatformOrder platformOrder = JSONObject.parseObject(platformObj.toJSONString(),PlatformOrder.class);
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
        platformOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());

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


    /**
     * 保存幂等流水
     *
     * @param shopOrderList
     */
    private void saveIdempotentFlow(List<ShopOrder> shopOrderList, List<ImportOrderInfo> importOrderInfoList, String orderType) {
        if(CollectionUtils.isEmpty(shopOrderList)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<ShopOrder> it = shopOrderList.iterator();
        while (it.hasNext()){
            ShopOrder shopOrder = it.next();
            try{
                OrderIdempotent orderIdempotent = new OrderIdempotent();
                orderIdempotent.setChannelCode(shopOrder.getChannelCode());
                orderIdempotent.setSellCode(shopOrder.getSellCode());
                orderIdempotent.setShopOrderCode(shopOrder.getShopOrderCode());
                orderIdempotent.setCreateTime(new Date());
                orderIdempotentService.insert(orderIdempotent);
            }catch (DuplicateKeyException e){
                if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), orderType)) {//导入订单
                    for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                        if(StringUtils.equals(shopOrder.getChannelCode(), importOrderInfo.getChannelCode()) &&
                                StringUtils.equals(shopOrder.getSellCode(), importOrderInfo.getSellCode()) &&
                                StringUtils.equals(shopOrder.getShopOrderCode(), importOrderInfo.getShopOrderCode())){
                            importOrderInfo.setFlag(false);
                            importOrderInfo.setErrorMessage("该订单已导入成功，暂不支持重复导入");
                        }
                    }
                    it.remove();
                }
                String msg = String.format("业务线%s销售渠道%s订单%s", shopOrder.getChannelCode(), shopOrder.getSellCode(), shopOrder.getShopOrderCode());
                log.error(msg+"重复");
                sb.append(msg);
            }
            if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), orderType)) {//接收订单
                if(sb.length() > 0){
                    throw new OrderException(ExceptionEnum.TRC_ORDER_PUSH_EXCEPTION, sb.toString()+"重复");
                }
            }
        }
    }

    /**
     * 获取店铺订单
     * @param shopOrderArray
     * @param payTime 支付时间
     * @return
     */
    private List<ShopOrder> getShopOrderList(JSONArray shopOrderArray, String platformType, Date payTime) {
        List<ShopOrder> shopOrderList = new ArrayList<ShopOrder>();
        BigDecimal totalShop = new BigDecimal(0);
        for (Object obj : shopOrderArray) {
            JSONObject tmpObj = (JSONObject) obj;
            JSONObject shopOrderObj = tmpObj.getJSONObject("shopOrder");
            ShopOrder shopOrder = JSONObject.parseObject(tmpObj.getString("shopOrder"),ShopOrder.class);
            String scmShopOrderCode = serialUtilService.generateCode(SupplyConstants.Serial.SYSTEM_ORDER_LENGTH, SupplyConstants.Serial.SYSTEM_ORDER_CODE, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            shopOrder.setScmShopOrderCode(scmShopOrderCode);
            shopOrder.setPlatformType(platformType);
            shopOrder.setCreateTime(DateUtils.timestampToDate(shopOrderObj.getLong("createTime")));//创建时间
            shopOrder.setPayTime(payTime);//支付时间
            shopOrder.setConsignTime(DateUtils.timestampToDate(shopOrderObj.getLong("consignTime")));//发货时间
            shopOrder.setUpdateTime(DateUtils.timestampToDate(shopOrderObj.getLong("updateTime")));//修改时间
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.WAIT_FOR_DELIVER.getCode());//待发货
            //设置店铺金额
            setShopOrderFee(shopOrder, shopOrderObj);
            JSONArray orderItemArray = tmpObj.getJSONArray("orderItems");
            AssertUtil.notEmpty(orderItemArray, String.format("接收渠道订单参数中平店铺订单%s相关商品订单明细信息为空为空", shopOrderObj));
            //获取订单商品明细
            List<OrderItem> orderItemList = getOrderItem(orderItemArray, scmShopOrderCode, shopOrder.getChannelCode(), shopOrder.getSellCode());
            totalShop = totalShop.add(shopOrder.getPayment());
            shopOrder.setOrderItems(orderItemList);
            shopOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());

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
    private List<OrderItem> getOrderItem(JSONArray orderItemArray, String scmShopOrderCode, String channelCode, String sellCode){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(Object obj: orderItemArray){
            JSONObject orderItemObj = (JSONObject)obj;
            OrderItem orderItem = JSONObject.parseObject(orderItemObj.toJSONString(),OrderItem.class);
            orderItem.setScmShopOrderCode(scmShopOrderCode);
            orderItem.setChannelCode(channelCode);
            orderItem.setSellCode(sellCode);
            orderItem.setOrderItemCode(orderItemObj.getString("id"));
            String channelSkuCode = orderItem.getSkuCode();//渠道sku编码
            String scmSkuCode = orderItem.getOuterSkuId();//供应链sku编码
            //将skuCode设置成供应链sku编码,outerSkuId设置成渠道sku编码
            orderItem.setSkuCode(scmSkuCode);
            orderItem.setOuterSkuId(channelSkuCode);
            //设置金额
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
            BigDecimal promotionPrice = orderItemObj.getBigDecimal("promotionPrice");
            orderItem.setPromotionPrice(promotionPrice);//促销价
            if(null != promotionPrice && promotionPrice.compareTo(new BigDecimal(ZERO_MONEY_STR)) > 0){
                orderItem.setPrice(promotionPrice);
            }
            orderItem.setMarketPrice(orderItemObj.getBigDecimal("marketPrice"));//市场价
            orderItem.setCustomsPrice(orderItemObj.getBigDecimal("customsPrice"));//报关单价
            orderItem.setTransactionPrice(orderItemObj.getBigDecimal("transactionPrice"));//成交单价
            orderItem.setTotalWeight(orderItemObj.getBigDecimal("totalWeight"));//商品重量

            orderItem.setCreateTime(DateUtils.timestampToDate(orderItemObj.getLong("createTime")));//创建时间
            orderItem.setPayTime(DateUtils.timestampToDate(orderItemObj.getLong("payTime")));//支付时间
            orderItem.setConsignTime(DateUtils.timestampToDate(orderItemObj.getLong("consignTime")));//发货时间
            orderItem.setUpdateTime(DateUtils.timestampToDate(orderItemObj.getLong("updateTime")));//修改时间
            orderItem.setTimeoutActionTime(DateUtils.timestampToDate(orderItemObj.getLong("timeoutActionTime")));//超时确认时间
            orderItem.setEndTime(DateUtils.timestampToDate(orderItemObj.getLong("endTime")));//结束时间
            if(orderItem.getSkuCode().startsWith(SP0)){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER.getCode());//等待仓库发货
            }else if(orderItem.getSkuCode().startsWith(SP1)){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode());//待发送供应商
            }

            orderItem.setIsStoreOrder(IsStoreOrderEnum.NOT_STORE_ORDER.getCode());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 校验是否供应链商品
     * @param orderItemList
     */
    private void isScmItems(List<OrderItem> orderItemList){
        Set<String> skuCodes = new HashSet<String>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        //查询自采商品
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        //criteria.andEqualTo("channelCode", channelCode);
        List<SkuRelation> skuRelations = skuRelationService.selectByExample(example);
        AssertUtil.notEmpty(skuRelations, String.format("skuCode为[%s]的订单商品在供应链系统无法识别", CommonUtil.converCollectionToString(Arrays.asList(skuCodes.toArray()))));
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
     * 商品参数校验
     * @param orderItemList
     */
    private List<ExternalItemSku> checkSupplierItems(List<OrderItem> orderItemList){
        Set<String> skuCodes = new HashSet<String>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        Example example2 = new Example(ExternalItemSku.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("skuCode", skuCodes);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example2);
        AssertUtil.notEmpty(externalItemSkuList, String.format("根据多个skuCode[%s]查询代发商品列表为空", CommonUtil.converCollectionToString(Arrays.asList(skuCodes.toArray()))));
        //校验商品上下架状态
        /*List<String> _tmpSkuCodes = new ArrayList<String>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), externalItemSku.getState()))
                _tmpSkuCodes.add(externalItemSku.getSkuCode());
        }
        if(_tmpSkuCodes.size() > 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("代发商品[%s]的供应商商品状态为下架!", CommonUtil.converCollectionToString(_tmpSkuCodes)));
        }*/
        //校验商品最小购买量
        for(OrderItem orderItem: orderItemList){
            for(ExternalItemSku externalItemSku: externalItemSkuList){
                if(StringUtils.equals(orderItem.getSkuCode(), externalItemSku.getSkuCode())){
                    if(null != externalItemSku.getMinBuyCount() && orderItem.getNum() < externalItemSku.getMinBuyCount()){
                        throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("代发商品[%s]的购买数量小于供应商要求的最小购买量！",orderItem.getSkuCode()));
                    }
                }
            }
        }
        return externalItemSkuList;
    }


    /**
     * 获取自采商品本地库存
     * @param orderItemList
     * @return
     */
    private List<SkuStock> getSelfItemsLocalStock(List<OrderItem> orderItemList){
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(SkuStock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        return skuStockService.selectByExample(example);
    }

    /** FIXME 该方法暂时不用，以后肯定会用，所以暂时注释
     * 校验自采商品的可用库存
     * @param skuStockList
     * @return
     */
    /*private Map<String, Object> checkSelfItemAvailableInventory(List<OrderItem> orderItemList, List<SkuStock> skuStockList, List<ScmInventoryQueryResponse> scmInventoryQueryResponseList){
        setScmInventoryQueryResponseItemCode(orderItemList, scmInventoryQueryResponseList);
        Map<String, List<SkuWarehouseDO>> warehouseSkuMap = new HashMap<>();
        List<String> skuCodeList = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            skuCodeList.add(orderItem.getSkuCode());
        }
        //校验失败的商品
        List<OrderItem> checkFailureItems = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            long qimenStock = 0;//奇门库存
            long localStock = 0;//本地库存
            List<ScmInventoryQueryResponse> _inventoryQueryItemList = new ArrayList<>();
            for(ScmInventoryQueryResponse items: scmInventoryQueryResponseList){
                if(StringUtils.equals(orderItem.getSkuCode(), items.getItemCode())){
                    _inventoryQueryItemList.add(items);
                }
            }
            //按仓库库存降序排序
            if(_inventoryQueryItemList.size() > 0){
                Collections.sort(_inventoryQueryItemList, new Comparator<ScmInventoryQueryResponse>() {
                    @Override
                    public int compare(ScmInventoryQueryResponse o1, ScmInventoryQueryResponse o2) {
                        return o2.getQuantity().intValue() - o1.getQuantity().intValue();
                    }
                });
                qimenStock = _inventoryQueryItemList.get(0).getQuantity();
            }
            List<SkuStock> _skuStockList = new ArrayList<>();
            for(SkuStock skuStock: skuStockList){
                if(StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())){
                    _skuStockList.add(skuStock);
                }
            }
            SkuStock maxSkuStock = null;
            //按本地库存降序排序
            if(_skuStockList.size() > 0){
                Collections.sort(_skuStockList, new Comparator<SkuStock>() {
                    @Override
                    public int compare(SkuStock o1, SkuStock o2) {
                        int _stock1 = o1.getRealInventory().intValue() - o1.getFrozenInventory().intValue();
                        int _stock2 = o2.getRealInventory().intValue() - o2.getFrozenInventory().intValue();
                        return _stock2 - _stock1;
                    }
                });
                maxSkuStock = _skuStockList.get(0);
                localStock = maxSkuStock.getRealInventory() - maxSkuStock.getFrozenInventory();
            }
            boolean _flag = false;
            //校验库存,本地库存和仓库库存以小的为准
            if(localStock >= qimenStock){
                if(qimenStock >= orderItem.getNum().longValue()){
                    _flag = true;
                }else{
                    checkFailureItems.add(orderItem);
                }
            }else{
                if(localStock >= orderItem.getNum().longValue()){
                    _flag = true;
                }else{
                    checkFailureItems.add(orderItem);
                }
            }
            if(_flag){
                List<SkuWarehouseDO> skuWarehouseDOList = new ArrayList<>();
                SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                skuWarehouseDO.setSkuCode(maxSkuStock.getSkuCode());
                skuWarehouseDO.setItemNum(orderItem.getNum().longValue());
                skuWarehouseDO.setChannelCode(maxSkuStock.getChannelCode());
                skuWarehouseDO.setWarehouseCode(maxSkuStock.getWarehouseCode());
                for(ScmInventoryQueryResponse items: scmInventoryQueryResponseList){
                    if(StringUtils.equals(maxSkuStock.getSkuCode(), items.getItemCode())){
                        skuWarehouseDO.setItemId(items.getItemId());
                    }
                }
                skuWarehouseDOList.add(skuWarehouseDO);
                warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
            }else {
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_FAILURE.getCode());//供应商下单失败
            }
        }
        Map<String, Object> map = new HashedMap();
        map.put("checkFailureItems", checkFailureItems);
        map.put("warehouseSkuMap", warehouseSkuMap);
        return map;
    }*/


    private Map<String, Object> checkSelfItemAvailableInventory(List<OrderItem> orderItemList, List<SkuStock> skuStockList, List<ScmInventoryQueryResponse> scmInventoryQueryResponseList,
                                                                List<WarehousePriority> warehousePriorityList, List<WarehouseInfo> storeWarehouseInfoList){
        Map<String, Object> map = new HashedMap();
        Map<String, List<SkuWarehouseDO>> warehouseSkuMap = new HashMap<>();
        //校验失败的商品
        List<OrderItem> checkFailureItems = new ArrayList<>();
        if(CollectionUtils.isEmpty(storeWarehouseInfoList)){//没有门店订单
            if(CollectionUtils.isEmpty(scmInventoryQueryResponseList) || CollectionUtils.isEmpty(skuStockList)){
                for(OrderItem orderItem: orderItemList){
                    checkFailureItems.add(orderItem);
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAREHOUSE_RECIVE_FAILURE.getCode());
                    log.error(String.format("自采拆单校验sku编码为[%s]的库存时查询不到商品库存信息", orderItem.getSkuCode()));
                }
                map.put("checkFailureItems", checkFailureItems);
                map.put("warehouseSkuMap", warehouseSkuMap);
                return map;
            }
        }
        //设置仓库库存返回结果里的skuCode值
        setScmInventoryQueryResponseItemCode(orderItemList, scmInventoryQueryResponseList);
        List<String> skuCodeList = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            skuCodeList.add(orderItem.getSkuCode());
        }
        for(OrderItem orderItem: orderItemList){
            long qimenStock = 0;//仓库库存
            List<ScmInventoryQueryResponse> _inventoryQueryItemList = new ArrayList<>();
            for(ScmInventoryQueryResponse item: scmInventoryQueryResponseList){
                if(StringUtils.equals(orderItem.getSkuCode(), item.getItemCode())){
                    _inventoryQueryItemList.add(item);
                }
            }
            //按仓库库存降序排序
            /*if(_inventoryQueryItemList.size() > 0){
                Collections.sort(_inventoryQueryItemList, new Comparator<ScmInventoryQueryResponse>() {
                    @Override
                    public int compare(ScmInventoryQueryResponse o1, ScmInventoryQueryResponse o2) {
                        return o2.getQuantity().intValue() - o1.getQuantity().intValue();
                    }
                });
                qimenStock = _inventoryQueryItemList.get(0).getQuantity();
            }*/


            if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == orderItem.getIsStoreOrder().intValue()){
                List<SkuWarehouseDO> skuWarehouseDOList = new ArrayList<>();
                SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                skuWarehouseDO.setSkuCode(orderItem.getSkuCode());
                skuWarehouseDO.setItemNum(orderItem.getNum().longValue());
                skuWarehouseDO.setChannelCode(orderItem.getChannelCode());
                skuWarehouseDO.setItemId(orderItem.getSkuCode());
                for(WarehouseInfo warehouseInfo: storeWarehouseInfoList){
                    if(StringUtils.equals(orderItem.getSellCode(), warehouseInfo.getStoreCorrespondChannel())){
                        skuWarehouseDO.setWarehouseCode(warehouseInfo.getCode());
                        break;
                    }
                }
                skuWarehouseDOList.add(skuWarehouseDO);
                warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
            }else{
                boolean _flag = false;
                //检查库存
                ScmInventoryQueryResponse scmInventoryQueryResponse = checkStock(orderItem.getNum().intValue(), _inventoryQueryItemList, warehousePriorityList);
                //校验库存
                if(null != scmInventoryQueryResponse){
                    _flag = true;
                }else{
                    checkFailureItems.add(orderItem);
                }
                if(_flag){
                    SkuStock _skuStock = null;
                    for(SkuStock skuStock: skuStockList){
                        if(StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode()) &&
                                StringUtils.equals(skuStock.getWarehouseItemId(), scmInventoryQueryResponse.getItemId()) &&
                                StringUtils.equals(skuStock.getWarehouseCode(), scmInventoryQueryResponse.getLocalWarehouseCode())){
                            _skuStock = skuStock;
                            break;
                        }
                    }
                    List<SkuWarehouseDO> skuWarehouseDOList = new ArrayList<>();
                    SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                    skuWarehouseDO.setSkuCode(_skuStock.getSkuCode());
                    skuWarehouseDO.setItemNum(orderItem.getNum().longValue());
                    skuWarehouseDO.setChannelCode(_skuStock.getChannelCode());
                    skuWarehouseDO.setOwnerCode(scmInventoryQueryResponse.getOwnerCode());
                    skuWarehouseDO.setWarehouseCode(_skuStock.getWarehouseCode());
                    /*for(ScmInventoryQueryResponse item: scmInventoryQueryResponseList){
                        if(StringUtils.equals(_skuStock.getSkuCode(), item.getItemCode()), StringUtils.equals()){
                            skuWarehouseDO.setItemId(item.getItemId());
                        }
                    }*/
                    skuWarehouseDO.setItemId(scmInventoryQueryResponse.getItemId());
                    skuWarehouseDOList.add(skuWarehouseDO);
                    warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
                }else {
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAREHOUSE_RECIVE_FAILURE.getCode());
                }
            }

        }
        map.put("checkFailureItems", checkFailureItems);
        map.put("warehouseSkuMap", warehouseSkuMap);
        return map;
    }

    private ScmInventoryQueryResponse checkStock(int itemNum, List<ScmInventoryQueryResponse> inventoryQueryItemList, List<WarehousePriority> warehousePriorityList){
        List<ScmInventoryQueryResponse> _inventoryQueryItemList = new ArrayList<>();
        for(WarehousePriority priority: warehousePriorityList){
            for(ScmInventoryQueryResponse response: inventoryQueryItemList){
                if(StringUtils.equals(priority.getWmsWarehouseCode(), response.getWarehouseCode())){
                    _inventoryQueryItemList.add(response);
                    break;
                }
            }
        }
        for(ScmInventoryQueryResponse response: _inventoryQueryItemList){
            if(response.getQuantity().intValue() >= itemNum){
                return response;
            }
        }
        return null;
    }




    /**
     * 设置库存查询返回结果的sku编码
     * @param orderItemList
     * @param scmInventoryQueryResponseList
     */
    private void setScmInventoryQueryResponseItemCode(List<OrderItem> orderItemList, List<ScmInventoryQueryResponse> scmInventoryQueryResponseList){
        List<String> skuCodes = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseExtService.getWarehouseItemInfos(skuCodes);
        for(ScmInventoryQueryResponse response: scmInventoryQueryResponseList){
            for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                if(StringUtils.equals(response.getItemId(), warehouseItemInfo.getWarehouseItemId())){
                    response.setItemCode(warehouseItemInfo.getSkuCode());
                    break;
                }
            }
        }
    }


    /**
     * 校验店铺订单渠道信息
     * @param channelCodes
     * @param sellCodes
     */
    private List<SellChannel> checkOrderChannel(List<String> channelCodes, List<String> sellCodes){
        if(!CollectionUtils.isEmpty(channelCodes)){
            Example example = new Example(Channel.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", channelCodes);
            List<Channel> channelList = channelService.selectByExample(example);
            StringBuilder sb = new StringBuilder();
            for(String channelCode: channelCodes){
                boolean flag = false;
                for(Channel channel: channelList){
                    if(StringUtils.equals(channelCode, channel.getCode())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    sb.append(channelCode).append(SupplyConstants.Symbol.COMMA);
                }
            }
            if(sb.length() > 0){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("业务线%s不是供应链的合法数据", sb.substring(0, sb.length()-1)));
            }
        }
        List<SellChannel> sellChannelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(sellCodes)){
            Example example = new Example(SellChannel.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("sellCode", sellCodes);
            List<SellChannel> channelList = sellChannelService.selectByExample(example);
            StringBuilder sb = new StringBuilder();
            for(String channelCode: sellCodes){
                boolean flag = false;
                for(SellChannel channel: channelList){
                    if(StringUtils.equals(channelCode, channel.getSellCode())){
                        flag = true;
                        sellChannelList.add(channel);
                        break;
                    }
                }
                if(!flag){
                    sb.append(channelCode).append(SupplyConstants.Symbol.COMMA);
                }
            }
            if(sb.length() > 0){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("销售渠道%s不是供应链的合法数据", sb.substring(0, sb.length()-1)));
            }
        }
        return sellChannelList;
    }

    /**
     * 平台订单校验
     *
     * @param platformOrder
     */
    private void platformOrderParamCheck(PlatformOrder platformOrder, List<SellChannel> sellChannelList, List<OrderItem> orderItems) {
        //检查收货用户信息
        checkCustmerInfo(platformOrder, sellChannelList);

        //AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");

        //AssertUtil.notBlank(platformOrder.getPayType(), "平台订单单支付类型不能为空");
        //AssertUtil.notBlank(platformOrder.getStatus(), "平台订单订单状态不能为空");
        //AssertUtil.notBlank(platformOrder.getType(), "平台订单订单类型不能为空");
        AssertUtil.notNull(platformOrder.getPayTime(), "平台订单支付时间不能为空");

        AssertUtil.isTrue(platformOrder.getItemNum() > 0, "买家购买的商品总数不能为空");
        //AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(new BigDecimal(0))==0 || platformOrder.getTotalFee().compareTo(new BigDecimal(0))==1, "平台订单总金额应大于等于0");
        //AssertUtil.isTrue(platformOrder.getPayment().compareTo(new BigDecimal(0))==0 || platformOrder.getPayment().compareTo(new BigDecimal(0))==1 ,"平台订单实付金额应大于等于0");

        List<String> skusList = new ArrayList<>();
        for(OrderItem orderItem: orderItems){
            if(orderItem.getSkuCode().startsWith(SP1)){
                skusList.add(orderItem.getSkuCode());
            }
        }
        if(skusList.size() == 0){
            return;
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skusList);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, externalItemSku.getSupplierCode())){
                AssertUtil.notBlank(platformOrder.getReceiverIdCard(), "粮油下单收货人身份证不能为空");
                break;
            }
        }
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, externalItemSku.getSupplierCode())){
                AssertUtil.notBlank(platformOrder.getReceiverEmail(), "京东下单收货人电子邮箱不能为空");
                ValidateUtil.checkEmail(platformOrder.getReceiverEmail());
                break;
            }
        }
    }



    /**
     * 检查收货用户信息
     * @param platformOrder
     */
    private void checkCustmerInfo(PlatformOrder platformOrder, List<SellChannel> sellChannelList){
        for(SellChannel sellChannel: sellChannelList){
            if(StringUtils.equals(SellChannelTypeEnum.ON_LINE.getCode().toString(), sellChannel.getSellType())){
                //AssertUtil.notBlank(platformOrder.getUserId(), "平台订单会员id不能为空");
                //AssertUtil.notBlank(platformOrder.getUserName(), "平台订单会员名称不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverName(), "平台订单收货人姓名不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverMobile(), "平台订单收货人手机号码不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverProvince(), "平台订单收货人所在省不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverCity(), "平台订单收货人所在城市不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverDistrict(), "平台订单收货人所在地区不能为空");
                AssertUtil.notBlank(platformOrder.getReceiverAddress(), "平台订单收货人详细地址不空");
                break;
            }
        }
    }

    /**
     * 店铺订单校验
     * @param shopOrder
     */
    private void shopOrderParamCheck(ShopOrder shopOrder) {
        AssertUtil.notBlank(shopOrder.getChannelCode(), "店铺订单业务线编码不能为空");
        AssertUtil.notBlank(shopOrder.getSellCode(), "店铺订单销售渠道编码不能为空");
        //AssertUtil.notBlank(shopOrder.getPlatformCode(), "店铺订单来源平台编码不能为空");
        //AssertUtil.notBlank(shopOrder.getPlatformOrderCode(), "店铺订单平台订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
        //AssertUtil.notBlank(shopOrder.getPlatformType(), "店铺订单订单来源类型不能为空");
        //AssertUtil.notNull(shopOrder.getShopId(), "店铺订单订单所属的店铺id不能为空");
        //AssertUtil.notBlank(shopOrder.getShopName(), "店铺订单店铺名称不能为空");
        //AssertUtil.notBlank(shopOrder.getUserId(), "店铺订单会员id不能为空");
        //AssertUtil.notBlank(shopOrder.getStatus(), "店铺订单订单状态不能为空");
        AssertUtil.notNull(shopOrder.getCreateTime(), "店铺订单创建时间不能为空");
        AssertUtil.isTrue(shopOrder.getItemNum() > 0, "店铺订单商品总数不能为空");

        AssertUtil.isTrue(shopOrder.getItemNum() > 0, "店铺订单购买的商品总数不能为空");
        //AssertUtil.isTrue(shopOrder.getTotalFee().compareTo(new BigDecimal(0))==0 || shopOrder.getTotalFee().compareTo(new BigDecimal(0))==1, "店铺订单总金额应大于等于0");
        //AssertUtil.isTrue(shopOrder.getPayment().compareTo(new BigDecimal(0))==0 || shopOrder.getPayment().compareTo(new BigDecimal(0))==1 ,"店铺订单实付金额应大于等于0");

    }

    /**
     * 商品参数校验
     *
     * @param orderItem
     */
    private void orderItemsParamCheck(OrderItem orderItem) {
        //AssertUtil.notBlank(orderItem.getChannelCode(), "订单商品渠道编码不能为空");
        //AssertUtil.notBlank(orderItem.getPlatformCode(), "订单商品来源平台编码不能为空");
        //AssertUtil.notBlank(orderItem.getPlatformOrderCode(), "订单商品平台订单编码不能为空");
        AssertUtil.notBlank(orderItem.getShopOrderCode(), "订单商品店铺订单编码不能为空");

        AssertUtil.notBlank(orderItem.getSkuCode(), "订单商品商品sku编码不能为空");
        //AssertUtil.notNull(orderItem.getShopId(), "订单商品订单所属的店铺id不能为空");
        //AssertUtil.notBlank(orderItem.getShopName(), "订单商品店铺名称不能为空");
        //AssertUtil.notBlank(orderItem.getUserId(), "订单商品会员id不能为空");
        //AssertUtil.notBlank(orderItem.getItemNo(), "订单商品货号不能为空");
        AssertUtil.notBlank(orderItem.getItemName(), "订单商品名称不能为空");
        //AssertUtil.notBlank(orderItem.getStatus(), "订单商品订单状态不能为空");
        AssertUtil.notNull(orderItem.getCreateTime(), "平台订单创建时间不能为空");

        AssertUtil.isTrue(orderItem.getNum() > 0, "订单商品购买数量不能为空");
        //AssertUtil.isTrue(orderItem.getPrice().compareTo(new BigDecimal(0))==1, "订单商品价格应大于0");
        //AssertUtil.isTrue(orderItem.getTotalFee().compareTo(new BigDecimal(0))==0 || orderItem.getTotalFee().compareTo(new BigDecimal(0))==1, "订单商品总金额应大于等于0");
        //AssertUtil.isTrue(orderItem.getPayment().compareTo(new BigDecimal(0))==0 || orderItem.getPayment().compareTo(new BigDecimal(0))==1 ,"订单商品实付金额应大于等于0");
        BigDecimal totalFee = orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum()));
        //AssertUtil.isTrue(totalFee.compareTo(orderItem.getTotalFee())==0, "订单商品价格*商品数量应等于订单应付金额totalFee");
    }


    /**
     * 拆分店铺级订单
     * @param platformOrder
     * @param shopOrder
     * @return
     */
    /*public List<WarehouseOrder> dealShopOrder(PlatformOrder platformOrder, ShopOrder shopOrder, List<SkuStock> skuStockList) {
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
            warehouseOrderList.addAll(dealSelfPurcharseOrder(orderItemList1, platformOrder, shopOrder, skuStockList));
        }
        if(orderItemList2.size() > 0){
            warehouseOrderList.addAll(dealSupplierOrder(orderItemList2, shopOrder));
        }
        List<OrderItem> _orderItemList = new ArrayList<>(orderItemList1);
        _orderItemList.addAll(orderItemList2);
        shopOrder.setOrderItems(_orderItemList);
        return warehouseOrderList;
    }*/


    /**
     * 处理自采订单
     * @param orderItems
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealSelfPurcharseOrder(List<OrderItem> orderItems, ShopOrder shopOrder, List<SkuStock> skuStockList, Map<String,
            List<SkuWarehouseDO>> skuWarehouseMap, List<WarehouseInfo> storeWarehouseInfoList) {
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        if(CollectionUtils.isEmpty(skuWarehouseMap) && CollectionUtils.isEmpty(skuStockList)){//企业购的订单
            return warehouseOrderList;
        }
        List<WarehouseInfo> warehouseList = new ArrayList<>();
        if(CollectionUtils.isEmpty(storeWarehouseInfoList)){//非门店订单
            Set<String> warehouseCodes = new HashSet<>();
            for(SkuStock skuStock: skuStockList){
                warehouseCodes.add(skuStock.getWarehouseCode());
            }
            Example example2 = new Example(WarehouseInfo.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("code", warehouseCodes);
            warehouseList = warehouseInfoService.selectByExample(example2);
            AssertUtil.notEmpty(warehouseList, String.format("根据仓库编码列表[%s]查询仓库为空", CommonUtil.converCollectionToString(new ArrayList<>(warehouseCodes))));
        }else{//门店订单
            warehouseList = storeWarehouseInfoList;
        }
        Set<String> warehouses = new HashSet<>();//所有匹配库存的仓库
        for(OrderItem orderItem: orderItems){
            for(Map.Entry<String, List<SkuWarehouseDO>> entry: skuWarehouseMap.entrySet()){
                if(StringUtils.equals(orderItem.getSkuCode(), entry.getKey())){
                    List<SkuWarehouseDO> skuWarehouseDOList = entry.getValue();
                    for(SkuWarehouseDO skuWarehouseDO: skuWarehouseDOList){
                        warehouses.add(skuWarehouseDO.getWarehouseCode());
                    }
                    break;
                }
            }
        }
        //创建仓库订单
        if(warehouses.size() > 0){
            Example example3 = new Example(WarehouseInfo.class);
            Example.Criteria criteria3 = example3.createCriteria();
            criteria3.andIn("code", warehouses);
            List<WarehouseInfo> warehouseList3 = warehouseInfoService.selectByExample(example3);
            for(String warehouseCode: warehouses){
                boolean flag = false;
                for(WarehouseInfo warehouse: warehouseList3){
                    if(StringUtils.equals(warehouseCode, warehouse.getCode())){
                        flag = true;
                        break;
                    }
                }
                AssertUtil.isTrue(flag, String.format("根据仓库订单编码[%s]查询仓库信息为空", warehouseCode));
            }
            for(WarehouseInfo warehouse: warehouseList3){
                List<OrderItem> warehouseOrderItemList = new ArrayList<>();
                for(Map.Entry<String, List<SkuWarehouseDO>> entry: skuWarehouseMap.entrySet()){
                    List<SkuWarehouseDO> skuWarehouseDOList = entry.getValue();
                    for(SkuWarehouseDO skuWarehouseDO: skuWarehouseDOList){
                        if(StringUtils.equals(warehouse.getCode(), skuWarehouseDO.getWarehouseCode())){
                            for(OrderItem orderItem: orderItems){
                                if(StringUtils.equals(orderItem.getSkuCode(), skuWarehouseDO.getSkuCode())){
                                    warehouseOrderItemList.add(orderItem);
                                }
                            }
                        }
                    }
                }
                WarehouseOrder warehouseOrder = getSelfWarehouseOrder(warehouse, warehouseOrderItemList, shopOrder);
                warehouseOrderList.add(warehouseOrder);
                //设置订单商品状态为待发送供应商
                for(OrderItem orderItem: orderItems){
                    for(OrderItem orderItem2: warehouseOrder.getOrderItemList()){
                        if(StringUtils.equals(orderItem.getSkuCode(), orderItem2.getSkuCode()) &&
                                StringUtils.equals(orderItem.getScmShopOrderCode(), orderItem2.getScmShopOrderCode())){
                            orderItem.setWarehouseOrderCode(orderItem2.getWarehouseOrderCode());
                            orderItem.setSupplierOrderStatus(orderItem2.getSupplierOrderStatus());
                        }
                    }
                }
            }
        }
        return warehouseOrderList;
    }




    /**
     * 获取自采仓库订单
     * @param warehouse
     * @param orderItems
     * @param shopOrder
     * @return
     */
    private WarehouseOrder getSelfWarehouseOrder(WarehouseInfo warehouse, List<OrderItem> orderItems, ShopOrder shopOrder){
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
        warehouseOrder.setWarehouseId(warehouse.getId());
        warehouseOrder.setWarehouseCode(warehouse.getCode());
        warehouseOrder.setWarehouseName(warehouse.getWarehouseName());
        warehouseOrder.setShopId(shopOrder.getShopId());
        warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
        warehouseOrder.setShopName(shopOrder.getShopName());
        warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
        warehouseOrder.setChannelCode(shopOrder.getChannelCode());
        warehouseOrder.setSellCode(shopOrder.getSellCode());
        warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
        warehouseOrder.setPlatformType(shopOrder.getPlatformType());
        warehouseOrder.setUserId(shopOrder.getUserId());
        warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
        warehouseOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        warehouseOrder.setPayTime(shopOrder.getPayTime());
        warehouseOrder.setIsStoreOrder(shopOrder.getIsStoreOrder());
        if(shopOrder.getIsStoreOrder()){//门店订单
            warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.ALL_DELIVER.getCode());
        }else{
            warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode());
        }
        warehouseOrder.setOrderType(OrderTypeEnum.SELF_PURCHARSE.getCode());//自采
        //流水号
        String code = serialUtilService.generateRandomCode(Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()), SupplyConstants.Serial.WAREHOUSE_ORDER,
                SupplyConstants.Serial.WAREHOUSE_ORDER_CODE, ZeroToNineEnum.ONE.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        warehouseOrder.setWarehouseOrderCode(code);
        setWarehouseOrderFee(warehouseOrder, orderItems);
        warehouseOrder.setOrderItemList(orderItems);
        return warehouseOrder;
    }


    /**
     * 获取sku和仓库库存关系map
     * @param orderItems
     * @param skusStockList
     * @return
     */
    /*private Map<String, List<SkuWarehouseDO>> getSkuWarehouseRelation(List<OrderItem> orderItems, List<SkuStock> skusStockList) {
        *//**
         * 商品库存匹配策略：
         * 1、目前只校验库存是否满足，如果不满足则是异常单
         * 2、匹配仓库库存的时候，随机匹配仓库，优先将同一个订单中的商品匹配在同一个仓库中
         * 3、如果一个商品库存不能再一个仓库匹配，那么分配到多个仓库
         *//*
        Map<String, List<SkuWarehouseDO>> warehouseSkuMap = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            long stock = 0;
            List<SkuWarehouseDO> skuWarehouseDOList = new ArrayList<>();
            //首先匹配单个仓库可以满足商品库存
            for (SkuStock skuStock : skusStockList) {
                if (StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())) {
                    //可用库存
                    long availableInventory = skuStock.getRealInventory() - skuStock.getFrozenInventory();
                    if (orderItem.getNum().longValue() <= availableInventory) {
                        stock = availableInventory;
                        SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                        skuWarehouseDO.setSkuCode(orderItem.getSkuCode());
                        skuWarehouseDO.setItemNum(availableInventory);
                        skuWarehouseDO.setWarehouseCode(skuStock.getWarehouseCode());
                        skuWarehouseDOList.add(skuWarehouseDO);
                        warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
                        break;
                    }
                }
            }
            if (stock == 0) {
                //然后匹配多个仓库可以满足商品库存
                for (SkuStock skuStock : skusStockList) {
                    if (stock < orderItem.getNum().longValue()) {
                        if (StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())) {
                            //可用库存
                            long availableInventory = skuStock.getRealInventory() - skuStock.getFrozenInventory();
                            stock += availableInventory;
                            SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                            skuWarehouseDO.setSkuCode(orderItem.getSkuCode());
                            skuWarehouseDO.setWarehouseCode(skuStock.getWarehouseCode());
                            if (orderItem.getNum().longValue() >= stock) {
                                skuWarehouseDO.setItemNum(availableInventory);
                            } else {
                                long _stock = stock - availableInventory;
                                skuWarehouseDO.setItemNum(orderItem.getNum() - _stock);
                            }
                            skuWarehouseDOList.add(skuWarehouseDO);
                        }
                    } else {
                        warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
                        break;
                    }
                }
            }
        }
        return warehouseSkuMap;
    }*/

    private Map<String, List<SkuWarehouseDO>> getSkuWarehouseRelation(List<OrderItem> orderItems, List<SkuStock> skusStockList) {
        /**
         * 商品库存匹配策略：
         * 1、目前只校验库存是否满足，如果不满足则是异常单
         * 2、匹配仓库库存的时候，随机匹配仓库，优先将同一个订单中的商品匹配在同一个仓库中
         * 3、如果一个商品库存不能再一个仓库匹配，那么分配到多个仓库(暂时不考虑)
         */
        Map<String, List<SkuWarehouseDO>> warehouseSkuMap = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            long stock = 0;
            List<SkuWarehouseDO> skuWarehouseDOList = new ArrayList<>();
            //首先匹配单个仓库可以满足商品库存
            for (SkuStock skuStock : skusStockList) {
                if (StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())) {
                    //可用库存
                    long availableInventory = skuStock.getRealInventory() - skuStock.getFrozenInventory();
                    if (orderItem.getNum().longValue() <= availableInventory) {
                        stock = availableInventory;
                        SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                        skuWarehouseDO.setSkuCode(orderItem.getSkuCode());
                        skuWarehouseDO.setItemNum(availableInventory);
                        skuWarehouseDO.setWarehouseCode(skuStock.getWarehouseCode());
                        skuWarehouseDOList.add(skuWarehouseDO);
                        warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
                        break;
                    }
                }
            }
            if (stock == 0) {
                //然后匹配多个仓库可以满足商品库存
                for (SkuStock skuStock : skusStockList) {
                    if (stock < orderItem.getNum().longValue()) {
                        if (StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())) {
                            //可用库存
                            long availableInventory = skuStock.getRealInventory() - skuStock.getFrozenInventory();
                            stock += availableInventory;
                            SkuWarehouseDO skuWarehouseDO = new SkuWarehouseDO();
                            skuWarehouseDO.setSkuCode(orderItem.getSkuCode());
                            skuWarehouseDO.setWarehouseCode(skuStock.getWarehouseCode());
                            if (orderItem.getNum().longValue() >= stock) {
                                skuWarehouseDO.setItemNum(availableInventory);
                            } else {
                                long _stock = stock - availableInventory;
                                skuWarehouseDO.setItemNum(orderItem.getNum() - _stock);
                            }
                            skuWarehouseDOList.add(skuWarehouseDO);
                        }
                    } else {
                        warehouseSkuMap.put(orderItem.getSkuCode(), skuWarehouseDOList);
                        break;
                    }
                }
            }
        }
        return warehouseSkuMap;
    }


    /**
     *
     * @param orderItem
     * @param exceptionReason
     */
    private ExceptionOrderItem getExceptionOrderItem(OrderItem orderItem, String exceptionReason, List<ExceptionOrderItem> exceptionOrderItemList){
        ExceptionOrderItem _exceptionOrderItem = null;
        for(ExceptionOrderItem exceptionOrderItem: exceptionOrderItemList){
            if(StringUtils.equals(orderItem.getScmShopOrderCode(), exceptionOrderItem.getScmShopOrderCode()) &&
                    StringUtils.equals(orderItem.getSkuCode(), exceptionOrderItem.getSkuCode())){
                _exceptionOrderItem = exceptionOrderItem;
                break;
            }
        }
        if(null != _exceptionOrderItem){
            return _exceptionOrderItem;
        }
        ExceptionOrderItem exceptionOrderItem = new ExceptionOrderItem();
        exceptionOrderItem.setScmShopOrderCode(orderItem.getScmShopOrderCode());
        exceptionOrderItem.setShopOrderCode(orderItem.getShopOrderCode());
        exceptionOrderItem.setPlatformOrderCode(orderItem.getPlatformOrderCode());
        exceptionOrderItem.setSkuCode(orderItem.getSkuCode());
        exceptionOrderItem.setItemName(orderItem.getItemName());
        if (orderItem.getSkuCode().startsWith(SP0)) {
            exceptionOrderItem.setItemType(GoodsTypeEnum.SELF_PURCHARSE.getCode());
        }else if(orderItem.getSkuCode().startsWith(SP1)) {
            exceptionOrderItem.setItemType(GoodsTypeEnum.SUPPLIER.getCode());
        }
        exceptionOrderItem.setStatus(ExceptionOrderHandlerEnum.HANDLERED.getCode());//默认已了结
        exceptionOrderItem.setExceptionReason(exceptionReason);
        exceptionOrderItem.setItemNum(orderItem.getNum());
        exceptionOrderItem.setExceptionNum(orderItem.getNum());
        exceptionOrderItem.setSupplierCode(SupplyConstants.Symbol.MINUS);
        exceptionOrderItem.setSupplierName(SupplyConstants.Symbol.MINUS);
        exceptionOrderItem.setSpecInfo(orderItem.getSpecNatureInfo());
        Date currentDate = Calendar.getInstance().getTime();
        exceptionOrderItem.setCreateTime(currentDate);
        exceptionOrderItem.setUpdateTime(currentDate);
        exceptionOrderItemList.add(exceptionOrderItem);
        return exceptionOrderItem;
    }

    /**
     * 保存异常单信息
     * @param platformOrder
     * @param shopOrders
     * @param exceptionOrderItemList
     */
    private void saveExceptionOrder(PlatformOrder platformOrder, List<ShopOrder> shopOrders, List<ExceptionOrderItem> exceptionOrderItemList){
        List<ExceptionOrder> exceptionOrderList = new ArrayList<>();
        for(ShopOrder shopOrder: shopOrders){
            ExceptionOrder exceptionOrder = new ExceptionOrder();
            exceptionOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
            exceptionOrder.setChannelCode(shopOrder.getChannelCode());
            exceptionOrder.setSellCode(shopOrder.getSellCode());
            exceptionOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            exceptionOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            exceptionOrder.setShopId(shopOrder.getShopId());
            exceptionOrder.setShopName(shopOrder.getShopName());
            exceptionOrder.setStatus(ExceptionOrderHandlerEnum.HANDLERED.getCode());
            exceptionOrder.setExceptionType(ExceptionTypeEnum.STOCK_LESS_REFUSE.getCode());
            exceptionOrder.setReceiverName(platformOrder.getReceiverName());
            exceptionOrder.setReceiverMobile(platformOrder.getReceiverMobile());
            Date currentDate = Calendar.getInstance().getTime();
            exceptionOrder.setCreateTime(currentDate);
            exceptionOrder.setUpdateTime(currentDate);
            int itemNum = 0;
            for(ExceptionOrderItem exceptionOrderItem: exceptionOrderItemList){
                if(StringUtils.equals(exceptionOrderItem.getScmShopOrderCode(), shopOrder.getScmShopOrderCode()) &&
                        StringUtils.equals(exceptionOrderItem.getShopOrderCode(), shopOrder.getShopOrderCode())){
                    itemNum += exceptionOrderItem.getItemNum();
                }
            }
            if(itemNum > 0){
                exceptionOrder.setItemNum(itemNum);
                String code = serialUtilService.generateCode(SupplyConstants.Serial.EXCEPTION_ORDER_LENGTH, SupplyConstants.Serial.EXCEPTION_ORDER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
                exceptionOrder.setExceptionOrderCode(code);
                for(ExceptionOrderItem exceptionOrderItem: exceptionOrderItemList){
                    if(StringUtils.equals(exceptionOrderItem.getShopOrderCode(), shopOrder.getShopOrderCode())){
                        exceptionOrderItem.setExceptionOrderCode(code);
                    }
                }
                exceptionOrderList.add(exceptionOrder);
            }
        }
        exceptionOrderService.insertList(exceptionOrderList);
        exceptionOrderItemService.insertList(exceptionOrderItemList);
        for(ExceptionOrder exceptionOrder: exceptionOrderList){
            //记录操作日志
            logInfoService.recordLog(exceptionOrder,exceptionOrder.getId().toString(), SYSTEM, LogOperationEnum.CREATE.getMessage(), String.format("创建原因：缺货退回"),null);
        }
    }


    /**
     * 处理代发供应商订单
     * @param orderItems
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealSupplierOrder(List<OrderItem> orderItems, ShopOrder shopOrder) {
        List<String> skuCodeList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            skuCodeList.add(orderItem.getSkuCode());
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodeList);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        AssertUtil.notEmpty(externalItemSkuList, String.format("根据sku编码列表[%s]查询一件代发商品为空", CommonUtil.converCollectionToString(skuCodeList)));
        Set<String> supplierInterfaceIds = new HashSet<>();
        for (ExternalItemSku externalItemSku : externalItemSkuList) {
            supplierInterfaceIds.add(externalItemSku.getSupplierCode());
        }
        Example example2 = new Example(Supplier.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("supplierKindCode", SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);//一件代发
        criteria2.andIn("supplierInterfaceId", supplierInterfaceIds);
        List<Supplier> supplierList = supplierService.selectByExample(example2);
        for(String supplierInterfaceId: supplierInterfaceIds){
            boolean bool = false;
            for(Supplier supplier: supplierList){
                if(StringUtils.equals(supplier.getSupplierInterfaceId(), supplierInterfaceId)){
                    bool = true;
                    break;
                }
            }
            if(!bool)
                AssertUtil.notEmpty(supplierList, String.format("接口ID为[%s]的一件代发供应商信息为空", supplierInterfaceId));
        }
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (Supplier supplier: supplierList) {
            List<OrderItem> orderItemList2 = new ArrayList<OrderItem>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
            warehouseOrder.setSupplierCode(supplier.getSupplierInterfaceId());
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setSellCode(shopOrder.getSellCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
            warehouseOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouseOrder.setPayTime(shopOrder.getPayTime());
            if(shopOrder.getIsStoreOrder()){//门店订单
                warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.ALL_DELIVER.getCode());
            }else{
                warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode());
            }
            //流水号
            String code = serialUtilService.generateRandomCode(Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()), SupplyConstants.Serial.WAREHOUSE_ORDER,
                    supplier.getSupplierCode(), ZeroToNineEnum.ONE.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            warehouseOrder.setWarehouseOrderCode(code);
            warehouseOrder.setOrderType(OrderTypeEnum.SUPPLIER.getCode());//代发
            for (ExternalItemSku externalItemSku : externalItemSkuList) {
                if(StringUtils.equals(supplier.getSupplierInterfaceId(), externalItemSku.getSupplierCode())){
                    OrderItem orderItem = getWarehouseOrderItems(warehouseOrder,externalItemSku, orderItems);
                    orderItem.setSupplierName(supplier.getSupplierName());
                    warehouseOrder.setSupplierName(supplier.getSupplierName());
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
            orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
            itemsNum += orderItem.getNum();
            totalFee = totalFee.add(null != orderItem.getTotalFee()?orderItem.getTotalFee():new BigDecimal(0));
            payment = payment.add(orderItem.getPayment());
            adjustFee = adjustFee.add(null != orderItem.getAdjustFee()?orderItem.getAdjustFee():new BigDecimal(0));
            postageFee = postageFee.add(null != orderItem.getPostDiscount()?orderItem.getPostDiscount():new BigDecimal(0));
            discountPromotion = discountPromotion.add(null != orderItem.getDiscountPromotion()?orderItem.getDiscountPromotion():new BigDecimal(0));
            discountCouponShop = discountCouponShop.add(null != orderItem.getDiscountCouponShop()?orderItem.getDiscountCouponShop():new BigDecimal(0));
            discountCouponPlatform = discountCouponPlatform.add(null != orderItem.getDiscountCouponPlatform()?orderItem.getDiscountCouponPlatform():new BigDecimal(0));
            discountFee = discountFee.add(null != orderItem.getDiscountFee()?orderItem.getDiscountFee():new BigDecimal(0));
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
            if(StringUtils.equals(externalItemSku.getSkuCode(),orderItem.getSkuCode())){
                orderItem.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
                orderItem.setShopOrderCode(warehouseOrder.getShopOrderCode());
                orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                //orderItem.setSupplierName(externalItemSku.getSupplierName());
                return orderItem;
            }
        }
        return null;
    }

    @Override
    public void setIjdService(IJDService ijdService) {
        this.ijdService = ijdService;
    }

    @Override
    @SupplierOrderCacheEvict
    public ResponseAck jdOrderSplitNotice(String orderInfo) {
        AssertUtil.notBlank(orderInfo, "京东订单拆分通知信息为空");
        JdOrderSplitParam jdOrderSplitParam = null;
        try {
            JSONObject orderObj = JSON.parseObject(orderInfo);
            jdOrderSplitParam = orderObj.toJavaObject(JdOrderSplitParam.class);
        } catch (JSONException e) {
            log.error("京东订单拆分子订单通知参数不是json格式", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), "京东订单拆分子订单通知参数不是json格式", "");
        } catch (ClassCastException e) {
            log.error("京东订单拆分子订单通知参数格式错误", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), "京东订单拆分子订单通知参数不是json格式", "");
        }
        //校验参数
        AssertUtil.notBlank(jdOrderSplitParam.getWarehouseOrderCode(), "京东订单拆分通知信息中仓库订单编码参数warehouseOrderCode为空");
        AssertUtil.notBlank(jdOrderSplitParam.getJdOrderCode(), "京东订单拆分通知信息中京东主订单编码参数jdOrderCode为空");
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(jdOrderSplitParam.getWarehouseOrderCode());
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("根据京东订单拆分通知信息中的仓库订单号%s查询仓库订单信息为空", jdOrderSplitParam.getWarehouseOrderCode()));
        AssertUtil.isTrue(warehouseOrder.getSupplierCode().equals(SupplyConstants.Order.SUPPLIER_JD_CODE), "京东订单拆分通知中的订单不是京东订单");
        //数据处理
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(jdOrderSplitParam.getWarehouseOrderCode());
        supplierOrderInfo.setStatus(ResponseAck.SUCCESS_CODE);
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        //处理订单物流信息
        if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
            for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
                handlerOrderLogisticsInfo(supplierOrderInfo2);
            }
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "接收京东订单拆分通知成功", "");
    }

    @Override
    @SupplierOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck orderSubmitResultNotice(String orderInfo) {
        AssertUtil.notBlank(orderInfo, "供应商订单下单结果通知信息为空");
        OrderSubmitResult orderSubmitResult = getOrderSubmitResult(orderInfo);
        AssertUtil.notBlank(orderSubmitResult.getWarehouseOrderCode(), "调供应商订单下单结果通知中仓库订单编码为空");
        AssertUtil.notBlank(orderSubmitResult.getOrderType(), "调供应商订单下单结果通知中订单类型为空");
        List<SupplierOrderReturn> supplierOrderReturnList = orderSubmitResult.getOrder();
        AssertUtil.notEmpty(supplierOrderReturnList, "调供应商订单下单结果通知中子订单信息为空");
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(orderSubmitResult.getWarehouseOrderCode());
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("根据仓库订单编码%s查询仓库订单信息为空", orderSubmitResult.getWarehouseOrderCode()));
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(orderSubmitResult.getWarehouseOrderCode());
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
        AssertUtil.notEmpty(supplierOrderInfoList, String.format("查询仓库编码为%s的仓库订单相关供应商订单信息为空", orderSubmitResult.getWarehouseOrderCode()));
        boolean success = false;
        for(SupplierOrderInfo supplierOrderInfo1: supplierOrderInfoList){
            if(StringUtils.equals(supplierOrderInfo1.getStatus(), ResponseAck.SUCCESS_CODE)) {//下单成功
                success = true;
                break;
            }
        }
        if(success){
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "接收供应商订单下单结果通知成功", "");
        }
        //更新供应商订单状态
        updateSupplierOrderInfo(supplierOrderInfoList,  supplierOrderReturnList);
        SupplierOrderInfo _supplierOrderInfo = new SupplierOrderInfo();
        _supplierOrderInfo.setWarehouseOrderCode(orderSubmitResult.getWarehouseOrderCode());
        List<SupplierOrderInfo> _supplierOrderInfoList = supplierOrderInfoService.select(_supplierOrderInfo);
        AssertUtil.notEmpty(_supplierOrderInfoList, String.format("查询仓库编码为%s的仓库订单相关供应商订单信息为空", orderSubmitResult.getWarehouseOrderCode()));
        //更新订单商品供应商订单状态
        updateOrderItemSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), _supplierOrderInfoList);
        //更新仓库订单供应商订单状态
        warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), true);
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "接收供应商订单下单结果通知成功", "");
    }


    /**
     * 更新供应商订单状态
     * @param supplierOrderInfoList
     * @param supplierOrderReturnList
     */
    private void updateSupplierOrderInfo(List<SupplierOrderInfo> supplierOrderInfoList, List<SupplierOrderReturn> supplierOrderReturnList){
        //先删除历史子订单数据
        for(SupplierOrderInfo supplierOrderInfo: supplierOrderInfoList){
            supplierOrderInfoService.deleteByPrimaryKey(supplierOrderInfo.getId());
        }
        //插入新返回的下单结果信息
        List<SupplierOrderInfo> newSupplierOrderInfoList = new ArrayList<>();
        SupplierOrderInfo supplierOrderInfo = supplierOrderInfoList.get(0);
        for(SupplierOrderReturn order: supplierOrderReturnList){
            SupplierOrderInfo _supplierOrderInfo = new SupplierOrderInfo();
            _supplierOrderInfo.setWarehouseOrderCode(supplierOrderInfo.getWarehouseOrderCode());
            _supplierOrderInfo.setSupplierCode(supplierOrderInfo.getSupplierCode());
            _supplierOrderInfo.setSupplierOrderCode(order.getSupplyOrderCode());
            _supplierOrderInfo.setJdCityCode(supplierOrderInfo.getJdCityCode());
            _supplierOrderInfo.setJdDistrictCode(supplierOrderInfo.getJdDistrictCode());
            _supplierOrderInfo.setJdProvinceCode(supplierOrderInfo.getJdProvinceCode());
            _supplierOrderInfo.setJdTownCode(supplierOrderInfo.getJdTownCode());
            _supplierOrderInfo.setJdCity(supplierOrderInfo.getJdCity());
            _supplierOrderInfo.setJdDistrict(supplierOrderInfo.getJdDistrict());
            _supplierOrderInfo.setJdProvince(supplierOrderInfo.getJdProvince());
            _supplierOrderInfo.setJdTown(supplierOrderInfo.getJdTown());
            _supplierOrderInfo.setLogisticsStatus(WarehouseOrderLogisticsStatusEnum.UN_COMPLETE.getCode());//未完成
            _supplierOrderInfo.setStatus(order.getState());
            if(StringUtils.equals(ResponseAck.SUCCESS_CODE, order.getState())){//供应商下单接口下单成功
                _supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode());//待发货
            }else{
                _supplierOrderInfo.setSupplierOrderStatus(SupplierOrderStatusEnum.ORDER_FAILURE.getCode());//下单失败
            }
            _supplierOrderInfo.setMessage(order.getMessage());
            _supplierOrderInfo.setSkus(JSON.toJSONString(order.getSkus()));
            ParamsUtil.setBaseDO(_supplierOrderInfo);
            _supplierOrderInfo.setCreateTime(supplierOrderInfo.getCreateTime());
            newSupplierOrderInfoList.add(_supplierOrderInfo);
        }
        supplierOrderInfoService.insertList(newSupplierOrderInfoList);
    }


    @Override
    //@Cacheable(key="#form.toString()+#aclUserAccreditInfo.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<ExceptionOrder> exceptionOrderPage(ExceptionOrderForm form, Pagenation<ExceptionOrder> page, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        AssertUtil.notNull(form, "查询拆单异常订单信息分页参数不能为空");
        Example example = new Example(ExceptionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }

        if(StringUtils.isNotBlank(form.getExceptionType())){//异常类型:1-缺货退回,2-缺货等待
            criteria.andEqualTo("exceptionType", form.getExceptionType());
        }
        if(StringUtils.isNotBlank(form.getStatus())){//状态:1-待了结,2-已了结
            criteria.andEqualTo("status", form.getStatus());
        }
        if(StringUtils.isNotBlank(form.getExceptionOrderCode())){//拆单异常单编号
            criteria.andLike("exceptionOrderCode", "%" + form.getExceptionOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getShopOrderCode())){//店铺订单编码
            criteria.andLike("shopOrderCode", "%" + form.getShopOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getScmShopOrderCode())){//系统订单号
            criteria.andLike("scmShopOrderCode", "%" + form.getScmShopOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getPlatformOrderCode())){//平台订单编码
            criteria.andLike("platformOrderCode", "%" + form.getPlatformOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getReceiverName())){//收货人姓名
            criteria.andLike("receiverName", "%" + form.getReceiverName() + "%");
        }
        if (StringUtil.isNotEmpty(form.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("createTime", DateUtils.parseDate(form.getStartDate()));
        }
        if (StringUtil.isNotEmpty(form.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(form.getEndDate());
            criteria.andLessThan("createTime", DateUtils.addDays(endDate, 1));
        }

        example.orderBy("status").asc();
        example.orderBy("createTime").desc();
        page = exceptionOrderService.pagination(example, page, form);
        orderExtBiz.setOrderSellName(page);
        return page;
    }

    @Override
    public ExceptionOrder queryExceptionOrdersDetail(String exceptionOrderCode) {
        AssertUtil.notBlank(exceptionOrderCode, "查询拆单异常订单明细参数拆单异常订单编码不能为空");
        ExceptionOrder exceptionOrder = new ExceptionOrder();
        exceptionOrder.setExceptionOrderCode(exceptionOrderCode);
        exceptionOrder = exceptionOrderService.selectOne(exceptionOrder);
        AssertUtil.notNull(exceptionOrder, String.format("根据拆单异常订单编码[%s]查询拆单异常订单为空",exceptionOrderCode));
        //查询平台订单
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(exceptionOrder.getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("根据平台订单编码[%s]查询平台订单为空", exceptionOrder.getPlatformOrderCode()));
        exceptionOrder.setPlatformOrder(platformOrder);
        //查询拆单异常订单商品明细
        ExceptionOrderItem exceptionOrderItem = new ExceptionOrderItem();
        exceptionOrderItem.setExceptionOrderCode(exceptionOrderCode);
        List<ExceptionOrderItem> exceptionOrderItemList = exceptionOrderItemService.select(exceptionOrderItem);
        AssertUtil.notEmpty(exceptionOrderItemList, String.format("根据拆单异常订单编码[%s]查询拆单异常订单商品明细为空",exceptionOrderCode));
        exceptionOrder.setExceptionOrderItemList(exceptionOrderItemList);
        return exceptionOrder;
    }

    @Override
    public void setiRealIpService(IRealIpService iRealIpService) {
        this.iRealIpService = iRealIpService;
    }

    @Override
    public void setTrcService(ITrcService trcService) {
        this.trcService = trcService;
    }

    @Override
    public ResponseAck handlerOrder(List<WarehouseOrder> warehouseOrders) throws Exception {
        if(CollectionUtils.isEmpty(warehouseOrders)){
            //提交粮油订单
            submitLiangYouOrders(warehouseOrders);
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "粮油订单提交成功", "");
    }

    @Override
    @SupplierOrderCacheEvict
    public ResponseAck outboundConfirmNotice(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "发货通知单发货明细确认通知参数仓库订单编码warehouseOrderCode不能为空");
        //更新仓库订单供应商订单状态
        WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrderCode, false);
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "通知接收成功", "");
    }

    @Override
    @OutboundOrderCacheEvict
    public ResponseAck submitSelfPurchaseOrder(List<WarehouseOrder> warehouseOrders, Map<String, List<SkuWarehouseDO>> skuWarehouseMap) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //提交自采订单
                            if(warehouseOrders.size() > 0){
                                try {
                                    invokeSubmitSelfPurchaseOrder(warehouseOrders, skuWarehouseMap);
                                } catch (Exception e) {
                                    log.error("提交自采订单异常", e);
                                }
                                //更新订单状态
                                updateOrderStatusByOutboundOrder(warehouseOrders);
                            }
                        }catch (Exception e){
                            log.error("提交自采订单失败", e);
                        }
                    }
                }
        ).start();

        return new ResponseAck(ResponseAck.SUCCESS_CODE, "提交自采订单成功", "");
    }


    /**
     * 提交自采订单
     * @param warehouseOrderList
     * @return
     */
    public ResponseAck invokeSubmitSelfPurchaseOrder(List<WarehouseOrder> warehouseOrderList, Map<String, List<SkuWarehouseDO>> skuWarehouseMap) throws Exception {
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(warehouseOrderList.get(0).getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("提交自采订单跟据平台订单编码%s查询平台订单信息为空", warehouseOrderList.get(0).getPlatformOrderCode()));
        Set<String> scmShopOrderCodes = new HashSet<>();
        Set<String> warehouseOrderCodes = new HashSet<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            scmShopOrderCodes.add(warehouseOrder.getScmShopOrderCode());
            warehouseOrderCodes.add(warehouseOrder.getWarehouseOrderCode());
        }
        Example shopOrderExample = new Example(ShopOrder.class);
        Example.Criteria criteria = shopOrderExample.createCriteria();
        criteria.andIn("scmShopOrderCode", scmShopOrderCodes);
        List<ShopOrder> shopOrderList = shopOrderService.selectByExample(shopOrderExample);
        for(String scmShopOrderCode : scmShopOrderCodes){
            boolean flag = false;
            for(ShopOrder shopOrder: shopOrderList){
                if(StringUtils.equals(scmShopOrderCode, shopOrder.getScmShopOrderCode())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("提交自采订单跟据系统订单号%s查询店铺订单信息为空", scmShopOrderCode));
            }
        }
        Example orderItemExample = new Example(OrderItem.class);
        Example.Criteria criteria2 = orderItemExample.createCriteria();
        criteria2.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<OrderItem> orderItemList = orderItemService.selectByExample(orderItemExample);
        AssertUtil.notEmpty(orderItemList, String.format("提交自采订单跟据仓库订单编码[%s]查询商品明细信息为空", CommonUtil.converCollectionToString(new ArrayList<>(warehouseOrderCodes))));
        Map<String, OutboundForm> outboundMap = new HashMap<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            ShopOrder _shopOrder = null;
            for(ShopOrder shopOrder: shopOrderList){
                if(StringUtils.equals(warehouseOrder.getShopOrderCode(), shopOrder.getShopOrderCode())){
                    _shopOrder = shopOrder;
                    break;
                }
            }
            List<OrderItem> orderItems = new ArrayList<>();
            for(Map.Entry<String, List<SkuWarehouseDO>> entry: skuWarehouseMap.entrySet()){
                List<SkuWarehouseDO> skuWarehouseDOList = entry.getValue();
                String skuCode = entry.getKey();
                for(SkuWarehouseDO skuWarehouseDO: skuWarehouseDOList){
                    if(StringUtils.equals(warehouseOrder.getWarehouseCode(), skuWarehouseDO.getWarehouseCode())){
                        boolean flag = false;
                        for(OrderItem orderItem: orderItemList){
                            if(StringUtils.equals(skuCode, orderItem.getSkuCode()) && StringUtils.equals(warehouseOrder.getWarehouseOrderCode(), orderItem.getWarehouseOrderCode())){
                                OrderItem _orderItem = orderItem;
                                _orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                                _orderItem.setNum(skuWarehouseDO.getItemNum().intValue());
                                orderItems.add(_orderItem);
                                flag = true;
                                break;
                            }
                        }
                        if(flag){
                            break;
                        }
                    }
                }
            }
            //创建发货通知单
            OutboundForm outboundForm  = createOutboundOrder(platformOrder, warehouseOrder, _shopOrder, orderItems, skuWarehouseMap);
            outboundMap.put(outboundForm.getOutboundOrder().getOutboundOrderCode(), outboundForm);
        }
        //通知仓库发货
        noticeWarehouseSendGoods(outboundMap);

        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
            /*if(IsStoreOrderEnum.NOT_STORE_ORDER.getCode().intValue() == outboundOrder.getIsStoreOrder().intValue()){//非门店订单
                //通知渠道发货结果 ......
                outboundOrderSubmitResultNoticeChannel(outboundOrder.getShopOrderCode());
            }*/
            //通知渠道发货结果 ......
            outboundOrderSubmitResultNoticeChannel(outboundOrder.getShopOrderCode());
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "提交自采订单成功", "");
    }

    /**
     * 自采商品发货结果通知渠道
     * @param shopOrderCodes 店铺订单列表
     * @param warehouseOrderList 仓库级订单列表
     */
/*    private void notifyChannelSelfPurchaseSubmitOrderResult(Set<String> shopOrderCodes, List<WarehouseOrder> warehouseOrderList) {
    	if (CollectionUtils.isEmpty(shopOrderCodes)
    			|| CollectionUtils.isEmpty(warehouseOrderList)) {
    		log.error("自采商品发货结果通知渠道异常:shopOrderCodeList或者warehouseOrderList为空");
    		return;
    	}
    	// 渠道平台订单编码
    	String platformOrderCode = warehouseOrderList.get(0).getPlatformOrderCode();
    	for (String shopOrderCode : shopOrderCodes) {
    		try {
    			*//**
    			 * 根据shopOrderCode筛选出warehouseOrderList
    			 **//*
    			List<WarehouseOrder> filterList = warehouseOrderList.stream()
    					.filter(order -> shopOrderCode.equals(order.getShopOrderCode())).collect(Collectors.toList());
    			*//**
    			 * 通知渠道数据封装
    			 * channelOrderResponse
    			 **//*
    			ChannelOrderResponse orderRes = new ChannelOrderResponse();
    			orderRes.setPlatformOrderCode(platformOrderCode);
    			orderRes.setShopOrderCode(shopOrderCode);
    			orderRes.setOrderType(SupplierOrderTypeEnum.ZC.getCode());

    			List<SupplierOrderReturn> orderList = new ArrayList<>();
    			for (WarehouseOrder order : filterList) {
    				OutboundOrder queryBoundOrder = new OutboundOrder();
    				queryBoundOrder.setWarehouseOrderCode(order.getWarehouseOrderCode());
    				OutboundOrder boundOrder = outBoundOrderService.selectOne(queryBoundOrder);
    				SupplierOrderReturn returnOrder = new SupplierOrderReturn();
    				returnOrder.setSupplyOrderCode(boundOrder.getOutboundOrderCode());
    				returnOrder.setState(getOutBundStatus(boundOrder.getStatus()));
    				//Map<String, String> returnMsgMap = new HashMap<>();
    				returnOrder.setSkus(generateSkuList(order.getWarehouseOrderCode(), shopOrderCode, platformOrderCode));
//    				if (StringUtils.isNotBlank(returnMsgMap.get("retMsg"))) {
//    					returnOrder.setMessage(returnMsgMap.get("retMsg"));
//    				}
    				orderList.add(returnOrder);
    			}

    	    	*//**
    	    	 * 获取异常skus，以单独一个异常订单通知给渠道
    	    	 **//*
    			generateExceptionOrder(shopOrderCode, platformOrderCode, orderList);

    			orderRes.setOrder(orderList);
    			noticeChannelOrderResult(orderRes);

    		} catch (Exception e) {
    			e.printStackTrace();
    			log.error("店铺订单: {}, 自采商品发货结果通知渠道异常:{}", shopOrderCode, e.getMessage());
    		}
    	}

    }*/

    /**
     * 获取异常skus，以单独一个异常订单通知给渠道
     * @param shopOrderCode 店铺级订单
     * @param platformOrderCode 渠道过来的平台订单
     * @param orderList  返回的订单列表-包括异常订单
     */
    private void generateExceptionOrder(String shopOrderCode, String platformOrderCode,
			List<SupplierOrderReturn> orderList) {
    	ExceptionOrderItem queryItem = new ExceptionOrderItem();
    	queryItem.setShopOrderCode(shopOrderCode);
    	queryItem.setPlatformOrderCode(platformOrderCode);
    	List<ExceptionOrderItem> itemList = exceptionOrderItemService.select(queryItem);
    	/**
    	 * 存在异常订单时，将所有sku的异常原因拼接到message字段中，返回给渠道
    	 */
    	if (!CollectionUtils.isEmpty(itemList)) {
    		List<SkuInfo> infoList = new ArrayList<>();
    		StringBuilder msg = new StringBuilder();
    		String exceptionOrderCode = "";
			for (ExceptionOrderItem item : itemList) {
				SkuInfo info = new SkuInfo();
				info.setSkuCode(item.getSkuCode());
				info.setNum(item.getItemNum());
				info.setSkuName(item.getItemName());
				exceptionOrderCode = item.getExceptionOrderCode();
				msg.append(item.getSkuCode());
				msg.append(":");
				msg.append(item.getExceptionReason());
				msg.append(",");
				infoList.add(info);
			}
    		String reMsg = msg.toString();
    		reMsg = reMsg.substring(0, reMsg.length() - 1);

			SupplierOrderReturn returnOrder = new SupplierOrderReturn();
			// 异常订单code
			returnOrder.setSupplyOrderCode(exceptionOrderCode);
			returnOrder.setState(NoticeChannelStatusEnum.FAILED.getCode());
			// 拼接后的异常信息
    		returnOrder.setMessage(reMsg);
    		returnOrder.setSkus(infoList);
    		orderList.add(returnOrder);
    	}
	}

	private List<SkuInfo> generateSkuList(String warehouseOrderCode, String shopOrderCode,
    		String platformOrderCode) {

		List<OutboundDetail> detailList = outboundDetailService.selectByWarehouseOrderCode(warehouseOrderCode);
//		AssertUtil.notEmpty(detailList,
//				String.format("根据仓库订单编码[%s]查询出货订单详情信息为空", warehouseOrderCode));
		List<SkuInfo> infoList = new ArrayList<>();
    	/**
    	 * 正常skus
    	 **/
		if (!CollectionUtils.isEmpty(detailList)) {
			for (OutboundDetail item : detailList) {
				SkuInfo info = new SkuInfo();
				info.setSkuCode(item.getSkuCode());
				info.setNum(item.getShouldSentItemNum().intValue());
				info.setSkuName(item.getSkuName());
				infoList.add(info);

			}
		}

		return infoList;
    }

    private String getOutBundStatus(String originStaus) {
    	return OutboundOrderStatusEnum.RECEIVE_FAIL.getCode().equals(originStaus) ?
    			NoticeChannelStatusEnum.FAILED.getCode() : NoticeChannelStatusEnum.SUCCESS.getCode();
    }

    /**
     * 根据发货通知单更新订单状态
     * @param warehouseOrderList
     */
    private void updateOrderStatusByOutboundOrder(List<WarehouseOrder> warehouseOrderList){
        List<String> warehouseOrderCodes = new ArrayList<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            warehouseOrderCodes.add(warehouseOrder.getWarehouseOrderCode());
        }
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<OutboundOrder> outboundOrderList = outBoundOrderService.selectByExample(example);
        AssertUtil.notEmpty(outboundOrderList, String.format("根据仓库订单编码[%s]查询发货通知信息为空", CommonUtil.converCollectionToString(warehouseOrderCodes)));
        List<String> outboundOrderCodes = new ArrayList<>();
        for(OutboundOrder outboundOrder: outboundOrderList){
            outboundOrderCodes.add(outboundOrder.getOutboundOrderCode());
        }
        Example example2 = new Example(OutboundDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("outboundOrderCode", outboundOrderCodes);
        List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example2);
        AssertUtil.notEmpty(outboundDetailList, String.format("根据发货单编码[%s]查询发货通知明细信息为空", CommonUtil.converCollectionToString(outboundOrderCodes)));
        Example example3 = new Example(OrderItem.class);
        Example.Criteria criteria3 = example3.createCriteria();
        criteria3.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<OrderItem> orderItemList = orderItemService.selectByExample(example3);
        AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单编码[%s]查询渠道订单商品明细信息为空", CommonUtil.converCollectionToString(warehouseOrderCodes)));
        //更新订单商品明细
        Date currentDate = Calendar.getInstance().getTime();
        for(OutboundDetail outboundDetail: outboundDetailList){
            OutboundOrder outboundOrder = null;
            for(OutboundOrder _outboundOrder: outboundOrderList){
                if(StringUtils.equals(outboundDetail.getOutboundOrderCode(), _outboundOrder.getOutboundOrderCode())){
                    outboundOrder = _outboundOrder;
                    break;
                }
            }
            for(OrderItem orderItem: orderItemList){
                if(StringUtils.equals(outboundDetail.getSkuCode(), orderItem.getSkuCode()) &&
                        StringUtils.equals(orderItem.getWarehouseOrderCode(), outboundOrder.getWarehouseOrderCode())){
                    OrderItemDeliverStatusEnum orderItemDeliverStatusEnum = getOrderItemDeliverStatusEnumByOutboundStatus(outboundDetail.getStatus());
                    orderItem.setSupplierOrderStatus(orderItemDeliverStatusEnum.getCode());
                    orderItem.setUpdateTime(currentDate);
                    orderItemService.updateByPrimaryKeySelective(orderItem);
                    break;
                }
            }
        }
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            //更新仓库订单供应商订单状态
            warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), false);
            //更新店铺订单供应商订单状态
            updateShopOrderSupplierOrderStatus(warehouseOrder.getScmShopOrderCode(), warehouseOrder.getShopOrderCode());
        }

    }


    /**
     * 根据发货单状态获取订单商品状态
     * @param outboundStatus
     * @return
     */
    private OrderItemDeliverStatusEnum getOrderItemDeliverStatusEnumByOutboundStatus(String outboundStatus){
        if(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.WAREHOUSE_RECIVE_FAILURE;
        }else if(OutboundDetailStatusEnum.WAITING.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER;
        }else if(OutboundDetailStatusEnum.ON_WAREHOUSE_NOTICE.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.WAREHOUSE_MIDDEL_STATUS;
        }else if(OutboundDetailStatusEnum.ALL_GOODS.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.ALL_DELIVER;
        }else if(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.PARTS_DELIVER;
        }else if(OutboundDetailStatusEnum.CANCELED.getCode().equals(outboundStatus)){
            return OrderItemDeliverStatusEnum.ORDER_CANCEL;
        }
        return null;
    }


    /**
     * 更新订单商品占用库存
     * @param skuWarehouseMap
     * @throws Exception
     */
    private void frozenOrderInventory(Map<String, List<SkuWarehouseDO>> skuWarehouseMap) throws Exception {
        List<RequsetUpdateStock> updateStockList = new ArrayList<RequsetUpdateStock>();
        Set<Map.Entry<String, List<SkuWarehouseDO>>> entries = skuWarehouseMap.entrySet();
        for(Map.Entry<String, List<SkuWarehouseDO>> entry: entries){
            List<SkuWarehouseDO> skuWarehouseDOs = entry.getValue();
            for(SkuWarehouseDO skuWarehouseDO: skuWarehouseDOs){
                RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
                Map<String, String> stockType = new HashMap<String, String>();
                stockType.put("frozen_inventory", skuWarehouseDO.getItemNum().toString());
                requsetUpdateStock.setStockType(stockType);
                requsetUpdateStock.setChannelCode(skuWarehouseDO.getChannelCode());
                requsetUpdateStock.setWarehouseCode(skuWarehouseDO.getWarehouseCode());
                requsetUpdateStock.setSkuCode(skuWarehouseDO.getSkuCode());
                updateStockList.add(requsetUpdateStock);
            }
        }
        if(updateStockList.size() > 0){
            //更新库存
            skuStockService.updateSkuStock(updateStockList);
        }

    }

    /**
     * 创建出库通知单
     * @param platformOrder
     * @param warehouseOrder
     * @param orderItemList
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    OutboundForm createOutboundOrder(PlatformOrder platformOrder, WarehouseOrder warehouseOrder, ShopOrder shopOrder,
                                     List<OrderItem> orderItemList, Map<String, List<SkuWarehouseDO>> skuWarehouseMap){
        OutboundOrder outboundOrder = new OutboundOrder();
        //流水号
        String code = serialUtilService.generateCode(SupplyConstants.Serial.OUTBOUND_ORDER_LENGTH,
                SupplyConstants.Serial.OUTBOUND_ORDER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        outboundOrder.setScmShopOrderCode(warehouseOrder.getScmShopOrderCode());
        outboundOrder.setChannelCode(shopOrder.getChannelCode());
        outboundOrder.setSellCode(shopOrder.getSellCode());
        outboundOrder.setOutboundOrderCode(code);
        outboundOrder.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        outboundOrder.setWarehouseCode(warehouseOrder.getWarehouseCode());
        outboundOrder.setShopId(shopOrder.getShopId());
        outboundOrder.setShopName(shopOrder.getShopName());
        outboundOrder.setShopOrderCode(shopOrder.getShopOrderCode());
        outboundOrder.setWarehouseId(warehouseOrder.getWarehouseId());
        outboundOrder.setOrderType(QimenOrderTypeEnum.JYCK.getCode());
        if(warehouseOrder.getIsStoreOrder()){//门店订单
            outboundOrder.setIsStoreOrder(IsStoreOrderEnum.STORE_ORDER.getCode());
            outboundOrder.setStatus(OutboundOrderStatusEnum.ALL_GOODS.getCode());
        }else {
            outboundOrder.setIsStoreOrder(IsStoreOrderEnum.NOT_STORE_ORDER.getCode());
            outboundOrder.setStatus(OutboundOrderStatusEnum.WAITING.getCode());
        }

        int itemNum = 0;
        for(OrderItem orderItem: orderItemList){
            itemNum += orderItem.getNum();
        }
        outboundOrder.setItemNum(itemNum);
        outboundOrder.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
        outboundOrder.setPayTime(platformOrder.getPayTime());
        outboundOrder.setReceiverProvince(platformOrder.getReceiverProvince());
        outboundOrder.setReceiverCity(platformOrder.getReceiverCity());
        outboundOrder.setReceiverDistrict(platformOrder.getReceiverDistrict());
        outboundOrder.setReceiverAddress(platformOrder.getReceiverAddress());
        outboundOrder.setReceiverZip(platformOrder.getReceiverZip());
        outboundOrder.setReceiverName(platformOrder.getReceiverName());
        if(StringUtils.isNotBlank(platformOrder.getReceiverMobile())){
            outboundOrder.setReceiverPhone(platformOrder.getReceiverMobile());
        }
        if(StringUtils.isBlank(outboundOrder.getReceiverPhone()) && StringUtils.isNotBlank(platformOrder.getReceiverPhone())){
            outboundOrder.setReceiverPhone(platformOrder.getReceiverPhone());
        }
        outboundOrder.setBuyerMessage(shopOrder.getBuyerMessage());
        outboundOrder.setSellerMessage(shopOrder.getShopMemo());
        Date currentTime = Calendar.getInstance().getTime();
        outboundOrder.setCreateTime(currentTime);
        outboundOrder.setUpdateTime(currentTime);

        List<OutboundDetail> outboundDetailList = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            OutboundDetail outboundDetail = new OutboundDetail();
            outboundDetail.setOutboundOrderCode(code);
            outboundDetail.setSkuName(orderItem.getItemName());
            outboundDetail.setSkuCode(orderItem.getSkuCode());
            outboundDetail.setInventoryType(InventoryTypeEnum.ZP.getCode());
            outboundDetail.setActualAmount(CommonUtil.getMoneyLong(orderItem.getPayment()));
            outboundDetail.setShouldSentItemNum(orderItem.getNum().longValue());
            if(warehouseOrder.getIsStoreOrder()){//门店订单
                outboundDetail.setStatus(OutboundDetailStatusEnum.ALL_GOODS.getCode());
                outboundDetail.setRealSentItemNum(outboundDetail.getShouldSentItemNum());
            }else {
                outboundDetail.setStatus(OutboundDetailStatusEnum.WAITING.getCode());
            }
            outboundDetail.setCreateTime(currentTime);
            outboundDetail.setUpdateTime(currentTime);
            //商品规格
            Example example = new Example(Skus.class);
            example.createCriteria().andEqualTo("skuCode", orderItem.getSkuCode()).andEqualTo("isDeleted", "0");
            List<Skus> skuses = skusService.selectByExample(example);
            log.info("创建出库通知单 商品规格 ---- skusList size:{}, skuCode:{}", skuses.isEmpty() ? 0 : skuses.size(), orderItem.getSkuCode());
            if(!skuses.isEmpty() && skuses.size() < 2){
                outboundDetail.setSpecNatureInfo(skuses.get(0).getSpecInfo());
            }
            List<SkuWarehouseDO> warehouseDOList = skuWarehouseMap.get(outboundDetail.getSkuCode());
            if(!CollectionUtils.isEmpty(warehouseDOList)){
                outboundDetail.setWarehouseItemId(warehouseDOList.get(0).getItemId());
            }
            outboundDetailList.add(outboundDetail);
        }

        outBoundOrderService.insert(outboundOrder);
        outboundDetailService.insertList(outboundDetailList);

        //记录操作日志
        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(), SYSTEM, LogOperationEnum.CREATE.getMessage(), null,null);

        OutboundForm outboundForm = new OutboundForm();
        outboundForm.setOutboundOrder(outboundOrder);
        outboundForm.setOutboundDetailList(outboundDetailList);
        return outboundForm;
    }


    /**
     * 通知仓库发货
     * @param outboundMap
     */
    private void noticeWarehouseSendGoods(Map<String, OutboundForm> outboundMap){
        //调用仓库接口创建发货单
        AppResult<List<ScmDeliveryOrderCreateResponse>> appResult = deliveryOrderCreate(outboundMap, false);
        //更新发货单状态
        updateOutboudOrderStatus(outboundMap, appResult);
    }

    private void updateOutboudOrderStatus(Map<String, OutboundForm> outboundMap, AppResult<List<ScmDeliveryOrderCreateResponse>> appResult){
        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        List<String> outboundCodes = new ArrayList<>();
        List<String> warehouseCodes = new ArrayList<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            outboundCodes.add(entry.getKey());
            warehouseCodes.add(entry.getValue().getOutboundOrder().getWarehouseCode());
        }
        List<ScmDeliveryOrderCreateResponse> successOutbound = new ArrayList<>();//成功的发货单
        List<ScmDeliveryOrderCreateResponse> failureOutbound = new ArrayList<>();//失败的发货单
        if(StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            List<ScmDeliveryOrderCreateResponse> scmDeliveryOrderCreateResponseList = (List<ScmDeliveryOrderCreateResponse>)appResult.getResult();
            for(ScmDeliveryOrderCreateResponse response: scmDeliveryOrderCreateResponseList){
                if(StringUtils.equals(ResponseAck.SUCCESS_CODE, response.getCode())){
                    successOutbound.add(response);
                }else{
                    failureOutbound.add(response);
                }
            }
        }else{
            for(String outboundCode: outboundCodes){
                ScmDeliveryOrderCreateResponse response = new ScmDeliveryOrderCreateResponse();
                response.setCode(appResult.getAppcode());
                response.setDeliveryOrderCode(outboundCode);
                response.setMessage("调用仓库创建发货单接口返回结果数据为空");
                failureOutbound.add(response);
            }
        }
        if(failureOutbound.size() > 0){
            for(ScmDeliveryOrderCreateResponse response: failureOutbound){
                updateOutboundOrderAfterCreate(OutboundOrderStatusEnum.RECEIVE_FAIL, response.getDeliveryOrderCode(), null, response.getMessage());
            }
        }
        if(successOutbound.size() > 0){
            for(ScmDeliveryOrderCreateResponse response: successOutbound){
                for(Map.Entry<String, OutboundForm> entry: entries){
                    OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
                    if(StringUtils.equals(response.getDeliveryOrderCode(), outboundOrder.getOutboundOrderCode())){
                        OutboundOrderStatusEnum outboundOrderStatusEnum = OutboundOrderStatusEnum.WAITING;
                        if(IsStoreOrderEnum.STORE_ORDER.getCode().intValue() == outboundOrder.getIsStoreOrder().intValue()){//门店订单
                            outboundOrderStatusEnum = OutboundOrderStatusEnum.ALL_GOODS;
                        }
                        updateOutboundOrderAfterCreate(outboundOrderStatusEnum, response.getDeliveryOrderCode(), response.getWmsOrderCode(), response.getMessage());
                    }
                }
            }
        }
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouseCodes);
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        AssertUtil.notEmpty(warehouseInfoList, String.format("查询仓库%s信息为空", CommonUtil.converCollectionToString(warehouseCodes)));
        String operator = SYSTEM;
        //记录操作日志
        if(failureOutbound.size() > 0){
            //失败日志
            for(ScmDeliveryOrderCreateResponse order: failureOutbound){
                for(Map.Entry<String, OutboundForm> entry: entries){
                    if(StringUtils.equals(order.getDeliveryOrderCode(), entry.getKey())){
                        OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
                        for(WarehouseInfo warehouse: warehouseInfoList){
                            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouse.getCode())){
                                operator = warehouse.getWarehouseName();
                            }
                        }
                        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(), operator, LogOperationEnum.OUTBOUND_RECEIVE_FAIL.getMessage(), order.getMessage(),null);
                    }
                }
            }
        }

        if(successOutbound.size() > 0){
            //成功日志
            for(ScmDeliveryOrderCreateResponse order: successOutbound){
                for(Map.Entry<String, OutboundForm> entry: entries){
                    if(StringUtils.equals(order.getDeliveryOrderCode(), entry.getKey())){
                        OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
                        for(WarehouseInfo warehouse: warehouseInfoList){
                            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouse.getCode())){
                                operator = warehouse.getWarehouseName();
                            }
                        }
                        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(), operator, LogOperationEnum.OUTBOUND_RECEIVE_SUCCESS.getMessage(), "",null);
                    }
                }
            }
        }
    }

    /**
     * 调用仓库接口创建发货通知单后更新发货通知单状态
     * @param outboundOrderStatusEnum
     * @param outboundOrderCode
     */

    private void updateOutboundOrderAfterCreate(OutboundOrderStatusEnum outboundOrderStatusEnum, String outboundOrderCode, String wmsOrderCode, String message){
        Date currentTime = Calendar.getInstance().getTime();
        //更新发货通知单状态
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outboundOrderCode", outboundOrderCode);
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setWmsOrderCode(wmsOrderCode);
        outboundOrder.setStatus(outboundOrderStatusEnum.getCode());
        outboundOrder.setMessage(message);
        outboundOrder.setUpdateTime(currentTime);
        outBoundOrderService.updateByExampleSelective(outboundOrder, example);
        //更新发货通知单明细状态
        Example example2 = new Example(OutboundDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("outboundOrderCode", outboundOrderCode);
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setStatus(outboundOrderStatusEnum.getCode());
        outboundDetail.setUpdateTime(currentTime);
        outboundDetailService.updateByExampleSelective(outboundDetail, example2);
    }

    /**
     *获取调用奇门批量创建发货单接口失败结果信息
     * @param outboundOrderCodes
     * @param failureMsg
     * @return
     */
    DeliveryorderBatchcreateResponse getFailureDeliveryorderBatchcreateResponse(List<String> outboundOrderCodes, String failureMsg){
        DeliveryorderBatchcreateResponse response = new DeliveryorderBatchcreateResponse();
        response.setFlag(SupplyConstants.Qimen.QIMEN_RESPONSE_FAILURE_FLAG);
        response.setMessage(failureMsg);
        List<DeliveryorderBatchcreateResponse.Order> orders = new ArrayList<>();
        for(String outboundOrderCode: outboundOrderCodes){
            DeliveryorderBatchcreateResponse.Order order = new DeliveryorderBatchcreateResponse.Order();
            order.setDeliveryOrderCode(outboundOrderCode);
            order.setMessage(failureMsg);
            orders.add(order);
        }
        response.setOrders(orders);
        return response;
    }





    /**
     * 调用奇门创建发货单接口(批量)
     * @param orderList
     * @return
     */
    private DeliveryorderBatchcreateResponse invokeDeliveryorderBatchcreate(List<DeliveryorderBatchcreateRequest.Order> orderList){
        DeliveryorderBatchcreateRequest request = new DeliveryorderBatchcreateRequest();
        request.setOrders(orderList);
        AppResult appResult = qimenService.deliveryorderBatchcreate(request);
        /*if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("调用奇门创建发货单接口(批量)接口失败, %s", appResult.getDatabuffer()));
        }
        AssertUtil.notNull(appResult.getResult(), "调用奇门创建发货单接口(批量)接口返回结果数据为空");
        AssertUtil.notBlank(appResult.getResult().toString(), "调用奇门创建发货单接口(批量)接口返回结果数据为空");*/
        if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            return null;
        }
        DeliveryorderBatchcreateResponse deliveryorderBatchcreateResponse = null;
        try{
            deliveryorderBatchcreateResponse = JSON.parseObject(appResult.getResult().toString()).toJavaObject(DeliveryorderBatchcreateResponse.class);
        }catch (ClassCastException e) {
            String msg = String.format("调用奇门创建发货单接口(批量)接口返回库存结果信息格式错误,%s", e.getMessage());
            log.error(msg, e);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        return deliveryorderBatchcreateResponse;
    }

    /**
     * 获取发货单主信息
     * @param outboundOrder
     * @param warehouseList
     * @return
     */
    private DeliveryorderBatchcreateRequest.DeliveryOrder getDeliveryOrder(OutboundOrder outboundOrder, List<WarehouseInfo> warehouseList){
        //发货单信息
        DeliveryorderBatchcreateRequest.DeliveryOrder deliveryOrder = new DeliveryorderBatchcreateRequest.DeliveryOrder();
        deliveryOrder.setSourcePlatformCode(SupplyConstants.SourcePlatformCodeType.OTHER);
        deliveryOrder.setDeliveryOrderCode(outboundOrder.getOutboundOrderCode());
        deliveryOrder.setOrderType(QimenOrderTypeEnum.JYCK.getCode());
        deliveryOrder.setWarehouseCode(outboundOrder.getWarehouseCode());
        deliveryOrder.setShopNick(outboundOrder.getShopName());
        deliveryOrder.setLogisticsCode(SupplyConstants.QimenLogisticsCompanyCode.YTO);
        deliveryOrder.setPayTime(DateUtils.dateToNormalFullString(outboundOrder.getPayTime()));
        deliveryOrder.setPlaceOrderTime(DateUtils.dateToNormalFullString(outboundOrder.getCreateTime()));
        deliveryOrder.setOperateTime(DateUtils.dateToNormalFullString(outboundOrder.getCreateTime()));
        deliveryOrder.setCreateTime(DateUtils.dateToNormalFullString(outboundOrder.getCreateTime()));
        deliveryOrder.setBuyerMessage(outboundOrder.getBuyerMessage());
        deliveryOrder.setSellerMessage(outboundOrder.getSellerMessage());
        //发件人信息
        DeliveryorderBatchcreateRequest.SenderInfo senderInfo = new DeliveryorderBatchcreateRequest.SenderInfo();
        for(WarehouseInfo warehouse: warehouseList){
            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouse.getCode())){
                senderInfo.setName(warehouse.getWarehouseName());
                senderInfo.setMobile(warehouse.getSenderPhoneNumber());
                senderInfo.setProvince(warehouse.getProvince());
                senderInfo.setCity(warehouse.getCity());
                senderInfo.setDetailAddress(warehouse.getAddress());
                break;
            }
        }

        //收件人信息
        DeliveryorderBatchcreateRequest.ReceiverInfo receiverInfo = new DeliveryorderBatchcreateRequest.ReceiverInfo();
        receiverInfo.setName(outboundOrder.getReceiverName());
        receiverInfo.setMobile(outboundOrder.getReceiverPhone());
        receiverInfo.setProvince(outboundOrder.getReceiverProvince());
        receiverInfo.setCity(outboundOrder.getReceiverCity());
        receiverInfo.setDetailAddress(outboundOrder.getReceiverAddress());
        deliveryOrder.setSenderInfo(senderInfo);
        deliveryOrder.setReceiverInfo(receiverInfo);
        return deliveryOrder;
    }

    /**
     * 获取发货单明细信息
     * @param outboundOrder
     * @param outboundDetailList
     * @param warehouseInfoList
     * @return
     */
    private List<DeliveryorderBatchcreateRequest.OrderLine> getDeliveryOrderLines(OutboundOrder outboundOrder, List<OutboundDetail> outboundDetailList, List<WarehouseInfo> warehouseInfoList){
        List<DeliveryorderBatchcreateRequest.OrderLine> orderLineList = new ArrayList<>();
        for(OutboundDetail outboundDetail: outboundDetailList){
            DeliveryorderBatchcreateRequest.OrderLine orderLine = new DeliveryorderBatchcreateRequest.OrderLine();
            for(WarehouseInfo warehouseInfo: warehouseInfoList){
                if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouseInfo.getCode()) &&
                        StringUtils.equals(outboundOrder.getChannelCode(), warehouseInfo.getChannelCode())){
                    orderLine.setOwnerCode(warehouseInfo.getWarehouseOwnerId());
                }
            }
            orderLine.setItemCode(outboundDetail.getSkuCode());
            orderLine.setInventoryType(InventoryTypeEnum.ZP.getCode());
            orderLine.setPlanQty(String.valueOf(outboundDetail.getShouldSentItemNum()));
            orderLine.setActualPrice(String.valueOf(CommonUtil.fenToYuan(outboundDetail.getActualAmount())));
            orderLineList.add(orderLine);
        }
        return orderLineList;
    }


    @Override
    @SupplierOrderCacheEvict
    public Response importOrder(String sellCode, InputStream uploadedInputStream, FormDataContentDisposition fileDetail, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(sellCode, "销售渠道编码不能为空");
        AssertUtil.notNull(uploadedInputStream, "上传文件不能为空");
        AssertUtil.notBlank(aclUserAccreditInfo.getUserId(), "当前操作用户信息为空");
        String fileName = fileDetail.getFileName();
        AssertUtil.notBlank(fileName, "上传文件名称不能为空");
        WarehouseItemInfoExceptionResult result = new WarehouseItemInfoExceptionResult();
        try {
            //检测是否是excel
            String suffix = fileName.substring(fileName.lastIndexOf(SupplyConstants.Symbol.FILE_NAME_SPLIT) + 1);
            if (!(suffix.toLowerCase().equals(XLSX) || suffix.toLowerCase().equals(XLS))) {
                return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入文件格式不支持", "");
            }
            //校验导入文件抬头信息
            String[] titleResult = null;
            try{
                titleResult = ImportExcel.readExcelTitle(uploadedInputStream);
            }catch(Exception e){
                return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入模板错误!", "");
            }
            //校验导入表格标题
            checkTitle(titleResult);
            //校验导入文件信息，并获取信息
            Map<String, String> contentResult = null;
            try{
                contentResult = ImportExcel.readExcelContent2(uploadedInputStream, SupplyConstants.Symbol.COMMA);
                if(StringUtils.equals("0", contentResult.get("count").toString())){
                    return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入附件不能为空！", "");
                }
            }catch (Exception e){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "导入订单数据包含不合规范的数据！请仔细检查数据确认完全正确后在提交！");
            }

            int count = Integer.parseInt(contentResult.get("count"));
            if(count > 500){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "每次导入订单数据不能超过500条！");
            }
            SellChannel sellChannel = new SellChannel();
            sellChannel.setSellCode(sellCode);
            sellChannel = sellChannelService.selectOne(sellChannel);
            AssertUtil.notNull(sellChannel, String.format("销售渠道%s不存在", sellCode));
            //获取导入订单sku明细
            List<ImportOrderInfo> importOrderInfoList = getImportOrderSkuDetail(aclUserAccreditInfo.getChannelCode(), sellCode, titleResult, contentResult, sellChannel);
            //校验是否包含了代发商品
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                if(importOrderInfo.getSkuCode().startsWith(SP1)){
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "暂不支持导入代发商品！");
                }
            }
            //检查导入订单是否重复导入
            //checkOrderRepeat(importOrderInfoList);
            Set<String> skuCodes = new HashSet<>();
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                skuCodes.add(importOrderInfo.getSkuCode());
            }
            List<Skus> skusList = getImportSkus(skuCodes);
            //List<ExternalItemSku> externalItemSkuList = getImportExternalSkus(skuCodes);
            //设置商品信息
            setImportSkuInfo(importOrderInfoList, skusList);
            //校验导入订单商品是否供应链商品
            //isScmItems2(importOrderInfoList, externalItemSkuList);
            //获取导入订单的店铺订单
            List<ShopOrder> shopOrderList = getImportShopOrders(importOrderInfoList);
            //获取导入订单的平台订单
            List<PlatformOrder> platformOrderList = getImportPlatformOrders(shopOrderList, importOrderInfoList);
            //订单处理
            String importOrderCode = processImportOrder(platformOrderList, shopOrderList, importOrderInfoList, aclUserAccreditInfo.getUserId());
            //处理返回结果
            int successCount = 0;//导入成功数
            int failCount = 0;//导入失败数
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                if(importOrderInfo.getFlag()){
                    successCount++;
                }else{
                    failCount++;
                }
            }
            ImportOrderResult importOrderResult = new ImportOrderResult(importOrderCode, successCount, failCount);
            if(failCount > 0){
                return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "订单导入失败", importOrderResult);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), msg, "");
        }
        return ResultUtil.createSuccessResult("订单导入完成", result);
    }


    /**
     * 检查导入订单是否重复导入
     * @param importOrderInfoList
     */
    /*private void checkOrderRepeat(List<ImportOrderInfo> importOrderInfoList){
       Set<String> channelCodes = new HashSet<>();
       Set<String> sellCodes = new HashSet<>();
       Set<String> shopOrderCodes = new HashSet<>();
       for(ImportOrderInfo importOrderInfo: importOrderInfoList){
           channelCodes.add(importOrderInfo.getChannelCode());
           sellCodes.add(importOrderInfo.getSellCode());
           shopOrderCodes.add(importOrderInfo.getShopOrderCode());
       }
       Example example = new Example(ShopOrder.class);
       Example.Criteria criteria = example.createCriteria();
       criteria.andIn("channelCode", channelCodes);
       criteria.andIn("sellCode", sellCodes);
       criteria.andIn("shopOrderCode", shopOrderCodes);
       List<ShopOrder> shopOrderList = shopOrderService.selectByExample(example);
       for(ImportOrderInfo orderInfo: importOrderInfoList){
           for(ShopOrder shopOrder: shopOrderList){
               if(StringUtils.equals(orderInfo.getChannelCode(), shopOrder.getChannelCode()) &&
                       StringUtils.equals(orderInfo.getSellCode(), shopOrder.getSellCode()) &&
                       StringUtils.equals(orderInfo.getShopOrderCode(), shopOrder.getShopOrderCode())){
                   orderInfo.setFlag(false);
                   setImportOrderErrorMsg(orderInfo, "该订单已导入成功，暂不支持重复导入");
                   break;
               }
           }
       }
    }*/

    private void setImportSkuInfo(List<ImportOrderInfo> importOrderInfoList, List<Skus> skusList){
        if(!CollectionUtils.isEmpty(skusList)){
            Set<String> spuCodes = new HashSet<>();
            for(Skus skus: skusList){
                spuCodes.add(skus.getSpuCode());
            }
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                boolean flag = false;
                for(Skus skus: skusList){
                    if(StringUtils.equals(importOrderInfo.getSkuCode(), skus.getSkuCode())){
                        importOrderInfo.setSkuName(skus.getSkuName());
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    importOrderInfo.setFlag(false);
                    setImportOrderErrorMsg(importOrderInfo, "商品SKU编号不存在");
                }
            }
        }else{
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                importOrderInfo.setFlag(false);
                setImportOrderErrorMsg(importOrderInfo, "商品SKU编号不存在");
            }
        }
        /*if(!CollectionUtils.isEmpty(externalItemSkuList)){
            for(ImportOrderInfo importOrderInfo: importOrderInfoList){
                boolean flag = false;
                for(ExternalItemSku externalItemSku: externalItemSkuList){
                    if(StringUtils.equals(importOrderInfo.getSkuCode(), externalItemSku.getSkuCode())){
                        importOrderInfo.setSkuName(externalItemSku.getItemName());
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    importOrderInfo.setFlag(false);
                    setImportOrderErrorMsg(importOrderInfo, "商品SKU编号不存在");
                }
            }
        }*/
    }

    private List<Skus> getImportSkus(Set<String> skuCodes){
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        return skusService.selectByExample(example);
    }

    private List<ExternalItemSku> getImportExternalSkus(Set<String> skuCodes){
        Example example2 = new Example(ExternalItemSku.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("skuCode", skuCodes);
        return externalItemSkuService.selectByExample(example2);
    }

    @Override
    public Response downloadErrorOrder(String orderCode) {
        AssertUtil.notBlank(orderCode, "错误订单编码不能为空");
        try {
            ImportOrderInfo importOrderInfo = new ImportOrderInfo();
            importOrderInfo.setImportOrderCode(orderCode);
            importOrderInfo.setIsFail(ZeroToNineEnum.ONE.getCode());//失败的订单
            List<ImportOrderInfo> list = importOrderInfoService.select(importOrderInfo);
            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            CellDefinition shopOrderCode = new CellDefinition("shopOrderCode", SHOP_ORDER_CODE, CellDefinition.TEXT, null, 4000);
            CellDefinition orignalPaytimeStr = new CellDefinition("orignalPaytimeStr", PAY_TIME, CellDefinition.TEXT, null,5000);
            CellDefinition receiverName = new CellDefinition("receiverName", RECIVE_NAME, CellDefinition.TEXT, null,3000);
            CellDefinition receiverMobile = new CellDefinition("receiverMobile", RECIVE_MOBILE, CellDefinition.TEXT, null,4000);
            CellDefinition receiverProvince = new CellDefinition("receiverProvince", RECIVE_PROVINCE, CellDefinition.TEXT, null,3000);
            CellDefinition receiverCity = new CellDefinition("receiverCity", RECIVE_CITY, CellDefinition.TEXT, null,3000);
            CellDefinition receiverDistrict = new CellDefinition("receiverDistrict", RECIVE_DISTRICT, CellDefinition.TEXT, null,3000);
            CellDefinition receiverAddress = new CellDefinition("receiverAddress", RECIVE_ADDRESS, CellDefinition.TEXT, null,8000);
            CellDefinition skuCode = new CellDefinition("skuCode", SKU_CODE, CellDefinition.TEXT, null,5000);
            CellDefinition num = new CellDefinition("num", NUM, CellDefinition.TEXT, null,4000);
            CellDefinition price = new CellDefinition("price", PRICE, CellDefinition.TEXT, null,5000);
            CellDefinition payment = new CellDefinition("payment", PAYMENT, CellDefinition.TEXT, null,4000);
            CellDefinition postFee = new CellDefinition("postFee", POST_FEE, CellDefinition.TEXT, null,4000);
            CellDefinition priceTax = new CellDefinition("priceTax", PRICE_TAX, CellDefinition.TEXT, null,4000);
            CellDefinition buyerMessage = new CellDefinition("buyerMessage", BUYER_MESSAGE, CellDefinition.TEXT, null,10000);
            CellDefinition shopMemo = new CellDefinition("shopMemo", SHOP_MEMO, CellDefinition.TEXT, null,10000);
            CellDefinition errorMessage = new CellDefinition("errorMessage", ERROR_MESSAGE, CellDefinition.TEXT, new HSSFColor.RED(), 10000);
            cellDefinitionList.add(shopOrderCode);
            cellDefinitionList.add(orignalPaytimeStr);
            cellDefinitionList.add(receiverName);
            cellDefinitionList.add(receiverMobile);
            cellDefinitionList.add(receiverProvince);
            cellDefinitionList.add(receiverCity);
            cellDefinitionList.add(receiverDistrict);
            cellDefinitionList.add(receiverAddress);
            cellDefinitionList.add(skuCode);
            cellDefinitionList.add(num);
            cellDefinitionList.add(price);
            cellDefinitionList.add(payment);
            cellDefinitionList.add(postFee);
            cellDefinitionList.add(priceTax);
            cellDefinitionList.add(buyerMessage);
            cellDefinitionList.add(shopMemo);
            cellDefinitionList.add(errorMessage);

            String sheetName = "导入失败订单";

            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(list, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);

            String fileName = String.valueOf(System.nanoTime())+ SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS;
            return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            log.error("订单导入错误下载异常" + e.getMessage(), e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.DOWNLOAD_ERROR_ORDER_EXCEPTION.getCode()), ExceptionEnum.DOWNLOAD_ERROR_ORDER_EXCEPTION.getMessage());
        }
    }


    /**
     * 校验导入订单商品是否供应链商品
     * @param importOrderInfoList
     */
    /*private void isScmItems2(List<ImportOrderInfo> importOrderInfoList, List<ExternalItemSku> externalItemSkuList){
        Set<String> skuCodes = new HashSet<String>();
        Set<String> channelCodes = new HashSet<String>();
        for(ImportOrderInfo detail: importOrderInfoList){
            skuCodes.add(detail.getSkuCode());
            channelCodes.add(detail.getChannelCode());
        }
        //检查sku绑定
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        List<SkuRelation> skuRelations = skuRelationService.selectByExample(example);
        for(ImportOrderInfo detail: importOrderInfoList){
            boolean flag = false;
            for(SkuRelation skuRelation: skuRelations){
                if(StringUtils.equals(detail.getChannelCode(), skuRelation.getChannelCode()) &&
                        StringUtils.equals(detail.getSkuCode(), skuRelation.getSkuCode())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                detail.setFlag(false);
                setImportOrderErrorMsg(detail, String.format("商品%s编号不存在", detail.getSkuCode()));
            }
        }
        //检查sku是否代发商品
        if(!CollectionUtils.isEmpty(externalItemSkuList)){
            for(ImportOrderInfo detail: importOrderInfoList){
                for(ExternalItemSku externalItemSku: externalItemSkuList){
                    if(StringUtils.equals(externalItemSku.getSkuCode(), detail.getSkuCode())){
                        detail.setFlag(false);
                        setImportOrderErrorMsg(detail, String.format("商品%s是代发商品,暂不支持订单导入", detail.getSkuCode()));
                    }
                }
            }
        }
    }*/



    /**
     * 导入订单处理
     * @param platformOrderList
     * @param shopOrderList
     */
    private String processImportOrder(List<PlatformOrder> platformOrderList, List<ShopOrder> shopOrderList, List<ImportOrderInfo> importOrderInfoList, String operator) {
        if(!CollectionUtils.isEmpty(platformOrderList)){
            for(PlatformOrder platformOrder: platformOrderList){
                List<ShopOrder> _shopOrders = new ArrayList<>();
                for(ShopOrder shopOrder: shopOrderList){
                    if(StringUtils.equals(platformOrder.getPlatformOrderCode(), shopOrder.getPlatformOrderCode())){
                        _shopOrders.add(shopOrder);
                    }
                }
                try {
                    Map<String, Object> map = processOrder(platformOrder, _shopOrders, ZeroToNineEnum.ONE.getCode(), importOrderInfoList, operator);
                    List<WarehouseOrder> warehouseOrderList = (List<WarehouseOrder>)map.get("warehouseOrderList");
                    Map<String, List<SkuWarehouseDO>> skuWarehouseMap = (Map<String, List<SkuWarehouseDO>>)map.get("skuWarehouseMap");
                    //获取粮油或者自采仓库订单
                    List<WarehouseOrder> lyWarehouseOrders = new ArrayList<WarehouseOrder>();
                    List<WarehouseOrder> selfPurchaseOrders = new ArrayList<WarehouseOrder>();
                    for(WarehouseOrder warehouseOrder: warehouseOrderList){
                        if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, warehouseOrder.getSupplierCode())){
                            lyWarehouseOrders.add(warehouseOrder);
                        }
                        if(StringUtils.equals(OrderTypeEnum.SELF_PURCHARSE.getCode(), warehouseOrder.getOrderType())){
                            selfPurchaseOrders.add(warehouseOrder);
                        }
                    }
                    if(selfPurchaseOrders.size() > 0){
                        //自采下单
                        submitImportSelfPurchaseOrder(selfPurchaseOrders, skuWarehouseMap);
                    }
                } catch (Exception e) {
                    log.error("导入订单异常", e);
                    for(ShopOrder shopOrder: _shopOrders){
                        for(OrderItem orderItem: shopOrder.getOrderItems()){
                            for(ImportOrderInfo detail: importOrderInfoList){
                                if(StringUtils.equals(orderItem.getChannelCode(), detail.getChannelCode()) &&
                                        StringUtils.equals(orderItem.getSellCode(), detail.getSellCode()) &&
                                        StringUtils.equals(orderItem.getShopOrderCode(), detail.getShopOrderCode()) &&
                                        StringUtils.equals(orderItem.getSkuCode(), detail.getSkuCode())){
                                    detail.setFlag(false);
                                    String msg = String.format("导入订单异常:%s", e.getMessage());
                                    setImportOrderErrorMsg(detail, msg);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        //保存导入订单信息
        String importOrdrCode = serialUtilService.generateCode(SupplyConstants.Serial.IMPORT_ORDER_LENGTH, SupplyConstants.Serial.IMPORT_ORDER_CODE, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        for(ImportOrderInfo importOrderInfo: importOrderInfoList){
            importOrderInfo.setImportOrderCode(importOrdrCode);
            if(importOrderInfo.getFlag()){
                importOrderInfo.setIsFail(ZeroToNineEnum.ZERO.getCode());
            }else{
                importOrderInfo.setIsFail(ZeroToNineEnum.ONE.getCode());
            }
        }
        if(!CollectionUtils.isEmpty(importOrderInfoList)){
            importOrderInfoService.insertList(importOrderInfoList);
        }
        return importOrdrCode;

    }

    /**
     * 提交导入的自采订单
     * @param warehouseOrders
     * @param skuWarehouseMap
     */
    public void submitImportSelfPurchaseOrder(List<WarehouseOrder> warehouseOrders, Map<String, List<SkuWarehouseDO>> skuWarehouseMap) {
        if(warehouseOrders.size() > 0){
            try {
                invokeSubmitSelfPurchaseOrder(warehouseOrders, skuWarehouseMap);
            } catch (Exception e) {
                log.error("提交导入自采订单异常", e);
            }
            //更新订单状态
            updateOrderStatusByOutboundOrder(warehouseOrders);
        }
    }



    //销售渠道订单号
    private final static String SHOP_ORDER_CODE = "*销售渠道订单号";
    //收货人姓名
    private  final static  String RECIVE_NAME = "*收货人姓名(门店订单可不填)";
    //收货人手机号
    private final static String RECIVE_MOBILE = "*收货人手机号(门店订单可不填)";
    //收货省份
    private final static String RECIVE_PROVINCE = "*收货省份(门店订单可不填)";
    //收货城市
    private final static String RECIVE_CITY = "*收货城市(门店订单可不填)";
    //收货地区
    private final static String RECIVE_DISTRICT = "*收货地区(门店订单可不填)";
    //收货详细地址
    private final static String RECIVE_ADDRESS = "*收货详细地址(门店订单可不填)";
    //商品SKU编号
    private final static String SKU_CODE = "*商品SKU编号";
    //商品SKU名称
    //private final static String SKU_NAME = "商品SKU名称";
    //商品交易数量
    private final static String NUM = "*商品交易数量";
    //商品销售单价
    private final static String PRICE = "*商品销售单价";
    //商品实付总金额
    private final static String PAYMENT = "*商品实付总金额";
    //商品运费
    private final static String POST_FEE = "*商品运费";
    //商品税费
    private final static String PRICE_TAX = "*商品税费";
    //买家留言
    private final static String BUYER_MESSAGE = "买家留言";
    //商家备注
    private final static String SHOP_MEMO = "商家备注";
    //备注
    private final static String MEMO = "备注";
    //付款时间
    private final static String PAY_TIME = "*付款时间";
    //付款时间
    private final static String ERROR_MESSAGE = "错误提示信息";


    /**
     * 检查列标题
     * @param titleResult
     * @return
     */
    private void checkTitle(String[] titleResult) {
        StringBuilder sb = new StringBuilder();
        _checkTitle(titleResult, SHOP_ORDER_CODE, sb);
        _checkTitle(titleResult, RECIVE_NAME, sb);
        _checkTitle(titleResult, RECIVE_MOBILE, sb);
        _checkTitle(titleResult, RECIVE_PROVINCE, sb);
        _checkTitle(titleResult, RECIVE_CITY, sb);
        _checkTitle(titleResult, RECIVE_DISTRICT, sb);
        _checkTitle(titleResult, RECIVE_ADDRESS, sb);
        _checkTitle(titleResult, SKU_CODE, sb);
        _checkTitle(titleResult, NUM, sb);
        _checkTitle(titleResult, PRICE, sb);
        _checkTitle(titleResult, PAYMENT, sb);
        _checkTitle(titleResult, POST_FEE, sb);
        _checkTitle(titleResult, PRICE_TAX, sb);
        _checkTitle(titleResult, BUYER_MESSAGE, sb);
        _checkTitle(titleResult, SHOP_MEMO, sb);
        _checkTitle(titleResult, PAY_TIME, sb);
        if(sb.length() > 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("导入订单模板缺少列%s", sb.toString()));
        }
    }

    private void _checkTitle(String[] titleResult, String colum, StringBuilder sb){
        boolean flag = false;
        for(String title: titleResult){
            if(StringUtils.equals(colum, title)){
                flag = true;
                break;
            }
        }
        if(!flag){
            sb.append(colum).append(SupplyConstants.Symbol.COMMA);
        }
    }

    /**
     * 获取列值
     * @param columVals
     * @param titleResult
     * @param columName
     * @return
     */
    private String getColumVal(String[] columVals, String[] titleResult, String columName){
        Integer idx = getColumIndex(titleResult, columName);
        if(null != idx){
            return columVals[idx];
        }
        return "";
    }

    /**
     * 获取订单导入数据
     * @param titleResult
     * @param contentResult
     * @return
     */
    private List<ImportOrderInfo> getImportOrderSkuDetail(String channelCode, String sellCode,
                                                          String[] titleResult, Map<String, String> contentResult, SellChannel sellChannel){
        List<ImportOrderInfo> importOrderInfoList = new ArrayList<>();
        Set<String> skuCodes = new HashSet<>();
        for(Map.Entry<String, String> entry: contentResult.entrySet()){
            if(entry.getKey().equals("count")){
                continue;
            }
            String record = entry.getValue();
            String[] columVals = record.split(SupplyConstants.Symbol.COMMA);
            for(int i=0; i<columVals.length; i++){
                if(StringUtils.equals(ImportExcel.NULL_STRING, columVals[i])){
                    columVals[i] = "";
                }
            }
            ImportOrderInfo detail = new ImportOrderInfo();
            detail.setChannelCode(channelCode);
            detail.setSellCode(sellCode);
            detail.setFlag(true);
            String shopOrderCode = getColumVal(columVals, titleResult, SHOP_ORDER_CODE);
            if(StringUtils.isNotBlank(shopOrderCode)){
                if(shopOrderCode.contains(SupplyConstants.Symbol.MINUS)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "销售渠道订单号不能包含符号\"-\"");
                }else{
                    if(CommonUtil.checkChinese(shopOrderCode)){//校验订单里不能含有汉字
                        if(detail.getFlag()){
                            detail.setFlag(false);
                        }
                        setImportOrderErrorMsg(detail, "销售渠道订单号不能包含汉字");
                    }
                }
                detail.setShopOrderCode(shopOrderCode);
            }else{
                if(detail.getFlag()){
                    detail.setFlag(false);
                }
                setImportOrderErrorMsg(detail, "销售渠道订单号不能为空");
            }

            String receiverName = getColumVal(columVals, titleResult, RECIVE_NAME);
            if(StringUtils.isNotBlank(receiverName)){
                detail.setReceiverName(receiverName);
            }

            String receiverMobil = getColumVal(columVals, titleResult, RECIVE_MOBILE);
            if(StringUtils.isNotBlank(receiverMobil)){
                if(!CommonUtil.checkMobilePhone(receiverMobil)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货人手机号码格式错误");
                }
                detail.setReceiverMobile(receiverMobil);
            }

            String receiverProvince = getColumVal(columVals, titleResult, RECIVE_PROVINCE);
            if(StringUtils.isNotBlank(receiverProvince)){
                detail.setReceiverProvince(receiverProvince);
            }

            String receiverCity = getColumVal(columVals, titleResult, RECIVE_CITY);
            if(StringUtils.isNotBlank(receiverCity)){
                detail.setReceiverCity(receiverCity);
            }

            String receiverDistrict = getColumVal(columVals, titleResult, RECIVE_DISTRICT);
            if(StringUtils.isNotBlank(receiverDistrict)){
                detail.setReceiverDistrict(receiverDistrict);
            }

            String receiverAddress = getColumVal(columVals, titleResult, RECIVE_ADDRESS);
            if(StringUtils.isNotBlank(receiverAddress)){
                detail.setReceiverAddress(receiverAddress);
            }
            if(!StringUtils.equals(String.valueOf(SellChannelTypeEnum.STORE.getCode()), sellChannel.getSellType())){//非门店订单
                if(StringUtils.isBlank(receiverName)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货人姓名不能为空");
                }

                if(StringUtils.isBlank(receiverMobil)){
                    if(!CommonUtil.checkMobilePhone(receiverMobil)){
                        if(detail.getFlag()){
                            detail.setFlag(false);
                        }
                        setImportOrderErrorMsg(detail, "收货人手机号不能为空");
                    }
                }

                if(StringUtils.isBlank(receiverProvince)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货省份不能为空");
                }

                if(StringUtils.isBlank(receiverCity)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货城市不能为空");
                }

                if(StringUtils.isBlank(receiverDistrict)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货地区不能为空");
                }

                if(StringUtils.isBlank(receiverAddress)){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "收货详细地址不能为空");
                }
            }

            String skuCode = getColumVal(columVals, titleResult, SKU_CODE);
            if(StringUtils.isNotBlank(skuCode)){
                detail.setSkuCode(skuCode);
            }else{
                if(detail.getFlag()){
                    detail.setFlag(false);
                }
                setImportOrderErrorMsg(detail, "商品SKU编号不能为空");
            }

            String paytime = getColumVal(columVals, titleResult, PAY_TIME);
            if(StringUtils.isNotBlank(paytime)){
                String dateFormate = "";
                if(paytime.contains(SupplyConstants.Symbol.XIE_GANG)){
                    dateFormate = DateUtils.DATETIME_FORMAT2;
                }else if(paytime.contains(SupplyConstants.Symbol.MINUS)){
                    dateFormate = DateUtils.DATETIME_FORMAT;
                }
                Date date = DateUtils.parseDateTimeFormate(paytime, dateFormate);
                if(null == date){
                    if(detail.getFlag()){
                        detail.setFlag(false);
                    }
                    setImportOrderErrorMsg(detail, "付款时间格式错误,必须是yyyy-MM-dd HH:mm:ss或者yyyy/MM/ dd HH:mm:ss格式字符串");
                }else{
                    detail.setPayTime(date);
                }
                detail.setOrignalPaytimeStr(paytime);
            }else{
                if(detail.getFlag()){
                    detail.setFlag(false);
                }
                setImportOrderErrorMsg(detail, "付款时间不能为空");
            }

            String buyerMessage = getColumVal(columVals, titleResult, BUYER_MESSAGE);
            if(!StringUtils.equals(ImportExcel.NULL_STRING, buyerMessage)){
                detail.setBuyerMessage(buyerMessage);
            }
            String shopMemo = getColumVal(columVals, titleResult, SHOP_MEMO);
            if(!StringUtils.equals(ImportExcel.NULL_STRING, shopMemo)){
                detail.setShopMemo(shopMemo);
            }
            String memo = getColumVal(columVals, titleResult, MEMO);
            if(!StringUtils.equals(ImportExcel.NULL_STRING, memo)){
                detail.setMemo(memo);
            }

            String num = getColumVal(columVals, titleResult, NUM);
            if(StringUtils.isNotBlank(num)){
                detail.setNum(Integer.parseInt(num));
            }else{
                if(detail.getFlag()){
                    detail.setFlag(false);
                }
                setImportOrderErrorMsg(detail, "商品交易数不能为空");
            }



            setImportOrderMoney(detail, PRICE, titleResult, columVals, true, true);
            setImportOrderMoney(detail, PAYMENT, titleResult, columVals, true, true);
            setImportOrderMoney(detail, POST_FEE, titleResult, columVals, true, false);
            setImportOrderMoney(detail, PRICE_TAX, titleResult, columVals, true, false);
            skuCodes.add(detail.getSkuCode());
            importOrderInfoList.add(detail);
        }
        return importOrderInfoList;
    }


    /**
     * 设置导入订单金额
     * @param importOrderInfo
     * @param colum
     * @param titleResult
     * @param columVals
     * @param emptyCheck 空校验
     * @param gtZeroCheck 大于0校验
     */
    private void setImportOrderMoney(ImportOrderInfo importOrderInfo, String colum, String[] titleResult, String[] columVals, boolean emptyCheck, boolean gtZeroCheck){
        String money = columVals[getColumIndex(titleResult, colum)];
        if(StringUtils.isNotBlank(money)){
            try{
                BigDecimal bigDecimal = new BigDecimal(money);
                if(PRICE.equals(colum)){
                    importOrderInfo.setPrice(bigDecimal);
                }else if(PAYMENT.equals(colum)){
                    importOrderInfo.setPayment(bigDecimal);
                }else if(POST_FEE.equals(colum)){
                    importOrderInfo.setPostFee(bigDecimal);
                }else if(PRICE_TAX.equals(colum)){
                    importOrderInfo.setPriceTax(bigDecimal);
                }
                /*if(gtZeroCheck && bigDecimal.compareTo(new BigDecimal(0))<= 0){
                    importOrderInfo.setFlag(false);
                    setImportOrderErrorMsg(importOrderInfo, colum+"必须大于0");
                }*/
                if(!CommonUtil.checkMoney(money)){
                    importOrderInfo.setFlag(false);
                    setImportOrderErrorMsg(importOrderInfo, colum+"不合规范");
                }
                if(money.contains(SupplyConstants.Symbol.FILE_NAME_SPLIT)){
                    String[] amonts = money.split("\\.");
                    String intStr = amonts[0];//整数部分
                    String minStr = amonts[1];//小数部分
                    if(intStr.length() > 10){
                        importOrderInfo.setFlag(false);
                        setImportOrderErrorMsg(importOrderInfo, colum+"整数部分不能超过10位");
                    }else{
                        if(minStr.length() > 3){
                            importOrderInfo.setFlag(false);
                            setImportOrderErrorMsg(importOrderInfo, colum+"小数部分不能超过3位");
                        }
                    }

                }else {
                    if(money.length() > 10){
                        importOrderInfo.setFlag(false);
                        setImportOrderErrorMsg(importOrderInfo, colum+"整数部分不能超过10位");
                    }
                }
            }catch (Exception e){
                importOrderInfo.setFlag(false);
                setImportOrderErrorMsg(importOrderInfo, String.format("%s格式错误", colum));
                log.error(String.format("商品%s的%s数据格式错误", importOrderInfo.getSkuCode(), colum), e);
            }
        }else{
            if(emptyCheck){
                importOrderInfo.setFlag(false);
                setImportOrderErrorMsg(importOrderInfo, colum+"不能为空");
            }
        }
    }

    /**
     * 获取列对应的位置
     * @param titleResult
     * @param colum
     * @return
     */
    private Integer getColumIndex(String[] titleResult, String colum){
        for(int i=0; i<titleResult.length; i++){
            if(titleResult[i].equals(colum)){
                return i;
            }
        }
        return null;
    }

    /**
     *
     * 获取导入订单的OrderItem列表
     * @param importOrderInfoList
     * @return
     */
    private List<OrderItem> getImportSkuOrderItems(String scmShopOrderCode, List<ImportOrderInfo> importOrderInfoList){
        List<OrderItem> orderItemList = new ArrayList<>();
        for(ImportOrderInfo detail: importOrderInfoList){
            if(detail.getFlag()){//基础校验通过的数据
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(detail, orderItem);
                orderItem.setScmShopOrderCode(scmShopOrderCode);
                orderItem.setItemName(detail.getSkuName());
                orderItem.setPostDiscount(detail.getPostFee());//邮费
                orderItem.setTradeMemo(detail.getMemo());
                orderItem.setCreateTime(detail.getPayTime());
                if(orderItem.getSkuCode().startsWith(SP0)){
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER.getCode());//等待仓库发货
                }else if(orderItem.getSkuCode().startsWith(SP1)){
                    orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode());//待发送供应商
                }
                orderItem.setIsStoreOrder(IsStoreOrderEnum.NOT_STORE_ORDER.getCode());
                orderItemList.add(orderItem);
            }
        }
        return orderItemList;
    }

    /**
     * 检查店铺订单下商品的内容:
     * 同一个“销售渠道订单号”下的付款时间、收货方式、收货人姓名、收货人手机号、收货省份、收货城市、收货地区、
     * 收货详细地址需完全一致，否则提示：同一个订单中的“显示字段名称”需完全一致！
     */
    private void checkShopOrderItemContent(List<ImportOrderInfo> importOrderInfoList){
        if(CollectionUtils.isEmpty(importOrderInfoList)){
           return;
        }
        ImportOrderInfo importOrderInfo = importOrderInfoList.get(0);
        //付款时间
        String payTime = DateUtils.dateToNormalFullString(importOrderInfo.getPayTime());
        //收货人姓名
        String receiverName = importOrderInfo.getReceiverName();
        //收货人手机号
        String receiverMobile = importOrderInfo.getReceiverMobile();
        //收货省份
        String receiverProvince = importOrderInfo.getReceiverProvince();
        //收货城市
        String receiverCity = importOrderInfo.getReceiverCity();
        //收货地区
        String receiverDistrict = importOrderInfo.getReceiverDistrict();
        //收货详细地址
        String receiverAddress = importOrderInfo.getReceiverAddress();
        boolean flag = true;
        String errorMsg = "";
        for(ImportOrderInfo _imortOrder: importOrderInfoList){
            StringBuilder sb = new StringBuilder();
            sb.append("同一个订单中的");
            if(!orderContentCompare(payTime, DateUtils.dateToNormalFullString(_imortOrder.getPayTime()), _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("付款时间");
            }
            if(!orderContentCompare(receiverName, _imortOrder.getReceiverName(), _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货人姓名");
            }
            if(!orderContentCompare(receiverMobile, _imortOrder.getReceiverMobile(), _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货人手机号");
            }
            if(!orderContentCompare(receiverProvince, _imortOrder.getReceiverProvince(),  _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货省份");
            }
            if(!orderContentCompare(receiverCity, _imortOrder.getReceiverCity(), _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货城市");
            }
            if(!orderContentCompare(receiverDistrict, _imortOrder.getReceiverDistrict(),  _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货地区");
            }
            if(!orderContentCompare(receiverAddress, _imortOrder.getReceiverAddress(), _imortOrder)){
                if(flag){
                    flag = false;
                }
                sb.append("收货详细地址");
            }
            sb.append("需完全一致");
            if(!flag){
                errorMsg = sb.toString();
                break;
            }
        }
        if(!flag){
            for(ImportOrderInfo _imortOrder: importOrderInfoList){
                _imortOrder.setFlag(false);
                setImportOrderErrorMsg(_imortOrder, errorMsg);
            }
        }

    }

    private boolean orderContentCompare(String baseContent, String newContent, ImportOrderInfo importOrderInfo){
        if(StringUtils.isBlank(baseContent) && StringUtils.isBlank(newContent)){
            return true;
        }
        if(!StringUtils.equals(baseContent, newContent)){
            importOrderInfo.setFlag(false);
            //importOrderInfo.setErrorMessage(String.format("同一个订单中的“%s”需完全一致", fieldName));
            return false;
        }
        return true;
    }

    private void setImportOrderErrorMsg(ImportOrderInfo importOrderInfo, String msg){
        /*if(StringUtils.isBlank(importOrderInfo.getErrorMessage())){
            importOrderInfo.setErrorMessage(msg);
        }else{
            importOrderInfo.setErrorMessage(String.format("%s;%s", importOrderInfo.getErrorMessage(), msg));
        }*/
        if(StringUtils.isBlank(importOrderInfo.getErrorMessage())){
            importOrderInfo.setErrorMessage(msg);
        }
    }

    /**
     * 获取导入订单的店铺订单
     * @param importOrderInfoList
     * @return
     */
    private List<ShopOrder> getImportShopOrders(List<ImportOrderInfo> importOrderInfoList){
        Set<String> shopOrderCodes = new HashSet<>();
        for(ImportOrderInfo orderItem: importOrderInfoList){
            if(orderItem.getFlag()){
                //以业务线编码-销售渠道编码-销售渠道订单号为维度决定一个店铺订单
                StringBuilder sb = new StringBuilder();
                sb.append(orderItem.getChannelCode()).append(SupplyConstants.Symbol.MINUS).append(orderItem.getSellCode()).
                        append(SupplyConstants.Symbol.MINUS).append(orderItem.getShopOrderCode());
                shopOrderCodes.add(sb.toString());
            }
        }
        Map<String, List<ImportOrderInfo>> map = new HashedMap();
        for(String key: shopOrderCodes){
            boolean isReaptSku = false;
            List<ImportOrderInfo> _orderItemList = new ArrayList<>();
            for(ImportOrderInfo orderItem: importOrderInfoList){
                if(isSameShop(key, orderItem)){
                    for(ImportOrderInfo _importOrderInfo: _orderItemList){
                        if(StringUtils.equals(orderItem.getSkuCode(), _importOrderInfo.getSkuCode())){
                            isReaptSku = true;
                        }
                    }
                    _orderItemList.add(orderItem);
                }
            }
            //检查店铺订单下商品的内容
            checkShopOrderItemContent(_orderItemList);
            //同一个订单下的数据要么一起导入成功，要么一起导入失败
            if(isReaptSku){
                for(ImportOrderInfo orderItem: _orderItemList){
                    for(ImportOrderInfo _orderItem: importOrderInfoList){
                        if(StringUtils.equals(orderItem.getChannelCode(), _orderItem.getChannelCode()) &&
                                StringUtils.equals(orderItem.getSellCode(), _orderItem.getSellCode()) &&
                                StringUtils.equals(orderItem.getShopOrderCode(), _orderItem.getShopOrderCode()) &&
                                StringUtils.equals(orderItem.getSkuCode(), _orderItem.getSkuCode())){
                            _orderItem.setFlag(false);
                            setImportOrderErrorMsg(_orderItem, "同一订单中的商品SKU编号不能重复");
                        }
                    }
                }
            }else {
                boolean _flag = false;
                ImportOrderInfo _orderInfo = null;
                for(ImportOrderInfo orderItem: _orderItemList){
                    if(!orderItem.getFlag()){
                        _flag = true;
                        _orderInfo = orderItem;
                        break;
                    }
                }
                if(_flag){
                    for(ImportOrderInfo orderItem: _orderItemList){
                        for(ImportOrderInfo _orderItem: importOrderInfoList){
                            if(StringUtils.equals(orderItem.getChannelCode(), _orderItem.getChannelCode()) &&
                                    StringUtils.equals(orderItem.getSellCode(), _orderItem.getSellCode()) &&
                                    StringUtils.equals(orderItem.getShopOrderCode(), _orderItem.getShopOrderCode()) &&
                                    StringUtils.equals(orderItem.getSkuCode(), _orderItem.getSkuCode())){
                                _orderItem.setFlag(false);
                                setImportOrderErrorMsg(_orderItem, String.format("同一订单中的【%s】有误！",  _orderInfo.getSkuCode()));
                            }
                        }
                    }
                    _orderItemList = new ArrayList<>();
                }
            }
            map.put(key, _orderItemList);
        }
        List<ShopOrder> shopOrderList = new ArrayList<>();
        for(Map.Entry<String, List<ImportOrderInfo>> entry: map.entrySet()){
            String shopOrderCode = entry.getKey();
            String[] keys = shopOrderCode.split(SupplyConstants.Symbol.MINUS);
            List<ImportOrderInfo> importOrderInfos = entry.getValue();
            if(CollectionUtils.isEmpty(importOrderInfos)){
                continue;
            }
            ImportOrderInfo importOrderInfo = importOrderInfos.get(0);
            ShopOrder shopOrder = new ShopOrder();
            String platformOrderCode = GuidUtil.getNextUid(PLATFORM_ORDER_CORD_PREFIX);
            shopOrder.setPlatformOrderCode(platformOrderCode);
            String scmShopOrderCode = serialUtilService.generateCode(SupplyConstants.Serial.SYSTEM_ORDER_LENGTH,
                    SupplyConstants.Serial.SYSTEM_ORDER_CODE, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            shopOrder.setScmShopOrderCode(scmShopOrderCode);
            shopOrder.setShopOrderCode(keys[2]);
            shopOrder.setChannelCode(importOrderInfo.getChannelCode());
            shopOrder.setSellCode(importOrderInfo.getSellCode());
            shopOrder.setCreateTime(new Date());
            shopOrder.setPayTime(importOrderInfo.getPayTime());
            StringBuilder buyerMessage = new StringBuilder();
            StringBuilder shopMemo = new StringBuilder();
            StringBuilder tradeMemo = new StringBuilder();
            int num = 0;//商品交易数量
            BigDecimal payment = new BigDecimal(0);//实付金额
            BigDecimal postFee = new BigDecimal(0);//运费
            BigDecimal tax = new BigDecimal(0);//税费
            for(ImportOrderInfo detail: importOrderInfos){
                if(StringUtils.isNotBlank(detail.getBuyerMessage())){
                    buyerMessage.append(detail.getBuyerMessage()).append(SupplyConstants.Symbol.SEMICOLON);
                }
                if(StringUtils.isNotBlank(detail.getShopMemo())){
                    shopMemo.append(detail.getShopMemo()).append(SupplyConstants.Symbol.SEMICOLON);
                }
                if(StringUtils.isNotBlank(detail.getMemo())){
                    tradeMemo.append(detail.getMemo()).append(SupplyConstants.Symbol.SEMICOLON);
                }
                num += detail.getNum();
                payment = payment.add(detail.getPayment());
                if(null != detail.getPostFee()){
                    postFee = postFee.add(detail.getPostFee());
                }
                if(null != detail.getPriceTax()){
                    tax = tax.add(detail.getPriceTax());
                }
            }
            if(buyerMessage.length() > 0){
                shopOrder.setBuyerMessage(buyerMessage.toString());
            }
            if(shopMemo.length() > 0){
                shopOrder.setShopMemo(shopMemo.toString());
            }
            if(tradeMemo.length() > 0){
                shopOrder.setTradeMemo(tradeMemo.toString());
            }
            shopOrder.setItemNum(num);
            shopOrder.setPayment(payment);
            shopOrder.setPostageFee(postFee);
            shopOrder.setTotalTax(tax);
            shopOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.WAIT_FOR_DELIVER.getCode());
            List<OrderItem> orderItemList = getImportSkuOrderItems(scmShopOrderCode, importOrderInfos);
            for(OrderItem orderItem: orderItemList){
                orderItem.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            }
            shopOrder.setOrderItems(orderItemList);
            if(orderItemList.size() > 0){
                shopOrderList.add(shopOrder);
            }
        }
        for(ImportOrderInfo importOrderInfo: importOrderInfoList){
            for(ShopOrder shopOrder: shopOrderList){
                if(StringUtils.equals(shopOrder.getChannelCode(), importOrderInfo.getChannelCode()) &&
                        StringUtils.equals(shopOrder.getSellCode(), importOrderInfo.getSellCode()) &&
                        StringUtils.equals(shopOrder.getShopOrderCode(), importOrderInfo.getShopOrderCode())){
                    importOrderInfo.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
                    importOrderInfo.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
                    break;
                }
            }
        }
        return shopOrderList;
    }

    /**
     * 获取导入订单的平台订单
     * @param shopOrderList
     * @param importOrderInfoList
     * @return
     */
    private List<PlatformOrder> getImportPlatformOrders(List<ShopOrder> shopOrderList, List<ImportOrderInfo> importOrderInfoList){
        List<PlatformOrder> platformOrderList = new ArrayList<>();
        for(ShopOrder shopOrder: shopOrderList){
            PlatformOrder platformOrder = new PlatformOrder();
            platformOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            platformOrder.setChannelCode(shopOrder.getChannelCode());
            platformOrder.setSellCode(shopOrder.getSellCode());
            platformOrder.setItemNum(shopOrder.getItemNum());
            platformOrder.setPayment(shopOrder.getPayment());
            platformOrder.setPostageFee(shopOrder.getPostageFee());
            platformOrder.setTotalTax(shopOrder.getTotalTax());
            platformOrder.setPayTime(shopOrder.getPayTime());
            platformOrder.setNeedInvoice(ZeroToNineEnum.ZERO.getCode());//不需要开票
            platformOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            ImportOrderInfo importOrderInfo = null;
            for(ImportOrderInfo detail: importOrderInfoList){
                if(StringUtils.equals(shopOrder.getChannelCode(), detail.getChannelCode()) &&
                        StringUtils.equals(shopOrder.getSellCode(), detail.getSellCode()) &&
                        StringUtils.equals(shopOrder.getShopOrderCode(), detail.getShopOrderCode())){
                    importOrderInfo = detail;
                    break;
                }
            }
            if(null != importOrderInfo){
                platformOrder.setPayTime(importOrderInfo.getPayTime());
                platformOrder.setReceiverName(importOrderInfo.getReceiverName());
                platformOrder.setReceiverMobile(importOrderInfo.getReceiverMobile());
                platformOrder.setReceiverProvince(importOrderInfo.getReceiverProvince());
                platformOrder.setReceiverCity(importOrderInfo.getReceiverCity());
                platformOrder.setReceiverDistrict(importOrderInfo.getReceiverDistrict());
                platformOrder.setReceiverAddress(importOrderInfo.getReceiverAddress());
            }
            platformOrderList.add(platformOrder);
        }
        return platformOrderList;
    }

    /**
     *
     * @param key
     * @param detail
     * @return
     */
    private boolean isSameShop(String key, ImportOrderInfo detail){
        String[] keys = key.split(SupplyConstants.Symbol.MINUS);
        String channelCode = keys[0];//业务线编码
        String sellCode = keys[1];//销售渠道编码
        String shopOrderCode = keys[2];//销售渠道订单号
        if(StringUtils.equals(channelCode, detail.getChannelCode()) &&
                StringUtils.equals(sellCode, detail.getSellCode()) &&
                StringUtils.equals(shopOrderCode, detail.getShopOrderCode())){
            return true;
        }
        return false;
    }





}
