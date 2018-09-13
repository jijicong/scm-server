package org.trc.biz.impl.afterSale;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleNoticeTaskBiz;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.enums.AfterSaleOrderEnum;
import org.trc.enums.AfterSaleTypeEnum;
import org.trc.form.afterSale.AfterSaleNoticeWmsFrom;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("afterSaleNoticeTaskBiz")
public class AfterSaleNoticeTaskBiz implements IAfterSaleNoticeTaskBiz {

    @Autowired
    IAfterSaleOrderService afterSaleOrderService;

    @Autowired
    IAfterSaleOrderDetailService afterSaleOrderDetailService;

    @Override
    public void cancelSendOutGoods() {
        //1.售后单状态为取消中,售后类型为取消发货的售后单
        Example example = new Example(AfterSaleOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("afterSaleType", AfterSaleTypeEnum.CANCEL_DELIVER.getCode());
        criteria.andEqualTo("status", AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_IS_CANCELING.getCode());
        List<AfterSaleOrder> afterSaleOrderList = afterSaleOrderService.selectByExample(example);
        //2.获取售后单详情
        if (!AssertUtil.collectionIsEmpty(afterSaleOrderList)) {
            Set<String> afterSaleCodeSet = new HashSet<>();
            for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
                afterSaleCodeSet.add(afterSaleOrder.getAfterSaleCode());
            }
            //
            Example detailExample = new Example(AfterSaleOrderDetail.class);
            Example.Criteria detailCriteria = detailExample.createCriteria();
            detailCriteria.andIn("afterSaleCode", afterSaleCodeSet);
            List<AfterSaleOrderDetail> afterSaleOrderDetailList = afterSaleOrderDetailService.selectByExample(detailExample);
            if (!AssertUtil.collectionIsEmpty(afterSaleOrderDetailList)) {
                List<AfterSaleNoticeWmsFrom> wmsFromList = new ArrayList<>();
                for (AfterSaleOrderDetail orderDetail : afterSaleOrderDetailList) {
                    AfterSaleNoticeWmsFrom saleNoticeWmsFrom = new AfterSaleNoticeWmsFrom();
                    saleNoticeWmsFrom.setAfterSaleCode(orderDetail.getAfterSaleCode());
                    saleNoticeWmsFrom.setScmShopOrderCode(orderDetail.getScmShopOrderCode());
                    saleNoticeWmsFrom.setSkuCode(orderDetail.getSkuCode());
                    wmsFromList.add(saleNoticeWmsFrom);
                }
            }
        }
        //3.请求仓库

    }
}
