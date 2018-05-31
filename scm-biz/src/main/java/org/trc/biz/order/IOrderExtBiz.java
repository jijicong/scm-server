package org.trc.biz.order;

import org.trc.domain.order.OrderBaseDO;
import org.trc.util.Pagenation;

import java.util.List;

public interface IOrderExtBiz {

    void cleanOrderCache();

    /**
     * 设置订单销售渠道名称
     * @param orderBaseDOList
     */
    void setOrderSellName(Pagenation pagenation);

}
