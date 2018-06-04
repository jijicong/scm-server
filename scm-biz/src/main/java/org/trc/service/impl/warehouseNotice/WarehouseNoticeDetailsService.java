package org.trc.service.impl.warehouseNotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.mapper.warehouseNotice.IWarehouseNoticeDetailsMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.warehouseNotice.IWarehouseNoticeDetailsService;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * Created by sone on 2017/7/11.
 */
@Service("warehouseNoticeDetailsService")
public class WarehouseNoticeDetailsService extends BaseService<WarehouseNoticeDetails,Long> implements IWarehouseNoticeDetailsService{

    @Autowired
    private IWarehouseNoticeDetailsMapper mapper;
    @Override
    public void updateWarehouseNoticeLists(List<WarehouseNoticeDetails> detailsList) {
        for (WarehouseNoticeDetails warehouseNoticeDetails : detailsList) {
//            Example example = new Example(WarehouseNoticeDetails.class);
//            Example.Criteria criteria = example.createCriteria();
//            criteria.andEqualTo("warehouseNoticeDetails",warehouseNoticeDetails);
            mapper.updateByPrimaryKeySelective(warehouseNoticeDetails);
        }
    }
}
