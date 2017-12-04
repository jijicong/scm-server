package org.trc.service.impl.goods;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
	public void updateSkuStock(List<RequsetUpdateStock> stockList) {
//		if (CollectionUtils.isEmpty(stockList)) {
//			return false;
//		}
		for (RequsetUpdateStock reqStock : stockList) {
			SkuStock tmpStock = new SkuStock();
			try {
//				skuStockMapper.selectByExample(example)(record)
//				Method method = SkuStock.class.getMethod("set" + toUpperFristChar(reqStock.getStockType()));
//				method.invoke(method, reqStock.getNum());
//				tmpStock.setSkuCode(reqStock.getSkuCode());
//				String stockType = reqStock.getSkuCode();
			} catch (Exception e) {
				
			}
		}
		
//		return false;
	}
	
	private String toUpperFristChar(String string) {  
	    char[] charArray = string.toCharArray();  
	    charArray[0] -= 32;  
	    return String.valueOf(charArray);  
	} 

}
