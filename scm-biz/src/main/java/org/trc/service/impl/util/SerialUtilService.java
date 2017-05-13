package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.util.Serial;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.mapper.util.ISerialMapper;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.impl.BaseService;
import org.trc.util.CommonUtil;
import org.trc.util.SerialUtil;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/8.
 */
@Service
public class SerialUtilService extends BaseService<Serial,Long> implements ISerialUtilService {
    @Resource
    private ISerialMapper iserialMapper;

    @Override
    public Serial selectSerialByname(String name) {
        return iserialMapper.selectSerialByname(name);
    }

    @Override
    public String getSerilCode(String name, int length) {

        Serial serial = this.selectSerialByname(name);

        int countVersionChange=this.updateSeralByName(name,serial.getNumber()+1,serial.getNumber());

        String code= SerialUtil.getMoveOrderNo(name, length,serial.getNumber() );
        return code;
    }
    @Override
    public int updateSeralByName(String name, int number,int originalNumber) {
        int countVersionChange=iserialMapper.updateSeralVersionByName(name,number,originalNumber);
        if(countVersionChange==0) {
            String msg = CommonUtil.joinStr("流水的版本[vesionMark=", number + "", "]的数据已存在,请再次提交").toString();
            throw new ConfigException(ExceptionEnum.DATABASE_DATA_VERSION_EXCEPTION, msg);
        }
        return countVersionChange;
    }

}
