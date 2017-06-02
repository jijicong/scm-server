package org.trc.service.impl.impower;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.mapper.impower.UserAccreditInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IUserAccreditInfoService;

import javax.annotation.Resource;
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
    public List<UserAddPageDate> selectUserAddPageList(Long[] userIds) throws Exception {
        return userAccreditInfoMapper.selectUserAddPageList(userIds);
    }

    @Override
    public List<UserAccreditInfo> findPurchase() throws Exception {
        return userAccreditInfoMapper.findPurchase();
    }

    @Override
    public int selectCountUser(Map map) throws Exception {
        return userAccreditInfoMapper.selectCountUser(map);
    }

    @Override
    public List<UserAccreditInfo> selectAccreditInfoList(Map map) throws Exception {
        return userAccreditInfoMapper.selectAccreditInfoList(map);
    }

    @Override
    public UserAccreditInfo selectOneById(String userId) throws Exception {
        return userAccreditInfoMapper.selectOneById(userId);
    }

}
