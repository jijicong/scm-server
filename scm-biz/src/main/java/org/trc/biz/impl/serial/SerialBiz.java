package org.trc.biz.impl.serial;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.serial.ISerialBiz;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.AssertUtil;

/**
 * Created by hzwdx on 2017/5/18.
 */
@Service("serialBiz")
public class SerialBiz implements ISerialBiz {

    private Logger log = LoggerFactory.getLogger(SerialBiz.class);

    /**
     * 供应商编码获取请求模块名称
     */
    public static final String SUPPLIER = "supplier";

    @Autowired
    private ISerialUtilService serialUtilService;

    @Override
    public String getSerialCode(String module) throws Exception {
        AssertUtil.notBlank(module, "生成编码的模块名称不能为空");
        String serialCode = "";
        if(StringUtils.equals(module, SUPPLIER)){//供应商
            serialCode = "00";//serialUtilService.getSerialCode(SupplyConstants.Serial.SUPPLIER_LENGTH, SupplyConstants.Serial.SUPPLIER_NAME);
        }else {
            String msg = String.format("%s%s%s", "生成序列号的模块",module,"不存在");
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SERIAL_MODULE_NOT_EXIST, msg);
        }
        return serialCode;
    }
}
