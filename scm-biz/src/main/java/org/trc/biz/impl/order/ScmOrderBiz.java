package org.trc.biz.impl.order;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.domain.order.*;
import org.trc.enums.*;
import org.trc.exception.OrderException;
import org.trc.form.JDModel.JdSku;
import org.trc.form.JDModel.JingDongOrder;
import org.trc.form.JDModel.OrderPriceSnap;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.service.IJDService;
import org.trc.service.order.*;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Service("scmOrderBiz")
public class ScmOrderBiz implements IScmOrderBiz {

    private Logger log = LoggerFactory.getLogger(ScmOrderBiz.class);

    //京东地址分隔符
    public final static String JING_DONG_ADDRESS_SPLIT = "/";

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

    @Override
    public Pagenation<ShopOrder> shopOrderPage(ShopOrderForm queryModel, Pagenation<ShopOrder> page) {
        Example example = new Example(ShopOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getPlatformOrderCode())) {//平台订单编码
            criteria.andLike("platform_order_code", "%" + queryModel.getPlatformOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getShopOrderCode())) {//店铺订单编码
            criteria.andLike("shop_order_code", "%" + queryModel.getShopOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getStatus())) {//订单状态
            criteria.andEqualTo("status", queryModel.getStatus());
        }
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(queryModel, ZeroToNineEnum.ZERO.getCode());
        List<String> platformOrderCodeList = new ArrayList<String>();
        for(PlatformOrder platformOrder: platformOrderList){
            platformOrderCodeList.add(platformOrder.getPlatformOrderCode());
        }
        if(platformOrderCodeList.size() > 0){
            criteria.andIn("platformOrderCode", platformOrderCodeList);
        }
        page = shopOrderService.pagination(example, page, queryModel);
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
        if(StringUtils.isNotBlank(form.getStatus())){//状态
            criteria.andEqualTo("status", form.getStatus());
        }
        if(StringUtils.isNotBlank(form.getWarehouseOrderCode())){//供应商订单编号
            criteria.andEqualTo("warehouseOrderCode", form.getWarehouseOrderCode());
        }
        if(StringUtils.isNotBlank(form.getSupplierName())){//供应商名称
            criteria.andEqualTo("supplierName", form.getSupplierName());
        }
        if(StringUtils.isNotBlank(form.getShopOrderCode())){//店铺订单号
            criteria.andEqualTo("shopOrderCode", form.getShopOrderCode());
        }
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(form, ZeroToNineEnum.ONE.getCode());
        List<String> platformOrderCodeList = new ArrayList<String>();
        for(PlatformOrder platformOrder: platformOrderList){
            platformOrderCodeList.add(platformOrder.getPlatformOrderCode());
        }
        if(platformOrderCodeList.size() > 0){
            criteria.andIn("platformOrderCode", platformOrderCodeList);
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
        BeanUtils.copyProperties(shopOrder, form);
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
            AssertUtil.notEmpty(platformOrderList, String.format("根据平台订单编号[%s]查询平台订单为空", CommonUtil.converCollectionToString(platformOrderCodeList)));
        }
        if(platformOrderList.size() > 0){
            for(ShopOrder shopOrder2: shopOrderList){
                for(PlatformOrder platformOrder: platformOrderList){
                    if(StringUtils.equals(shopOrder2.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                        setShopOrderItemsDetail(shopOrder, platformOrder);
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
    public AppResult submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName) {
        AssertUtil.notBlank(jdAddressCode, "提交订单京东订单四级地址编码不能为空");
        AssertUtil.notBlank(jdAddressName, "提交订单京东订单四级地址不能为空");
        AssertUtil.doesNotContain(jdAddressCode, JING_DONG_ADDRESS_SPLIT, "提交订单京东订单四级地址编码格式错误");
        AssertUtil.doesNotContain(jdAddressName, JING_DONG_ADDRESS_SPLIT, "提交订单京东订单四级地址格式错误");
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setWarehouseOrderCode(warehouseOrderCode);
        warehouseOrder = warehouseOrderService.selectOne(warehouseOrder);
        AssertUtil.notNull(warehouseOrder, String.format("根据仓库订单编码[%s]查询仓库订单为空", warehouseOrderCode));
        //获取京东四级地址
        String[] jdAddressCodes = jdAddressCode.split(JING_DONG_ADDRESS_SPLIT);
        String[] jdAddressNames = jdAddressName.split(JING_DONG_ADDRESS_SPLIT);
        AssertUtil.isTrue(jdAddressCodes.length == jdAddressNames.length, "京东四级地址编码与名称个数不匹配");
        //查询平台订单
        PlatformOrder platformOrder = new PlatformOrder();
        platformOrder.setPlatformOrderCode(warehouseOrder.getPlatformOrderCode());
        platformOrder = platformOrderService.selectOne(platformOrder);
        AssertUtil.notNull(platformOrder, String.format("根据平台订单编码[%s]查询平台订单为空", warehouseOrder.getPlatformOrderCode()));
        //获取京东订单对象
        JingDongOrder jingDongOrder = getJingDongOrder(warehouseOrder, platformOrder, jdAddressCodes);
        //保存京东订单信息
        SupplierOrderInfo supplierOrderInfo = saveSupplierOrderInfo(warehouseOrder, jdAddressCodes, jdAddressNames);
        //调用京东下单服务接口
        String jdOrderId = invokeSubmitJingDongOrder(jingDongOrder);
        //更新京东订单信息
        supplierOrderInfo.setSupplierOrderCode(jdOrderId);
        supplierOrderInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierOrderInfoService.updateByPrimaryKeySelective(supplierOrderInfo);
        if(count == 0){
            String msg = String.format("更新京东订单信息%s的供应商订单编码为[%s]异常", supplierOrderInfo, jdOrderId);
            log.error(msg);
            return ResultUtil.createFailAppResult(msg);
        }
        return ResultUtil.createSucssAppResult("提交京东订单成功","");
    }

    private JingDongOrder getJingDongOrder(WarehouseOrder warehouseOrder, PlatformOrder platformOrder, String[] jdAddressCodes){
        JingDongOrder jingDongOrder = new JingDongOrder();
        jingDongOrder.setThirdOrder(warehouseOrder.getWarehouseOrderCode());
        jingDongOrder.setName(platformOrder.getReceiverName());
        if(jdAddressCodes.length == 1){
            jingDongOrder.setProvince(jdAddressCodes[0]);
            jingDongOrder.setInvoiceProvice(Integer.parseInt(jdAddressCodes[0]));
        }
        if(jdAddressCodes.length == 2){
            jingDongOrder.setCity(jdAddressCodes[1]);
            jingDongOrder.setInvoiceCity(Integer.parseInt(jdAddressCodes[1]));
        }
        if(jdAddressCodes.length == 3){
            jingDongOrder.setCounty(jdAddressCodes[2]);
            jingDongOrder.setInvoiceCounty(Integer.parseInt(jdAddressCodes[2]));
        }
        if(jdAddressCodes.length == 4){
            jingDongOrder.setTown(jdAddressCodes[3]);
        }
        jingDongOrder.setAddress(platformOrder.getReceiverAddress());
        jingDongOrder.setZip(platformOrder.getReceiverZip());
        jingDongOrder.setPhone(platformOrder.getReceiverPhone());
        jingDongOrder.setMobile(platformOrder.getReceiverMobile());
        jingDongOrder.setEmail(platformOrder.getReceiverEmail());
        jingDongOrder.setRemark("");// TODO 备注信息
        jingDongOrder.setInvoiceState(JdInvoiceStateEnum.FOLLOW_GOODS.getCode());
        jingDongOrder.setInvoiceType(JdInvoiceTypeEnum.NORMAL.getCode());
        jingDongOrder.setSelectedInvoiceTitle(JdInvoiceTitleEnum.PERSONAL.getCode());
        jingDongOrder.setCompanyName(platformOrder.getInvoiceName());//发票抬头
        jingDongOrder.setInvoiceContent(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));//默认是1-明细
        jingDongOrder.setPaymentType(JdPaymentTypeEnum.ON_LINE.getCode());
        jingDongOrder.setIsUseBalance(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));
        jingDongOrder.setSubmitState(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));//不预占库存
        jingDongOrder.setInvoiceName(platformOrder.getReceiverName());
        jingDongOrder.setInvoiceAddress(platformOrder.getReceiverAddress());
        jingDongOrder.setDoOrderPriceMode(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));//下单价格模式,1-必需验证客户端订单价格快照
        // TODO 大家电、中小件配送安装参数设置
        //设置京东订单SKU信息
        setJdOrderSkuInfo(jingDongOrder, warehouseOrder.getWarehouseOrderCode());
        return jingDongOrder;
    }

