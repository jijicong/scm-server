package org.trc.biz.impower;

import org.trc.domain.impower.AclResource;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.impower.JurisdictionTreeNode;

import javax.ws.rs.BeanParam;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
public interface IAclResourceBiz {
    /**
     * 查询全局的资源权限
     *
     * @return 资源权限集合
     * @throws Exception
     */
    List<AclResource> findWholeJurisdiction() ;

    /**
     *查询全局的资源权限:模块资源
     * @return
     */
    List<AclResource> findWholeJurisdictionModule();

    /**
     * 查询渠道的资源权限:三级资源
     *
     * @return 资源权限集合
     */
    List<AclResource> findChannelJurisdiction();

    /**
     *查询渠道的资源权限:模块资源
     * @return 模块资源集合
     */
    List<AclResource> findChannelJurisdictionModule();

    /**
     * 根据角色的id，查询被选中的全局权限
     *
     * @param roleId
     * @return
     * @throws Exception
     */
    List<AclResource> findWholeJurisdictionAndCheckedByRoleId(Long roleId);

    /**
     * 根据角色的id，查询被选中的渠道权限
     *
     * @param roleId
     * @return
     * @throws Exception
     */
    List<AclResource> findChannelJurisdictionAndCheckedByRoleId(Long roleId);

    /**
     * 对用户访问权限的检查
     *
     * @param userId
     * @param url
     * @param method
     * @return
     * @throws Exception
     */
    Boolean authCheck(String userId, String url, String method);

    /**
     * 验证该url是否需要拦截
     * @param url
     * @return
     * @throws Exception
     */
    Boolean urlCheck(String url);

    /**
     *查询角色资源树
     * @param parentId
     * @param isRecursive
     * @return
     * @throws Exception
     */
    List<JurisdictionTreeNode> getNodes(Long parentId, boolean isRecursive);

    /**
     * 新增资源
     * @param jurisdictionTreeNode
     * @throws Exception
     */
    void saveJurisdiction(@BeanParam JurisdictionTreeNode jurisdictionTreeNode,AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 编辑资源
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    void updateJurisdiction(JurisdictionTreeNode jurisdictionTreeNode);

    List<Map<String,Object>> getHtmlJurisdiction(String userId);

    List<AclResource> findWholeJurisdictionAndCheckedModuleByRoleId(Long roleId);

    List<AclResource> findChannelJurisdictionAndCheckedModuleByRoleId(Long roleId);
}
