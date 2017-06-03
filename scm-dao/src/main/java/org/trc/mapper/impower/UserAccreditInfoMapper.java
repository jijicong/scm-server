package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.util.BaseMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 用户授权信息的mapper
 * Created by sone on 2017/5/11.
 */
public  interface UserAccreditInfoMapper  extends BaseMapper<UserAccreditInfo>{
    /**
     * 用户授权信息及角色names
     * @param
     * @return
     */
    List<UserAddPageDate> selectUserAddPageList(@Param("userIds") Long[] userIds);

    /**
     * 查询据有采购组角色的用户
     * @return
     */
    List<UserAccreditInfo> findPurchase();
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
    List<UserAccreditInfo> selectAccreditInfoList(Map map);

    /**
     * 查询分页对应的用户信息
     * @param strs
     * @return
     */
    List<UserAccreditInfo> selectUserNames(String strs[]);


    UserAccreditInfo selectOneById(@Param("userId")String userId);
}
