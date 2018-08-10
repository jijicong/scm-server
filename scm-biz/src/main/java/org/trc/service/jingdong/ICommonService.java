package org.trc.service.jingdong;

import org.trc.domain.config.Common;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.warehouse.ScmWarehouseRequestBase;
import org.trc.service.IBaseService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
public interface ICommonService extends IBaseService<Common, Long> {
    public Common selectByCode(String code);

    WarehouseInfo getWarehoueType (String whCode, ScmWarehouseRequestBase request);
}
