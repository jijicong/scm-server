package org.trc.service.warehouseNotice;

import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/7/11.
 */
public interface IWarehouseNoticeDetailsService extends IBaseService<WarehouseNoticeDetails,Long>{
  void   updateWarehouseNoticeLists(List<WarehouseNoticeDetails> detailsList);

}
