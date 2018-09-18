package org.trc.dbUnit.order;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.Skus;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.OutboundDetail;
import org.trc.enums.InventoryTypeEnum;
import org.trc.enums.OutboundDetailStatusEnum;
import org.trc.service.BaseTest;
import org.trc.service.goods.ISkusService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/6/19
 */
public class scmOrderTest extends BaseTest {

    @Autowired
    IScmOrderBiz scmOrderBiz;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private ISkusService skusService;

    @Autowired
    private IOutboundDetailService outboundDetailService;

    @Test
    public void createOutboundOrderTest(){

        ArrayList<OrderItem> list = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setScmShopOrderCode("1806141314051982540");
        item.setPlatformOrderCode("1806141314051972540");
        item.setItemNo("SPU2018061200531");
        item.setSkuCode("SP0201707250000034");
        item.setCreateTime(new Date());
        item.setPayTime(new Date());
        item.setItemName("自采手动1");
        item.setCategory("1188");
        item.setId(1806141314052002540L);
        item.setShopId(2L);
        item.setShopName("泰然直营1（自营店铺）");
        item.setUserId("542540");
        item.setBarCode("333333333333");
        item.setSpecNatureInfo("环境5属性：4环境5");
        item.setPrice(new BigDecimal(22));
        item.setMarketPrice(new BigDecimal(22.01));
        item.setPromotionPrice(new BigDecimal(0.00));
        item.setCustomsPrice(new BigDecimal(22));
        item.setNum(1);
        item.setSendNum(0);
        //item.setIsOversold("0");
        //item.setShippingType("express");
        item.setTotalFee(new BigDecimal(22));
        item.setPayment(new BigDecimal(22));
        item.setTotalWeight(new BigDecimal(1));
        item.setAdjustFee(new BigDecimal(22));
        item.setPicPath("https://image.tairanmall.com/FmtfqBT8wXl51s_SfWJSKxrI1vib");
        item.setOuterSkuId("SP0201806120002423");
        item.setSubStock("1");
        //item.setDlytmplId(7);
        item.setSupplierName("泰然城");
        //item.setObjType("item");
        list.add(item);


        //ZYFHTZ2018061900078

        createOutboundOrder(list);
    }

    private void createOutboundOrder(List<OrderItem> orderItemList) {

        //流水号
        String code = serialUtilService.generateCode(SupplyConstants.Serial.OUTBOUND_ORDER_LENGTH, SupplyConstants.Serial.OUTBOUND_ORDER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));


        Date currentTime = Calendar.getInstance().getTime();

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
            //商品规格
            Example example = new Example(Skus.class);
            example.createCriteria().andEqualTo("skuCode", orderItem.getSkuCode()).andEqualTo("isDeleted", "0");
            List<Skus> skuses = skusService.selectByExample(example);
            if(!skuses.isEmpty() && skuses.size() < 2){
                outboundDetail.setSpecNatureInfo(skuses.get(0).getSpecInfo());
            }
            /*List<SkuWarehouseDO> warehouseDOList = skuWarehouseMap.get(outboundDetail.getSkuCode());
            if(!CollectionUtils.isEmpty(warehouseDOList)){
                outboundDetail.setWarehouseItemId(warehouseDOList.get(0).getItemId());
            }*/
            outboundDetailList.add(outboundDetail);
        }

        outboundDetailService.insertList(outboundDetailList);
    }
}
