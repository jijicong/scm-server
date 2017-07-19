package org.trc.biz.warehouseNotice;

import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.util.Pagenation;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by sone on 2017/7/12.
 */
public interface IWarehouseNoticeBiz {
    /**入库通知单的分页查询
     * @param form form表单查询条件
     * @param page 分页查询的条件
     * @return 返回分页的内容
     */
    Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, Pagenation<WarehouseNotice> page,ContainerRequestContext requestContext);

    /**
     * 执行通知收货
     * @param warehouseNotice
     */
    void receiptAdvice(WarehouseNotice warehouseNotice);

    /** 根据入库通知单的id查询入库通知单
     * @param id
     * @return
     */
    WarehouseNotice findfindWarehouseNoticeById(Long id);

}
