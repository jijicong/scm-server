package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional
    public Serial selectSerialByName(String name) {
        return iserialMapper.selectSerialByname(name);
    }

    @Override
    @Transactional
    public String getSerialCode(int length, String... names) throws Exception {

        if (names.length < 1) {
            throw new ConfigException(ExceptionEnum.DATABASE_CREATE_SERIAL_EXCEPTION, "创建流水号异常，前缀为空");
        }

        Serial serial = this.selectSerialByName(names[0]);//查询序列号
        int number = serial.getNumber();
        number += 1;
        this.updateSerialByName(names[0], number, serial.getNumber());
        StringBuilder sb=new StringBuilder();
        for (String name:names) {
            sb.append(name);
        }
        //获得最大的数
        int temp = SerialUtil.jointNineByLen(length);
        if (number > temp) {
            sb.append(temp);
            return sb.toString();
        }
        String code = SerialUtil.getMoveOrderNo(sb.toString(), length, number);
        return code;

    }

    /**
     * 1.如果调用的序号方法抛出异常，那么需要再次调用，确保拿到可以使用的流水号
     * <p>
     * 2.拿到可用的流水号之后，以防，外部调用的方法本身出异常（占用流水号）
     */
    @Override
    @Transactional
    public int updateSerialByName(String name, int number, int originalNumber) throws Exception {

        int countVersionChange = iserialMapper.updateSerialVersionByName(name, number, originalNumber);

        if (countVersionChange == 0) {
            String msg = CommonUtil.joinStr("流水的版本[vesionMark=", number + "", "]的数据已存在,请再次提交").toString();
            throw new ConfigException(ExceptionEnum.DATABASE_DATA_VERSION_EXCEPTION, msg);
        }
        return countVersionChange;

    }

}
