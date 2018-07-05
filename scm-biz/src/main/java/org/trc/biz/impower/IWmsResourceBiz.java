package org.trc.biz.impower;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.WmsResource;
import org.trc.form.impower.JurisdictionTreeNode;

import java.util.List;

public interface IWmsResourceBiz {

    /**
     * 查询所有仓级资源
     * @return
     */
    List<WmsResource> queryWmsResource();

    /**
     *新增资源
     */
    void saveWmsResource(JurisdictionTreeNode jurisdictionTreeNode, AclUserAccreditInfo aclUserAccreditInfo);


    /**
     * 编辑WMS资源
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    void updateWmsResource(JurisdictionTreeNode jurisdictionTreeNode,AclUserAccreditInfo accreditInfo);

    /**
     * 查询资源树
     * @param parentId
     * @param isRecursive
     * @return
     */
    List<JurisdictionTreeNode> getWmsNodes(Long parentId, boolean isRecursive);
}
