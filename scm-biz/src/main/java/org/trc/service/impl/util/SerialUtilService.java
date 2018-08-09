package org.trc.service.impl.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.domain.util.Serial;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.DistributeLockEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.RedisLockException;
import org.trc.mapper.util.ISerialMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.CommonUtil;
import org.trc.util.SerialUtil;
import org.trc.util.lock.RedisLock;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/8.
 */
@Service("serialUtilService")
public class SerialUtilService extends BaseService<Serial, Long> implements ISerialUtilService {

    private Logger  log = LoggerFactory.getLogger(SerialUtilService.class);

    @Resource
    private ISerialMapper iserialMapper;
    @Autowired
    private RedisLock redisLock;

    //获得流水号
    public int selectNumber(String name) {
          return  iserialMapper.selectNumber(name);
    }

    private String getRedisKey(String lockKey, int count){
        if(count == 20){
            return null;
        }
        log.info("获取锁"+lockKey+"操作当前重试第"+(count+1)+"次");
        String identifier = redisLock.Lock(lockKey, 5000, 6000);
        if (StringUtils.isBlank(identifier)){
            count++;
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getRedisKey(lockKey, count);
        }else{
            return identifier;
        }
    }

    private String generate(String name, int length, int count, String ... names){
        int number = this.selectNumber(name);//获得将要使用的流水号
        String code = SerialUtil.getMoveOrderNo(length,number,names);//获得需要的code编码
        try {
            this.updateSerialByName(name,number);//修改流水的长度
        }catch (Exception e){
            if(count < 15){
                count++;
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                log.info("更新序列号"+name+"操作当前重试第"+(count+1)+"次");
                return generate(name, length, count, names);
            }
            throw e;
        }
        return code;
    }

    //获得前缀不固定的流水号
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String generateRandomCode(int length,String flag,String ...names){ //需要其它的前缀，直接在后面添加
        String code = null;
        String lockKey = DistributeLockEnum.SERIAL_GENERATE.getCode();
        int count = 0;
        String identifier = getRedisKey(lockKey, count);
        if (StringUtils.isBlank(identifier)){
            throw new RedisLockException(CommonExceptionEnum.REDIS_LOCK_ERROR, String.format("序列号%s生成失败", names));
        }
        try{
            int count2 = 0;
            code = generate(flag, length, count2, names);
        }catch (Exception e){
            //释放锁
            if (redisLock.releaseLock(lockKey, identifier)) {
                log.info("锁" +lockKey + "已释放！");
            } else {
                log.error("锁" +lockKey + "解锁失败！");
            }
            log.error(String.format("序列号%s生成异常", names), e);
            throw e;
        }
        //释放锁
        if (redisLock.releaseLock(lockKey, identifier)) {
            log.info("锁" +lockKey + "已释放！");
        } else {
            log.error("锁" +lockKey + "解锁失败！");
        }
        return code;
    }


    //获得前缀固定的流水号
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String generateCode(int length,String ...names){ //需要其它的前缀，直接在后面添加
        String code = null;
        String lockKey = DistributeLockEnum.SERIAL_GENERATE.getCode();
        int count = 0;
        String identifier = getRedisKey(lockKey, count);
        if (StringUtils.isBlank(identifier)){
            throw new RedisLockException(CommonExceptionEnum.REDIS_LOCK_ERROR, String.format("序列号%s生成失败", names));
        }
        try{
            int count2 = 0;
            code = generate(names[0], length, count2, names);
        }catch (Exception e){
            //释放锁
            if (redisLock.releaseLock(lockKey, identifier)) {
                log.info("锁" +lockKey + "已释放！");
            } else {
                log.error("锁" +lockKey + "解锁失败！");
            }
            log.error(String.format("序列号%s生成异常", names), e);
            throw e;
        }
        //释放锁
        if (redisLock.releaseLock(lockKey, identifier)) {
            log.info("锁" +lockKey + "已释放！");
        } else {
            log.error("锁" +lockKey + "解锁失败！");
        }
        return code;
    }


    public int updateSerialByName(String name,int number) {
        int countVersionChange = iserialMapper.updateSerialVersionByName(name, number,number-1 );
        if (countVersionChange == 0) {
            String msg = CommonUtil.joinStr("流水的版本[vesionMark=", number + "", "]的数据已存在,请再次提交").toString();
            throw new ConfigException(ExceptionEnum.DATABASE_DATA_VERSION_EXCEPTION, msg);
        }
        return countVersionChange;

    }

}
