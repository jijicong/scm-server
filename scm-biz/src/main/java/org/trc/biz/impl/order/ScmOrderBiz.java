package org.trc.biz.impl.order;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.domain.order.*;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Service("scmOrderBiz")
public class ScmOrderBiz implements IScmOrderBiz {

    private Logger log = LoggerFactory.getLogger(ScmOrderBiz.class);

    @Autowired
    private IShopOrderService shopOrderService;
    @Autowired
    private IPlatformOrderService platformOrderService;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private IWarehouseOrderService warehouseOrderService;

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
    public AppResult submitJingDongOrder(String warehouseOrderCode, String jdAddress) {
        //TODO
        return null;
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
