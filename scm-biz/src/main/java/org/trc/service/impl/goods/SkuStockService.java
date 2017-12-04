package org.trc.service.impl.goods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.goods.RequsetUpdateStock;
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

	@Override
	public void updateSkuStock(List<RequsetUpdateStock> updateStockList) throws Exception {
//		if (CollectionUtils.isEmpty(stockList)) {
//			return false;
//		}
		List<SkuStock> queryList = new ArrayList<>();
		for (RequsetUpdateStock reqStock : updateStockList) {
			SkuStock queryStock = new SkuStock();
			queryStock.setChannelCode(reqStock.getChannelCode());
			queryStock.setWarehouseCode(reqStock.getWarehouseCode());
			queryStock.setSkuCode(reqStock.getSkuCode());
			List<SkuStock> stockList = skuStockMapper.select(queryStock);
			SkuStock stock = stockList.get(0);
			SkuStock tmpStock = new SkuStock();
			
//			Method getMethod = SkuStock.class.getMethod("get" + toUpperFristChar(reqStock.getStockType()));
//			Long interval = (Long) getMethod.invoke(stock);
//			interval = interval == null ? 0L: interval;
			Method setMethod = SkuStock.class.getMethod("set" + toUpperFristChar(reqStock.getStockType()), Long.class);
			setMethod.invoke(tmpStock, reqStock.getNum());
//			tmpStock.setAirInventory(airInventory);
			
			tmpStock.setId(stock.getId());
			queryList.add(tmpStock);
		}
		skuStockMapper.batchUpdateStock(queryList);
		
//		return false;
	}
	
	private String toUpperFristChar(String string) {  
	    char[] charArray = string.toCharArray();  
	    charArray[0] -= 32;  
	    return String.valueOf(charArray);  
	} 

}
