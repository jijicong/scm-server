package org.trc.service.config;

import org.trc.domain.config.WarehouseNoticeCallback;
import org.trc.service.IBaseService;

public interface IWarehouseNoticeCallbackService extends IBaseService<WarehouseNoticeCallback,Long> {

	/**
	 * 记录仓库回调日志
	 * @param reqParams  请求参数json
	 * @param status 请求状态:1-初始状态,2-处理成功,3-处理失败
	 * @param warehouseCode  仓库编号
	 * @param warehouseNoticeCode 入库单编号
	 */
	void recordCallbackLog(String reqParams, Integer status, String warehouseCode, String warehouseNoticeCode);
	
}
