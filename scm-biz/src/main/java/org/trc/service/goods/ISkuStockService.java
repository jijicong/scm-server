package org.trc.service.goods;

import java.util.List;

import org.trc.domain.goods.SkuStock;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.service.IBaseService;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface ISkuStockService extends IBaseService<SkuStock, Long>{

	void batchUpdateStockAirInventory(String channelCode, String warehouseCode,
			List<WarehouseNoticeDetails> detailsList);

}
