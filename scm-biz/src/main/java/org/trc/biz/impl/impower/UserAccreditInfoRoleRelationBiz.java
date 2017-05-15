package org.trc.biz.impl.impower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IUserAccreditInfoRoleRelationBiz;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.service.impower.IUserAccreditRoleRelationService;
import org.trc.util.AssertUtil;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/15.
 */
@Service("userAccreditInfoRoleRelationBiz")
public class UserAccreditInfoRoleRelationBiz implements IUserAccreditInfoRoleRelationBiz{

    private final static Logger LOGGER = LoggerFactory.getLogger(RoleBiz.class);
    @Resource
    private IUserAccreditRoleRelationService userAccreditRoleRelationService;

    @Override
    public int findRoleAndAccreditInfoByRoleId(Long roleId) throws Exception {

        AssertUtil.notNull(roleId,"根据角色id查询角色对应的授权用户失败，角色id为空");
        UserAccreditRoleRelation userAccreditRoleRelation = new UserAccreditRoleRelation();
        userAccreditRoleRelation.setRoleId(roleId);
        userAccreditRoleRelationService.select(userAccreditRoleRelation);

        return 0;
    }
}
