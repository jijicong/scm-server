package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclResource;
import org.trc.domain.impower.AclResourceExt;
import org.trc.mapper.impower.AclResourceExtMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclResourceExtService;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("aclResourceExtService")
public class AclResourceExtService extends BaseService<AclResourceExt, Long> implements IAclResourceExtService{

    @Autowired
    private AclResourceExtMapper aclResourceMapper;

    @Override
    public List<AclResource> selectJurisdictionListByCodes(Long... codes){
        return aclResourceMapper.selectJurisdictionListByCodes(codes);
    }

}
