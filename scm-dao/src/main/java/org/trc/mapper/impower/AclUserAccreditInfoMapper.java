package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * 用户授权信息的mapper
 *
 * @author sone
 * @date 2017/5/11
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
    List<AclUserAccreditInfo> findPurchase(@Param("channelCode")String channelCode);

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

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    AclUserAccreditInfo selectOneById(@Param("userId")String userId);

    /**
     * 根据用户id批量查询
     * @param ids
     * @return
     */
    List<AclUserAccreditInfo> selectByUserIds(@Param("userIds")String ...ids);


    /**
     *根据姓名模糊查询用户
     * @param name
     * @return
     */
    List<AclUserAccreditInfo> selectUserByName(String name);

    /**
     * 根据用户id查询同用户的不同业务线
     * @param userId
     *  @param channelCodeString
     * @return
     */
    List<AclUserAccreditInfo> selectUserListByUserId(@Param("userId")String userId,@Param("channelCode")String channelCodeString);

    List<AclUserAccreditInfo> selectUserListByUserId2(@Param("userId")String userId);

}
