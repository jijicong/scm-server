package org.trc.service.impl.jingdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.config.Common;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.OperationalNatureEnum;
import org.trc.form.warehouse.ScmWarehouseRequestBase;
import org.trc.mapper.config.ICommonMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
@Service("commonService")
public class CommonService extends BaseService<Common, Long> implements ICommonService {

    @Autowired
    ICommonMapper commonMapper;
    @Autowired
    IWarehouseInfoService warehouseInfoService;
    @Override
    public Common selectByCode(String code) {
        return commonMapper.selectByCode(code);
    }

    public String getWarehoueType (String whCode, ScmWarehouseRequestBase request) {
        WarehouseInfo whi = new WarehouseInfo();
        whi.setCode(whCode);
        WarehouseInfo warehouse = warehouseInfoService.selectOne(whi);
        AssertUtil.notNull(warehouse, "调出仓库不存在");

        if (OperationalNatureEnum.SELF_SUPPORT.getCode().equals(warehouse.getOperationalNature())) {
            request.setWarehouseType("TRC");
        } else if (OperationalNatureEnum.THIRD_PARTY.getCode().equals(warehouse.getOperationalNature())) {
            request.setWarehouseType("JD");
        } else {
        	throw new RuntimeException("请确认仓库的运营性质是否正确");
        }
        return warehouse.getWarehouseName();
    }
}
