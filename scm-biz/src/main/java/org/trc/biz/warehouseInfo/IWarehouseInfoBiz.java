package org.trc.biz.warehouseInfo;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.form.warehouseInfo.WarehouseInfoForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.form.warehouseInfo.WarehouseItemInfoForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by wangyz on 2017/11/15.
 */
public interface IWarehouseInfoBiz {

    /**
     * 添加仓库
     *
     * @param code 奇门仓库编码
     * @return
     */
    Response saveWarehouse(String code, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 查询不在本地的仓库
     *
     * @return
     */
    Response selectWarehouseNotInLocation();

    /**
     * 查询仓库名
     *
     * @return
     */
    Response selectWarehouse();

    /**
     * 仓库信息分页查询
     *
     * @param query 查询条件
     * @param page  分页
     * @return
     */
    Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page);

    /**
     * 保存货主信息
     *
     * @param warehouseInfo 货主信息
     * @return
     */
    Response saveOwnerInfo(WarehouseInfo warehouseInfo);

    /**
     * 删除仓库信息
     *
     * @param id 仓库ID
     * @return
     */
    Response deleteWarehouse(String id);

    /**
     * 仓库商品信息分页查询
     *
     * @param query
     * @param page
     * @return
     */
    Pagenation<WarehouseItemInfo> queryWarehouseItemInfoPage(WarehouseItemInfoForm form, Long warehouseInfoId, Pagenation<WarehouseItemInfo> page);

    /**
     * 删除仓库商品信息
     *
     * @param id
     */
    void deleteWarehouseItemInfoById(Long id);

    /**
     * 编辑仓库商品信息
     *
     * @param warehouseItemInfo
     */
    void updateWarehouseItemInfo(WarehouseItemInfo warehouseItemInfo);

    /**
     * 导入仓库商品信息通知状态
     * @param uploadedInputStream
     * @param fileDetail
     * @param warehouseInfoId
     * @return
     */
    Response uploadNoticeStatus(InputStream uploadedInputStream, FormDataContentDisposition fileDetail, String warehouseInfoId);
}
