package org.trc.biz.afterSale;

import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.form.afterSale.AfterSaleDetailTabVO;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;

public interface IAfterSaleOrderTabBiz {

    /**
     * 根据店铺订单号查询,售后单
     * @param scmShopOrderCode
     * @return
     */
    Pagenation<AfterSaleDetailTabVO> queryAfterSaleOrderTabPage(String scmShopOrderCode, QueryModel form, Pagenation<AfterSaleOrder> page);

}
