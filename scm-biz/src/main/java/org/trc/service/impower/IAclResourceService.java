package org.trc.service.impower;

import org.trc.domain.impower.AclResource;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IAclResourceService extends IBaseService<AclResource, Long> {

    List<AclResource> selectJurisdictionListByCodes(Long... codes) throws Exception;
}
