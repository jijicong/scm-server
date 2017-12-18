package org.trc.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.util.RedisDistrbuteLockUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * Created by liuyang on 2017/4/20.
 */
@Component
public class Service {

    @Autowired
    private RedisDistrbuteLockUtil redisDistrbuteLockUtil;

    int n = 500;

    public void seckill() {
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = redisDistrbuteLockUtil.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        System.out.println(--n);
        if(StringUtils.isNotBlank(indentifier)){
            redisDistrbuteLockUtil.releaseLock("resource", indentifier);
        }
    }
}
