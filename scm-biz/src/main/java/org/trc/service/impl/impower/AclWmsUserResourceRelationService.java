package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclWmsUserResourceRelation;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclWmsUserResourceRelationService;

@Service("aclWmsUserResourceRelationService")
public  class AclWmsUserResourceRelationService extends BaseService<AclWmsUserResourceRelation, Long> implements IAclWmsUserResourceRelationService {
}
