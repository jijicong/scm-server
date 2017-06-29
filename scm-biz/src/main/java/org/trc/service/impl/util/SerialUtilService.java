package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.domain.util.Serial;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.mapper.util.ISerialMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.CommonUtil;
import org.trc.util.SerialUtil;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/8.
 */
@Service("serialUtilService")
public class SerialUtilService extends BaseService<Serial, Long> implements ISerialUtilService {

    private Logger  log = LoggerFactory.getLogger(SerialUtilService.class);

    @Resource
    private ISerialMapper iserialMapper;

    //获得流水号
    public int selectNumber(String name) {
          return  iserialMapper.selectNumber(name);
    }

    public String generateRandomCode(int length,String flag,String ...names){ //需要其它的前缀，直接在后面添加
        int number = this.selectNumber(flag);//获得将要使用的流水号
        String code = SerialUtil.getMoveOrderNo(length,number,names);//获得需要的code编码
        int assess= this.updateSerialByName(flag,number);//修改流水的长度
        if (assess < 1) {
            String msg = CommonUtil.joinStr("保存编号数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.DATABASE_SAVE_SERIAL_EXCEPTION, msg);
        }
        return code;
    }

    public String generateCode(int length,String ...names){ //需要其它的前缀，直接在后面添加
        int number = this.selectNumber(names[0]);//获得将要使用的流水号
        String code = SerialUtil.getMoveOrderNo(length,number,names);//获得需要的code编码
        int assess= this.updateSerialByName(names[0],number);//修改流水的长度
        if (assess < 1) {
            String msg = CommonUtil.joinStr("保存编号数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.DATABASE_SAVE_SERIAL_EXCEPTION, msg);
        }
        return code;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateSerialByName(String name,int number) {
        int countVersionChange = iserialMapper.updateSerialVersionByName(name, number,number-1 );
        if (countVersionChange == 0) {
            String msg = CommonUtil.joinStr("流水的版本[vesionMark=", number + "", "]的数据已存在,请再次提交").toString();
            throw new ConfigException(ExceptionEnum.DATABASE_DATA_VERSION_EXCEPTION, msg);
    }
        return countVersionChange;

    }

}
