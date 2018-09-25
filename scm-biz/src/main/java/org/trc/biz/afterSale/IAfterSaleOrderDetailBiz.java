package org.trc.biz.afterSale;

import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.form.afterSale.AfterSaleOrderDetailForm;

import java.util.List;

public interface IAfterSaleOrderDetailBiz {

    /**
     * @Description: 根据条件查询售后单字表列表数据
     * @Author: hzluoxingcheng
     * @Date: 2018/8/29
     */ 
   public List<AfterSaleOrderDetail> queryListByCondition(AfterSaleOrderDetailForm afterSaleOrderDetailForm);

}
