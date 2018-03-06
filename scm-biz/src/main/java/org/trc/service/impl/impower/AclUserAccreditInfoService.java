package org.trc.service.impl.impower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.mapper.impower.AclUserAccreditInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AssertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sone
 * @date 2017/5/11
 */
@Service("userAccreditInfoService")
public class AclUserAccreditInfoService extends BaseService<AclUserAccreditInfo, Long> implements IAclUserAccreditInfoService {


    @Autowired
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
    @Cacheable(value = SupplyConstants.Cache.SCM_USER)
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

    @Override
    @Cacheable(value = SupplyConstants.Cache.SCM_USER)
    public List<AclUserAccreditInfo> selectUserListByUserId(String userId,String channelCodeString) {
        //根据userId查询用户业务线销售渠道关联表,只需要关联的业务线条数,所以要去重
        List<AclUserAccreditInfo> aclUserAccreditInfoList =  aclUserAccreditInfoMapper.selectUserListByUserId(userId,channelCodeString);
        List<AclUserAccreditInfo> dedupAccreditInfoList = new ArrayList<>();
        Map<String,AclUserAccreditInfo> userMap = new HashMap<>();
        for (AclUserAccreditInfo accreditInfo:aclUserAccreditInfoList ) {
            userMap.put(accreditInfo.getChannelCode(),accreditInfo);
        }
        for (String channelCode : userMap.keySet()) {
            dedupAccreditInfoList.add(userMap.get(channelCode));
        }
        return dedupAccreditInfoList;
    }
    @Override
    public List<AclUserAccreditInfo> selectUserListByUserId2(String userId) {
        //根据userId查询用户业务线销售渠道关联表,只需要关联的业务线条数,所以要去重
        List<AclUserAccreditInfo> aclUserAccreditInfoList =  aclUserAccreditInfoMapper.selectUserListByUserId2(userId);
        AssertUtil.notEmpty(aclUserAccreditInfoList,"根据userId未查询到相关的用户信息.");
        List<AclUserAccreditInfo> dedupAccreditInfoList = new ArrayList<>();
        Map<String,AclUserAccreditInfo> userMap = new HashMap<>();
        for (AclUserAccreditInfo accreditInfo:aclUserAccreditInfoList ) {
            userMap.put(accreditInfo.getChannelCode(),accreditInfo);
        }
        for (String channelCode : userMap.keySet()) {
            dedupAccreditInfoList.add(userMap.get(channelCode));
        }
        return dedupAccreditInfoList;
    }
}
