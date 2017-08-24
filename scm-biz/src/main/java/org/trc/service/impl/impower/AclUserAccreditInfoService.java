package org.trc.service.impl.impower;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.mapper.impower.AclUserAccreditInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AssertUtil;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoService")
public class AclUserAccreditInfoService extends BaseService<AclUserAccreditInfo, Long> implements IAclUserAccreditInfoService {


    @Resource
    private AclUserAccreditInfoMapper aclUserAccreditInfoMapper;

    @Override
    public List<AclUserAddPageDate> selectUserAddPageList(Long[] userIds){
        return aclUserAccreditInfoMapper.selectUserAddPageList(userIds);
    }

    @Override
    public List<AclUserAccreditInfo> findPurchase(String channelCode){
        return aclUserAccreditInfoMapper.findPurchase(channelCode);
    }

    @Override
    public int selectCountUser(Map map){
        return aclUserAccreditInfoMapper.selectCountUser(map);
    }

    @Override
    public List<AclUserAccreditInfo> selectAccreditInfoList(Map map){
        return aclUserAccreditInfoMapper.selectAccreditInfoList(map);
    }

    @Override
    public List<AclUserAccreditInfo> selectUserNames(String[] strs){
        return aclUserAccreditInfoMapper.selectUserNames(strs);
    }

    @Override
    public AclUserAccreditInfo selectOneById(String userId){
        return aclUserAccreditInfoMapper.selectOneById(userId);
    }

  /*  @Override
    public AclUserAccreditInfo selectOneByRequestContext(ContainerRequestContext requestContext) {
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        if(StringUtils.isBlank(userId)){
            return null;
        }
        return aclUserAccreditInfoMapper.selectOneById(userId);
    }*/

    @Override
    public Map<String,AclUserAccreditInfo> selectByIds(String... ids) {
        List<AclUserAccreditInfo> list= aclUserAccreditInfoMapper.selectByUserIds(ids);
        if(AssertUtil.collectionIsEmpty(list)){
            return null;
        }
        Map<String,AclUserAccreditInfo> map=new HashMap<>();
        for (AclUserAccreditInfo aclUserAccreditInfo :list) {
            map.put(aclUserAccreditInfo.getUserId(), aclUserAccreditInfo);
        }
        return map;
    }

    @Override
    public List<AclUserAccreditInfo> selectUserByName(String name) {
        return aclUserAccreditInfoMapper.selectUserByName(name);
    }
}
