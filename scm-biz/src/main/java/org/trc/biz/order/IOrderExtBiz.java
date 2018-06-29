package org.trc.biz.order;

import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.order.OrderBaseDO;
import org.trc.enums.LogisticsTypeEnum;
import org.trc.util.Pagenation;

import java.util.List;

public interface IOrderExtBiz {

    void cleanOrderCache();

    /**
     * 设置订单销售渠道名称
     * @param
     */
    void setOrderSellName(Pagenation pagenation);

    /**
     * 根据类型和物流公司名称查询物流公司
     * @param type TRC-泰然城，QIMEN-奇门
     * @param logisticsCompanyName
     * @return
     */
    LogisticsCompany getLogisticsCompanyByName(LogisticsTypeEnum type, String logisticsCompanyName);

}
