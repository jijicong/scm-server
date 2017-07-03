package org.trc.service.impl.impower;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.AclResource;
import org.trc.mapper.impower.AclResourceMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclResourceService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("jurisdictionService")
public class AclResourceService extends BaseService<AclResource, Long> implements IAclResourceService {

    @Resource
    private AclResourceMapper aclResourceMapper;

    @Override
    public List<AclResource> selectJurisdictionListByCodes(Long... codes){
        return aclResourceMapper.selectJurisdictionListByCodes(codes);
    }

    @Override
    public void insertOne(AclResource aclResource){
        aclResourceMapper.insertOne(aclResource);
    }
}
