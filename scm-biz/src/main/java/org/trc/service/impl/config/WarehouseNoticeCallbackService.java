package org.trc.service.impl.config;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.domain.config.LogInfo;
import org.trc.domain.config.WarehouseNoticeCallback;
import org.trc.mapper.config.IWarehouseNoticeCallbackMapper;
import org.trc.service.config.IWarehouseNoticeCallbackService;
import org.trc.service.impl.BaseService;

@Service("warehouseNoticeCallbackService")
public class WarehouseNoticeCallbackService extends BaseService<WarehouseNoticeCallback,Long> implements IWarehouseNoticeCallbackService{
	
    private Logger log = LoggerFactory.getLogger(WarehouseNoticeCallbackService.class);
    
    @Autowired
    private IWarehouseNoticeCallbackMapper callbackMapper;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void recordCallbackLog(String reqParams, Integer status, String warehouseCode, String warehouseNoticeCode) {
        try {
        	Date nowDate = Calendar.getInstance().getTime();
        	WarehouseNoticeCallback record = new WarehouseNoticeCallback();
    		record.setCreateTime(nowDate);
    		record.setRequestTime(nowDate);
    		//record.setRequestCode("001");
    		record.setRequestParams(reqParams);
            if (null == status) {
            	status = 0;
            }
    		record.setState(status);
    		record.setWarehouseCode(warehouseCode);
    		record.setWarehouseNoticeCode(warehouseNoticeCode);
            callbackMapper.insert(record);
        } catch (Exception e) {
            log.error("WarehouseNoticeCallback日志记录异常,message:{},e:{}", e.getMessage(), e);
        }
    }
}
