package org.trc.biz.impower;

import org.trc.domain.impower.WmsResource;

import java.util.List;

public interface IWmsResourceBiz {

    /**
     * 查询所有仓级资源
     * @return
     */
    List<WmsResource> queryWmsResource();
}
