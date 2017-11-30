package org.trc.service.impl.goods;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.mapper.goods.ISkuStockMapper;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("skuStockService")
public class SkuStockService extends BaseService<SkuStock, Long> implements ISkuStockService {
	
	@Autowired
	ISkuStockMapper skuStockMapper;
	
	@Override
	public void batchUpdateStockAirInventory(String channelCode, String warehouseCode,
			List<WarehouseNoticeDetails> detailsList) {
		skuStockMapper.batchUpdateStockAirInventory(channelCode, warehouseCode, detailsList);
		
	}

}
