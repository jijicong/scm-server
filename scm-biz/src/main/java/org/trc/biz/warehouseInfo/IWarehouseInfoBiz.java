package org.trc.biz.warehouseInfo;

import javax.ws.rs.core.Response;

/**
 * Created by wangyz on 2017/11/15.
 */
public interface IWarehouseInfoBiz {

    //添加仓库
    Response saveWarehouse(String qimenWarehouseCode);

    //查询仓库
    Response selectWarehouse();

}
