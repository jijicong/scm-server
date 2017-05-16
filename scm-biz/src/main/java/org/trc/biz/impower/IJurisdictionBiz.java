package org.trc.biz.impower;

import org.trc.domain.impower.Jurisdiction;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IJurisdictionBiz {
    /**
     *查询全局的资源权限
     * @return 资源权限集合
     * @throws Exception
     */
    List<Jurisdiction> findWholeJurisdiction() throws Exception;
    /**
     * 查询渠道的资源权限
     * @return 资源权限集合
     * @throws Exception
     */
    List<Jurisdiction> findChannelJurisdiction() throws Exception;

    /**
     * 根据角色的id，查询被选中的全局权限
     * @param roleId
     * @return
     * @throws Exception
     */
    List<Jurisdiction> findWholeJurisdictionAndCheckedByRoleId(Long roleId) throws Exception;

    /**
     *根据角色的id，查询被选中的渠道权限
     * @param roleId
     * @return
     * @throws Exception
     */
    List<Jurisdiction> findChannelJurisdictionAndCheckedByRoleId(Long roleId) throws Exception;
}