package org.trc.mapper.goods;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.util.BaseMapper;

public interface ISkuStockMapper extends BaseMapper<SkuStock>{

	void batchUpdateStockAirInventory(@Param ("channelCode") String channelCode, @Param ("warehouseCode") String warehouseCode,
			@Param ("detailList") List<WarehouseNoticeDetails> detailList);

	void batchUpdateStock(List<SkuStock> queryList);

}
