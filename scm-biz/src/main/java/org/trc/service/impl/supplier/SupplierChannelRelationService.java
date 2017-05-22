package org.trc.service.impl.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierChannelRelation;
import org.trc.domain.supplier.SupplierChannelRelationExt;
import org.trc.mapper.supplier.ISupplierCategoryMapper;
import org.trc.mapper.supplier.ISupplierChannelRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierChannelRelationService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierChannelRelation")
public class SupplierChannelRelationService extends BaseService<SupplierChannelRelation, Long> implements ISupplierChannelRelationService {

    @Autowired
    private ISupplierChannelRelationMapper supplierChannelRelationMapper;

    @Override
    public List<SupplierChannelRelationExt> selectSupplierChannels(Map<String, Object> map) throws Exception{
        return supplierChannelRelationMapper.selectSupplierChannels(map);
    }

}
