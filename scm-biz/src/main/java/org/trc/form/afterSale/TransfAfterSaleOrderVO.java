package org.trc.form.afterSale;

import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;

import java.util.Objects;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 16:08
 * @Description:组装售后单列表展示数据
 */
public class TransfAfterSaleOrderVO {

    public static  AfterSaleOrderVO getAfterSaleOrderVO(AfterSaleOrder afterSaleOrder,WarehouseInfo searWarehouseInfo){
        if(Objects.equals(null,afterSaleOrder)){
            return null;
        }

        AfterSaleOrderVO afterSaleOrderVO = new AfterSaleOrderVO();
        //赋值仓库名称



        afterSaleOrderVO.setWmsName(wmsMap.get(asd.getWmsCode()));

        return afterSaleOrderVO;
    }

}
