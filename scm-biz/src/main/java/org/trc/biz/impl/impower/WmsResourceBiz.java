package org.trc.biz.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IWmsResourceBiz;
import org.trc.domain.impower.WmsResource;
import org.trc.service.impower.IWmsResourceService;
import org.trc.util.AssertUtil;

import java.util.List;

@Service("wmsResourceBiz")
public class WmsResourceBiz implements IWmsResourceBiz {
    /**
     * 仓级资源所属
     */
    private final static Integer WMS_RESOURCE = 1;

    @Autowired
    private IWmsResourceService wmsResourceService;


    @Override
    public List<WmsResource> queryWmsResource() {
        WmsResource wmsResource = new WmsResource();
        wmsResource.setBelong(WMS_RESOURCE);
        List<WmsResource> wmsResourceList = wmsResourceService.select(wmsResource);
        AssertUtil.notEmpty(wmsResourceList,"查询所有仓级资源为空!");
        return wmsResourceList;
    }



}
