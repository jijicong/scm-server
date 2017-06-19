package org.trc.service.impower;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.service.IBaseService;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
public interface IAclUserAccreditInfoService extends IBaseService<AclUserAccreditInfo,Long>{
    /**
     *关联查询用户授权信息和用户角色
     * @param
     * @return
     * @throws Exception
     */
    List<AclUserAddPageDate>  selectUserAddPageList(Long[] userIds);

    /**
     * 查询具有角色采购角色的用户
     * @return
     * @throws Exception
     */
    List<AclUserAccreditInfo> findPurchase();

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
    List<AclUserAccreditInfo> selectAccreditInfoList(Map map);

    /**
     * 根据用户userids查询用户
     * @param strs
     * @return
     */
    List<AclUserAccreditInfo> selectUserNames(String strs[]);
   // List<AclUserAccreditInfo> selectAccreditInfoList(Map map) throws Exception;

    /**
     * 查询单个授权用户信息
     * @param userId
     * @return
     */
    AclUserAccreditInfo selectOneById(String userId);

    /**
     * 根据cookies中的token获取用户信息
     * @param requestContext
     * @return
     */
    AclUserAccreditInfo selectOneByRequestContext(ContainerRequestContext requestContext);

    Map<String,AclUserAccreditInfo> selectByIds(String ...ids);
}
