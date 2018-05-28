package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.WmsResource;
import org.trc.mapper.impower.IWmsResourceMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IWmsResourceService;

@Service("wmsResourceService")
public class WmsResourceService extends BaseService<WmsResource, Long> implements IWmsResourceService {
    @Autowired
    private IWmsResourceMapper wmsResourceMapper;
    @Override
    public void insertOne(WmsResource wmsResource) {
        wmsResourceMapper.insertOne(wmsResource);
    }
}
