package org.trc.biz.impl.order;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.common.RequsetUpdateStock;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.System.Warehouse;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.config.SystemConfig;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.SkuRelation;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.DeliverPackageForm;
import org.trc.domain.order.ExceptionOrder;
import org.trc.domain.order.ExceptionOrderItem;
import org.trc.domain.order.OrderBase;
import org.trc.domain.order.OrderExt;
import org.trc.domain.order.OrderFlow;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.SupplierOrderInfo;
import org.trc.domain.order.SupplierOrderLogistics;
import org.trc.domain.order.WarehouseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.CancelStatusEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ExceptionOrderHandlerEnum;
import org.trc.enums.ExceptionTypeEnum;
import org.trc.enums.GoodsTypeEnum;
import org.trc.enums.InventoryTypeEnum;
import org.trc.enums.ItemNoticeStateEnum;
import org.trc.enums.ItemTypeEnum;
import org.trc.enums.JdInvoiceStateEnum;
import org.trc.enums.JdInvoiceTitleEnum;
import org.trc.enums.JdInvoiceTypeEnum;
import org.trc.enums.JdPaymentTypeEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.LogsticsTypeEnum;
import org.trc.enums.OrderDeliverStatusEnum;
import org.trc.enums.OrderItemDeliverStatusEnum;
import org.trc.enums.OrderTypeEnum;
import org.trc.enums.OutboundDetailStatusEnum;
import org.trc.enums.OutboundOrderStatusEnum;
import org.trc.enums.OwnerWarehouseStateEnum;
import org.trc.enums.QimenOrderTypeEnum;
import org.trc.enums.RequestFlowStatusEnum;
import org.trc.enums.RequestFlowTypeEnum;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.SupplierLogisticsEnum;
import org.trc.enums.SupplierOrderDeliverStatusEnum;
import org.trc.enums.SupplierOrderStatusEnum;
import org.trc.enums.SupplierOrderTypeEnum;
import org.trc.enums.TrcActionTypeEnum;
import org.trc.enums.WarehouseOrderLogisticsStatusEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.OrderException;
import org.trc.exception.ParamValidException;
import org.trc.exception.QimenException;
import org.trc.exception.SignException;
import org.trc.form.ChannelOrderResponse;
import org.trc.form.Logistic;
import org.trc.form.LogisticForm;
import org.trc.form.LogisticInfo;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.LogisticNoticeForm2;
import org.trc.form.OrderSubmitResult;
import org.trc.form.SkuInfo;
import org.trc.form.SupplierOrderReturn;
import org.trc.form.TrcConfig;
import org.trc.form.TrcParam;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.JDModel.JdSku;
import org.trc.form.JDModel.JingDongSupplierOrder;
import org.trc.form.JDModel.OrderPriceSnap;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.JDModel.SupplyItemsUpdate;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.form.liangyou.OutOrderGoods;
import org.trc.form.order.ExceptionOrderForm;
import org.trc.form.order.InventoryQueryItemDO;
import org.trc.form.order.OutboundForm;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.SkuWarehouseDO;
import org.trc.form.order.SupplierOrderCancelForm;
import org.trc.form.order.SupplierOrderCancelInfo;
import org.trc.form.order.SupplierOrderCancelNotify;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.form.order.WarehouseOwernSkuDO;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IJDService;
import org.trc.service.IQimenService;
import org.trc.service.ITrcService;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.System.IWarehouseService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.config.ISystemConfigService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.impl.outbound.OutBoundOrderService;
import org.trc.service.order.IExceptionOrderItemService;
import org.trc.service.order.IExceptionOrderService;
import org.trc.service.order.IOrderFlowService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.order.ISupplierOrderInfoService;
import org.trc.service.order.ISupplierOrderLogisticsService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.IRealIpService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.CellDefinition;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import org.trc.util.ExceptionUtil;
import org.trc.util.ExportExcel;
import org.trc.util.GuidUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import org.trc.util.QueryModel;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;
import org.trc.util.SHAEncrypt;
import org.trc.util.ValidateUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.request.DeliveryorderBatchcreateRequest;
import com.qimen.api.request.InventoryQueryRequest;
import com.qimen.api.response.DeliveryorderBatchcreateResponse;
import com.qimen.api.response.InventoryQueryResponse;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Service("scmOrderBiz")
public class ScmOrderBiz implements IScmOrderBiz {

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
    //下单失败日志信息
    public final static String ORDER_FAILURE_INFO = "下单失败";
    //下单成功日志信息
    public final static String ORDER_CANCEL_INFO = "已取消";
    private Logger log = LoggerFactory.getLogger(ScmOrderBiz.class);
    //创建线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    //渠道订单金额校验:1-是,0-否
    @Value("${channel.orderMoneyCheck}")
    private String channelOrderMoneyCheck;
    //是否正式提交订单:1-是,0-否
    @Value("${submit.order.status}")
    private String submitOrderStatus;
    //京东下单方式 0--预占库存方式 1--不是预占库存
    @Value("${jd.submit.state}")
    private String jdSubmitState;
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
    @Autowired
    private ILogisticsCompanyService logisticsCompanyService;
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
    private IWarehouseService warehouseService;
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



    @Value("{trc.jd.logistic.url}")
    private String TRC_JD_LOGISTIC_URL;
    private String SP0 = "SP0";
    private String SP1 = "SP1";
    private String ONE = "1";
    private String ZERO = "0";

