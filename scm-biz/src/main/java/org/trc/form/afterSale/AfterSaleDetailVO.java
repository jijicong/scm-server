package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;

import java.util.List;

@Setter
@Getter
public class AfterSaleDetailVO {

    /**
     * 售后单
     */
    private AfterSaleOrder afterSaleOrder;
    /**
     * 售后单明细
     */
    private List<AfterSaleOrderDetail> afterSaleOrderDetailList;

}
