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
import org.trc.form.order.ShopOrderForm;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        List<PlatformOrder> platformOrderList = getPlatformOrdersConditon(queryModel);
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

    /**
     * 获取平台订单编码列表条件
     * @param queryModel
     * @return
     */
    private List<PlatformOrder> getPlatformOrdersConditon(ShopOrderForm queryModel){
        Example example = new Example(PlatformOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("type", queryModel.getPlatformOrderCode());
        if (StringUtil.isNotEmpty(queryModel.getReceiverName())) {//收货人姓名
            criteria.andLike("receiverName", "%" + queryModel.getReceiverName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//支付开始日期
            criteria.andGreaterThanOrEqualTo("payTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//支付截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("payTime", DateUtils.addDays(endDate, 1));
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

}
