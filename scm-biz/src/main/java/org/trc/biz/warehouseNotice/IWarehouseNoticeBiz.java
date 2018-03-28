package org.trc.biz.warehouseNotice;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.service.IQimenService;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by sone on 2017/7/12.
 */
public interface IWarehouseNoticeBiz {
    /**入库通知单的分页查询
     * @param form form表单查询条件
     * @param page 分页查询的条件
     * @return 返回分页的内容
     */
    Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, Pagenation<WarehouseNotice> page,AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 执行通知收货
     * @param warehouseNotice
     */
    void receiptAdvice(WarehouseNotice warehouseNotice,AclUserAccreditInfo aclUserAccreditInfo);

    /** 根据入库通知单的id查询入库通知单
     * @param id
     * @return
     */
    WarehouseNotice findfindWarehouseNoticeById(Long id);

    /**
     * 根据入库通知的ID，查询入库通知明细
     * @param warehouseNoticeId
     * @return
     */
    List<WarehouseNoticeDetails> warehouseNoticeDetailList(Long warehouseNoticeId);

    /**
     * 入库通知单详情页的入库通知操作
     * @param warehouseNotice
     * @param aclUserAccreditInfo
     */
    void  receiptAdviceInfo(WarehouseNotice warehouseNotice,AclUserAccreditInfo aclUserAccreditInfo);

    void updateInStock(String requestText) ;

	void setQimenService(IQimenService service);

	void updateStock();


}
