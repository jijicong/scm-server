package org.trc.service.impl.impower;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.mapper.impower.UserAccreditInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.StringUtil;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoService")
public class UserAccreditInfoService extends BaseService<UserAccreditInfo, Long> implements IUserAccreditInfoService {


    @Resource
    private UserAccreditInfoMapper userAccreditInfoMapper;

    @Override
    public List<UserAddPageDate> selectUserAddPageList(Long[] userIds){
        return userAccreditInfoMapper.selectUserAddPageList(userIds);
    }

    @Override
    public List<UserAccreditInfo> findPurchase(){
        return userAccreditInfoMapper.findPurchase();
    }

    @Override
    public int selectCountUser(Map map){
        return userAccreditInfoMapper.selectCountUser(map);
    }

    @Override
    public List<UserAccreditInfo> selectAccreditInfoList(Map map){
        return userAccreditInfoMapper.selectAccreditInfoList(map);
    }

    @Override
    public List<UserAccreditInfo> selectUserNames(String[] strs){
        return userAccreditInfoMapper.selectUserNames(strs);
    }

    @Override
    public UserAccreditInfo selectOneById(String userId){
        return userAccreditInfoMapper.selectOneById(userId);
    }

    @Override
    public UserAccreditInfo selectOneByRequestContext(ContainerRequestContext requestContext) {
        String userId= (String) requestContext.getProperty("userId");
        if(StringUtils.isBlank(userId)){
            return null;
        }
        return userAccreditInfoMapper.selectOneById(userId);
    }

    @Override
    public Map<String,UserAccreditInfo> selectByIds(String... ids) {
        List<UserAccreditInfo> list=userAccreditInfoMapper.selectByUserIds(ids);
        if(AssertUtil.CollectionIsEmpty(list)){
            return null;
        }
        Map<String,UserAccreditInfo> map=new HashMap<>();
        for (UserAccreditInfo userAccreditInfo:list) {
            map.put(userAccreditInfo.getUserId(),userAccreditInfo);
        }
        return map;
    }

}
