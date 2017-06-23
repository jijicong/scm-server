package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * 用户授权信息的mapper
 * Created by sone on 2017/5/11.
 */
public  interface AclUserAccreditInfoMapper extends BaseMapper<AclUserAccreditInfo>{
    /**
     * 用户授权信息及角色names
     * @param
     * @return
     */
    List<AclUserAddPageDate> selectUserAddPageList(@Param("userIds") Long[] userIds);

    /**
     * 查询据有采购组角色的用户
     * @return
     */
    List<AclUserAccreditInfo> findPurchase();
    /**
     * 查询分页条件查询的总数量
     * @param map
     * @return
     */
    int selectCountUser(Map map);
    /**
     * 查询授权用户信息
     * @param map
     * @return
     */
    List<AclUserAccreditInfo> selectAccreditInfoList(Map map);

    /**
     * 查询分页对应的用户信息
     * @param strs
     * @return
     */
    List<AclUserAccreditInfo> selectUserNames(String strs[]);


    AclUserAccreditInfo selectOneById(@Param("userId")String userId);

    List<AclUserAccreditInfo> selectByUserIds(@Param("userIds")String ...ids);
    //根据姓名模糊查询用户
    List<AclUserAccreditInfo> selectUserByName(String name);
}
