package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impl.system.ChannelBiz;
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
@Service("serialUtilService")
public class SerialUtilService extends BaseService<Serial, Long> implements ISerialUtilService {

    private final static Logger log = LoggerFactory.getLogger(SerialUtilService.class);
    @Resource
    private ISerialMapper iserialMapper;

    //获得流水号
    public int selectNumber(String name) {
          return  iserialMapper.selectNumber(name);
    }

    /**
     * 1.如果调用的序号方法抛出异常，那么需要再次调用，确保拿到可以使用的流水号
     * <p>
     * 2.拿到可用的流水号之后，以防，外部调用的方法本身出异常（占用流水号）
     */
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
