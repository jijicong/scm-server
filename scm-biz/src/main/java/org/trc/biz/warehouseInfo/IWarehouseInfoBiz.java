package org.trc.biz.warehouseInfo;


import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.form.warehouseInfo.*;
import org.trc.service.warehouse.IWarehouseApiService;
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
    Response selectWarehouseNotInLocation(AclUserAccreditInfo aclUserAccreditInfo);

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
    Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page,AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 保存货主信息
     *
     * @param warehouseInfo 货主信息
     * @return
     */
    Response saveOwnerInfo(WarehouseInfo warehouseInfo, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 删除仓库信息
     *
     * @param id 仓库ID
     * @return
     */
    Response deleteWarehouse(String id);

    /**
     * 仓库商品信息分页查询
     * @param page
     * @return
     */
    Pagenation<WarehouseItemInfo> queryWarehouseItemInfoPage(WarehouseItemInfoForm form, String warehouseCode, Pagenation<WarehouseItemInfo> page);

    /**
     * 删除仓库商品信息
     *
     * @param id
     */
    void deleteWarehouseItemInfoById(Long id, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 编辑仓库商品信息
     *
     * @param warehouseItemInfo
     */
    void updateWarehouseItemInfo(WarehouseItemInfo warehouseItemInfo);

    /**
     * 商品信息导出
     *
     * @param form            查询条件
     * @param warehouseInfoId 仓库ID
     * @return
     */
    Response exportWarehouseItems(WarehouseItemInfoForm form, Long warehouseInfoId);

    /**
     * 新增商品
     *
     * @param items
     * @param warehouseInfoId
     * @return
     */
    Response saveWarehouseItemsSku(String items, Long warehouseInfoId, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 新增商品信息分页查询
     *
     * @param form
     * @param page
     * @param warehouseInfoId
     * @return
     */
    Pagenation<ItemsResult> queryWarehouseItemsSku(SkusForm form, Pagenation<Skus> page, Long warehouseInfoId);

    /**
     * 导入仓库商品信息通知状态
     *
     * @param uploadedInputStream
     * @param fileDetail
     * @param warehouseInfoId
     * @return
     */
    Response uploadNoticeStatus(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
                                String warehouseInfoId, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 本地仓库商品同步仓库
     * @param itemIds
     * @return
     */
    Response warehouseItemNoticeQimen(String itemIds, AclUserAccreditInfo aclUserAccreditInfo);

    void setWmsService(IWarehouseApiService service);

    /**
     * 导出错误信息
     * @param excelCode
     * @return
     */
    Response exportItemNoticeException(String excelCode);
    
    /**
     * @Description: 查询退货仓库map
     * @Author: hzluoxingcheng
     * @Date: 2018/8/30
     */ 
    Response selectReturnWarehouse();
}
