package org.trc.service.impower;

import org.trc.domain.impower.AclResource;
import org.trc.domain.impower.AclResourceExt;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IAclResourceExtService extends IBaseService<AclResourceExt, Long> {

    List<AclResource> selectJurisdictionListByCodes(Long... codes);



}