    /**
     * 设置京东订单SKU信息
     * @param jingDongOrder
     * @param warehouseOrderCode
     */
    private void setJdOrderSkuInfo(JingDongOrder jingDongOrder, String warehouseOrderCode){
        OrderItem orderItem = new OrderItem();
        orderItem.setWarehouseOrderCode(warehouseOrderCode);
        List<OrderItem> orderItemList = orderItemService.select(orderItem);
        AssertUtil.notEmpty(orderItemList, String.format("根据仓库订单编码[%s]查询订单商品明细信息为空", warehouseOrderCode));
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
            orderPriceSnap.setPrice(new BigDecimal(orderItem2.getTransactionPrice()));
            orderPriceSnapList.add(orderPriceSnap);
        }
        jingDongOrder.setSku(jdSkuList);
        jingDongOrder.setOrderPriceSnap(orderPriceSnapList);
    }

    /**
     * 保存供应商订单信息
     * @param warehouseOrder
     * @param jdAddressCodes
     * @param jdAddressNames
     */
    private SupplierOrderInfo saveSupplierOrderInfo(WarehouseOrder warehouseOrder, String[] jdAddressCodes, String[] jdAddressNames){
        SupplierOrderInfo supplierOrderInfo = new SupplierOrderInfo();
        supplierOrderInfo.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
        supplierOrderInfo.setSupplierCode(warehouseOrder.getSupplierCode());
        if(jdAddressCodes.length == 1){
            supplierOrderInfo.setJdProvinceCode(jdAddressCodes[0]);
            supplierOrderInfo.setJdProvince(jdAddressNames[0]);
        }
        if(jdAddressCodes.length == 2){
            supplierOrderInfo.setJdCityCode(jdAddressCodes[1]);
            supplierOrderInfo.setJdCity(jdAddressNames[1]);
        }
        if(jdAddressCodes.length == 3){
            supplierOrderInfo.setJdDistrictCode(jdAddressCodes[2]);
            supplierOrderInfo.setJdDistrict(jdAddressNames[2]);
        }
        if(jdAddressCodes.length == 4){
            supplierOrderInfo.setJdTownCode(jdAddressCodes[3]);
            supplierOrderInfo.setJdTown(jdAddressNames[3]);
        }
        ParamsUtil.setBaseDO(supplierOrderInfo);
        supplierOrderInfoService.insert(supplierOrderInfo);
        return supplierOrderInfo;
    }



    /**
     * 调用京东下单服务接口
     * @param jingDongOrder
     * @return
     */
    private String invokeSubmitJingDongOrder(JingDongOrder jingDongOrder){
        ReturnTypeDO returnTypeDO = ijdService.submitJingDongOrder(jingDongOrder);
        if(!returnTypeDO.getSuccess()){
            throw new OrderException(ExceptionEnum.SUBMIT_JING_DONG_ORDER, "调用京东下单服务接口失败");
        }
        return returnTypeDO.getResult().toString();
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
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
        }
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//店铺订单分页查询
            ShopOrderForm shopOrderForm = (ShopOrderForm)queryModel;
            criteria.andLike("type", shopOrderForm.getPlatformOrderCode());
            if (StringUtil.isNotEmpty(shopOrderForm.getReceiverName())) {//收货人姓名
                criteria.andLike("receiverName", "%" + shopOrderForm.getReceiverName() + "%");
            }
        }
        return platformOrderService.selectByExample(example);
    }

    private void handlerOrderInfo(Pagenation<ShopOrder> page, List<PlatformOrder> platformOrderList){
        List<PlatformOrder> platformOrders = new ArrayList<PlatformOrder>();
        if(platformOrderList.size() > 0){
            platformOrders = platformOrderList;
        }else{
            List<String> platformOrdersCodes = new ArrayList<String>();
            for(ShopOrder shopOrder: page.getResult()){
                platformOrdersCodes.add(shopOrder.getPlatformCode());
            }
            Example example = new Example(PlatformOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("platformOrderCode", platformOrdersCodes);
            platformOrders = platformOrderService.selectByExample(example);
        }
        //设置商品订单扩展信息
        for(ShopOrder shopOrder: page.getResult()){
            for(PlatformOrder platformOrder: platformOrders){
                if(StringUtils.equals(shopOrder.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                    BeanUtils.copyProperties(shopOrder, platformOrder);
                    setShopOrderItemsDetail(shopOrder, platformOrder);
                }
            }
        }
        //按付款时间将序排序
        Collections.sort(page.getResult(), new Comparator<ShopOrder>() {
            @Override
            public int compare(ShopOrder o1, ShopOrder o2) {
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
        BeanUtils.copyProperties(orderExt, platformOrder);
        orderExt.setOrderItemList(orderItemList);
        List<OrderExt> orderExts = new ArrayList<OrderExt>();
        orderExts.add(orderExt);
        shopOrder.setRecords(orderExts);
    }

    private void handlerWarehouseOrderInfo(Pagenation<WarehouseOrder> page, List<PlatformOrder> platformOrderList){
        List<PlatformOrder> platformOrders = new ArrayList<PlatformOrder>();
        if(platformOrderList.size() > 0){
            platformOrders = platformOrderList;
        }else{
            List<String> platformOrdersCodes = new ArrayList<String>();
            for(WarehouseOrder warehouseOrder: page.getResult()){
                platformOrdersCodes.add(warehouseOrder.getPlatformCode());
            }
            if(platformOrdersCodes.size() > 0){
                Example example = new Example(PlatformOrder.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("platformOrderCode", platformOrdersCodes);
                platformOrders = platformOrderService.selectByExample(example);
            }
        }
        //设置仓库订单支付时间
        for(WarehouseOrder warehouseOrder: page.getResult()){
            for(PlatformOrder platformOrder: platformOrders){
                if(StringUtils.equals(warehouseOrder.getPlatformOrderCode(), platformOrder.getPlatformOrderCode())){
                    warehouseOrder.setPayTime(platformOrder.getPayTime());
                }
            }
        }
        //按付款时间将序排序
        Collections.sort(page.getResult(), new Comparator<WarehouseOrder>() {
            @Override
            public int compare(WarehouseOrder o1, WarehouseOrder o2) {
                return o1.getPayTime().compareTo(o2.getPayTime());
            }
        });
    }
}
