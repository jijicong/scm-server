package org.trc.biz.impl.warehouseNotice;

import org.springframework.stereotype.Service;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.domain.purchase.WarehouseNotice;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.service.purchase.IWarehouseNoticeService;
import org.trc.util.Pagenation;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/7/12.
 */
@Service("warehouseNoticeBiz")
public class WarehouseNoticeBiz implements IWarehouseNoticeBiz {

    @Resource
    private IWarehouseNoticeService warehouseNoticeService;

    @Override
    public Pagenation<WarehouseNotice> warehouseNoticePage(WarehouseNoticeForm form, Pagenation<WarehouseNotice> page) {

        return null;
    }
}
