package org.trc.service.impower;

import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.service.IBaseService;

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
    List<UserAddPageDate>  selectUserAddPageList(Long[] userIds) throws Exception;

    /**
     * 查询需要查询的数量
     * @param map
     * @return
     * @throws Exception
     */
    int selectCountUser(Map map) throws Exception;

    /**
     * 查询授权用户信息
     * @param map
     * @return
     */
    List<UserAccreditInfo> selectAccreditInfoList(Map map) throws Exception;
}
