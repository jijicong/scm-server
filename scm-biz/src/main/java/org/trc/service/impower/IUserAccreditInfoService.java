package org.trc.service.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.service.IBaseService;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
public interface IUserAccreditInfoService extends IBaseService<UserAccreditInfo,Long>{
    /**
     *关联查询用户授权信息和用户角色
     * @param
     * @return
     * @throws Exception
     */
    List<UserAddPageDate>  selectUserAddPageList(Long[] userIds);

    /**
     * 查询具有角色采购角色的用户
     * @return
     * @throws Exception
     */
    List<UserAccreditInfo> findPurchase();

    /**
     * 查询需要查询的数量
     * @param map
     * @return
     * @throws Exception
     */
    int selectCountUser(Map map);

    /**
     * 查询授权用户信息
     * @param map
     * @return
     */
    List<UserAccreditInfo> selectAccreditInfoList(Map map);

    /**
     * 根据用户userids查询用户
     * @param strs
     * @return
     */
    List<UserAccreditInfo> selectUserNames(String strs[]);
   // List<UserAccreditInfo> selectAccreditInfoList(Map map) throws Exception;

    /**
     * 查询单个授权用户信息
     * @param userId
     * @return
     */
    UserAccreditInfo selectOneById(String userId);

    /**
     * 根据cookies中的token获取用户信息
     * @param requestContext
     * @return
     */
    UserAccreditInfo selectOneByRequestContext(ContainerRequestContext requestContext);

    Map<String,UserAccreditInfo> selectByIds(String ...ids);
}