    @Override
    @Cacheable(key="#queryModel.toString()+#aclUserAccreditInfo.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<ShopOrder> shopOrderPage(ShopOrderForm queryModel, Pagenation<ShopOrder> page, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }
        if (StringUtil.isNotEmpty(queryModel.getPlatformOrderCode())) {//平台订单编码
            criteria.andLike("platformOrderCode", "%" + queryModel.getPlatformOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopOrderCode())) {//店铺订单编码
            criteria.andLike("shopOrderCode", "%" + queryModel.getShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopName())) {//店铺名称
            criteria.andLike("shopName", "%" + queryModel.getShopName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSupplierOrderStatus())) {//发货状态
            criteria.andEqualTo("supplierOrderStatus", queryModel.getSupplierOrderStatus());
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
        example.orderBy("payTime").desc();
        page = shopOrderService.pagination(example, page, new QueryModel());
        if(page.getResult().size() > 0){
            handlerOrderInfo(page, platformOrderList);
        }
        return page;
    }

    @Override
    //@Cacheable(key="#form+#aclUserAccreditInfo+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<WarehouseOrder> warehouseOrderPage(WarehouseOrderForm form, Pagenation<WarehouseOrder> page, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        AssertUtil.notNull(form, "查询供应商订单分页参数不能为空");
        Example example = new Example(WarehouseOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }

        if(StringUtils.isNotBlank(form.getOrderType())){//订单类型
            criteria.andEqualTo("orderType", form.getOrderType());
        }
        if(StringUtils.isNotBlank(form.getSupplierOrderStatus())){//状态
            criteria.andEqualTo("supplierOrderStatus", form.getSupplierOrderStatus());
        }
        if(StringUtils.isNotBlank(form.getPlatformOrderCode())){//平台订单编号
            criteria.andLike("platformOrderCode", "%" + form.getPlatformOrderCode() + "%");
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
        example.setOrderByClause("instr('2,5,1',`supplier_order_status`) DESC");
        example.orderBy("payTime").desc();
        example.orderBy("updateTime").desc();
        page = warehouseOrderService.pagination(example, page, form);
        handlerWarehouseOrderInfo(page, platformOrderList);
        return page;
    }

    @Override
    @Cacheable(key="#form.toString()",isList=true)
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
            supplierOrderInfo = supplierOrderInfoService.selectOne(supplierOrderInfo);
            if(null != supplierOrderInfo){
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
    @Cacheable(key="#form.toString()",isList=true)
    public List<PlatformOrder> queryPlatformOrders(PlatformOrderForm form) {
        AssertUtil.notNull(form, "查询平台订单列表参数对象不能为空");
        PlatformOrder platformOrder = new PlatformOrder();
        BeanUtils.copyProperties(form, platformOrder);
        return platformOrderService.select(platformOrder);
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单京东订单仓库订单编码不能为空");
        AssertUtil.notBlank(jdAddressCode, "提交订单京东订单四级地址编码不能为空");
        AssertUtil.notBlank(jdAddressName, "提交订单京东订单四级地址不能为空");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressCode, "提交订单京东订单四级地址编码格式错误");
        AssertUtil.doesNotContain(JING_DONG_ADDRESS_SPLIT, jdAddressName, "提交订单京东订单四级地址格式错误");
        //获取京东四级地址
        String[] jdAddressCodes = jdAddressCode.split(JING_DONG_ADDRESS_SPLIT);
        String[] jdAddressNames = jdAddressName.split(JING_DONG_ADDRESS_SPLIT);
        AssertUtil.isTrue(jdAddressCodes.length == jdAddressNames.length, "京东四级地址编码与名称个数不匹配");
        //适配京东地址,实际测试发现这段代码用不着，所以注释
        /*Map<String, String[]> addressMap =  adapterJingDongAddress(jdAddressCodes, jdAddressNames);
        jdAddressCodes = addressMap.get("jdAddressCodes");
        jdAddressNames = addressMap.get("jdAddressNames");*/
        ResponseAck responseAck = null;
        //获取供应链订单数据
        Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
        PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
        WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
        List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
        //获取京东订单对象
        JingDongSupplierOrder jingDongOrder = getJingDongOrder(warehouseOrder, platformOrder, orderItemList, jdAddressCodes);
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
                    //记录操作日志
                    //logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), LogOperationEnum.SUBMIT_JINGDONG_ORDER.getMessage(), null,null);
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
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.SUBMIT_JINGDONG_ORDER.getMessage(), null,null);
        }else{
            responseAck2 = responseAck;
            log.error(String.format("调用京东下单接口提交订单%s失败,错误信息:%s", JSONObject.toJSON(jingDongOrder), responseAck.getMessage()));
        }
        //保存京东订单信息
        List<SupplierOrderInfo> supplierOrderInfoList = saveSupplierOrderInfo(warehouseOrder, responseAck, orderItemList, jdAddressCodes, jdAddressNames, ZeroToNineEnum.ZERO.getCode());
        //更新订单商品供应商订单状态
        updateOrderItemSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderInfoList);
        //更新仓库订单供应商订单状态
        warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode());
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
        if(StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)){
            String msg = String.format("提交仓库级订单编码为[%s]的京东订单下单成功", warehouseOrderCode);
            log.info(msg);
        }else{
            log.error(String.format("调用京东下单接口提交仓库订单%s失败,错误信息:%s", JSONObject.toJSON(warehouseOrder), responseAck.getMessage()));
        }
        //下单结果通知渠道
        try{
            notifyChannelSubmitOrderResult(warehouseOrder);
        }catch (Exception e){
            String msg = String.format("仓库级订单编码为[%s]的京东订单下单结果通知渠道异常,异常信息:%s",
                    warehouseOrderCode, e.getMessage());
            log.error(msg, e);
        }
        if(null != responseAck2){
            return responseAck2;
        }
        return responseAck;
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
            if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_SUCCESS_INFO).append(HTML_BR);
                }
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_FAILURE_INFO).append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append(HTML_BR);
                }
            }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_CANCEL.getCode(), supplierOrderInfo.getSupplierOrderStatus())){
                for(SkuInfo skuInfo: skuInfoList){
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_CANCEL_INFO).append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append(HTML_BR);
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
                    sb.append(skuInfo.getSkuCode()).append(":").append(ORDER_CANCEL_INFO).append(SupplyConstants.Symbol.COMMA).append(supplierOrderInfo.getMessage()).append(HTML_BR);
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck submitLiangYouOrder(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "提交订单粮油订单仓库订单编码不能为空");
        //获取供应链订单数据
        Map<String, Object> scmOrderMap = getScmOrderMap(warehouseOrderCode);
        PlatformOrder platformOrder = (PlatformOrder)scmOrderMap.get("platformOrder");
        WarehouseOrder warehouseOrder = (WarehouseOrder)scmOrderMap.get("warehouseOrder");
        List<OrderItem> orderItemList = (List<OrderItem>)scmOrderMap.get("orderItemList");
        //获取粮油订单对象
        LiangYouSupplierOrder liangYouOrder = getLiangYouOrder(warehouseOrder, platformOrder, orderItemList);
        //调用粮油下单服务接口
        ResponseAck responseAck = invokeSubmitSuuplierOrder(liangYouOrder);
        //保存请求流水
        requestFlowBiz.saveRequestFlow(JSONObject.toJSON(liangYouOrder).toString(), RequestFlowConstant.GYL, RequestFlowConstant.LY, RequestFlowTypeEnum.LY_SUBMIT_ORDER, responseAck, RequestFlowConstant.GYL);
        //保存粮油订单信息
        List<SupplierOrderInfo> supplierOrderInfoList = saveSupplierOrderInfo(warehouseOrder, responseAck, orderItemList, new String[0], new String[0], ZeroToNineEnum.ZERO.getCode());
        //更新订单商品供应商订单状态
        updateOrderItemSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode(), supplierOrderInfoList);
        //更新仓库订单供应商订单状态
        warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode());
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            log.info(responseAck.getMessage());
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), SYSTEM, LogOperationEnum.SUBMIT_ORDER.getMessage(), null,null);
        }
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
    private WarehouseOrder updateWarehouseOrderSupplierOrderStatus(String warehouseOrderCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新仓库订单供应商订单状态,根据仓库订单号[%s]查询相应的商品明细为空", warehouseOrderCode));
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("更新仓库订单供应商订单状态,根据仓库订单编码%s查询仓库订单信息为空", warehouseOrder));
        if(StringUtils.equals(OrderTypeEnum.SUPPLIER.getCode(), warehouseOrder.getOrderType())){
            //更新代发仓库订单供应商订单状态
            updateWarehouseOrderSupplierOrderStatus_supplier(warehouseOrder, orderItemList);
        }else if(StringUtils.equals(OrderTypeEnum.SELF_PURCHARSE.getCode(), warehouseOrder.getOrderType())){
            //更新代发仓库订单供应商订单状态
            updateWarehouseOrderSupplierOrderStatus_selfPurchase(warehouseOrder, orderItemList);
        }
        return warehouseOrder;
    }


    /**
     * 更新代发仓库订单供应商订单状态
     * @param warehouseOrder
     * @param orderItemList
     */
    private void updateWarehouseOrderSupplierOrderStatus_supplier(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        warehouseOrder.setSupplierOrderStatus(getSupplierOrderStatusByItems(orderItemList, ZeroToNineEnum.ZERO.getCode()));
        warehouseOrder.setUpdateTime(Calendar.getInstance().getTime());
        warehouseOrderService.updateByPrimaryKey(warehouseOrder);
        LogOperationEnum logOperationEnum = null;
        if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode(), warehouseOrder.getSupplierOrderStatus())){
            logOperationEnum = LogOperationEnum.ORDER_EXCEPTION;
        }else if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), warehouseOrder.getSupplierOrderStatus())){
            logOperationEnum = LogOperationEnum.ORDER_FAILURE;
        }else if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
            logOperationEnum = LogOperationEnum.WAIT_FOR_DELIVER;
        }else if(StringUtils.equals(SupplierOrderStatusEnum.PARTS_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
            logOperationEnum = LogOperationEnum.PARTS_DELIVER;
        }else if(StringUtils.equals(SupplierOrderStatusEnum.ALL_DELIVER.getCode(), warehouseOrder.getSupplierOrderStatus())){
            logOperationEnum = LogOperationEnum.ALL_DELIVER;
        }
        if(null != logOperationEnum){
            String remark = "";
            if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode(), warehouseOrder.getSupplierOrderStatus()) ||
                    StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), warehouseOrder.getSupplierOrderStatus())){
                SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
                supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.select(supplierOrderInfo);
                if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
                    remark = getOrderExceptionMessage(supplierOrderInfoList);
                }
            }
            //记录操作日志
            logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), warehouseOrder.getSupplierName(), logOperationEnum.getMessage(), remark,null);
        }
    }

    /**
     * 更新自采仓库订单供应商订单状态
     * @param warehouseOrder
     * @param orderItemList
     */
    private void updateWarehouseOrderSupplierOrderStatus_selfPurchase(WarehouseOrder warehouseOrder, List<OrderItem> orderItemList){
        warehouseOrder.setSupplierOrderStatus(getSupplierOrderStatusByItems_selfPurchase(orderItemList, ZeroToNineEnum.ZERO.getCode()));
        warehouseOrder.setUpdateTime(Calendar.getInstance().getTime());
        warehouseOrderService.updateByPrimaryKey(warehouseOrder);
    }




    /**
     *根据订单商品状态获取订单状态(代发)
     * @param orderItemList
     * @param flag：0-仓库级订单,1-店铺级订单
     * @return
     */
    private String getSupplierOrderStatusByItems(List<OrderItem> orderItemList, String flag){
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
            if((failureNum > 0 && waitDeliverNum > 0) || (failureNum > 0 && cancelNum > 0) )
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
        return OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode();
    }

    /**
     *根据订单商品状态获取订单状态(自采)
     * @param orderItemList
     * @param flag：0-仓库级订单,1-店铺级订单
     * @return
     */
    private String getSupplierOrderStatusByItems_selfPurchase(List<OrderItem> orderItemList, String flag){
        int waitHandlerNum = 0;//待了结数
        int handlerNum = 0;//已了结数
        int sendWarehouseFialure = 0;//仓库接收失败数
        int waitDeliverNum = 0;//等待仓库发货数
        int warehouseSendProcessrNum = 0;//仓库告知的过程中状态数
        int allDeliverNum = 0;//全部发货数
        int partsDeliverNum = 0;//部分发货数
        int cancelNum = 0;//已取消数
        for(OrderItem orderItem: orderItemList){
            if(StringUtils.equals(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode(), orderItem.getSupplierOrderStatus()))
                sendWarehouseFialure++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.WAITING.getCode(), orderItem.getSupplierOrderStatus()))
                waitDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.ON_WAREHOUSE_NOTICE.getCode(), orderItem.getSupplierOrderStatus()))
                warehouseSendProcessrNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.ALL_GOODS.getCode(), orderItem.getSupplierOrderStatus()))
                allDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode(), orderItem.getSupplierOrderStatus()))
                partsDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.CANCELED.getCode(), orderItem.getSupplierOrderStatus()))
                cancelNum++;
            else if(StringUtils.equals(ExceptionOrderHandlerEnum.WAIT_HANDLER.getCode().toString(), orderItem.getSupplierOrderStatus()))
                waitHandlerNum++;
            else if(StringUtils.equals(ExceptionOrderHandlerEnum.HANDLERED.getCode().toString(), orderItem.getSupplierOrderStatus()))
                handlerNum++;
        }
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//仓库级订单
            //下单失败：仓库接收失败数 + 已了结数 = 商品应发数量
            if((handlerNum + sendWarehouseFialure) == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_FAILURE.getCode();
            //等待供应商发货：等待仓库发货数 + 已了结数 = 商品应发数量
            if((handlerNum + waitDeliverNum) == orderItemList.size())
                return SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode();
            //部分发货：部分发货数 > 0 || (等待仓库发货数 > 0 && (部分发货数 > 0 || 全部发货数 > 0))
            if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0)))
                return SupplierOrderStatusEnum.PARTS_DELIVER.getCode();
            //全部发货：全部发货数 + 已了结数 + 已取消数 = 商品应发数量
            if(allDeliverNum + handlerNum + cancelNum == orderItemList.size())
                return SupplierOrderStatusEnum.ALL_DELIVER.getCode();
            //已取消：已取消数 = 商品应发数量
            if(cancelNum == orderItemList.size())
                return SupplierOrderStatusEnum.ORDER_CANCEL.getCode();
            //供应商下单异常: (仓库接收失败数 > 0 && 等待仓库发货数 > 0) || (仓库接收失败数 > 0 && 已取消数 > 0)
            if((sendWarehouseFialure > 0 && waitDeliverNum > 0) || (sendWarehouseFialure > 0 && cancelNum > 0) )
                return SupplierOrderStatusEnum.ORDER_EXCEPTION.getCode();
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//店铺级订单
            //已取消：已取消数 = 商品应发数量
            if(cancelNum == orderItemList.size())
                return OrderDeliverStatusEnum.ORDER_CANCEL.getCode();
            //部分发货数 > 0 || (等待仓库发货数 > 0 && (部分发货数 > 0 || 全部发货数 > 0))
            if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0)))
                return OrderDeliverStatusEnum.PARTS_DELIVER.getCode();
            //全部发货：全部发货数 + 已取消数 = 商品应发数量
            if(allDeliverNum+cancelNum == orderItemList.size())
                return OrderDeliverStatusEnum.ALL_DELIVER.getCode();
        }
        return OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode();
    }

    /**
     * 更新店铺订单供应商订单状态
     * @param platformOrderCode
     * @param shopOrderCode
     */
    private void updateShopOrderSupplierOrderStatus(String platformOrderCode, String shopOrderCode){
        /*OrderItem orderItem = new OrderItem();
        orderItem.setShopOrderCode(shopOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("更新店铺订单供应商订单状态,根据店铺订单号[%s]查询相应的商品明细为空", shopOrderCode));
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setPlatformOrderCode(platformOrderCode);
        shopOrder.setShopOrderCode(shopOrderCode);
        shopOrder = shopOrderService.selectOne(shopOrder);
        AssertUtil.notNull(shopOrder, String.format("更新店铺订单供应商订单状态,根据平台订单编码%s和店铺订单编码%s查询店铺订单信息为空", platformOrderCode, shopOrderCode));
        shopOrder.setSupplierOrderStatus(getSupplierOrderStatusByItems(orderItemList, ZeroToNineEnum.ONE.getCode()));
        shopOrder.setUpdateTime(Calendar.getInstance().getTime());
        shopOrderService.updateByPrimaryKey(shopOrder);*/
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setPlatformOrderCode(platformOrderCode);
        shopOrder.setShopOrderCode(shopOrderCode);
        shopOrder = shopOrderService.selectOne(shopOrder);
        AssertUtil.notNull(shopOrder, String.format("更新店铺订单供应商订单状态,根据平台订单编码%s和店铺订单编码%s查询店铺订单信息为空", platformOrderCode, shopOrderCode));
        shopOrder.setUpdateTime(Calendar.getInstance().getTime());
        shopOrderService.updateByPrimaryKey(shopOrder);
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
        if(waitSendNum + cancelNum == warehouseOrderList.size())
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.WAIT_FOR_DELIVER.getCode());
        //部分发货:
        if(partsDeliverNum > 0)
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.PARTS_DELIVER.getCode());
        //全部发货：
        if(allDeliverNum + cancelNum == warehouseOrderList.size())
            shopOrder.setSupplierOrderStatus(OrderDeliverStatusEnum.ALL_DELIVER.getCode());
    }


    @Override
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
        //设置商品扩展信息
        OrderBase orderBase = new OrderBase();
        BeanUtils.copyProperties(platformOrder, orderBase);
        BeanUtils.copyProperties(orderBase, shopOrder, "buyerMessage", "shopMemo");
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
        Example example2 = new Example(SupplierOrderLogistics.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<SupplierOrderLogistics> supplierOrderLogisticsList = supplierOrderLogisticsService.selectByExample(example2);
        if(!CollectionUtils.isEmpty(supplierOrderLogisticsList)){
            //设置商品供应商订单、物流信息
            for (OrderItem orderItem : orderItemList) {
                StringBuilder sb = new StringBuilder();//供应商订单编码
                int deliverNum = 0;//实发商品数量
                List<DeliverPackageForm> deliverPackageFormList = new ArrayList<>();
                Set<String> supplierSkus = new HashSet<>();
                for(SupplierOrderLogistics supplierOrderLogistics2: supplierOrderLogisticsList){
                    List<SkuInfo> skuInfoList = JSONArray.parseArray(supplierOrderLogistics2.getSkus(), SkuInfo.class);
                    if(!CollectionUtils.isEmpty(skuInfoList)){
                        for(SkuInfo skuInfo: skuInfoList){
                            if(StringUtils.equals(orderItem.getSupplierSkuCode(), skuInfo.getSkuCode())){
                                deliverNum += skuInfo.getNum();
                                if(StringUtils.isBlank(orderItem.getSupplierOrderCode())){
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
                if(sb.length() > 0){
                    orderItem.setSupplierOrderCode(sb.substring(0, sb.length()-1));
                }
                if(StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode(), orderItem.getSupplierOrderStatus()) ||
                        StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus()) ||
                        StringUtils.equals(SupplierOrderStatusEnum.WAIT_FOR_DELIVER.getCode(), orderItem.getSupplierOrderStatus())){
                    orderItem.setDeliverNum(null);
                }else {
                    orderItem.setDeliverNum(deliverNum);
                }
                if(supplierSkus.size() > 0){
                    Example example3 = new Example(ExternalItemSku.class);
                    Example.Criteria criteria3 = example3.createCriteria();
                    criteria3.andIn("supplierSkuCode", supplierSkus);
                    List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example3);
                    for(DeliverPackageForm deliverPackageForm: deliverPackageFormList){
                        for(ExternalItemSku externalItemSku: externalItemSkuList){
                            if(StringUtils.equals(deliverPackageForm.getSkuCode(), externalItemSku.getSupplierSkuCode())){
                                deliverPackageForm.setSkuCode(externalItemSku.getSkuCode());
                            }
                        }
                    }
                }
                orderItem.setDeliverPackageFormList(deliverPackageFormList);
            }
        }
        Example example = new Example(SupplierOrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseOrderCode", warehouseOrderCodes);
        List<SupplierOrderInfo> supplierOrderInfoList = supplierOrderInfoService.selectByExample(example);
        if(!CollectionUtils.isEmpty(supplierOrderInfoList)){
            for (OrderItem orderItem : orderItemList) {
                StringBuilder sb = new StringBuilder();//供应商订单编码
                for(SupplierOrderInfo SupplierOrderInfo: supplierOrderInfoList){
                    List<SkuInfo> skuInfoList = JSONArray.parseArray(SupplierOrderInfo.getSkus(), SkuInfo.class);
                    for(SkuInfo skuInfo: skuInfoList){
                        if(StringUtils.equals(orderItem.getSupplierSkuCode(), skuInfo.getSkuCode())){
                            if(StringUtils.isBlank(orderItem.getSupplierOrderCode())){
                                if(!StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus())){
                                    sb.append(SupplierOrderInfo.getSupplierOrderCode()).append(SupplyConstants.Symbol.COMMA);
                                }
                            }
                        }
                    }
                }
                if(StringUtils.equals(SupplierOrderStatusEnum.ORDER_FAILURE.getCode(), orderItem.getSupplierOrderStatus())){
                    orderItem.setSupplierOrderCode(null);
                }else{
                    if(sb.length() > 0){
                        orderItem.setSupplierOrderCode(sb.substring(0, sb.length()-1));
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
                int days = DateUtils.getDaysBetween(warehouseOrder.getPayTime(), Calendar.getInstance().getTime());
                if(days <= 7){
                    warehouseOrder.setShowCancel(ZeroToNineEnum.ONE.getCode());
                }else {
                    warehouseOrder.setShowCancel(ZeroToNineEnum.ZERO.getCode());
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
                if(StringUtils.isNotBlank(supplierOrderInfo2.getMessage())){
                    sb.append(supplierOrderInfo2.getMessage()).append(SupplyConstants.Symbol.SEMICOLON);
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
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseAck<List<WarehouseOrder>> reciveChannelOrder(String orderInfo) throws Exception {
        AssertUtil.notBlank(orderInfo, "渠道同步订单给供应链订单信息参数不能为空");
        JSONObject orderObj = getChannelOrder(orderInfo);
        //订单检查
        //orderCheck(orderObj);
        //获取平台订单信息
        PlatformOrder platformOrder = getPlatformOrder(orderObj);
        JSONArray shopOrderArray = getShopOrdersArray(orderObj);
        //获取店铺订单
        List<ShopOrder> shopOrderList = getShopOrderList(shopOrderArray, platformOrder.getPlatformType(), platformOrder.getPayTime());
        //拆分自采和代发商品
        List<OrderItem> tmpOrderItemList = new ArrayList<>();//全部商品
        List<OrderItem> selfPurcharseOrderItemList = new ArrayList<>();//自采商品
        List<OrderItem> supplierOrderItemList = new ArrayList<>();//一件代发
        for(ShopOrder shopOrder: shopOrderList){
            for (OrderItem orderItem : shopOrder.getOrderItems()) {
                tmpOrderItemList.add(orderItem);
                if (orderItem.getSkuCode().startsWith(SP0)) {
                    selfPurcharseOrderItemList.add(orderItem);
                }
                if (orderItem.getSkuCode().startsWith(SP1)) {
                    supplierOrderItemList.add(orderItem);
                }
            }
        }
        //校验订单金额
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), channelOrderMoneyCheck)){
            orderMoneyCheck(platformOrder, shopOrderList, tmpOrderItemList);
        }
        //校验商品是否从供应链新增
        isScmItems(tmpOrderItemList, platformOrder.getChannelCode());
        //校验代发商品
        if(supplierOrderItemList.size() > 0){
            checkSupplierItems(supplierOrderItemList);
        }
        //自采商品处理
        List<SkuStock> skuStockList = new ArrayList<>();
        if(selfPurcharseOrderItemList.size() > 0){
            //获取并校验业务线相关仓储信息
            List<WarehouseInfo> warehouseInfoList = getChannelAndCheckWarehouseInfo(platformOrder.getChannelCode());
            //获取自采商品奇门库存
            List<InventoryQueryItemDO> selfItemsInventorys = getSelfItemsQmStock(selfPurcharseOrderItemList, warehouseInfoList, platformOrder.getChannelCode());
            //获取自采商品本地库存
            skuStockList = getSelfItemsLocalStock(selfPurcharseOrderItemList);
            //校验自采商品的可用库存
            checkSelfItemAvailableInventory(selfPurcharseOrderItemList, skuStockList, selfItemsInventorys);
        }
        //拆分仓库订单
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        for (ShopOrder shopOrder : shopOrderList) {
            warehouseOrderList.addAll(dealShopOrder(platformOrder, shopOrder, skuStockList));
        }
        //订单商品明细
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for (WarehouseOrder warehouseOrder : warehouseOrderList) {
            orderItemList.addAll(warehouseOrder.getOrderItemList());
        }
        //保存幂等流水
        saveIdempotentFlow(shopOrderList);
        //保存商品明细
        List<OrderItem> itemList = new ArrayList<>();//一件代发
        for(ShopOrder shopOrder: shopOrderList){
            itemList.addAll(shopOrder.getOrderItems());
        }
        orderItemService.insertList(itemList);
        //保存仓库订单
        warehouseOrderService.insertList(warehouseOrderList);
        //保存商铺订单
        shopOrderService.insertList(shopOrderList);
        //保存平台订单
        platformOrderService.insert(platformOrder);
        //创建订单日志
        createOrderLog(warehouseOrderList);
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
        //提交自采订单
        if(selfPurchaseOrders.size() > 0){
            submitSelfPurchaseOrder(selfPurchaseOrders);
            //更新订单状态
            updateOrderStatusByOutboundOrder(selfPurchaseOrders, itemList);
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "接收订单成功", lyWarehouseOrders);
    }


    /**
     * 获取并校验业务线相关仓储信息
     * @param channelCode
     * @return
     */
    private List<WarehouseInfo> getChannelAndCheckWarehouseInfo(String channelCode){
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setChannelCode(channelCode);
        warehouseInfo.setOwnerWarehouseState(OwnerWarehouseStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        AssertUtil.notEmpty(warehouseInfoList, String.format("业务线%s还没有绑定仓库", channelCode));
        return warehouseInfoList;
    }



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
        sb.append(platformOrder.getChannelCode()).append(SupplyConstants.Symbol.FULL_PATH_SPLIT);
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
    private void orderMoneyCheck(PlatformOrder platformOrder, List<ShopOrder> shopOrders, List<OrderItem> orderItems){
        platformOrderParamCheck(platformOrder, orderItems);
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
            AssertUtil.isTrue(shopOrder.getTotalFee().compareTo(totalFee2) == 0, "店铺订单应付总金额与该店铺所有商品应付总金额不相等");
            if(null != shopOrder.getPostageFee())
                AssertUtil.isTrue(shopOrder.getPostageFee().compareTo(postFee2) == 0, "店铺订单邮费金额与该店铺所有商品邮费总金额不相等");
            if(null != shopOrder.getTotalTax())
                AssertUtil.isTrue(shopOrder.getTotalTax().compareTo(totalTax2) == 0, "店铺订单税费金额与该店铺所有商品税费总金额不相等");
        }
        if(null != platformOrder.getItemNum())
            AssertUtil.isTrue(platformOrder.getItemNum().compareTo(itemsNum) == 0, "平台订单商品数量与所有店铺商品总数量不相等");
        AssertUtil.isTrue(platformOrder.getPayment().compareTo(payment) == 0, "平台订单实付金额与所有店铺实付总金额不相等");
        AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(totalFee) == 0, "平台订单应付总金额与所有店铺应付总金额不相等");
        if(null != platformOrder.getPostageFee())
            AssertUtil.isTrue(platformOrder.getPostageFee().compareTo(postFee) == 0, "平台订单邮费金额与所有店铺邮费总金额不相等");
        if(null != platformOrder.getTotalTax())
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
        noticeChannelOrderResult(warehouseOrder, channelOrderResponse);
    }

    /**
     * 通知渠道订单结果
     * @param warehouseOrder
     * @param channelOrderResponse
     */
    private void noticeChannelOrderResult(Object warehouseOrder, ChannelOrderResponse channelOrderResponse){
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
            log.error(String.format("仓库级订单订单%s提交结果通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(warehouseOrder), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("仓库级订单订单%s提交结果通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(warehouseOrder), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("仓库级订单订单%s提交结果通知渠道成功", JSON.toJSONString(warehouseOrder)));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            log.error(String.format("仓库级订单订单%s提交结果通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(warehouseOrder), toGlyResultDO.getMsg()));
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
        for(SupplierOrderInfo supplierOrderInfo2: supplierOrderInfoList){
            try{
                WarehouseOrder warehouseOrder = new WarehouseOrder();
                warehouseOrder.setWarehouseOrderCode(supplierOrderInfo2.getWarehouseOrderCode());
                warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
                AssertUtil.notNull(warehouseOrder, String.format("定时查询物流信息根据仓库订单编码[%s]查询仓库订单为空", supplierOrderInfo2.getWarehouseOrderCode()));
                SupplierOrderLogistics supplierOrderLogistics = new SupplierOrderLogistics();
                supplierOrderLogistics.setWarehouseOrderCode(supplierOrderInfo2.getWarehouseOrderCode());
                List<SupplierOrderLogistics> supplierOrderLogisticsList = supplierOrderLogisticsService.select(supplierOrderLogistics);
                String flag = "";
                if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_JD_CODE, supplierOrderInfo2.getSupplierCode()))
                    flag = SupplierLogisticsEnum.JD.getCode();
                else if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, supplierOrderInfo2.getSupplierCode()))
                    flag = SupplierLogisticsEnum.LY.getCode();
                if(StringUtils.isNotBlank(flag)){
                    //获取仓库订单编码的物流信息  -----------  一个仓库订单可能会产生多个包裹 -supplier_order_info
                    LogisticForm logisticForm = invokeGetLogisticsInfo(supplierOrderInfo2.getWarehouseOrderCode(), warehouseOrder.getChannelCode(), flag);
                    if(null != logisticForm){
                        String supplierOrderStatus = supplierOrderInfo2.getSupplierOrderStatus();
                        //在这里剔除已经通知了的物流信息(全部发货)
                        if(StringUtils.equals(supplierOrderStatus,SupplierOrderStatusEnum.ALL_DELIVER.getCode())){
                            List<Logistic> logistics = logisticForm.getLogistics();
                            for (Iterator<Logistic> it = logistics.iterator(); it.hasNext();) {
                                Logistic logistic = it.next();
                                for(SupplierOrderLogistics supplierOrderLogistics2: supplierOrderLogisticsList){
                                    if (StringUtils.equals(logistic.getSupplierOrderCode(),supplierOrderLogistics2.getSupplierOrderCode())) {
                                        it.remove();
                                    }
                                }
                            }
                        }
                        //更新供应商订单物流信息
                        updateSupplierOrderLogistics(supplierOrderInfo2, logisticForm);
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
            }catch (Exception e){
                log.error(String.format("更新供应商订单%s物流信息异常,%s", JSONObject.toJSON(supplierOrderInfo2), e.getMessage()), e);
            }
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
                    logisticsInfo=  logisticsInfo.replaceAll(HTML_BR,"");
                    warehouseOrder.setLogisticsInfo(logisticsInfo);
                }
            }
            CellDefinition warehouseOrderCode = new CellDefinition("warehouseOrderCode", "供应商订单编号", CellDefinition.TEXT, 8000);
            CellDefinition supplierName = new CellDefinition("supplierName", "供应商名称", CellDefinition.TEXT, 8000);
            CellDefinition shopOrderCode = new CellDefinition("shopOrderCode", "店铺订单号", CellDefinition.TEXT, 8000);
            CellDefinition itemsNum = new CellDefinition("itemsNum", "商品总数量", CellDefinition.NUM_0, 8000);
            CellDefinition payment = new CellDefinition("payment", "商品总金额(元)", CellDefinition.NUM_0_00, 8000);
            CellDefinition payTime = new CellDefinition("payTime", "付款时间", CellDefinition.DATE_TIME, 8000);
            CellDefinition supplierOrderStatus = new CellDefinition("supplierOrderStatus", "状态", CellDefinition.TEXT, 8000);
            CellDefinition logisticsInfo = new CellDefinition("logisticsInfo", "反馈物流公司名称-反馈运单号", CellDefinition.TEXT, 16000);

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
        updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
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
                WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(supplierOrderInfo.getWarehouseOrderCode());
                //更新店铺订单供应商订单状态
                updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
                //记录操作日志
                logInfoService.recordLog(warehouseOrder,warehouseOrder.getId().toString(), SYSTEM, LogOperationEnum.ORDER_CANCEL.getMessage(), SUPPLIER_PLATFORM_CANCEL_ORDER,null);
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
        supplierOrderReturn.setState(ZeroToNineEnum.TWO.getCode());//订单取消
        supplierOrderReturn.setMessage(SUPPLIER_PLATFORM_CANCEL_ORDER);
        supplierOrderReturn.setSkus(getSupplierOrderReturnSkuInfo(supplierOrderInfo, orderItemList));
        supplierOrderReturnList.add(supplierOrderReturn);
        channelOrderResponse.setOrder(supplierOrderReturnList);
        //通知渠道订单结果
        noticeChannelOrderResult(warehouseOrder, channelOrderResponse);
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
            WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(supplierOrderInfo.getWarehouseOrderCode());
            //更新店铺订单供应商订单状态
            updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
        }
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
            LogisticsCompany logisticsCompany = new LogisticsCompany();
            logisticsCompany.setType(channelCode);
            logisticsCompany.setCompanyName(logistic.getLogisticsCorporation());
            logisticsCompany = logisticsCompanyService.selectOne(logisticsCompany);
            AssertUtil.notNull(logisticsCompany, String.format("根据type[%s]和companyName[%s]查询物流公司信息为空", channelCode, logistic.getLogisticsCorporation()));
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
            AssertUtil.notNull(shopOrderObj, "接收渠道订单参数中平店铺订单信息为空");
            ShopOrder shopOrder = JSONObject.parseObject(tmpObj.getString("shopOrder"),ShopOrder.class);
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
            List<OrderItem> orderItemList = getOrderItem(orderItemArray);
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
    private List<OrderItem> getOrderItem(JSONArray orderItemArray){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        for(Object obj: orderItemArray){
            JSONObject orderItemObj = (JSONObject)obj;
            OrderItem orderItem = JSONObject.parseObject(orderItemObj.toJSONString(),OrderItem.class);
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
            orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode());//待发送供应商
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 校验是否供应链商品
     * @param orderItemList
     * @param channelCode
     */
    private void isScmItems(List<OrderItem> orderItemList, String channelCode){
        Set<String> skuCodes = new HashSet<String>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("channelCode", channelCode);
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
    private void checkSupplierItems(List<OrderItem> orderItemList){
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
        List<String> _tmpSkuCodes = new ArrayList<String>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), externalItemSku.getState()))
                _tmpSkuCodes.add(externalItemSku.getSkuCode());
        }
        if(_tmpSkuCodes.size() > 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("代发商品[%s]的供应商商品状态为下架!", CommonUtil.converCollectionToString(_tmpSkuCodes)));
        }
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

    }



    /**
     * 获取自采商品奇门库存
     * @param orderItemList
     * @param channelCode 渠道编码
     */
    private List<InventoryQueryItemDO> getSelfItemsQmStock(List<OrderItem> orderItemList, List<WarehouseInfo> warehouseInfoList, String channelCode){
        List<Long> warehouseInfoIds = new ArrayList<>();
        for(WarehouseInfo warehouseInfo2: warehouseInfoList){
            warehouseInfoIds.add(warehouseInfo2.getId());
        }
        List<String> skuCodes = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            skuCodes.add(orderItem.getSkuCode());
        }
        //查询跟仓库绑定过的商品,其中没有绑定过的在后面的拆单时会归到异常订单里面
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseInfoId", warehouseInfoIds);
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("itemType", ItemTypeEnum.NOEMAL.getCode());//正常的商品
        criteria.andEqualTo("noticeStatus", ItemNoticeStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
        AssertUtil.notEmpty(warehouseItemInfoList, String.format("业务线%s还没有跟仓库绑定商品", channelCode));
        Set<String> warehouseOwnerIds = new HashSet<>();
        for(WarehouseInfo warehouseInfo: warehouseInfoList){
            warehouseOwnerIds.add(warehouseInfo.getWarehouseOwnerId());
        }
        Map<String, List<WarehouseOwernSkuDO>> map = new HashMap<>();
        for(String warehouseOwnerId: warehouseOwnerIds){
            List<WarehouseOwernSkuDO> warehouseOwernSkuDOList = new ArrayList<>();
            for(WarehouseInfo warehouseInfo: warehouseInfoList){
                List<WarehouseItemInfo> tmpWarehouseItemInfoList = new ArrayList<>();
                WarehouseOwernSkuDO warehouseOwernSkuDO = new WarehouseOwernSkuDO();
                if(StringUtils.equals(warehouseOwnerId, warehouseInfo.getWarehouseOwnerId())){
                    for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                        if(warehouseItemInfo.getWarehouseInfoId().longValue() == warehouseInfo.getId().longValue()){
                            tmpWarehouseItemInfoList.add(warehouseItemInfo);
                        }
                    }
                }
                if(tmpWarehouseItemInfoList.size() > 0){
                    warehouseOwernSkuDO.setWarehouseInfo(warehouseInfo);
                    warehouseOwernSkuDO.setWarehouseItemInfoList(tmpWarehouseItemInfoList);
                    warehouseOwernSkuDOList.add(warehouseOwernSkuDO);
                }
            }
            if(warehouseOwernSkuDOList.size() > 0){
                map.put(warehouseOwnerId, warehouseOwernSkuDOList);
            }
        }
        Set<Map.Entry<String, List<WarehouseOwernSkuDO>>> entries = map.entrySet();
        List<InventoryQueryItemDO> inventoryQueryItemDOList = new ArrayList<>();
        for(Map.Entry<String, List<WarehouseOwernSkuDO>> entry: entries){
            inventoryQueryItemDOList.addAll(getQimenStockByWarehouseOwnerId(entry));
        }
        return inventoryQueryItemDOList;
    }

    /**
     * 根据货主ID获取奇门库存
     * @param entry
     * @return
     */
    private List<InventoryQueryItemDO> getQimenStockByWarehouseOwnerId(Map.Entry<String, List<WarehouseOwernSkuDO>> entry){
        String warehouseOwnerId = entry.getKey();
        List<WarehouseOwernSkuDO> warehouseOwernSkuDOList = entry.getValue();
        //调用奇门库存查询接口校验绑定过商品的库存
        InventoryQueryRequest request = new InventoryQueryRequest();
        InventoryQueryRequest.Criteria criteria = new InventoryQueryRequest.Criteria();
        List<InventoryQueryRequest.Criteria> criteriaList = new ArrayList<>();
        for(WarehouseOwernSkuDO warehouseOwernSkuDO: warehouseOwernSkuDOList){
            WarehouseInfo warehouseInfo = warehouseOwernSkuDO.getWarehouseInfo();
            for(WarehouseItemInfo warehouseItemInfo: warehouseOwernSkuDO.getWarehouseItemInfoList()){
                criteria.setItemCode(warehouseItemInfo.getSkuCode());
                criteria.setItemId(warehouseItemInfo.getWarehouseItemId());
                criteria.setInventoryType(InventoryTypeEnum.ZP.getCode());//正品
                criteria.setWarehouseCode(warehouseInfo.getQimenWarehouseCode());
                criteria.setOwnerCode(warehouseOwnerId);
                criteriaList.add(criteria);
            }
        }
        request.setCriteriaList(criteriaList);
        AppResult appResult = qimenService.inventoryQuery(request);
        if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("调用奇门库存查询接口失败, %s", appResult.getDatabuffer()));
        }
        AssertUtil.notNull(appResult.getResult(), "调用奇门库存查询接口返回结果数据为空");
        AssertUtil.notBlank(appResult.getResult().toString(), "调用奇门库存查询接口返回结果数据为空");
        InventoryQueryResponse inventoryQueryResponse = null;
        try{
            inventoryQueryResponse = JSON.parseObject(appResult.getResult().toString()).toJavaObject(InventoryQueryResponse.class);
        }catch (ClassCastException e) {
            String msg = String.format("调用奇门库存查询接口返回库存结果信息格式错误,%s", e.getMessage());
            log.error(msg, e);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        List<InventoryQueryItemDO> inventoryQueryItemDOList = new ArrayList<>();
        if(inventoryQueryResponse.isSuccess()){
            for(InventoryQueryResponse.Item item: inventoryQueryResponse.getItems()){
                InventoryQueryItemDO inventoryQueryItemDO = new InventoryQueryItemDO();
                BeanUtils.copyProperties(item, inventoryQueryItemDO);
                inventoryQueryItemDO.setOwnerCode(warehouseOwnerId);
                inventoryQueryItemDOList.add(inventoryQueryItemDO);
            }
        }else {
            throw new QimenException(ExceptionEnum.QIMEN_INVENTORY_QUERY_EXCEPTION, inventoryQueryResponse.getMessage());
        }
        return inventoryQueryItemDOList;
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
        return skuStockService.selectByExample(example);
    }

    /**
     * 校验自采商品的可用库存
     * @param skuStockList
     * @return
     */
    private void checkSelfItemAvailableInventory(List<OrderItem> orderItemList, List<SkuStock> skuStockList, List<InventoryQueryItemDO> inventoryQueryItemDOList){
        List<String> skuCodeList = new ArrayList<>();
        for(OrderItem orderItem: orderItemList){
            skuCodeList.add(orderItem.getSkuCode());
        }
        boolean flag = false;
        for(OrderItem orderItem: orderItemList){
            boolean _flag = false;
            //本地库存
            long localStock = 0;
            for(SkuStock skuStock: skuStockList){
                if(StringUtils.equals(orderItem.getSkuCode(), skuStock.getSkuCode())){
                    //可用库存
                    long availableInventory = skuStock.getRealInventory() - skuStock.getFrozenInventory();
                    localStock += availableInventory;
                }
            }
            //奇门库存
            long qimenStock = 0;
            for(InventoryQueryItemDO item: inventoryQueryItemDOList){
                if(StringUtils.equals(orderItem.getSkuCode(), item.getItemCode())){
                    qimenStock += item.getQuantity();
                }
            }
            //校验库存,本地库存和奇门库存以小的为准
            if(localStock >= qimenStock){
                if(qimenStock >= orderItem.getNum().longValue()){
                    if(!flag){
                        flag = true;
                    }
                    _flag = true;
                }
            }else{
                if(localStock >= orderItem.getNum().longValue()){
                    if(!flag){
                        flag = true;
                    }
                    _flag = true;
                }
            }
            if(!_flag){
                orderItem.setSupplierOrderStatus(OrderItemDeliverStatusEnum.ORDER_FAILURE.getCode());//供应商下单失败
            }
        }
        if(!flag){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("自采商品[%s]库存不足", CommonUtil.converCollectionToString(skuCodeList)));
        }
    }


    /**
     * 校验渠道
     * @param channelCode
     */
    private void checkChannel(String channelCode){
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setType(SupplyConstants.SystemConfigType.CHANNEL);
        systemConfig.setCode(channelCode);
        List<SystemConfig> systemConfigList = systemConfigService.select(systemConfig);
        AssertUtil.notEmpty(systemConfigList, "不是供应链授权访问的渠道，非法访问");
    }

    /**
     * 平台订单校验
     *
     * @param platformOrder
     */
    private void platformOrderParamCheck(PlatformOrder platformOrder, List<OrderItem> orderItems) {
        AssertUtil.notBlank(platformOrder.getChannelCode(), "渠道编码不能为空");
        checkChannel(platformOrder.getChannelCode());
        AssertUtil.notBlank(platformOrder.getPlatformCode(), "来源平台编码不能为空");
        AssertUtil.notBlank(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
        AssertUtil.notBlank(platformOrder.getUserId(), "平台订单会员id不能为空");
        AssertUtil.notBlank(platformOrder.getUserName(), "平台订单会员名称不能为空");

        AssertUtil.notBlank(platformOrder.getReceiverName(), "平台订单收货人姓名不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverMobile(), "平台订单收货人手机号码不能为空");

        AssertUtil.notBlank(platformOrder.getReceiverProvince(), "平台订单收货人所在省不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverCity(), "平台订单收货人所在城市不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverDistrict(), "平台订单收货人所在地区不能为空");
        AssertUtil.notBlank(platformOrder.getReceiverAddress(), "平台订单收货人详细地址不空");

        AssertUtil.notBlank(platformOrder.getPayType(), "平台订单单支付类型不能为空");
        AssertUtil.notBlank(platformOrder.getStatus(), "平台订单订单状态不能为空");
        AssertUtil.notBlank(platformOrder.getType(), "平台订单订单类型不能为空");
        AssertUtil.notNull(platformOrder.getPayTime(), "平台订单支付时间不能为空");

        AssertUtil.isTrue(platformOrder.getItemNum() > 0, "买家购买的商品总数不能为空");
        AssertUtil.isTrue(platformOrder.getTotalFee().compareTo(new BigDecimal(0))==0 || platformOrder.getTotalFee().compareTo(new BigDecimal(0))==1, "平台订单总金额应大于等于0");
        AssertUtil.isTrue(platformOrder.getPayment().compareTo(new BigDecimal(0))==0 || platformOrder.getPayment().compareTo(new BigDecimal(0))==1 ,"平台订单实付金额应大于等于0");

        List<String> skusList = new ArrayList<>();
        for(OrderItem orderItem: orderItems){
            skusList.add(orderItem.getSkuCode());
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
     * 店铺订单校验
     * @param shopOrder
     */
    private void shopOrderParamCheck(ShopOrder shopOrder) {
        AssertUtil.notBlank(shopOrder.getChannelCode(), "店铺订单渠道编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformCode(), "店铺订单来源平台编码不能为空");
        AssertUtil.notBlank(shopOrder.getPlatformOrderCode(), "店铺订单平台订单编码不能为空");
        AssertUtil.notBlank(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
        //AssertUtil.notBlank(shopOrder.getPlatformType(), "店铺订单订单来源类型不能为空");
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
        //AssertUtil.isTrue(totalFee.compareTo(orderItem.getTotalFee())==0, "订单商品价格*商品数量应等于订单应付金额totalFee");
    }


    /**
     * 拆分店铺级订单
     * @param platformOrder
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealShopOrder(PlatformOrder platformOrder, ShopOrder shopOrder, List<SkuStock> skuStockList) {
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
    }


    /**
     * 处理自采订单
     * @param orderItems
     * @param shopOrder
     * @return
     */
    public List<WarehouseOrder> dealSelfPurcharseOrder(List<OrderItem> orderItems, PlatformOrder platformOrder,
                 ShopOrder shopOrder, List<SkuStock> skuStockList) {
        List<WarehouseOrder> warehouseOrderList = new ArrayList<WarehouseOrder>();
        List<Warehouse> warehouseList = new ArrayList<>();
        Set<String> warehouseCodes = new HashSet<>();
        for(SkuStock skuStock: skuStockList){
            warehouseCodes.add(skuStock.getWarehouseCode());
        }
        Example example2 = new Example(Warehouse.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("code", warehouseCodes);
        warehouseList = warehouseService.selectByExample(example2);
        AssertUtil.notEmpty(warehouseList, String.format("根据仓库编码列表[%s]查询仓库为空", CommonUtil.converCollectionToString(new ArrayList<>(warehouseCodes))));
        //sku和仓库可用库存关系,一个sku对应多个仓库可用库存
        Map<String, List<SkuWarehouseDO>> warehouseSkuMap = getSkuWarehouseRelation(orderItems, skuStockList);
        /**
         * 异常订单商品处理
         * 目前只判断库存不足的情况
         */
        List<ExceptionOrderItem> exceptionOrderItemList = new ArrayList<>();//失败的商品
        Set<String> warehouses = new HashSet<>();//所有匹配库存的仓库
        Iterator<Map.Entry<String, List<SkuWarehouseDO>>> entries = warehouseSkuMap.entrySet().iterator();
        for(OrderItem orderItem: orderItems){
            boolean flag = false;
            while (entries.hasNext()){
                Map.Entry<String, List<SkuWarehouseDO>> entry = entries.next();
                String skuCode = entry.getKey();
                if(StringUtils.equals(orderItem.getSkuCode(), skuCode)){
                    flag = true;
                    List<SkuWarehouseDO> skuWarehouseDOList = entry.getValue();
                    for(SkuWarehouseDO skuWarehouseDO: skuWarehouseDOList){
                        warehouses.add(skuWarehouseDO.getWarehouseCode());
                    }
                    break;
                }
            }
            if(!flag){
                ExceptionOrderItem exceptionOrderItem = getExceptionOrderItem(shopOrder, orderItem, SupplyConstants.ExceptionOrder.ALL_WAREHOUSE_STOCK_LESS);
                exceptionOrderItemList.add(exceptionOrderItem);
                //设置订单商品状态跟异常单状态一致
                orderItem.setSupplierOrderStatus(exceptionOrderItem.getStatus().toString());
            }
        }
        if(exceptionOrderItemList.size() > 0){
            //保存拆单异常信息
            saveExceptionOrder(platformOrder, shopOrder, exceptionOrderItemList);
        }
        //创建仓库订单
        if(warehouses.size() > 0){
            Example example3 = new Example(Warehouse.class);
            Example.Criteria criteria3 = example3.createCriteria();
            criteria3.andIn("code", warehouses);
            List<Warehouse> warehouseList3 = warehouseService.selectByExample(example3);
            for(String warehouseCode: warehouses){
                boolean flag = false;
                for(Warehouse warehouse: warehouseList3){
                    if(StringUtils.equals(warehouseCode, warehouse.getCode())){
                        flag = true;
                        break;
                    }
                }
                AssertUtil.isTrue(flag, String.format("根据仓库订单编码[%s]查询仓库信息为空", warehouseCode));
            }
            for(Warehouse warehouse: warehouseList3){
                List<OrderItem> warehouseOrderItemList = new ArrayList<>();
                entries = warehouseSkuMap.entrySet().iterator();
                while (entries.hasNext()){
                    Map.Entry<String, List<SkuWarehouseDO>> entry = entries.next();
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
                        if(StringUtils.equals(orderItem.getSkuCode(), orderItem2.getSkuCode())){
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
    private WarehouseOrder getSelfWarehouseOrder(Warehouse warehouse, List<OrderItem> orderItems, ShopOrder shopOrder){
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        //warehouseOrder.setSupplierCode(supplier.getSupplierInterfaceId());
        warehouseOrder.setWarehouseId(warehouse.getId());
        warehouseOrder.setWarehouseCode(warehouse.getCode());
        warehouseOrder.setWarehouseName(warehouse.getName());
        warehouseOrder.setShopId(shopOrder.getShopId());
        warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
        warehouseOrder.setShopName(shopOrder.getShopName());
        warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
        warehouseOrder.setChannelCode(shopOrder.getChannelCode());
        warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
        warehouseOrder.setPlatformType(shopOrder.getPlatformType());
        warehouseOrder.setUserId(shopOrder.getUserId());
        warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
        warehouseOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        warehouseOrder.setPayTime(shopOrder.getPayTime());
        warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode());
        warehouseOrder.setOrderType(OrderTypeEnum.SELF_PURCHARSE.getCode());//自采
        //流水号
        String code = serialUtilService.generateRandomCode(Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()), SupplyConstants.Serial.WAREHOUSE_ORDER,
                SupplyConstants.Serial.WAREHOUSE_ORDER_CODE, ZeroToNineEnum.ONE.getCode(), DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        warehouseOrder.setWarehouseOrderCode(code);
        setWarehouseOrderFee(warehouseOrder, orderItems);
        warehouseOrder.setOrderItemList(orderItems);
        return warehouseOrder;
    }



    private Map<String, List<SkuWarehouseDO>> getSkuWarehouseRelation(List<OrderItem> orderItems, List<SkuStock> skusStockList) {
        /**
         * 商品库存匹配策略：
         * 1、目前只校验库存是否满足，如果不满足则是异常单
         * 2、匹配仓库库存的时候，随机匹配仓库，优先将同一个订单中的商品匹配在同一个仓库中
         * 3、如果一个商品库存不能再一个仓库匹配，那么分配到多个仓库
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
     * @param shopOrder
     * @param orderItem
     * @param exceptionReason
     */
    private ExceptionOrderItem getExceptionOrderItem(ShopOrder shopOrder, OrderItem orderItem, String exceptionReason){
        ExceptionOrderItem exceptionOrderItem = new ExceptionOrderItem();
        exceptionOrderItem.setShopOrderCode(shopOrder.getShopOrderCode());
        exceptionOrderItem.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
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
        Date currentDate = Calendar.getInstance().getTime();
        exceptionOrderItem.setCreateTime(currentDate);
        exceptionOrderItem.setUpdateTime(currentDate);
        return exceptionOrderItem;
    }

    /**
     * 保存异常单信息
     * @param platformOrder
     * @param shopOrder
     * @param exceptionOrderItemList
     */
    private void saveExceptionOrder(PlatformOrder platformOrder, ShopOrder shopOrder, List<ExceptionOrderItem> exceptionOrderItemList){
        String code = serialUtilService.generateCode(SupplyConstants.Serial.EXCEPTION_ORDER_LENGTH, SupplyConstants.Serial.EXCEPTION_ORDER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        ExceptionOrder exceptionOrder = new ExceptionOrder();
        exceptionOrder.setChannelCode(shopOrder.getChannelCode());
        exceptionOrder.setSellCode(platformOrder.getSellCode());
        exceptionOrder.setExceptionOrderCode(code);
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
            itemNum += exceptionOrderItem.getItemNum();
        }
        exceptionOrder.setItemNum(itemNum);
        for(ExceptionOrderItem exceptionOrderItem: exceptionOrderItemList){
            exceptionOrderItem.setExceptionOrderCode(code);
        }

        exceptionOrderService.insert(exceptionOrder);
        exceptionOrderItemService.insertList(exceptionOrderItemList);

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
            warehouseOrder.setSupplierCode(supplier.getSupplierInterfaceId());
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
            warehouseOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehouseOrder.setPayTime(shopOrder.getPayTime());
            warehouseOrder.setSupplierOrderStatus(SupplierOrderStatusEnum.WAIT_FOR_SUBMIT.getCode());
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
    @Cacheable(key="#form.toString()+#aclUserAccreditInfo.toString()+#page.pageNo+#page.pageSize",isList=true)
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
    public ResponseAck outboundConfirmNotice(String warehouseOrderCode) {
        AssertUtil.notBlank(warehouseOrderCode, "发货通知单发货明细确认通知参数仓库订单编码warehouseOrderCode不能为空");
        //更新仓库订单供应商订单状态
        WarehouseOrder warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrderCode);
        //更新店铺订单供应商订单状态
        updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "通知接收成功", "");
    }


    /**
     * 提交自采订单
     * @param warehouseOrderList
     * @return
     */
    public ResponseAck submitSelfPurchaseOrder(List<WarehouseOrder> warehouseOrderList) throws Exception {
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(warehouseOrderList.get(0).getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("提交自采订单跟据平台订单编码%s查询平台订单信息为空", warehouseOrderList.get(0).getPlatformOrderCode()));
        Set<String> shopOrderCodes = new HashSet<>();
        Set<String> warehouseOrderCodes = new HashSet<>();
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            shopOrderCodes.add(warehouseOrder.getShopOrderCode());
            warehouseOrderCodes.add(warehouseOrder.getWarehouseOrderCode());
        }
        Example shopOrderExample = new Example(ShopOrder.class);
        Example.Criteria criteria = shopOrderExample.createCriteria();
        criteria.andIn("shopOrderCode", shopOrderCodes);
        List<ShopOrder> shopOrderList = shopOrderService.selectByExample(shopOrderExample);
        for(String shopOrderCode : shopOrderCodes){
            boolean flag = false;
            for(ShopOrder shopOrder: shopOrderList){
                if(StringUtils.equals(shopOrderCode, shopOrder.getShopOrderCode())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("提交自采订单跟据店铺订单编码%s查询店铺订单信息为空", shopOrderCode));
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
            for(OrderItem orderItem: orderItemList){
                if(StringUtils.equals(orderItem.getWarehouseOrderCode(), warehouseOrder.getWarehouseOrderCode()) &&
                        StringUtils.equals(orderItem.getSupplierOrderStatus(), OrderItemDeliverStatusEnum.WAIT_FOR_SUBMIT.getCode())){
                    orderItems.add(orderItem);
                }
            }
            //创建发货通知单
            OutboundForm outboundForm  = createOutboundOrder(platformOrder, warehouseOrder, _shopOrder, orderItems);
            outboundMap.put(outboundForm.getOutboundOrder().getOutboundOrderCode(), outboundForm);
        }
        //更新订单商品占用库存
        frozenOrderInventory(outboundMap);
        //通知仓库发货
        noticeWarehouseSendGoods(platformOrder.getChannelCode(), outboundMap);
        
        //通知渠道发货结果 .....
        
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "提交自采订单成功", "");
    }
    
    /**
     * 自采商品发货结果通知渠道
     * @param shopOrderCodes 店铺订单列表
     * @param warehouseOrderList 仓库级订单列表
     */
    private void notifyChannelSelfPurchaseSubmitOrderResult(Set<String> shopOrderCodes, List<WarehouseOrder> warehouseOrderList) {
    	// 渠道平台订单编码
    	String platformOrderCode = warehouseOrderList.get(0).getPlatformOrderCode();
    	for (String shopOrderCode : shopOrderCodes) {
    		try {
    			/** 
    			 * 根据shopOrderCode筛选出warehouseOrderList
    			 **/
    			List<WarehouseOrder> filterList = warehouseOrderList.stream()
    					.filter(order -> shopOrderCode.equals(order.getShopOrderCode())).collect(Collectors.toList());
    			/** 
    			 * 通知渠道数据封装 
    			 * channelOrderResponse
    			 **/
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
    				Map<String, String> returnMsgMap = new HashMap<>();
    				returnOrder.setSkus(generateSkuList(order.getWarehouseOrderCode(), shopOrderCode, returnMsgMap));
    				if (StringUtils.isNotBlank(returnMsgMap.get("retMsg"))) {
    					returnOrder.setMessage(returnMsgMap.get("retMsg"));
    				}
    				orderList.add(returnOrder);
    			}
    			orderRes.setOrder(orderList);	        
    			noticeChannelOrderResult(warehouseOrderList, orderRes);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.error("店铺订单: {}, 自采商品发货结果通知渠道异常:{}", shopOrderCode, e.getMessage());
    		}
    	}

    }
    
    private List<SkuInfo> generateSkuList(String warehouseOrderCode, String shopOrderCode, Map<String, String> returnMsgMap) {
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
    	/** 
    	 * 异常skus
    	 **/
    	ExceptionOrderItem queryItem = new ExceptionOrderItem();
    	queryItem.setShopOrderCode(shopOrderCode);
    	List<ExceptionOrderItem> itemList = exceptionOrderItemService.select(queryItem);
    	if (!CollectionUtils.isEmpty(itemList)) {
    		StringBuilder msg = new StringBuilder();
    		for (ExceptionOrderItem item : itemList) {
    			SkuInfo info = new SkuInfo();
    			info.setSkuCode(item.getSkuCode());
    			info.setNum(item.getItemNum());
    			info.setSkuName(item.getItemName());
    			msg.append(item.getSkuCode());
    			msg.append(":");
    			msg.append(item.getExceptionReason());
    			msg.append(",");
    			infoList.add(info);
    		}
    	  	/** 
        	 * 异常信息组装
        	 **/
    		String reMsg = msg.toString();
    		reMsg = reMsg.substring(0, reMsg.length() - 1); 
    		returnMsgMap.put("retMsg", reMsg);
    	}
		return infoList;
    }
    
    private String getOutBundStatus(String originStaus) {
    	return OutboundOrderStatusEnum.RECEIVE_FAIL.getCode().equals(originStaus) ? "0":"200";
    }

    /**
     * 根据发货通知单更新订单状态
     * @param warehouseOrderList
     */
    private void updateOrderStatusByOutboundOrder(List<WarehouseOrder> warehouseOrderList, List<OrderItem> itemList){
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
        //更新订单商品明细
        Date currentDate = Calendar.getInstance().getTime();
        for(OutboundDetail outboundDetail: outboundDetailList){
            for(OrderItem orderItem: itemList){
                if(StringUtils.equals(outboundDetail.getSkuCode(), orderItem.getSkuCode())){
                    orderItem.setSupplierOrderStatus(outboundDetail.getStatus());
                    orderItem.setUpdateTime(currentDate);
                    orderItemService.updateByPrimaryKeySelective(orderItem);
                    break;
                }
            }
        }
        for(WarehouseOrder warehouseOrder: warehouseOrderList){
            //更新仓库订单供应商订单状态
            warehouseOrder = updateWarehouseOrderSupplierOrderStatus(warehouseOrder.getWarehouseOrderCode());
            //更新店铺订单供应商订单状态
            updateShopOrderSupplierOrderStatus(warehouseOrder.getPlatformOrderCode(), warehouseOrder.getShopOrderCode());
        }

    }



    /**
     * 更新订单商品占用库存
     */
    private void frozenOrderInventory(Map<String, OutboundForm> outboundMap) throws Exception {
        List<RequsetUpdateStock> updateStockList = new ArrayList<RequsetUpdateStock>();
        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
            List<OutboundDetail> outboundDetailList = entry.getValue().getOutboundDetailList();
            for(OutboundDetail detail : outboundDetailList){
                RequsetUpdateStock requsetUpdateStock = new RequsetUpdateStock();
                Map<String, String> stockType = new HashMap<String, String>();
                stockType.put("frozen_inventory", String.valueOf((detail.getShouldSentItemNum())));
                requsetUpdateStock.setStockType(stockType);
                requsetUpdateStock.setChannelCode(outboundOrder.getChannelCode());
                requsetUpdateStock.setWarehouseCode(outboundOrder.getWarehouseCode());
                requsetUpdateStock.setSkuCode(detail.getSkuCode());
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
    OutboundForm createOutboundOrder(PlatformOrder platformOrder, WarehouseOrder warehouseOrder, ShopOrder shopOrder, List<OrderItem> orderItemList){
        OutboundOrder outboundOrder = new OutboundOrder();
        //流水号
        String code = serialUtilService.generateCode(SupplyConstants.Serial.OUTBOUND_ORDER_LENGTH, SupplyConstants.Serial.OUTBOUND_ORDER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        outboundOrder.setChannelCode(platformOrder.getChannelCode());
        outboundOrder.setOutboundOrderCode(code);
        outboundOrder.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        outboundOrder.setWarehouseCode(warehouseOrder.getWarehouseCode());
        outboundOrder.setShopId(shopOrder.getShopId());
        outboundOrder.setShopName(shopOrder.getShopName());
        outboundOrder.setShopOrderCode(shopOrder.getShopOrderCode());
        outboundOrder.setWarehouseId(warehouseOrder.getWarehouseId());
        outboundOrder.setOrderType(QimenOrderTypeEnum.JYCK.getCode());
        outboundOrder.setStatus(OutboundOrderStatusEnum.WAITING.getCode());
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
        outboundOrder.setReceiverPhone(platformOrder.getReceiverPhone());
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
            outboundDetail.setStatus(OutboundDetailStatusEnum.WAITING.getCode());
            outboundDetail.setCreateTime(currentTime);
            outboundDetail.setUpdateTime(currentTime);
            outboundDetailList.add(outboundDetail);
        }

        outBoundOrderService.insert(outboundOrder);
        outboundDetailService.insertList(outboundDetailList);

        OutboundForm outboundForm = new OutboundForm();
        outboundForm.setOutboundOrder(outboundOrder);
        outboundForm.setOutboundDetailList(outboundDetailList);
        return outboundForm;
    }


    /**
     * 通知仓库发货
     * @param outboundMap
     */
    private void noticeWarehouseSendGoods(String channelCode, Map<String, OutboundForm> outboundMap){
        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        //查询所有发货单相关仓库信息
        Set<String> warehouseCodes = new HashSet<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundForm outboundForm = entry.getValue();
            warehouseCodes.add(outboundForm.getOutboundOrder().getWarehouseCode());
        }
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouseCodes);
        criteria.andEqualTo("channelCode", channelCode);
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        AssertUtil.notEmpty(warehouseInfoList, String.format("根据仓库编码[%s]和业务线[%s]查询仓储信息为空", CommonUtil.converCollectionToString(new ArrayList<>(warehouseCodes)), channelCode));
        Example example2 = new Example(Warehouse.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("code", warehouseCodes);
        List<Warehouse> warehouseList = warehouseService.selectByExample(example2);
        AssertUtil.notEmpty(warehouseList, String.format("根据仓库编码[%s]查询仓库信息为空", CommonUtil.converCollectionToString(new ArrayList<>(warehouseCodes))));
        Map<String, OutboundForm> outboundMap2 = new HashedMap(outboundMap);
        //替换发货通知单中的仓库编码
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundForm outboundForm = entry.getValue();
            OutboundOrder outboundOrder = outboundForm.getOutboundOrder();
            for(Warehouse warehouse: warehouseList){
                if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouse.getCode())){
                    outboundOrder.setWarehouseCode(warehouse.getCode());
                    break;
                }
            }
            outboundForm.setOutboundOrder(outboundOrder);
            outboundMap.put(entry.getKey(), outboundForm);
        }
        //处理发货通知单创建参数
        List<DeliveryorderBatchcreateRequest.Order> orderList = new ArrayList<>();
        for(Map.Entry<String, OutboundForm> entry: entries){
            OutboundForm outboundForm = entry.getValue();
            OutboundOrder outboundOrder = outboundForm.getOutboundOrder();
            List<OutboundDetail> outboundDetailList = outboundForm.getOutboundDetailList();
            DeliveryorderBatchcreateRequest.Order order = new DeliveryorderBatchcreateRequest.Order();
            order.setDeliveryOrder(getDeliveryOrder(outboundOrder, warehouseList));
            order.setOrderLines(getDeliveryOrderLines(outboundOrder, outboundDetailList, warehouseInfoList));
            orderList.add(order);
        }
        //调用奇门创建发货单接口(批量)
        DeliveryorderBatchcreateResponse deliveryorderBatchcreateResponse = invokeDeliveryorderBatchcreate(orderList);
        //更新发货单状态
        updateOutboudOrderStatus(outboundMap2, deliveryorderBatchcreateResponse);
    }

    /**
     * 更新发货单状态
     * @param outboundMap
     * @param deliveryorderBatchcreateResponse
     */
    private void updateOutboudOrderStatus(Map<String, OutboundForm> outboundMap, DeliveryorderBatchcreateResponse deliveryorderBatchcreateResponse){
        Set<Map.Entry<String, OutboundForm>> entries = outboundMap.entrySet();
        List<String> outboundCodes = new ArrayList<>();
        if(deliveryorderBatchcreateResponse.isSuccess()){
            List<DeliveryorderBatchcreateResponse.Order> orderList = deliveryorderBatchcreateResponse.getOrders();
            if(!CollectionUtils.isEmpty(orderList)){
                for(DeliveryorderBatchcreateResponse.Order order: orderList){
                    outboundCodes.add(order.getDeliveryOrderCode());
                }
            }
        }else {
            for(Map.Entry<String, OutboundForm> entry: entries){
                outboundCodes.add(entry.getKey());
            }
        }
        if(outboundCodes.size() > 0){
            Date currentTime = Calendar.getInstance().getTime();
            //更新发货通知单状态
            Example example = new Example(OutboundOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("outboundOrderCode", outboundCodes);
            OutboundOrder outboundOrder = new OutboundOrder();
            outboundOrder.setStatus(OutboundOrderStatusEnum.RECEIVE_FAIL.getCode());
            outboundOrder.setUpdateTime(currentTime);
            outBoundOrderService.updateByExampleSelective(outboundOrder, example);
            //更新发货通知单明细状态
            Example example2 = new Example(OutboundDetail.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("outboundOrderCode", outboundCodes);
            OutboundDetail outboundDetail = new OutboundDetail();
            outboundDetail.setStatus(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode());
            outboundDetail.setUpdateTime(currentTime);
            outboundDetailService.updateByExampleSelective(outboundDetail, example2);
        }
        //记录操作日志
        if(outboundCodes.size() > 0){
            //失败日志
            List<DeliveryorderBatchcreateResponse.Order> orderList = deliveryorderBatchcreateResponse.getOrders();
            for(DeliveryorderBatchcreateResponse.Order order: orderList){
                for(Map.Entry<String, OutboundForm> entry: entries){
                    if(StringUtils.equals(order.getDeliveryOrderCode(), entry.getKey())){
                        OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
                        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(), SYSTEM, LogOperationEnum.OUTBOUND_RECEIVE_FAIL.getMessage(), order.getMessage(),null);
                    }
                }
            }
        }
        //成功日志
        for(Map.Entry<String, OutboundForm> entry: entries){
            for(String outboundCode: outboundCodes){
                boolean flag = false;
                if(StringUtils.equals(entry.getKey(), outboundCode)){
                    flag = true;
                    break;
                }
                if(!flag){
                    OutboundOrder outboundOrder = entry.getValue().getOutboundOrder();
                    logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(), SYSTEM, LogOperationEnum.OUTBOUND_SEND.getMessage(), "",null);
                }
            }

        }


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
        if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("调用奇门创建发货单接口(批量)接口失败, %s", appResult.getDatabuffer()));
        }
        AssertUtil.notNull(appResult.getResult(), "调用奇门创建发货单接口(批量)接口返回结果数据为空");
        AssertUtil.notBlank(appResult.getResult().toString(), "调用奇门创建发货单接口(批量)接口返回结果数据为空");
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
    private DeliveryorderBatchcreateRequest.DeliveryOrder getDeliveryOrder(OutboundOrder outboundOrder, List<Warehouse> warehouseList){
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
        for(Warehouse warehouse: warehouseList){
            if(StringUtils.equals(outboundOrder.getWarehouseCode(), warehouse.getCode())){
                senderInfo.setName(warehouse.getName());
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
                        StringUtils.equals(outboundOrder.getChannelCode(), warehouseInfo.getChannelCode()) ){
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






}
