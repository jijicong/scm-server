package org.trc.biz.impower;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclWmsUserAccreditInfo;
import org.trc.domain.impower.WmsResource;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.impower.WmsUserAccreditInfoForm;
import org.trc.util.Pagenation;

import java.util.List;

public interface IAclWmsUserAccreditInfoBiz {

    /**
     * wms用户分页查询
     * @param form
     * @param page
     * @return
     */
    Pagenation<AclWmsUserAccreditInfo> wmsUserAccreditInfoPage(WmsUserAccreditInfoForm form, Pagenation<AclWmsUserAccreditInfo> page);

    /**
     * 新增仓级管理用户
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    void saveAclWmsUserAccreditInfo(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext);

    /**
     * 编辑仓级用户
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    void updateAclWmsUserAccreditInfo(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext);

    /**
     * 更新用户起停用状态
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    void updateAclWmsUserAccreditInfoState(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext);


    /**
     * 查询用户信息
     * @param id
     * @return
     */
    AclWmsUserAccreditInfo queryAclWmsUserAccreditInfo(Long id);

    /**
     * wms-手机号校验,并返回UserId
     * @param phone
     * @return
     */
    String checkWmsPhone(String phone);

    /**
     * 根据用户Id查询已经关联的资源
     * @param userId
     * @return
     */
    List<WmsResource> queryResource(Long userId);


    /**
     * 根据用户 查询仓库
     * @param Id
     * @return
     */
    List<WarehouseInfo> queryWarehouseInfo(Long Id);
}
